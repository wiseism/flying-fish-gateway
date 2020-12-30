package com.flying.fish.formwork.service;

import com.flying.fish.formwork.bean.CountReq;
import com.flying.fish.formwork.bean.CountRsp;
import com.flying.fish.formwork.bean.CountTotalRsp;
import com.flying.fish.formwork.bean.RouteCountRsp;
import com.flying.fish.formwork.entity.Route;
import com.flying.fish.formwork.util.ApiResult;
import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.PageResult;
import com.flying.fish.formwork.util.RouteConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @Description 统计接口请求业务类
 * @Author JL
 * @Date 2020/12/30
 * @Version V1.0
 */
@Service
public class CountService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RouteService routeService;

    /**
     * 查询路由集合统计结果
     * @param route
     * @param currentPage
     * @param pageSize
     * @return
     */
    public ApiResult countRouteList(Route route, int currentPage, int pageSize){
        PageResult<Route> pageResult = routeService.pageList(route,currentPage, pageSize);
        if (pageResult.getPageSize() > 0){
            String key = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
            Map<String,String> countMap = redisTemplate.opsForHash().entries(key);
            List<Route> routeList = pageResult.getLists();
            List<RouteCountRsp> routeCountRspList = routeList.stream().map(r -> {
                RouteCountRsp rsp = new RouteCountRsp();
                BeanUtils.copyProperties(r, rsp);
                //将routeId缓存的统计值取出
                String count = countMap.get(rsp.getId());
                rsp.setCount(StringUtils.isNotBlank(count) ? Integer.parseInt(count) : 0);
                return rsp;
            }).collect(Collectors.toList());
            PageResult<RouteCountRsp> pageResult1 = new PageResult<>();
            pageResult1.setCurrentPage(currentPage);
            pageResult1.setPageSize(pageSize);
            pageResult1.setTotalNum(pageResult.getTotalNum());
            pageResult1.setLists(routeCountRspList);
            return new ApiResult(pageResult1);
        }
        return new ApiResult(pageResult);
    }

    /**
     * 统计按7天和按24小时维度计算的请求总量
     * @return
     */
    public CountTotalRsp countRequestTotal(){
        CountTotalRsp rsp = new CountTotalRsp();
        rsp.setDayCounts(this.get7DayTotalApiCount());
        rsp.setHourCounts(this.get24HourTotalApiCount());
        return rsp;
    }

    /**
     * 统计7天内请求量数据
     */
    public CountRsp get7DayTotalApiCount(){
        return this.getCount(Constants.DAY);
    }

    /**
     * 统计24小时内请求量数据
     */
    public CountRsp get24HourTotalApiCount(){
        return this.getCount(Constants.HOUR);
    }

    /**
     * 统计天或小时维度的请求量数据
     * @param dateType
     * @return
     */
    private CountRsp getCount(String dateType){
        Object [] infos = getCountInfo(dateType);
        List<String> dates = (List<String>)infos[0];
        String countTag = (String)infos[1];
        List<Integer> counts = new ArrayList<>(dates.size());
        for (String date : dates){
            Map<String,String> countMap = redisTemplate.opsForHash().entries(countTag + date);
            AtomicReference<Integer> count = new AtomicReference<>(0);
            if (!CollectionUtils.isEmpty(countMap)){
                countMap.forEach((k,v)->count.updateAndGet(v1 -> v1 + Integer.parseInt(v)));
            }
            counts.add(count.get());
        }
        CountRsp countRsp = new CountRsp();
        countRsp.setDates(dates);
        countRsp.setCounts(counts);
        return countRsp;
    }

    /**
     * 统计路由请求数据
     * @param countReq      请求对象
     * @param isBalanced    是否负载
     * @return
     */
    public ApiResult count(CountReq countReq, boolean isBalanced){
        Assert.notNull(countReq, "未获取到对象");
        List<String> routeIds = countReq.getRouteIds();
        Assert.isTrue(routeIds != null, "未获取到路由ID");
        String dateType = StringUtils.isNotBlank(countReq.getDateType()) ? countReq.getDateType() : "min";
        Object [] infos = getCountInfo(dateType);
        List<String> dates = (List<String>)infos[0];
        String countTag = (String)infos[1];
        Map<String,List<Integer>> countMap = new HashMap<>(routeIds.size());
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

        List<CountRsp.CountData> datas = new ArrayList<>();
        CountRsp rsp = new CountRsp();
        rsp.setDatas(datas);
        rsp.setDates(dates);
        countMap.forEach((k,v)->{
            CountRsp.CountData data = new CountRsp.CountData();
            data.setRouteId(k);
            data.setCounts(v.toArray(new Integer [v.size()]));
            datas.add(data);
        });
        return new ApiResult(rsp);
    }

    /**
     * 获取统计维度信息
     * @param dateType
     * @return
     */
    private Object[] getCountInfo(String dateType){
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
        return new Object[]{dates, countTag};
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
