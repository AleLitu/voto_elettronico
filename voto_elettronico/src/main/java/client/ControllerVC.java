package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Candidato;
import model.Partito;
import model.Referendum;
import javafx.beans.value.*;

public class ControllerVC {
	
	Socket so;
    InputStream in;
    OutputStream out;
    
    @FXML
    private Label lblNome;
    
    @FXML
    private VBox vboxPa;
    
    @FXML
    private VBox vboxCa;
    
    @FXML
    private ToggleGroup groupPa;

    @FXML
    private Button btnConferma;
    
    List<Partito> list;
    private PublicKey pubKey;

    @FXML
    void handleConferma(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    	RadioButton rb = (RadioButton)groupPa.getSelectedToggle();
    	byte buffer[] = new byte[100];
    	//byte[] cipherData = null;
    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    	out.write("vc".getBytes(), 0, "vc".length());
        if (rb != null) {
    		Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare "  + rb.getText() + "?");
			alert.showAndWait().ifPresent(response -> {
			     if (response == ButtonType.OK) {
			        try {
			        	byte[] cipherData = cipher.doFinal((ControllerAttive.getScelta() + "," + rb.getId()).getBytes());
				        DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
						int mes = in.read(buffer);
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
						DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
				    	return;
					} catch (Exception e) {
						new Alert(AlertType.ERROR, "Errore", ButtonType.CLOSE).show();
						return;
					}
			     }
			 });
    		//out.write((ControllerAttive.getScelta() + "," + rb.getId()).getBytes(), 0, (ControllerAttive.getScelta() + "," + rb.getId()).length());
        } else {
        	Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare Scheda bianca?");
			alert.showAndWait().ifPresent(response -> {
			     if (response == ButtonType.OK) {
			        try {
			        	byte[] cipherData = cipher.doFinal((ControllerAttive.getScelta() + ",-1@schede bianche,").getBytes());
				        DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
						int mes = in.read(buffer);
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
						DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
				    	return;
					} catch (Exception e) {
						new Alert(AlertType.ERROR, "Errore", ButtonType.CLOSE).show();
						return;
					}
			     }
			 });
        }
    }
    
    private void votato(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Votazione");
    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	if(ControllerLogin.getSocket() == null)
    		so = ClientLocal.getSocket();
    	else
    		so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	String nome = (String) oin.readObject();
    	lblNome.setText(nome);
    	ObjectInputStream keystream = new ObjectInputStream(in);
        pubKey = (PublicKey) keystream.readObject();
        //out.write("attive".getBytes(), 0, "attive".length());
    	ObjectInputStream oin1 = new ObjectInputStream(in);
    	list = (List<Partito>) oin1.readObject();
    	//List<HBox> righe = new ArrayList<>();
    	groupPa = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setToggleGroup(groupPa);
    		rb.setId(list.get(i).getId() + "@" + list.get(i).getNome());
    		rb.setPadding(new Insets(10));
    		rb.setText(list.get(i).getNome());
    		/*
    		Label l = new Label(list.get(i).getNome());
    		l.setWrapText(true);
    		l.setPadding(new Insets(10));*/
    		vboxPa.getChildren().addAll(rb);
    		//righe.add(new HBox(rb, l));
    	}
    	//vboxPa.getChildren().clear();
    	//vboxPa.getChildren().addAll(righe);
    	
    	groupPa.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton)groupPa.getSelectedToggle();
                if (rb != null) {
                	for(int i = 0; i < list.size(); i++) {
                		if(list.get(i).getId() == Integer.parseInt(rb.getId().split("@")[0])) {
                			ArrayList<Candidato> candidati = list.get(i).getCandidati();
                			List<HBox> righe = new ArrayList<>();
                			vboxCa.getChildren().clear();
                	    	for(int j = 0; j < candidati.size(); j++) {
                	    		/*RadioButton rb1 = new RadioButton();
                	    		rb1.setToggleGroup(groupPa);
                	    		rb1.setId(Integer.toString(candidati.get(j).getId()));
                	    		rb1.setPadding(new Insets(10));*/
                	    		Label l = new Label(candidati.get(j).getNome());
                	    		l.setWrapText(true);
                	    		l.setPadding(new Insets(10));
                	    		vboxCa.getChildren().addAll(l);
                	    		//righe.add(new HBox(rb1, l));
                	    	}
                	    	//vboxCa.getChildren().clear();
                	    	//vboxCa.getChildren().addAll(righe);
                		}
                	}
                }
            }
        });
    }
}
