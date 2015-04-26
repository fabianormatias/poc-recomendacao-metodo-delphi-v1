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

import org.yooreeka.util.metrics.CosineSimilarityMeasure;

import br.com.waiso.recommender.DatasetWaiso;
import br.com.waiso.recommender.data.Produto;

/**
 * Similarity between produtos based on the content associated with produtos.
 */
public class ProdutoContentBasedSimilarity extends SimilarityMatrixImpl {

	/**
	 * SVUID
	 */
	private static final long serialVersionUID = -2807190886025734879L;

	public ProdutoContentBasedSimilarity(String id, DatasetWaiso ds) {
		this.id = id;
		this.useObjIdToIndexMapping = ds.isIdMappingRequired();
		calculate(ds);
	}

	@Override
	protected void calculate(DatasetWaiso dataSet) {
		int nProdutos = dataSet.getProdutoCount();

		similarityValues = new double[nProdutos][nProdutos];

		// if we want to use mapping from produtoId to index then generate
		// index for every produtoId
		if (useObjIdToIndexMapping) {
			for (Produto produto : dataSet.getProdutos()) {
				idMapping.getIndex(String.valueOf(produto.getId()));
			}
		}

		CosineSimilarityMeasure cosineMeasure = new CosineSimilarityMeasure();
		String[] allTerms = dataSet.getAllTerms();

		for (int u = 0; u < nProdutos; u++) {

			int produtoAId = getObjIdFromIndex(u);
			Produto produtoA = dataSet.getProduto(produtoAId);

			// we only need to calculate elements above the main diagonal.
			for (int v = u + 1; v < nProdutos; v++) {

				int produtoBId = getObjIdFromIndex(v);
				Produto produtoB = dataSet.getProduto(produtoBId);

				similarityValues[u][v] = cosineMeasure.calculate(produtoA
						.getProdutoContent().getTermVector(allTerms), produtoB
						.getProdutoContent().getTermVector(allTerms));
			}

			// for u == v assign 1
			similarityValues[u][u] = 1.0;

		}
	}

}
