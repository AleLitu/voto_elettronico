package client;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Referendum;
import model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
    private Label lblReferendum;
    
    private Socket so;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ObjectInputStream ois;
    private Referendum re;
    
    @FXML
    void handleInvia(ActionEvent event) throws IOException {    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        outputStream.write("c".getBytes(), 0, "c".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);
		if(ok.equals("ok")) {
			outputStream.write(String.valueOf(ControllerLogin.getUser().getId()).getBytes(), 0, String.valueOf(ControllerLogin.getUser().getId()).length());
			if(ok.equals("ok")) {
				if(!radioNo.isSelected() && !radioSi.isSelected()) {
		   			outputStream.write("sb".getBytes(), 0, "sb".length());
		   		}else{
					if(radioSi.isSelected())
			    	   	outputStream.write("si".getBytes(), 0, "si".length());
			    	else
			    		outputStream.write("no".getBytes(), 0, "no".length());
			    	}
			}else{
			   		System.out.println("Errore");
			}
		}else{
	   		System.out.println("Errore");
		}
		Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Votazione");
    }
    
    @FXML
    private void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
        outputStream = so.getOutputStream();
        inputStream = so.getInputStream();
        outputStream.write("domanda".getBytes(), 0, "domanda".length());
        ois = new ObjectInputStream(inputStream);
    	re = (Referendum) ois.readObject();
    	lblReferendum.setText(re.getTesto());
    	
    }
}