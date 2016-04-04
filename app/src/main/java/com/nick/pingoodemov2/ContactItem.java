package com.nick.pingoodemov2;

/**
 * Created by nick on 17/3/16.
 */
public class ContactItem
{
    private String id;

    private String content;
    private int info;

    public ContactItem(String id, String content, int info)
    {
        this.id = id;
        this.content = content;
        this.info = info;
    }

    @Override
    public String toString() {
        return this.getContent() + " # " + this.getInfo() + " # " + this.getId();
    }

    public int getInfo() {
        return info;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setInfo(int info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

}
