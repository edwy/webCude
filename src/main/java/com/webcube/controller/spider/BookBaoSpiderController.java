package com.webcube.controller.spider;

import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.webcube.utils.FileUtil;
import org.apache.commons.codec.net.URLCodec;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;


import us.codecraft.webmagic.processor.PageProcessor;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yang Jiyu
 */
@Path
public class BookBaoSpiderController implements PageProcessor {

    URLCodec codec = new URLCodec();

    private static String dirName = "D:/ebook/";

    /**
     * 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
     */
    private Site site = Site.me().setRetryTimes(10).setSleepTime(5000).setTimeOut(5000)
            .addCookie("UM_distinctid", "15f3dd37295e9-0d9a032de259758-12666d4a-1fa400-15f3dd37296171")
            .addCookie("CNZZDATA4769021", "cnzz_eid%3D1500105049-1508566602-null%26ntime%3D1508582803")
            .addCookie("bdshare_firstime", "1508570788716; ASPSESSIONIDAAQASQRC=EJDHABHBOGCAIDFMEGEDHJMF; ftcpvrich_fidx=3")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0");

    /**
     * 定义需要抓去固定页
     */
    public static String listUrl = "http://www\\.bookbao\\.cc/TXT/list2_[0-9]+\\.html";
    public static String dataUrl = "http://www\\.bookbao\\.cc/TXT/down_\\w+\\.html";
    public static String contentPage = "http://www\\.bookbao\\.cc/book\\.php\\?txt=/TXT/((%[0-9A-Fa-f]{2}){2})+\\.txt";
    public static String urlSuffix = "\\&yeshu=";
    public static String numberReg = "[0-9]+";

    @PostRoute("bookSpider")
    public String startBookSpider(@Param String url, Request request, Response response) {
        Site site = new Site();
        site.setCharset("UTF-8");
        Spider bookBaoSpider = Spider.create(new BookBaoSpiderController()).addUrl(url).thread(20);
        bookBaoSpider.start();
        return null;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {

        String pre = "^" , end = "$",slash ="/",txt = ".txt";
        Integer maxListPageIndex = 0;
        //so fool  i need downloer
        if(page.getUrl().regex(listUrl).match()){
            if(1 == findNum(page.getUrl().toString())){
                maxListPageIndex = Integer.parseInt(page.getHtml().$(".listl2 > dl:nth-child(3) > code:nth-child(5) > a:nth-child(10)").regex("(?<=>).*(?=</a>)").toString());
                page.addTargetRequests(page.getHtml().xpath("//div[@class='listl2']/ul/li/h5").links().all());
                for(int i = 1; i<maxListPageIndex;i++){
                    page.addTargetRequest("http://www.bookbao.cc/TXT/list2_"+i+".html");
                }
            }else{
                page.addTargetRequests(page.getHtml().xpath("//div[@class='listl2']/ul/li/h5").links().all());
            }
        } else if (page.getUrl().regex(dataUrl).match()) {
            page.addTargetRequest(urlTransform(page.getHtml().$(".downlistbox > li:nth-child(1) > a:nth-child(1)").links().toString()));
        } else if (page.getUrl().regex(pre+contentPage+end).match()) {
            String endPageUrl = urlTransform(page.getHtml().xpath("/html/body/div/div").links().all().get(13).toString());
            Integer endPageIndex = findNum(endPageUrl);
            for (int i = 0; i <= endPageIndex; i++) {
                    page.addTargetRequest(urlTransform(page.getUrl() + "&yeshu=" + i));
            }
        } else if (page.getUrl().regex(contentPage+urlSuffix+numberReg).match()) {
            String bookName = page.getHtml().xpath("/html/body/h1/text()").toString();
            String currentPageContents = page.getHtml().xpath("//div[@class='ddd']/text()").toString();
            String dir = dirName + bookName;
            String fileName = dir + slash + bookName + txt;
            if(0 == findNum(page.getUrl().toString())){
                FileUtil.createDir(dir);
                FileUtil.createFile(fileName);
            }
            FileUtil.writeFile(fileName,currentPageContents);
        } else {
            page.setSkip(true);
        }
    }

    /**n
     * @param url
     * @return
     * @throws Exception
     * 中文转换GB2312编码
     */
    public String urlTransform(String url) {
        String encodeUrl = "";
        String chinese = "";
        String regex = "([\u4e00-\u9fa5]+)";
        Matcher matcher = Pattern.compile(regex).matcher(url);
        while (matcher.find()) {
            chinese += matcher.group(0);
        }
        try {
            encodeUrl = url.replaceAll("[\u4e00-\u9fa5]+", codec.encode(chinese, "gb2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeUrl;
    }

    /**
     * @param url
     * @return
     * @throws Exception
     * 解决字符串中数字
     */
    public Integer findNum(String url) {
        Integer result = 0;
        String regEX = numberReg ;
        Matcher matcher = Pattern.compile(regEX).matcher(url);
        while(matcher.find()){
            result = Integer.parseInt(matcher.group());
        }
        return result;
    }

}