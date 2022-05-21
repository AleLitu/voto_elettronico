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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Candidato;
import model.Partito;
import model.Referendum;
import javafx.beans.value.*;

public class ControllerVCP {
	
	Socket so;
    InputStream in;
    OutputStream out;
    
    @FXML
    private VBox vboxPa;
    
    @FXML
    private VBox vboxCa;
    
    @FXML
    private ToggleGroup groupPa;

    @FXML
    private Button btnConferma;
    
    List<Partito> list;
    ArrayList<RadioButton> selected;
    private PublicKey pubKey;

    @FXML
    void handleConferma(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    	RadioButton rb = (RadioButton)groupPa.getSelectedToggle();
    	boolean trovato = false;
        if (rb != null) {
    		out.write("vcp".getBytes(), 0, "vcp".length());
    		String s = ControllerAttive.getScelta() + "," + rb.getId();
    		for(int i = 0; i < selected.size(); i++) {
    			if(selected.get(i).isSelected()) {
    				s += "," + selected.get(i).getId();
    			}
    		}
    		byte[] cipherData = null;
			Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    		cipherData = cipher.doFinal(s.getBytes());
	        DataOutputStream dos = new DataOutputStream(out);
	        dos.writeInt(cipherData.length);
	        dos.write(cipherData, 0, cipherData.length);
    		//out.write(s.getBytes(), 0, s.length());
        } else {
        	//TODO: scheda bianca
        }
        Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Votazione");
    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream keystream = new ObjectInputStream(in);
        pubKey = (PublicKey) keystream.readObject();
        //out.write("partiti".getBytes(), 0, "partiti".length());
    	ObjectInputStream oin = new ObjectInputStream(in);
    	list = (List<Partito>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
    	groupPa = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setToggleGroup(groupPa);
    		rb.setId(list.get(i).getId() + "@" + list.get(i).getNome());
    		rb.setPadding(new Insets(10));
    		Label l = new Label(list.get(i).getNome());
    		l.setWrapText(true);
    		l.setPadding(new Insets(10));
    		righe.add(new HBox(rb, l));
    	}
    	vboxPa.getChildren().clear();
    	vboxPa.getChildren().addAll(righe);
    	
    	groupPa.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton)groupPa.getSelectedToggle();
                if (rb != null) {
                	for(int i = 0; i < list.size(); i++) {
                		if(list.get(i).getId() == Integer.parseInt(rb.getId().split("@")[0])) {
                			ArrayList<Candidato> candidati = list.get(i).getCandidati();
                			selected = new ArrayList<>();
                			List<HBox> righe = new ArrayList<>();
                	    	for(int j = 0; j < candidati.size(); j++) {
                	    		RadioButton rb1 = new RadioButton();
                	    		rb1.setId(candidati.get(j).getId() + "@" + candidati.get(j).getNome());
                	    		rb1.setPadding(new Insets(10));
                	    		selected.add(rb1);
                	    		Label l = new Label(candidati.get(j).getNome());
                	    		l.setWrapText(true);
                	    		l.setPadding(new Insets(10));
                	    		righe.add(new HBox(rb1, l));
                	    	}
                	    	vboxCa.getChildren().clear();
                	    	vboxCa.getChildren().addAll(righe);
                		}
                	}
                }
            }
        });
    }
}
