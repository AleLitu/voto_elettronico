package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.crypto.Cipher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Candidato;

import model.User;

public class ControllerRegistrazione{

	private static User user;
	private static Socket so;
    OutputStream outputStream;
    InputStream inputStream;
    public final static int SOCKET_PORT=50000;
	
    @FXML
    private MenuButton btnAnno;

    @FXML
    private MenuButton btnGiorno;

    @FXML
    private MenuButton btnMese;

    @FXML
    private Button btnregister;
    
    @FXML
    private Button btnIndietro;
    
    @FXML
    private Label lblIndirizzo;

    @FXML
    private Label lblCitta;

    @FXML
    private Label lblCognome;

    @FXML
    private Label lblComune;

    @FXML
    private Label lblConfPassword;

    @FXML
    private Label lblDataNascita;

    @FXML
    private TextField lblFirst;

    @FXML
    private TextField lblFourth;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblNome;

    @FXML
    private Label lblPaese;

    @FXML
    private Label lblPassword;

    @FXML
    private TextField lblSecond;

    @FXML
    private TextField lblThird;

    @FXML
    private TextField tfCitta;

    @FXML
    private TextField tfCognome;

    @FXML
    private TextField tfComune;

    @FXML
    private PasswordField pfConfPassword;

    @FXML
    private TextField tfNome;

    @FXML
    private TextField tfPaese;

    @FXML
    private PasswordField pfPassword;
    
    @FXML
    private Label lblCF;
    
    @FXML
    private TextField tfCF;
    
    @FXML
    private MenuButton mbSesso;
    
    @FXML
    private MenuItem miFemmina;

    @FXML
    private MenuItem miMaschio;
    
    @FXML
    private MenuItem btnGiorni[];
    
    @FXML
    private MenuItem btnMesi[];
    
    @FXML
    private MenuItem btnAnni[];
    
    private boolean connected;
    
    private int mesi = 12;
    private int anni = 120;
    private int giorni = 31;
     
    private PublicKey pubKey;
    
    //@requires a > 0 && m > 0 && g > 0;
    //@ensures (a > 18 || (a == 18 && (m > 0 || (m == 0 && g >= 0))));
    public static boolean maggiorenne(int a, int m, int g) {
    	LocalDate dataCorrente = LocalDate.now();
        final int annoAttuale = dataCorrente.getYear();
        final int meseAttuale = dataCorrente.getMonthValue();
        final int giornoAttuale = dataCorrente.getDayOfMonth();
    	a = annoAttuale - a;
    	m = meseAttuale - m;
    	g = giornoAttuale - g;
    	if(a > 18 || (a == 18 && (m > 0 || (m == 0 && g >= 0))))
    		return true;
    	return false;
    }
    
    public void connection (String address) throws IOException, ClassNotFoundException{
    	try {
	    	if(connected == false) {
	    		so = new Socket(address, SOCKET_PORT);
	    		inputStream = so.getInputStream();
	    		outputStream = so.getOutputStream();
	    	}    		
		} catch (IOException e) {
			e.printStackTrace();
		}
    	System.out.println("Client connesso, Indirizzo: " + so.getInetAddress() + "; porta: "+ so.getPort());
    }
    
    public static int isInteger(String input) {
        try {
            int n;
            n=Integer.parseInt(input);
            return n;
        } catch (Exception e) {
            return -1;
        }
    }
    
    protected void checkIntType() throws Exception {
        String n1=lblFirst.getText();
        String n2=lblSecond.getText();
        String n3=lblThird.getText();
        String n4=lblFourth.getText();

        int n1_int=isInteger(n1);
        int n2_int=isInteger(n2);
        int n3_int=isInteger(n3);
        int n4_int=isInteger(n4);
        if(((n1_int<0))||(n1_int>255)||((n2_int<0))||(n2_int>255)||((n3_int<0))||(n3_int>255)||((n4_int<0))||(n4_int>255)){
        	lblMessage.setText("Indirizzo sbagliato coglione");
        }else{
            String indirizzo=n1+"."+n2+"."+n3+"."+n4;
            //errorLabel.setText("L'indirizzo inserito: "+indirizzo);
            //errorLabel.setOpacity(1);
            connection(indirizzo);
            //DA QUA CI DOVREBBE ESSERE IL TENTATIVO DI CONNESSIONE AL SERVER
        }
    }
    
