package com.dailycodework.dreamshops.service.order;

import com.dailycodework.dreamshops.dto.OrderDto;
import com.dailycodework.dreamshops.model.order.Order;

import java.util.List;

public interface IOrderService {

    Order placeOrder(Long userId);

    OrderDto getOrderById(long orderId);

    List<OrderDto> getOrderByUserId(Long userId);

    OrderDto convertToOrderDto(Order order);
}