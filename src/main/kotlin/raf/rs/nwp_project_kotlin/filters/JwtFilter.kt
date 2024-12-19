package raf.rs.nwp_project_kotlin.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import raf.rs.nwp_project_kotlin.util.JwtUtil

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    @Qualifier("userServiceImpl") private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    // interceptuje svaki request
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 1. proverava da li ima jwt
        val authHeader = request.getHeader("Authorization")


        var jwt: String? = null
        var userEmail: String? = null

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7)


            try {
                // 2. dobijamo email iz tokena koristeci funkciju iz jwtUtil
                userEmail = jwtUtil.extractUsername(jwt)
            }catch (e: Exception){
                println("JWT Error: ${e.message}")
            }
        }

        // 3. cekira da li postoji email i da nema authentication
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            // 4. loaduje user details iz UserService
            val userDetails = userDetailsService.loadUserByUsername(userEmail)


            // 8. validira token
            if (jwtUtil.validateToken(jwt!!, userDetails)) {
                // 9. pravi token sa authorities
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }
                // 10. setuje authentication u SecurityContextu, odavde sledece se vraca na controller i onda na service
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }
}