package com.imooc.repository;

import com.imooc.dataobject.OrderMaster;
import com.imooc.enums.PayStatusEnum;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderMasterRepositoryTest {

    @Autowired
    private OrderMasterRepository repository;
    private  final  String openId = "zqylxb1993";
    @Test
    public void save() throws  Exception{
        OrderMaster orderMaster = new OrderMaster();
        orderMaster.setOrderId("1232");
        orderMaster.setBuyerName("罂粟");
        orderMaster.setBuyerPhone("13316495880");
        orderMaster.setBuyerAddress("金融广场3号楼");
        orderMaster.setOrderAmount(new BigDecimal("5.2"));
        orderMaster.setBuyerOpenid(openId);
        orderMaster.setCreateTime(new Date());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        OrderMaster result = repository.save(orderMaster);
        Assert.assertNotNull(result);
    }
    @Test
    public void findByBuyerOpenid() throws  Exception{
           PageRequest page = new PageRequest(0,1);
           Page<OrderMaster> result =  repository.findByBuyerOpenid(openId,page);
           System.out.println(result.getTotalElements());
    }
}