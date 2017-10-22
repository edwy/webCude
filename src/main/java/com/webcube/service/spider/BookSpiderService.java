package com.webcube.service.spider;

import com.blade.ioc.annotation.Bean;
import com.webcube.model.BookInfo;

/**
 * @author Yang Jiyu
 */
@Bean
public class BookSpiderService {

    public void saveBook(BookInfo bookBao){
        bookBao.save();
    }
}
