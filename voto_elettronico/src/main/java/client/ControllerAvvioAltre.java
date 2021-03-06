package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class ControllerAvvioAltre {

    @FXML
    private Button btnConferma;

    @FXML
    private Button btnIndietro;
    
    @FXML
    private Button btnTermina;

    @FXML
    private VBox vboxCa;

    @FXML
    private VBox vboxPa;
    
    @FXML
    private ToggleGroup groupPa;
    
    Socket so;
    InputStream in;
    OutputStream out;
    
    ArrayList<Partito> list;
    ArrayList<RadioButton> selected;
    

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	RadioButton rb = (RadioButton)groupPa.getSelectedToggle();
    	boolean trovato = false;
        if (rb != null) {
    		String s = ControllerAvvio.getNome_votazione() + "," + ControllerAvvio.getTipo_votazione() + "," + rb.getId();
    		for(int i = 0; i < selected.size(); i++) {
    			if(selected.get(i).isSelected()) {
    				s += "," + selected.get(i).getId();
    				trovato = true;
    			}
    		}
    		if(trovato) {
    			out.write(s.getBytes(), 0, s.length());
    			Alert alert = new Alert(AlertType.NONE, "Inserimento avvenuto con successo", ButtonType.CLOSE);
        		alert.show();
    		}
    		else {
    			Alert alert = new Alert(AlertType.WARNING, "Seleziona i candidati del partito", ButtonType.CLOSE);
        		alert.show();
    		}
        } else {
        	Alert alert = new Alert(AlertType.WARNING, "Seleziona prima un partito e poi i candidati", ButtonType.CLOSE);
    		alert.show();
        }
    }
    
    @FXML
    void handleIndietro(ActionEvent event) throws IOException {
		out.write("esc".getBytes(), 0, "esc".length());
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("avvio.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Avvio votazione");
    }
    
    @FXML
    void handleTermina(ActionEvent event) throws IOException {
		out.write("esc".getBytes(), 0, "esc".length());
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Home gestore");
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	list = (ArrayList<Partito>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
    	groupPa = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setToggleGroup(groupPa);
    		rb.setId(Integer.toString(list.get(i).getId()));
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
                		if(list.get(i).getId() == Integer.parseInt(rb.getId())) {
                			ArrayList<Candidato> candidati = list.get(i).getCandidati();
                			selected = new ArrayList<>();
                			List<HBox> righe = new ArrayList<>();
                	    	for(int j = 0; j < candidati.size(); j++) {
                	    		RadioButton rb1 = new RadioButton();
                	    		rb1.setId(Integer.toString(candidati.get(j).getId()));
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
