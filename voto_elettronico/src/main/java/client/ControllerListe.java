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
    void handleAggiungi(ActionEvent event) {    
    	Socket so = ControllerLogin.getSocket();
    	String[] c = txtCandidati.getText().split(", ");
    	
    	//Connessione al database
    	String url = "jdbc:mysql://localhost:3306/votazioni?";
    	String usr = "root";
    	String pwd = "";
    	int id, count = 0;
    	try {
    		Connection conn = DriverManager.getConnection(url, usr, pwd);
    		
    		//Query per inserire il partito
    		PreparedStatement stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE partito = ?");
    		stmt.setString(1, txtPartito.getText());
    		ResultSet rs = stmt.executeQuery();
    		if(rs.next()) {
    			System.out.println("1");
    			//rs.next();
    			id = rs.getInt("idPartito");
    			System.out.println(id);
    		} else {
    			System.out.println("2");
    			stmt = conn.prepareStatement("INSERT INTO Partiti (partito) VALUES (?)");
        		stmt.setString(1, txtPartito.getText());
        		stmt.execute();
        		
        		//Query per prendere l'id del partito appena inserito
        		stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE partito = ?");
        		stmt.setString(1, txtPartito.getText());
        		rs = stmt.executeQuery();
        		rs.next();
        		id = rs.getInt("idPartito");
    		}
        		//Ciclo con query per inserire i vari candidati di quel partito
        		for(int i = 0; i < c.length; i++) {
        			stmt = conn.prepareStatement("SELECT idCandidato FROM candidati WHERE candidato = ?");
            		stmt.setString(1, c[i]);
            		rs = stmt.executeQuery();
            		if(!rs.next()) {
            			stmt = conn.prepareStatement("INSERT INTO Candidati (candidato, idPartito) VALUES (?, ?)");
                		stmt.setString(1, c[i]);
                		stmt.setInt(2, id);
                		stmt.execute();
            		} else {
            			count++;
            		}
        		}    		
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
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
