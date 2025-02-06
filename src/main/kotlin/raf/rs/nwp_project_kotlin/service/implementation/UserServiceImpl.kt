package raf.rs.nwp_project_kotlin.service.implementation

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.repository.PermissionRepository
import raf.rs.nwp_project_kotlin.repository.UserRepository
import raf.rs.nwp_project_kotlin.service.UserService
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService{

    override fun findAll(): List<User> {
        return userRepository.findAll()
    }

    override fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }


    override fun findByEmail(email: String): Optional<User> {
        return userRepository.findByEmail(email)
    }

    override fun save(user: User): User {
        if (user.id == null) {
            // For new user, encode password
            user.password = passwordEncoder.encode(user.password)

            // Get existing permissions from database
            val actualPermissions = user.permissions.mapNotNull { permission ->
                permissionRepository.findByType(permission.type).orElse(null)
            }.toMutableSet()

            // Set the actual permissions
            user.permissions = actualPermissions
        }
        return userRepository.save(user)
    }

    override fun update(id: Long, user: User): User? {
        return userRepository.findById(id).map { existingUser ->
            existingUser.firstName = user.firstName
            existingUser.lastName = user.lastName
            existingUser.email = user.email

            // Convert incoming permission types to actual Permission entities
            val updatedPermissions = user.permissions.mapNotNull { permission ->
                permissionRepository.findByType(permission.type).orElse(null)
            }.toMutableSet()

            existingUser.permissions = updatedPermissions

            userRepository.save(existingUser)
        }.orElse(null)
    }

    override fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }

    override fun loadUserByUsername(email: String?): UserDetails {
        // 5. pretrazuje usera po emailu
        val user = email?.let {
            findByEmail(it)
                .orElseThrow { UsernameNotFoundException("User not found with email: $email") }
        }

        // 6. konvertuje permisije u authority (ovo je cela poenta - ovde se nalaze sve permisije koje user ima)
        val authorities = user?.permissions?.map { permission ->
            SimpleGrantedAuthority(permission.type.name)
        }

        // Spring Security koristi ovo loaduje user details tokom autentifikacije
        // 7. Konvertuje nas model User u Spring Security UserDetails
        return org.springframework.security.core.userdetails.User(
            user?.email ?: "",
            user?.password ?: "",
            authorities
        )
    }
}