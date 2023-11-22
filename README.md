# OnlineServiceWithGUI
Online service for the sale of tech products with an easy GUI for the user.

The program allows information on a set of products for sale to be kept in memory, and allows the user to access the system to make purchases, return previously purchased products and add new products to the list of those available.

Last version released on 11/22/2023.

<h2>Usage</h2>
To run the program, you must initially compile and run the Server.java file. The server once started will wait for a client connection. To establish the connection with the client, compile and run ClientLoginGUI.java, an interface will open where the user will be prompted to log in to the system.

To summarize, in order to run Online Service you must:
-Run the server 
-Run the client

Once authenticated with username and password (see next paragraph for more information), it will be possible to select the various operations available on the products and follow the steps provided.

<h2>Login Info</h2>
To log in correctly, you must enter username and password in the fields provided in the Login window. The server allows 2 possible username-password pairs, which are shown in the userMap data structure (in Server.java):<br>

1) user - password
2) root - password

The program does not provide for user registration so those shown are the only possible combinations for access to the Online Service.

<h2>Main features</h2>
Once logged in, the user can choose from several possible features, each capable of performing an operation on the list of products available on the server:

1) Buy<br>
Function to buy a product. <br>
Clicking on the relevant button will open an interface where it will be possible to view all the products available for purchase. To buy one, just select it with the cursor and press the "buy a product" button. You cannot purchase a product without first selecting it. 
It is possible to return to the main menu by clicking on the "back to main menu" button.

2) Return<br>
Function that allows you to return a previously purchased product. <br>
Clicking on the relevant button will open an interface where you can see all previously purchased products. To return one, you must first select it with the cursor and then press "return a product”. You cannot return a product without first selecting it. If no products have been purchased yet, it will be impossible to open the window to return products. 
It is possible to return to the main menu by clicking on the "back to main menu" button.

3) Add<br>
Function to add new products. <br>
Clicking on the relevant button will open an interface where you can see all the products currently in the list and possibly add new ones through the appropriate input fields.
If you intend to add a new product, you must specify name and price in the appropriate fields, and then press the "add" button. The price must be explicitly numeric and the fields must not be blank in order to proceed with adding a product.
It is possible to return to the main menu by clicking on the "back" button.

4) Quit<br>
Closes the connection between client and server.<br>
It is recommended to press it once all operations are finished.


<h2>Technical features</h2>
The application is based on two nodes: client and server. Their interaction is based on the use of sockets. A user can interact via the client with the server, after authenticating with his username and file password. The server is created with an initial set of products given by an input file (products.txt) and usernames and password. A product is described by: product name, price and identifier. Products are deleted from the server when they are transferred to the customer (client).

JavaFX is used for the GUI.
