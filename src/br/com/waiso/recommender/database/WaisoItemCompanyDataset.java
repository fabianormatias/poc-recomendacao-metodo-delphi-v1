/*
 *   ________________________________________________________________________________________
 *   
 *   Y O O R E E K A
 *   A library for data mining, machine learning, soft computing, and mathematical analysis
 *   ________________________________________________________________________________________ 
 *    
 *   The Yooreeka project started with the code of the book "Algorithms of the Intelligent Web " 
 *   (Manning 2009). Although the term "Web" prevailed in the title, in essence, the algorithms 
 *   are valuable in any software application.
 *  
 *   Copyright (c) 2007-2009 Haralambos Marmanis & Dmitry Babenko
 *   Copyright (c) 2009-${year} Marmanis Group LLC and individual contributors as indicated by the @author tags.  
 * 
 *   Certain library functions depend on other Open Source software libraries, which are covered 
 *   by different license agreements. See the NOTICE file distributed with this work for additional 
 *   information regarding copyright ownership and licensing.
 * 
 *   Marmanis Group LLC licenses this file to You under the Apache License, Version 2.0 (the "License"); 
 *   you may not use this file except in compliance with the License.  
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under 
 *   the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 *   either express or implied. See the License for the specific language governing permissions and
 *   limitations under the License.
 *   
 */
package br.com.waiso.recommender.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.waiso.recommender.data.Compra;
import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;
import br.com.waiso.recommender.data.RatingWaiso;

/**
 * Dataset implementation that we will use to work with MovieLens data. All data
 * is loaded from three files: empresas, movies (produtos), and ratings.
 */
public class WaisoItemCompanyDataset implements DatasetWaiso {

	public static final String USERS_FILENAME = "empresas.txt";
	public static final String ITEMS_FILENAME = "movies.txt";
	public static final String RATINGS_FILENAME = "ratings.txt";

	/*
	 * Delimiter that is used by MovieLens data files.
	 */
	private static final String FIELD_DELIMITER = "::";

