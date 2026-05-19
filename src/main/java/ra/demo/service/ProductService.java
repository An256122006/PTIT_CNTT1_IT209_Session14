package ra.demo.service;

import org.springframework.data.domain.Page;
import ra.demo.model.entity.Product;

public interface ProductService {
    //Hàm lấy dữ liệu có phân trang
    Page<Product> getProductsPagination(Integer page, Integer pageSize);
    Page<Product> getProductsPaginationAndSorting(Integer page, Integer pageSize, String sortBy, String orderBy);

    Product insertProduct(Product product);
    Product getProductById(Long proId);
    Product updateProduct(Long proId, Product product);
    boolean checkCategory(String cateId);
}
