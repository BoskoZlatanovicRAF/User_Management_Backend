package raf.rs.nwp_project_kotlin.controller

import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import raf.rs.nwp_project_kotlin.model.orders.Dish
import raf.rs.nwp_project_kotlin.service.DishService

@RestController
@CrossOrigin(origins = ["http://localhost:4200"])
@RequestMapping("/api/dishes")
class DishController(private val dishService: DishService) {

    @GetMapping
    fun getAllDishes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<Dish>> {
        return ResponseEntity.ok(dishService.getAllDishes(page, size))
    }

    @GetMapping("/{id}")
    fun getDish(@PathVariable id: Long): ResponseEntity<Dish> {
        return ResponseEntity.ok(dishService.getDish(id))
    }
}