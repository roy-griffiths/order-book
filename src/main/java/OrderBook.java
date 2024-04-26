import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import com.google.common.base.Preconditions;


public final class OrderBook
{
    private static final char BidSide = 'B';
    private static final char OfferSide = 'O';

    private final Map<Long, Order> allOrders;
    private final SortedMap<Double, List<Long>> bids;
    private final SortedMap<Double, List<Long>> offers;


    private SortedMap<Double, List<Long>> getOrdersForSide(char side)
    {
        SortedMap<Double, List<Long>> sideOrders;

        if (side == BidSide)
        {
            sideOrders = bids;
        }
        else if (side == OfferSide)
        {
            sideOrders = offers;
        }
        else
        {
            throw new IllegalArgumentException("Unknown order side " + side);
        }

        return sideOrders;
    }

    private List<Long> getBucketForPrice(Double price, SortedMap<Double, List<Long>> sideOrders)
    {
        // If bucket does not exist, create it, before returning bucket
        return sideOrders.computeIfAbsent(price, k -> new ArrayList<>());
    }

    /**
     * Constructor.
     */
    public OrderBook()
    {
        this(new HashMap<>());
    }

    public OrderBook(Map<Long, Order> allOrders)
    {
        this.allOrders = allOrders;

        bids = new TreeMap<>(Comparator.reverseOrder());
        offers = new TreeMap<>();

        for(Order o : allOrders.values())
        {
            addOrder(o);
        }
    }

    /**
     * Adds an order to the order bok.
     * @param order The order to add.
     */
    public void addOrder(Order order)
    {
        allOrders.put(order.getId(), order);

        SortedMap<Double, List<Long>> sideOrders = getOrdersForSide(order.getSide());

        List<Long> ordersForPrice = getBucketForPrice(order.getPrice(), sideOrders);
        ordersForPrice.add(order.getId());
    }

    /**
     * Remives an order from the order book.
     * @param orderId Identifies the order to remove.
     */
    public void removeOrder(long orderId)
    {
        Order order = allOrders.remove(orderId);

        if (order != null)
        {
            if (order.getSide() == BidSide)
            {
                List<Long> bidsForPrice = bids.get(order.getPrice());

                if (bidsForPrice != null)
                {
                    bidsForPrice.remove(order.getId());     // O(n) operation, but not too many orders at same price
                }
            }
            else if (order.getSide() == OfferSide)
            {
                List<Long> offersForPrice = offers.get(order.getPrice());

                if (offersForPrice != null)
                {
                    offersForPrice.remove(order.getId());     // O(n) operation, but not too many orders at same price
                }
            }
            else
            {
                throw new IllegalArgumentException("Unknown order side " + order.getSide());
            }
        }
    }

    /**
     * Updates the newSize of an existing order.
     * @param orderId Identifies the order to update.
     * @param newSize The new newSize of the order.
     */
    public void updateOrderSize(long orderId, long newSize)
    {
        Order existingOrder = allOrders.get(orderId);

        if (existingOrder != null)
        {
            Order newOrder = new Order(orderId, existingOrder.getPrice(), existingOrder.getSide(), newSize);
            allOrders.put(orderId, newOrder);
        }
    }

    /**
     * Gets the price associated with a level in the order book.
     * @param side The side of the order.
     * @param level The level for which to obtain the price.
     * @return The price.
     */
    public double getPriceForLevel(char side, int level)
    {
        Preconditions.checkArgument(level > 0, "Level must be larger than 0");

        SortedMap<Double, List<Long>> sideOrders = getOrdersForSide(side);

        List<Double> prices = new ArrayList<>(sideOrders.keySet());

        return prices.get(level - 1);
    }

    /**
     * Gets the number of orders for a given price in the order book.
     * @param side The side of the order.
     * @param level The level for which to obtain the size.
     * @return The number of orders at the price point.
     */
    public int getSizeofBucket(char side, int level)
    {
        Preconditions.checkArgument(level > 0, "Level must be larger than 0");

        SortedMap<Double, List<Long>> sideOrders = getOrdersForSide(side);

        List<Double> bucket = new ArrayList<>(sideOrders.keySet());
        return bucket.size();
    }

    /**
     * Gets the orders for the specified side, sorted by price and time.
     * @param side The side of the orders.
     * @return The orders.
     */
    public List<Order> getOrders(char side)
    {
        SortedMap<Double, List<Long>> sideOrders = getOrdersForSide(side);

        List<Long> orderIds = new ArrayList<>();

        for (List<Long> bucket : sideOrders.values())
        {
            orderIds.addAll(bucket);
        }

        return orderIds.stream().map(allOrders::get).collect(Collectors.toList());
    }
}
