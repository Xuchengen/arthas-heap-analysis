package com.github.xuchengen.aha.vo;

import java.math.BigDecimal;

public class OrderDO {

    private String orderNo;

    private String goodsName;

    private BigDecimal amount;

    private transient byte[] bytes = new byte[1024 * 1024];

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
