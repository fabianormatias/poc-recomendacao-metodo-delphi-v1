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

import java.util.Collection;
import java.util.logging.Logger;

import org.yooreeka.config.YooreekaConfigurator;

import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;
import br.com.waiso.recommender.data.RatingWaiso;
import br.com.waiso.recommender.database.DatasetWaiso;
import br.com.waiso.recommender.database.WaisoItemCompanyDataset;

/**
 * Calculates Root Mean Squared Error for the recommender.
 */
public class RMSEEstimatorWaiso {

	public static final double DEFAULT_MAXIMUM_RATING=5.0d;
	
	private static final Logger LOG = Logger.getLogger(RMSEEstimatorWaiso.class.getName());

	private double maximumRating;
	
	public RMSEEstimatorWaiso() {
		this(DEFAULT_MAXIMUM_RATING);
	}
	public RMSEEstimatorWaiso(double val) {
		maximumRating = val;
		LOG.setLevel(YooreekaConfigurator.getLevel(RMSEEstimatorWaiso.class.getName()));
	}

//	/**
//	 * Calculates Root Mean Squared Error for the recommender. Uses test rating
//	 * values returned by recommender's dataset.
//	 * 
//	 * @param delphi
//	 *            recommender.
//	 * @return root mean squared error value.
//	 */
//	public double calculateRMSE(Recommender delphi) {
//
//		MovieLensDataset ds = (MovieLensDataset) delphi.getDataset();
//		Collection<RatingWaiso> testRatings = ds.getTestRatings();
//
//		return calculateRMSE(delphi, testRatings);
//	}
//
//	/**
//	 * Calculates Root Mean Squared Error for the recommender.
//	 * 
//	 * @param delphi
//	 *            recommender to evaluate.
//	 * @param testRatings
//	 *            ratings that will be used to calculate the error.
//	 * @return root mean squared error.
//	 */
//	public double calculateRMSE(Recommender delphi,
//			Collection<RatingWaiso> testRatings) {
//
//		double sum = 0.0;
//
//		DatasetWaiso ds = delphi.getDataset();
//
//		int totalSamples = testRatings.size();
//
//		LOG.fine("Calculating RMSE ...");
//		LOG.fine("Training ratings count: "	+ ds.getRatingsCount());
//		LOG.fine("Test ratings count: " + testRatings.size());
//
//		for (RatingWaiso r : testRatings) {
//			Empresa empresa = ds.getEmpresa(r.getEmpresaId());
//			Produto produto = ds.getProduto(r.getProdutoId());
//			double predictedProdutoRating = delphi.predictRating(empresa, produto);
//
//			if (predictedProdutoRating > 5.0) {
//				predictedProdutoRating = 5.0;
//				LOG.finest("Predicted produto rating: " + predictedProdutoRating);
//			}
//			LOG.finest(
//			 "empresa: " + r.getEmpresaId() +
//			 ", produto: " + r.getProdutoId() +
//			 ", actual rating: " + r.getRating() +
//			 ", predicted: " + String.valueOf(predictedProdutoRating));
//
//			sum += Math.pow((predictedProdutoRating - r.getRating()), 2);
//
//		}
//		double rmse = Math.sqrt(sum / totalSamples);
//
//		LOG.fine("RMSE:" + rmse);
//		
//		return rmse;
//	}
//
//	public void compareRMSEs(Recommender delphi) {
//
//		MovieLensDataset ds = (MovieLensDataset) delphi.getDataset();
//		Collection<RatingWaiso> testRatings = ds.getTestRatings();
//
//		compareRMSEs(delphi, testRatings);
//	}
//
//	public void compareRMSEs(Recommender delphi, Collection<RatingWaiso> testRatings) {
//
//		double sum = 0.0;
//		double sumAvgProduto = 0.0;
//		double sumAvgEmpresa = 0.0;
//
//		DatasetWaiso ds = delphi.getDataset();
//
//		int totalSamples = testRatings.size();
//
//		LOG.fine("Calculating RMSE ...");
//		LOG.fine("Training ratings count: "+ds.getRatingsCount());
//		LOG.fine("Test ratings count: " + testRatings.size());
//
//		for (RatingWaiso r : testRatings) {
//			Empresa empresa = ds.getEmpresa(r.getEmpresaId());
//			Produto produto = ds.getProduto(r.getProdutoId());
//			double predictedProdutoRating = delphi.predictRating(empresa, produto);
//			double predictedAvgProdutoRating = delphi
//					.predictBasedOnProdutoAverage(produto);
//			double predictedAvgEmpresaRating = delphi
//					.predictBasedOnEmpresaAverage(empresa);
//
//			if (predictedProdutoRating > maximumRating) {
//				predictedProdutoRating = maximumRating;
//				LOG.finest("Predicted produto rating: " + predictedProdutoRating);
//			}
//			 LOG.finest(
//			 "empresa: " + r.getEmpresaId() +
//			 ", produto: " + r.getProdutoId() +
//			 ", actual rating: " + r.getRating() +
//			 ", predicted: " + String.valueOf(predictedProdutoRating));
//
//			sum += Math.pow((predictedProdutoRating - r.getRating()), 2);
//			sumAvgProduto += Math.pow((predictedAvgProdutoRating - r.getRating()), 2);
//			sumAvgEmpresa += Math.pow((predictedAvgEmpresaRating - r.getRating()), 2);
//
//		}
//
//		double rmse = Math.sqrt(sum / totalSamples);
//		double rmseAvgProduto = Math.sqrt(sumAvgProduto / totalSamples);
//		double rmseAvgEmpresa = Math.sqrt(sumAvgEmpresa / totalSamples);
//
//		System.out.println("RMSE:" + rmse);
//		System.out.println("RMSE (based on avg. Produto rating):" + rmseAvgProduto);
//		System.out.println("RMSE (based on avg. Empresa rating):" + rmseAvgEmpresa);
//	}
//
//	/**
//	 * @return the maximumRating
//	 */
//	public double getMaximumRating() {
//		return maximumRating;
//	}
}
