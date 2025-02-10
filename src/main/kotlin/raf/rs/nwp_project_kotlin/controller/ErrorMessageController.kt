package raf.rs.nwp_project_kotlin.controller

import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.dto.ErrorMessageDTO
import raf.rs.nwp_project_kotlin.service.ErrorMessageService
import raf.rs.nwp_project_kotlin.service.UserService
import raf.rs.nwp_project_kotlin.util.toErrorMessageDTO
import java.util.Optional

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/errors")
class ErrorMessageController(
    private val errorMessageService: ErrorMessageService,
    private val userService: UserService
) {

    @GetMapping
    fun getErrors(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<ErrorMessageDTO>> {
        val currentUser = getCurrentUser()
        val errors = if (currentUser.email == "admin@example.com") {
            errorMessageService.getAllErrors(page, size)
        } else {
            errorMessageService.getUserErrors(currentUser, page, size)
        }

        val errorDTOs = errors.map { toErrorMessageDTO(it) } // Konvertujemo u DTO

        return ResponseEntity.ok(errorDTOs)
    }


    @GetMapping("/order/{orderId}")
    fun getErrorsForOrder(@PathVariable orderId: Long): ResponseEntity<Optional<ErrorMessageDTO>> {
        val error = errorMessageService.getErrorsForOrder(orderId)
        return ResponseEntity.ok(error.map { toErrorMessageDTO(it) }) // Konvertujemo u DTO
    }

    private fun getCurrentUser() =
        userService.findByEmail(SecurityContextHolder.getContext().authentication.name)
            .orElseThrow()
}