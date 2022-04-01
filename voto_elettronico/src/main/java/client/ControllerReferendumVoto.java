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
		Socket so = ControllerLogin.getSocket();    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        OutputStream outputStream = so.getOutputStream();
        InputStream inputStream = so.getInputStream();
        outputStream.write("c".getBytes(), 0, "b".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);
		if(ok.equals("ok")) {
			if(radioSi.isSelected()) {
    	        String voto = "si";
    	    	outputStream.write(voto.getBytes(), 0, partito.length());
    			letti = inputStream.read(buffer);
    			ok = new String(buffer, 0, letti);
    		}else {	    		
    			String voto = "no";
    			outputStream.write(voto.getBytes(), 0, partito.length());
    			letti = inputStream.read(buffer);
    			ok = new String(buffer, 0, letti);
    		}
		}
		else {
    		System.out.println("Errore");
    	}
    	
    	//parte per cambiare pagina
    }
}
