package client;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControllerGestore {
	
	@FXML
	private BorderPane gestoreBorderPane;

    @FXML
    private Button btnAvvia;

    @FXML
    private MenuButton btnInserimento;

    @FXML
    private MenuItem btnLista;

    @FXML
    private Button btnLogout;

    @FXML
    private MenuItem btnMaggAss;

    @FXML
    private MenuItem btnMaggioranza;

    @FXML
    private MenuItem btnNoQuorum;

    @FXML
    private MenuItem btnOrdinale;

    @FXML
    private MenuItem btnQuorum;

    @FXML
    private MenuItem btnReferendum;

    @FXML
    private Button btnRisultati;
    
    @FXML
    private Button btnTermina;

    @FXML
    private MenuButton btnScrutinio;

    @FXML
    private Label lblNome;
    
    Socket so;
    InputStream in;
    OutputStream out;
    ObjectOutputStream oout;
    ObjectInputStream oin;
    
    public ControllerGestore() {
    	so = ControllerLogin.getSocket();
    	try {
			in = so.getInputStream();
	    	out = so.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    @FXML
    void handleAvvia(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("avvio.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Avvia votazione");
    }
    
    @FXML
    void handleTermina(ActionEvent event) throws Exception {
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		out.write("type".getBytes(), 0, "type".length());
        letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
    	if(risposta.equals("null")) {
    		Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni attive da chiudere", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		out.write("end".getBytes(), 0, "end".length());
    		letti = in.read(buffer);
            String r = new String(buffer, 0, letti);
            if(r.equals("ok")) {
            	if(risposta.equals("Referendum")) {
            		btnMaggioranza.setVisible(false);
            		btnMaggAss.setVisible(false);
            	} else {
            		btnNoQuorum.setVisible(false);
            		btnQuorum.setVisible(false);
            	}
            	return;
            } else {
            	throw new Exception("Errore nella chiusura");
            }
    	}
    }

    @FXML
    void handleCategorico(ActionEvent event) {

    }

    @FXML
    void handleCategoricoPr(ActionEvent event) {

    }

    @FXML
    void handleLista(ActionEvent event) throws IOException {
		Stage actual = (Stage) gestoreBorderPane.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("liste.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("");
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Login");
    }

    @FXML
    void handleMaggAss(ActionEvent event) throws IOException {
    	byte buffer[] = new byte[100];
    	out.write("magg".getBytes(), 0, "magg_ass".length());
    	int letti = in.read(buffer);
    	String risposta = new String(buffer, 0, letti);
    	if(risposta.equals("ok")) {
    		Alert alert = new Alert(AlertType.WARNING, "Calcoli completati correttamente; aprire il file creato per consultare i risultati", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "Errore nel calcolodei risultati", ButtonType.CLOSE);
    		alert.show();
    	}
    }

    @FXML
    void handleMaggioranza(ActionEvent event) throws IOException {
		byte buffer[] = new byte[100];
    	out.write("magg".getBytes(), 0, "magg".length());
    	int letti = in.read(buffer);
    	String risposta = new String(buffer, 0, letti);
    	if(risposta.equals("ok")) {
    		Alert alert = new Alert(AlertType.WARNING, "Calcoli completati correttamente; aprire il file creato per consultare i risultati", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "Errore nel calcolodei risultati", ButtonType.CLOSE);
    		alert.show();
    	}
    }
    
    @FXML
    void btnNoQuorum(ActionEvent event) throws IOException {
    	//byte buffer[] = new byte[100];
    	out.write("noquorum".getBytes(), 0, "noquorum".length());
    	/*int letti = in.read(buffer);
    	String risposta = new String(buffer, 0, letti);
    	if(risposta.equals("ok")) {
    		Alert alert = new Alert(AlertType.WARNING, "Calcoli completati correttamente; aprire il file creato per consultare i risultati", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "Errore nel calcolodei risultati", ButtonType.CLOSE);
    		alert.show();
    	}*/
    	FileOutputStream fos = null;
    	BufferedOutputStream bos = null;
    	try {
	    	File file = new File("ricevuto.txt");
	    	file.createNewFile();
	    	byte[] buffer = new byte[99999999];
	        fos = new FileOutputStream(file);
	        bos = new BufferedOutputStream(fos);
	        int byteread = in.read(buffer, 0, buffer.length);
	        int current = byteread;
	        do{
	            System.out.println("ricevuto");
	
	            byteread = in.read(buffer, 0, buffer.length - current);
	            if (byteread >= 0) current += byteread;
	        } while (byteread > -1);
	        bos.write(buffer, 0, current);
	        bos.flush();
    	} finally {
        //fos.close();
        //bos.close();
    	}
    }
    
    @FXML
    void handleQuorum(ActionEvent event) throws IOException {
    	byte buffer[] = new byte[100];
    	out.write("quorum".getBytes(), 0, "quorum".length());
    	int letti = in.read(buffer);
    	String risposta = new String(buffer, 0, letti);
    	if(risposta.equals("ok")) {
    		Alert alert = new Alert(AlertType.WARNING, "Calcoli completati correttamente; aprire il file creato per consultare i risultati", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "Errore nel calcolodei risultati", ButtonType.CLOSE);
    		alert.show();
    	}
    }

    @FXML
    void handleOrdinale(ActionEvent event) {

    }

    @FXML
    void handleReferendum(ActionEvent event) throws IOException {
    	Stage actual = (Stage) gestoreBorderPane.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("referendum.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("");
    }

    @FXML
    void handleRisultati(ActionEvent event) {

    }
    
    @FXML
    public void initialize() {
    	lblNome.setText("Benvenuto, " + ControllerLogin.getUser().getUsername());
    }

}
