package com.hiraeth.im.orm.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author linxueqi
 * @description
 * @createtime 2020-10-22 11:19
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * Version乐观锁插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisplusInterceptor = new MybatisPlusInterceptor();
        mybatisplusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mybatisplusInterceptor;
    }
}
