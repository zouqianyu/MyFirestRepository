package com.imooc.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderForm {
    @NotEmpty(message = "姓名必填")
    public String name;
    @NotEmpty(message = "电话必填")
    public String phone;
    @NotEmpty(message = "地址必填")
    public String address;
    @NotEmpty(message = "openid必填")
    public String openid;
    @NotEmpty(message = "购物车不能为空")
    public String items;
}
