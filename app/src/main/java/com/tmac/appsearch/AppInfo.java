package com.tmac.appsearch;

import io.realm.RealmObject;

/**
 * Created by T_MAC on 2016/5/15.
 */
public class AppInfo extends RealmObject {

//    private static final long serialVersionUID = 201605221520L;

    private byte[] iconByte;

    public byte[] getIconByte() {
        return iconByte;
    }

    public void setIconByte(byte[] iconByte) {
        this.iconByte = iconByte;
    }

    private String appName;
    //    private Drawable appIcon;
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

//    Drawable getAppIcon() {
//        return appIcon;
//    }

//    void setAppIcon(Drawable appIcon) {
//        this.appIcon = appIcon;
//    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }
}
