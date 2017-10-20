package com.webcube.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.blade.Blade;
import com.blade.event.BeanProcessor;
import com.blade.ioc.annotation.Bean;
import com.blade.jdbc.Base;
import com.blade.mvc.view.template.JetbrickTemplateEngine;


/**
 * @author  Edwin Yang
 * on 2017/10/20
 * Created
 */
@Bean
public class InitConfig  implements BeanProcessor{

    @Override
    public void processor(Blade blade) {

        String version = blade.environment().get("app.version", "0.0.1");

        // 配置数据库
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriverClassName(blade.environment().get("jdbc.driver", "com.mysql.jdbc.Driver"));
        dataSource.setUrl(blade.environment().get("jdbc.url", "jdbc:mysql://127.0.0.1:3306/data_resp"));
        dataSource.setUsername(blade.environment().get("jdbc.username", "root"));
        dataSource.setPassword(blade.environment().get("jdbc.password", "root"));

        dataSource.setInitialSize(5);
        dataSource.setMaxActive(5);
        dataSource.setMinIdle(2);
        dataSource.setMaxWait(6000);
        Base.open(dataSource);

        // 配置模板引擎
        JetbrickTemplateEngine templateEngine = new JetbrickTemplateEngine();
        templateEngine.getGlobalContext().set("version", version);
        blade.templateEngine(templateEngine);
    }
}
