package raf.rs.nwp_project_kotlin.service.implementation

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.errors.ErrorMessage
import raf.rs.nwp_project_kotlin.model.orders.Order
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.repository.ErrorMessageRepository
import raf.rs.nwp_project_kotlin.service.ErrorMessageService
import java.util.*

@Service
class ErrorMessageServiceImpl(
    private val errorMessageRepository: ErrorMessageRepository
) : ErrorMessageService {
    override fun logError(order: Order, operation: String, message: String) {
        val error = ErrorMessage(
            order = order,
            operation = operation,
            message = message
        )
        errorMessageRepository.save(error)
    }

    override fun getErrorsForOrder(orderId: Long): Optional<ErrorMessage> =
        errorMessageRepository.findById(orderId)


//    override fun getErrorsForOrder(order: Order): List<ErrorMessage> =
//        errorMessageRepository.findByOrder(order)

    override fun getAllErrors(page: Int, size: Int): Page<ErrorMessage> =
        errorMessageRepository.findAll(PageRequest.of(page, size))

    override fun getUserErrors(user: User, page: Int, size: Int): Page<ErrorMessage> =
        errorMessageRepository.findByOrderCreatedBy(user, PageRequest.of(page, size))
}