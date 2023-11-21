/*
 * OnlineService by Davide Reverberi (matr. 332781)
 * 
 */

package OnlineServicePackage;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Represents the graphical user interface (GUI) for the client main menu and 
 * and the related resulting functions in the OnlineService application.
 * When the corresponding buttons are pressed, the various interfaces that 
 * handle all possible functions are handled.
 * 
 * @author Davide Reverberi
 */
public class ClientMainGUI {
	
	private static List<Product> productList = new ArrayList<>(); //local productList of the client
	public static List<Product> purchasedProductsList = new ArrayList<>();  //list of purchased products

	public ClientMainGUI()
	{
		//Empty costructor
	}
	
	/**
	 * Function that retrieves the list of products from the server and populates the local product list.
	 * The method reads product information from the BufferedReader received from the server,
	 * processes the data, and adds products to the local list.
	 *
	 * @param is The BufferedReader for reading data from the server.
	 * @param os The DataOutputStream for sending data to the server (not used in this method).
	 */
	private static void getProductsListFromServer(BufferedReader is, DataOutputStream os) 
	{
		productList.clear();
		String data_in;
		try {
			
			data_in = is.readLine();
			while(!data_in.equals("end")) //Getting product List from server
			{
				String[] parts = data_in.split(" ");  //Adding the bought product to the local List.
	            if (parts.length == 3) 
	            {
	                String productName = parts[0]; 
	                double productPrice = Double.parseDouble(parts[1]); 
	                int productID = Integer.parseInt(parts[2]);
	                productList.add(new Product(productName, productPrice, productID)); 
	            }
				data_in = is.readLine();
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates and configures a TableView to display a list of products.
	 *
	 * @param observableProductList The ObservableList containing the products to be displayed.
	 * @return A configured TableView displaying the product information.
	 */
	@SuppressWarnings("unchecked")
	private static TableView<Product> createProductTable(ObservableList<Product> observableProductList) {
        TableView<Product> tableView = new TableView<>(observableProductList);

        // Creazione delle colonne della tabella
        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Product, String> nameColumn = new TableColumn<>("Nome");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Prezzo");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        tableView.getColumns().addAll(idColumn, nameColumn, priceColumn);

        return tableView;
    }
	
	/**
	 * Displays a window for adding new products to the product list.
	 * Allows the user to view the current list of products, input a new product's name and price,
	 * and add the new product to the list. If the product already exists, provides feedback to the user.
	 * Also allows returning back to the main menu.
	 * 
	 * @param client Socket object representing the client connection.
	 * @param os DataOutputStream object for sending data to the server.
	 * @param is BufferedReader object for reading data from the server.
	 * @throws IOException If an I/O error occurs while communicating with the server.
	*/
	public static void addProductsInterface(Socket client, BufferedReader is, DataOutputStream os) throws IOException
	{
		os.writeBytes("s\n");
		getProductsListFromServer(is, os);
		
		ObservableList<Product> observableProductList = FXCollections.observableArrayList(productList);
		
		Stage addStage = new Stage();
		
		TableView<Product> tableView = createProductTable(observableProductList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		
		VBox productInputBox = new VBox(10);
        productInputBox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        
        Label headerLabel = new Label("Add a new product:");
        headerLabel.setStyle("-fx-font-size: 15;");

        Button addButton = new Button("Add");
        Button backButton = new Button("Back");
        
        //add button handler:
        addButton.setOnAction(event -> {
        	try
        	{
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
	            alert.setTitle("Product viewer");
	            alert.setHeaderText(null);
	            
	        	if(!nameField.getText().isEmpty() && !priceField.getText().isEmpty()) //True if name and price fields aren't empty
	        	{
	        			os.writeBytes("a\n"); //Telling the server to add a new product from server in his list
	        			
	    				os.writeBytes(nameField.getText()+" "+ priceField.getText()+"\n");
	    	            
	    	            String serverAnwser = is.readLine(); //Blocking instruction, waits for the outcome of adding the product to the server's list	
	    	            
	    	            if(serverAnwser.equals("alreadyin")) //Waiting for the server to receive the product back.
	    	            {
	    	            	alert.setContentText(nameField.getText() + " already exist! Try adding a new product..");
	    					
	    	            }else { //is returns "ok" --> Operation done
	    	            	alert.setContentText(nameField.getText() + " succesfully added in the list!");
	    				}
	    	            
	        	}else { //Name and price fileds are empty, cannot add a product
	        		
					alert.setContentText("Add a product with a valid name and price!");
				}
	        	
	            alert.showAndWait(); //Waiting for the user to close the alert
				
	            addStage.close();
	            ClientMainGUI.addProductsInterface(client, is, os); //Open a new add window
	            
        	} catch (IOException e) 
    		{
				e.printStackTrace();
			}
            
        });
        
        //back button handler:
        backButton.setOnAction(e ->{ //Return to main menu (mainDisplay function)
        	ClientMainGUI.mainDisplay(client, is, os);
        	addStage.close();
        });
        
        productInputBox.getChildren().addAll(headerLabel, nameField, priceField, addButton, backButton);
        productInputBox.setAlignment(Pos.CENTER);
        
        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);

        // Set column constraints to make the table take up 2/3 of the space --> Organize space into columns with gridPane
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(70); // about 2/3 of the space
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(30); // about 1/3 of the space

        gridPane.getColumnConstraints().addAll(column1, column2);
        gridPane.addRow(0, tableView, productInputBox);

        VBox root = new VBox(gridPane);
        root.setPadding(new Insets(10));
        root.setSpacing(20);

		Scene scene = new Scene(root, 600, 400);
		
		addStage.setScene(scene);
        addStage.show();
	}
	
	/**
	 * Displays a window for returning purchased products.
	 * Allows the user to view a list of purchased products, select a product for return,
	 * and return to the main menu.
	 *
	 * @param client Socket object representing the client connection.
	 * @param os DataOutputStream object for sending data to the server.
	 * @param is BufferedReader object for reading data from the server.
	 */
	public static void returnProductsInterface(Socket client, BufferedReader is, DataOutputStream os)
	{		
		ObservableList<Product> observableProductList = FXCollections.observableArrayList(purchasedProductsList);
		
		Stage returnStage = new Stage();
		returnStage.setTitle("Product viewer");
		
		Button returnButton = new Button("Return a product");
		Button backButton = new Button("Back to main menu");
		
		TableView<Product> tableView = createProductTable(observableProductList);
        
        Label titleLabel = new Label("Purchased products:");
                
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tableView.setMinWidth(300); // Minimum table width
        
		BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(tableView);
        root.setTop(titleLabel);
        
        // Creating an HBox for the buttons
        HBox buttonBox = new HBox(10); // 10 pixel spacing between buttons
        buttonBox.getChildren().addAll(returnButton, backButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-alignment: CENTER;"); 	// Center the buttons inside the HBox
        
        root.setBottom(buttonBox);
        
        if(purchasedProductsList.isEmpty()) //If there are no purchased items yet, close the interface with an alert.
        {
        	Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Product viewer");
            alert.setHeaderText(null);
            alert.setContentText("No Items yet!");

            alert.showAndWait();
			
	        ClientMainGUI.mainDisplay(client, is, os); //Return to main menu 
			returnStage.close();
			
        }else { //If there is at least one purchased item, open the window
        	
            Scene scene = new Scene(root, 600, 400);
            returnStage.setScene(scene);
            returnStage.show();
		}
        
        //return button handler:
        returnButton.setOnAction(e -> {  
        	if (!tableView.getSelectionModel().isEmpty()) //If true, a prouduct in the list shown is selected and ready to be returned
        	{  
        		try {
					Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
					purchasedProductsList.remove(selectedProduct);
					
					os.writeBytes("r\n");
					os.writeBytes(selectedProduct.getName()+" "+selectedProduct.getPrice()+" "+selectedProduct.getId()+"\n");
										
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
		            alert.setTitle("Product viewer");
		            alert.setHeaderText(null);
		            
		            //Blocking instruction, wait for the result of adding the product to the server list:
		            String serverAnwser = is.readLine();
		            
		            if(serverAnwser.equals("ok")) //Waiting for the server to receive the product back
		            {
						alert.setContentText(selectedProduct.getName() + " succesfully returned");
						
		            }else {
						alert.setContentText("Operation failed, try again!");
		            }
		            
		            alert.showAndWait(); //Waiting for the user to close the alert
		            
			        ClientMainGUI.mainDisplay(client, is, os); 
					returnStage.close();
		            
				} catch (IOException e1)
        		{
					e1.printStackTrace();
				} 
        	}else 
        	{
        		Label titleLabel2 = new Label("Select a product!");
        		titleLabel2.setStyle("-fx-text-fill: red;");
        		root.setTop(titleLabel2);
        	}
        });
        
        //back button handler:
        backButton.setOnAction(e ->{ //Return to main menu (mainDisplay function)
        	ClientMainGUI.mainDisplay(client, is, os);
        	returnStage.close();
        });
	}
	
	/**
	 * Displays a window for buying products.
	 * Allows the user to view a list of products, select a product for purchase,
	 * or return back to the main menu.
	 *
	 * @param client Socket object representing the client connection.
	 * @param os DataOutputStream object for sending data to the server.
	 * @param is BufferedReader object for reading data from the server.
	 * @throws IOException If an I/O error occurs while communicating with the server.
	 */
	public static void showProductsInterface(Socket client, BufferedReader is, DataOutputStream os) throws IOException
	{
		productList.clear();
		os.writeBytes("s\n");  //Telling the server to send the products to the client
		getProductsListFromServer(is, os);
		
		ObservableList<Product> observableProductList = FXCollections.observableArrayList(productList);
		
		Stage showStage = new Stage();
		showStage.setTitle("Products viewer");
		
		Button buyButton2 = new Button("Buy a product");
		Button backButton = new Button("Back to main menu");
		
		TableView<Product> tableView = createProductTable(observableProductList);
        
        Label titleLabel = new Label("Available products:");
        
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); //Using all the available space
        tableView.setMinWidth(300); // Minimum width of the table
        
		BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(tableView);
        root.setTop(titleLabel);
        
        // Creating an HBox for the buttons
        HBox buttonBox = new HBox(10);	 // Spacing of 10 pixels between the buttons
        buttonBox.getChildren().addAll(buyButton2, backButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setStyle("-fx-alignment: CENTER;"); 	// Center the buttons inside the HBox
        
        root.setBottom(buttonBox);
        
        //buy button handler:
        buyButton2.setOnAction(e -> {  
        	if (!tableView.getSelectionModel().isEmpty()) //If true, a prouduct in the list shown is selected and ready to be purchased
        	{  
        		try {
					Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
					os.writeBytes("b\n"); //Sending the request to the server 
					os.writeBytes(selectedProduct.getName()+"\n");
					
					productList.remove(selectedProduct);
					purchasedProductsList.add(selectedProduct);
					
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
		            alert.setTitle("Product viewer");
		            alert.setHeaderText(null);
		            alert.setContentText(selectedProduct.getName() + " succesfully purchased for "+ selectedProduct.getPrice()+"$");

		            alert.showAndWait(); //Waiting for the user to close the alert
					
		            ClientMainGUI.mainDisplay(client, is, os); //Return to main menu 
					showStage.close();
					
				} catch (IOException e1)
        		{
					e1.printStackTrace();
				} 
        	}else //No products selected, unable to purchase
        	{
        		Label titleLabel2 = new Label("Select a product!");
        		titleLabel2.setStyle("-fx-text-fill: red;");
        		root.setTop(titleLabel2);
        	}
        });
        
        backButton.setOnAction(e ->{ 
        	ClientMainGUI.mainDisplay(client, is, os);	//Return to main menu (mainDisplay function)
        	showStage.close();
        });
        
        Scene scene = new Scene(root, 600, 400);
        showStage.setScene(scene);
        showStage.show();
	}

	/**
	 * Displays the main menu of the OnlineService application in a new window.
	 * There is a button for every possible function available to the user.
	 *
	 * @param client Socket object representing the client connection.
	 * @param os DataOutputStream object for sending data to the server.
	 * @param is BufferedReader object for reading data from the server.
	 */
	public static void mainDisplay(Socket client, BufferedReader is, DataOutputStream os)
	{
		Stage productsWindowStage = new Stage();
		productsWindowStage.setTitle("Main Menu");

		Text labelText = new Text("Choose the operation:");
		labelText.setFont(Font.font("Arial", 20));
		
        Button showButton = new Button("Buy");
        Button quitButton = new Button("Quit");
        Button returnButton = new Button("Return");
        Button addButton = new Button("Add");
        
        showButton.setPrefSize(150, 50);
        quitButton.setPrefSize(150, 50);
        returnButton.setPrefSize(150, 50);
        addButton.setPrefSize(150, 50);

        // Creating a VBox layout to center the buttons
        VBox vbox = new VBox(20); // 20 is the spacing between the buttons 
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(labelText,showButton, returnButton, addButton, quitButton);
        
        showButton.setOnAction(e -> {
        	try {
				ClientMainGUI.showProductsInterface(client, is, os);
				productsWindowStage.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });
        
        quitButton.setOnAction(e -> {
        	try {
				client.close();
				productsWindowStage.close();
				os.writeBytes("quit\n"); //Telling the server to quit and close the OnlineService
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });
        
        returnButton.setOnAction(e -> {
        	ClientMainGUI.returnProductsInterface(client, is, os);
			productsWindowStage.close();
        });
        
        addButton.setOnAction(e -> {
        	try {
        		ClientMainGUI.addProductsInterface(client, is, os);
				productsWindowStage.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        });

        // Creating a scene
        Scene scene = new Scene(vbox, 600, 400);

        productsWindowStage.setScene(scene);
        productsWindowStage.show();
	}
}

