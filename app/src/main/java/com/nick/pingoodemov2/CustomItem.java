package com.nick.pingoodemov2;

/**
 * Created by nick on 17/3/16.
 */
public class CustomItem {
    private String content;

    private int info;

    public CustomItem (String content, int info){
        this.content = content;
        this.info = info;
    }

    @Override
    public String toString() {
        return this.content;
    }

    public int getInfo() {
        return info;
    }

    public void setInfo(int info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
