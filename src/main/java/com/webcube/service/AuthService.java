package com.webcube.service;

import com.blade.ioc.annotation.Bean;
import com.blade.ioc.annotation.Inject;
import com.blade.jdbc.Base;
import com.webcube.config.InitConfig;
import com.webcube.model.user.User;
import org.sql2o.Sql2o;

import java.util.List;

/**
 * @author
 * Edwin Yang
 * 2017/10/19 0019.
 */
@Bean
public class AuthService {

    public boolean checkAuth(User user){
        if(null != user.find()){
            return true;
        }
        return false;
    }
}
