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
package br.com.waiso.recommender.database;

import java.util.Collection;
import java.util.List;

import br.com.waiso.recommender.data.Compra;
import br.com.waiso.recommender.data.Empresa;
import br.com.waiso.recommender.data.Produto;

/**
 * Defines service that provides access to all users, items, and ratings.
 * Recommender and similarity implementations rely on this service to access
 * data.
 */
public interface DatasetWaiso {

	/**
	 * For content-based dataset returns array of terms that represent document
	 * space.
	 * 
	 * @return
	 */
	public String[] getAllTerms();

	/**
	 * Provides the average rating for this item
	 * 
	 * @param itemId
	 * @return
	 */
	public double getAverageProdutoRatingByComprador(int itemId);
	
	/**
	 * Provides the average rating for this item
	 * 
	 * @param itemId
	 * @return
	 */
	public double getAverageProdutoRatingByVendedor(int itemId);

	/**
	 * Provides the average rating for this user
	 * 
	 * @param userId
	 * @return
	 */
	public double getAverageCompradorRating(int compradorId);
	
	/**
	 * Provides the average rating for this user
	 * 
	 * @param userId
	 * @return
	 */
	public double getAverageVendedorRating(int vendedorId);
	
	/**
	 * Retrieves a specific item.
	 * 
	 * @param itemId
	 *            item id.
	 * @return item.
	 */
	public Produto getProduto(Integer itemId);

	/**
	 * Total number of all available items.
	 * 
	 * @return number of items.
	 */
	public int getProdutoCount();

	/**
	 * Retrieves all items.
	 * 
	 * @return collection of all items.
	 */
	public Collection<Produto> getProdutos();

	/**
	 * Logical name for the dataset instance.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Provides access to all ratings.
	 * 
	 * @return collection of ratings.
	 */
	public Collection<Compra> getCompras();

	/**
	 * Total number of all available item ratings.
	 * 
	 * @return number of item ratings by users.
	 */
	public int getComprasCount();

	/**
	 * Retrieves a specific user.
	 * 
	 * @param userId
	 *            user id.
	 * @return user.
	 */
	public Empresa getComprador(Integer compradorId);
	
	/**
	 * Retrieves a specific user.
	 * 
	 * @param userId
	 *            user id.
	 * @return user.
	 */
	public Empresa getVendedor(Integer vendedorId);

	/**
	 * Total number of all available users.
	 * 
	 * @return number of users.
	 */
	public int getCompradorCount();
	
	/**
	 * Total number of all available users.
	 * 
	 * @return number of users.
	 */
	public Collection<Empresa> getCompradores();
	
	/**
	 * Total number of all available users.
	 * 
	 * @return number of users.
	 */
	public Collection<Empresa> getVendedores();
	
	/**
	 * Total number of all available users.
	 * 
	 * @return number of users.
	 */
	public int getVendedorCount();

	/**
	 * Provides information about user and item ids returned by this dataset.
	 * 
	 * @return true if ids aren't in sequence and can't be used as array
	 *         indexes. false if user or items ids can be treated as sequences
	 *         that start with 1. In this case index will be derived from id:
	 *         index = id - 1.
	 */
	public boolean isIdMappingRequired();
	
	public List<Compra> getComprasByProdutoId(Integer produtoId);

	public void print();
}
