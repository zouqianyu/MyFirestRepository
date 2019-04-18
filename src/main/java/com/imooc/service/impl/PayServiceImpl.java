package com.imooc.service.impl;

import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.service.OrderService;
import com.imooc.service.PayService;
import com.imooc.utils.MathUtil;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.model.RefundRequest;
import com.lly835.bestpay.model.RefundResponse;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PayServiceImpl implements PayService {
    private static final String ORDER_NAME = "微信点餐订单";

    @Autowired
    private BestPayService bestPayService;

    @Autowired
    private OrderService orderService;
    /**
     *     private BestPayTypeEnum payTypeEnum;
     *     private String orderId;
     *     private Double orderAmount;
     *     private String orderName;
     *     private String openid;
     * @param orderDTO
     * @return
     */
    public PayResponse create(OrderDTO orderDTO){
        PayRequest payRequest = new PayRequest();
        payRequest.setOpenid(orderDTO.getBuyerOpenid());
        payRequest.setOrderId(orderDTO.getOrderId());
        payRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        payRequest.setOrderName(ORDER_NAME);
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("[微信支付]发起支付，request={}", JsonUtil.toJson(payRequest));
        PayResponse payResponse = bestPayService.pay(payRequest);
        log.info("【微信支付】发起支付, response={}",JsonUtil.toJson(payResponse));
        return payResponse;
    }

    public PayResponse notify(String notifyData){
        PayResponse payResponse =  bestPayService.asyncNotify(notifyData);
        log.info("[微信支付]异步回调payResponse = {}",JsonUtil.toJson(payResponse));
        //查询订单
        OrderDTO orderDTO  = orderService.findOne(payResponse.getOrderId());
        //判断订单是否存在
        if(orderDTO==null){
            log.info("[微信支付]订单不存在 OrderId = {}",JsonUtil.toJson(payResponse.getOrderId()));
            throw  new SellException(ResultEnum.ORDER_NOT_EXISTS);
        }
        //判断金额是否一致orderDTO.getOrderAmount().equals(payResponse.getOrderAmount())
        if (!MathUtil.equals(payResponse.getOrderAmount(), orderDTO.getOrderAmount().doubleValue())) {
            log.error("[微信支付] 订单金额不一致, orderId={}, 微信通知金额={}, 系统金额={} ",orderDTO.getOrderId(),orderDTO.getOrderAmount(),payResponse.getOrderAmount());
            throw  new SellException(ResultEnum.WXPAY_NOTIFY_MONEY_VERIFY_ERROR);
        }
        //修改订单的支付状态
        orderService.paid(orderDTO);
        return  payResponse;
    }

    /**
     *     private BestPayTypeEnum payTypeEnum;
     *     private String orderId;
     *     private Double orderAmount;
     * @param orderDTO
     * @return
     */
    public RefundResponse refund(OrderDTO orderDTO){
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setOrderId(orderDTO.getOrderId());
        refundRequest.setOrderAmount(orderDTO.getOrderAmount().doubleValue());
        refundRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_H5);
        log.info("[微信退款]request={}",JsonUtil.toJson(refundRequest));
        RefundResponse refundResponse = bestPayService.refund(refundRequest);
        log.info("[微信退款]refundResponse = {}",JsonUtil.toJson(refundResponse));
        return  refundResponse;
    }
}
