package com.webcube;

import com.blade.Blade;

/**
 * @author Yang Jiyu
 * Created
 */
public class Application {

    public static void main(String[] args){

        Blade.me().get("/",(request,response) -> response.text("Hello World")).start(Application.class,args);

    }
}
