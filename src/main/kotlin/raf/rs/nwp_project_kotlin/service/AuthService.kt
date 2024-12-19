package raf.rs.nwp_project_kotlin.service

import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.request.LoginRequest
import raf.rs.nwp_project_kotlin.response.LoginResponse

@Service
interface AuthService {
    fun login(loginRequest: LoginRequest) : LoginResponse
}