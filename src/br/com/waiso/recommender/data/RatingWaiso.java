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
package br.com.waiso.recommender.data;


/**
 * Generic representation of a rating given by empresa to a product (produto).
 */
public class RatingWaiso implements java.io.Serializable {

	/**
	 * SVUID
	 */
	private static final long serialVersionUID = 1438346522502387789L;

	protected Produto produto;

	private int empresaId;
	private int produtoId;
	private int rating;

	public RatingWaiso(int empresaId, int bookId, int rating) {
		this.empresaId = empresaId;
		this.produtoId = bookId;
		this.rating = rating;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RatingWaiso other = (RatingWaiso) obj;
		if (produtoId != other.produtoId)
			return false;
		if (rating != other.rating)
			return false;
		if (empresaId != other.empresaId)
			return false;
		return true;
	}

	/**
	 * @return the produto
	 */
	public Produto getProduto() {
		return produto;
	}

	public int getProdutoId() {
		return produtoId;
	}

	public int getRating() {
		return rating;
	}

	public int getEmpresaId() {
		return empresaId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + produtoId;
		result = prime * result + rating;
		result = prime * result + empresaId;
		return result;
	}

	/**
	 * @param produto
	 *            the produto to set
	 */
	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public void setProdutoId(int bookId) {
		this.produtoId = bookId;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setEmpresaId(int empresaId) {
		this.empresaId = empresaId;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[empresaId: " + empresaId
				+ ", produtoId: " + produtoId + ", rating: " + rating + "]";
	}
}
