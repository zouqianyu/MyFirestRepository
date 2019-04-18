package com.imooc.repository;

import com.imooc.dataobject.ProductInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductInfoRepositoryTest {
    @Autowired
    private ProductInfoRepository repository;

    @Test
    public void  findByAndProductStatus(){
        List<ProductInfo> productInfos = repository.findByAndProductStatus(0);
        Assert.assertNotEquals(0,productInfos.size());
    }

    @Test
    public void save() throws Exception{
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductId("123445");
        productInfo.setProductName("蒙古牛肉");
        productInfo.setProductPrice(new BigDecimal("3.2"));
        productInfo.setProductDescription("very good");
        productInfo.setProductStock(100);
        productInfo.setProductIcon("http://xxxxx.jpg");
        productInfo.setCategoryType(3);
        productInfo.setProductStatus(0);
        //productInfo.setCreateTime(new Date().toString());
        ProductInfo result = repository.save(productInfo);
        Assert.assertNotNull(result);
    }
}