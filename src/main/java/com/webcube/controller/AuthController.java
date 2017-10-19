package com.webcube.controller;

import com.alibaba.druid.filter.AutoLoad;
import com.blade.ioc.annotation.Inject;
import com.blade.mvc.annotation.GetRoute;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.webcube.model.user.User;
import com.webcube.service.AuthService;

/**
 * @author Yang Jiyu
 * Created
 */
@Path
public class AuthController {

    @Inject
    private AuthService authService;

    @GetRoute("index")
    public String toIndex(){
        return "index.jsp";
    }

    @GetRoute("login")
    public String toLogin(){
        return "login.jsp";
    }

    @PostRoute("doLogin")
    public String doLogin(User user, Request request, Response response){

        if(authService.checkAuth(user)){
            response.redirect("/index");
        }else{

        }

        return null;
    }
}
