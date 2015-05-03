package br.com.waiso.recommender.data;

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

	public static Integer[] getSharedCompradoresIds(Produto x, Produto y) {
		List<Integer> sharedCompradores = new ArrayList<Integer>();
		for (Compra c : x.getAllComprasByComprador()) {
			// same empresa rated the produto
			if (y.getCompradorCompra(c.getCompradorId()) != null) {
				sharedCompradores.add(c.getCompradorId());
			}
		}
		return sharedCompradores.toArray(new Integer[sharedCompradores.size()]);
	}
	
	public static Integer[] getSharedVendedoresIds(Produto x, Produto y) {
		List<Integer> sharedVendedores = new ArrayList<Integer>();
		for (Compra c : x.getAllComprasByComprador()) {
			// same empresa rated the produto
			if (y.getCompradorCompra(c.getVendedorId()) != null) {
				sharedVendedores.add(c.getVendedorId());
			}
		}
		return sharedVendedores.toArray(new Integer[sharedVendedores.size()]);
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
	private Map<Integer, Compra> comprasByVendedorId;
	private Map<Integer, Compra> comprasByCompradorId;

	private Content produtoContent;

	public Produto(Integer id, List<Compra> compras) {
		this(id, String.valueOf(id), compras);
	}

	public Produto(Integer id, String name) {
		this(id, name, new ArrayList<Compra>(3));
	}

	public Produto(Integer id, String name, List<Compra> compras) {
		this.id = id;
		this.name = name;
		// load ratings into empresaId -> rating map.
		comprasByVendedorId = new HashMap<Integer, Compra>(compras.size());
		comprasByCompradorId = new HashMap<Integer, Compra>(compras.size());
		for (Compra c : compras) {
			comprasByVendedorId.put(c.getVendedorId(), c);
			comprasByCompradorId.put(c.getCompradorId(), c);
		}
	}

	/**
	 * Updates existing empresa rating or adds a new empresa rating for this produto.
	 * 
	 * @param r
	 *            rating to add.
	 */
	public void addVendedorCompra(Compra c) {
		comprasByVendedorId.put(c.getVendedorId(), c);
	}
	
	/**
	 * Updates existing empresa rating or adds a new empresa rating for this produto.
	 * 
	 * @param r
	 *            rating to add.
	 */
	public void addCompradorCompra(Compra c) {
		comprasByCompradorId.put(c.getCompradorId(), c);
	}

	/**
	 * Returns all compras that we have for this produto.
	 * 
	 * @return
	 */
	public Collection<Compra> getAllComprasByVendedor() {
		return comprasByVendedorId.values();
	}
	
	/**
	 * Returns all compras that we have for this produto.
	 * 
	 * @return
	 */
	public Collection<Compra> getAllComprasByComprador() {
		return comprasByCompradorId.values();
	}

	public double getAverageRatingByComprador() {
		return getAvarageRating(comprasByCompradorId);
	}
	
	public double getAverageRatingByVendedor() {
		return getAvarageRating(comprasByVendedorId);
	}
	
	private double getAvarageRating(Map<Integer, Compra> compras) {
		double allRatingsSum = 0.0;
		Collection<Compra> allCompras = compras.values();
		RatingWaiso rating = null; 
		for (Compra compra : allCompras) {
			rating = compra.getPontuacao();
			allRatingsSum += rating.getRating();
		}
		// use 2.5 if there are no ratings.
		return allCompras.size() > 0 ? allRatingsSum
				/ allCompras.size() : 2.5;
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
	public double[] getComprasForProdutoListByComprador(Integer[] compradoresIds) {
		return getComprasForProdutoList(compradoresIds, true);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of empresa ids.
	 */
	public double[] getComprasForProdutoListByVendedor(Integer[] vendedoresIds) {
		return getComprasForProdutoList(vendedoresIds, false);
	}

	/*
	 * Utility method to extract array of ratings based on array of empresa ids.
	 */
	private double[] getComprasForProdutoList(Integer[] empresasIds, boolean compradores) {
		double[] ratings = new double[empresasIds.length];
		for (int i = 0, n = empresasIds.length; i < n; i++) {
			RatingWaiso r = null;
			if(compradores) {
				Compra c = getCompradorCompra(empresasIds[i]);
				r = c.getPontuacao();
			} else {
				Compra c = getVendedorCompra(empresasIds[i]);
				r = c.getPontuacao();
			}
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
	public Compra getCompradorCompra(Integer compradorId) {
		return comprasByCompradorId.get(compradorId);
	}
	
	/**
	 * Returns rating that specified empresa gave to the produto.
	 * 
	 * @param empresaId
	 *            empresa
	 * @return empresa rating or null if empresa hasn't rated this produto.
	 */
	public Compra getVendedorCompra(Integer vendedorId) {
		return comprasByVendedorId.get(vendedorId);
	}

	public void setProdutoContent(Content content) {
		this.produtoContent = content;
	}
	
}
