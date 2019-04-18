package com.imooc.service.impl;

import com.imooc.dataobject.ProductCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceImplTest {

     @Autowired
    private CategoryServiceImpl categoryServiceIml;

     @Test
    public void findOne() throws  Exception{
        ProductCategory productCategory =  categoryServiceIml.findOne("1");
        Assert.assertEquals(1,productCategory.getCategoryId());
    }

    @Test
    public void save() throws Exception{
        ProductCategory productCategory = new ProductCategory("男生最爱",2,new Date());
        productCategory.setCategoryId("1");
        ProductCategory result =  categoryServiceIml.save(productCategory);
        Assert.assertNotNull(result);
    }


}