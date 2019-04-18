package com.imooc.service.impl;

import com.imooc.dataobject.OrderDetail;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayStatusEnum;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.service.OrderService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Autowired
    private OrderService orderService ;

    @Autowired
    private OrderMasterRepository orderMasterRepository;

    private static  final String buyerOpenId = "zqylxb1993";

    private static  final Logger log = LoggerFactory.getLogger(OrderServiceImplTest.class);

    @Test
    public void create() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName("罂粟");
        orderDTO.setBuyerAddress("陆家嘴");
        orderDTO.setBuyerOpenid(buyerOpenId);
        orderDTO.setBuyerPhone("13316498580");
        orderDTO.setCreateTime(new Date());
        List<OrderDetail> orderDetailList = new ArrayList<>();
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProductId("2");
        orderDetail.setProductQuantity(2);
        orderDetailList.add(orderDetail);
        orderDTO.setOrderDetailList(orderDetailList);
        OrderDTO  result  = orderService.create(orderDTO);
        log.info("创建订单 , result = {}" , result);
    }

    @Test
    public void findOne() {
        OrderDTO orderDTO =  orderService.findOne("1552984353042559056");
        System.out.println(orderDTO);
    }

    @Test
    public void findList() {
        PageRequest pageRequest = new PageRequest(1,2);
        Page<OrderDTO> orderDTOPage = orderService.findList("zqylxb1993",pageRequest);
        Assert.assertNotNull(orderDTOPage);
    }

    @Test
    public void cancel() {
        OrderDTO orderDTO = orderService.findOne("1552984353042559056");
        OrderDTO result  = orderService.cancel(orderDTO);
        Assert.assertNotEquals(OrderStatusEnum.CANCEL.getCode(),result.getOrderStatus());
    }

    @Test
    public void finish() {
        OrderDTO orderDTO = orderService.findOne("1552984353042559056");
        OrderDTO result  = orderService.finish(orderDTO);
        Assert.assertNotEquals(OrderStatusEnum.FINISHED.getCode(),result.getOrderStatus());
    }

    @Test
    public void paid() {
        OrderDTO orderDTO = orderService.findOne("1552984353042559056");
        OrderDTO result  = orderService.paid(orderDTO);
        Assert.assertNotEquals(PayStatusEnum.SUCCESS.getCode(),result.getPayStatus());
    }

    @Test
    public void list(){
    PageRequest pageRequest = new PageRequest(0,2);
        Page<OrderDTO> page = orderService.findList(pageRequest);
        Assert.assertTrue(page.getTotalElements()>0);
    }
}