import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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
    void handleAggiungi(ActionEvent event) {
    	//Prove di output
    	System.out.println("Partito: " + txtPartito.getText() + " e candidati : " + txtCandidati.getText());
    	
    	String[] c = txtCandidati.getText().split(",");
    	
    	//Connessione al server
    	String url = "jdbc:mysql://localhost:3306/votazioni?";
    	String usr = "root";
    	String pwd = "";
    	try {
    		Connection conn = DriverManager.getConnection(url, usr, pwd);
    		
    		//Query per inserire il partito
    		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Partiti (partito) VALUES (?)");
    		stmt.setString(1, txtPartito.getText());
    		stmt.execute();
    		
    		//Query per prendere l'id del partito appena inserito
    		stmt = conn.prepareStatement("SELECT idPartito FROM Partiti WHERE partito = ?");
    		stmt.setString(1, txtPartito.getText());
    		ResultSet rs = stmt.executeQuery();
    		rs.next();
    		int id = rs.getInt("idPartito");
    		
    		//Ciclo con query per inserire i vari candidati di quel partito
    		for(int i = 0; i < c.length; i++) {
    			stmt = conn.prepareStatement("INSERT INTO Candidati (candidato, idPartito) VALUES (?, ?)");
        		stmt.setString(1, c[i]);
        		stmt.setInt(2, id);
        		stmt.execute();
    		}
    		
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	//Testo vuoto per un nuovo inserimento
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
