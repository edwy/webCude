package com.webcube.controller.spider;

import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.webcube.model.BookInfo;
import com.webcube.service.spider.BookSpiderService;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;


import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Yang Jiyu
 */
@Path
public class BookBaoSpiderController implements PageProcessor {

    @PostRoute("bookSpider")
    public String startBookSpider(@Param String url , Request request, Response response){
        Site site = new Site();
        site.setCharset("UTF-8");
        Spider bookBaoSpider = Spider.create(new BookBaoSpiderController()).addUrl(url).thread(5);
        bookBaoSpider.start();
        return  null;
    }

    /**部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等*/
    private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
            .addCookie("UM_distinctid", "15f3dd37295e9-0d9a032de259758-12666d4a-1fa400-15f3dd37296171")
            .addCookie("CNZZDATA4769021", "cnzz_eid%3D1500105049-1508566602-null%26ntime%3D1508582803")
            .addCookie("bdshare_firstime","1508570788716; ASPSESSIONIDAAQASQRC=EJDHABHBOGCAIDFMEGEDHJMF; ftcpvrich_fidx=3")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0");

    /**定义需要抓去固定页*/
    public static String dataUrl = "http://www\\.bookbao\\.cc/TXT/down_\\w+\\.html";

    @Override
    public void process(Page page) {
        String filePath = "D:\\eBook\\";
        BookInfo book = new BookInfo();
        page.addTargetRequests(page.getHtml().xpath("//div[@class='listl2']/ul/li/h5").links().all());
        if (page.getUrl().regex(dataUrl).match()) {
            String bookName = page.getHtml().xpath("h1").regex("《([^》]+)》").toString();
            String summary = page.getHtml().$(".con_text span:last-child").toString();
            String downLoadUrl = "www.bookbao.cc"+page.getHtml().$(".downlistbox li:nth-child(4)").regex("href=['\\\"]([^'\\\"]*)['\\\"]").toString();
            book.setBook_name(bookName);
            book.setBook_summary(summary);
            book.setBook_url(downLoadUrl.replaceAll("amp;",""));
            new BookSpiderService().saveBook(book);
        } else {
            System.out.println(page.getUrl());
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

}
