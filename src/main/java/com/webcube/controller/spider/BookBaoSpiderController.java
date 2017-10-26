package com.webcube.controller.spider;

import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PostRoute;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.webcube.model.BookInfo;
import com.webcube.service.spider.BookSpiderService;
import com.webcube.utils.DownloadUtil;
import com.webcube.utils.FileUtil;
import org.apache.commons.codec.net.URLCodec;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;


import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    public static String dataUrl = "http://www\\.bookbao\\.cc/TXT/down_\\w+\\.html";

    public static String contentPage = "^http://www\\.bookbao\\.cc/book\\.php\\?txt=/TXT/((%[0-9A-Fa-f]{2}){2})+\\.txt$";

    public static String urlSuffix = "\\&yeshu=";

    public static String numberReg = "[0-9]+";

    @PostRoute("bookSpider")
    public String startBookSpider(@Param String url, Request request, Response response) {
        Site site = new Site();
        site.setCharset("UTF-8");
        Spider bookBaoSpider = Spider.create(new BookBaoSpiderController()).addUrl(url).thread(5).addPipeline(new FilePipeline());
        bookBaoSpider.start();
        return null;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().xpath("//div[@class='listl2']/ul/li/h5").links().all());
        //明细页,抓取在线阅读URL
        if (page.getUrl().regex(dataUrl).match()) {
            //转换内容页URL中汉字为encod=GB2312,添加TargetRequest
            page.addTargetRequest(urlTransform(page.getHtml().$(".downlistbox > li:nth-child(1) > a:nth-child(1)").links().toString()));
            //如果是内容页,获取当前页数和当前页内容,并循环获取每一页,保存本地文件;
        } else if (page.getUrl().regex(contentPage).match()) {
            //获取最后一页,循环添加TargetRequest
            String endPageUrl = urlTransform(page.getHtml().xpath("/html/body/div/div").links().all().get(13).toString());
            //正常获取应该是没有页数的;
            //也有可能存在有页数的;
            Integer endPageIndex = Integer.parseInt(subUrl(endPageUrl,"suffix"));
                        for (int i = 0; i <= endPageIndex; i++) {
                if(page.getUrl().regex(urlSuffix).match()){
                    page.addTargetRequest(subUrl(page.getUrl().toString(),"pre")+i);
                }else{
                    page.addTargetRequest(urlTransform(page.getUrl() + "&yeshu=" + i));
                }

            }
            //最终目标页;
        } else if (page.getUrl().regex(contentPage+urlSuffix+numberReg).match()) {
            String bookName = page.getHtml().xpath("/html/body/h1/text()").toString();
            String currentPageContents = page.getHtml().xpath("//div[@class='ddd']/text()").toString();
            //如果是第一页,则建立当前小说名字的文件;并建立文件
            //else打开文件写入后续页内容
            if(0 == Integer.parseInt(subUrl(page.getUrl().toString(),"suffix"))){
                if(FileUtil.createDir(dirName + bookName)){
                    //创建文件并写入内容;
                    FileUtil.createFile(dirName + bookName + "/" + bookName + ".txt");
                }else{
                    FileUtil.writeFile(dirName + bookName + "/" + bookName + ".txt",currentPageContents);
                }
            }else{
                FileUtil.writeFile(dirName + bookName + "/" + bookName + ".txt",currentPageContents);
            }
        } else {
            page.setSkip(true);
        }
    }

    /**
     * @param url
     * @return
     * @throws Exception
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
     * @param
     * @return
     * @throws Exception
     * [0] 为需要切的URL [1]为是否需要后缀标识
     */
    public String subUrl(String ... values) {
        String regEX = numberReg , result="";
        if("pre".equals(values[1])){
            regEX = contentPage;
        }
        Matcher matcher = Pattern.compile(regEX).matcher(values[0]);
        while(matcher.find()){
            result = matcher.group();
        }
        return result;
    }

}