package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.User;


public class ClientLocal extends Application {
	
	private static Socket so;
	private OutputStream outputStream;
	private InputStream inputStream;
    public final static int SOCKET_PORT = 50000;
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	if(!connection("127.0.0.1"))
    		return;
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		outputStream.write("attive".getBytes(), 0, "attive".length());
        letti = inputStream.read(buffer);
        risposta = new String(buffer, 0, letti);
        if(risposta.equals("no")) {
        	Alert alert = new Alert(AlertType.WARNING, "Non ci sono votazioni attive al momento", ButtonType.CLOSE);
    		alert.show();
    		so.close();
        } else {
			Parent root = FXMLLoader.load(getClass().getResource("sceltaVotazione.fxml"));
			primaryStage.setTitle("Scegli");
	        primaryStage.setScene(new Scene(root, 400, 500));
	        primaryStage.show();
        }
    }
    public boolean connection (String address) throws IOException{
    	try {
			so = new Socket(address, SOCKET_PORT);
			outputStream = so.getOutputStream();
	        inputStream = so.getInputStream();
			System.out.println("Client connesso, Indirizzo: " + so.getInetAddress() + "; porta: "+ so.getPort());
			return true;
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.WARNING, "Il server non è online", ButtonType.CLOSE);
    		alert.show();
    		return false;
		}
    }
    
    public static Socket getSocket() {
    	return so;
    }

    public static void main(String[] args) {
    	launch(args);
    }
}