public abstract class Elettore{
	//nome e cognome dell'elettore
	private String nome, cognome;
	//numero di tessera elettorale
	private int tessera_elettorale;	
	
	public Elettore(String nome, String cognome, int tessera_elettorale ) {
		this.nome = nome;
		this.cognome = cognome;
		this.tessera_elettorale = tessera_elettorale;
	}
	
	//votazione dell'elettore
	abstract void vota();
}