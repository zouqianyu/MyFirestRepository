package com.imooc.repository;

import com.imooc.dataobject.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductCategoryRepository extends JpaRepository <ProductCategory,String>{
     List<ProductCategory> findProductCategoriesByCategoryTypeIn(List<Integer> categoryTypeList);
}
