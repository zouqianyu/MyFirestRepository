package com.imooc.controller;

import com.imooc.dataobject.ProductCategory;
import com.imooc.form.CategoryForm;
import com.imooc.service.CategoryService;
import com.imooc.utils.KeyUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

@RequestMapping("/seller/category")
@Controller
public class SellerCategoryController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/list")
    public ModelAndView list(Map<String,Object> map){
        List<ProductCategory> productCategoryList =  categoryService.findAll();
        map.put("categoryList",productCategoryList);
        return new ModelAndView("category/list");
    }

    @GetMapping("/index")
    public ModelAndView index(@RequestParam(value = "categoryId",required = false) String categoryId,Map<String,Object> map){
        if(!StringUtils.isEmpty(categoryId)){
            ProductCategory productCategory =  categoryService.findOne(categoryId);
            map.put("category",productCategory);
        }
        return new ModelAndView("category/index",map);
    }

    @PostMapping("/save")
    public ModelAndView save(@Valid CategoryForm categoryForm,
                             BindingResult bindingResult ,
                             Map<String,Object> map){
       if(bindingResult.hasErrors()){
        map.put("msg",bindingResult.getFieldError().getDefaultMessage());
        map.put("url","/sell/seller/category/index");
        return new ModelAndView("common/error", map);
       }
       ProductCategory productCategory = new ProductCategory();
       if(StringUtils.isEmpty(categoryForm.getCategoryId())){
            //add
           categoryForm.setCategoryId(KeyUtil.genUniqueKey());
           productCategory.setCreateTime(new Date());
       }else{
           //update
           productCategory = categoryService.findOne(categoryForm.getCategoryId());
       }
        BeanUtils.copyProperties(categoryForm,productCategory);
        categoryService.save(productCategory);
        map.put("url", "/sell/seller/category/list");
        return new ModelAndView("common/success", map);
    }
}
