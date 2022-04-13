package model;

import java.io.Serializable;

public class Referendum implements Serializable {
	private int id;
	private String testo;
	
	public Referendum(int id, String testo) {
		this.id = id;
		this.testo = testo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTesto() {
		return testo;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}
}