    public static char[] creazione_cf(String nome, String cognome, String nazione_nascita, char sesso, int giorno, int mese, int anno, char[] cf1){
		try {	
			char[] cf = new char[16];
			int count = 0;
			cognome = cognome.toUpperCase();
			for(int i = 0; i < cognome.length(); i++){
				if(cognome.charAt(i) != 'A' && cognome.charAt(i) != 'E' && cognome.charAt(i) != 'I' && cognome.charAt(i) != 'O' && cognome.charAt(i) != 'U' && count < 3){
					cf[count] = cognome.charAt(i);
					count++;
				}
			}
			if(count != 3){
				for(int i = 0; i < cognome.length(); i++){
					if((cognome.charAt(i) == 'A' || cognome.charAt(i) == 'E' || cognome.charAt(i) == 'I' || cognome.charAt(i) == 'O' || cognome.charAt(i) == 'U') && count < 3){
						cf[count] = cognome.charAt(i);
						count++;
					}
				}
			}
			while(count < 3){
				cf[count] = 'X';
				count++;
			}
			nome = nome.toUpperCase();
			int count_cons = 0, count_loop = 0;
			for(int i = 0; i < nome.length(); i++){
				if(nome.charAt(i) != 'A' && nome.charAt(i) != 'E' && nome.charAt(i) != 'I' && nome.charAt(i) != 'O' && nome.charAt(i) != 'U' && count < 6){
					count_cons++;
				}
			}
			for(int i = 0; i < nome.length(); i++){
				if(nome.charAt(i) != 'A' && nome.charAt(i) != 'E' && nome.charAt(i) != 'I' && nome.charAt(i) != 'O' && nome.charAt(i) != 'U' && count < 6){
					count_loop++;
					if(count_cons >= 4 && count_loop == 2){}
					else{
						cf[count] = nome.charAt(i);
						count++;
					}
				}
			}
			if(count != 6){
				for(int i = 0; i < nome.length(); i++){
					if((nome.charAt(i) == 'A' || nome.charAt(i) == 'E' || nome.charAt(i) == 'I' || nome.charAt(i) == 'O' || nome.charAt(i) == 'U') && count < 6){
						cf[count] = nome.charAt(i);
						count++;
					}
				}
			}
			while(count < 6){
				cf[count] = 'X';
				count++;
			}
			String a = Integer.toString(anno);
			char[] an = new char[4];
			for(int i = 0; i < 4; i++){
				an[i] = a.charAt(i);
			}		
			cf[6] = an[2];
			cf[7] = an[3];
			if(mese == 1)
				cf[8] = 'A';
			else if(mese == 2)
				cf[8] = 'B';
			else if(mese == 3)
				cf[8] = 'C';
			else if(mese == 4)
				cf[8] = 'D';
			else if(mese == 5)
				cf[8] = 'E';
			else if(mese == 6)
				cf[8] = 'H';
			else if(mese == 7)
				cf[8] = 'L';
			else if(mese == 8)
				cf[8] = 'M';
			else if(mese == 9)
				cf[8] = 'P';
			else if(mese == 10)
				cf[8] = 'R';
			else if(mese == 11)
				cf[8] = 'S';
			else if(mese == 12)
				cf[8] = 'T';
			if(sesso == 'F')
				giorno += 40;
			String g = Integer.toString(giorno);
			if(giorno < 10)
				g = "0" + g;
			char[] gi = new char[4];
			for(int i = 0; i < 2; i++){
				gi[i] = g.charAt(i);
			}
			cf[9] = gi[0];
			cf[10] = gi[1];
			if ('A' <= cf1[11] && cf1[11] <= 'Z' && '0' <= cf1[12] && cf1[12] <= '9' && '0' <= cf1[13] && cf1[13] <= '9' && '0' <= cf1[14] && cf1[14] <= '9'){
				cf[11] = cf1[11];
				cf[12] = cf1[12];
				cf[13] = cf1[13];
				cf[14] = cf1[14];
			}
			if (!nazione_nascita.equals("Italia"))
				cf[15] = 'Z';
			else if('A' <= cf1[15] && cf1[15] <= 'Y')
				cf[15] = cf1[15];
			return cf;
    	}catch(Exception e) {
    		return null;
    	}
	}
    
    //@ensures (\forall int i; i>= 0 && i< cfc.length; cf.charArray[i] == cfc[i]);
    public static boolean codiceFiscaleIsOk(String cf, char[] cfc) {
    	char[]cft = cf.toCharArray();
    	for (int i = 0; i < cfc.length; i++) {
    		if (!String.valueOf(cft[i]).equals(String.valueOf(cfc[i])))
    			return false;
    	}
    	return true;
    }
    
    @FXML
    void handleIndietro(ActionEvent event) throws Exception {
    	System.out.println("conn "+connected);
    	if(connected == true) {
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
	    	Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Gestore");
		}else {
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
	    	Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Login");
		}
    }

