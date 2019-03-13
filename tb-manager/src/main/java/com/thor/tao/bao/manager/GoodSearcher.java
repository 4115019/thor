package com.thor.tao.bao.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.thor.spider.core.Spider;
import com.thor.spider.core.processor.PageProcessor;
import com.thor.spider.downloader.Page;
import com.thor.spider.downloader.ResultItems;
import com.thor.spider.downloader.Site;
import com.thor.spider.parser.selector.Selectable;
import com.thor.tao.bao.manager.model.Good;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author huangpin
 * @date 2019-03-11
 */
public class GoodSearcher implements PageProcessor {

    private Site site = Site.me()
            .setRetryTimes(3).setSleepTime(6000)
            .setUseGzip(true);

    @Override
    public void process(Page page) {
        Selectable regex = page.getHtml().regex("g_page_config.*g_srp_loadCss");
        if (StringUtils.isNotEmpty(regex.toString())) {
            JSONArray goods = JSON.parseObject(regex.regex("\\{.*\\}").toString())
                    .getJSONObject("mods")
                    .getJSONObject("itemlist")
                    .getJSONObject("data")
                    .getJSONArray("auctions");
            List<Good> taoGoods = new ArrayList<Good>();
            for (int i = 0; i < goods.size(); i++) {
                Good good = JSON.parseObject(goods.get(i).toString(), Good.class).format();
                if (good != null) {
                    taoGoods.add(good);
                }
            }
            page.putField("goods", taoGoods);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

        String cookie = "t=bd004b1406b07863243605c6a0d1b08a; thw=cn; cna=Xn2CFJcpozYCAWcljBOXe1vd; hng=CN%7Czh-CN%7CCNY%7C156; tg=0; enc=GxVrP%2BoZFwDg%2FwbHHmcbY4UpYmnW9EbExxnzE8SJIlFi4pOoI0sX3D6FJHmCb1uzO7lKaL7PvXOTA%2BUSinFc%2FA%3D%3D; cookie2=147a83fb96fd8de4b8e61b74fc5b87e4; _tb_token_=ee105ee7779ee; alitrackid=www.taobao.com; lastalitrackid=www.taobao.com; _uab_collina=155229496092987939978912; _cc_=V32FPkk%2Fhw%3D%3D; UM_distinctid=1696c5ab6f1507-00129e825dee73-36637902-1fa400-1696c5ab6f2565; ali_ab=103.37.140.37.1552301161777.5; mt=ci=0_0; v=0; uc1=cookie14=UoTZ5iY22ThIFA%3D%3D; JSESSIONID=81D3F962D89E91C132B61BDFB963A602;";
        String[] s = cookie.replace(" ", "").split(";");
        CookieStore cookieStore = new BasicCookieStore();
        for (String subCookie : s) {
            BasicClientCookie basicClientCookie = new BasicClientCookie(subCookie.split("=")[0], subCookie.split("=")[1]);
            basicClientCookie.setDomain("s.taobao.com");
            cookieStore.addCookie(basicClientCookie);
        }

        //single download
        GoodSearcher goodSearcher = new GoodSearcher();
        goodSearcher.getSite().setCookieStore(cookieStore);
        Spider spider = Spider.create(goodSearcher)
                .thread(1);
        String rootUrl = "https://s.taobao.com/search?q=书本";
//        String rootUrl = "https://www.taobao.com/";


        List<Good> allGoods = new ArrayList<Good>();
        for (int i = 0; i < 10; i++) {
            ResultItems resultItems = spider.get(i == 0 ? rootUrl : rootUrl + "&data-key=s&data-value=" + (44 * i));
            if (resultItems.get("goods") != null) {
                allGoods.addAll((Collection<? extends Good>) resultItems.get("goods"));
            }
        }
        System.out.println(allGoods);
    }
}
