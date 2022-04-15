package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Partito implements Serializable{
	int id;
	String nome;
	ArrayList<String> candidati;
	
	public Partito(int id, String nome, ArrayList<String> candidati) {
		this.id = id;
		this.nome = nome;
		this.candidati = candidati;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<String> getCandidati() {
		return candidati;
	}

	public void setCandidati(ArrayList<String> candidati) {
		this.candidati = candidati;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}