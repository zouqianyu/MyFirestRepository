package com.imooc.controller;

import com.imooc.VO.ResultVO;
import com.imooc.converter.OrderForm2OrderDTOConverter;
import com.imooc.dto.OrderDTO;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.form.OrderForm;
import com.imooc.service.BuyerService;
import com.imooc.service.OrderService;
import com.imooc.utils.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/buyer/order")
public class BuyerOrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private BuyerService buyerService;
    //创建订单
    @RequestMapping(value = "/create")
    public ResultVO <Map<String,String>> create(@Valid OrderForm orderForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("[表单校验] 表单数据不全, orderForm = {} " , orderForm);
            throw  new SellException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if(CollectionUtils.isEmpty(orderDTO.getOrderDetailList())){
            log.error("[创建订单] 购物车不能为空");
            throw new SellException(ResultEnum.CART_EMPTY);
        }
        OrderDTO createResult =  orderService.create(orderDTO);
        Map<String , String> map = new HashMap<>(0);
        map.put("orderId",orderDTO.getOrderId());
        return ResultVOUtil.success(map);
    }

    //订单列表
    @RequestMapping(value = "/list")
    public ResultVO<List<OrderDTO>> list(@RequestParam("openid") String openid,
                                         @RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "10") Integer size){
        if(StringUtils.isEmpty(openid)){
            log.error("[查询订单列表]openid为空");
            throw new SellException(ResultEnum.PARAM_ERROR);
        }
        PageRequest request = new PageRequest(page,size);
        Page<OrderDTO> orderDTOPage =  orderService.findList(openid,request);
        return ResultVOUtil.success(orderDTOPage.getContent());

    }

    //订单详情
    @RequestMapping(value = "/detail")
    public ResultVO<OrderDTO> detail(@RequestParam(value = "openid") String openid ,
                                     @RequestParam(value = "orderId") String orderId){
       if(StringUtils.isEmpty(openid) || StringUtils.isEmpty(orderId)){
            log.error("[获取订单详情] 参数不全");
            throw new SellException(ResultEnum.PARAM_ERROR);
       }
       //TODO修改不安全 有待改进
        OrderDTO orderDTO = buyerService.findOrderOne(openid,orderId);
       return ResultVOUtil.success(orderDTO);
    }
    //取消订单
    @RequestMapping(value = "/cancel")
    public ResultVO cancel(@RequestParam(value = "openid") String openid, @RequestParam(value = "orderId") String orderId){
        OrderDTO orderDTO = buyerService.cancelOrder(openid,orderId);
        return ResultVOUtil.success(orderDTO);
    }



}
