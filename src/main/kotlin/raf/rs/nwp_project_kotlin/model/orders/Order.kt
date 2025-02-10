package raf.rs.nwp_project_kotlin.model.orders

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.users.User
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
@JsonIgnoreProperties(value = ["createdBy"], allowSetters = true)
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Version
    val version: Long = 0,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus,

    @ManyToOne
    @JoinColumn(name = "created_by")
    val createdBy: User,

    var active: Boolean,

    @ManyToMany
    @JoinTable(
        name = "order_dishes",
        joinColumns = [JoinColumn(name = "order_id")],
        inverseJoinColumns = [JoinColumn(name = "dish_id")]
    )
    val items: List<Dish>,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val scheduledFor: LocalDateTime? = null,
    var statusUpdatedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this(
        id = null,
        version = 0,
        status = OrderStatus.ORDERED,
        createdBy = User(),
        active = true,
        items = emptyList(),
        scheduledFor = null
    )
}


/*
// Version = 0
val order = orderRepository.findById(1).get()

// Neko drugi u međuvremenu menja isti order
val order2 = orderRepository.findById(1).get()
order2.status = OrderStatus.CANCELED
orderRepository.save(order2)  // Version = 1

// Pokušaj da se sačuva prva verzija će fail-ovati
order.status = OrderStatus.DELIVERED
orderRepository.save(order)  // Throws OptimisticLockException jer je version već 1
 */
