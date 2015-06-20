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
			int numEmpresas = 10;
			int numModelosProdutos = 2;
			int numCompras = 20;
			String[] nomesProdutos = 
				{"Bicicleta", "Televisão", "Carro", "Computador", "Smartphone", 
				 "Guitarra", "Moto", "Caminhão", "Aparelho de som", "Ar condicionado"};
			
			StringBuffer buff = new StringBuffer();
			File empresas = new File(ConstantsDatabase.DATABASE_DIR+"empresas.txt");
			if(!empresas.exists()) {
				empresas.getParentFile().mkdirs();
			} else {
				empresas.delete();
			}
			
			FileWriter wEmpresas = new FileWriter(empresas);
			for (int i = 1; i <= numEmpresas; i++) {
				buff.append(i);
				buff.append(";");
				buff.append("Empresa ");
				buff.append(i);
				buff.append("\n");
			}
			System.out.println(buff.toString());
			wEmpresas.write(buff.toString());
			wEmpresas.flush();
			wEmpresas.close();
			
			buff = new StringBuffer();
			File produtos = new File(ConstantsDatabase.DATABASE_DIR+"produtos.txt");
			if(!produtos.exists()) {
				produtos.getParentFile().mkdirs();
			} else {
				produtos.delete();
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
			System.out.println(buff.toString());
			wProdutos.write(buff.toString());
			wProdutos.flush();
			wProdutos.close();
			
			buff = new StringBuffer();
			File compras = new File(ConstantsDatabase.DATABASE_DIR+"compras.txt");
			if(!compras.exists()) {
				compras.getParentFile().mkdirs();
			} else {
				compras.delete();
			}
			
			//Gera as compras viciadas caso  o comprador tem id múltiplo de 5 o vendedor também terá 
			//e a pontuação neste caso é sempre 5
			//espera-se como resultado para a busca de uma empresa cujo o id seja múltiplo de 5
			//esta informação será utilizada posteriormente para avaliar os resultados da recomendação
			Random r = new Random();
			FileWriter wCompras = new FileWriter(compras);
			int produtoCompra = 0;
			int comprador = 0;
			int vendedor = 0;
			int pontuacao = 0;
			for (int i = 0; i < numCompras; i++) {
				produtoCompra = r.nextInt(nomesProdutos.length*numModelosProdutos)+1;
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
				buff.append(comprador==0?1:comprador);
				buff.append(";");
				buff.append(vendedor==0?1:vendedor);
				buff.append(";");
				buff.append(produtoCompra==0?1:produtoCompra);
				buff.append(";");
				buff.append(pontuacao);
				buff.append("\n");
			}
			System.out.println(buff.toString());
			wCompras.write(buff.toString());
			wCompras.flush();
			wCompras.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
