package raf.rs.nwp_project_kotlin.service.implementation

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import raf.rs.nwp_project_kotlin.model.orders.Dish
import raf.rs.nwp_project_kotlin.repository.DishRepository
import raf.rs.nwp_project_kotlin.service.DishService

@Service
class DishServiceImpl(
    private val dishRepository: DishRepository
) : DishService {
    override fun getAllDishes(page: Int, size: Int): Page<Dish> =
        dishRepository.findAll(PageRequest.of(page, size))

    override fun getDish(id: Long): Dish = dishRepository.findById(id).orElseThrow()
}