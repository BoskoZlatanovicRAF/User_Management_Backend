package raf.rs.nwp_project_kotlin.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.request.LoginRequest
import raf.rs.nwp_project_kotlin.response.LoginResponse
import raf.rs.nwp_project_kotlin.service.AuthService

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(authService.login(loginRequest))
    }
}
