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


    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	ArrayList<Votazione> votazioni = new ArrayList<>();
        if (!selected.isEmpty()) {
    		for(int i = 0; i < selected.size(); i++) {
    			if(selected.get(i).isSelected()) {
    				votazioni.add(new Votazione(Integer.parseInt(selected.get(i).getId()), "referendum"));
    			}
    		}
        }
    	oout = new ObjectOutputStream(out);
		oout.writeObject(votazioni);
        handleIndietro(event);
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
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	ArrayList<Referendum> list = (ArrayList<Referendum>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
		selected = new ArrayList<>();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setId(Integer.toString(list.get(i).getId()));
    		rb.setPadding(new Insets(10));
    		selected.add(rb);
    		Label l = new Label(list.get(i).getTesto());
    		l.setWrapText(true);
    		l.setPadding(new Insets(10));
    		righe.add(new HBox(rb, l));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }

}
