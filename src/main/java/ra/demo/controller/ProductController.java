package ra.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.demo.model.dto.response.ApiDataResonse;
import ra.demo.model.entity.Product;
import ra.demo.service.ProductService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Value("${pageSizeProducts}")
    private Integer pageSize;

    //có phân trang
    @GetMapping
    public ResponseEntity<ApiDataResonse<Page<Product>>> getProducts(@RequestParam(name = "page", defaultValue = "1") Integer page) {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Lấy danh sách sản phẩm trang " + page + " thành công!",
                productService.getProductsPagination(page - 1, pageSize),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    //có phân trang và sắp xếp
    @GetMapping("/list-products")
    public ResponseEntity<ApiDataResonse<Page<Product>>> getProductWithSortingAndPagination(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "orderBy", defaultValue = "proId") String orderBy,
            @RequestParam(name = "sortBy", defaultValue = "asc") String sortBy) {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Lấy danh sách trang " + page + " thành công",
                productService.getProductsPaginationAndSorting(page - 1, pageSize, sortBy, orderBy),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiDataResonse<Product>> insertProduct(@RequestBody Product product) {
        return new ResponseEntity<>(new ApiDataResonse<>(
                true,
                "Thêm mới sản phẩm thành công!",
                productService.insertProduct(product),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }
}
