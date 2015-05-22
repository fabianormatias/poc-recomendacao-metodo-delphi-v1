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
import org.yooreeka.util.internet.crawling.util.ValueToIndexMapping;
import org.yooreeka.util.metrics.CosineSimilarityMeasure;

import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;
import br.com.waiso.recommender.database.DatasetWaiso;

/**
 * Similarity between empresas based on the content associated with empresas.
 */
public class EmpresaProdutoContentBasedSimilarity extends SimilarityMatrixImplEmpresa {

	/**
	 * SVUID
	 */
	private static final long serialVersionUID = -372816966539384847L;

	private ValueToIndexMapping idMappingForEmpresa = new ValueToIndexMapping();
	private ValueToIndexMapping idMappingForProduto = new ValueToIndexMapping();

	public EmpresaProdutoContentBasedSimilarity(String id, DatasetWaiso ds) {
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
		int nProdutos = dataSet.getProdutoCount();

		similarityValues = new double[nEmpresas][nProdutos];

		// if we want to use mapping from empresaId/produtoId to matrix index
		// then we need to generate index for every empresaId and produtoId
		if (useObjIdToIndexMapping) {
			Collection<Empresa> empresas = compradores ? dataSet.getCompradores() : dataSet.getVendedores();
			for (Empresa u : empresas) {
				idMappingForEmpresa.getIndex(String.valueOf(u.getId()));
			}

			for (Produto i : dataSet.getProdutos()) {
				idMappingForProduto.getIndex(String.valueOf(i.getId()));
			}
		}

		CosineSimilarityMeasure cosineMeasure = new CosineSimilarityMeasure();
		String[] allTerms = dataSet.getAllTerms();

		for (int u = 0; u < nEmpresas; u++) {
			int empresaId = getEmpresaIdForIndex(u);
			Empresa empresa = compradores ? dataSet.getComprador(empresaId) : dataSet.getVendedor(empresaId);

			for (int v = 0; v < nProdutos; v++) {

				int produtoId = getProdutoIdFromIndex(v);
				Produto produto = dataSet.getProduto(produtoId);

				double simValue = 0.0;
				double bestCosineSimValue = 0.0;

				for (Content empresaContent : empresa.getEmpresaContent()) {

					simValue = cosineMeasure.calculate(empresaContent
							.getTermVector(allTerms), produto.getProdutoContent()
							.getTermVector(allTerms));
					bestCosineSimValue = Math.max(bestCosineSimValue, simValue);
				}

				similarityValues[u][v] = bestCosineSimValue;
			}
		}
	}

	/*
	 * Utility method to convert produtoId into matrix index
	 */
	private int getIndexForProdutoId(Integer produtoId) {
		int index = 0;
		if (useObjIdToIndexMapping) {
			index = idMappingForProduto.getIndex(String.valueOf(produtoId));
		} else {
			index = produtoId - 1;
		}
		return index;
	}

	/*
	 * Utility method to convert empresaId into matrix index.
	 */
	private int getIndexForEmpresaId(Integer empresaId) {
		int index = 0;
		if (useObjIdToIndexMapping) {
			index = idMappingForEmpresa.getIndex(String.valueOf(empresaId));
		} else {
			index = empresaId - 1;
		}
		return index;
	}

	@Override
	protected int getIndexFromObjId(Integer objId) {
		throw new UnsupportedOperationException(
				"Should not be used. Use empresa or produto specific method istead.");
	}

	/*
	 * Utility method to convert matrix index into produtoId.
	 */
	private Integer getProdutoIdFromIndex(int index) {
		Integer objId;
		if (useObjIdToIndexMapping) {
			objId = Integer.parseInt(idMappingForProduto.getValue(index));
		} else {
			objId = index + 1;
		}
		return objId;
	}

	@Override
	protected Integer getObjIdFromIndex(int index) {
		throw new UnsupportedOperationException(
				"Should not be used.  Use empresa or produto specific method istead.");
	}

	/*
	 * Utility method to convert matrix index into empresaId
	 */
	private Integer getEmpresaIdForIndex(int index) {
		Integer objId;
		if (useObjIdToIndexMapping) {
			objId = Integer.parseInt(idMappingForEmpresa.getValue(index));
		} else {
			objId = index + 1;
		}
		return objId;
	}

	@Override
	public double getValue(Integer empresaId, Integer produtoId) {
		if (similarityValues == null) {
			throw new IllegalStateException(
					"You have to calculate similarities first.");
		}

		int x = getIndexForEmpresaId(empresaId);
		int y = getIndexForProdutoId(produtoId);

		return similarityValues[x][y];
	}

	//TODO alterar posteriormente para este método sumir
	@Override
	protected void calculate(DatasetWaiso dataSet) {
		
	}

}
