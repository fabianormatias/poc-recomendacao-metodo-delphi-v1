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
	public static Integer[] getSharedProdutosComprados(Empresa x, Empresa y) {
		List<Integer> sharedProdutosComprados = new ArrayList<Integer>();
		for (Compra c : x.getAllCompras()) {
			if (y.getProdutoCompra(c.getProdutoId()) != null) {
				sharedProdutosComprados.add(c.getProdutoId());
			}
		}
		return sharedProdutosComprados.toArray(new Integer[sharedProdutosComprados.size()]);
	}
	
	/**
	 * Utility method to extract item ids that are shared between empresa A and
	 * empresa B.
	 */
	public static Integer[] getSharedProdutosVendidos(Empresa x, Empresa y) {
		List<Integer> sharedProdutosVendidos = new ArrayList<Integer>();
		for (Compra r : x.getAllCompras()) {
			if (y.getProdutoCompra(r.getProdutoId()) != null) {
				sharedProdutosVendidos.add(r.getProdutoId());
			}
		}
		return sharedProdutosVendidos.toArray(new Integer[sharedProdutosVendidos.size()]);
	}
	
	int id;

	String name;

	protected Map<Integer, Compra> comprasByProdutoId;

	private List<Content> empresaContent = new ArrayList<Content>();

	public Empresa(int id) {
		this(id, String.valueOf(id), new ArrayList<Compra>(3));
	}

	public Empresa(int id, List<Compra> compras) {
		this(id, String.valueOf(id), compras);
	}

	public Empresa(int id, String name) {
		this(id, name, new ArrayList<Compra>(3));
	}

	public Empresa(int id, String name, List<Compra> compras) {
		this.id = id;
		this.name = name;
		comprasByProdutoId = new HashMap<Integer, Compra>(compras.size());
		for (Compra c : compras) {
			comprasByProdutoId.put(c.getProdutoId(), c);
		}
	}

	public void addRating(Compra compra) {
		comprasByProdutoId.put(compra.getProdutoId(), compra);
	}

	public void addEmpresaContent(Content content) {
		empresaContent.add(content);
	}

	public Collection<Compra> getAllCompras() {
		return comprasByProdutoId.values();
	}

	public double getAverageRating() {
		double allRatingsSum = 0.0;
		Collection<Compra> allEmpresaRatings = getAllCompras();
		for (Compra compra : allEmpresaRatings) {
			allRatingsSum += compra.getPontuacao();
		}
		return allEmpresaRatings.size() > 0 ? allRatingsSum
				/ allEmpresaRatings.size() : 2.5;
	}

	public int getId() {
		return id;
	}

	public Compra getProdutoCompra(Integer produtoId) {
		return comprasByProdutoId.get(produtoId);
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
			Compra c = getProdutoCompra(produtoIds[i]);
			if (c == null) {
				throw new IllegalArgumentException(
						"Empresa doesn't have specified item id (" + "empresaId="
								+ getId() + ", itemId=" + produtoIds[i]);
			}
			ratings[i] = c.getPontuacao();
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

	public void setRatings(List<Compra> compras) {
		// Initialize or clean up
		if (comprasByProdutoId == null) {
			comprasByProdutoId = new HashMap<Integer, Compra>(compras.size());
		} else {
			comprasByProdutoId.clear();
		}

		// Load the ratings
		for (Compra c : compras) {
			comprasByProdutoId.put(c.getProdutoId(), c);
		}
	}

	public void setEmpresaContent(List<Content> content) {
		this.empresaContent = content;
	}
	
}
