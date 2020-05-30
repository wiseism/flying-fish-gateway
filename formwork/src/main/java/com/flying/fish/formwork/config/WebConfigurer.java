package com.flying.fish.formwork.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.flying.fish.formwork.util.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description 配置类：处理跨域
 * @Author jianglong
 * @Date 2020/05/14
 * @Version V1.0
 */
@Configuration
public class WebConfigurer implements WebFluxConfigurer {
    /*
    1.webflux已经没有了Interceptor的概念，但是可以通过WebFilter的方式实现

    */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        Formatter<Date> formatter = new Formatter<Date>() {
            SimpleDateFormat sdf = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
            @Override
            public String print(Date object, Locale locale) {
                return sdf.format(object);
            }
            @Override
            public Date parse(String text, Locale locale) throws ParseException {
                return sdf.parse(text);
            }
        };
        //对时间，进行格式化
        registry.addFormatterForFieldType(Date.class, formatter);
        registry.addFormatterForFieldType(Timestamp.class, formatter);
        registry.addFormatterForFieldType(LocalDateTime.class, formatter);
    }

    /**
     * json格式转换
     */
    private HttpMessageConverter fastJsonMessageConverter() {
        //配置参数
        FastJsonHttpMessageConverter httpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                //禁止时间格式化，禁止循环引用
                SerializerFeature.WriteDateUseDateFormat,SerializerFeature.DisableCircularReferenceDetect
        );

        //日期转换
        fastJsonConfig.getSerializeConfig().put(Date.class, new SimpleDateFormatSerializer(Constants.YYYY_MM_DD_HH_MM_SS));
        fastJsonConfig.getSerializeConfig().put(Timestamp.class, new SimpleDateFormatSerializer(Constants.YYYY_MM_DD_HH_MM_SS));
        fastJsonConfig.getSerializeConfig().put(LocalDateTime.class, new SimpleDateFormatSerializer(Constants.YYYY_MM_DD_HH_MM_SS));

        //Long转String,防止精度丢失
        fastJsonConfig.getSerializeConfig().put(BigInteger.class, ToStringSerializer.instance);
        fastJsonConfig.getSerializeConfig().put(BigDecimal.class, ToStringSerializer.instance);
        fastJsonConfig.getSerializeConfig().put(Long.class, ToStringSerializer.instance);
        fastJsonConfig.getSerializeConfig().put(Long.TYPE, ToStringSerializer.instance);
        httpMessageConverter.setFastJsonConfig(fastJsonConfig);

        //支持数据格式
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        httpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return httpMessageConverter;
    }

    /**
     * 注册跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/**");
        corsRegistration.allowedOrigins(CorsConfiguration.ALL);
        corsRegistration.allowedHeaders(CorsConfiguration.ALL);
        corsRegistration.allowedMethods(CorsConfiguration.ALL);
        corsRegistration.allowCredentials(true);
        corsRegistration.maxAge(3600L);
    }

}
