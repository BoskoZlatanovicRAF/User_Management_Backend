package raf.rs.nwp_project_kotlin.dto

import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.orders.Dish
import java.time.LocalDateTime

data class OrderDTO(
    val id: Long?,
    val status: OrderStatus,
    val createdBy: UserDTO,
    val active: Boolean,
    val items: List<Dish>,
    val createdAt: LocalDateTime,
    val scheduledFor: LocalDateTime?
)
