package model;

import java.io.Serializable;

public class Referendum implements Serializable {
	private int id;
	private String nome;
	private String testo;
	
	public Referendum(int id, String nome, String testo) {
		this.id = id;
		this.nome = nome;
		this.testo = testo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
}