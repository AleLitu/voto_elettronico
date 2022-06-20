package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;


public class ClientOnline extends Application {
	
    @Override
    public void start(Stage primaryStage) throws Exception{
    	//FXMLLoader fxmlLoader = new FXMLLoader(DaoPatternDemo.class.getResource("login.fxml"));
    	//System.out.println(User.create("12345"));
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
    	launch(args);
    }
}