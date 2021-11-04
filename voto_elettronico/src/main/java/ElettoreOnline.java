public class ElettoreOnline extends Elettore{
	private String pwd;
	public ElettoreOnline (String nome, String cognome, String pwd, int tessera_elettorale) {
		super(nome, cognome, tessera_elettorale);
		this.pwd = pwd;
	}
	
	public void vota() {}
}