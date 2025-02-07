package raf.rs.nwp_project_kotlin.dto

import raf.rs.nwp_project_kotlin.model.orders.Dish
import java.time.LocalDateTime

data class CreateOrderRequest(
    val items: List<Dish>,
    val scheduledFor: LocalDateTime? = null
)
