package raf.rs.nwp_project_kotlin.dto

import java.time.LocalDateTime

data class ErrorMessageDTO(
    val id: Long?,
    val date: LocalDateTime,
    val order: OrderDTO,
    val operation: String,
    val message: String
)
