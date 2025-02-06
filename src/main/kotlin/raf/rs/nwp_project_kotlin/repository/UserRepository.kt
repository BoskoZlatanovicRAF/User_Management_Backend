package raf.rs.nwp_project_kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import raf.rs.nwp_project_kotlin.model.users.User
import java.util.*

interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
}