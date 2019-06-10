package com.china.mobile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huangpin
 * @date 2019-04-11
 */
public class ClientInfo {

    private final static String COOKIE = "AlteonP=DOVNUvsUYAo23/lGldU3Og$$; JSESSIONID=0000FAHl5p_HofBRa8v4MHDsUXu:-1; HallId=379494; OperatorId=100006277405; StaffCode=A614116";
    private final static String NATAPP_HOST = "dbw7bv.natappfree.cc:80";
    private final static String CLIENT_INFO_URL_TEMPLATE = "http://%s1/dbformrefresh?action=refresh&pk=-1&condition=billId%3D%s2%26CenterType%3DBillId%26CenterValue%3D%s2&url_source=XMLHTTP";
    private final static String CLIENT_BODY = "<FormInfo formid=\"detailForm\" setname=\"com.asiainfo.boss.so.soinfomgr.web.SETBaseUserInfo\" datamodel=\"com.ai.appframe2.web.datamodel.MethodModelForService\" editable=\"false\" implservice_name=\"com.asiainfo.boss.so.instance.base.service.interfaces.IUserInfoQuerySV\" implservice_querymethod=\"getUserInfoNew(String billId)\" conditionname=\"condition\" cols=\"CUST_NAME;AMOUNT_CREDIT;BILL_ID;ACC_ID;IMSI_CODE;YHTX_STATE;OFFER_NAME;OFFER_ID;KDNAME;REGION_ID;AGE_RANGE;KEEPERINFO_STATE;USER_STATE;USER_STATE_ID;NOTICE_FLAG;CUST_AUTH_STATE;HISTORY_OWE;PRE_SAVE;SPEBALANCE_SUM;CURRENT_COST;CURRENT_OWE_FEE;CURRENT_SSHFYE;CURRENT_SSXFJE;VIP_FLAG;4G_FLAG;CREATE_DATE;FIRST_USE_DATE;BAL_ORG_ID;RE_CREATE_DATE;MDB_FLAG;PHONE_ID;IS_CONTROL;LEVEL\"  >\n" +
            "</FormInfo>\n";
    private final static Pattern pattern = Pattern.compile("<tr.*</tr>");

    private final static String COMBINE_BODY = "<TableInfo \n" +
            "tableid=\"tblUseMonth\" setname=\"com.asiainfo.boss.so.ams.bo.SETFreeRes\" settype=\"SET\" tablemodel=\"com.ai.appframe2.web.datamodel.MethodModelForService\" implservice_name=\"com.asiainfo.boss.so.ams.service.interfaces.IQuerySV\" implservice_querymethod=\"getUserOfferCurAndHisToHaBoss(String keyNum, String condition, String billingCycle,DateTime cycleBeginDate, DateTime cycleEndDate)\" \n" +
            "multiselect=\"false\" isquerycount=\"TRUE\" width=\"750px\" height=\"200\" >\n" +
            "<col fieldname='SERV_ID' visible='false' />\n" +
            "<col fieldname='ITEM_ID' visible='false' />\n" +
            "<col fieldname='SUB_DATE' visible='false' />\n" +
            "<col fieldname='PROD_ID' visible='false' />\n" +
            "<col fieldname=\"KEY_NUM\" width=\"90\" title=\"号码\"  />\n" +
            "<col fieldname=\"ITEM_NAME\" width=\"220\" title=\"类型\"  />\n" +
            "<col fieldname=\"FREE_RES\" title=\"包月情况\"  />\n" +
            "<col fieldname=\"FREE_RES_USED\" width=\"120\" title=\"已使用的资源\"  />\n" +
            "<col fieldname=\"FREE_RES_PRIORTITY\" width=\"120\" title=\"免费资源优先级\"  />\n" +
            "<col fieldname=\"VALID_DATE\" width=\"140\" title=\"生效时间\"  />\n" +
            "<col fieldname=\"EXPIRE_DATE\" width=\"140\" title=\"失效时间\"  />\n" +
            "</TableInfo>\n";

