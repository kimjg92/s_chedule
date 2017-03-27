package com.shinhan.fcmexam;

import android.graphics.drawable.Drawable;

/**
 * Created by GYU on 2017-03-26.
 */

public class BankFragmentListViewItem {
    private Drawable iconDrawable ;
    private String tradeDate ;
    private String tradeTime ;
    private String tradeAmount;

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public String getTradeAccount() {
        return tradeAccount;
    }

    private String tradeAccount;

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public void setTradeAccount(String tradeAccount) {
        this.tradeAccount = tradeAccount;
    }
}

