package server ;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControllerServer {
	
    @FXML
    private Button btnAccendi;

    @FXML
    private Button btnSpegni;

    @FXML
    private Label lblIndirizzo;
	
	ServerSocket sSrv;
	Socket toClient;

    @FXML
    void handleAccendi(ActionEvent event) throws IOException {
    		try {
    			sSrv = new ServerSocket(50000);
    			lblIndirizzo.setText(sSrv.getInetAddress() + "");
    			while(true) {
    				toClient = sSrv.accept();
    				GestisciClient client = new GestisciClient(toClient);
    				Thread t = new Thread(client);
    				t.start();
    			}
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    }

    @FXML
    void handleSpegni(ActionEvent event) {
    	try {
    		sSrv.close();
    	}catch(Exception e) {
			e.printStackTrace();
		}
    }
}