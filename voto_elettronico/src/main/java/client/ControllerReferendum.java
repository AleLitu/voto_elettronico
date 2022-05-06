package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ControllerReferendum{

    @FXML
    private Button btnConfermaR;
    
    @FXML
    private Button btnIndietro;
    
    @FXML
    private TextField txtNome;

    @FXML
    private TextArea txtDomanda;

    @FXML
    void handleConfermaR(ActionEvent event) throws IOException {
		int dim_buffer = 100;
		int letti;
		String ok;
		byte buffer[] = new byte[dim_buffer];
    	Socket so = ControllerLogin.getSocket();
        OutputStream outputStream = so.getOutputStream();
        InputStream inputStream = so.getInputStream();
        outputStream.write("a".getBytes(), 0, "a".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);

		if(ok.equals("ok")) {
	        String testo = txtDomanda.getText();
	        String nome = txtNome.getText();
	        if(testo.equals("") || nome.equals("")) {
		    	outputStream.write("no".getBytes(), 0, "no".length());
		    	Alert alert = new Alert(AlertType.WARNING, "Compilare prima entrambi i campi", ButtonType.CLOSE);
        		alert.show();
	        } else {
	        	testo += "," + nome;
	        	outputStream.write(testo.getBytes(), 0, testo.length());
				//letti = inputStream.read(buffer);
				//ok = new String(buffer, 0, letti);

				//if(ok.equals("ok")) {
					Node node = (Node) event.getSource();
					Stage actual = (Stage) node.getScene().getWindow();
					Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
			        actual.setScene(new Scene(root));
			        actual.setTitle("Gestore");
				/*} else {
					System.out.println("Errore");
				}*/
	        }
		}
    }

    @FXML
    void handleIndietro(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Gestore");
    }
}
