package br.com.waiso.recommender.data;

public class Compra {
	
	private Integer compradorId;
	private Integer vendedorId;
	private Integer produtoId;
	private Produto produto;
	private RatingWaiso pontuacao;
	
	public Compra(Integer compradorId, Integer vendedorId, Integer produtoId, Integer rating) {
		this.compradorId = compradorId;
		this.vendedorId = vendedorId;
		this.produtoId = produtoId;
		this.pontuacao = new RatingWaiso(rating);
	}
	
	public void setCompradorId(Integer compradorId) {
		this.compradorId = compradorId;
	}
	
	public Integer getCompradorId() {
		return compradorId;
	}
	
	public Integer getVendedorId() {
		return vendedorId;
	}
	
	public void setEmpresaId(Integer vendedor) {
		this.vendedorId = vendedor;
	}
	
	public Produto getProduto() {
		return produto;
	}
	
	public Integer getProdutoId() {
		return produtoId;
	}
	
	public void setProduto(Produto produto) {
		this.produto = produto;
	}
	
	public Integer getPontuacao() {
		return pontuacao.getRating();
	}
	
	public void setPontuacao(RatingWaiso pontuacao) {
		this.pontuacao = pontuacao;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + produto.getId();
		result = prime * result + pontuacao.getRating();
		result = prime * result + compradorId;
		result = prime * result + vendedorId;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Compra other = (Compra) obj;
		if (produto.getId() != other.getProdutoId())
			return false;
		if (getPontuacao() != other.getPontuacao())
			return false;
		if (compradorId != other.getCompradorId())
			return false;
		if (vendedorId != other.getVendedorId())
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[compradorId: " + getCompradorId()
				+ ", vendedorId: " + getVendedorId()  + ", produtoId: " + getProdutoId() + ", rating: " + getPontuacao() + "]";
	}
	
	

}
