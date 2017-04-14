package com.zuliangwang;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zuliangwang on 17/4/11.
 */
public class FileHelper {
    public File curDir;

    public FileHelper(File curDir) {
        this.curDir = curDir;
    }

    public ArrayList<String> pwd(String fileName){
        ArrayList<String> resultList = new ArrayList<>();

        ArrayList<MyFile> list = new ArrayList<>();
        File curDirectory = new File(curDir.getAbsolutePath()+"/"+fileName);
        System.out.println(curDirectory.getAbsolutePath());

        File[] files = curDirectory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File cur = files[i];
//            System.out.println();
            Date date = new Date(cur.lastModified());
            MyFile myFile = new MyFile(cur.getName(),cur.length(),cur.isDirectory(),dateToString(date));
            list.add(myFile);
            resultList.add(myFile.toString());
        }

        for (int i=0;i<list.size();i++){
            MyFile file = list.get(i);
            System.out.println(file.getName()+"  "+file.getSize()+"  "+file.getLastChange());
        }

        curDir = new File(curDir.getAbsolutePath()+"/"+fileName);
        return resultList;
    }

//    public File curDir(){
//
//    }

    public static String dateToString(Date time){
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);
        return ctime;
    }

    public static void main(String args[]){
//        FileHelper helper =new FileHelper();
//        helper.pwd("/Users/zuliangwang");
    }
}
