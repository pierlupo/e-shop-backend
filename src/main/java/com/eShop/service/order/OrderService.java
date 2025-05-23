package com.eShop.service.order;

import com.eShop.dto.OrderDto;
import com.eShop.dto.OrderItemDto;
import com.eShop.exceptions.ResourceNotFoundException;
import com.eShop.model.Cart;
import com.eShop.model.Product;
import com.eShop.model.order.Order;
import com.eShop.model.order.OrderItem;
import com.eShop.model.order.OrderStatus;
import com.eShop.repository.OrderRepository;
import com.eShop.repository.ProductRepository;
import com.eShop.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Override
    public Order placeOrder(Long userId) {

        Cart cart = cartService.getCartByUserId(userId);

        Order order = createOrder(cart);
        List<OrderItem> orderItemList = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItemList));
        order.setTotalAmount(calculateTotalAmount(orderItemList));
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(cart.getId());

        return savedOrder;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart) {
        return cart.getItems()
                .stream()
                .map(cartItem -> {
                   Product product = cartItem.getProduct();
                   product.setInventory(product.getInventory() - cartItem.getQuantity());
                   productRepository.save(product);
                   return new OrderItem(
                           order,
                           product,
                           cartItem.getQuantity(),
                           cartItem.getUnitPrice()
                   );
                }).toList();
    }

    private Order createOrder(Cart cart) {

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;

    }

    private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
        return orderItemList
                .stream()
                .map(item-> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto getOrderById(long orderId) {
        return orderRepository.findById(orderId)
                .map(this :: convertToOrderDto)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public List<OrderDto> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return  orders.stream().map(this :: convertToOrderDto).toList();
    }

    @Override
    public OrderDto convertToOrderDto(Order order) {
        OrderDto orderDto = modelMapper.map(order, OrderDto.class);
        // Manual fix for nested product fields in order items
        List<OrderItemDto> orderItemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemDto dto = new OrderItemDto();
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getProductName());
            dto.setBrand(item.getProduct().getBrand());
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            return dto;
        }).toList();

        orderDto.setItems(orderItemDtos);
        return orderDto;
    }
}