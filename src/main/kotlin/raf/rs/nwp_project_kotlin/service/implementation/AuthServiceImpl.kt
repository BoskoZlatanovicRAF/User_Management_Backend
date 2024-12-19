package raf.rs.nwp_project_kotlin.service.implementation

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.request.LoginRequest
import raf.rs.nwp_project_kotlin.response.LoginResponse
import raf.rs.nwp_project_kotlin.service.AuthService
import raf.rs.nwp_project_kotlin.util.JwtUtil

@Service
class AuthServiceImpl(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil : JwtUtil
) : AuthService{



    override fun login(loginRequest: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.email,
                loginRequest.password
            )
        )

        return LoginResponse(jwtUtil.generateJwt(authentication))
    }
}