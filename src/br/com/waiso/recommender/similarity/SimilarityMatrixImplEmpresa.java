package br.com.waiso.recommender.similarity;

import br.com.waiso.recommender.database.DatasetWaiso;

public abstract class SimilarityMatrixImplEmpresa extends SimilarityMatrixImpl {

	private static final long serialVersionUID = 2650214332178232114L;

	protected abstract void calculateCompradores(DatasetWaiso dataSet);
	protected abstract void calculateVendedores(DatasetWaiso dataSet);
	
	
}
