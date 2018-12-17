package com.cacheClean.datacleaner;

public class  Adapter {

    private String name, size, appname;

    public Adapter() {
    }

    public Adapter(String name, String size, String appname) {
        this.name = name;
        this.size = size;
        this.appname = appname;

    }

    public String getName() {
        return name;
    }

    public void setTitle(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setYear(String size) {
        this.size = size;
    }

    public String getAppname() {
        return appname;
    }

    public void setName(String appname) {
        this.appname = appname;
    }

}
