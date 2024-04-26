public class Order
{
    private final long id;        // id of order
    private final double price;
    private final char side;      // B "Bid" or O "Offer"
    private final long size;

    public Order (long id, double price, char side, long size)
    {
        this.id = id ;
        this.price = price;
        this.size = size;
        this.side = side;
    }

    public long getId()
    {
        return id ;
    }

    public double getPrice()
    {
        return price;
    }

    public long getSize()
    {
        return size;
    }

    public char getSide()
    {
        return side;
    }
}