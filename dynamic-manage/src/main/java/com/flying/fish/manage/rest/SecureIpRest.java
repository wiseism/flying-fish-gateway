package com.flying.fish.manage.rest;

import com.flying.fish.formwork.base.BaseRest;
import com.flying.fish.formwork.entity.SecureIp;
import com.flying.fish.formwork.service.SecureIpService;
import com.flying.fish.formwork.util.ApiResult;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.manage.bean.SecureIpReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description Ip管理控制器
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@RestController
@RequestMapping("/ip")
public class SecureIpRest extends BaseRest {

    @Resource
    private SecureIpService secureIpService;

    @RequestMapping(value = "/add", method = {RequestMethod.POST})
    @ResponseBody
    public Object add(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setCreateTime(new Date());
        this.validate(secureIp);
        //验证注册服务是否重复
        SecureIp qSecureIp = new SecureIp();
        qSecureIp.setIp(secureIp.getIp());
        long count = secureIpService.count(qSecureIp);
        Assert.isTrue(count <= 0, "该IP已添加，请不要重复添加");
        secureIpService.save(secureIp);
        return new ApiResult();
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object delete(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        secureIpService.deleteById(ip);
        return new ApiResult();
    }

    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    @ResponseBody
    public Object update(@RequestBody SecureIp secureIp) {
        Assert.notNull(secureIp, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(secureIp.getIp()), "IP值不能为空");
        secureIp.setUpdateTime(new Date());
        this.validate(secureIp);
        secureIpService.update(secureIp);
        return new ApiResult();
    }

    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object findById(@RequestParam String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "IP值不能为空");
        return new ApiResult(secureIpService.findById(ip));
    }

    @RequestMapping(value = "/pageList", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Object pageList(@RequestBody SecureIpReq secureIpReq){
        Assert.notNull(secureIpReq, "未获取到对象");
        int currentPage = getCurrentPage(secureIpReq.getCurrentPage());
        int pageSize = getPageSize(secureIpReq.getPageSize());
        SecureIp secureIp = new SecureIp();
        if (StringUtils.isNotBlank(secureIpReq.getIp())){
            secureIp.setIp(secureIpReq.getIp());
        }
        if (StringUtils.isNotBlank(secureIpReq.getStatus())){
            secureIp.setStatus(secureIpReq.getStatus());
        }
        return new ApiResult(secureIpService.pageList(secureIp,currentPage, pageSize));
    }

}
