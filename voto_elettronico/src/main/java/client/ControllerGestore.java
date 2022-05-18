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

import javafx.application.Platform;
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
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

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
    private Button btnScrutinio;

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
		out.write("end".getBytes(), 0, "end".length());
		letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
        if(risposta.equals("no")) {
			Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni attive da chiudere", ButtonType.CLOSE);
    		alert.show();
		} else {
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("termina.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Termina votazioni");
    	}
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
    	out.write("logout".getBytes(), 0, "logout".length());
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Login");
    }

    @FXML
    void handleReferendum(ActionEvent event) throws IOException {
    	Stage actual = (Stage) gestoreBorderPane.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("referendum.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("");
    }
    
    @FXML
    void handleScrutinio(ActionEvent event) throws IOException {
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		out.write("scrutinio".getBytes(), 0, "scrutinio".length());
		letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
        if(risposta.equals("no")) {
			Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni terminate", ButtonType.CLOSE);
    		alert.show();
		} else {
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("scrutinio.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Scrutinio votazioni");
    	}
    }

    @FXML
    void handleRisultati(ActionEvent event) throws IOException {
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		out.write("calculated".getBytes(), 0, "calculated".length());
		letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
        if(risposta.equals("no")) {
			Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni risultati da mostrare", ButtonType.CLOSE);
    		alert.show();
		} else {
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("risultati.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Risultati");
    	}
    }
    
    @FXML
    public void initialize() {
    	lblNome.setText("Benvenuto, " + ControllerLogin.getUser().getUsername());
    }

}
