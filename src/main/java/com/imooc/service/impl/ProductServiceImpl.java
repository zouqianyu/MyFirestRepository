package com.imooc.service.impl;

import com.imooc.dataobject.ProductInfo;
import com.imooc.dto.CartDTO;
import com.imooc.enums.ProductStatusEnum;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.repository.ProductInfoRepository;
import com.imooc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private  ProductInfoRepository productInfoRepository;

    //@Cacheable(cacheNames = "product",key = "123")
    public ProductInfo findOne(String productId){
       return productInfoRepository.findById(productId).orElse(null);
    }
    /**
     * 查询所有在架的商品列表
     * @return
     */
    public  List<ProductInfo> findUpAll(){
       return  productInfoRepository.findByAndProductStatus(ProductStatusEnum.UP.getCode());
    }

    public Page<ProductInfo> findAll(Pageable pageable){
       return productInfoRepository.findAll(pageable);
    }

    //@CachePut(cacheNames = "product",key = "123")
    public ProductInfo save(ProductInfo productInfo){
        return productInfoRepository.save(productInfo);
    }

    //加库存
    @Transactional
    public  void increaseStock(List<CartDTO> CartDTOList){
        for (CartDTO cartDTO : CartDTOList){
            ProductInfo productInfo = productInfoRepository.findById(cartDTO.getProductId()).orElse(null);
            if(productInfo==null){
                throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
            }
            Integer result = productInfo.getProductStock() + cartDTO.getProductQuantity();
            if(result < 0){
                throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }
            productInfo.setProductStock(result);
            productInfoRepository.save(productInfo);
        }
    }

    //减库存
    @Transactional
    public  void decreaseStock(List<CartDTO> CartDTOList){
        for (CartDTO cartDTO : CartDTOList){
            ProductInfo productInfo = productInfoRepository.findById(cartDTO.getProductId()).orElse(null);
            if(productInfo==null){
                throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
            }
            Integer result = productInfo.getProductStock() - cartDTO.getProductQuantity();
            if(result < 0){
               throw new SellException(ResultEnum.PRODUCT_STOCK_ERROR);
            }
            productInfo.setProductStock(result);
            productInfoRepository.save(productInfo);
        }
    }

    @Override
    public ProductInfo onSale(String productId) {
       ProductInfo productInfo =  productInfoRepository.getOne(productId);
       if(productInfo==null){
            throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
       }
        if(productInfo.getProductStatusEnum() == ProductStatusEnum.UP){
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
       }
       //更新
        productInfo.setProductStatus(ProductStatusEnum.UP.getCode());
        return productInfoRepository.save(productInfo);
    }

    @Override
    public ProductInfo offSale(String productId) {
        ProductInfo productInfo =  productInfoRepository.getOne(productId);
        if(productInfo==null){
            throw new SellException(ResultEnum.PRODUCT_NO_EXISTS);
        }
        if(productInfo.getProductStatusEnum() == ProductStatusEnum.DOWN){
            throw new SellException(ResultEnum.PRODUCT_STATUS_ERROR);
        }
        //更新
        productInfo.setProductStatus(ProductStatusEnum.DOWN.getCode());
        return productInfoRepository.save(productInfo);
    }
}
