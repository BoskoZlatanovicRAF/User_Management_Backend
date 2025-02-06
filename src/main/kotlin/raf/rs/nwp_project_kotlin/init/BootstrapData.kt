package raf.rs.nwp_project_kotlin.init

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import raf.rs.nwp_project_kotlin.enums.PermissionType
import raf.rs.nwp_project_kotlin.model.users.Permission
import raf.rs.nwp_project_kotlin.model.users.User
import raf.rs.nwp_project_kotlin.repository.PermissionRepository
import raf.rs.nwp_project_kotlin.service.UserService


@Component
class BootstrapData (
    private val permissionRepository: PermissionRepository,
    private val userService: UserService
) : CommandLineRunner{


    override fun run(vararg args: String?) {
        for (type in PermissionType.entries){
            if(permissionRepository.findByType(type).isEmpty){
                val permission = Permission(type)
                permissionRepository.save(permission)
            }
        }

        // create admin user
        userService.findByEmail("admin@example.com").isEmpty.takeIf { it }?.let {
            userService.save(
                User(
                    email = "admin@example.com",
                    password = "admin",
                    firstName = "Admin",
                    lastName = "Admin",
                    permissions = permissionRepository.findAll().toMutableSet()
                )
            )
        }
    }
}