package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.demo.model.entity.Category;
import ra.demo.repository.CategoryRepository;
import ra.demo.repository.ProductRepository;
import ra.demo.service.CategoryService;
import ra.demo.service.ProductService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductService productService;

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category insertCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(String cateId) {
        return categoryRepository.findById(cateId).orElseThrow(()-> new NoSuchElementException("Không tồn tại danh mục :"+cateId));
    }

    @Override
    public Category updateCategory(String cateId, Category category) {
        categoryRepository.findById(cateId).orElseThrow(()-> new NoSuchElementException("Không tồn tại danh mục :"+cateId));
        category.setCateId(cateId);
        return categoryRepository.save(category);
    }

    @Override
    public boolean deleteCategory(String cateId) throws Exception {
        categoryRepository.findById(cateId).orElseThrow(()-> new NoSuchElementException("Không tồn tại danh mục :"+cateId));
        //check có sản phẩm hay không?
        if(productService.checkCategory(cateId)){
            throw new Exception("Danh mục đang có sản phẩm, không được xóa");
        }
        categoryRepository.deleteById(cateId);
        return true;
    }
}
