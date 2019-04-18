package com.imooc.sell;

import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LoggerTest {
    private final Logger logger = LoggerFactory.getLogger(LoggerTest.class);
    @Test
    public void  test1(){
        logger.debug("debug....");
        logger.info("info....");
        logger.error("error.....");

        String name = "poppy";
        String pawd = "123466";
        logger.info("name={}, pwd={} ",name,pawd);
    }
}
