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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Referendum;
import model.Votazione;

public class ControllerTermina {

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
    ArrayList<RadioButton> selected;
    List<HBox> righe;

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	ArrayList<Votazione> votazioni = new ArrayList<>();
        if (!selected.isEmpty()) {
    		for(int i = 0; i < selected.size(); i++) {
    			if(selected.get(i).isSelected()) {
    				for(int j = 0; j < righe.size(); j++) {
    					if(Integer.parseInt(selected.get(i).getId()) == Integer.parseInt(righe.get(j).getChildren().get(0).getId())) {
    	    				votazioni.add(new Votazione(Integer.parseInt(selected.get(i).getId()), righe.get(j).getChildren().get(2).getId(), righe.get(j).getChildren().get(1).getId()));
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
		selected = new ArrayList<>();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setId(Integer.toString(list.get(i).getId()));
    		rb.setPadding(new Insets(10));
    		selected.add(rb);
    		Label ln = new Label(list.get(i).getNome());
    		ln.setId(list.get(i).getNome());
    		ln.setWrapText(true);
    		ln.setPadding(new Insets(10));
    		Label lt = new Label();
    		lt.setId(list.get(i).getTipo());
    		lt.setVisible(false);
    		righe.add(new HBox(rb, ln, lt));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }

}
