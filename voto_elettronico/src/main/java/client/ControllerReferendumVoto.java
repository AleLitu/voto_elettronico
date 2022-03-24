package client;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

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
    void handleInvia(ActionEvent event) {
    	if(!radioNo.isSelected() && !radioSi.isSelected()) {
    		System.out.println("Errore");
    	}
    	
    	//Connessione al server
    	String url = "jdbc:mysql://localhost:3306/votazioni?";
    	String usr = "root";
    	String pwd = "";
    	try {
    		Connection conn = DriverManager.getConnection(url, usr, pwd);
    	
	    	if(radioSi.isSelected()) {
	    		//Query per inserire il si al referendum
	    		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (Voto) (?);");
	    		stmt.setString(1, "Si");
	    		ResultSet rs = stmt.executeQuery();
	    	}else {
	    		//Query per inserire il no al referendum
	    		PreparedStatement stmt = conn.prepareStatement("INSERT INTO Referendum (Voto) (?);");
	    		stmt.setString(1, "No");
	    		ResultSet rs = stmt.executeQuery();
	    	}
    	}catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	//parte per cambiare pagina
    }
}
