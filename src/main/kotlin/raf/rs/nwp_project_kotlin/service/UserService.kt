package raf.rs.nwp_project_kotlin.service

import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.User
import java.util.*

@Service
interface UserService : UserDetailsService{
    fun findAll(): List<User>
    fun findById(id: Long) : Optional<User>
    fun findByEmail(email: String) : Optional<User>
    fun save(user: User) : User
    fun update(id: Long, user: User): User?  // Add this method
    fun deleteById(id: Long)
}