    private final static String COMBINE_URL_TEMPLATE = "http://%s1/gridturnpage?action=refresh&pk=-1&condition=keyNum%3D%s2%26condition%3Dcycle%20in(0,1,101)%26billingCycle%3D%s3%26CenterType%3DBillId%26CenterValue%3D%s2%26cycleBeginDate%3D%s4%2000:00:00%26cycleEndDate%3D%s5%2023:59:59&localcache=table&url_source=XMLHTTP";

    public static void main(String[] args) throws InterruptedException {
        System.out.println(CLIENT_BODY.getBytes());
        String mobile = "18838123123";
        String clientInfoUrl = CLIENT_INFO_URL_TEMPLATE.replace("%s1", NATAPP_HOST).replace("%s2", mobile);
        System.out.println(clientInfoUrl);
        String result = HttpUtil.post(clientInfoUrl, CLIENT_BODY.getBytes(), COOKIE);
        System.out.println(result);

        Document document = Jsoup.parse(result);
        String name = document.select("p[n=CUST_NAME]").get(0).text();
        String discount = document.select("p[n=YHTX_STATE]").get(0).text();
        String offerName = document.select("p[n=OFFER_NAME]").get(0).text();
        String kd = document.select("p[n=KDNAME]").get(0).text();
        String region = document.select("p[n=REGION_ID_AIDISPLAY_VAL]").get(0).text();
        String age = document.select("p[n=AGE_RANGE]").get(0).text();
        String guardian = document.select("p[n=KEEPERINFO_STATE]").get(0).text();
        String status = document.select("p[n=USER_STATE_AIDISPLAY_VAL]").get(0).text();
        String notice = document.select("p[n=NOTICE_FLAG_AIDISPLAY_VAL]").get(0).text();
        String nameAuth = document.select("p[n=CUST_AUTH_STATE_AIDISPLAY_VAL]").get(0).text();
        String arrearageHistory = document.select("p[n=HISTORY_OWE_AIDISPLAY_VAL]").get(0).text();
        String preSave = document.select("p[n=PRE_SAVE]").get(0).text();
        String spebalance = document.select("p[n=SPEBALANCE_SUM]").get(0).text();
        String currentCost = document.select("p[n=CURRENT_COST_AIDISPLAY_VAL]").get(0).text();
        String arrearageCurrent = document.select("p[n=CURRENT_OWE_FEE]").get(0).text();
        String currentHF = document.select("p[n=CURRENT_SSHFYE]").get(0).text();
        String currentSSCost = document.select("p[n=CURRENT_SSXFJE]").get(0).text();
        String vip = document.select("p[n=VIP_FLAG]").get(0).text();
        String simType = document.select("p[n=4G_FLAG]").get(0).text();
        System.out.println(name);

//        String combineUrl = COMBINE_URL_TEMPLATE.replace("%s1", NATAPP_HOST)
//                .replace("%s2", mobile)
//                .replace("%s3", "201904")
//                .replace("%s4", "2019-04-01")
//                .replace("%s5", "2019-04-30");
//        String combineResult = HttpUtil.post(combineUrl, COMBINE_BODY.getBytes(), COOKIE);
//
//
//        Matcher matcher = pattern.matcher(combineResult);
//        int count = 0;
//        System.out.println(matcher.groupCount());
//        while (matcher.find()) {
//            count++;
//            if (count != 1) {
//                Document combineDocument = Jsoup.parseBodyFragment("<table>" + matcher.group() + "</table>");
//                Elements elements = combineDocument.select("td");
//                if (elements != null && elements.size() == 7) {
//                    System.out.println(elements.get(0));
//                    System.out.println(elements.get(1));
//                    System.out.println(elements.get(2));
//                    System.out.println(elements.get(3));
//                    System.out.println(elements.get(4));
//                    System.out.println(elements.get(5));
//                    System.out.println(elements.get(6));
//                    System.out.println("\n");
//                }
//            }
//        }
    }
}
