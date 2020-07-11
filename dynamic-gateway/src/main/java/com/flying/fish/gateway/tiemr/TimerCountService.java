package com.flying.fish.gateway.tiemr;

import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.RouteConstants;
import com.flying.fish.gateway.cache.CountCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description 定时执行路由访问量统计业务类
 * @Author jianglong
 * @Date 2020/07/07
 * @Version V1.0
 */
@Slf4j
@Service
public class TimerCountService {

    @Resource
    private RedisTemplate redisTemplate;

    private static String hourTag = "";
    private static String dayTag = "";

    /**
     * 每1分钟执行一次缓存同步
     */
    @Scheduled(cron = "0 * * * * ?")
    public void writerCache(){
        log.info("执行定时任务：统计数据同步到redis缓存...");
        //保存按天统计的数据
        ConcurrentHashMap<String,Integer> dayMap = CountCache.getCacheMap();
        Map<String,String> map = new HashMap<>(dayMap.size());

        //保存按分钟统计的数据
        String minKey = RouteConstants.COUNT_MIN_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_MIN);
        redisTemplate.delete(minKey);
        Map<String,Integer> oldDayMap = this.readDayCache();
        Map<String,Integer> minMap = this.countDifference(dayMap, oldDayMap);
        minMap.forEach((k,v)->map.put(k,String.valueOf(v)));
        redisTemplate.opsForHash().putAll(minKey, map);

        //保存按小时统计的数据
        String hourKey = RouteConstants.COUNT_HOUR_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_HOUR);
        Map<String,Integer> topHourMap = this.readHourCache();
        map.clear();
        redisTemplate.delete(hourKey);
        boolean isEqHour = hourTag.equals(hourKey);
        minMap.forEach((k,v) -> {
            Integer value ;
            if (isEqHour) {
                value = topHourMap.get(k);
                value = (value == null ? 0 : value) + v;
            }else {
                value = v;
            }
            map.put(k, String.valueOf(value));
        });
        hourTag = hourKey;
        redisTemplate.opsForHash().putAll(hourKey, map);

        //保存按天统计的数据
        String dayKey = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
        boolean isEqDay = dayTag.equals(dayKey);
        map.clear();
       //同一天，则默认直接取dayMap数据，如果不是同一天，则求差值
        if (isEqDay){
            dayMap.forEach((k,v)->map.put(k,String.valueOf(v)));
        }else {
            minMap.forEach((k,v)->map.put(k,String.valueOf(v)));
            dayTag = dayKey;
        }
        redisTemplate.opsForHash().putAll(dayKey, map);
        //数据缓存7天，7天后，redis清除指定KEY缓存数据
        redisTemplate.expire(dayKey, 7, TimeUnit.DAYS);

        //跨天，赋值完毕，清空缓存
        if (!isEqDay) {
            CountCache.clear();
        }
        map.clear();
    }

    /**
     * 读取当天的缓存数据
     * @return
     */
    private Map<String, Integer> readDayCache(){
        String day = DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
        String key = RouteConstants.COUNT_DAY_KEY + day;
        Map<String, String> hashMap = redisTemplate.opsForHash().entries(key);
        if (hashMap != null){
            Map<String, Integer> dayMap = new HashMap<>(hashMap.size());
            hashMap.forEach((k,v)->dayMap.put(k, Integer.parseInt(v)));
            return dayMap;
        }
        return null;
    }

    /**
     * 读取一个小时的缓存数据
     * @return
     */
    private Map<String, Integer> readHourCache(){
        String hour = DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_HOUR);
        String key = RouteConstants.COUNT_HOUR_KEY + hour;
        Map<String, String> hashMap = redisTemplate.opsForHash().entries(key);
        if (hashMap != null){
            Map<String, Integer> dayMap = new HashMap<>(hashMap.size());
            hashMap.forEach((k,v)->dayMap.put(k, Integer.parseInt(v)));
            return dayMap;
        }
        return null;
    }

    /**
     * 计算两个时间数组之间的统计差，生成新的统计维度数组
     * @param dayMap
     * @param oldDayMap
     * @return
     */
    private Map<String,Integer> countDifference(Map<String,Integer> dayMap, Map<String,Integer> oldDayMap){
        if (oldDayMap == null || oldDayMap.size()<=0){
            return dayMap;
        }
        Map<String,Integer> newMap = new HashMap<>(dayMap.size());
        if (dayMap == null || dayMap.size() <= 0){
            oldDayMap.forEach((k,v)-> newMap.put(k,0));
        }else {
            for (Map.Entry<String, Integer> entry : dayMap.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                Integer oldValue = oldDayMap.get(key);
                Integer newValue = 0;
                oldValue = oldValue == null ? newValue : oldValue;
                if (value != null) {
                    newValue = (value >= oldValue) ? value - oldValue : value;
                }
                newMap.put(key, newValue);
            }
        }
        return newMap;
    }
}
