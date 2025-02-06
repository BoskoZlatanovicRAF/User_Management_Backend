package raf.rs.nwp_project_kotlin.model.users

import jakarta.persistence.*
import raf.rs.nwp_project_kotlin.enums.PermissionType

@Entity
@Table(name = "permissions")
data class Permission(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    val type: PermissionType
){
    constructor() : this(null, PermissionType.CAN_READ_USERS)

    constructor(type: PermissionType) : this(id = null, type = type)
}
