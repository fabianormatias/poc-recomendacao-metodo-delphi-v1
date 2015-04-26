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
package br.com.waiso.recommender.similarity;

import br.com.waiso.recommender.data.Produto;
import br.com.waiso.recommender.database.DatasetWaiso;

public class ProdutoPenaltyBasedSimilarity extends SimilarityMatrixImpl {

	/**
	 * Unique identifier for serialization
	 */
	private static final long serialVersionUID = -6137735175034641281L;

	public ProdutoPenaltyBasedSimilarity(DatasetWaiso dataSet) {

		this(ProdutoPenaltyBasedSimilarity.class.getSimpleName(), dataSet, true);
	}

	public ProdutoPenaltyBasedSimilarity(String id, DatasetWaiso dataSet,
			boolean keepRatingCountMatrix) {
		this.id = id;
		this.keepRatingCountMatrix = keepRatingCountMatrix;
		this.useObjIdToIndexMapping = dataSet.isIdMappingRequired();
		calculate(dataSet);
	}

	@Override
	protected void calculate(DatasetWaiso dataSet) {

		int nProdutos = dataSet.getProdutoCount();
		int nRatingValues = 5;

		/*
		 * The penalties distort the scale that we use for similarities
		 * maxBoundWeight is an auxiliary variable for scaling back to [0,1]
		 */
		double scaleFactor = 0.0;

		similarityValues = new double[nProdutos][nProdutos];

		if (keepRatingCountMatrix) {
			ratingCountMatrix = new RatingCountMatrix[nProdutos][nProdutos];
		}

		// if we want to use mapping from produtoId to index then generate
		// index for every produtoId
		if (useObjIdToIndexMapping) {
			for (Produto produto : dataSet.getProdutos()) {
				idMapping.getIndex(String.valueOf(produto.getId()));
			}
		}

		// By using these variables we reduce the number of method calls
		// inside the double loop.
		int totalCount = 0;
		int agreementCount = 0;

		for (int u = 0; u < nProdutos; u++) {

			int produtoAId = getObjIdFromIndex(u);
			Produto produtoA = dataSet.getProduto(produtoAId);

			// we only need to calculate elements above the main diagonal.
			for (int v = u + 1; v < nProdutos; v++) {

				int produtoBId = getObjIdFromIndex(v);

				Produto produtoB = dataSet.getProduto(produtoBId);

				RatingCountMatrix rcm = new RatingCountMatrix(produtoA, produtoB,
						nRatingValues);

				totalCount = rcm.getTotalCount();
				agreementCount = rcm.getAgreementCount();

				if (agreementCount > 0) {

					/*
					 * See ImprovedUserBasedSimilarity class for detailed
					 * explanation.
					 */
					double weightedDisagreements = 0.0;

					int maxBandId = rcm.getMatrix().length - 1;

					for (int matrixBandId = 1; matrixBandId <= maxBandId; matrixBandId++) {

						/*
						 * The following is a heuristic. Can you figure out what
						 * characteristics are captured in such an expression?
						 * The numbers 1.8 and 0.4 are arbitrary, however, we
						 * could define them by solving an optimization problem.
						 * How would you formulate the problem? How would you
						 * solve it?
						 */
						double bandWeight = 1.8 - Math.exp(1 - matrixBandId);
						bandWeight = Math.pow(bandWeight, 0.4);

						if (bandWeight > scaleFactor) {
							scaleFactor = bandWeight;
						}

						weightedDisagreements += bandWeight
								* rcm.getBandCount(matrixBandId);
					}

					double similarityValue = 1.0 - (weightedDisagreements / totalCount);

					// w is the upper (negative) bound of the weighted
					// similarity scale
					double w = scaleFactor * (totalCount - agreementCount);

					similarityValues[u][v] = (w + similarityValue) / (w + 1);

				} else {
					similarityValues[u][v] = 0.0;
				}

				if (keepRatingCountMatrix) {
					ratingCountMatrix[u][v] = rcm;
				}
			}

			// for u == v assign 1
			// ratingCountMatrix wasn't created for this case
			similarityValues[u][u] = 1.0;

		}
	}

}
