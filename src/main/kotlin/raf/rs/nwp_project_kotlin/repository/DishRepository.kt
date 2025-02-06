package raf.rs.nwp_project_kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import raf.rs.nwp_project_kotlin.model.orders.Dish

interface DishRepository: JpaRepository<Dish, Long> {

}