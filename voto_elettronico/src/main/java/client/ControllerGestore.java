package client;
import java.io.IOException;
import java.io.InputStream;
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

    @FXML
    void btnNoQuorum(ActionEvent event) {

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
    	Socket so = ControllerLogin.getSocket();
    	InputStream in = so.getInputStream();
    	OutputStream out = so.getOutputStream();
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		out.write("e".getBytes(), 0, "e".length());
        letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
    	if(risposta.equals("null")) {
    		Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni attive da chiudere", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		out.write("end".getBytes(), 0, "end".length());
    		letti = in.read(buffer);
            risposta = new String(buffer, 0, letti);
            if(risposta.equals("ok")) {
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
    void handleMaggAss(ActionEvent event) {

    }

    @FXML
    void handleMaggioranza(ActionEvent event) {

    }

    @FXML
    void handleOrdinale(ActionEvent event) {

    }

    @FXML
    void handleQuorum(ActionEvent event) {

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
