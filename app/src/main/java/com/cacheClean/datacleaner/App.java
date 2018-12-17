package com.cacheClean.datacleaner;

import android.graphics.drawable.Drawable;

class App {

    public String name;
    public Drawable icon;
    public String packageName;
    public boolean enable;
    public String apkSize;
    public String versionName;
    public long cacheSize;
    public long dataSize;



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public boolean getEnable() {
        return enable;
    }
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    public String getApkSize() {
        return apkSize;
    }
    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }
    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public long getCacheSize() {
        return cacheSize;
    }
    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
    public long getDataSize() {
        return dataSize;
    }
    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }
}
