package client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ControllerListe{

    @FXML
    private Button btnAggiungi;

    @FXML
    private Button btnTermina;

    @FXML
    private TextArea txtCandidati;

    @FXML
    private TextField txtPartito;
    
    @FXML
    private Label lblDuplicati;
    
    @FXML
    void handleAggiungi(ActionEvent event) throws IOException {    
    	Socket so = ControllerLogin.getSocket();    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        OutputStream outputStream = so.getOutputStream();
        InputStream inputStream = so.getInputStream();
        outputStream.write("b".getBytes(), 0, "b".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);

		if(ok.equals("ok")) {
	        String partito = txtPartito.getText();
	    	outputStream.write(partito.getBytes(), 0, partito.length());
			letti = inputStream.read(buffer);
			ok = new String(buffer, 0, letti);
			
			if(ok.equals("ok")) {
				String candidati = txtCandidati.getText();
		    	outputStream.write(candidati.getBytes(), 0, candidati.length());
				letti = inputStream.read(buffer);
				ok = new String(buffer, 0, letti);
				count = Integer.parseInt(ok);
			} else {
				System.out.println("Errore");
			}
		}
    	
    	//Testo vuoto per un nuovo inserimento
    	if(count == 1)
    		lblDuplicati.setText("C'è " + count + " candidato duplicato in questo o altri partiti");
    	else if(count > 1)
    		lblDuplicati.setText("Ci sono " + count + " candidati duplicati in questo o altri partiti");
    	else
    		lblDuplicati.setText("");
    	txtPartito.setText("");
    	txtCandidati.setText("");
    }

    @FXML
    void handleTermina(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Login");
    }
}
