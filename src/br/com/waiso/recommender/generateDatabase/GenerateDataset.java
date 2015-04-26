package br.com.waiso.recommender.generateDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import br.com.waiso.recommender.database.ConstantsDatabase;

public class GenerateDataset {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StringBuffer buff = new StringBuffer(); 
			FileWriter empresas = new FileWriter(new File(ConstantsDatabase.DATABASE_DIR+""));
			for (int i = 0; i < 30; i++) {
				
			}
			
			empresas.write("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
