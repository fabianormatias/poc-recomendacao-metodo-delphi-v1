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

import java.util.Collection;

import org.yooreeka.algos.reco.collab.model.Content;
import org.yooreeka.util.metrics.CosineSimilarityMeasure;

import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.database.DatasetWaiso;

/**
 * Similarity between empresas based on the content associated with empresas.
 */
public class EmpresaContentBasedSimilarity extends SimilarityMatrixImplEmpresa {

	/**
	 * SVUID
	 */
	private static final long serialVersionUID = 5809078434246172835L;

	public EmpresaContentBasedSimilarity(String id, DatasetWaiso ds) {
		this.id = id;
		this.useObjIdToIndexMapping = ds.isIdMappingRequired();
		calculate(ds);
	}
	
	// here we assume that empresaId and bookId are:
	// - integers,
	// - start with 1
	// - have no gaps in sequence.
	// Otherwise we would have to have a mapping from empresaId/bookId into index
	@Override
	protected void calculateCompradores(DatasetWaiso dataSet) {
		calculate(dataSet, true);
	}
	
	// here we assume that empresaId and bookId are:
	// - integers,
	// - start with 1
	// - have no gaps in sequence.
	// Otherwise we would have to have a mapping from empresaId/bookId into index
	@Override
	protected void calculateVendedores(DatasetWaiso dataSet) {
		calculate(dataSet, false);
	}

	private void calculate(DatasetWaiso dataSet, boolean compradores) {

		int nEmpresas = compradores ? dataSet.getCompradorCount() : dataSet.getVendedorCount();

		similarityValues = new double[nEmpresas][nEmpresas];

		// if we want to use mapping from empresaId to index then generate
		// index for every empresaId
		if (useObjIdToIndexMapping) {
			Collection<Empresa> empresas = compradores ? dataSet.getCompradores() : dataSet.getVendedores();
			for (Empresa u : empresas) {
				idMapping.getIndex(String.valueOf(u.getId()));
			}
		}

		CosineSimilarityMeasure cosineMeasure = new CosineSimilarityMeasure();
		String[] allTerms = dataSet.getAllTerms();

		for (int u = 0; u < nEmpresas; u++) {
			int empresaAId = getObjIdFromIndex(u);
			Empresa empresaA = compradores ? dataSet.getComprador(empresaAId) : dataSet.getVendedor(empresaAId);

			for (int v = u + 1; v < nEmpresas; v++) {

				int empresaBId = getObjIdFromIndex(v);
				Empresa empresaB = compradores ? dataSet.getComprador(empresaBId) : dataSet.getVendedor(empresaBId);

				double similarity = 0.0;

				for (Content empresaAContent : empresaA.getEmpresaContent()) {

					double bestCosineSimValue = 0.0;

					for (Content empresaBContent : empresaB.getEmpresaContent()) {
						double cosineSimValue = cosineMeasure.calculate(
								empresaAContent.getTermVector(allTerms),
								empresaBContent.getTermVector(allTerms));
						bestCosineSimValue = Math.max(bestCosineSimValue,
								cosineSimValue);
					}

					similarity += bestCosineSimValue;
				}
				// System.out.println("Similarity empresa[" + u + "][" + v + "]=" +
				// similarity);
				similarityValues[u][v] = similarity
						/ empresaA.getEmpresaContent().size();
			}

			// for u == v assign 1.
			similarityValues[u][u] = 1.0;
		}
	}

	//TODO alterar posteriormente para este método sumir
	@Override
	protected void calculate(DatasetWaiso dataSet) {
		// TODO Auto-generated method stub
		
	}
}
