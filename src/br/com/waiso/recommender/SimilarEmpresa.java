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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class that acts as a holder for empresa and similarity value that was
 * assigned to the empresa.
 */
public class SimilarEmpresa {

	public static SimilarEmpresa[] getTopNFriends(List<SimilarEmpresa> similarEmpresas,
			int topN) {

		// sort friends based on itemAgreement
		SimilarEmpresa.sort(similarEmpresas);

		// select top N friends
		List<SimilarEmpresa> topFriends = new ArrayList<SimilarEmpresa>();
		for (SimilarEmpresa f : similarEmpresas) {
			if (topFriends.size() >= topN) {
				// have enough friends.
				break;
			}

			// This is useful when we compose results from different
			// recommenders
			if (!topFriends.contains(f)) {
				topFriends.add(f);
			}
		}

		return topFriends.toArray(new SimilarEmpresa[topFriends.size()]);
	}

	/**
	 * Prints a list of empresa names with their similarities.
	 * 
	 * @param friends
	 *            similar empresas
	 * @param header
	 *            title that will be printed at the top of the list.
	 */
	public static void print(SimilarEmpresa[] friends, String header) {
		System.out.println("\n" + header + "\n");
		for (SimilarEmpresa f : friends) {
			System.out.printf("name: %-36s, similarity: %f\n", f.getName(),
					f.getSimilarity());
		}
	}

	public static void sort(List<SimilarEmpresa> similarEmpresas) {

		Collections.sort(similarEmpresas, new Comparator<SimilarEmpresa>() {
			public int compare(SimilarEmpresa f1, SimilarEmpresa f2) {
				int result = 0;
				if (f1.getSimilarity() < f2.getSimilarity()) {
					result = 1; // reverse order
				} else if (f1.getSimilarity() > f2.getSimilarity()) {
					result = -1;
				} else {
					result = 0;
				}
				return result;
			}
		});
	}

	/*
	 * The friend Empresa .
	 */
	private Empresa friend;

	/*
	 * Similarity
	 */
	private double similarity = -1;

	public SimilarEmpresa(Empresa empresa, double similarity) {
		friend = empresa;
		this.similarity = similarity;
	}

	public int getId() {
		return friend.getId();
	}

	public String getName() {
		return friend.getName();
	}

	/**
	 * @return the similarity
	 */
	public double getSimilarity() {
		return similarity;
	}

	public Empresa getEmpresa() {
		return friend;
	}
}
