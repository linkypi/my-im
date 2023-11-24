package com.hiraeth.im.business.config;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

/**
 * Description
 * Created by linxueqi on 2021/1/21 14:41
 */
@Slf4j
@Configuration
public class DruidConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource")
    public DataSource druid(){
        return new DruidDataSource();
    }

    /**
     * 设置允许多条SQL同时执行： multi-statement-allow
     * yml配置无效，只能通过代码设置！！！
     * @return
     */
    @Bean
    public WallFilter wallFilter(DruidDataSource dataSource){
        WallFilter wallFilter = null;
        final List<Filter> proxyFilters = dataSource.getProxyFilters();
        boolean exist = false;
        for(Filter filter : proxyFilters){
            if(filter instanceof  WallFilter){
                ((WallFilter)filter).setConfig(wallConfig());
                exist = true;
            }
        }
        if(!exist){
            wallFilter = new WallFilter();
            wallFilter.setConfig(wallConfig());
            proxyFilters.add(wallFilter);
            dataSource.setProxyFilters(proxyFilters);
        }
        return wallFilter;
    }

    @Bean
    public Slf4jLogFilter slf4jLogFilter(DruidDataSource dataSource){
        Slf4jLogFilter slf4jLogFilter = null;
        final List<Filter> proxyFilters = dataSource.getProxyFilters();
        boolean exist = false;
        for(Filter filter : proxyFilters){
            if(filter instanceof Slf4jLogFilter){
                ((Slf4jLogFilter)filter).setResultSetLogEnabled(false);
                exist = true;
            }
        }
        if(!exist){
            slf4jLogFilter = new Slf4jLogFilter();
            slf4jLogFilter.setResultSetLogEnabled(false);
            dataSource.setProxyFilters(proxyFilters);
        }
        return slf4jLogFilter;
    }

    @Bean
    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        // 允许一次执行多条语句
        config.setMultiStatementAllow(true);
        config.setNoneBaseStatementAllow(true);
        return config;
    }
}
