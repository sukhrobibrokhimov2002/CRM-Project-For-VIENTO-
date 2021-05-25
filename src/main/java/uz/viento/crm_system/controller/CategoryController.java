package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.viento.crm_system.entity.Category;
import uz.viento.crm_system.payload.CategoryDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDto categoryDto) {
        ResponseApi responseApi = categoryService.addCategory(categoryDto);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }
    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editCategory(@PathVariable Long id,@RequestBody CategoryDto categoryDto) {
        ResponseApi responseApi = categoryService.editCategory(id,categoryDto);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }
    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        ResponseApi responseApi = categoryService.deleteCategory(id);
        if (responseApi.isSuccess())
            return ResponseEntity.ok(responseApi);
        return ResponseEntity.status(409).body(responseApi);
    }
    @PreAuthorize("hasAnyRole(ROLE_ADMIN)")
    @GetMapping("/getAll")
    public ResponseEntity<?> deleteCategory(@RequestParam int page) {
        Page<Category> categories = categoryService.getCategories(page);
        if (!categories.isEmpty())
            return ResponseEntity.ok(categories);
        return ResponseEntity.status(409).body(categories);
    }



}
