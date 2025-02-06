package raf.rs.nwp_project_kotlin.websocket.dto

import raf.rs.nwp_project_kotlin.enums.OrderStatus
import java.time.LocalDateTime

data class OrderStatusUpdate(
    val orderId: Long,
    val status: OrderStatus,
    val timestamp: LocalDateTime = LocalDateTime.now()
)