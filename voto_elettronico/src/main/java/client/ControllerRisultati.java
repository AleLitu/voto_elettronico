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

public class ControllerRisultati {

    @FXML
    private Button btnConferma;

    @FXML
    private Button btnIndietro;
    
    @FXML
    private ToggleGroup group;

    @FXML
    private VBox vbox;
    
    Socket so;
    InputStream in;
    OutputStream out;
    ObjectInputStream oin;
    ObjectOutputStream oout;
    List<HBox> righe;

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	RadioButton rb = (RadioButton)group.getSelectedToggle();
        if (rb != null) {
        	out.write(rb.getId().getBytes(), 0, rb.getId().length());
    		Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
    		Parent root = FXMLLoader.load(getClass().getResource("riepilogo.fxml"));
            actual.setScene(new Scene(root));
            actual.setTitle("Riepilogo");
        } else {
        	Alert alert = new Alert(AlertType.WARNING, "Selezionare un'opzione o tornare indietro", ButtonType.CLOSE);
    		alert.show();
        }
    }

    @FXML
    void handleIndietro(ActionEvent event) throws IOException {
    	out.write("no".getBytes(), 0, "no".length());
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
    	ArrayList<String> list = (ArrayList<String>) oin.readObject();
    	righe = new ArrayList<>();
    	group = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		String[] v = list.get(i).split("@");
    		RadioButton rb = new RadioButton();
    		rb.setId(v[0] + v[1]);
    		rb.setPadding(new Insets(10));
    		rb.setToggleGroup(group);
    		Label ln = new Label(v[0]);
    		ln.setWrapText(true);
    		ln.setPadding(new Insets(10));
    		righe.add(new HBox(rb, ln));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }
}