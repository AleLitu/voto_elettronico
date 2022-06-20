package client;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Referendum;
import model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

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
    private PublicKey pubKey;
    
    @FXML
    void handleInvia(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        outputStream.write("ref".getBytes(), 0, "ref".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);
		if(ok.equals("ok")) {
			String codfis = "";
        	if(ControllerCL.getCodiceFiscale() != null)
        		codfis = ControllerCL.getCodiceFiscale();
        	else 
        		codfis = ControllerLogin.getUser().getCodiceFiscale();
        	final String cf = codfis;
			//outputStream.write(String.valueOf(ControllerLogin.getUser().getId()).getBytes(), 0, String.valueOf(ControllerLogin.getUser().getId()).length());
			//if(ok.equals("ok")) {
				//byte[] cipherData;
				Cipher cipher = Cipher.getInstance("RSA");
		        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				if(!radioNo.isSelected() && !radioSi.isSelected()) {
					Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare Scheda bianca?");
					alert.showAndWait().ifPresent(response -> {
					     if (response == ButtonType.OK) {
					        try {
					        	byte[] cipherData = cipher.doFinal((re.getId() + "," + re.getNome() + "," + cf + ",sb").getBytes());
						        DataOutputStream dos = new DataOutputStream(outputStream);
								dos.writeInt(cipherData.length);
								dos.write(cipherData, 0, cipherData.length);
								int mes = inputStream.read(buffer);
								String s = new String(buffer, 0, mes);
								if(s.equals("ok"))
									votato(event);
								else {
									new Alert(AlertType.ERROR, "Errore nella registrazione del voto, riprovare", ButtonType.CLOSE).show();
									return;
								}
							} catch (Exception e) {
								new Alert(AlertType.ERROR, "Errore nell'invio del voto, riprovare", ButtonType.CLOSE).show();
								return;
							}
					     } else {
							try {
								byte[] cipherData = cipher.doFinal("err".getBytes());
								DataOutputStream dos = new DataOutputStream(outputStream);
								dos.writeInt(cipherData.length);
								dos.write(cipherData, 0, cipherData.length);
						    	return;
							} catch (Exception e) {
								new Alert(AlertType.ERROR, "Errore", ButtonType.CLOSE).show();
								return;
							}
					     }
					 });
		   		}else{
					if(radioSi.isSelected()) {
						Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare Sì?");
						alert.showAndWait().ifPresent(response -> {
						     if (response == ButtonType.OK) {
						        try {
						        	byte[] cipherData = cipher.doFinal((re.getId() + "," + re.getNome() + "," + cf + ",si").getBytes());
							        DataOutputStream dos = new DataOutputStream(outputStream);
									dos.writeInt(cipherData.length);
									dos.write(cipherData, 0, cipherData.length);
									int mes = inputStream.read(buffer);
									String s = new String(buffer, 0, mes);
									if(s.equals("ok"))
										votato(event);
									else {
										radioSi.setSelected(false);
										new Alert(AlertType.ERROR, "Errore nella registrazione del voto, riprovare", ButtonType.CLOSE).show();
										return;
									}
								} catch (Exception e) {
									new Alert(AlertType.WARNING, "Errore nell'invio del voto, riprovare", ButtonType.CLOSE).show();
									return;
								} 
						     } else {
						    	 try {
									byte[] cipherData = cipher.doFinal("err".getBytes());
									DataOutputStream dos = new DataOutputStream(outputStream);
									dos.writeInt(cipherData.length);
									dos.write(cipherData, 0, cipherData.length);
									radioSi.setSelected(false);
							    	return;
								} catch (Exception e) {
									new Alert(AlertType.ERROR, "Errore", ButtonType.CLOSE).show();
									return;
								}
						     }
						 });
					} else {
						Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare No?");
						alert.showAndWait().ifPresent(response -> {
						     if (response == ButtonType.OK) {
						        try {
						        	byte[] cipherData = cipher.doFinal((re.getId() + "," + re.getNome() + "," + cf + ",no").getBytes());
							        DataOutputStream dos = new DataOutputStream(outputStream);
									dos.writeInt(cipherData.length);
									dos.write(cipherData, 0, cipherData.length);
									int mes = inputStream.read(buffer);
									String s = new String(buffer, 0, mes);
									if(s.equals("ok"))
										votato(event);
									else {
										radioNo.setSelected(false);
										new Alert(AlertType.ERROR, "Errore nella registrazione del voto, riprovare", ButtonType.CLOSE).show();
										return;
									}
								} catch (Exception e) {
									new Alert(AlertType.WARNING, "Errore nell'invio del voto, riprovare", ButtonType.CLOSE).show();
									return;
								} 
						     } else {
						    	 try {
									byte[] cipherData = cipher.doFinal("err".getBytes());
									DataOutputStream dos = new DataOutputStream(outputStream);
									dos.writeInt(cipherData.length);
									dos.write(cipherData, 0, cipherData.length);
									radioNo.setSelected(false);
							    	return;
								} catch (Exception e) {
									new Alert(AlertType.ERROR, "Errore", ButtonType.CLOSE).show();
									return;
								}
						     }
						 });
				    }
			    }
			}else{
			   	System.out.println("Errore");
			}
		//}else{
	   		//System.out.println("Errore");
		//}
    }
    
    private void votato(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Votazione");
    }
    
    @FXML
    private void initialize() throws IOException, ClassNotFoundException {
    	if(ControllerLogin.getSocket() == null)
    		so = ControllerCL.getSocket();
    	else
    		so = ControllerLogin.getSocket();
        outputStream = so.getOutputStream();
        inputStream = so.getInputStream();
        ObjectInputStream keystream = new ObjectInputStream(inputStream);
        pubKey = (PublicKey) keystream.readObject();
        System.out.println(pubKey);
        ois = new ObjectInputStream(inputStream);
    	re = (Referendum) ois.readObject();
    	lblReferendum.setText(re.getTesto());
    }
}