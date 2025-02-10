package raf.rs.nwp_project_kotlin.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import raf.rs.nwp_project_kotlin.model.errors.ErrorMessage
import raf.rs.nwp_project_kotlin.model.users.User

@Repository
interface ErrorMessageRepository : JpaRepository<ErrorMessage, Long> {
    fun findByOrderCreatedBy(user: User, pageable: Pageable): Page<ErrorMessage>
}