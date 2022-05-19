package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    		for(int i = 0; i < listc.size(); i++) {
    			mic[i] = null;
    			btnVoto[i].setText("Voto");
    			btnNumero[i].setText(count + "");
    		}
    	}
    }
    
    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	RadioButton rb = (RadioButton)group.getSelectedToggle();
        if (rb != null) {
        	if(rb.getId().equals("p")){
        		ArrayList<Partito> votip = new ArrayList<>();
        		int count = 1;
        		int i;
        		for(i = 0; i < mip.length && count <= voti; i++) {
        			if(mip[i] != null) {
        				if(btnNumero[i].getText().equals(count + "")) {
        					votip.add(new Partito(Integer.parseInt(btnVoto[i].getId()), listp.get(i).getNome()));
        					count++;
        					i = -1;
        				}
        			}
        		}        		
        		out.write("vo".getBytes(), 0, "vo".length());
        		byte buffer[];
        		int dim_buffer = 100;
    			buffer = new byte[dim_buffer];
        		int letti = in.read(buffer);
        		String risposta = new String(buffer, 0, letti);
        		if(risposta.equals("ok")) {
        			out.write("partiti".getBytes(), 0, "partiti".length());
        			letti = in.read(buffer);
        			risposta = "";
        			risposta = new String(buffer, 0, letti);
        			if(risposta.equals("ok")) {
        				out = so.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(out);
        				oos.writeObject(votip);
        				//out.write(id.getBytes(), 0, id.length());
        				letti = in.read(buffer);
        				risposta = "";
        				risposta = new String(buffer, 0, letti);
        				if(risposta.equals("ok")) {
        					Node node = (Node) event.getSource();
        					Stage actual = (Stage) node.getScene().getWindow();
        					Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        				    actual.setScene(new Scene(root));
        				    actual.setTitle("Votazione");
        				}
        			}
        		}
        	}else{
        		ArrayList<Candidato> votic = new ArrayList<>();
        		int count = 1;
        		int i;
        		for(i = 0; i < mic.length && count <= voti; i++) {
        			if(mic[i] != null) {
        				if(btnNumero[i].getText().equals(count + "")) {
        					votic.add(new Candidato(Integer.parseInt(btnVoto[i].getId()), listc.get(i).getNome()));
        					count++;
        					i = -1;
        				}
        			}
        		}        		
        		out.write("vo".getBytes(), 0, "vo".length());
        		byte buffer[];
        		int dim_buffer = 100;
    			buffer = new byte[dim_buffer];
        		int letti = in.read(buffer);
        		String risposta = new String(buffer, 0, letti);
        		if(risposta.equals("ok")) {
        			out.write("candidati".getBytes(), 0, "candidati".length());
        			letti = in.read(buffer);
        			risposta = "";
        			risposta = new String(buffer, 0, letti);
        			if(risposta.equals("ok")) {
        				out = so.getOutputStream();
        				ObjectOutputStream oos = new ObjectOutputStream(out);
        				oos.writeObject(votic);
        				//out.write(id.getBytes(), 0, id.length());
        				letti = in.read(buffer);
        				risposta = "";
        				risposta = new String(buffer, 0, letti);
        				if(risposta.equals("ok")) {
        					Node node = (Node) event.getSource();
        					Stage actual = (Stage) node.getScene().getWindow();
        					Parent root = FXMLLoader.load(getClass().getResource("votato.fxml"));
        				    actual.setScene(new Scene(root));
        				    actual.setTitle("Votazione");
        				}
        			}
        		}
        	}
        } else {
        	//TODO: scheda bianca
        }
    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	
    	out.write("partiti".getBytes(), 0, "partiti".length());
    	ObjectInputStream oin = new ObjectInputStream(in);
        listp = (List<Partito>) oin.readObject();
        int sizep = listp.size();
        
        out.write("candidati".getBytes(), 0, "candidati".length());
    	oin = new ObjectInputStream(in);
        listc = (List<Candidato>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
    	int sizec = listc.size();
    	
    	group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n) {
                RadioButton rb = (RadioButton)group.getSelectedToggle();
                if (rb != null) {
                	if(rb.getId().equals("p")){
                		voti = 0;
                		btnVoto = new MenuButton[sizep];
                    	List<HBox> righe = new ArrayList<>();
                    	btnNumero = new MenuItem[sizep];
                    	for(int i = 0; i < listp.size(); i++) {
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
                    	for(int i = 0; i < listp.size(); i++) {
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
                		voti = 0;
                		btnVoto = new MenuButton[sizec];
                		List<HBox> righe = new ArrayList<>();
                		btnNumero = new MenuItem[sizec];
                    	for(int i = 0; i < listc.size(); i++) {
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
                    	for(int i = 0; i < listc.size(); i++) {
                    		mic[i] = null;
                    	}
                    	for(int i = 0; i < listc.size(); i++) {
                    		final int k = i;
                    		btnNumero[i].setOnAction(e -> {
                    			voti++;
                    			mic[k] = btnNumero[k];
                    			btnVoto[k].setText(count + "");
                    			count++;
                    			for(int j = 0; j < listc.size(); j++) {
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
