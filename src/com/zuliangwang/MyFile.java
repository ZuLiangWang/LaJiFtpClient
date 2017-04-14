package com.zuliangwang;

/**
 * Created by zuliangwang on 17/4/11.
 */
public class MyFile {

    private String name;
    private long size;
    //type  0 file  1 directory
    private boolean isDirectory;
    private String lastChange;


    public MyFile(String name, long size, boolean isDirectory, String lastChange) {
        this.name = name;
        this.size = size;
        this.isDirectory = isDirectory;
        this.lastChange = lastChange;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }



    public boolean isDirectory() {
        return isDirectory;
    }

    public String getLastChange() {
        return lastChange;
    }

    @Override
    public String toString() {
        String s;
        if (isDirectory) s = "目录";
        else s = "文件";
        return name + "  " + size + "  " + s + "  " + lastChange;
    }
}
