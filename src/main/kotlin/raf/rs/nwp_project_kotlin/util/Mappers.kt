package raf.rs.nwp_project_kotlin.util

import raf.rs.nwp_project_kotlin.dto.ErrorMessageDTO
import raf.rs.nwp_project_kotlin.dto.OrderDTO
import raf.rs.nwp_project_kotlin.dto.UserDTO
import raf.rs.nwp_project_kotlin.model.errors.ErrorMessage
import raf.rs.nwp_project_kotlin.model.orders.Order

fun toOrderDTO(order: Order): OrderDTO {
    return OrderDTO(
        id = order.id,
        status = order.status,
        createdBy = UserDTO(order.createdBy.id, order.createdBy.email),  // Dodaj eksplicitno korisnika
        active = order.active,
        items = order.items,
        createdAt = order.createdAt,
        scheduledFor = order.scheduledFor
    )
}
fun toErrorMessageDTO(errorMessage: ErrorMessage): ErrorMessageDTO {
    return ErrorMessageDTO(
        id = errorMessage.id,
        date = errorMessage.date,
        order = toOrderDTO(errorMessage.order), // Konvertujemo Order u OrderDTO
        operation = errorMessage.operation,
        message = errorMessage.message
    )
}
