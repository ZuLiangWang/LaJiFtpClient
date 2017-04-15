package com.zuliangwang;

import java.io.File;

/**
 * Created by zuliangwang on 17/4/15.
 */
public class Test {
    public static void main(String args[]){
        File file = new File("ftp.png");
        File file1 = new File("ftp.png");
        System.out.println(file.hashCode());
        System.out.println(file1.hashCode());
    }
}
