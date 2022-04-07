package client;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import model.UserDao;
import model.UserDaoImpl;

public class ControllerLogin {
	
	private static User user;
	private static Socket so;
    OutputStream outputStream;
    InputStream inputStream;
	
	@FXML
    private TextField lblFirst;
	
    @FXML
    private TextField lblSecond;
    
    @FXML
    private TextField lblThird;
    
    @FXML
    private TextField lblFourth;

    @FXML
    private Button btnSend;

    @FXML
    private Label lblMessage;

    @FXML
    private TextField lblPassword;

    @FXML
    private TextField lblUsername;
    
    public final static int SOCKET_PORT=50000;


    @FXML
    void handlePassword(ActionEvent event) {

    }
    
    public static User getUser() {
    	return user;
    }
    
    public static Socket getSocket() {
    	return so;
    }

    @FXML
    void handleSend(ActionEvent event) throws Exception {
    	lblMessage.setVisible(true);
    	
    	String usr = lblUsername.getText();
    	String pwd = lblPassword.getText();
    	
    	UserDao userdao = new UserDaoImpl();
    	user = userdao.getUser(usr, pwd);
    	String messaggio;
    	checkIntType();
    	if(user != null) {
    		Node node = (Node) event.getSource();
    		Stage actual = (Stage) node.getScene().getWindow();
    		//actual.close();
    		try {
    			if(user.getType().equals("gestore")) {
    				Parent root = FXMLLoader.load(getClass().getResource("gestore.fxml"));
                    actual.setScene(new Scene(root));
                    actual.setTitle("Logged");
    			} else {
    				int dim_buffer = 100;
    				int letti;
    				String risposta;
    				byte buffer[] = new byte[dim_buffer];
    				outputStream.write("e".getBytes(), 0, "e".length());
    		        letti = inputStream.read(buffer);
    		        risposta = new String(buffer, 0, letti);
    		        if(risposta.equals("null")) {
    		        	lblMessage.setText("Non ci sono votazioni aperte al momento");
    		        	so.close();
    		        } else if(risposta.equals("Voto categorico")){
    		        	/*Parent root = FXMLLoader.load(getClass().getResource("categoricoVoto.fxml"));
    	                actual.setScene(new Scene(root));
    	                actual.setTitle("Voto categorico");*/
    		        } else if(risposta.equals("Voto categorico con preferenze")){
    		        	/*Parent root = FXMLLoader.load(getClass().getResource("categoricoPVoto.fxml"));
    	                actual.setScene(new Scene(root));
    	                actual.setTitle("Voto categorico con preferenze");*/
    		        } else if(risposta.equals("Voto ordinale")){
    		        	/*Parent root = FXMLLoader.load(getClass().getResource("ordinaleVoto.fxml"));
    	                actual.setScene(new Scene(root));
    	                actual.setTitle("Voto ordinale");*/
    		        } else if(risposta.equals("Referendum")){
    		        	Parent root = FXMLLoader.load(getClass().getResource("referendumVoto.fxml"));
    	                actual.setScene(new Scene(root));
    	                actual.setTitle("Referendum");
    		        }
    			}
    		} catch(Exception e) {
    			System.out.println(e);
    		}
    	} else {
    		messaggio = "Errore nelle credenziali inserite";
    		lblMessage.setText(messaggio);
    	}
    }
    
    public void connection (String address) throws IOException{
    	so = new Socket(address, SOCKET_PORT);
        outputStream = so.getOutputStream();
        inputStream = so.getInputStream();
		System.out.println("Client connesso, Indirizzo: " + so.getInetAddress() + "; porta: "+ so.getPort());
    }
    
    public int isInteger(String input) {
        try {
            int n;
            n=Integer.parseInt(input);
            return n;
        } catch (Exception e) {
            return -1;
        }
    }
    
    protected void checkIntType() throws Exception {
        String n1=lblFirst.getText();
        String n2=lblSecond.getText();
        String n3=lblThird.getText();
        String n4=lblFourth.getText();

        int n1_int=isInteger(n1);
        int n2_int=isInteger(n2);
        int n3_int=isInteger(n3);
        int n4_int=isInteger(n4);
        if(((n1_int<0))||(n1_int>255)||((n2_int<0))||(n2_int>255)||((n3_int<0))||(n3_int>255)||((n4_int<0))||(n4_int>255)){
        	lblMessage.setText("Indirizzo sbagliato coglione");
        }else{
            String indirizzo=n1+"."+n2+"."+n3+"."+n4;
            //errorLabel.setText("L'indirizzo inserito: "+indirizzo);
            //errorLabel.setOpacity(1);
            connection(indirizzo);
            //DA QUA CI DOVREBBE ESSERE IL TENTATIVO DI CONNESSIONE AL SERVER
        }

    }

    @FXML
    void handleUsername(ActionEvent event) {

    }

}
