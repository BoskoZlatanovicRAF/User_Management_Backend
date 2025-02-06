package raf.rs.nwp_project_kotlin.model.errors

import jakarta.persistence.*
import raf.rs.nwp_project_kotlin.model.orders.Order
import java.time.LocalDateTime

@Entity
data class ErrorMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val date: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "order_id")
    val order: Order,

    val operation: String,
    val message: String
)