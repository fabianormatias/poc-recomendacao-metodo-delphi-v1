package br.com.waiso.recommender.generateDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import br.com.waiso.recommender.database.ConstantsDatabase;

public class GenerateDataset {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			int numEmpresas = 30;
			int numModelosProdutos = 5;
			int numCompras = 100;
			String[] nomesProdutos = 
				{"Bicicleta", "Televisão", "Carro", "Computador", "Smartphone", 
				 "Guitarra", "Moto", "Caminhão", "Aparelho de som", "Ar condicionado"};
			
			StringBuffer buff = new StringBuffer();
			File empresas = new File(ConstantsDatabase.DATABASE_DIR+"empresas.txt");
			if(!empresas.exists()) {
				empresas.getParentFile().mkdirs();
			}
			
			FileWriter wEmpresas = new FileWriter(empresas);
			for (int i = 1; i <= numEmpresas; i++) {
				buff.append(i);
				buff.append(";");
				buff.append("Empresa ");
				buff.append(i);
				buff.append("\n");
			}
//			System.out.println(buff.toString());
//			wEmpresas.write("");
			
			buff = new StringBuffer();
			File produtos = new File(ConstantsDatabase.DATABASE_DIR+"produtos.txt");
			if(!produtos.exists()) {
				produtos.getParentFile().mkdirs();
			}
			
			FileWriter wProdutos = new FileWriter(produtos);
			int idProduto = 1;
			for (int i = 0; i < nomesProdutos.length; i++) {
				for (int j = 1; j <= numModelosProdutos; j++) {
					buff.append(idProduto);
					buff.append(";");
					buff.append(nomesProdutos[i]+" ");
					buff.append(j);
					buff.append("\n");
					idProduto++;
				}
			}
//			System.out.println(buff.toString());
//			wProdutos.write("");
			
			buff = new StringBuffer();
			File compras = new File(ConstantsDatabase.DATABASE_DIR+"compras.txt");
			if(!compras.exists()) {
				compras.getParentFile().mkdirs();
			}
			
			Random r = new Random();
			FileWriter wCompras = new FileWriter(compras);
			int produtoCompra = 0;
			int comprador = 0;
			int vendedor = 0;
			int pontuacao = 0;
			for (int i = 0; i < numCompras; i++) {
				produtoCompra = r.nextInt(idProduto)+1;
				comprador = r.nextInt(numEmpresas)+1;
				if((comprador%5) == 0) {
					vendedor = r.nextInt(numEmpresas)+1;
					while((vendedor%5) != 0 || vendedor == comprador) {
						vendedor = r.nextInt(numEmpresas)+1;
					}
					pontuacao = 5;
				} else {
					while(vendedor == comprador) {
						vendedor = r.nextInt(numEmpresas)+1;
					}
					pontuacao = r.nextInt(5)+1;
				}
				buff.append(comprador);
				buff.append(";");
				buff.append(vendedor);
				buff.append(";");
				buff.append(produtoCompra);
				buff.append(";");
				buff.append(pontuacao);
				buff.append("\n");
			}
//			System.out.println(buff.toString());
//			wCompras.write("");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
