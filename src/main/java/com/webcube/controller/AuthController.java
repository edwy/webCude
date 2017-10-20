package com.webcube.controller;

import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.webcube.model.user.User;
import com.webcube.service.AuthService;

/**
 * @author Yang Jiyu
 * Created
 * 用户登录认证页面;
 */
@Path
public class AuthController {

    @Inject
    private AuthService authService;

    @GetRoute("index")
    public String toIndex(){
        return "index.html";
    }

    @GetRoute("login")
    public String toLogin(){
        return "login.html";
    }

    @PostRoute("doLogin")
    public String doLogin(@Param User user, Request request, Response response){

        if(authService.checkAuth(user)){
            response.redirect("/index");
        }else{
            response.text("登录失败");
        }

        return null;
    }
}
