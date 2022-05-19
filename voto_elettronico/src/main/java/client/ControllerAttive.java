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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Votazione;

public class ControllerAttive {

    @FXML
    private Button btnConferma;

    @FXML
    private VBox vbox;
    
    @FXML
    private ToggleGroup group;
    
    private static String scelta;

	Socket so;
    InputStream in;
    OutputStream out;
    ObjectInputStream oin;
    ObjectOutputStream oout;
    
    public static String getScelta() {
		return scelta;
	}

	public void setScelta(String scelta) {
		ControllerAttive.scelta = scelta;
	}

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	RadioButton rb = (RadioButton)group.getSelectedToggle();
        if (rb != null) {
        	out.write("votazione".getBytes(), 0, "votazione".length());
        	out.write(rb.getId().getBytes(), 0, rb.getId().length());
        	scelta = rb.getId();
    		Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
    		String[] v = rb.getId().split("@");
    		if(v[2].equals("referendum")) {
    			Parent root = FXMLLoader.load(getClass().getResource("referendumVoto.fxml"));
                actual.setScene(new Scene(root));
                actual.setTitle(v[0]);
    		} else if(v[2].equals("categorico_preferenze")) {
    			Parent root = FXMLLoader.load(getClass().getResource("votoCategoricoP.fxml"));
                actual.setScene(new Scene(root));
                actual.setTitle(v[0]);
    		} else if(v[2].equals("categorico")) {
    			Parent root = FXMLLoader.load(getClass().getResource("votoCategorico.fxml"));
	        	actual.setScene(new Scene(root));
                actual.setTitle(v[0]);
    		} else if(v[2].equals("ordinale")) {
    			Parent root = FXMLLoader.load(getClass().getResource("VotoOrdinale.fxml"));
                actual.setScene(new Scene(root));
                actual.setTitle(v[0]);
    		}
        } else {
        	Alert alert = new Alert(AlertType.WARNING, "Selezionare un'opzione o chiudere la pagina", ButtonType.CLOSE);
    		alert.show();
        }
    }
    
    @FXML
    void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	ArrayList<Votazione> votazioni = (ArrayList<Votazione>) oin.readObject();
    	group = new ToggleGroup();
    	for(int i = 0; i < votazioni.size(); i++) {
    		RadioButton rb = new RadioButton(votazioni.get(i).getNome());
    		rb.setId(votazioni.get(i).getNome() + "@" + votazioni.get(i).getId() + "@" + votazioni.get(i).getTipo());
    		rb.setPadding(new Insets(10));
    		rb.setToggleGroup(group);
    		vbox.getChildren().addAll(rb);
    	}
    }
}
