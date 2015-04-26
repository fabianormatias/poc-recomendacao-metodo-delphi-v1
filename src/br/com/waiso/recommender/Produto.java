package br.com.waiso.recommender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yooreeka.algos.reco.collab.model.Content;

public class Produto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6119040388138010186L;

	public static Integer[] getSharedEmpresaIds(Produto x, Produto y) {
		List<Integer> sharedEmpresas = new ArrayList<Integer>();
		for (RatingWaiso r : x.getAllRatings()) {
			// same empresa rated the produto
			if (y.getEmpresaRating(r.getEmpresaId()) != null) {
				sharedEmpresas.add(r.getEmpresaId());
			}
		}
		return sharedEmpresas.toArray(new Integer[sharedEmpresas.size()]);
	}

	/*
	 * Unique id in the dataset.
	 */
	private int id;

	/*
	 * Name.
	 */
	private String name;

	/*
	 * All ratings for this produto. Supports only one rating per produto for a empresa.
	 * Mapping: empresaId -> rating
	 */
	private Map<Integer, RatingWaiso> ratingsByEmpresaId;

	private Content produtoContent;

	public Produto(Integer id, List<RatingWaiso> ratings) {
		this(id, String.valueOf(id), ratings);
	}

	public Produto(Integer id, String name) {
		this(id, name, new ArrayList<RatingWaiso>(3));
	}

	public Produto(Integer id, String name, List<RatingWaiso> ratings) {
		this.id = id;
		this.name = name;
		// load ratings into empresaId -> rating map.
		ratingsByEmpresaId = new HashMap<Integer, RatingWaiso>(ratings.size());
		for (RatingWaiso r : ratings) {
			ratingsByEmpresaId.put(r.getEmpresaId(), r);
		}
	}

	/**
	 * Updates existing empresa rating or adds a new empresa rating for this produto.
	 * 
	 * @param r
	 *            rating to add.
	 */
	public void addEmpresaRatingWaiso(RatingWaiso r) {
		ratingsByEmpresaId.put(r.getEmpresaId(), r);
	}

	/**
	 * Returns all ratings that we have for this produto.
	 * 
	 * @return
	 */
	public Collection<RatingWaiso> getAllRatings() {
		return ratingsByEmpresaId.values();
	}

	public double getAverageRating() {
		double allRatingWaisosSum = 0.0;
		Collection<RatingWaiso> allProdutoRatingWaisos = ratingsByEmpresaId.values();
		for (RatingWaiso rating : allProdutoRatingWaisos) {
			allRatingWaisosSum += rating.getRating();
		}
		// use 2.5 if there are no ratings.
		return allProdutoRatingWaisos.size() > 0 ? allRatingWaisosSum
				/ allProdutoRatingWaisos.size() : 2.5;
	}

	public int getId() {
		return id;
	}

	public Content getProdutoContent() {
		return produtoContent;
	}

	public String getName() {
		return name;
	}

	/*
	 * Utility method to extract array of ratings based on array of empresa ids.
	 */
	public double[] getRatingWaisosForProdutoList(Integer[] empresasIds) {
		double[] ratings = new double[empresasIds.length];
		for (int i = 0, n = empresasIds.length; i < n; i++) {
			RatingWaiso r = getEmpresaRating(empresasIds[i]);
			if (r == null) {
				throw new IllegalArgumentException(
						"Produto doesn't have rating by specified empresa id ("
								+ "empresaId=" + empresasIds[i] + ", produtoId="
								+ getId());
			}
			ratings[i] = r.getRating();
		}
		return ratings;
	}

	/**
	 * Returns rating that specified empresa gave to the produto.
	 * 
	 * @param empresaId
	 *            empresa
	 * @return empresa rating or null if empresa hasn't rated this produto.
	 */
	public RatingWaiso getEmpresaRating(Integer empresaId) {
		return ratingsByEmpresaId.get(empresaId);
	}

	public void setProdutoContent(Content content) {
		this.produtoContent = content;
	}
	
}
