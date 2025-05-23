package com.eShop.service.order;

import com.eShop.dto.OrderDto;
import com.eShop.model.order.Order;

import java.util.List;

public interface IOrderService {

    Order placeOrder(Long userId);

    OrderDto getOrderById(long orderId);

    List<OrderDto> getOrderByUserId(Long userId);

    OrderDto convertToOrderDto(Order order);
}