package raf.rs.nwp_project_kotlin.controller

import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.dto.CreateOrderRequest
import raf.rs.nwp_project_kotlin.dto.OrderDTO
import raf.rs.nwp_project_kotlin.enums.OrderStatus
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.service.OrderService
import raf.rs.nwp_project_kotlin.service.UserService
import raf.rs.nwp_project_kotlin.util.toOrderDTO
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
    private val userService: UserService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('CAN_SEARCH_ORDER')")
    fun getOrders(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) dateFrom: String?,
        @RequestParam(required = false) dateTo: String?,
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<Page<OrderDTO>> {
        val currentUser = getCurrentUser()
        val fromDateTime = dateFrom?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
        val toDateTime = dateTo?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }

        val orders = if (currentUser.email == "admin@example.com") {
            orderService.getAllOrders(page, size, status, fromDateTime, toDateTime, userId)
        } else {
            orderService.getUserOrders(currentUser, page, size, status, fromDateTime, toDateTime)
        }

        return ResponseEntity.ok(orders.map { toOrderDTO(it) })
    }


    @PostMapping
    @PreAuthorize("hasAuthority('CAN_PLACE_ORDER')")
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<Order> {
        val currentUser = getCurrentUser()
        val newOrder = Order(
            status = OrderStatus.ORDERED,
            createdBy = currentUser,
            active = true,
            items = request.items,
            scheduledFor = null
        )
        return ResponseEntity.ok(orderService.createOrder(newOrder))
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('CAN_CANCEL_ORDER')")
    fun cancelOrder(@PathVariable id: Long): ResponseEntity<Unit> {
        val currentUser = getCurrentUser()

        val order = orderService.getOrder(id)
        if (currentUser.email != "admin@example.com" && order.createdBy.id != currentUser.id) {
            throw RuntimeException("You can only cancel your own orders")
        }

        orderService.cancelOrder(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{id}/track")
    @PreAuthorize("hasAuthority('CAN_TRACK_ORDER')")
    fun trackOrder(@PathVariable id: Long): ResponseEntity<OrderDTO> {  // Menjamo tip povratne vrednosti u OrderDTO
        val currentUser = getCurrentUser()

        val order = orderService.getOrder(id)
        if (currentUser.email != "admin@example.com" && order.createdBy.id != currentUser.id) {
            throw RuntimeException("You can only track your own orders")
        }

        return ResponseEntity.ok(toOrderDTO(order))  // Konvertujemo Order u OrderDTO
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('CAN_SCHEDULE_ORDER')")
    fun scheduleOrder(
        @RequestBody request: CreateOrderRequest,
        @RequestParam scheduledTime: String  // String jer nije radilo kad se salje kao LocalDateTime zbog Angulara, da bi prebacio u ISO
    ): ResponseEntity<OrderDTO> {
        val currentUser = getCurrentUser()
        val dateTime = LocalDateTime.parse(scheduledTime, DateTimeFormatter.ISO_DATE_TIME)

        val newOrder = Order(
            status = OrderStatus.ORDERED,
            createdBy = currentUser,
            active = true,
            items = request.items,
            scheduledFor = dateTime
        )

        val scheduledOrder = orderService.scheduleOrder(newOrder, dateTime)
        return ResponseEntity.ok(toOrderDTO(scheduledOrder))
    }

    private fun getCurrentUser() =
        userService.findByEmail(SecurityContextHolder.getContext().authentication.name)
            .orElseThrow()
}