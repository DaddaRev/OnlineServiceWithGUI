package OnlineServicePackage;

/**
 * Represents a product with a name, price, and unique identifier.
 * The class provides constructors for creating instances of the product,
 * as well as methods to retrieve information about the product such as its name,
 * price, and identifier.
 */
public class Product {
	
	private String name = null;
	private double price = 0;
	private int id = 0;
	private static int id_counter = 1000;  //Each new product created has an identifier that increases automatically
	
	public Product()
	{
		//Default constructor creating an invalid object.
	}

	/**
     * Constructs a product with the specified name and price.
     * The product is assigned a unique identifier.
     *
     * @param n    The name of the product.
     * @param d    The price of the product.
     */
	public Product(String n, double d) {
		this.name = n;
		this.price = d;
		this.id = id_counter;
		Product.id_counter ++; 		//Automatic id setting on each new product
	}
	
    /**
     * Constructs a product with the specified name, price, and identifier.
     *
     * @param n   The name of the product.
     * @param d   The price of the product.
     * @param id  The unique identifier of the product.
     */
	public Product(String n, double d, int id)
	{
		this.name = n;
		this.price = d;
		this.id = id;
	}
	
    /**
     * Gets the name of the product.
     *
     * @return The name of the product.
     */
	public String getName()
	{
		return this.name;
	}
	
    /**
     * Gets the price of the product.
     *
     * @return The price of the product.
     */
	public double getPrice()
	{
		return this.price;
	}
	
    /**
     * Gets the unique identifier of the product.
     *
     * @return The unique identifier of the product.
     */
	public int getId()
	{
		return this.id;
	}
	
    /**
     * Returns a string representation of the product.
     * The string includes the name, price, and identifier of the product.
     *
     * @return A string representation of the product.
     */
	@Override
    public String toString() {
        return name + "  $" + price + "  "+ id;
    }
	
}
