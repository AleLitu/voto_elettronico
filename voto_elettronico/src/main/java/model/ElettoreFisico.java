package model;
public class ElettoreFisico extends Elettore{
	
	public ElettoreFisico(String nome, String cognome, int tessera_elettorale) {
		super(nome, cognome, tessera_elettorale);
	}
	
	//votazione dell'elettore
	public void vota() {}
}