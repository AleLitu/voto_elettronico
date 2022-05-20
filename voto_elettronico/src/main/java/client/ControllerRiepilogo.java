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
    void handleHome(ActionEvent event) throws IOException {
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
    	if(list.size() == 1) {
        	String v[] = list.get(0).split("@");
        	lblTitolo.setText("Riepilogo per: " + v[0]);
        	
    		Label l1 = new Label(v[1]);
    		l1.setWrapText(true);
    		l1.setPadding(new Insets(10));
    		vbox.getChildren().addAll(l1);
    		
    		String v1[] = v[2].split("%");
    		l1 = new Label("Sì: " + v1[0] + "   -->   " + v1[1] + " %");
    		l1.setWrapText(true);
    		l1.setPadding(new Insets(10));
    		vbox.getChildren().addAll(l1);
    		
    		v1 = v[3].split("%");
    		l1 = new Label("No: " + v1[0] + "   -->   " + v1[1] + " %");
    		l1.setWrapText(true);
    		l1.setPadding(new Insets(10));
    		vbox.getChildren().addAll(l1);

    		v1 = v[4].split("%");
    		l1 = new Label("Schede bianche: " + v1[0] + "   -->   " + v1[1] + " %");
    		l1.setWrapText(true);
    		l1.setPadding(new Insets(10));
    		vbox.getChildren().addAll(l1);

    		if(v[5].contains("%")) {
    			v1 = v[5].split("%");
        		l1 = new Label("Vincitore: " + v1[0] + "   -->   Raggiunto il " + v1[1] + " % dei voti possibili");
    		} else {
        		l1 = new Label("Vincitore: " + v[5]);
    		}
    		l1.setWrapText(true);
    		l1.setPadding(new Insets(10));
    		vbox.getChildren().addAll(l1);
    	} else {
    		lblTitolo.setText("Riepilogo per: " + list.get(1));
    		if(list.get(0).equals("categorico")) {
    			for(int i = 2; i < list.size(); i++) {
            		String[] v = list.get(i).split("@");
            		
            		HBox hbox = new HBox();
            		
        			HBox hbox1 = new HBox();
        			Label l1;
            		l1 = new Label("Partiti");
            		l1.setWrapText(true);
            		l1.setPadding(new Insets(10));
            		hbox1.getChildren().addAll(l1);
            		vbox.getChildren().addAll(hbox1);
            		
        			l1 = new Label(v[1]);
            		l1.setWrapText(true);
            		l1.setPadding(new Insets(10));
            		hbox.getChildren().addAll(l1);
            		
            		l1 = new Label (v[2]);
            		l1.setWrapText(true);
            		l1.setPadding(new Insets(10));
            		hbox.getChildren().addAll(l1);

            		if(Integer.parseInt(v[3]) == 1) {
            			l1 = new Label("Vincitore");
    	        		l1.setWrapText(true);
    	        		l1.setPadding(new Insets(10));
    	        		hbox.getChildren().addAll(l1);
            		}
            		vbox.getChildren().addAll(hbox);
        		}
    		} else {
    			String prev = "";
        		for(int i = 2; i < list.size(); i++) {
            		String[] v = list.get(i).split("@");
            		
            		HBox hbox = new HBox();
            		
            		if(!prev.equals(v[0])) {
            			HBox hbox1 = new HBox();
            			Label l1;
            			if(v[0].equals("partito"))
                			l1 = new Label("Partiti");
            			else
                			l1 = new Label("Candidati");
                		l1.setWrapText(true);
                		l1.setPadding(new Insets(10));
                		prev = v[0];
                		hbox1.getChildren().addAll(l1);
                		vbox.getChildren().addAll(hbox1);
            		}
        			Label l1 = new Label(v[1]);
            		l1.setWrapText(true);
            		l1.setPadding(new Insets(10));
            		hbox.getChildren().addAll(l1);
            		
            		l1 = new Label (v[2]);
            		l1.setWrapText(true);
            		l1.setPadding(new Insets(10));
            		hbox.getChildren().addAll(l1);

            		if(Integer.parseInt(v[3]) == 1) {
            			l1 = new Label("Vincitore");
    	        		l1.setWrapText(true);
    	        		l1.setPadding(new Insets(10));
    	        		hbox.getChildren().addAll(l1);
            		}
            		vbox.getChildren().addAll(hbox);
        		}
        	}
    	}
    }
}
