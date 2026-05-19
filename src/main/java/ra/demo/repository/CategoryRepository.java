package ra.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.demo.model.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,String> {
}
