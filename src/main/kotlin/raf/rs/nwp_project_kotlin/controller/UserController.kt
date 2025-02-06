package raf.rs.nwp_project_kotlin.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.service.UserService

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    @PreAuthorize("hasAuthority('CAN_READ_USERS')")
    fun getAllUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.findAll())
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_READ_USERS')")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        return userService.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(userService.save(user))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_UPDATE_USERS')")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val existingUser = userService.findById(id).orElse(null) ?:
        return ResponseEntity.notFound().build()

        if (currentUser != "admin@example.com") {
            user.permissions = existingUser.permissions
        }

        return userService.update(id, user)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CAN_DELETE_USERS')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Unit> {
        if (!userService.findById(id).isPresent) {
            return ResponseEntity.notFound().build()
        }
        userService.deleteById(id)
        return ResponseEntity.ok().build()
    }

}