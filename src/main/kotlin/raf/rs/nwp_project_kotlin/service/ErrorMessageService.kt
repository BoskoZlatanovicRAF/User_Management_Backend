package raf.rs.nwp_project_kotlin.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.errors.ErrorMessage
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import java.util.Optional

@Service
interface ErrorMessageService {
    fun logError(order: Order, operation: String, message: String)
    fun getErrorsForOrder(orderId: Long): Optional<ErrorMessage>
    fun getAllErrors(page: Int, size: Int): Page<ErrorMessage>
    fun getUserErrors(user: User, page: Int, size: Int): Page<ErrorMessage>
}