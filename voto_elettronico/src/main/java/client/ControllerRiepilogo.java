package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerRiepilogo {

    @FXML
    private Button btnHome;

    @FXML
    private Label lblTitolo;

    @FXML
    private VBox vbox;
    
    Socket so;
    InputStream in;
    OutputStream out;
    ObjectInputStream oin;
    ObjectOutputStream oout;

    @FXML
    public void initialize() throws IOException, ClassNotFoundException {
    	so = ControllerLogin.getSocket();
    	in = so.getInputStream();
    	out = so.getOutputStream();
    	ObjectInputStream oin = new ObjectInputStream(in);
    	String s = (String) oin.readObject();
    	System.out.println(s);
    }
}
