package com.imooc.service.impl;

import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.service.BuyerService;
import com.imooc.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BuyerServiceImpl implements BuyerService {
    @Autowired
    private OrderService orderService;
    @Override
    public OrderDTO findOrderOne(String openid, String orderid) {
        return checkOrderOwner(openid,orderid);
    }

    @Override
    public OrderDTO cancelOrder(String openid, String orderid) {
        OrderDTO orderDTO = checkOrderOwner(openid,orderid);
        if(orderDTO == null){
            log.error("[取消订单] 查不到该订单");
            throw new SellException(ResultEnum.ORDER_NOT_EXISTS);
        }
        return orderService.cancel(orderDTO);
    }

    private OrderDTO checkOrderOwner(String openid, String orderid){
        OrderDTO orderDTO = orderService.findOne(orderid);
        if(orderDTO==null){
            return null;
        }
        if(!orderDTO.getBuyerOpenid().equals(openid)){
            log.error("[查询订单] 订单的openid 不一致 opendid = {}",orderDTO.getBuyerOpenid());
            throw new SellException(ResultEnum.ORDER_OWER_ERROR);
        }
        return orderDTO;
    }
}
