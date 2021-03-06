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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import br.com.waiso.recommender.data.Compra;
import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;
import br.com.waiso.recommender.data.RatingWaiso;

public class RatingCountMatrixWaisoProduto extends RatingCountMatrixWaiso implements Serializable {

	/**
	 * Unique identifier for serialization
	 */
	private static final long serialVersionUID = -8216800040843757769L;

	private int matrix[][] = null;

	public RatingCountMatrixWaisoProduto(Produto produtoA, Produto produtoB, int nRatingValues) {
		init(nRatingValues);
		calculate(produtoA, produtoB);
	}

	/*
	 * Populates matrix using empresa ratings for provided produtos. We only consider
	 * empresas that rated both produtos.
	 */
	private void calculate(Produto produtoA, Produto produtoB) {
		for (List<Compra> compraForA : produtoA.getAllComprasByComprador()) {
			// check if the same empresa rated produtoB
			List<Compra> compraForB = produtoB.getCompradorCompra(compraForA.get(0).getCompradorId()); 
			if (compraForB != null) {
				// element in the matrix is determined by the rating values.
				int i = (int) (RatingWaiso.avarageRating(compraForA) - 1);
				int j = (int) (RatingWaiso.avarageRating(compraForB) - 1);
				matrix[i][j]++;
			}
		}
	}

}
