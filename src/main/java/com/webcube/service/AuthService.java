package com.webcube.service;

import com.webcube.model.user.User;

/**
 * @author
 * Edwin Yang
 * 2017/10/19 0019.
 */
public class AuthService {

    public boolean checkAuth(User user){
        boolean selResult = false;
        User loginUser = user.query("select * From t_user where username = ? and password = ?",user.getUserName(),user.getPassWord());
        if(null != loginUser){
            selResult = true;
        }
        return selResult;
    }

}
