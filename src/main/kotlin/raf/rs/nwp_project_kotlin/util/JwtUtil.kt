package raf.rs.nwp_project_kotlin.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import raf.rs.nwp_project_kotlin.service.UserService
import java.util.*

@Component
class JwtUtil (
    private val userService: UserService
){
    @Value("\${jwt.secret}")
    private var secret: String = ""

    @Value("\${jwt.expiration}")
    private var expiration: Long = 0

    init {
        println("JWT Secret: $secret") // For debugging
    }

    fun generateJwt(authentication: Authentication): String {
        val user = userService.findByEmail(authentication.name)
            .orElseThrow { UsernameNotFoundException("User not found") }
        val claims: MutableMap<String, Any> = HashMap()
        claims["permissions"] = user.permissions.map { it.type }

        val token = Jwts.builder()
            .setClaims(claims)
            .setSubject(authentication.name)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
            .compact()

        return token

    }

    fun extractAllClaims(jwt: String): Claims {
        return Jwts.parser()
            .setSigningKey(secret.toByteArray())
            .parseClaimsJws(jwt)
            .body
    }

    fun extractUsername(jwt: String): String {
        return extractAllClaims(jwt).subject
    }

    fun isTokenExpired(jwt: String): Boolean {
        return extractAllClaims(jwt)
            .expiration
            .before(Date())
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }
}