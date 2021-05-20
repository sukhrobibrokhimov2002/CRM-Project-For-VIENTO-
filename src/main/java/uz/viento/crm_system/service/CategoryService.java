package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.viento.crm_system.entity.Category;
import uz.viento.crm_system.payload.CategoryDto;
import uz.viento.crm_system.payload.ResponseApi;
import uz.viento.crm_system.repository.CategoryRepository;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public ResponseApi addCategory(CategoryDto categoryDto) {
        boolean existsByName = categoryRepository.existsByName(categoryDto.getName());
        if (existsByName) return new ResponseApi("Category already exists", false);
        Category category = new Category();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return new ResponseApi("Successfully added", true);
    }

    public ResponseApi editCategory(long id, CategoryDto categoryDto) {
        Optional<Category> byId = categoryRepository.findById(id);
        if (!byId.isPresent()) return new ResponseApi("Category not found", false);
        Category category = byId.get();
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return new ResponseApi("Category successfully edited", true);
    }

    public ResponseApi deleteCategory(long id) {
        try {
            categoryRepository.deleteById(id);
            return new ResponseApi("Category successfully deleted", true);
        } catch (Exception e) {
            return new ResponseApi("Error in deleting category", false);
        }
    }

    public Page<Category> getCategories(int page) {
        PageRequest of = PageRequest.of(page, 10);
        Page<Category> all = categoryRepository.findAll(of);
        return all;
    }

}
