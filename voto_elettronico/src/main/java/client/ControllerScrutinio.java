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

public class ControllerScrutinio {

    @FXML
    private Button btnConferma;

    @FXML
    private Button btnIndietro;

    @FXML
    private VBox vbox;
    
    Socket so;
    InputStream in;
    OutputStream out;
    ObjectInputStream oin;
    ObjectOutputStream oout;
    ArrayList<ToggleGroup> gruppi;
    List<HBox> righe;

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	ArrayList<Votazione> votazioni = new ArrayList<>();
        if (!gruppi.isEmpty()) {
    		for(int i = 0; i < gruppi.size(); i++) {
    			if((RadioButton)gruppi.get(i).getSelectedToggle() != null) {
    				for(int j = 0; j < righe.size(); j++) {
    					RadioButton rb = (RadioButton)gruppi.get(i).getSelectedToggle();
    					if(rb.getId().equals(righe.get(j).getChildren().get(0).getId())) {
    	    				votazioni.add(new Votazione(Integer.parseInt(righe.get(j).getChildren().get(3).getId()), rb.getText(), righe.get(j).getChildren().get(0).getId()));
    					}
    				}
    			}
    		}
        } else {
        	Alert alert = new Alert(AlertType.ERROR, "", ButtonType.CLOSE);
    		alert.show();
        }
        if(votazioni.size() == 0) {
        	Alert alert = new Alert(AlertType.WARNING, "Selezionare un'opzione o tornare indietro", ButtonType.CLOSE);
    		alert.show();
        } else {
        	oout = new ObjectOutputStream(out);
    		oout.writeObject(votazioni);
    		Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
    		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
            actual.setScene(new Scene(root));
            actual.setTitle("Gestore");
        }
    }

    @FXML
    void handleIndietro(ActionEvent event) throws IOException {
    	oout = new ObjectOutputStream(out);
		oout.writeObject(null);
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Gestore");
    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	ArrayList<Votazione> list = (ArrayList<Votazione>) oin.readObject();
    	righe = new ArrayList<>();
		gruppi = new ArrayList<>();
    	for(int i = 0; i < list.size(); i++) {
    		ToggleGroup gruppo = new ToggleGroup();
    		Label ln = new Label(list.get(i).getNome() + " : ");
    		ln.setId(list.get(i).getNome());
    		ln.setWrapText(true);
    		ln.setPadding(new Insets(10));
    		RadioButton rb1;
    		RadioButton rb2;
    		if(list.get(i).getTipo().equals("referendum")) {
    			rb1 = new RadioButton();
        		rb1.setId(list.get(i).getNome());
        		rb1.setText("Senza quorum");
        		rb1.setPadding(new Insets(10));
        		rb1.setToggleGroup(gruppo);
        		rb2 = new RadioButton();
        		rb2.setId(list.get(i).getNome());
        		rb2.setText("Con quorum");
        		rb2.setPadding(new Insets(10));
        		rb2.setToggleGroup(gruppo);
    		} else {
    			rb1 = new RadioButton();
        		rb1.setId(list.get(i).getNome());
        		rb1.setText("Maggioranza");
        		rb1.setPadding(new Insets(10));
        		rb1.setToggleGroup(gruppo);
        		rb2 = new RadioButton();
        		rb2.setId(list.get(i).getNome());
        		rb2.setText("Maggioranza assoluta");
        		rb2.setPadding(new Insets(10));
        		rb2.setToggleGroup(gruppo);
    		}
    		gruppi.add(gruppo);
    		Label lt = new Label();
    		lt.setId(Integer.toString(list.get(i).getId()));
    		lt.setText(list.get(i).getTipo());
    		lt.setVisible(false);
    		righe.add(new HBox(ln, rb1, rb2, lt));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }

}
