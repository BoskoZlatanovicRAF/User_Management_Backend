package raf.rs.nwp_project_kotlin.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import java.time.LocalDateTime

@Service
interface OrderService {
    fun createOrder(order: Order): Order
    fun getUserOrders(user: User, page: Int, size: Int): Page<Order>
    fun getAllOrders(page: Int, size: Int): Page<Order>
    fun cancelOrder(id: Long)
    fun updateOrderStatus(id: Long, newStatus: OrderStatus): Order
    fun getOrder(id: Long): Order
    fun scheduleOrder(order: Order, scheduledTime: LocalDateTime): Order
}