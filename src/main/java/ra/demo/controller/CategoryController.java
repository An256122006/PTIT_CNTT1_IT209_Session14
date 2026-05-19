package ra.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.response.ApiDataResonse;
import ra.demo.model.entity.Category;
import ra.demo.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiDataResonse<List<Category>>> getCategories() {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Lấy danh sách danh mục thành công",
                categoryService.getCategories(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiDataResonse<Category>> insertCategory(@RequestBody Category category) {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Thêm mới danh mục thành công",
                categoryService.insertCategory(category),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @PutMapping("/{cateId}")
    public ResponseEntity<ApiDataResonse<Category>> updateCategory(@PathVariable("cateId") String cateId, @RequestBody Category category) {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Cập nhật danh mục thành công",
                categoryService.updateCategory(cateId, category),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{cateId}")
    public ResponseEntity<ApiDataResonse<Boolean>> deleteCategory(@PathVariable("cateId")String cateId) throws Exception {
        return new ResponseEntity<>(new ApiDataResonse<>(
           true,
           "Xóa danh mục "+cateId+" thành công",
           categoryService.deleteCategory(cateId),
           HttpStatus.NO_CONTENT
        ), HttpStatus.NO_CONTENT);
    }
}
