package com.imooc.service.impl;

import com.imooc.converter.OrderMaster2OrderDTOConverter;
import com.imooc.dataobject.OrderDetail;
import com.imooc.dataobject.OrderMaster;
import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.repository.OrderDetailRepository;
import com.imooc.repository.OrderMasterRepository;
import com.imooc.repository.ProductInfoRepository;
import com.imooc.service.OrderService;
import com.imooc.service.PayService;
import com.imooc.service.ProductService;
import com.imooc.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductService productService;
    @Autowired
    private PayService payService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private OrderMasterRepository orderMasterRepository;
    /**
     * 创建订单
     */
    @Transactional
    public  OrderDTO create(OrderDTO orderDTO){
        BigDecimal orderAmount = new BigDecimal(BigInteger.ZERO);
        String orderId = KeyUtil.genUniqueKey();
        //1、查询商品数量 价格
        for (OrderDetail orderDetail : orderDTO.getOrderDetailList()){
          ProductInfo productInfo= productService.findOne(orderDetail.getProductId());
          if(productInfo==null){
              throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
          }
            //2、计算总价
            orderAmount = productInfo.getProductPrice().multiply(new BigDecimal(orderDetail.getProductQuantity())).add(orderAmount);
           //3、输入存入订单详细表
            BeanUtils.copyProperties(productInfo,orderDetail);
            orderDetail.setOrderId(orderId);
            orderDetail.setDetailId(KeyUtil.genUniqueKey());
            orderDetail.setCreateTime(new Date());
            orderDetailRepository.save(orderDetail);
        }
        //3、写入订单数据库
        OrderMaster orderMaster = new OrderMaster();
        orderDTO.setOrderId(orderId);
        BeanUtils.copyProperties(orderDTO,orderMaster);
        orderMaster.setOrderAmount(orderAmount);
        orderMaster.setOrderStatus(OrderStatusEnum.NEW.getCode());
        orderMaster.setPayStatus(PayStatusEnum.WAIT.getCode());
        orderMaster.setCreateTime(new Date());
        orderMasterRepository.save(orderMaster);
        //4、扣库存
        List<CartDTO> CartDTOList = orderDTO.getOrderDetailList().stream().map(e ->new CartDTO(e.getProductId(),e.getProductQuantity())).collect(Collectors.toList());
        productService.decreaseStock(CartDTOList);
        return orderDTO;
    }
    /**
     * 查询单个订单
     */
    public  OrderDTO findOne(String orderId){
        OrderMaster orderMaster= orderMasterRepository.findById(orderId).orElse(null);
        if(orderMaster==null){
            throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
        }
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderId(orderMaster.getOrderId());
        if(StringUtils.isEmpty(orderDetailList)){
            throw new  SellException(ResultEnum.ORDERDETAIL_NOT_EXISTS);
        }
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(orderMaster,orderDTO);
        orderDTO.setOrderDetailList(orderDetailList);
        return orderDTO;
    }
    /**
     * 查询订单列表
     */
    public  Page<OrderDTO> findList(String buyerId , Pageable pageable){
        Page<OrderMaster> orderMasterPage = orderMasterRepository.findByBuyerOpenid(buyerId,pageable);
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        Page<OrderDTO> orderDTOPage = new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalElements());
        return orderDTOPage;
    }
    /**
     * 取消订单
     */
    public  OrderDTO cancel (OrderDTO orderDTO){
        OrderMaster orderMaster = new OrderMaster();
        //判断订单状态
        if(!orderMaster.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.info("[取消订单]订单状态不正确,orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
                throw  new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态
        orderDTO.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster updateResult =  orderMasterRepository.save(orderMaster);
        if(updateResult == null){
            log.info("[取消订单]订单状态更新失败,orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw  new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        //返回库存
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.info("[取消订单]订单中没有商品详情,orderId={}, orderStatus={}", orderDTO.getOrderId(), orderDTO.getOrderStatus());
            throw  new SellException(ResultEnum.ORDER_DETAIL_EMPTY);
        }
        List<CartDTO> cartDTOList = orderDTO.getOrderDetailList().stream().map(e ->new CartDTO(e.getProductId(),e.getProductQuantity())).collect(Collectors.toList());
        productService.increaseStock(cartDTOList);

        //如果已经支付 需要退款
        if(orderDTO.getPayStatus().equals(PayStatusEnum.SUCCESS)){
            payService.refund(orderDTO);
        }
        return orderDTO;
    }
    /**
     * 完结订单
     */
    public  OrderDTO finish (OrderDTO orderDTO){
        //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW.getCode())){
            log.error("[完结订单] 订单状态不正确 ， orderID = {} , orderStatus = {}",orderDTO.getOrderId() , orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        orderDTO.setOrderStatus(OrderStatusEnum.FINISHED.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster result = orderMasterRepository.save(orderMaster);
        if(result == null){
            log.error("[完结订单] 订单更新失败 orderMaster = {} " , orderMaster);
            throw new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }
    /**
     * 支付订单
     */
    public  OrderDTO  paid (OrderDTO orderDTO){
       //判断订单状态
        if(!orderDTO.getOrderStatus().equals(OrderStatusEnum.NEW)){
            log.error("[订单支付] 订单状态不正确 ， orderID = {} , orderStatus = {}",orderDTO.getOrderId() , orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改支付状态
        if(!orderDTO.getPayStatus().equals(PayStatusEnum.WAIT.getCode())){
            log.error("[订单支付] 订单支付状态不正确 ， orderID = {} , orderStatus = {}",orderDTO.getOrderId() , orderDTO.getOrderStatus());
            throw new SellException(ResultEnum.ORDER_PAY_STATUS_ERROR);
        }
        //修改订单状态
        orderDTO.setOrderStatus(PayStatusEnum.SUCCESS.getCode());
        OrderMaster orderMaster = new OrderMaster();
        BeanUtils.copyProperties(orderDTO,orderMaster);
        OrderMaster result =  orderMasterRepository.save(orderMaster);
        if(result==null){
            log.error("[订单支付] 更新失败 orderMAster = {}" , orderMaster);
            throw  new SellException(ResultEnum.ORDER_UPDATE_FAIL);
        }
        return orderDTO;
    }

    public Page<OrderDTO> findList(Pageable pageable){
       Page<OrderMaster>  orderMasterPage =  orderMasterRepository.findAll(pageable);
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(orderMasterPage.getContent());
        return new PageImpl<OrderDTO>(orderDTOList,pageable,orderMasterPage.getTotalPages());
    }
}
