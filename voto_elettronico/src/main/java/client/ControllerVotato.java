package client;

import javafx.scene.Node;

import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ControllerVotato {
	
	@FXML
	private Label lblVoto;

    @FXML
    private Button btnLogout;
    
    @FXML
    private Button btnElenco;

    @FXML
    void handleLogout(ActionEvent event) {
    	Node node = (Node) event.getSource();
    	((Stage) node.getScene().getWindow()).close();
    }
    
    @FXML
    void handleElenco(ActionEvent event) throws IOException {
    	Socket so;
    	OutputStream out;
    	InputStream in;
    	if(ControllerLogin.getSocket() == null)
    		so = ClientLocal.getSocket();
    	else
    		so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		out.write("attive".getBytes(), 0, "attive".length());
        letti = in.read(buffer);
        risposta = new String(buffer, 0, letti);
        if(risposta.equals("no")) {
        	Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni attive al momento", ButtonType.CLOSE);
    		alert.show();
        } else {
        	Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("sceltaVotazione.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Scegli");
        }
    }
}
