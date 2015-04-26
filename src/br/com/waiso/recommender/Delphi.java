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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.yooreeka.algos.reco.collab.model.RecommendationType;
import org.yooreeka.config.YooreekaConfigurator;

/**
 * Recommender. Has to be initialized with similarity function and data.
 *
 * @author <a href="mailto:babis@marmanis.com">Babis Marmanis</a>
 * 
 */
public class Delphi implements Recommender {

	private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.50;
	private static final double MAX_RATING = 5;
	private static final Logger LOG = Logger.getLogger(Delphi.class.getName());

	private RecommendationType type;
	private SimilarityMatrix similarityMatrix;
	private DatasetWaiso dataSet;
	private boolean verbose = true;
	private double similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;
	private Map<Integer, Double> maxPredictedRating;

	public Delphi(DatasetWaiso dataSet, 
			      RecommendationType type) {

		this(dataSet,type,false);		
	}

	/**
	 * This constructor should be used when we want to use the cache but
	 * we also need to calculate the similarity matrix.
	 * 
	 * @param dataSet
	 * @param type
	 * @param useSimilarityCache
	 */
	public Delphi(DatasetWaiso dataSet, 
			      RecommendationType type,	
			      boolean useSimilarityCache) {
		
		this(dataSet,type,useSimilarityCache,null);
		
		SimilarityMatrixRepository smRepo = new SimilarityMatrixRepository(useSimilarityCache);
		setSimilarityMatrix(smRepo.load(type, dataSet));
	}

	public Delphi(DatasetWaiso dataSet, 
				  RecommendationType type,
				  boolean useSimilarityCache,
				  SimilarityMatrix similarityMatrix) {

		LOG.setLevel(YooreekaConfigurator.getLevel(Delphi.class.getName()));
		
		this.type = type;

		this.dataSet = dataSet;
		maxPredictedRating = new HashMap<Integer, Double>(dataSet.getEmpresaCount() / 2);
		
		this.similarityMatrix = similarityMatrix;
	}

	
	// --------------------------------------------------------------------
	// USER BASED SIMILARITY
	// --------------------------------------------------------------------

	private double estimateProdutoBasedRating(Empresa user, Produto produto) {

		double estimatedRating;

		if (produto != null && user != null) {

			estimatedRating = produto.getAverageRating();

		} else {
			if (produto == null && user == null) {
				throw new IllegalArgumentException(
						"At least, one of the arguments must not be null!");
			} else {
				return 3.0d;
			}
		}

		int itemId = produto.getId();
		int userId = user.getId();
		double similaritySum = 0.0;
		double weightedRatingSum = 0.0;

		// check if the user has already rated the produto
		RatingWaiso existingRatingByEmpresa = user.getProdutoRating(produto.getId());

		if (existingRatingByEmpresa != null) {

			estimatedRating = existingRatingByEmpresa.getRating();

		} else {

			double similarityBetweenProdutos = 0;
			double weightedRating = 0;

			for (Produto anotherProduto : dataSet.getProdutos()) {

				// only consider items that were rated by the user
				RatingWaiso anotherProdutoRating = anotherProduto.getEmpresaRating(userId);

				if (anotherProdutoRating != null) {

					similarityBetweenProdutos = similarityMatrix.getValue(itemId,
							anotherProduto.getId());

					if (similarityBetweenProdutos > similarityThreshold) {

						weightedRating = similarityBetweenProdutos
								* anotherProdutoRating.getRating();

						weightedRatingSum += weightedRating;
						similaritySum += similarityBetweenProdutos;
					}
				}
			}

			if (similaritySum > 0.0) {

				estimatedRating = weightedRatingSum / similaritySum;
			}
		}

		return estimatedRating;
	}

	// -----------------------------------------------------------
	// PRIVATE (AUXILIARY) METHODS
	// -----------------------------------------------------------
	private double estimateEmpresaBasedRating(Empresa user, Produto produto) {

		double estimatedRating = user.getAverageRating();

		int itemId = produto.getId();
		int userId = user.getId();

		double similaritySum = 0.0;
		double weightedRatingSum = 0.0;

		// check if user has already rated this produto
		RatingWaiso existingRatingByEmpresa = user.getProdutoRating(produto.getId());

		if (existingRatingByEmpresa != null) {

			estimatedRating = existingRatingByEmpresa.getRating();

		} else {
			for (Empresa anotherEmpresa : dataSet.getEmpresas()) {

				RatingWaiso itemRating = anotherEmpresa.getProdutoRating(itemId);

				// only consider users that rated this produto
				if (itemRating != null) {

					/**
					 * @todo describe how this generalizes to more accurate
					 *       similarities
					 */
					double similarityBetweenEmpresas = similarityMatrix.getValue(
							userId, anotherEmpresa.getId());

					double ratingByNeighbor = itemRating.getRating();

					double weightedRating = similarityBetweenEmpresas
							* ratingByNeighbor;

					weightedRatingSum += weightedRating;
					similaritySum += similarityBetweenEmpresas;
				}
			}

			if (similaritySum > 0.0) {
				estimatedRating = weightedRatingSum / similaritySum;
			}
		}

		return estimatedRating;
	}

