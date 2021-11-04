public abstract class Elettore{
	private String nome, cognome;
	private int tessera_elettorale;	
	public Elettore(String nome, String cognome, int tessera_elettorale ) {
		this.nome = nome;
		this.cognome = cognome;
		this.tessera_elettorale = tessera_elettorale;
	}
	abstract void vota();
}