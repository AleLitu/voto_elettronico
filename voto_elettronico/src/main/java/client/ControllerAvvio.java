package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Candidato;
import model.Partito;

public class ControllerAvvio {

    @FXML
    private Button btnConferma;
    
    @FXML
    private Button btnIndietro;

    @FXML
    private RadioButton radioCategorico;

    @FXML
    private RadioButton radioCategoricoP;

    @FXML
    private RadioButton radioOrdinale;

    @FXML
    private RadioButton radioRef;

    @FXML
    private ToggleGroup votazione;
    
    private static String nome_votazione;
    private static String tipo_votazione;

	@FXML
    void handleConferma(ActionEvent event) throws IOException {
    	Socket so = ControllerLogin.getSocket();    	
    	int dim_buffer = 100;
		int letti, count = 0;
		String ok;
		byte buffer[] = new byte[dim_buffer];
        OutputStream outputStream = so.getOutputStream();
        InputStream inputStream = so.getInputStream();
        
    	if(radioCategorico.isSelected()) {
    		outputStream.write("avvio".getBytes(), 0, "avvio".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("Voto categorico".getBytes(), 0, "Voto categorico".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(!ok.equals("no")) {
        			TextInputDialog dialog = new TextInputDialog();
        			dialog.setTitle("Nome Votazione");
        			dialog.setHeaderText("Inserisci il nome della votazione");
        			//dialog.setContentText("Please enter your name:");

        			Optional<String> result = dialog.showAndWait();
        			if (result.isPresent() && !result.get().equals("")){
        	    		outputStream.write("ok".getBytes(), 0, "ok".length());
        				setNome_votazione(result.get());
        				setTipo_votazione("categorico");
        				Node node = (Node) event.getSource();
            			Stage actual = (Stage) node.getScene().getWindow();
            			Parent root = FXMLLoader.load(getClass().getResource("avvioAltre.fxml"));
            	        actual.setScene(new Scene(root));
            	        actual.setTitle("Avvio Voto Categorico");
        			} else {
        	    		outputStream.write("no".getBytes(), 0, "no".length());
        				Alert alert = new Alert(AlertType.WARNING, "Nome votazione necessario", ButtonType.CLOSE);
                		alert.show();
        			}
        		} else {
        			Alert alert = new Alert(AlertType.WARNING, "Non ci sono partiti e/o candidati", ButtonType.CLOSE);
            		alert.show();
            	}
    		}
    	} else if(radioCategoricoP.isSelected()){
    		outputStream.write("avvio".getBytes(), 0, "avvio".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("Voto categorico con preferenze".getBytes(), 0, "Voto categorico con preferenze".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(!ok.equals("no")) {
        			TextInputDialog dialog = new TextInputDialog();
        			dialog.setTitle("Nome Votazione");
        			dialog.setHeaderText("Inserisci il nome della votazione");
        			//dialog.setContentText("Please enter your name:");

        			Optional<String> result = dialog.showAndWait();
        			if (result.isPresent() && !result.get().equals("")){
        	    		outputStream.write("ok".getBytes(), 0, "ok".length());
        				setNome_votazione(result.get());
        				setTipo_votazione("categorico_preferenze");
        				Node node = (Node) event.getSource();
            			Stage actual = (Stage) node.getScene().getWindow();
            			Parent root = FXMLLoader.load(getClass().getResource("avvioAltre.fxml"));
            	        actual.setScene(new Scene(root));
            	        actual.setTitle("Avvio Voto Categorico con Preferenze");
        			} else {
        	    		outputStream.write("no".getBytes(), 0, "no".length());
        				Alert alert = new Alert(AlertType.WARNING, "Nome votazione necessario", ButtonType.CLOSE);
                		alert.show();
        			}
        		} else {
        			Alert alert = new Alert(AlertType.WARNING, "Non ci sono partiti e/o candidati", ButtonType.CLOSE);
            		alert.show();
            	}
    		}
    	} else if(radioOrdinale.isSelected()) {
    		outputStream.write("avvio".getBytes(), 0, "avvio".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);

    		if(ok.equals("ok")) {
    			outputStream.write("Voto ordinale".getBytes(), 0, "Voto ordinale".length());
                letti = inputStream.read(buffer);
        		ok = new String(buffer, 0, letti);
        		if(!ok.equals("no")) {
        			TextInputDialog dialog = new TextInputDialog();
        			dialog.setTitle("Nome Votazione");
        			dialog.setHeaderText("Inserisci il nome della votazione");
        			//dialog.setContentText("Please enter your name:");

        			Optional<String> result = dialog.showAndWait();
        			if (result.isPresent() && !result.get().equals("")){
        	    		outputStream.write("ok".getBytes(), 0, "ok".length());
        				setNome_votazione(result.get());
        				setTipo_votazione("ordinale");
        				Node node = (Node) event.getSource();
            			Stage actual = (Stage) node.getScene().getWindow();
            			Parent root = FXMLLoader.load(getClass().getResource("avvioAltre.fxml"));
            	        actual.setScene(new Scene(root));
            	        actual.setTitle("Avvio Voto Ordinale");
        			} else {
        	    		outputStream.write("no".getBytes(), 0, "no".length());
        				Alert alert = new Alert(AlertType.WARNING, "Nome votazione necessario", ButtonType.CLOSE);
                		alert.show();
        			}
        		} else {
        			Alert alert = new Alert(AlertType.WARNING, "Non ci sono partiti e/o candidati", ButtonType.CLOSE);
            		alert.show();
            	}
    		}
    	} else if(radioRef.isSelected()) {
    		outputStream.write("avvio".getBytes(), 0, "avvio".length());
            letti = inputStream.read(buffer);
    		ok = new String(buffer, 0, letti);
    		if(ok.equals("ok")) {
    			outputStream.write("Referendum".getBytes(), 0, "Referendum".length());
    			letti = inputStream.read(buffer);
    			ok = new String(buffer, 0, letti);
        		if(!ok.equals("no")) {
        			Node node = (Node) event.getSource();
        			Stage actual = (Stage) node.getScene().getWindow();
        			Parent root = FXMLLoader.load(getClass().getResource("avvioRef.fxml"));
        	        actual.setScene(new Scene(root));
        	        actual.setTitle("Avvio referendum");
        			//handleIndietro(event);
        		} else {
        			Alert alert = new Alert(AlertType.WARNING, "Non ci sono referendum da avviare", ButtonType.CLOSE);
            		alert.show();
            	}
    		} else {
    			Alert alert = new Alert(AlertType.WARNING, ok, ButtonType.CLOSE);
        		alert.show();
        	}
    	} else {
    		Alert alert = new Alert(AlertType.WARNING, "Seleziona un'opzione oppure torna indietro", ButtonType.CLOSE);
    		alert.show();
    	}
    }

	public static String getNome_votazione() {
		return nome_votazione;
	}

	public static void setNome_votazione(String nome_votazione) {
		ControllerAvvio.nome_votazione = nome_votazione;
	}

    public static String getTipo_votazione() {
		return tipo_votazione;
	}

	public static void setTipo_votazione(String tipo_votazione) {
		ControllerAvvio.tipo_votazione = tipo_votazione;
	}

	@FXML
    void handleIndietro(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Gestore");
    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	nome_votazione = "";
    }

}
