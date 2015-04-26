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
package br.com.waiso.recommender;

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
import java.util.List;
import java.util.Map;
import java.util.Random;

import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;

/**
 * Dataset implementation that we will use to work with MovieLens data. All data
 * is loaded from three files: empresas, movies (produtos), and ratings.
 */
public class BusinessNetworkDataset implements DatasetWaiso {

	public static final String EMPRESAS_FILENAME = "empresas.dat";
	public static final String PRODUTOS_FILENAME = "produtos.dat";
	public static final String COMPRAS_FILENAME = "compras.dat";
	public static final String RATINGS_FILENAME = "ratings.dat";

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
	public static void createNewRatingsFile(File f, Collection<RatingWaiso> ratings) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
					f)));
			for (RatingWaiso rating : ratings) {
				pw.println(rating.getEmpresaId() + FIELD_DELIMITER
						+ rating.getProdutoId() + FIELD_DELIMITER
						+ rating.getRating());
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

	public static List<RatingWaiso> loadRatings(File f) {
		List<RatingWaiso> allRatings = new ArrayList<RatingWaiso>();

		BufferedReader reader = null;
		String line = null;
		try {
			reader = getReader(f);
			while ((line = reader.readLine()) != null) {
				String[] tokens = parseLine(line);
				int empresaId = Integer.parseInt(tokens[0]);
				int produtoId = Integer.parseInt(tokens[1]);
				int rating = Integer.parseInt(tokens[2]);
				allRatings.add(new RatingWaiso(empresaId, produtoId, rating));
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

		return allRatings;
	}

	private static String[] parseLine(String line) {
		// possible field delimiters: "::", "\t", "|"
		return line.split("::|\t|\\|");
	}
	/*
	 * All produto ratings.
	 */
	private List<RatingWaiso> allRatings = new ArrayList<RatingWaiso>();

	/*
	 * Map of all empresas.
	 */
	private Map<Integer, Empresa> allEmpresas = new HashMap<Integer, Empresa>();

	/*
	 * Map of all produtos.
	 */
	private Map<Integer, Produto> allProdutos = new HashMap<Integer, Produto>();

	/*
	 * Parameters for test dataset
	 */
	private int numberOfTestRatings = 0;

	private List<RatingWaiso> testRatings = new ArrayList<RatingWaiso>();

	/*
	 * Map of produto ratings by produto id.
	 */
	private Map<Integer, List<RatingWaiso>> ratingsByProdutoId = new HashMap<Integer, List<RatingWaiso>>();

	/*
	 * Map of produto ratings by empresa id.
	 */
	Map<Integer, List<RatingWaiso>> ratingsByEmpresaId = new HashMap<Integer, List<RatingWaiso>>();

	private String name;

	public BusinessNetworkDataset(File empresas, File movies, File ratings) {
		name = getClass().getSimpleName() + System.currentTimeMillis();
		loadData(empresas, movies, ratings, null);
	}

	public BusinessNetworkDataset(File empresas, File movies, File ratings,
			int numOfTestRatings) {
		name = getClass().getSimpleName() + System.currentTimeMillis();
		this.numberOfTestRatings = numOfTestRatings;
		loadData(empresas, movies, ratings, null);
	}

	public BusinessNetworkDataset(String name, File empresas, File movies, File ratings) {

		this.name = name;
		loadData(empresas, movies, ratings, null);
	}

	public BusinessNetworkDataset(String name, File empresas, File produtos,
			List<RatingWaiso> ratings) {

		this.name = name;
		loadData(empresas, produtos, null, ratings);
	}

	private void addRatingToMap(Map<Integer, List<RatingWaiso>> map, Integer key,
			RatingWaiso rating) {
		List<RatingWaiso> ratingsForKey = map.get(key);
		if (ratingsForKey == null) {
			ratingsForKey = new ArrayList<RatingWaiso>();
			map.put(key, ratingsForKey);
		}
		ratingsForKey.add(rating);
	}

	private Produto createNewProduto(int produtoId, String name) {
		List<RatingWaiso> ratings = ratingsByProdutoId.get(produtoId);
		if (ratings == null) {
			ratings = new ArrayList<RatingWaiso>();
		}

		Produto produto = new Produto(produtoId, name, ratings);

		// establish link between rating and produto
		for (RatingWaiso r : ratings) {
			r.setProduto(produto);
		}

		return produto;
	}

	public String[] getAllTerms() {
		return new String[0];
	}

	public double getAverageProdutoRating(int produtoId) {
		return getProduto(produtoId).getAverageRating();
	}

	public double getAverageEmpresaRating(int empresaId) {
		return getEmpresa(empresaId).getAverageRating();
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

	public Collection<RatingWaiso> getRatings() {
		return this.allRatings;
	}

	public int getRatingsCount() {
		return allRatings.size();
	}

	public Collection<RatingWaiso> getTestRatings() {
		return this.testRatings;
	}

	public Empresa getEmpresa(Integer empresaId) {
		return allEmpresas.get(empresaId);
	}

	public int getEmpresaCount() {
		return allEmpresas.size();
	}

	public Collection<Empresa> getEmpresas() {
		return allEmpresas.values();
	}

	public boolean isIdMappingRequired() {
		return false;
	}

	private void loadData(File empresasFile, File produtosFile, File ratingsFile,
			List<RatingWaiso> ratings) {
		try {
			/* Load all available ratings */
			if (ratings == null) {
				allRatings = loadRatings(ratingsFile);
			} else {
				allRatings = ratings;
			}

			/* Exclude ratings if needed */
			withholdRatings();

			/* build maps that provide access to ratings by empresaId or produtoId */
			for (RatingWaiso rating : allRatings) {
				addRatingToMap(ratingsByProdutoId, rating.getProdutoId(), rating);
				addRatingToMap(ratingsByEmpresaId, rating.getEmpresaId(), rating);
			}
			/*
			 * load empresas and produto. Each instance will have a set of ratings
			 * relevant to it
			 */
			allEmpresas = loadEmpresas(empresasFile);
			allProdutos = loadProdutos(produtosFile);
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

	private Map<Integer, Empresa> loadEmpresas(File empresasFile) throws IOException {
		Map<Integer, Empresa> empresas = new HashMap<Integer, Empresa>();

		BufferedReader reader = getReader(empresasFile);
		String line = null;

		while ((line = reader.readLine()) != null) {
			String[] tokens = parseLine(line);
			/* at the moment we are only interested in empresa id */
			int empresaId = Integer.parseInt(tokens[0]);
			List<RatingWaiso> empresaRatings = ratingsByEmpresaId.get(empresaId);
			if (empresaRatings == null) {
				empresaRatings = new ArrayList<RatingWaiso>();
			}
			Empresa empresa = new Empresa(empresaId, empresaRatings);
			empresas.put(empresa.getId(), empresa);
		}

		return empresas;
	}

	public void setTestRatingsCount(int numberOfRatings) {
		this.numberOfTestRatings = numberOfRatings;
	}

	private void withholdRatings() {
		Random rnd = new Random();
		while (testRatings.size() < this.numberOfTestRatings) {
			int randomIndex = rnd.nextInt(allRatings.size());
			RatingWaiso rating = allRatings.remove(randomIndex);
			testRatings.add(rating);
		}
	}

}
