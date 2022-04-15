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
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Partito;
import model.Referendum;

public class ControllerVC {
	
	Socket so;
    InputStream in;
    OutputStream out;
    
    @FXML
    private VBox vbox;
    
    @FXML
    private ToggleGroup group;

    @FXML
    private Button btnConferma;

    @FXML
    void handleConferma(ActionEvent event) {

    }
    
    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
        out.write("partiti".getBytes(), 0, "partiti".length());
    	ObjectInputStream oin = new ObjectInputStream(in);
    	List<Partito> list = (List<Partito>) oin.readObject();
    	List<HBox> righe = new ArrayList<>();
    	group = new ToggleGroup();
    	for(int i = 0; i < list.size(); i++) {
    		RadioButton rb = new RadioButton();
    		rb.setToggleGroup(group);
    		rb.setId(Integer.toString(list.get(i).getId()));
    		rb.setPadding(new Insets(10));
    		Label l = new Label(list.get(i).getNome());
    		l.setWrapText(true);
    		l.setPadding(new Insets(10));
    		righe.add(new HBox(rb, l));
    	}
    	vbox.getChildren().clear();
    	vbox.getChildren().addAll(righe);
    }

}
