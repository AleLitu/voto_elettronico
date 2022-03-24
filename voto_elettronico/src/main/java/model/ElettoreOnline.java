public class ElettoreOnline extends Elettore{
	//password dell'elettore online
	private String pwd;
	
	public ElettoreOnline (String nome, String cognome, String pwd, int tessera_elettorale) {
		super(nome, cognome, tessera_elettorale);
		this.pwd = pwd;
	}
	
	//votazione dell'elettore
	public void vota() {}
}