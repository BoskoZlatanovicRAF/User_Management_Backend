package raf.rs.nwp_project_kotlin.enums

enum class PermissionType {
    CAN_READ_USERS,
    CAN_CREATE_USERS,
    CAN_UPDATE_USERS,
    CAN_DELETE_USERS,

    // permisije za porudžbine
    CAN_SEARCH_ORDER,
    CAN_PLACE_ORDER,
    CAN_CANCEL_ORDER,
    CAN_TRACK_ORDER,
    CAN_SCHEDULE_ORDER
}