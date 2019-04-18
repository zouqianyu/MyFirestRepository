package com.imooc.repository;

import com.imooc.dataobject.OrderDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderDetailRepositoryTest {

    @Autowired
    private  OrderDetailRepository repository;
    @Test
    public  void save() throws  Exception{
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setDetailId("1");
        orderDetail.setOrderId("1111");
        orderDetail.setProductIcon("21212");
        orderDetail.setProductId("sjksjkjkj");
        orderDetail.setProductPrice(new BigDecimal("2.3"));
        orderDetail.setProductQuantity(45);
        orderDetail.setProductName("hah");
        orderDetail.setCreateTime(new Date());
        repository.save(orderDetail);
    }
    @Test
    public void findByOrderId() {
    }
}