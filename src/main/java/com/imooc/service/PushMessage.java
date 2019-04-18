package com.imooc.service;

import com.imooc.dto.OrderDTO;

public interface PushMessage {
    void orderStatus(OrderDTO orderDTO);

}
