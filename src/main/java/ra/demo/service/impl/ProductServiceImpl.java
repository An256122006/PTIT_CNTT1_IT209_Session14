package ra.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ra.demo.model.entity.Product;
import ra.demo.repository.ProductRepository;
import ra.demo.service.ProductService;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public Page<Product> getProductsPagination(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getProductsPaginationAndSorting(Integer page, Integer pageSize, String sortBy, String orderBy) {
        Sort sort = null;
        Pageable pageable;

        switch (orderBy){
            case "proId":
                sort = Sort.by("proId");
                break;
            case "proName":
                sort = Sort.by("proName");
                break;
            case "producer":
                sort = Sort.by("producer");
                break;
            case "yearMaking":
                sort = Sort.by("yearMaking");
                break;
            case "expireDate":
                sort = Sort.by("expireDate");
                break;
            case "quantity":
                sort = Sort.by("quantity");
                break;
            case "price":
                sort = Sort.by("price");
                break;
        }

        switch (sortBy){
            case "asc":
                sort = sort.ascending();
                break;
            case "desc":
                sort = sort.descending();
                break;
        }

        pageable = PageRequest.of(page, pageSize,sort);
        return productRepository.findAll(pageable);
    }

    @Override
    public Product insertProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long proId) {
        return productRepository.findById(proId).orElseThrow(()-> new NoSuchElementException("Không tồn tại sản phẩm có mã: "+proId));
    }

    @Override
    public Product updateProduct(Long proId, Product product) {
        productRepository.findById(proId).orElseThrow(()-> new NoSuchElementException("Không tồn tại sản phẩm có mã: "+proId));
        product.setProId(proId);
        return productRepository.save(product);
    }

    @Override
    public boolean checkCategory(String cateId) {
        return productRepository.findByCategory_CateId(cateId);
    }
}
