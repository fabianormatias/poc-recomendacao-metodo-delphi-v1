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

import br.com.waiso.recommender.DatasetWaiso;
import br.com.waiso.recommender.RatingCountMatrix;
import br.com.waiso.recommender.data.Empresa;


public class EmpresaBasedSimilarity extends SimilarityMatrixImpl {

	/**
	 * Unique identifier for serialization
	 */
	private static final long serialVersionUID = 5741616253320567238L;

	public EmpresaBasedSimilarity(DatasetWaiso dataSet) {

		this(EmpresaBasedSimilarity.class.getSimpleName(), dataSet, true);
	}

	public EmpresaBasedSimilarity(String id, DatasetWaiso dataSet,
			boolean keepRatingCountMatrix) {
		this.id = id;
		this.keepRatingCountMatrix = keepRatingCountMatrix;
		this.useObjIdToIndexMapping = dataSet.isIdMappingRequired();
		calculate(dataSet);
	}

	// here we assume that empresaId and bookId are:
	// - integers,
	// - start with 1
	// - have no gaps in sequence.
	// Otherwise we would have to have a mapping from empresaId/bookId into index
	@Override
	protected void calculate(DatasetWaiso dataSet) {

		int nEmpresas = dataSet.getEmpresaCount();
		int nRatingValues = 5;

		similarityValues = new double[nEmpresas][nEmpresas];

		if (keepRatingCountMatrix) {
			ratingCountMatrix = new RatingCountMatrix[nEmpresas][nEmpresas];
		}

		// if we want to use mapping from empresaId to index then generate
		// index for every empresaId
		if (useObjIdToIndexMapping) {
			for (Empresa u : dataSet.getEmpresas()) {
				idMapping.getIndex(String.valueOf(u.getId()));
			}
		}

		for (int u = 0; u < nEmpresas; u++) {

			int empresaAId = getObjIdFromIndex(u);
			Empresa empresaA = dataSet.getEmpresa(empresaAId);

			for (int v = u + 1; v < nEmpresas; v++) {

				int empresaBId = getObjIdFromIndex(v);
				Empresa empresaB = dataSet.getEmpresa(empresaBId);

				RatingCountMatrix rcm = new RatingCountMatrix(empresaA, empresaB,
						nRatingValues);

				int totalCount = rcm.getTotalCount();
				int agreementCount = rcm.getAgreementCount();

				if (agreementCount > 0) {

					similarityValues[u][v] = (double) agreementCount
							/ (double) totalCount;
				} else {
					similarityValues[u][v] = 0.0;
				}

				// For large datasets
				if (keepRatingCountMatrix) {
					ratingCountMatrix[u][v] = rcm;
				}
			}

			// for u == v assign 1.
			// RatingCountMatrix wasn't created for this case
			similarityValues[u][u] = 1.0;
		}
	}
}
