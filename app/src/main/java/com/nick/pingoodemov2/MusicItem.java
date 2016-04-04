package com.nick.pingoodemov2;

/**
 * Created by Dexter on 04-Apr-16.
 */
public class MusicItem
{
    private String id;
    private String path;
    private String content;
    private int info;

    public MusicItem(String id, String content, String path, int info)
    {
        this.id = id;
        this.path = path;
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
    public String getPath() {
        return path;
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
