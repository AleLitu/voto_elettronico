import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ControllerGestore {
	
	@FXML
	private BorderPane gestoreBorderPane;

    @FXML
    private MenuButton btnAvvia;

    @FXML
    private MenuItem btnAvviaReferendum;

    @FXML
    private MenuItem btnCategorico;

    @FXML
    private MenuItem btnCategoricoPr;

    @FXML
    private MenuButton btnInserimento;

    @FXML
    private MenuItem btnLista;

    @FXML
    private Button btnLogout;

    @FXML
    private MenuItem btnMaggAss;

    @FXML
    private MenuItem btnMaggioranza;

    @FXML
    private MenuItem btnNoQuorum;

    @FXML
    private MenuItem btnOrdinale;

    @FXML
    private MenuItem btnQuorum;

    @FXML
    private MenuItem btnReferendum;

    @FXML
    private Button btnRisultati;

    @FXML
    private MenuButton btnScrutinio;

    @FXML
    private Label lblNome;

    @FXML
    void btnNoQuorum(ActionEvent event) {

    }

    @FXML
    void handleAvviaReferendum(ActionEvent event) {

    }

    @FXML
    void handleCategorico(ActionEvent event) {

    }

    @FXML
    void handleCategoricoPr(ActionEvent event) {

    }

    @FXML
    void handleLista(ActionEvent event) throws IOException {
		Stage actual = (Stage) gestoreBorderPane.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("liste.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("");
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
    	Node node = (Node) event.getSource();
		Stage actual = (Stage) node.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("Login");
    }

    @FXML
    void handleMaggAss(ActionEvent event) {

    }

    @FXML
    void handleMaggioranza(ActionEvent event) {

    }

    @FXML
    void handleOrdinale(ActionEvent event) {

    }

    @FXML
    void handleQuorum(ActionEvent event) {

    }

    @FXML
    void handleReferendum(ActionEvent event) throws IOException {
    	Stage actual = (Stage) gestoreBorderPane.getScene().getWindow();
		Parent root = FXMLLoader.load(getClass().getResource("referendum.fxml"));
        actual.setScene(new Scene(root));
        actual.setTitle("");
    }

    @FXML
    void handleRisultati(ActionEvent event) {

    }

}
