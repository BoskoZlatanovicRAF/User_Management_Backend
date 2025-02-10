package raf.rs.nwp_project_kotlin.service.implementation

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.errors.ErrorMessage
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.repository.ErrorMessageRepository
import raf.rs.nwp_project_kotlin.repository.OrderRepository
import raf.rs.nwp_project_kotlin.service.OrderService
import raf.rs.nwp_project_kotlin.websocket.controller.OrderWebSocketController
import raf.rs.nwp_project_kotlin.websocket.dto.OrderStatusUpdate
import java.time.LocalDateTime


@Service
class OrderServiceImpl(
    private val orderRepository: OrderRepository,
    private val errorMessageRepository: ErrorMessageRepository,
    private val orderWebSocketController: OrderWebSocketController
) : OrderService {


    override fun createOrder(order: Order): Order {
        val currentActiveOrders = orderRepository.countByStatusIn(
            listOf(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
        )
        if (currentActiveOrders >= 3) {
            throw RuntimeException("Maximum number of concurrent orders reached")
        }

        order.status = OrderStatus.ORDERED
        val savedOrder = orderRepository.save(order)

        // Eksplicitno učitaj order sa svim relacijama
        return orderRepository.findById(savedOrder.id!!)
            .orElseThrow { RuntimeException("Order not found after saving") }
    }

    override fun getAllOrders(page: Int, size: Int, status: OrderStatus?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?, userId: Long?): Page<Order>
    {
        val pageable = PageRequest.of(page, size)

        return when {
            status != null -> orderRepository.findByStatus(status, pageable)
            dateFrom != null && dateTo != null -> orderRepository.findByCreatedAtBetween(dateFrom, dateTo, pageable)
            userId != null -> orderRepository.findByCreatedById(userId, pageable)
            else -> orderRepository.findAll(pageable)
        }
    }

    override fun getUserOrders(user: User, page: Int, size: Int, status: OrderStatus?, dateFrom: LocalDateTime?, dateTo: LocalDateTime?): Page<Order>
    {
        val pageable = PageRequest.of(page, size)

        return when {
            status != null -> orderRepository.findByCreatedByAndStatus(user, status, pageable)
            dateFrom != null && dateTo != null ->
                orderRepository.findByCreatedByAndCreatedAtBetween(user, dateFrom, dateTo, pageable)
            else -> orderRepository.findByCreatedBy(user, pageable)
        }
    }
    override fun cancelOrder(id: Long) {
        val order = orderRepository.findById(id).orElseThrow()
        if (order.status != OrderStatus.ORDERED) {
            throw RuntimeException("Can only cancel orders in ORDERED status")
        }
        order.status = OrderStatus.CANCELED
        order.active = false
        orderRepository.save(order)
    }

    override fun updateOrderStatus(id: Long, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(id).orElseThrow()
        order.status = newStatus
        val savedOrder = orderRepository.save(order)

        // Šaljemo update preko WebSocket-a
        orderWebSocketController.sendOrderStatusUpdate(
            OrderStatusUpdate(
                orderId = savedOrder.id!!, // !! zato što je id nullable
                status = savedOrder.status
            )
        )

        return savedOrder
    }

    override fun getOrder(id: Long): Order =
        orderRepository.findById(id).orElseThrow { RuntimeException("Order not found") }

    override fun scheduleOrder(order: Order, scheduledTime: LocalDateTime): Order {
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw RuntimeException("Cannot schedule order in the past")
        }

        return orderRepository.save(order.copy(
            status = OrderStatus.ORDERED,
            scheduledFor = scheduledTime
        ))
    }

    @Scheduled(fixedDelay = 5000) // Proverava svakih 5 sekundi
    fun processOrderStatuses() {
        val now = LocalDateTime.now()
        val pageable = PageRequest.of(0, 10)

        orderRepository.findByStatus(OrderStatus.ORDERED, pageable).forEach { order ->
            if (order.scheduledFor == null && order.createdAt.plusSeconds(10) <= now) {
                updateOrderStatus(order.id!!, OrderStatus.PREPARING)
            }
        }

        orderRepository.findByStatus(OrderStatus.PREPARING, pageable).forEach { order ->
            if (order.statusUpdatedAt.plusSeconds(15) <= now) {
                updateOrderStatus(order.id!!, OrderStatus.IN_DELIVERY)
            }
        }

        orderRepository.findByStatus(OrderStatus.IN_DELIVERY, pageable).forEach { order ->
            if (order.statusUpdatedAt.plusSeconds(20) <= now) {
                updateOrderStatus(order.id!!, OrderStatus.DELIVERED)
            }
        }
    }

    @Scheduled(fixedDelay = 2000) // Proverava svakih 2 sekunde
    fun processScheduledOrders() {
        val now = LocalDateTime.now()
        // Pronalazi sve zakazane porudžbine čije je vreme došlo
        orderRepository.findByScheduledForBeforeAndStatus(now, OrderStatus.ORDERED)
            .forEach { order ->
                try {
                    val currentActiveOrders = orderRepository.countByStatusIn(
                        listOf(OrderStatus.PREPARING, OrderStatus.IN_DELIVERY)
                    )

                    if (currentActiveOrders >= 3) {
                        // Ako ima previše aktivnih porudžbina, evidentiraj grešku
                        errorMessageRepository.save(
                            ErrorMessage(
                                order = order,
                                operation = "SCHEDULE_EXECUTION",
                                message = "Maximum number of concurrent orders reached"
                            )
                        )
                        updateOrderStatus(order.id!!, OrderStatus.CANCELED)
                    } else {
                        // Inače započni obradu porudžbine
                        updateOrderStatus(order.id!!, OrderStatus.PREPARING)
                    }
                } catch (e: Exception) {
                    errorMessageRepository.save(
                        ErrorMessage(
                            order = order,
                            operation = "SCHEDULE_EXECUTION",
                            message = "Error processing scheduled order: ${e.message}"
                        )
                    )
                }
            }
    }

}