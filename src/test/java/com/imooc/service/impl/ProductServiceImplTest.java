package com.imooc.service.impl;

import com.imooc.dataobject.ProductInfo;
import com.imooc.enums.ProductStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTest {
@Autowired
private ProductServiceImpl productService;
    @Test
    public void findOne() {
       ProductInfo productInfo = productService.findOne("12345");
        Assert.assertEquals("12345",productInfo.getProductId());
    }

    @Test
    public void findUpAll() {
        List<ProductInfo> productInfoList = productService.findUpAll();
        Assert.assertNotEquals(0,productInfoList.size());
    }

    @Test
    public void findAll() {
        PageRequest pageRequest = new PageRequest(0,10);
        Page<ProductInfo> page =  productService.findAll(pageRequest);
        System.out.println(page.getTotalElements());
    }

    @Test
    public void save() {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("2344");
        productInfo.setProductName("皮皮虾");
        productInfo.setProductPrice(new BigDecimal("33.2"));
        productInfo.setProductDescription("very good");
        productInfo.setProductStock(100);
        productInfo.setProductIcon("http://xxxxx.jpg");
        productInfo.setCategoryType(2);
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
        productInfo.setCreateTime(new Date());
        ProductInfo result = productService.save(productInfo);
        Assert.assertNotNull(result);
    }
}