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

public class ControllerLogin {
	
	private static User user;

    @FXML
    private Button btnSend;

    @FXML
    private Label lblMessage;

    @FXML
    private TextField lblPassword;

    @FXML
    private TextField lblUsername;

    @FXML
    void handlePassword(ActionEvent event) {

    }
    
    public static User getUser() {
    	return user;
    }

    @FXML
    void handleSend(ActionEvent event) {
    	lblMessage.setVisible(true);
    	
    	String usr = lblUsername.getText();
    	String pwd = lblPassword.getText();
    	
    	UserDao userdao = new UserDaoImpl();
    	user = userdao.getUser(usr, pwd);
    	String messaggio;
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
    				//votante
	    			Parent root = FXMLLoader.load(getClass().getResource("benvenuto.fxml"));
	                actual.setScene(new Scene(root));
	                actual.setTitle("Logged");
    			}
    		} catch(Exception e) {
    			System.out.println(e);
    		}
    	} else {
    		messaggio = "Errore nelle credenziali inserite";
    		lblMessage.setText(messaggio);
    	}
    }

    @FXML
    void handleUsername(ActionEvent event) {

    }

}
