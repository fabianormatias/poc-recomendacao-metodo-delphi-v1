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
		for (List<Compra> c : x.getAllComprasByComprador()) {
			// same empresa rated the produto
			if (y.getCompradorCompra(c.get(0).getCompradorId()) != null) {
				sharedCompradores.add(c.get(0).getCompradorId());
			}
		}
		return sharedCompradores.toArray(new Integer[sharedCompradores.size()]);
	}
	
	public static Integer[] getSharedVendedoresIds(Produto x, Produto y) {
		List<Integer> sharedVendedores = new ArrayList<Integer>();
		for (List<Compra> c : x.getAllComprasByComprador()) {
			// same empresa rated the produto
			if (y.getCompradorCompra(c.get(0).getVendedorId()) != null) {
				sharedVendedores.add(c.get(0).getVendedorId());
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
	private Map<Integer, List<Compra>> comprasByVendedorId;
	private Map<Integer, List<Compra>> comprasByCompradorId;

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
		comprasByVendedorId = new HashMap<Integer, List<Compra>>();
		comprasByCompradorId = new HashMap<Integer, List<Compra>>();
		for (Compra c : compras) {
			List<Compra> compraByCompradores = comprasByCompradorId.get(c.getCompradorId()); 
			List<Compra> compraByVendedores = comprasByVendedorId.get(c.getVendedorId()); 
			if(compraByCompradores == null) {
				compraByCompradores = new ArrayList<Compra>();
				comprasByCompradorId.put(c.getCompradorId(), compraByCompradores);
			}
			compraByCompradores.add(c);
			
			if(compraByVendedores == null) {
				compraByVendedores = new ArrayList<Compra>();
				comprasByVendedorId.put(c.getVendedorId(), compraByVendedores);
			}
			compraByVendedores.add(c);
		}
	}

	/**
	 * Updates existing empresa rating or adds a new empresa rating for this produto.
	 * 
	 * @param r
	 *            rating to add.
	 */
	public void addVendedorCompra(Compra c) {
		List<Compra> compraByVendedores = comprasByVendedorId.get(c.getVendedorId());
		if(compraByVendedores == null) {
			compraByVendedores = new ArrayList<Compra>();
			comprasByVendedorId.put(c.getVendedorId(), compraByVendedores);
		}
		compraByVendedores.add(c);
	}
	
	/**
	 * Updates existing empresa rating or adds a new empresa rating for this produto.
	 * 
	 * @param r
	 *            rating to add.
	 */
	public void addCompradorCompra(Compra c) {
		List<Compra> compraByCompradores = comprasByVendedorId.get(c.getCompradorId());
		if(compraByCompradores == null) {
			compraByCompradores = new ArrayList<Compra>();
			comprasByVendedorId.put(c.getVendedorId(), compraByCompradores);
		}
		compraByCompradores.add(c);
	}

	/**
	 * Returns all compras that we have for this produto.
	 * 
	 * @return
	 */
	public Collection<List<Compra>> getAllComprasByVendedor() {
		return comprasByVendedorId.values();
	}
	
	/**
	 * Returns all compras that we have for this produto.
	 * 
	 * @return
	 */
	public Collection<List<Compra>> getAllComprasByComprador() {
		return comprasByCompradorId.values();
	}

	public double getAverageRatingByComprador() {
		return getAvarageRating(comprasByCompradorId);
	}
	
	public double getAverageRatingByVendedor() {
		return getAvarageRating(comprasByVendedorId);
	}
	
	private double getAvarageRating(Map<Integer, List<Compra>> compras) {
		double allRatingsSum = 0.0;
		int numRatings = 0;
		Collection<List<Compra>> allCompras = compras.values();
		for (List<Compra> compraList : allCompras) {
			for (Compra c : compraList) {
				allRatingsSum += c.getPontuacao();
				numRatings++;
			}
		}
		// use 2.5 if there are no ratings.
		return numRatings > 0 ? allRatingsSum
				/ numRatings : 2.5;
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
	public List<double[]> getComprasForProdutoListByComprador(Integer[] compradoresIds) {
		return getComprasForProdutoList(compradoresIds, true);
	}
	
	/*
	 * Utility method to extract array of ratings based on array of empresa ids.
	 */
	public List<double[]> getComprasForProdutoListByVendedor(Integer[] vendedoresIds) {
		return getComprasForProdutoList(vendedoresIds, false);
	}

	/*
	 * Utility method to extract array of ratings based on array of empresa ids.
	 */
	private List<double[]> getComprasForProdutoList(Integer[] empresasIds, boolean compradores) {
		List<double[]> ratings = new ArrayList<double[]>();
		for (int i = 0, n = empresasIds.length; i < n; i++) {
			List<Compra> c;
			double[] r = new double[empresasIds.length];
			if(compradores) {
				c = getCompradorCompra(empresasIds[i]);
			} else {
				c = getVendedorCompra(empresasIds[i]);
			}
			r = getRatings(c);
			if (r == null) {
				throw new IllegalArgumentException(
						"Produto doesn't have rating by specified empresa id ("
								+ "empresaId=" + empresasIds[i] + ", produtoId="
								+ getId());
			}
			ratings.add(r);
		}
		return ratings;
	}

	private double[] getRatings(List<Compra> c) {
		double[] r = new double[c.size()];
		int j = 0;
		for (Compra compra : c) {
			r[j] = compra.getPontuacao();
			j++;
		}
		return r;
	}

	/**
	 * Returns rating that specified empresa gave to the produto.
	 * 
	 * @param empresaId
	 *            empresa
	 * @return empresa rating or null if empresa hasn't rated this produto.
	 */
	public List<Compra> getCompradorCompra(Integer compradorId) {
		return comprasByCompradorId.get(compradorId);
	}
	
	/**
	 * Returns rating that specified empresa gave to the produto.
	 * 
	 * @param empresaId
	 *            empresa
	 * @return empresa rating or null if empresa hasn't rated this produto.
	 */
	public List<Compra> getVendedorCompra(Integer vendedorId) {
		return comprasByVendedorId.get(vendedorId);
	}

	public void setProdutoContent(Content content) {
		this.produtoContent = content;
	}
	
}
