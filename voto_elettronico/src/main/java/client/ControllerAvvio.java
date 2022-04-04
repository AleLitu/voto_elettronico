package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class ControllerAvvio {

    @FXML
    private Button btnConferma;

    @FXML
    private RadioButton radioCategorico;

    @FXML
    private RadioButton radioCategoricoP;

    @FXML
    private RadioButton radioOrdinale;

    @FXML
    private RadioButton radioRef;

    @FXML
    private ToggleGroup votazione;

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	//TODO: controlli se dati inseriti in database
    	Socket so = ControllerLogin.getSocket();    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        OutputStream outputStream = so.getOutputStream();
        InputStream inputStream = so.getInputStream();
        
    	if(radioCategorico.isSelected()) {
    		outputStream.write("d".getBytes(), 0, "d".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("vc".getBytes(), 0, "vc".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(ok.equals("ok")) {
        			cambia(event);
        		}
    		}
    	} else if(radioCategoricoP.isSelected()){
    		outputStream.write("d".getBytes(), 0, "d".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("vcp".getBytes(), 0, "vcp".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(ok.equals("ok")) {
        			cambia(event);
        		}
    		}
    	} else if(radioOrdinale.isSelected()) {
    		outputStream.write("d".getBytes(), 0, "d".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("vo".getBytes(), 0, "vo".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(ok.equals("ok")) {
        			cambia(event);
        		}
    		}
    	} else if(radioRef.isSelected()) {
    		outputStream.write("d".getBytes(), 0, "d".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("ref".getBytes(), 0, "ref".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(ok.equals("ok")) {
        			cambia(event);
        		}
    		}
    	} else {
    		return;
    	}
    }
    
    public void cambia(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Gestore");
    }

}
