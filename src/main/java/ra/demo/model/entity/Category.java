package ra.demo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Category {
    @Id
    @Column(name = "category_id",length = 15)
    private String cateId;
    @Column(name = "category_name",length = 100,nullable = false,unique = true)
    private String cateName;
    private Boolean status;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products;
}
