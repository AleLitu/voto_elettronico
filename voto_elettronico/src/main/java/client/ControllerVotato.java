package client;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ControllerVotato {
	
	 @FXML
	    private Label lblVoto;

    @FXML
    private Button btnLogout;

    @FXML
    void handleLogout(ActionEvent event) {
    	Node node = (Node) event.getSource();
    	((Stage) node.getScene().getWindow()).close();
    }
}
