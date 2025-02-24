package com.future.orderservice.controller;

import com.future.orderservice.payload.*;
import com.future.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order-service")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody(required = true) OrderRequestDTO orderRequestDTO) {
        return new ResponseEntity<>(orderService.createOrder(orderRequestDTO), HttpStatus.OK);
    }

    @PutMapping("/order/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable long id,
                                                        @RequestBody(required = true) OrderRequestDTO orderRequestDTO) {
        return new ResponseEntity<>(orderService.updateOrder(id, orderRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/order-cancellation/{id}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable long id,
                                                @RequestBody(required = true) OrderCancellationDTO orderCancellationDTO) {
        return new ResponseEntity<>(orderService.cancelOrder(id, orderCancellationDTO), HttpStatus.OK);
    }

    @PostMapping("/user/{userId}/order-confirmation/{orderId}")
    public ResponseEntity<OrderDTO> confirmOrder(@PathVariable long userId, @PathVariable long orderId,
                                                 @RequestBody(required = true) BankCardInfoDTO bankCardInfoDTO) {
        return new ResponseEntity<>(orderService.confirmOrder(orderId, userId, bankCardInfoDTO), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/order/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable long userId, @PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrder(orderId, userId), HttpStatus.OK);
    }

    @PostMapping("/order/customized-querying")
    public ResponseEntity<Page<OrderDTO>> getFilteredOrders(@RequestBody(required = true) OrderQueryDTO orderQueryDTO,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int pageSize) {
        return new ResponseEntity<>(orderService.getFilteredOrders(orderQueryDTO, page, pageSize), HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<Page<OrderDTO>> getOrderByUserId(@RequestParam(required = true) Long userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "0") int pageSize) {
        return new ResponseEntity<>(orderService.getOrderByUserId(userId, page, pageSize), HttpStatus.OK);
    }

}