	private List<SimilarEmpresa> findFriendsBasedOnEmpresaSimilarity(Empresa user) {

		List<SimilarEmpresa> similarEmpresas = new ArrayList<SimilarEmpresa>();

		for (Empresa friend : dataSet.getEmpresas()) {

			if (user.getId() != friend.getId()) {

				double similarity = similarityMatrix.getValue(user.getId(),
						friend.getId());
				similarEmpresas.add(new SimilarEmpresa(friend, similarity));
			}
		}

		return similarEmpresas;
	}

	// --------------------------------------------------------------------
	// ITEM BASED SIMILARITY
	// --------------------------------------------------------------------

	private List<SimilarProduto> findProdutosBasedOnProdutoSimilarity(Produto produto) {

		List<SimilarProduto> similarProdutos = new ArrayList<SimilarProduto>();

		int itemId = produto.getId();

		for (Produto sProduto : dataSet.getProdutos()) {

			if (itemId != sProduto.getId()) {

				double similarity = similarityMatrix.getValue(itemId,
						sProduto.getId());
				if (similarity > 0.0) {
					similarProdutos.add(new SimilarProduto(sProduto, similarity));
				}
			}
		}

		return similarProdutos;
	}

	public SimilarProduto[] findSimilarProdutos(Produto produto) {
		return findSimilarProdutos(produto, 5);
	}

	public SimilarProduto[] findSimilarProdutos(Produto produto, int topN) {

		List<SimilarProduto> similarProdutos = new ArrayList<SimilarProduto>();

		if (!isEmpresaBased()) {

			similarProdutos = findProdutosBasedOnProdutoSimilarity(produto);

		} else {

			LOG.warning("Finding similar items based on Empresa similarity is not supported!");
		}

		SimilarProduto[] topSimilarProdutos = SimilarProduto.getTopSimilarProdutos(
				similarProdutos, topN);

		if (verbose) {
			SimilarProduto.printProdutos(topSimilarProdutos,
					"Produtos like produto " + produto.getName() + ":");
		}

		return topSimilarProdutos;
	}

	public SimilarEmpresa[] findSimilarEmpresas(Empresa user) {
		SimilarEmpresa[] topFriends = findSimilarEmpresas(user, 5);

		if (verbose) {
			SimilarEmpresa.print(topFriends,
					"Top Friends for user " + user.getName() + ":");
		}

		return topFriends;
	}

	public SimilarEmpresa[] findSimilarEmpresas(Empresa user, int topN) {

		List<SimilarEmpresa> similarEmpresas = new ArrayList<SimilarEmpresa>();

		if (isEmpresaBased()) {

			similarEmpresas = findFriendsBasedOnEmpresaSimilarity(user);

		} else {

			/**
			 * TODO: 3.x: Create an algorithm that would allow you to find
			 * similar users based on produto similarities. What kind of results do
			 * you get? Is it space efficient? How about execution time?
			 */
			LOG.warning("Finding friends based on Produto similarity is not supported!");
		}

		return SimilarEmpresa.getTopNFriends(similarEmpresas, topN);
	}

	/**
	 * @return recommender's dataset.
	 */
	public DatasetWaiso getDataset() {
		return this.dataSet;
	}

	/**
	 * @return the maxPredictedRating of a particular user
	 */
	public double getMaxPredictedRating(Integer uID) {
		Double maxPR = maxPredictedRating.get(uID);

		return (maxPR == null) ? 5.0d : maxPR;
	}

	// --------------------------------------------------------------------
	// RATING PREDICTIONS
	// --------------------------------------------------------------------

	public double getSimilarity(Produto i1, Produto i2) {

		double sim = similarityMatrix.getValue(i1.getId(), i2.getId());

		if (verbose) {
			System.out.print("Produto similarity between");
			System.out.print(" ProdutoID: " + i1.getId());
			System.out.print(" and");
			System.out.print(" ProdutoID: " + i2.getId());
			System.out.println(" is equal to " + sim);
		}

		return sim;
	}

	public double getSimilarity(Empresa u1, Empresa u2) {

		double sim = similarityMatrix.getValue(u1.getId(), u2.getId());

		if (verbose) {
			System.out.print("Empresa Similarity between");
			System.out.print(" EmpresaID: " + u1.getId());
			System.out.print(" and");
			System.out.print(" EmpresaID: " + u2.getId());
			System.out.println(" is equal to " + sim);
		}

		return sim;
	}

