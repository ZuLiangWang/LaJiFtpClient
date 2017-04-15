package com.zuliangwang;

import java.util.Scanner;

/**
 * Created by zuliangwang on 17/4/9.
 */
public class Main {

    private String myFtpServerIp = "104.194.89.182";

    public static void main(String[] args) throws Exception {


        SimpleFTP  ftp = new SimpleFTP();

        System.out.println("Welcome to Ftp Client");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please Input the Ftp Server Host Address");
        ftp.setHost("104.194.89.182");
//        ftp.setHost(scanner.next());
        System.out.println("Please Input your UserName");
        ftp.user.setUserName(scanner.nextLine());
//        ftp.user.setUserName("zuliangwang");
        System.out.println("Please Input your PassWord");
        ftp.user.setPassWord(scanner.nextLine());
//        ftp.user.setPassWord("wdde123");
        ftp.connect(ftp.getHost(),21,ftp.user.getUserName(),ftp.user.getPassWord());

        while (true){
            try {
                SimpleFTP.Instruction instruction = SimpleFTP.Instruction.valueOf(scanner.next());
                switch (instruction){
                    case get:
                        System.out.println("(remote-file) ");
                        String getRemoteFilePath = scanner.next();
                        System.out.println("(local-fie) ");
                        String getLocalFilePath = scanner.next();
                        ftp.get(getRemoteFilePath,getLocalFilePath,null);
                        break;
                    case put:
                        System.out.println("(remote-file) ");
                        String putRemoteFilePath = scanner.next();
                        System.out.println("(local-fie) ");
                        String putLocalFilePath = scanner.next();
                        ftp.load(putRemoteFilePath,putLocalFilePath);
                        break;
                    case bye:
                        ftp.quit();
                        return;
                    case cd:
                        String path = scanner.next();
                        ftp.cwd(path);
                        break;
                    case pwd:
                        ftp.pwd();
                        break;
                    case user:
                        ftp.user();
                        break;
                    case serverHelp:
                        ftp.serverHelp();
                        break;
                    case ls:
                        ftp.ls();
                        break;
                    case ftp:
                        break;
                    case pause:
//                        ftp.pause("",1);
                        break;
                    case port:
                        ftp.port();
                        break;
                    case passive:
                        ftp.passive();
                        break;
                    default:
                        System.out.println("Invalid Commend");
                        break;
                }
            }
            catch (IllegalArgumentException e) {
                System.out.println("Invalid Commend");
            }
        }

    }
}
