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
		for (List<Compra> c : x.getAllComprasCompra()) {
			if (y.getProdutoCompraByCompra(c.get(0).getProdutoId()) != null) {
				sharedProdutosComprados.add(c.get(0).getProdutoId());
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
		for (List<Compra> r : x.getAllComprasVenda()) {
			if (y.getProdutoCompraByVenda(r.get(0).getProdutoId()) != null) {
				sharedProdutosVendidos.add(r.get(0).getProdutoId());
			}
		}
		return sharedProdutosVendidos.toArray(new Integer[sharedProdutosVendidos.size()]);
	}
	
	int id;

	String name;

	protected Map<Integer, List<Compra>> comprasByProdutoVendidoId;
	protected Map<Integer, List<Compra>> comprasByProdutoCompradoId;
	
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
		if(comprasByProdutoCompradoId == null) {
			comprasByProdutoCompradoId = new HashMap<Integer, List<Compra>>(compras.size());
		}
		if(comprasByProdutoVendidoId == null) {
			comprasByProdutoVendidoId = new HashMap<Integer, List<Compra>>(compras.size());
		}
		if(comprador) {
			for (Compra c : compras) {
				comprasByProdutoCompradoId.put(c.getProdutoId(), compras);
			}
		} else {
			for (Compra c : compras) {
				comprasByProdutoVendidoId.put(c.getProdutoId(), compras);
			}
		}
	}

	public void addCompraComprado(Compra compra) {
		List<Compra> compras = comprasByProdutoCompradoId.get(compra.getProdutoId());
		if(compras == null) {
			compras = new ArrayList<Compra>();
			comprasByProdutoCompradoId.put(compra.getProdutoId(), compras);
		}
		compras.add(compra);
	}
	
	public void addCompraVendido(Compra compra) {
		List<Compra> compras = comprasByProdutoVendidoId.get(compra.getProdutoId());
		if(compras == null) {
			compras = new ArrayList<Compra>();
			comprasByProdutoVendidoId.put(compra.getProdutoId(), compras);
		}
		compras.add(compra);
	}

	public void addEmpresaContent(Content content) {
		empresaContent.add(content);
	}

	public Collection<List<Compra>> getAllComprasCompra() {
		return comprasByProdutoCompradoId.values();
	}
	
	public Collection<List<Compra>> getAllComprasVenda() {
		return comprasByProdutoVendidoId.values();
	}

	public double getAverageRatingByCompra() {
		return  getAverageRating(getAllComprasCompra());
	}
	
	public double getAverageRatingByVenda() {
		return  getAverageRating(getAllComprasVenda());
	}
	
	private double getAverageRating(Collection<List<Compra>> allCompras) {
		double allRatingsSum = 0.0;
		int numRatings = 0;
		for (List<Compra> list : allCompras) {
			Collection<Compra> allEmpresaRatings = list;
			for (Compra compra : allEmpresaRatings) {
				allRatingsSum += compra.getPontuacao();
			}
			numRatings += allEmpresaRatings.size(); 
		}
		
		return numRatings > 0 ? allRatingsSum
				/ numRatings : 2.5;
	}

	public int getId() {
		return id;
	}

	public List<Compra> getProdutoCompraByCompra(Integer produtoId) {
		return comprasByProdutoCompradoId.get(produtoId);
	}
	
	public List<Compra> getProdutoCompraByVenda(Integer produtoId) {
		return comprasByProdutoVendidoId.get(produtoId);
	}

	public String getName() {
		return name;
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public List<double[]> getRatingsForProdutoVendaList(Integer[] produtoIds) {
		return getRatingsForProdutoList(produtoIds, comprasByProdutoVendidoId);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public List<double[]> getRatingsForProdutoCompraList(Integer[] produtoIds) {
		return getRatingsForProdutoList(produtoIds, comprasByProdutoVendidoId);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of item ids.
	 */
	public List<double[]> getRatingsForProdutoList(Integer[] produtoIds, Map<Integer, List<Compra>> compras) {
		List<double[]> ratings = new ArrayList<double[]>();
		for (int i = 0, n = produtoIds.length; i < n; i++) {
			List<Compra> c = compras.get(produtoIds[i]);
			if (c == null) {
				throw new IllegalArgumentException(
						"Empresa doesn't have specified item id (" + "empresaId="
								+ getId() + ", itemId=" + produtoIds[i]);
			}
			double[] r = new double[c.size()];
			int j = 0;
			for (Compra compra : c) {
				r[j] = compra.getPontuacao();
				j++;
			}
			ratings.add(r);
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
			comprasByProdutoCompradoId = new HashMap<Integer, List<Compra>>();
		} else {
			comprasByProdutoCompradoId.clear();
		}

		// Load the ratings
		for (Compra compra : compras) {
			List<Compra> c = comprasByProdutoCompradoId.get(compra.getProdutoId());
			if(c == null) {
				c = new ArrayList<Compra>();
				comprasByProdutoCompradoId.put(compra.getProdutoId(), c);
			}
			c.add(compra);
		}
	}
	
	public void setComprasVenda(List<Compra> compras) {
		// Initialize or clean up
		if (comprasByProdutoVendidoId == null) {
			comprasByProdutoVendidoId = new HashMap<Integer, List<Compra>>();
		} else {
			comprasByProdutoVendidoId.clear();
		}

		// Load the ratings
		for (Compra compra : compras) {
			List<Compra> c = comprasByProdutoVendidoId.get(compra.getProdutoId());
			if(c == null) {
				c = new ArrayList<Compra>();
				comprasByProdutoVendidoId.put(compra.getProdutoId(), c);
			}
			c.add(compra);
		}
	}

	public void setEmpresaContent(List<Content> content) {
		this.empresaContent = content;
	}
	
}