    @FXML
    void handleRegistrazione(ActionEvent event) throws Exception {
    	String codiceFiscale = tfCF.getText();
    	String sesso = mbSesso.getText();
		lblFirst.setText("127");
    	lblSecond.setText("0");
    	lblThird.setText("0");
    	lblFourth.setText("1");
    	checkIntType();
    	String cognome = tfCognome.getText();
    	String nome = tfNome.getText();
    	String password = pfPassword.getText();
    	String confPassword = pfConfPassword.getText();
    	String anno = btnAnno.getText();
    	String mese = btnMese.getText();
    	String giorno = btnGiorno.getText();
    	String paese = tfPaese.getText();
    	String citta = tfCitta.getText();
    	String comune = tfComune.getText();
    	if (codiceFiscale.equals("") || cognome.equals("") || nome.equals("") || password.equals("") || confPassword.equals("") || paese.equals("") || citta.equals("") || comune.equals("") || anno.equals("Anno") || mese.equals("Mese") || giorno.equals("Giorno") /* || codiceFiscale.equals("")*/) {
    		Alert alert = new Alert(AlertType.WARNING, "Compilare tutti i campi!", ButtonType.CLOSE);
    		alert.show();
    	}else {
    		int a = Integer.parseInt(anno);
    		int m = Integer.parseInt(mese);
    		int g = Integer.parseInt(giorno);
    		if(!maggiorenne(a, m, g)) {
    			Alert alert = new Alert(AlertType.WARNING, "Per potersi registrare bisogna essere maggiorenni!", ButtonType.CLOSE);
        		alert.show();
    		}else if(!password.equals(confPassword)) {
    			Alert alert = new Alert(AlertType.WARNING, "Password e conferma password diversi!", ButtonType.CLOSE);
        		alert.show();
    		}else {
    			char[] cf = creazione_cf(nome, cognome, paese, sesso.charAt(0), Integer.parseInt(giorno), Integer.parseInt(mese), Integer.parseInt(anno), codiceFiscale.toCharArray());
    			if (cf == null || !codiceFiscaleIsOk(codiceFiscale, cf)) {
    				Alert alert = new Alert(AlertType.WARNING, "Codice Fiscale errato", ButtonType.CLOSE);
    				alert.show();
	    		}else {
    				if(connected != true)
    					user = new User(codiceFiscale, cognome, nome, password, sesso, Integer.parseInt(anno), Integer.parseInt(mese), Integer.parseInt(giorno), paese, citta, comune, "elettore");
    				else
    					user = new User(codiceFiscale, cognome, nome, "", sesso, Integer.parseInt(anno), Integer.parseInt(mese), Integer.parseInt(giorno), paese, citta, comune, "elettore");
    				int dim_buffer = 100;
					int letti;
					String risposta;
					byte buffer[] = new byte[dim_buffer];
					outputStream.write("registrazione".getBytes(), 0, "registrazione".length());
					//outputStream.write("registrazione".getBytes(), 0, "registrazione".length());
					letti = inputStream.read(buffer);
			        risposta = new String(buffer, 0, letti);
			        if(risposta.equals("ok")) {
			        	/*byte buffer1[] = new byte[100];
			        	//byte[] cipherData = null;
			        	Cipher cipher = Cipher.getInstance("RSA");
			        	cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			        	byte[] cipherData = cipher.doFinal(user.toString().getBytes());
				        DataOutputStream dos = new DataOutputStream(outputStream);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
						int mes = inputStream.read(buffer1);
						String r = new String(buffer1, 0, mes);*/
			        	outputStream.write(user.toString().getBytes(), 0, user.toString().length());
						letti = inputStream.read(buffer);
				        String r = new String(buffer, 0, letti);
						if(r.equals("ok")) {
							Alert alert = new Alert(AlertType.INFORMATION , "Registrato correttamente");
							alert.showAndWait().ifPresent(response -> {
								if (response == ButtonType.OK) {
									try {
										if(connected == false)
											outputStream.write("logout".getBytes(), 0, "logout".length());
										if(connected == true) {
											Node node = (Node) event.getSource();
					    					Stage actual = (Stage) node.getScene().getWindow();
					    			    	Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
					    			        actual.setScene(new Scene(root));
					    			        actual.setTitle("Gestore");
										}else {
											Node node = (Node) event.getSource();
					    					Stage actual = (Stage) node.getScene().getWindow();
					    			    	Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
					    			        actual.setScene(new Scene(root));
					    			        actual.setTitle("Login");
										}
									} catch (Exception e) {
										new Alert(AlertType.ERROR, "Errore nel ritorno alla pagina di login", ButtonType.CLOSE).show();
										return;
									}
								}
							});
						}else {
							new Alert(AlertType.ERROR, "Errore nella registrazione del voto, riprovare", ButtonType.CLOSE).show();
							return;
						}
			        }
	    		 }
    		}
    	}
    }
  
    
    @FXML
    public void initialize() throws IOException {
    	connected = false;
    	System.out.println("socket: "+ControllerLogin.getSocket());
    	if(ControllerLogin.getSocket() != null) {
    		lblIndirizzo.setVisible(false);
        	lblFirst.setVisible(false);
        	lblSecond.setVisible(false);
        	lblThird.setVisible(false);
        	lblFourth.setVisible(false);
        	lblPassword.setVisible(false);
        	pfPassword.setVisible(false);
        	lblFirst.setText("0");
        	lblSecond.setText("0");
        	lblThird.setText("0");
        	lblFourth.setText("0");
        	pfPassword.setText("a");
        	lblConfPassword.setVisible(false);
        	pfConfPassword.setVisible(false);
        	pfConfPassword.setText("a");
        	so = ControllerLogin.getSocket();
        	inputStream = so.getInputStream();
    		outputStream = so.getOutputStream();
    		System.out.println("conn == true");
        	connected = true;
    	}
    	LocalDate dataCorrente = LocalDate.now();
    	final int annoAttuale = dataCorrente.getYear();
        final int meseAttuale = dataCorrente.getMonthValue();
        final int giornoAttuale = dataCorrente.getDayOfMonth();
    	btnAnni = new MenuItem[anni];
    	int a = 0, d = 0;
    	for(a = annoAttuale; d < anni; d++) {
    		btnAnni[d] = new MenuItem(a + "");
    		final int k = a;
    		btnAnni[d].setOnAction(e -> {
    			btnMese.setText("Mese");
    			btnMese.getItems().clear();
    			btnGiorno.setText("Giorno");
    			btnGiorno.getItems().clear();
    			btnAnno.setText(btnAnni[annoAttuale - k].getText());
    			btnMesi = new MenuItem[mesi];
    			int b = 0;
    	    	for(b = 0; b < mesi; b++) {
    	    		if(b < 9)
    	    			btnMesi[b] = new MenuItem("0" + (b + 1) + "");
    	    		else
    	    			btnMesi[b] = new MenuItem((b + 1) + "");
    	    		
    	    		final int l = b;
    	    		btnMesi[b].setOnAction(f -> {
    	    			btnGiorno.setText("Giorno");
    	    			btnGiorno.getItems().clear();
    	    			btnMese.setText(btnMesi[l].getText());
    	    			switch(btnMesi[l].getText()) {
    					case "1":
    						giorni = 31;
    						break;
    					case "2":
    						giorni = 28;
    						if(Integer.parseInt(btnAnno.getText()) % 4 == 0)
    							if(Integer.parseInt(btnAnno.getText()) % 100 == 0)
    								if(Integer.parseInt(btnAnno.getText()) % 400 == 0)
    									giorni = 29;
    						break;
    					case "3":
    						giorni = 31;
    						break;
    					case "4":
    						giorni = 30;
    						break;
    					case "5":
    						giorni = 31;
    						break;
    					case "6":
    						giorni = 30;
    						break;
    					case "7":
    						giorni = 31;
    						break;
    					case "8":
    						giorni = 31;
    						break;
    					case "9":
    						giorni = 30;
    						break;
    					case "10":
    						giorni = 31;
    						break;
    					case "11":
    						giorni = 30;
    						break;
    					case "12":
    						giorni = 31;
    						break;
    	    			}
    	    			btnGiorni = new MenuItem[giorni];
    	    			int c = 0;
    	    	    	for(c = 0; c < giorni; c++) {
    	    	    		if(c < 9)
    	    	    			btnGiorni[c] = new MenuItem("0" + (c + 1) + "");
    	    	    		else
    	    	    			btnGiorni[c] = new MenuItem((c + 1) + "");
    	    	    		final int m = c;
    	    	    		btnGiorni[c].setOnAction(g -> {
    	    	    			btnGiorno.setText(btnGiorni[m].getText());
    	    	    		});
    	    	    		btnGiorno.getItems().add(btnGiorni[c]);
    	    	    	}	
    	              });
    	    		btnMese.getItems().add(btnMesi[b]);
    	    	}
    		});
    		btnAnno.getItems().add(btnAnni[d]);
    		a--;
    	}
		miMaschio.setOnAction(f -> {
			mbSesso.setText("M");
		});
		miFemmina.setOnAction(f -> {
			mbSesso.setText("F");
		});
    }
}
