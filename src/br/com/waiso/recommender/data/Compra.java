package br.com.waiso.recommender.data;

public class Compra {
	
	private Empresa empresaCompradora;
	private Empresa empresaVendedora;
	private Produto produto;
	private RatingWaiso pontuacao;
	
	public Compra(Empresa comprador, Empresa vendedor, Produto produto, RatingWaiso rating) {
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
	
	public Integer getCompradorId() {
		return empresaCompradora.getId();
	}
	
	public Empresa getEmpresaVendedora() {
		return empresaVendedora;
	}
	
	public Integer getVendedorId() {
		return empresaVendedora.getId();
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
	public RatingWaiso getPontuacao() {
		return pontuacao;
	}
	public void setPontuacao(RatingWaiso pontuacao) {
		this.pontuacao = pontuacao;
	}
	
	

}
