package com.flying.fish.gateway.tiemr;

import com.flying.fish.formwork.util.Constants;
import com.flying.fish.formwork.util.RouteConstants;
import com.flying.fish.gateway.cache.CountCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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

    private static String minTag = "";
    private static String hourTag = "";
    private static String dayTag = "";

    /**
     * 每1分钟执行一次缓存同步
     */
    @Scheduled(cron = "0 * * * * ?")
    public void writerCache(){
        log.info("执行定时任务：统计数据同步到redis缓存...");
        //保存按分钟统计的数据
        ConcurrentHashMap<String,Integer> minMap = CountCache.getCacheMap();

        //保存按分钟统计的数据
        String minKey = RouteConstants.COUNT_MIN_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_MIN);
        if (minTag.equals(minKey)){
            //清除，防止历史缓存数据
            redisTemplate.delete(minKey);
        }
        syncCountCache(minKey, minMap);
        minTag = minKey;

        //保存按小时统计的数据
        String hourKey = RouteConstants.COUNT_HOUR_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_HOUR);
        //清除下一个小时的历史结果，防止历史缓存数据叠加
        if (!hourTag.equals(hourKey)) {
            redisTemplate.delete(hourKey);
        }
        syncCountCache(hourKey, minMap);
        hourTag = hourKey;

        //保存按天统计的数据
        String dayKey = RouteConstants.COUNT_DAY_KEY + DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
        syncCountCache(dayKey, minMap);
        //数据缓存7天，7天后，redis清除指定KEY缓存数据
        redisTemplate.expire(dayKey, 7, TimeUnit.DAYS);

        //每次统计完清除缓存统计数据，重新记录下一分钟请求量
        CountCache.clear();
    }

    /**
     * 考虑网关服务的集群式架构设计，需要累加已知统计缓存数据
     * @param key
     * @param map
     */
    public void syncCountCache(String key, Map<String,Integer> map){
        map.forEach((k,v)->{
            String value = (String) redisTemplate.opsForHash().get(key, k);
            if (StringUtils.isNotBlank(value)){
                int count = 0;
                try{
                    count = Integer.parseInt(value);
                }catch(Exception e){
                    log.error("redis get key:{} & field:{} to value is null", key, k);
                }
                v += count;
            }
            redisTemplate.opsForHash().put(key, k, String.valueOf(v));
        });
    }

}
