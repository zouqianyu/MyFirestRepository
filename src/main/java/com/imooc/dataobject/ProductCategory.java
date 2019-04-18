package com.imooc.dataobject;


import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@DynamicUpdate
@Data
public class ProductCategory {

    /**id*/
    @Id
    public String categoryId ;
    /**名字*/
    public String categoryName ;
    /**编号*/
    public int categoryType ;

    public Date createTime;

    public Date updateTime;

    public ProductCategory(String categoryName, int categoryType,Date createTime) {
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.createTime = createTime;
    }

    public ProductCategory() {
    }
}
