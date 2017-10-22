package com.webcube.service;

import com.blade.ioc.annotation.Bean;

import com.webcube.model.user.User;


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
