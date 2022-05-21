package server ;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ControllerServer {
	
    @FXML
    private Button btnAccendi;

    @FXML
    private Button btnSpegni;

    @FXML
    private Label lblIndirizzo;
    
    @FXML
    private ImageView redPng;
    
    @FXML
    private ImageView greenPng;
	
	ServerSocket sSrv;
	Socket toClient;
	Thread t;
	Server server;

    @FXML
    void handleAccendi(ActionEvent event) throws IOException {
    	btnAccendi.setDisable(true);
    	btnSpegni.setDisable(false);
 	   	greenPng.setVisible(true);
 	   	redPng.setVisible(false);
    	t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    void handleSpegni(ActionEvent event) {
    	btnAccendi.setDisable(false);
    	greenPng.setVisible(false);
 	   	redPng.setVisible(true);
    	btnSpegni.setDisable(true);
    	try {
    		server.stop();
    	}catch(Exception e) {
			e.printStackTrace();
		}
    }
    
   @FXML
   void initialize() throws UnknownHostException {
	   greenPng.setVisible(false);
	   btnSpegni.setDisable(true);
	   server = new Server();
	   InetAddress ip = InetAddress.getLocalHost();
	   lblIndirizzo.setText(ip.toString());
   }
}