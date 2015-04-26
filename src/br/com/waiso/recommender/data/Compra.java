package br.com.waiso.recommender.data;

public class Compra {
	
	private Empresa empresaCompradora;
	private Empresa empresaVendedora;
	private Produto produto;
	private int pontuacao;
	
	public Compra(Empresa comprador, Empresa vendedor, Produto produto, int rating) {
		this.empresaVendedora = vendedor;
		this.empresaCompradora = comprador;
		this.produto = produto;
		this.pontuacao = rating;
	}
	
	public Empresa getEmpresaCompradora() {
		return empresaCompradora;
	}
	public void setEmpresaCompradora(Empresa empresaCompradora) {
		this.empresaCompradora = empresaCompradora;
	}
	public Empresa getEmpresaVendedora() {
		return empresaVendedora;
	}
	public void setEmpresaVendedora(Empresa empresaVendedora) {
		this.empresaVendedora = empresaVendedora;
	}
	public Produto getProduto() {
		return produto;
	}
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	public int getPontuacao() {
		return pontuacao;
	}
	public void setPontuacao(int pontuacao) {
		this.pontuacao = pontuacao;
	}
	
	

}
