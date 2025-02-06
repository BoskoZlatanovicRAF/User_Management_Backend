package raf.rs.nwp_project_kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import raf.rs.nwp_project_kotlin.enums.PermissionType
import raf.rs.nwp_project_kotlin.model.users.Permission
import java.util.Optional

interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByType(type: PermissionType): Optional<Permission>
}