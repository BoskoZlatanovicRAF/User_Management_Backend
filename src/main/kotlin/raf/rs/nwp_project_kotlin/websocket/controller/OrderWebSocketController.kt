package raf.rs.nwp_project_kotlin.websocket.controller

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import raf.rs.nwp_project_kotlin.websocket.dto.OrderStatusUpdate

@Controller
class OrderWebSocketController(
    private val simpMessagingTemplate: SimpMessagingTemplate
) {
    fun sendOrderStatusUpdate(update: OrderStatusUpdate) {
        simpMessagingTemplate.convertAndSend("/topic/orders/${update.orderId}", update)
    }
}