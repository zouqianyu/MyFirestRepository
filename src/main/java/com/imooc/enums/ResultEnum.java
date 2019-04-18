package com.imooc.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(0,"成功"),
    PARAM_ERROR(1,"数据不全"),
    PRODUCT_NO_EXISTS(10,"商品不存在"),
    PRODUCT_STOCK_ERROR(11,"库存不足"),
    ORDER_NOT_EXISTS(12,"订单不存在"),
    ORDERDETAIL_NOT_EXISTS(13,"订单明细不存在"),
    ORDER_STATUS_ERROR(14,"订单状态错误"),
    ORDER_UPDATE_FAIL(15,"订单更新失败"),
    ORDER_DETAIL_EMPTY(16,"订单中没有商品详情"),
    ORDER_PAY_STATUS_ERROR(17,"订单支付状态不正确"),
    WXPAY_NOTIFY_MONEY_VERIFY_ERROR(18, "微信支付异步通知金额校验不通过"),
    CART_EMPTY(19,"购物车为空"),
    ORDER_OWER_ERROR(20,"该订单不属于当前用户"),
    WECHAT_MP_ERROR(21,"微信公众账号方面错误"),
    ORDER_CANCEL_SUCCESS(22,"订单取消成功"),
    ORDER_FINISH_SUCCESS(23,"订单完结成功"),
    PRODUCT_STATUS_ERROR(24,"商品状态不对"),
    LOGIN_FAIL(25,"登陆信息不正确"),
    LOGINOUT_SUCCESS(26,"登出成功"),
    ;
    private  Integer code;
    private  String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}


