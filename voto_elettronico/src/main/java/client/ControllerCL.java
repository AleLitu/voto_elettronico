package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.User;
import model.UserDao;
import model.UserDaoImpl;


public class ControllerCL {
	
	private static User user;
	private static Socket so;
	private OutputStream outputStream;
	private InputStream inputStream;
    public final static int SOCKET_PORT = 50000;
    private static String codiceFiscale;
    
    @FXML
    private Button btnSend;

    @FXML
    private TextField lblCodiceFiscale;

    @FXML
    private Label lblMessage;
	
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
    
    public static String getCodiceFiscale() {
    	return codiceFiscale;
    }
    
    public boolean isOkCF(String cf) throws IOException {
    	int dim_buffer = 100;
		int letti;
		String risposta;
		byte buffer[] = new byte[dim_buffer];
		outputStream.write("codiceFiscale".getBytes(), 0, "codiceFiscale".length());
		letti = inputStream.read(buffer);
	    risposta = new String(buffer, 0, letti);
		if(risposta.equals("ok")) {
			outputStream.write(cf.getBytes(), 0, cf.length());
			letti = inputStream.read(buffer);
		    risposta = new String(buffer, 0, letti);
		    if(risposta.equals("err")) {
		    	return false;
		    }else {
		    	return true;
		    }
		}
    	return false;
    }
    
    @FXML
    void handleSend(ActionEvent event) throws IOException {
    	codiceFiscale = lblCodiceFiscale.getText();
    	Alert alert;
    	if(codiceFiscale.equals("")) {
     		alert = new Alert(AlertType.WARNING, "Inserire il codice fiscale", ButtonType.CLOSE);
     		alert.show();
     	}else {
	    	alert = new Alert(AlertType.CONFIRMATION, "Conferma di votare con codice fiscale: "  + codiceFiscale);
	    	alert.showAndWait().ifPresent(response -> {
    	    if (response == ButtonType.OK) {
    	    	{
    	    		try {
						if(!connection("127.0.0.1")) {
							Alert alert1 = new Alert(AlertType.WARNING, "Connessione non riuscita", ButtonType.CLOSE);
							alert1.show();
						}else {
							if(isOkCF(codiceFiscale)) {
								int dim_buffer = 100;
								int letti;
								String risposta;
								byte buffer[] = new byte[dim_buffer];
								outputStream.write("login".getBytes(), 0, "login".length());
								outputStream.write("login".getBytes(), 0, "login".length());
								letti = inputStream.read(buffer);
							    String risposta1 = new String(buffer, 0, letti);
							    System.out.println(risposta1);
				                if(risposta1.equals("ok")) {
									outputStream.write((codiceFiscale + ", ").getBytes(), 0, (codiceFiscale + ", ").length());
									letti = inputStream.read(buffer);
								    risposta = new String(buffer, 0, letti);
					                if(risposta.equals("ok")) {
					                	outputStream.write("a".getBytes(), 0, "a".length());
					 				    ObjectInputStream oin = new ObjectInputStream(inputStream);
					 					user = (User) oin.readObject();
					 					System.out.println(user);
					                	if(user != null) {
											outputStream.write("attive".getBytes(), 0, "attive".length());
											letti = inputStream.read(buffer);
										    risposta = new String(buffer, 0, letti);
										    if(risposta.equals("ok")) {
												outputStream.write(codiceFiscale.getBytes(), 0, codiceFiscale.length());
										        letti = inputStream.read(buffer);
										        risposta = new String(buffer, 0, letti);
										        if(risposta.equals("ok")) {
										        	letti = inputStream.read(buffer);
											        risposta = new String(buffer, 0, letti);
											        if(risposta.equals("no")) {
											        	Alert alert1 = new Alert(AlertType.WARNING, "Non ci sono votazioni attive al momento", ButtonType.CLOSE);
											    		alert1.show();
											    		so.close();
											        } else {
											        	Node node = (Node) event.getSource();
											        	Stage actual = (Stage) node.getScene().getWindow();
														Parent root = FXMLLoader.load(getClass().getResource("sceltaVotazione.fxml"));
												        actual.setScene(new Scene(root));
												        actual.setTitle("Scegli");
											        }
										        }
										    }else if(risposta.equals("no")) {
										     	Alert alert1 = new Alert(AlertType.WARNING, "Non ci sono votazioni attive al momento", ButtonType.CLOSE);
										 		alert1.show();
										 		so.close();
										     } else {
										     	Node node = (Node) event.getSource();
										     	Stage actual = (Stage) node.getScene().getWindow();
												Parent root = FXMLLoader.load(getClass().getResource("sceltaVotazione.fxml"));
												actual.setTitle("Scegli");
												actual.setScene(new Scene(root, 400, 500));
												actual.show();
										     }
					                	}else {
					                		Alert alert1 = new Alert(AlertType.WARNING, "Errore codice fiscale", ButtonType.CLOSE);
								    		alert1.show();
					                	}
									}else {
										Alert alert1 = new Alert(AlertType.WARNING, "Errore nell'inserimento nel database", ButtonType.CLOSE);
							    		alert1.show();
							    		so.close();
									}	
								}
							}else {
					     		Alert alert1 = new Alert(AlertType.WARNING, "Inserire il codice fiscale corretto", ButtonType.CLOSE);
					    		alert1.show();
					     	}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	     	}
    	     }
    	});
     	}
     }
}