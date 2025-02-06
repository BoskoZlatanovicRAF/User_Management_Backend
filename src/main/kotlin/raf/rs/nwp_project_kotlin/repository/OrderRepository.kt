package raf.rs.nwp_project_kotlin.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import java.time.LocalDateTime

@Repository
interface OrderRepository : JpaRepository<Order, Long> {
    fun findByCreatedBy(user: User, pageable: Pageable): Page<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun countByStatusIn(statuses: List<OrderStatus>): Int
    fun findByScheduledForBeforeAndStatus(
        scheduledFor: LocalDateTime,
        status: OrderStatus
    ): List<Order>
}