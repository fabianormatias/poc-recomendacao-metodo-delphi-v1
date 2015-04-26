package br.com.waiso.recommender.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yooreeka.algos.reco.collab.model.Content;


public class Empresa implements Serializable {

	/**
	 * Unique identifier for serialization
	 */
	private static final long serialVersionUID = -1884424246968533858L;

	/**
	 * Utility method to extract item ids that are shared between empresa A and
	 * empresa B.
	 */
	public static Integer[] getSharedProdutos(Empresa x, Empresa y) {
		List<Integer> sharedProdutos = new ArrayList<Integer>();
		for (RatingWaiso r : x.getAllRatings()) {
			if (y.getProdutoRating(r.getProdutoId()) != null) {
				sharedProdutos.add(r.getProdutoId());
			}
		}
		return sharedProdutos.toArray(new Integer[sharedProdutos.size()]);
	}
	int id;

	String name;

	protected Map<Integer, RatingWaiso> ratingsByProdutoId;

	private List<Content> empresaContent = new ArrayList<Content>();

	public Empresa(int id) {
		this(id, String.valueOf(id), new ArrayList<RatingWaiso>(3));
	}

	public Empresa(int id, List<RatingWaiso> ratings) {
		this(id, String.valueOf(id), ratings);
	}

	public Empresa(int id, String name) {
		this(id, name, new ArrayList<RatingWaiso>(3));
	}

	public Empresa(int id, String name, List<RatingWaiso> ratings) {
		this.id = id;
		this.name = name;
		ratingsByProdutoId = new HashMap<Integer, RatingWaiso>(ratings.size());
		for (RatingWaiso r : ratings) {
			ratingsByProdutoId.put(r.getProdutoId(), r);
		}
	}

	public void addRating(RatingWaiso rating) {
		ratingsByProdutoId.put(rating.getProdutoId(), rating);
	}

	public void addEmpresaContent(Content content) {
		empresaContent.add(content);
	}

	public Collection<RatingWaiso> getAllRatings() {
		return ratingsByProdutoId.values();
	}

	public double getAverageRating() {
		double allRatingsSum = 0.0;
		Collection<RatingWaiso> allEmpresaRatings = getAllRatings();
		for (RatingWaiso rating : allEmpresaRatings) {
			allRatingsSum += rating.getRating();
		}
		return allEmpresaRatings.size() > 0 ? allRatingsSum
				/ allEmpresaRatings.size() : 2.5;
	}

	public int getId() {
		return id;
	}

	public RatingWaiso getProdutoRating(Integer produtoId) {
		return ratingsByProdutoId.get(produtoId);
	}

	public String getName() {
		return name;
	}

	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public double[] getRatingsForProdutoList(Integer[] produtoIds) {
		double[] ratings = new double[produtoIds.length];
		for (int i = 0, n = produtoIds.length; i < n; i++) {
			RatingWaiso r = getProdutoRating(produtoIds[i]);
			if (r == null) {
				throw new IllegalArgumentException(
						"Empresa doesn't have specified item id (" + "empresaId="
								+ getId() + ", itemId=" + produtoIds[i]);
			}
			ratings[i] = r.getRating();
		}
		return ratings;
	}

	public List<Content> getEmpresaContent() {
		return empresaContent;
	}

	public Content getEmpresaContent(String contentId) {
		Content matchedContent = null;
		for (Content c : empresaContent) {
			if (c.getId().equals(contentId)) {
				matchedContent = c;
				break;
			}
		}
		return matchedContent;
	}

	public void setRatings(List<RatingWaiso> ratings) {
		// Initialize or clean up
		if (ratingsByProdutoId == null) {
			ratingsByProdutoId = new HashMap<Integer, RatingWaiso>(ratings.size());
		} else {
			ratingsByProdutoId.clear();
		}

		// Load the ratings
		for (RatingWaiso r : ratings) {
			ratingsByProdutoId.put(r.getProdutoId(), r);
		}
	}

	public void setEmpresaContent(List<Content> content) {
		this.empresaContent = content;
	}
	
}
