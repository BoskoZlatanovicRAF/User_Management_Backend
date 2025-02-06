package raf.rs.nwp_project_kotlin.controller

import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.service.OrderService
import raf.rs.nwp_project_kotlin.service.UserService
import java.time.LocalDateTime

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
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Order>> {
        val currentUser = getCurrentUser()
        val orders = if (currentUser.email == "admin@example.com") {
            orderService.getAllOrders(page, size)
        } else {
            orderService.getUserOrders(currentUser, page, size)
        }
        return ResponseEntity.ok(orders)
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CAN_PLACE_ORDER')")
    fun createOrder(@RequestBody order: Order): ResponseEntity<Order> {
        val currentUser = getCurrentUser()

        return ResponseEntity.ok(orderService.createOrder(order.copy(createdBy = currentUser)))
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
    fun trackOrder(@PathVariable id: Long): ResponseEntity<Order> {
        val currentUser = getCurrentUser()

        val order = orderService.getOrder(id)

        if (currentUser.email != "admin@example.com" && order.createdBy.id != currentUser.id) {
            throw RuntimeException("You can only track your own orders")
        }

        return ResponseEntity.ok(order)
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAuthority('CAN_SCHEDULE_ORDER')")
    fun scheduleOrder(@RequestBody order: Order, @RequestParam scheduledTime: LocalDateTime): ResponseEntity<Order> {
        val currentUser = getCurrentUser()

        val orderWithUser = order.copy(createdBy = currentUser)
        return ResponseEntity.ok(orderService.scheduleOrder(orderWithUser, scheduledTime))
    }


    private fun getCurrentUser() =
        userService.findByEmail(SecurityContextHolder.getContext().authentication.name)
            .orElseThrow()
}