	/**
	 * Saves provided ratings into a new file. Used to split ratings provided as
	 * part of MovieLens data set into files that represent various rating sets
	 * for training and testing.
	 * 
	 * @param f
	 *            file to write to.
	 * @param ratings
	 *            ratings to save.
	 */
	public static void createNewComprasFile(File f, Collection<Compra> compras) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
					f)));
			for (Compra compra : compras) {
				pw.println(compra.getCompradorId() + FIELD_DELIMITER
						+ compra.getVendedorId() + FIELD_DELIMITER
						+ compra.getProdutoId() + FIELD_DELIMITER
						+ compra.getPontuacao());
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			throw new RuntimeException(
					"Failed to save rating into file (file: '"
							+ f.getAbsolutePath() + "').", e);
		}
	}

	private static BufferedReader getReader(File f)
			throws FileNotFoundException {
		return new BufferedReader(new FileReader(f));
	}

	public static List<Compra> loadCompras(File f) {
		List<Compra> allCompras = new ArrayList<Compra>();

		BufferedReader reader = null;
		String line = null;
		try {
			reader = getReader(f);
			while ((line = reader.readLine()) != null) {
				String[] tokens = parseLine(line);
				int compradorId = Integer.parseInt(tokens[0]);
				int vendedorId = Integer.parseInt(tokens[1]);
				int produtoId = Integer.parseInt(tokens[2]);
				int rating = Integer.parseInt(tokens[3]);
				allCompras.add(new Compra(compradorId, vendedorId, produtoId, rating));
			}
		} catch (IOException e) {
			throw new RuntimeException(
					"Failed to load rating from file (file: '"
							+ f.getAbsolutePath() + "'): ", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					System.out.println("ERROR: \n");
					System.out.println(e.getMessage()
							+ "\n while closing file reader (file: '"
							+ f.getAbsolutePath() + "'): ");
				}
			}
		}

		return allCompras;
	}

	private static String[] parseLine(String line) {
		// possible field delimiters: "::", "\t", "|"
		return line.split(";");
	}
	/*
	 * All produto ratings.
	 */
	private List<Compra> allCompras = new ArrayList<Compra>();

	/*
	 * Map of all empresas.
	 */
	private Map<Integer, Empresa> allCompradores = new HashMap<Integer, Empresa>();
	
	/*
	 * Map of all empresas.
	 */
	private Map<Integer, Empresa> allVendedores = new HashMap<Integer, Empresa>();

	/*
	 * Map of all produtos.
	 */
	private Map<Integer, Produto> allProdutos = new HashMap<Integer, Produto>();
	
	/*
	 * Parameters for test dataset
	 */
	private int numberOfTestCompras = 0;

	private List<Compra> testCompras = new ArrayList<Compra>();

	/*
	 * Map of produto ratings by produto id.
	 */
	private Map<Integer, List<Compra>> comprasByProdutoId = new HashMap<Integer, List<Compra>>();

	/*
	 * Map of produto ratings by empresa id.
	 */
	Map<Integer, List<Compra>> comprasByCompradorId = new HashMap<Integer, List<Compra>>();
	Map<Integer, List<Compra>> comprasByVendedorId = new HashMap<Integer, List<Compra>>();

	private String name;

	public WaisoItemCompanyDataset(File empresas, File produtos, File compras) {
		name = getClass().getSimpleName() + System.currentTimeMillis();
		loadData(empresas, produtos, compras, null);
	}

	public WaisoItemCompanyDataset(File empresas, File movies, File ratings,
			int numOfTestCompras) {
		name = getClass().getSimpleName() + System.currentTimeMillis();
		this.numberOfTestCompras = numOfTestCompras;
		loadData(empresas, movies, ratings, null);
	}

	public WaisoItemCompanyDataset(String name, File empresas, File movies, File ratings) {

		this.name = name;
		loadData(empresas, movies, ratings, null);
	}

	public WaisoItemCompanyDataset(String name, File empresas, File produtos,
			List<Compra> compras) {

		this.name = name;
		loadData(empresas, produtos, null, compras);
	}

	private void addCompraToMap(Map<Integer, List<Compra>> map, Integer key,
			Compra compra) {
		List<Compra> comprasForKey = map.get(key);
		if (comprasForKey == null) {
			comprasForKey = new ArrayList<Compra>();
			map.put(key, comprasForKey);
		}
		comprasForKey.add(compra);
	}

	private Produto createNewProduto(int produtoId, String name) {
		List<Compra> compras = comprasByProdutoId.get(produtoId);
		if (compras == null) {
			compras = new ArrayList<Compra>();
		}

		Produto produto = new Produto(produtoId, name, compras);

		// establish link between rating and produto
		for (Compra r : compras) {
			r.setProduto(produto);
		}

		return produto;
	}

	public String[] getAllTerms() {
		return new String[0];
	}

	public double getAverageProdutoRatingByComprador(int produtoId) {
		return getProduto(produtoId).getAverageRatingByComprador();
	}
	
	public double getAverageProdutoRatingByVendedor(int produtoId) {
		return getProduto(produtoId).getAverageRatingByVendedor();
	}

	public double getAverageCompradorRating(int compradorId) {
		return getComprador(compradorId).getAverageRatingByCompra();
	}
	
	public double getAverageVendedorRating(int vendedorId) {
		return getComprador(vendedorId).getAverageRatingByVenda();
	}

	public Produto getProduto(Integer produtoId) {
		return allProdutos.get(produtoId);
	}

	public int getProdutoCount() {
		return allProdutos.size();
	}

	public Collection<Produto> getProdutos() {
		return allProdutos.values();
	}

	public String getName() {
		return name;
	}

	public Collection<Compra> getCompras() {
		return this.allCompras;
	}

	public int getComprasCount() {
		return allCompras.size();
	}

	public Collection<Compra> getTestCompras() {
		return this.testCompras;
	}

	public Empresa getComprador(Integer compradorId) {
		return allCompradores.get(compradorId);
	}
	
	public Empresa getVendedor(Integer vendedorId) {
		return allVendedores.get(vendedorId);
	}

	public int getVendedorCount() {
		return allVendedores.size();
	}
	
	public int getCompradorCount() {
		return allCompradores.size();
	}

	public Collection<Empresa> getVendedores() {
		return allVendedores.values();
	}
	
	public Collection<Empresa> getCompradores() {
		return allCompradores.values();
	}

	public boolean isIdMappingRequired() {
		return false;
	}

	private void loadData(File empresasFile, File produtosFile, File compraFile,
			List<Compra> compras) {
		try {
			/* Load all available ratings */
			if (compras == null) {
				allCompras = loadCompras(compraFile);
			} else {
				allCompras = compras;
			}

			/* Exclude ratings if needed */
//			withholdCompras();

			/* build maps that provide access to ratings by empresaId or produtoId */
			for (Compra compra : allCompras) {
				addCompraToMap(comprasByProdutoId, compra.getProdutoId(), compra);
				addCompraToMap(comprasByCompradorId, compra.getCompradorId(), compra);
				addCompraToMap(comprasByVendedorId, compra.getVendedorId(), compra);
			}
			/*
			 * load empresas and produto. Each instance will have a set of ratings
			 * relevant to it
			 */
			allProdutos = loadProdutos(produtosFile);
			allCompradores = loadEmpresas(empresasFile, comprasByCompradorId, true);
			allVendedores = loadEmpresas(empresasFile, comprasByVendedorId, false);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load MovieLens data: ", e);
		}
	}

	private Map<Integer, Produto> loadProdutos(File moviesFile) throws IOException {

		Map<Integer, Produto> produtos = new HashMap<Integer, Produto>();

		BufferedReader reader = getReader(moviesFile);
		String line = null;
		int lastId = 0;
		while ((line = reader.readLine()) != null) {

			String[] tokens = parseLine(line);

			/* at the moment we are only interested in movie id */
			int produtoId = Integer.parseInt(tokens[0]);
			String title = tokens[1];

			/*
			 * In some cases we need to create produtos for missing ids. Movies
			 * file from MovieLens dataset skips over some ids. To keep things
			 * simple we made assumption that empresa and movie (produto) ids are
			 * sequences without gaps that start with 1.
			 */
			if (produtoId > lastId + 1) {

				for (int i = lastId + 1; i < produtoId; i++) {
					// System.out.println("DEBUG:\n");
					// System.out.println("Movies file has a gap in ID sequence. ");
					// System.out.println("Creating artificial produto for ID: " +
					// i);

					Produto missingProduto = createNewProduto(i, "Missing-Produto-" + i);
					produtos.put(missingProduto.getId(), missingProduto);
				}
			}

			Produto produto = createNewProduto(produtoId, title);

			produtos.put(produto.getId(), produto);
			lastId = produto.getId();
		}
		return produtos;
	}

	private Map<Integer, Empresa> loadEmpresas(File empresasFile, Map<Integer, List<Compra>> comprasByEmpresaId, boolean comprador) throws IOException {
		Map<Integer, Empresa> empresas = new HashMap<Integer, Empresa>();

		BufferedReader reader = getReader(empresasFile);
		String line = null;

		while ((line = reader.readLine()) != null) {
			String[] tokens = parseLine(line);
			/* at the moment we are only interested in empresa id */
			int empresaId = Integer.parseInt(tokens[0]);
			List<Compra> empresaCompras = comprasByEmpresaId.get(empresaId);
			if (empresaCompras == null) {
				empresaCompras = new ArrayList<Compra>();
			}
			Empresa empresa = new Empresa(empresaId, empresaCompras, comprador);
			empresas.put(empresa.getId(), empresa);
		}

		return empresas;
	}

	public void setTestComprasCount(int numberOfCompras) {
		this.numberOfTestCompras = numberOfCompras;
	}

	private void withholdCompras() {
		Random rnd = new Random();
		while (testCompras.size() < this.numberOfTestCompras) {
			int randomIndex = rnd.nextInt(allCompras.size());
			Compra compra = allCompras.remove(randomIndex);
			testCompras.add(compra);
		}
	}
	
	public List<Compra> getComprasByProdutoId(Integer produtoId) {
		return comprasByProdutoId.get(produtoId);
	}
	
	@Override
	public void print() {
		Iterator<List<Compra>> i = comprasByProdutoId.values().iterator();
		List<Compra> compras = null;
		while (i.hasNext()) {
			compras = i.next();
			for (Compra compra : compras) {
				System.out.print("Pontuação - " + compra.getPontuacao());
				System.out.print(" -> Comprador - " + getComprador(compra.getCompradorId()).getName());
				System.out.print(" -> Vendedor - " + getVendedor(compra.getVendedorId()).getName());
				System.out.println(" -> Produto - " + getProduto(compra.getProdutoId()).getName());
			}
		}
	}

}
