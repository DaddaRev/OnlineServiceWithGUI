/*
 * OnlineService by Davide Reverberi (matr. 332781)
 * 
 */

package OnlineServicePackage;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Represents the graphical user interface (GUI) for the client login in the OnlineService application.
 * The class extends the JavaFX Application class and provides methods to handle user authentication
 * by connecting to the server and displaying a login window.
 * Upon successful login, it transitions to the main display of the OnlineService application. 
 * 
 * @author Davide Reverberi
 */
public class ClientLoginGUI extends Application {
	
	private static final int SPORT = 4444;
	private static final String SHOST = "localhost";
	
	public List<Product> purchasedProductsList = new ArrayList<>();  //List of the purchased products.

	/**
	 * Attempts to authenticate a client by sending the provided username and password to the server.
	 * The authentication process involves sending the credentials to the server, receiving a response,
	 * and checking whether the authentication was successful.
	 *
	 * @param client Socket object representing the client connection.
	 * @param os DataOutputStream object for sending data to the server.
	 * @param is BufferedReader object for reading data from the server.
	 * @param user Username to be used for authentication.
	 * @param password Password to be used for authentication.
	 * @return true if the authentication is successful; false otherwise.
	 */
	private static boolean login(Socket client, DataOutputStream os, BufferedReader is, String user, String password)
	{
		try 
		{
			os.writeBytes(user+"\n");
			os.writeBytes(password+"\n");
			
			if(!is.readLine().equals("ok"))    //Reading the outcome of the authentication process
			{
				return false;
			}else {
				System.out.println("Succesfully logged in\n");
			}
			return true;
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Initializes and displays the JavaFX GUI for the OnlineService application.
	 * This method is automatically called when launching the JavaFX application.
	 * It creates a login window with input fields for username and password, as well as a login button.
	 * Upon successful login, it transitions to the main display of the OnlineService application.
	 *
	 * @param primaryStage The primary stage for the JavaFX application.
	 */
    @Override
    public void start(Stage primaryStage)
    {
    	primaryStage.setTitle("OnlineService Welcome");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton   = new Button("Sign in");
        HBox   hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(loginButton);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        loginButton.setOnAction(e -> {
			try
			{
				//Creating a client every time the login button is pressed
				Socket client = new Socket(SHOST, SPORT);
		    	BufferedReader   is = new BufferedReader(new InputStreamReader(client.getInputStream()));
				DataOutputStream os = new DataOutputStream(client.getOutputStream());
			
	        	String user  = usernameField.getText();
	            String password = passwordField.getText();
	            
	            if(ClientLoginGUI.login(client, os, is, user, password))
	            {
		            ClientMainGUI.mainDisplay(client, is, os); 	//Main menu performed in the ClientMainGUI class
		            primaryStage.close();

	            }else {
	            	actiontarget.setFill(Color.FIREBRICK);
	            	actiontarget.setText("Wrong user or password!");
	            	usernameField.clear();
	                passwordField.clear();
	            	client.close();	           
	            }
	            
			} catch (UnknownHostException e1) 
			{
				e1.printStackTrace();
				
			} catch (IOException e1) 
			{
				e1.printStackTrace();
			}
        });
        
        Scene scene = new Scene(grid, 300, 275);   	
        
        primaryStage.setScene(scene);
        primaryStage.show();		
    }
    
    /**
     * The main entry point for launching a JavaFX application.
     * This method is automatically called.
     * 
     * @param args Command-line arguments passed to the application (not used in this context).
     */
    public static void main(String[] args) {
        launch(args);
    }
}
