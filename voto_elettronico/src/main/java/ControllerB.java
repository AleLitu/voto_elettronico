import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


public class ControllerB {

    @FXML
    private Label lblWelcome;
    
	private User user;
    
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@FXML
	public void transferMessage(String message) {
		lblWelcome = new Label();
		System.out.println("madonna scrofa");
        lblWelcome.setText("Benvenuto, " + message);
        System.out.println(lblWelcome);
    }

	@FXML
	public void initialize() {
		System.out.println("dio merdone");
	}

	/*@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("dio merdone");		
	}*/

}
