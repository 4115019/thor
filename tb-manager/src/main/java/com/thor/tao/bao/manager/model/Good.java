package com.thor.tao.bao.manager.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author huangpin
 * @date 2019-03-11
 */
@Data
public class Good {

    private String nid;

    private String category;

    @JSONField(name = "raw_title")
    private String title;

    @JSONField(name = "pic_url")
    private String picUrl;

    @JSONField(name = "detail_url")
    private String detailUrl;

    @JSONField(name = "view_price")
    private String price;

    @JSONField(name = "view_fee")
    private String viewFee;

    @JSONField(name = "item_loc")
    private String itemLoc;

    @JSONField(name = "view_sales")
    private String viewSales;

    @JSONField(name = "comment_count")
    private String commentCount;

    @JSONField(name = "user_id")
    private String userId;

    @JSONField(name = "nick")
    private String nick;

    private String shopLink;

    public Good format() {
        if (!detailUrl.startsWith("//")
                || !shopLink.startsWith("//")) {
            return null;
        }

        this.detailUrl = "https:" + this.detailUrl;
        this.shopLink = "https:" + this.shopLink;
        this.picUrl = "https:" + this.picUrl;
        return this;
    }
}
