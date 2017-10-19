package com.webcube.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.blade.Blade;
import com.blade.event.BeanProcessor;
import com.blade.ioc.annotation.Bean;
import com.blade.jdbc.Base;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yang Jiyu
 * Created
 */
@Bean
@Slf4j
public class DbConfig implements BeanProcessor {

    @Override
    public void processor(Blade blade) {

        DruidDataSource dataSource = new DruidDataSource();
        //基本配置初始化
        dataSource.setDriverClassName("jdbc:mysql://localhost:3306/data_resp");
        dataSource.setUsername("root");
        dataSource.setPassword("atPw-3891");

        //配置初始化大小、最小、最大
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(200);

        //配置获取连接等待超时的时间
        dataSource.setMaxWait(60000);

        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);

        //配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(30000);

        // 打开PSCache，并且指定每个连接上PSCache的大小
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        //配置监控统计拦截的filters，去掉后监控界面sql无法统计
/*        try {
            dataSource.setFilters("stat");
            dataSource.setRemoveAbandoned(true);
            dataSource.setRemoveAbandonedTimeout(60);
            dataSource.setLogAbandoned(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        Base.open(dataSource);
        System.out.println("数据库初始成功");


    }
}
