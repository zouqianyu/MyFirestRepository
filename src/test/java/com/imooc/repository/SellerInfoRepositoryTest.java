package com.imooc.repository;

import com.imooc.dataobject.SellerInfo;
import com.imooc.utils.KeyUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SellerInfoRepositoryTest extends Exception {

    @Autowired
    private SellerInfoRepository sellerInfoRepository;
    @Test
    public void findByOpenid() {
        SellerInfo sellerInfo = sellerInfoRepository.findByOpenid("zqylxb1993");
        Assert.assertNotNull(sellerInfo);
    }

    @Test
    public void save(){
        SellerInfo sellerInfo = new SellerInfo();
        sellerInfo.setId(KeyUtil.genUniqueKey());
        sellerInfo.setOpenid("zqylxb1993");
        sellerInfo.setUsername("poppy");
        sellerInfo.setPassword("zqy123611");
        sellerInfo.setCreateTime(new Date());

        SellerInfo sellerInfo1 =sellerInfoRepository.save(sellerInfo);
        Assert.assertNotNull(sellerInfo1);
    }
}