/*
 * OnlineService by Davide Reverberi (matr. 332781)
 * 
 */

package OnlineServicePackage;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class for Server operation.
 * @author Davide Reverberi
 */
public class Server
{
	private static final int SPORT = 4444; 	//Port for the socket connection.
	private static final String FILENAME = "./src/OnlineServicePackage/Products.txt";  //Path of the input file.
	
    public final Map<String, String> userMap = Map.of("user", "password", "root", "password");	//Possible combinations to access the service
    public List<Product> productsList = new ArrayList<>(); 	//List of the available products.

    /**
     * Constructor for the Server class.
     * Reads a file to retrieve products and adds them to the internal products list.
     * The file format should be: "productName productPrice".
     * Each line in the file represents a product and should be separated by a space.
     * If the file has a line containing only the number -1, the reading process stops.
     * 
     * @throws IOException If an error occurs while reading the file.
     */
	public Server()  
	{ 
		try {
            BufferedReader br = new BufferedReader(new FileReader(FILENAME));
            String line;

            while (!(line = br.readLine()).equals("-1")) //-1 is the end of the input file (Product.txt)
            {
                String[] parts = line.split(" "); 
                if (parts.length == 2) 
                {
                    String productName = parts[0]; 
                    double productPrice = Double.parseDouble(parts[1]); 
                    productsList.add(new Product(productName, productPrice));
                }
            }

            br.close(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Function to check if a product is in the productsList.
	 * 
	 * @param name String name of the product to check
	 * @return true if product is found, false if not
	 */
	public boolean isInList(String name)
	{
		for(Product p : this.productsList)
		{
			if(p.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Function to send every product in the productsList in the Client buffer.
	 * Each product is sent as a string text to the client.
	 * 
	 * @param os DataOutputStream object used to send each product contained in the Productslist to the client.
	 * @throws IOException If an error occurs while using the output stream.
	 */
	public void products_showToClient(DataOutputStream os) throws IOException  
	{
		for (Product product : this.productsList) 
		{
			os.writeBytes(product.getName()+" "+product.getPrice()+" "+product.getId()+"\n");
		}
		os.writeBytes("end\n");

	}
	
	/**
	 * Function to remove the product requested by the user from the products list.
	 * 
	 * @param os DataOutputStream object used to send each product contained in the Productslist to the client.
	 * @param is BufferedReader object used to send each product contained in the Productslist to the client.
	 * @throws IOException If an error occurs while using the output stream.
	 */
	public void product_sendToClient(DataOutputStream os, BufferedReader is) throws IOException  
	{
		String product_name = is.readLine(); //Server is waiting for the name of the product
		boolean found = false;
		for(Product p : this.productsList)
		{
			if(p.getName().equals(product_name))
			{
				productsList.remove(p);
				found = true;
				break;
			}
		}
		if(!found)
		{
			os.writeBytes("notfound\n");
		}
	}
	
	/**
	 * Function to add a product to the Product list. This method is called by a client
	 * to add a new product to the list or to return an already purchased existing product to the server.
	 *
	 * @param os DataOutputStream for sending the response back to the client.
	 * @param is BufferedReader for reading data sent by the client.
	 * @throws IOException If an error occurs during the reading or sending of data through the streams.
	 */
	public void product_addToList(DataOutputStream os, BufferedReader is) throws IOException		
	{					
		String temp_product = is.readLine();
		String[] parts = temp_product.split(" "); 
		
		String productName = null;
		double productPrice = 0;
				
        if (parts.length == 3) //client is returning a product
        {
            productName = parts[0]; 
            productPrice = Double.parseDouble(parts[1]); 
            int productId = Integer.parseInt(parts[2]);
            productsList.add(new Product(productName, productPrice, productId));
            os.writeBytes("ok\n");
        }
        if(parts.length == 2) //client is asking to add a new product
        {
			productName = parts[0];
        	productPrice = Double.parseDouble(parts[1]);
        	
        	if(isInList(productName))
        	{
        		os.writeBytes("alreadyin\n");
        	}else {
	        	productsList.add(new Product(productName, productPrice));
	        	os.writeBytes("ok\n");
			}
        }
	}

	/**
	 * Waits for client connections on a specified server socket and provides services based on client requests.
	 * The method handles authentication, allowing only authenticated users to use the service. Once authenticated,
	 * the method processes client commands, such as displaying the list of available products, sending requested
	 * products to the client, and adding new products to the server's product list.
	 *
	 * @throws IOException If an error occurs during socket operations or input/output streams.
	 */
	public void service()
	{
		try
		{
			ServerSocket server = new ServerSocket(SPORT);

			boolean authentication = false;
			String client_user = "User";
			String password_client = "password";
			String command = "";
			
			while(true)  
			{
				System.out.println("Waiting for a connection\n");
				Socket client = server.accept();
				System.out.println("Connected");
				authentication = false;

				BufferedReader   is = new BufferedReader(new InputStreamReader(client.getInputStream()));
				DataOutputStream os = new DataOutputStream(client.getOutputStream());
				
				client_user = is.readLine();
				
				//Checking if user has entered the right credentials
				for (java.util.Map.Entry<String, String> e: userMap.entrySet())
				{
					if(client_user.equals(e.getKey()))
					{
						password_client = is.readLine();
						
						if(password_client.equals(e.getValue())) {
							os.writeBytes("ok\n");
							authentication = true;
							break;
						}
					}
				}
				os.writeBytes("notok\n");
				if(authentication) //if authentication is propertly done, service is ready to be used
				{
					boolean closed_client = false;
					while(!closed_client)
					{
						command = is.readLine();
						
						switch (command) {
						case "s": this.products_showToClient(os); break;		//Show the list of available products
						case "b": this.product_sendToClient(os,is); break;		//Send a requested product to the client
						case "r": this.product_addToList(os, is); break;		//Accept an already purchased product form client.
						case "a": this.product_addToList(os, is); break;		//Add a new product requested by the client
						case "close":
						{
							//Close the client connection
							closed_client = true;
							client.close();
							break;
						}
						case "quit":
						{
							//Close the service (and the connected client)
							client.close();
							server.close();
							System.out.println("\nSERVICE CLOSED");
							return;
						}
						default:
							//The invalid command is handled by the client
							break;
						}
					}
					
				}
			}			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main entry point for starting the server application and the OnlineService.
	 * Instantiates a Server object and calls the service method to handle client connections
	 * and provide server functionalities.
	 *
	 * @param args Command-line arguments (not used in this application).
	 */
	public static void main(final String[] args)
	{
		Server server = new Server();
		server.service();
	}
}
