package com.imooc.controller;

import com.imooc.enums.ResultEnum;
import com.imooc.exception.SellException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    public WxMpService wxMpService;
    @Autowired
    public WxMpService wxOpenService;


    @GetMapping("/authorize")
    public String authorize(@RequestParam(value = "returnUrl") String returnUrl) throws UnsupportedEncodingException {
        String redirectUrl =  wxMpService.oauth2buildAuthorizationUrl("http://zouqianyu.natapp1.cc/sell/wechat/userInfo",WxConsts.OAUTH2_SCOPE_BASE, URLEncoder.encode(returnUrl,"UTF-8"));
        log.info("[微信网页授权] redirectUrl = {} " ,redirectUrl);
        return redirectUrl;
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam(value = "code") String code,@RequestParam(value = "state") String returnUrl) {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
        }catch (WxErrorException e){
            log.error("[微信网页授权] {} " , e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }
        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("[微信网页授权] openid ={}" ,openId);
        return returnUrl+"?openid="+openId;
    }

    @GetMapping("/qrAuthorize")
    public String qrAuthorize(@RequestParam(value = "returnUrl") String returnUrl){
        String url = "http://zouqianyu.natapp1.cc/sell/wechat/qrUserInfo";
        String redirectUrl = wxOpenService.buildQrConnectUrl(url,WxConsts.QRCONNECT_SCOPE_SNSAPI_LOGIN,URLEncoder.encode(returnUrl));
        return "redirectUrl:" + redirectUrl;
    }

    @GetMapping("/qrUserInfo")
    public String qrUserInfo(@RequestParam(value = "code") String code,@RequestParam(value = "state") String returnUrl){
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = new WxMpOAuth2AccessToken();
        try {
            wxMpOAuth2AccessToken = wxOpenService.oauth2getAccessToken(code);
        }catch (WxErrorException e){
            log.error("[微信网页授权] {} " , e);
            throw new SellException(ResultEnum.WECHAT_MP_ERROR.getCode(),e.getError().getErrorMsg());
        }
        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("[微信网页授权] openid ={}" ,openId);
        return returnUrl+"?openid="+openId;    }

}
