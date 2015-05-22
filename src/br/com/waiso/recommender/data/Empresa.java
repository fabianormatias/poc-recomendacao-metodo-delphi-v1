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
		for (Compra c : x.getAllComprasCompra()) {
			if (y.getProdutoCompraByCompra(c.getProdutoId()) != null) {
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
		for (Compra r : x.getAllComprasVenda()) {
			if (y.getProdutoCompraByVenda(r.getProdutoId()) != null) {
				sharedProdutosVendidos.add(r.getProdutoId());
			}
		}
		return sharedProdutosVendidos.toArray(new Integer[sharedProdutosVendidos.size()]);
	}
	
	int id;

	String name;

	protected Map<Integer, Compra> comprasByProdutoVendidoId;
	protected Map<Integer, Compra> comprasByProdutoCompradoId;
	
	private List<Content> empresaContent = new ArrayList<Content>();

	public Empresa(int id, boolean comprador) {
		this(id, String.valueOf(id), new ArrayList<Compra>(3), comprador);
	}

	public Empresa(int id, List<Compra> compras, boolean comprador) {
		this(id, String.valueOf(id), compras, comprador);
	}

	public Empresa(int id, String name, boolean comprador) {
		this(id, name, new ArrayList<Compra>(3), comprador);
	}

	public Empresa(int id, String name, List<Compra> compras, boolean comprador) {
		this.id = id;
		this.name = name;
		if(comprador) {
			comprasByProdutoCompradoId = new HashMap<Integer, Compra>(compras.size());
			for (Compra c : compras) {
				comprasByProdutoCompradoId.put(c.getProdutoId(), c);
			}
		} else {
			comprasByProdutoVendidoId = new HashMap<Integer, Compra>(compras.size());
			for (Compra c : compras) {
				comprasByProdutoVendidoId.put(c.getProdutoId(), c);
			}
		}
	}

	public void addCompraComprado(Compra compra) {
		comprasByProdutoCompradoId.put(compra.getProdutoId(), compra);
	}

	public void addEmpresaContent(Content content) {
		empresaContent.add(content);
	}

	public Collection<Compra> getAllComprasCompra() {
		return comprasByProdutoCompradoId.values();
	}
	
	public Collection<Compra> getAllComprasVenda() {
		return comprasByProdutoVendidoId.values();
	}

	public double getAverageRatingByCompra() {
		return  getAverageRating(getAllComprasCompra());
	}
	
	public double getAverageRatingByVenda() {
		return  getAverageRating(getAllComprasVenda());
	}
	
	private double getAverageRating(Collection<Compra> allCompras) {
		double allRatingsSum = 0.0;
		Collection<Compra> allEmpresaRatings = allCompras;
		for (Compra compra : allEmpresaRatings) {
			allRatingsSum += compra.getPontuacao();
		}
		return allEmpresaRatings.size() > 0 ? allRatingsSum
				/ allEmpresaRatings.size() : 2.5;
	}

	public int getId() {
		return id;
	}

	public Compra getProdutoCompraByCompra(Integer produtoId) {
		return comprasByProdutoCompradoId.get(produtoId);
	}
	
	public Compra getProdutoCompraByVenda(Integer produtoId) {
		return comprasByProdutoVendidoId.get(produtoId);
	}

	public String getName() {
		return name;
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public double[] getRatingsForProdutoVendaList(Integer[] produtoIds) {
		return getRatingsForProdutoList(produtoIds, comprasByProdutoVendidoId);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public double[] getRatingsForProdutoCompraList(Integer[] produtoIds) {
		return getRatingsForProdutoList(produtoIds, comprasByProdutoVendidoId);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public double[] getRatingsForProdutoList(Integer[] produtoIds, Map<Integer, Compra> compras) {
		double[] ratings = new double[produtoIds.length];
		for (int i = 0, n = produtoIds.length; i < n; i++) {
			Compra c = compras.get(produtoIds[i]);
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

	public void setComprasCompra(List<Compra> compras) {
		// Initialize or clean up
		if (comprasByProdutoCompradoId == null) {
			comprasByProdutoCompradoId = new HashMap<Integer, Compra>(compras.size());
		} else {
			comprasByProdutoCompradoId.clear();
		}

		// Load the ratings
		for (Compra c : compras) {
			comprasByProdutoCompradoId.put(c.getProdutoId(), c);
		}
	}
	
	public void setComprasVenda(List<Compra> compras) {
		// Initialize or clean up
		if (comprasByProdutoVendidoId == null) {
			comprasByProdutoVendidoId = new HashMap<Integer, Compra>(compras.size());
		} else {
			comprasByProdutoVendidoId.clear();
		}

		// Load the ratings
		for (Compra c : compras) {
			comprasByProdutoVendidoId.put(c.getProdutoId(), c);
		}
	}

	public void setEmpresaContent(List<Content> content) {
		this.empresaContent = content;
	}
	
}
