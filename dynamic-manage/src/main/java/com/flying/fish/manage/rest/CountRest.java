package com.flying.fish.manage.rest;

import com.flying.fish.formwork.util.ApiResult;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.RouteConstants;
import com.flying.fish.manage.bean.CountReq;
import com.flying.fish.manage.bean.CountRsp;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description
 * @Author jianglong
 * @Date 2020/07/07
 * @Version V1.0
 */
@RestController
@RequestMapping("/count")
public class CountRest {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 负载类型的请求（需要在缓存filed前加一个balanced前缀，用于区分普通路由ID）
     * @param countReq
     * @return
     */
    @RequestMapping(value = "/balanced/request", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult balancedRequest(@RequestBody CountReq countReq) {
        return count(countReq, true);
    }

    /**
     * 请求
     * @param countReq
     * @return
     */
    @RequestMapping(value = "/request", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult request(@RequestBody CountReq countReq) {
        return count(countReq, false);
    }

    /**
     * 流量
     * @param routeIds
     * @return
     */
    @RequestMapping(value = "/flux", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult flux(@RequestParam String [] routeIds ) {
        Assert.isTrue(routeIds != null, "未获取到对象ID");
        return new ApiResult();
    }

    /**
     * 统计路由请求数据
     * @param countReq      请求对象
     * @param isBalanced    是否负载
     * @return
     */
    private ApiResult count(CountReq countReq,  boolean isBalanced){
        Assert.notNull(countReq, "未获取到对象");
        List<String> routeIds = countReq.getRouteIds();
        Assert.isTrue(routeIds != null, "未获取到路由ID");
        String dateType = StringUtils.isNotBlank(countReq.getDateType()) ? countReq.getDateType() : "min";
        List<String> dates;
        String countTag ;
        if (Constants.MIN.equals(dateType)){
             dates = this.getCountMins();
            countTag =  RouteConstants.COUNT_MIN_KEY;
        }else if (Constants.HOUR.equals(dateType)){
            dates = this.getCountHours();
            countTag =  RouteConstants.COUNT_HOUR_KEY;
        }else if (Constants.DAY.equals(dateType)){
            dates = this.getCountDays();
            countTag =  RouteConstants.COUNT_DAY_KEY;
        }else {
            throw new IllegalArgumentException("统计时间类型错误");
        }

        Map<String,List<Integer>> countMap = new HashMap<>(routeIds.size());
        CountRsp rsp = new CountRsp();
        List<CountRsp.CountData> datas = new ArrayList<>();
        rsp.setDatas(datas);
        rsp.setDates(dates);
        for (String date : dates){
            for (String routeId : routeIds){
                String key = countTag + date;
                String field = (isBalanced ? RouteConstants.BALANCED + "-" : "") + routeId;
                String count = (String) redisTemplate.opsForHash().get(key, field);
                List<Integer> counts = countMap.get(routeId);
                if (counts == null){
                    counts = new ArrayList<>();
                }
                counts.add(count != null ? Integer.parseInt(count) : 0);
                countMap.put(routeId, counts);
            }
        }

        countMap.forEach((k,v)->{
            CountRsp.CountData data = new CountRsp.CountData();
            data.setRouteId(k);
            data.setCounts(v.toArray(new Integer [v.size()]));
            datas.add(data);
        });
//        String json = JSONObject.toJSONString(rsp);
//        System.out.println(json);
        return new ApiResult(rsp);
    }

    /**
     * 按天生成统计范围
     * @return
     */
    private List<String> getCountDays(){
        List<String>  days = new ArrayList<>();
        for (int i = 6; i>=0; i--){
            Date dayDate = DateUtils.addDays(new Date(), - i);
            String day = DateFormatUtils.format(dayDate, "yyyyMMdd");
            days.add(day);
        }
        return days;
    }

    /**
     * 按小时生成统计范围
     * @return
     */
    private List<String> getCountHours(){
        List<String> hours = new ArrayList<>();
        for (int i = 23; i>=0; i--){
            hours.add(DateFormatUtils.format(DateUtils.addHours(new Date(), - i), "HH"));
        }
        return hours;
    }

    /**
     * 按分钟生成统计范围
     * @return
     */
    private List<String> getCountMins(){
        List<String> mins = new ArrayList<>();
        for (int i = 59; i>=0; i--){
            mins.add(DateFormatUtils.format(DateUtils.addMinutes(new Date(), - i), "mm"));
        }
        return mins;
    }

}
