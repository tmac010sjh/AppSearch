package com.tmac.appsearch;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

/**
 * Created by T_MAC on 2016/5/15.
 */
class AppInfo implements Serializable {

    private static final long serialVersionUID = 201605221520L;

    private String appName;
    private Drawable appIcon;
    private String pkgName;
    private String pinyinIndex;

    String getPinyinIndex() {
        return pinyinIndex;
    }

    void setPinyinIndex(String pinyinIndex) {
        this.pinyinIndex = pinyinIndex;
    }

    String getAppName() {
        return appName;
    }

    void setAppName(String appName) {
        this.appName = appName;
    }

    Drawable getAppIcon() {
        return appIcon;
    }

    void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
