package ra.demo.service;

import ra.demo.model.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    Category insertCategory(Category category);
    Category getCategoryById(String cateId);
    Category updateCategory(String cateId, Category category);
    boolean deleteCategory(String cateId) throws Exception;
}
