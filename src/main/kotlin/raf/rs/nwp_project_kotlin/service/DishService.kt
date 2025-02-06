package raf.rs.nwp_project_kotlin.service

import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.orders.Dish
@Service
interface DishService {
    fun getAllDishes(page: Int, size: Int): Page<Dish>  // nova verzija
    fun getDish(id: Long): Dish
}