package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
import model.Referendum;

public class ControllerAvvioRef {

    @FXML
    private Button btnConferma;

    @FXML
    private Button btnIndietro;
    
    @FXML
    private VBox vbox;
    
    @FXML
    private ToggleGroup group;
    
    Socket so;
    InputStream in;
    OutputStream out;
    

    @FXML
    void handleConferma(ActionEvent event) throws IOException {
    	RadioButton rb = (RadioButton) group.getSelectedToggle();
    	if(rb == null) {
    		Alert alert = new Alert(AlertType.WARNING, "Nessun referendum selezionato", ButtonType.CLOSE);
    		alert.show();
    	} else {
    		out.write(rb.getId().getBytes(), 0, rb.getId().length());
    		Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
    		Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
            actual.setScene(new Scene(root));
            actual.setTitle("Avvio");
    	}
    }

    @FXML
    void handleIndietro(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("avvio.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Avvio");
    }

    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	List<Referendum> list = (List<Referendum>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
    	group = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setToggleGroup(group);
    		rb.setId(Integer.toString(list.get(i).getId()));
    		rb.setPadding(new Insets(10));
    		Label l = new Label(list.get(i).getTesto());
    		l.setWrapText(true);
    		l.setPadding(new Insets(10));
    		righe.add(new HBox(rb, l));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }
}
