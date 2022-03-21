import java.io.IOException;
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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ControllerReferendum{

    @FXML
    private Button btnConfermaR;

    @FXML
    private TextArea txtDomanda;

    @FXML
    void handleConfermaR(ActionEvent event) throws IOException {
    	//Connessione al server
    	String url = "jdbc:mysql://localhost:3306/votazioni?";
    	String usr = "root";
    	String pwd = "";
    	try {
    		Connection conn = DriverManager.getConnection(url, usr, pwd);
    	
	    	//Query per inserire il referendum
	    	PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (testo) VALUES (?);");
	    	stmt.setString(1, txtDomanda.getText());
	    	stmt.execute();
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Login");
    }

}
