package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
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
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

public class ControllerVO{

	Socket so;
    InputStream in;
    OutputStream out;
    
    @FXML
    private ToggleGroup group;
    
    @FXML
    private VBox vboxPa;
        
    @FXML
    private RadioButton partito;
    
    @FXML
    private RadioButton candidato;

    @FXML
    private Button btnConferma;
    
    @FXML
    private Button btnPulisci;
    
    @FXML
    private Label lblNome;
    
    @FXML
    private MenuButton btnVoto[];

    @FXML
    private MenuItem btnNumero[];
    
    @FXML
    private MenuItem mip[];
    
    @FXML
    private MenuItem mic[];
    
    private int count = 1;
    
    List<Partito> listp;
    List<Candidato> listc;
    int voti = 0;
    private PublicKey pubKey;
    
    @FXML
    void handlePulisci(ActionEvent event) {
    	count = 1;
    	if(mip != null) {
    		for(int i = 0; i < listp.size(); i++) {
    			mip[i] = null;
    			btnVoto[i].setText("Voto");
    			btnNumero[i].setText(count + "");
    		}
    	}
    	if(mic != null) {
    		for(int i = 0; i < mic.length; i++) {
    			mic[i] = null;
    			btnVoto[i].setText("Voto");
    			btnNumero[i].setText(count + "");
    		}
    	}
    }
    
