package com.tmac.appsearch;

import android.graphics.drawable.Drawable;

/**
 * Created by T_MAC on 2016/5/15.
 */
public class AppInfo {
    private String appName;
    private Drawable appIcon;
    private String pkgName;
    private String pinyinIndex;

    public String getPinyinIndex() {
        return pinyinIndex;
    }

    public void setPinyinIndex(String pinyinIndex) {
        this.pinyinIndex = pinyinIndex;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