	// --------------------------------------------------------------------
	// AUXILIARY METHODS
	// --------------------------------------------------------------------

	public SimilarityMatrix getSimilarityMatrix() {
		return similarityMatrix;
	}

	public double getSimilarityThreshold() {
		return similarityThreshold;
	}

	public RecommendationType getType() {
		return type;
	}

	public double getEmpresaProdutoSimilarity(Empresa user, Produto produto) {

		if (!isEmpresaProdutoBased()) {
			throw new IllegalStateException(
					"Not valid for current similarity type:" + type);
		}

		double sim = similarityMatrix.getValue(user.getId(), produto.getId());

		if (verbose) {
			System.out.print("Empresa Produto Similarity between");
			System.out.print(" EmpresaID: " + user.getId());
			System.out.print(" and");
			System.out.print(" ProdutoID: " + produto.getId());
			System.out.println(" is equal to " + sim);
		}

		return sim;
	}

	private boolean isContentBased() {
		return type.toString().indexOf("CONTENT") >= 0;
	}

	private boolean isEmpresaBased() {
		return type.toString().indexOf("USER") >= 0
				&& type.toString().indexOf("USER_ITEM") < 0;
	}

	private boolean isEmpresaProdutoBased() {
		return type.toString().indexOf("USER_ITEM") >= 0;
	}

	public boolean isVerbose() {
		return verbose;
	}

	@Override
	public double predictBasedOnProdutoAverage(Produto produto) {
		return produto.getAverageRating();
	}

	@Override
	public double predictBasedOnEmpresaAverage(Empresa user) {
		return user.getAverageRating();
	}

	public double predictRating(int userId, int itemId) {
		return predictRating(dataSet.getEmpresa(userId), dataSet.getProduto(itemId));
	}

	public double predictRating(Empresa user, Produto produto) {
		switch (type) {
		case USER_BASED:
			return estimateEmpresaBasedRating(user, produto);
		case IMPROVED_USER_BASED:
			return estimateEmpresaBasedRating(user, produto);
		case ITEM_BASED:
			return estimateProdutoBasedRating(user, produto);
		case ITEM_PENALTY_BASED:
			return estimateProdutoBasedRating(user, produto);
		case USER_CONTENT_BASED:
			throw new IllegalStateException(
					"Not valid for current similarity type:" + type);
		case ITEM_CONTENT_BASED:
			throw new IllegalStateException(
					"Not valid for current similarity type:" + type);
		case USER_ITEM_CONTENT_BASED:
			// Using similarity between Empresa and Produto
			return MAX_RATING
					* similarityMatrix.getValue(user.getId(), produto.getId());
		}

		throw new RuntimeException("Unknown recommendation type:" + type);
	}

	public List<PredictedProdutoRating> recommend(Integer userId) {
		return recommend(dataSet.getEmpresa(userId));
	}

	// --------------------------------------------------------------------
	// RECOMMENDATIONS
	// --------------------------------------------------------------------
	public List<PredictedProdutoRating> recommend(Empresa user) {
		List<PredictedProdutoRating> recommendedProdutos = recommend(user, 5);
		return recommendedProdutos;
	}

	public List<PredictedProdutoRating> recommend(Empresa user, int topN) {

		List<PredictedProdutoRating> recommendations = new ArrayList<PredictedProdutoRating>();

		double maxRating = -1.0d;

		for (Produto produto : dataSet.getProdutos()) {

			// only consider items that user hasn't rated yet or doesn't own the
			// content
			if (!skipProduto(user, produto)) {
				double predictedRating = predictRating(user, produto);

				if (maxRating < predictedRating) {
					maxRating = predictedRating;
				}

				if (!Double.isNaN(predictedRating)) {
					recommendations.add(new PredictedProdutoRating(user.getId(),
							produto.getId(), predictedRating));
				}
			} else {
				if (verbose) {
					System.out.println("Skipping produto:" + produto.getName());
				}
			}
		}

		this.maxPredictedRating.put(user.getId(), maxRating);

		List<PredictedProdutoRating> topNRecommendations = PredictedProdutoRating
				.getTopNRecommendations(recommendations, topN);

		if (verbose) {
			PredictedProdutoRating.printEmpresaRecommendations(user, dataSet,
					topNRecommendations);
		}

		return topNRecommendations;
	}

	public void setSimilarityMatrix(SimilarityMatrix similarityMatrix) {
		this.similarityMatrix = similarityMatrix;
	}

	public void setSimilarityThreshold(double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	private boolean skipProduto(Empresa user, Produto produto) {
		boolean skipProduto = true;
		if (isContentBased()) {
			if (user.getEmpresaContent(produto.getProdutoContent().getId()) == null) {
				skipProduto = false;
			}
		} else {
			if (user.getProdutoRating(produto.getId()) == null) {
				skipProduto = false;
			}
		}
		return skipProduto;
	}

}
