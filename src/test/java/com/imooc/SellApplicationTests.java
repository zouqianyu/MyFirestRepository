package com.imooc;

import com.imooc.dataobject.ProductCategory;
import com.imooc.repository.ProductCategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SellApplicationTests {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Test
    public void contextLoads() {
        ProductCategory productCategory =  productCategoryRepository.getOne("1");
        System.out.printf(productCategory.toString());
    }

}
