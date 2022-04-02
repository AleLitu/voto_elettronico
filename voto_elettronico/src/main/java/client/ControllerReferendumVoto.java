package client;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ControllerReferendumVoto{

    @FXML
    private ToggleGroup Voto;

    @FXML
    private Button btnInvia;

    @FXML
    private RadioButton radioNo;

    @FXML
    private RadioButton radioSi;
    
    @FXML
    private Label lblVoto;

    @FXML
    void handleInvia(ActionEvent event) {
   		if(!radioNo.isSelected() && !radioSi.isSelected())
   			lblVoto.setText("Selezionare una scelta");
   		else {
			Socket so = ControllerLogin.getSocket();    	
	    	int dim_buffer = 100;
			int letti, count = 0;
			String ok;
			byte buffer[] = new byte[dim_buffer];
	        OutputStream outputStream = so.getOutputStream();
	        InputStream inputStream = so.getInputStream();
	        outputStream.write("c".getBytes(), 0, "c".length());
	        letti = inputStream.read(buffer);
			ok = new String(buffer, 0, letti);
			if(ok.equals("ok")) {
				if(radioSi.isSelected()) {
	    	    	outputStream.write("si".getBytes(), 0, "si".length());
	    			letti = inputStream.read(buffer);
	    			ok = new String(buffer, 0, letti);
	    		}else {	    		
	    			outputStream.write("no".getBytes(), 0, "no".length());
	    			letti = inputStream.read(buffer);
	    			ok = new String(buffer, 0, letti);
	    		}
			}
			else {
	    		System.out.println("Errore");
	    	}
	    	
			Node node = (Node) event.getSource();
			Stage actual = (Stage) node.getScene().getWindow();
			Parent root = FXMLLoader.load(getClass().getResource("Votato.fxml"));
	        actual.setScene(new Scene(root));
	        actual.setTitle("Votato");
   		}
    }
}
