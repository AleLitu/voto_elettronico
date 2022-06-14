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
    
    @FXML
    private TextField lblFirst;
	
    @FXML
    private TextField lblSecond;
    
    @FXML
    private TextField lblThird;
    
    @FXML
    private TextField lblFourth;
	
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
    
    public int isInteger(String input) {
        try {
            int n;
            n=Integer.parseInt(input);
            return n;
        } catch (Exception e) {
            return -1;
        }
    }
    
    protected boolean checkIntType() throws Exception {
        String n1=lblFirst.getText();
        String n2=lblSecond.getText();
        String n3=lblThird.getText();
        String n4=lblFourth.getText();
        System.out.println(n1 + " " + n2 + " " + n3 + " " + n4);
        int n1_int=isInteger(n1);
        int n2_int=isInteger(n2);
        int n3_int=isInteger(n3);
        int n4_int=isInteger(n4);
        if(((n1_int<0))||(n1_int>255)||((n2_int<0))||(n2_int>255)||((n3_int<0))||(n3_int>255)||((n4_int<0))||(n4_int>255)){
        	Alert alert = new Alert(AlertType.WARNING, "Errore nell'indirizzo", ButtonType.CLOSE);
    		alert.show();
        	return false;
        }else{
            String indirizzo=n1+"."+n2+"."+n3+"."+n4;
            return connection(indirizzo);
        }
    }
    
    @FXML
    void handleSend(ActionEvent event) throws IOException {
    	codiceFiscale = lblCodiceFiscale.getText();
    	lblFirst.setText("127");
    	lblSecond.setText("0");
    	lblThird.setText("0");
    	lblFourth.setText("1");
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
						if(!checkIntType()) {
							Alert alert1 = new Alert(AlertType.WARNING, "Connessione non riuscita", ButtonType.CLOSE);
							alert1.show();
						} else {
							if(isOkCF(codiceFiscale)) {
								int dim_buffer = 100;
								int letti;
								String risposta;
								byte buffer[] = new byte[dim_buffer];
								outputStream.write("login".getBytes(), 0, "login".length());
								//outputStream.write("login".getBytes(), 0, "login".length());
								letti = inputStream.read(buffer);
							    risposta = new String(buffer, 0, letti);
				                if(risposta.equals("ok")) {
									outputStream.write((codiceFiscale + ", ").getBytes(), 0, (codiceFiscale + ", ").length());
									letti = inputStream.read(buffer);
								    risposta = new String(buffer, 0, letti);
								    if(risposta.equals("ok")) {
								    	outputStream.write("a".getBytes(), 0, "a".length());
					 				    ObjectInputStream oin = new ObjectInputStream(inputStream);
					 					user = (User) oin.readObject();
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
										    } else if(risposta.equals("no")) {
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
					                	} else {
					                		Alert alert1 = new Alert(AlertType.WARNING, "Errore codice fiscale", ButtonType.CLOSE);
								    		alert1.show();
					                	}
									} else {
										Alert alert1 = new Alert(AlertType.WARNING, "Errore nell'inserimento nel database", ButtonType.CLOSE);
							    		alert1.show();
							    		so.close();
									}	
								}
							} else {
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