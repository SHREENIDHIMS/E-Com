package com.app.ecom.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Order;
import com.app.ecom.model.OrderItem;
import com.app.ecom.model.OrderStatus;
import com.app.ecom.model.User;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {
	
	private final CartService cartService;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;

	public Optional<OrderResponse> createOrder(String userId) {
		
		List<CartItem> cartItems = cartService.getCart(userId);
		if(cartItems.isEmpty()) {
			return Optional.empty();
		}
		
		Optional<User> userOptinal = userRepository.findById(Long.valueOf(userId));
		if(userOptinal.isEmpty()) {
			return Optional.empty();

		}
		User user = userOptinal.get();
		
		BigDecimal totalPrice = cartItems.stream()
				.map(CartItem::getPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
				
		
		Order order = new Order();
		order.setUser(user);
		order.setStatus(OrderStatus.CONFIRMERD);
		order.setTotalAmount(totalPrice);
		
		List<OrderItem> orderItems = cartItems.stream()
				.map(item -> new OrderItem(
						null,
						item.getProduct(),
						item.getQuantity(),
						item.getPrice(),
						order))
						.toList();
		
		order.setItems(orderItems);
		Order savedOrder = orderRepository.save(order);
		
		
		cartService.clearCart(userId);
		
		return Optional.of(mapToOrderResponse(savedOrder));
	}

	private OrderResponse mapToOrderResponse(Order order) {
	    return new OrderResponse(
	            order.getId(),
	            order.getTotalAmount(),
	            order.getStatus(),
	            order.getItems().stream()
	                    .map(orderItem -> new OrderItemDTO(
	                            orderItem.getId(),
	                            orderItem.getProduct().getId(),
	                            orderItem.getQuantity(),
	                            orderItem.getPrice(),
	                            orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
	                    ))
	                    .toList(),
	                    order.getCreatedAt()
	                  
	    );
	}

}