    @FXML
    void handleConferma(ActionEvent event) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
    	RadioButton rb = (RadioButton)group.getSelectedToggle();
    	String s = "", s1 = "";
    	if (rb != null) {
    		if(rb.getId().equals("p")){
	    		int count = 1;
	    		int i;
	    		int j = 0;
	    		for(i = 0; i < mip.length && count <= voti; i++) {
	    			if(mip[i] != null) {
	    				if(btnNumero[i].getText().equals(count + "")) {
	    					if(j == 0) {
	    						s = "p," + listp.get(i).getId() + "@" + listp.get(i).getNome();
	    						s1 = listp.get(i).getId() + " " + listp.get(i).getNome() + " " + count + " ";
	    						j++;
	    					}else {
	    						s += "," + listp.get(i).getId() + "@" + listp.get(i).getNome();
	    						s1 += ", " + listp.get(i).getId() + " " + listp.get(i).getNome() + " " + count + " ";
	    					}
	    					count++;
	    					i = -1;
	    				}
	    			}
	    		}
    		}else{
        		int count = 1;
        		int i;
        		int j = 0;
        		for(i = 0; i < mic.length && count <= voti; i++) {
        			if(mic[i] != null) {
        				if(btnNumero[i].getText().equals(count + "")) {
        					if(j == 0) {
	    						s = "c," + listc.get(i).getId() + "@" +  listc.get(i).getNome();
	    						s1 = listc.get(i).getId() + " " + listc.get(i).getNome() + " " + count + " ";
	    						j++;
        					}else {
        						s += "," + listc.get(i).getId() + "@" + listc.get(i).getNome();
        						s1 += ", " + listc.get(i).getId() + " " + listc.get(i).getNome() + " " + count + " ";
        					}
        					count++;
        					i = -1;
        				}
        			}
        		}
    		}
    	}
    	final String a = s + ",";
    	byte buffer[] = new byte[100];
    	//byte[] cipherData = null;
    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    	out.write("vo".getBytes(), 0, "vo".length());
    	String codfis = "";
    	if(ControllerCL.getCodiceFiscale() != null)
    		codfis = ControllerCL.getCodiceFiscale();
    	else 
    		codfis = ControllerLogin.getUser().getCodiceFiscale();
    	final String cf = codfis;
        if (rb != null) {
    		Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare "  + s1 + "?");
			alert.showAndWait().ifPresent(response -> {
			     if (response == ButtonType.OK) {
			        try {			        	
			        	byte[] cipherData = cipher.doFinal((ControllerAttive.getScelta() + "," + cf + "," + a).getBytes());
				        DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
						int mes = in.read(buffer);
						String r = new String(buffer, 0, mes);
						if(r.equals("ok"))
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
        } else if(voti == 0) {
        	Alert alert = new Alert(AlertType.CONFIRMATION, "Confermi di votare Scheda bianca?");
			alert.showAndWait().ifPresent(response -> {
			     if (response == ButtonType.OK) {
			        try {
			        	byte[] cipherData = cipher.doFinal((ControllerAttive.getScelta() + ",-1@schede bianche,").getBytes());
				        DataOutputStream dos = new DataOutputStream(out);
						dos.writeInt(cipherData.length);
						dos.write(cipherData, 0, cipherData.length);
						int mes = in.read(buffer);
						String r = new String(buffer, 0, mes);
						if(r.equals("ok"))
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
    		so = ControllerCL.getSocket();
    	else
    		so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	String nome = (String) oin.readObject();
		lblNome.setText(nome);
    	ObjectInputStream keystream = new ObjectInputStream(in);
    	pubKey = (PublicKey) keystream.readObject();
    	
    	//out.write("partiti".getBytes(), 0, "partiti".length());
    	ObjectInputStream oin1 = new ObjectInputStream(in);
        listp = (List<Partito>) oin1.readObject();
        int sizep = listp.size();
        listc = new ArrayList<Candidato>();
        for(int i = 0; i < listp.size(); i++) {
        	for(int j = 0; j < listp.get(i).getCandidati().size(); j++) {
            	listc.add(listp.get(i).getCandidati().get(j));
            }
        }
        int sizec = listc.size();
    	group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton)group.getSelectedToggle();
                if (rb != null) {
                	if(rb.getId().equals("p")){
                		count = 1;
                		voti = 0;
                		btnVoto = new MenuButton[sizep];
                    	List<HBox> righe = new ArrayList<>();
                    	btnNumero = new MenuItem[sizep];
                    	for(int i = 0; i < sizep; i++) {
                    		btnNumero[i] = new MenuItem(count + "");
                    		btnVoto[i] = new MenuButton("Voto", null, btnNumero[i]);
                    		btnVoto[i].setPadding(new Insets(10));
                    		btnVoto[i].setId(listp.get(i).getId() + "");
                    		Label l = new Label(listp.get(i).getNome());
                    		l.setWrapText(true);
                    		l.setPadding(new Insets(10));
                    		righe.add(new HBox(btnVoto[i], l));
                    	}
                    	vboxPa.getChildren().clear();
                    	vboxPa.getChildren().addAll(righe);
                    	
                    	mip = new MenuItem[sizep];
                    	for(int i = 0; i < listp.size(); i++) {
                    		mip[i] = null;
                    	}
                    	for(int i = 0; i < sizep; i++) {
                    		final int k = i;
                    		btnNumero[i].setOnAction(e -> {
                    			voti++;
                    			mip[k] = btnNumero[k];
                    			btnVoto[k].setText(count + "");
                    			count++;
                    			for(int j = 0; j < listp.size(); j++) {
                    				if(mip[j] == null)
                    					btnNumero[j].setText(count + "");
                    			}
                              });
                    	}
                	}else{
                		count = 1;
                		voti = 0;
                		btnVoto = new MenuButton[sizec];
                		List<HBox> righe = new ArrayList<>();
                		btnNumero = new MenuItem[sizec];
                    	for(int i = 0; i < sizec; i++) {
                    		btnNumero[i] = new MenuItem(count + "");
                    		btnVoto[i] = new MenuButton("Voto", null, btnNumero[i]);
                    		btnVoto[i].setPadding(new Insets(10));
                    		btnVoto[i].setId(listc.get(i).getId() + "");
                    		Label l = new Label(listc.get(i).getNome());
                    		l.setWrapText(true);
                    		l.setPadding(new Insets(10));
                    		righe.add(new HBox(btnVoto[i], l));
                    	}
                    	vboxPa.getChildren().clear();
                    	vboxPa.getChildren().addAll(righe);
                    	
                    	mic = new MenuItem[sizec];
                    	for(int i = 0; i < sizec; i++) {
                    		mic[i] = null;
                    	}
                    	for(int i = 0; i < sizec; i++) {
                    		final int k = i;
                    		btnNumero[i].setOnAction(e -> {
                    			voti++;
                    			mic[k] = btnNumero[k];
                    			btnVoto[k].setText(count + "");
                    			count++;
                    			for(int j = 0; j < sizec; j++) {
                    				if(mic[j] == null)
                    					btnNumero[j].setText(count + "");
                    			}
                              });
                    	}
                	}
                }
            }
        });
    }
}
