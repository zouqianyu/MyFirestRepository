package com.imooc.controller;

import com.imooc.dataobject.ProductCategory;
import com.imooc.dataobject.ProductInfo;
import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import com.imooc.form.ProductForm;
import com.imooc.service.CategoryService;
import com.imooc.service.ProductService;
import com.imooc.utils.KeyUtil;
import com.lly835.bestpay.rest.type.Get;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 买家端商品
 */
@Controller
@RequestMapping("/seller/product")
public class SellerProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/list")
    public ModelAndView list(@RequestParam(value = "page",defaultValue = "1") Integer page ,
                             @RequestParam(value = "size",defaultValue = "10") Integer size,
                             Map<String,Object> map){
        PageRequest pageRequest = new PageRequest(page-1,size);
        Page<ProductInfo> productInfoPage =  productService.findAll(pageRequest);
        map.put("productInfoPage",productInfoPage);
        map.put("currentPage",page);
        map.put("size",size);
        return new ModelAndView("product/list",map);
    }

    /**
     * 上架
     * @return
     */
    @RequestMapping("/on_sale")
    public ModelAndView on_sale(@RequestParam("productId") String productId,
                                Map<String,Object> map){
        ProductInfo productInfo = new ProductInfo();
        try{
            productInfo =  productService.findOne(productId);
            productService.onSale(productId);
        }catch (SellException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }
        map.put("msg","");
        map.put("url","/sell/seller/product/list");
        return new ModelAndView("common/success");
    }

    /**
     * 下架
     * @return
     */
    @RequestMapping("/off_sale")
    public ModelAndView off_sale(@RequestParam("productId") String productId,
                                 Map<String,Object> map){
        ProductInfo productInfo = new ProductInfo();
        try{
            productInfo =  productService.findOne(productId);
            productService.offSale(productId);
        }catch (SellException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }
            map.put("msg","");
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/success");
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam(value = "productId",required = false) String productId ,
                              Map<String,Object> map){
        ProductInfo productInfo = new ProductInfo();
        List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
        try{
            if(!StringUtils.isEmpty(productId)){
                productInfo = productService.findOne(productId);
            }
            //查询类目
            productCategoryList = categoryService.findAll();

        }catch (SellException e){
            map.put("msg", e.getMessage());
            map.put("url","/sell/seller/product/list");
            return new ModelAndView("common/error",map);
        }
        map.put("productInfo",productInfo);
        map.put("categoryList",productCategoryList);
        return new ModelAndView("product/index");
    }

    /**
     * 保存/更新
     * @param productForm
     * @param bindingResult
     * @param map
     * @return
     */
    @PostMapping("/save")
    public ModelAndView save(@Valid ProductForm productForm ,
                             BindingResult bindingResult,
                             Map<String,Object> map){
        if(bindingResult.hasErrors()){
            map.put("msg",bindingResult.getFieldError().getDefaultMessage());
            map.put("url","/sell/seller/product/index");
            return new ModelAndView("common/error",map);
        }
        try{
            ProductInfo productInfo = new ProductInfo();
            if(!StringUtils.isEmpty(productForm.getProductId())){
                productInfo =productService.findOne(productForm.getProductId());
            }else {
                productForm.setProductId(KeyUtil.genUniqueKey());
                productInfo.setCreateTime(new Date());
            }
            BeanUtils.copyProperties(productForm,productInfo);
            productService.save(productInfo);
        }catch (SellException e){
            map.put("msg",e.getMessage());
            map.put("url","/sell/seller/product/index");
            return new ModelAndView("common/error");
        }
        map.put("url","/sell/seller/product/list");
        return new ModelAndView("common/success");
    }
}
