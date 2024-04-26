import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public final class OrderBookTests
{
    private Map<Long, Order> allOrders;

    @BeforeEach
    public void Setup()
    {
        allOrders = new HashMap<>(Map.of(
                1L, new Order(1L, 100, 'B', 1000),
                2L, new Order(2L, 100, 'B', 2000),
                3L, new Order(3L, 101, 'B', 2000)
        ));

    }

    @Test
    public void canAddOrderWhenBid()
    {
        OrderBook book = new OrderBook(allOrders);

        Order order = new Order(4, 101, 'B', 1000);
        book.addOrder(order);

        assertEquals(4, allOrders.size());

        // Check order added to correct side ...
        List<Order> bidOrders = book.getOrders('B');
        assertEquals(4, bidOrders.size());
        assertTrue(bidOrders.contains(order));

        List<Order> offerOrders = book.getOrders('O');
        assertEquals(0, offerOrders.size());

        // Check new joint highest ...
        double priceForLevel = book.getPriceForLevel('B', 1);
        assertEquals(101.0, priceForLevel, 0.001);
    }

    @Test
    public void canAddOrderWhenOffer()
    {
        OrderBook book = new OrderBook(allOrders);

        Order order = new Order(4, 101, 'O', 1000);
        book.addOrder(order);

        assertEquals(4, allOrders.size());

        // Check order added to correct side ...
        List<Order> bidOrders = book.getOrders('B');
        assertEquals(3, bidOrders.size());

        List<Order> offerOrders = book.getOrders('O');
        assertEquals(1, offerOrders.size());
        assertTrue(offerOrders.contains(order));
    }


    @Test
    public void canAddOrderWhenBidNewHigh()
    {
        OrderBook book = new OrderBook(allOrders);

        Order order = new Order(4, 102, 'B', 1000);
        book.addOrder(order);

        assertEquals(4, allOrders.size());

        List<Order> bidOrders = book.getOrders('B');
        assertEquals(4, bidOrders.size());
        assertTrue(bidOrders.contains(order));

        List<Order> offerOrders = book.getOrders('O');
        assertEquals(0, offerOrders.size());
    }


    @Test
    public void canRemoveOrder()
    {
        OrderBook book = new OrderBook(allOrders);

        book.removeOrder(2);

        assertEquals(2, allOrders.size());
        assertFalse(allOrders.containsKey(2L));
    }


    @Test
    public void canHandleNonExistingOrderWhenRemoveOrder()
    {
        OrderBook book = new OrderBook(allOrders);

        book.removeOrder(5);

        assertEquals(3, allOrders.size());
    }


    @Test
    public void canUpdateOrder()
    {
        OrderBook book = new OrderBook(allOrders);

        book.updateOrderSize(2, 3000);

        assertEquals(3, allOrders.size());
        assertTrue(allOrders.containsKey(2L));
        assertEquals(3000, allOrders.get(2L).getSize());
    }


    @Test
    public void canHandleNonExistantOrderWhenUpdateOrder()
    {
        OrderBook book = new OrderBook(allOrders);

        book.updateOrderSize(9, 3000);

        assertEquals(3, allOrders.size());
        assertFalse(allOrders.containsKey(9L));
    }


    @Test
    public void canGetPriceForLevel()
    {
        OrderBook book = new OrderBook(allOrders);

        double price = book.getPriceForLevel('B', 1);

        assertEquals(101.0, price, 0.001);
    }


    @Test
    public void canHandleNonExistantLevelWhenGetPriceForLevel()
    {
        OrderBook book = new OrderBook(allOrders);

        assertThrows(IndexOutOfBoundsException.class, () -> book.getPriceForLevel('B', 6));
    }


    @Test
    public void canGetSizeOfBucketForLevel()
    {
        OrderBook book = new OrderBook(allOrders);

        int size = book.getSizeofBucket('B', 2);

        assertEquals(2, size);
    }


    @Test
    public void canGetSizeOfBucketForLevelWhenBookSideEmpty()
    {
        OrderBook book = new OrderBook(allOrders);

        int size = book.getSizeofBucket('O', 2);

        assertEquals(0, size);
    }


    @Test
    public void canGetOrdersForSide()
    {
        OrderBook book = new OrderBook(allOrders);

        List<Order> orders = book.getOrders('B');

        assertEquals(3, orders.size());
        assertEquals(3L, orders.get(0).getId());
        assertEquals(1L, orders.get(1).getId());
        assertEquals(2L, orders.get(2).getId());
    }


    @Test
    public void canGetOrdersForSideWhenSideEmpty()
    {
        OrderBook book = new OrderBook(allOrders);

        List<Order> orders = book.getOrders('O');

        assertEquals(0, orders.size());
    }
}
