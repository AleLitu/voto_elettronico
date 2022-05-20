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
import javafx.scene.control.Button;
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
        outputStream.write("c".getBytes(), 0, "c".length());
        letti = inputStream.read(buffer);
		ok = new String(buffer, 0, letti);
		if(ok.equals("ok")) {
			outputStream.write(String.valueOf(ControllerLogin.getUser().getId()).getBytes(), 0, String.valueOf(ControllerLogin.getUser().getId()).length());
			if(ok.equals("ok")) {
				byte[] cipherData = null;
				Cipher cipher = Cipher.getInstance("RSA");
		        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				if(!radioNo.isSelected() && !radioSi.isSelected()) {
					cipherData = cipher.doFinal("sb".getBytes());
			        DataOutputStream dos = new DataOutputStream(outputStream);
			        dos.writeInt(cipherData.length);
			        dos.write(cipherData, 0, cipherData.length);
		   		}else{
					if(radioSi.isSelected()) {
				        cipherData = cipher.doFinal("si".getBytes());
				        DataOutputStream dos = new DataOutputStream(outputStream);
				        dos.writeInt(cipherData.length);
				        dos.write(cipherData, 0, cipherData.length);
					} else {
						cipherData = cipher.doFinal("no".getBytes());
				        DataOutputStream dos = new DataOutputStream(outputStream);
				        dos.writeInt(cipherData.length);
				        dos.write(cipherData, 0, cipherData.length);
				    }
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
        ObjectInputStream keystream = new ObjectInputStream(inputStream);
        pubKey = (PublicKey) keystream.readObject();
        ois = new ObjectInputStream(inputStream);
    	re = (Referendum) ois.readObject();
    	lblReferendum.setText(re.getTesto());
    }
}