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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents predicted empresa rating of an produto. Used to return recommendations
 * for the empresa.
 */
public class PredictedProdutoRating {
	/**
	 * Sorts list of recommendations in descending order and return topN
	 * elements.
	 * 
	 * @param recommendations
	 * @param topN
	 * @return
	 */
	public static List<PredictedProdutoRating> getTopNRecommendations(
			List<PredictedProdutoRating> recommendations, int topN) {

		PredictedProdutoRating.sort(recommendations);

		List<PredictedProdutoRating> topRecommendations = new ArrayList<PredictedProdutoRating>();
		for (PredictedProdutoRating r : recommendations) {
			if (topRecommendations.size() >= topN) {
				// have enough recommendations.
				break;
			}
			topRecommendations.add(r);
		}

		return topRecommendations;
	}
	public static void printEmpresaRecommendations(Empresa empresa, DatasetWaiso ds,
			List<PredictedProdutoRating> recommendedProdutos) {
		System.out.println("\nRecommendations for empresa " + empresa.getName()
				+ ":\n");
		for (PredictedProdutoRating r : recommendedProdutos) {
			System.out.printf("Produto: %-36s, predicted rating: %f\n", ds
					.getProduto(r.getProdutoId()).getName(), r.getRating(4));
		}
	}
	/**
	 * Sorts list by rating value in descending order. Produtos with higher ratings
	 * will be in the head of the list.
	 * 
	 * @param values
	 *            list to sort.
	 */
	public static void sort(List<PredictedProdutoRating> values) {
		Collections.sort(values, new Comparator<PredictedProdutoRating>() {

			public int compare(PredictedProdutoRating f1, PredictedProdutoRating f2) {

				int result = 0;
				if (f1.getRating() < f2.getRating()) {
					result = 1; // reverse order
				} else if (f1.getRating() > f2.getRating()) {
					result = -1;
				} else {
					result = 0;
				}
				return result;
			}
		});
	}

	private int empresaId;

	private int produtoId;

	private double rating;

	public PredictedProdutoRating(int empresaId, int produtoId, double rating) {
		this.empresaId = empresaId;
		this.produtoId = produtoId;
		this.rating = rating;
	}

	public int getProdutoId() {
		return produtoId;
	}

	public double getRating() {
		return rating;
	}

	/**
	 * Returns rounded rating value with number of digits after decimal point
	 * specified by <code>scale</code> parameter.
	 * 
	 * @param scale
	 *            number of digits to keep after decimal point.
	 * @return rounded value.
	 */
	public double getRating(int scale) {
		BigDecimal bd = new BigDecimal(rating);
		return bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	public int getEmpresaId() {
		return empresaId;
	}

	public void setRating(double val) {
		this.rating = val;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[empresaId: " + empresaId
				+ ", produtoId: " + produtoId + ", rating: " + rating + "]";
	}
}
