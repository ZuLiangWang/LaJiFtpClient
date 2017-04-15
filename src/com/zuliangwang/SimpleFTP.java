package com.zuliangwang;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;



public class SimpleFTP {

    public ArrayList<Task> tasks = new ArrayList<>();


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    private String host;
//    private int port;

    private Socket controlSocket = null;
    private Socket dataSocket = null;


    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private BufferedReader dataReader = null;
    private BufferedWriter dataWriter = null;

    private static boolean debug = true;

//    private String user = "zuliangwang";
//    private String pass = "wdde123";

    protected User user = new User();

    //0是passvie 1是port 默认是0
    private int  dataConncetType=0;

    public String getDefaultLocalFileDir() {
        return defaultLocalFileDir;
    }

    public void setDefaultLocalFileDir(String defaultLocalFileDir) {
        this.defaultLocalFileDir = defaultLocalFileDir;
    }

//    private String defaultLocalFileDir = "/Users/zuliangwang";
    private String defaultLocalFileDir = "./";

    public SimpleFTP() {

    }
    /**
     * connect to the ftp server
     * @param host
     * @throws Exception
     * 21号端口传送FTP控制命令 20号端口传送文件数据 使用两个TCP连接
     */
    public synchronized void connect(String host) throws Exception {
        connect(host, 21, user.getUserName(), user.getPassWord());
    }

    public synchronized void connect(String host, int port, String user, String pass) throws Exception {
        if(controlSocket != null) {
            throw new Exception("already connect!");
        }
        controlSocket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(controlSocket.getOutputStream()));
        String response = readLine(reader);
        if(!response.startsWith("220")) {
            throw new Exception("unknow response after connect!");
        }
        sendLine("USER " + user,writer);
        response = readLine(reader);
        if(!response.startsWith("331")) {
            throw new Exception("unknow response after send user");
        }
        sendLine("PASS " + pass,writer);
        response = readLine(reader);
        if(!response.startsWith("230")) {
            throw new Exception("unknow response after send pass");
        }
        System.out.println("login!");
    }

    private void sendLine(String line,Writer writer) throws Exception {
        if(controlSocket == null) {
            throw new Exception("not connect!");
        }
        writer.write(line + "\r\n");
        writer.flush();
        if(debug) {
            System.out.println(">" + line);
        }
    }

    private String readLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if(debug) {
            System.out.println("<" + line);
        }
        return line;
    }

    private String readAll(BufferedReader reader) {
        StringBuilder builder = new StringBuilder();
        String s  =null;
        try {
            while ( (s= readLine(reader)) !=null){
//                System.out.println("before append");
                builder.append(s);
//                System.out.println("append");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return builder.toString();
        }
    }

    /**
     * get the working directory of the FTP server
     * @return
     * @throws Exception
     */
    public synchronized String pwd() throws Exception {
        sendLine("PWD",writer);
        String dir = null;
        String response = readLine(reader);
        if(response.startsWith("257")) {
            int firstQuote = response.indexOf("/");
            int secondQuote = response.indexOf("/", firstQuote + 1);
            if(secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

//    /**
//     * send a file to ftp server
//     * @param file
//     * @return
//     * @throws Exception
//     */
//    public synchronized boolean stor(File file) throws Exception {
//        if(!file.isDirectory()) {
//            throw new Exception("cannot upload a directory!");
//        }
//        String fileName = file.getName();
//        return upload(new FileInputStream(file), fileName);
//    }

//    public synchronized boolean upload(InputStream inputStream, String fileName) throws Exception {
//        BufferedInputStream input = new BufferedInputStream(inputStream);
//        sendLine("PASV",writer);
//        String response = readLine(reader);
//        if(!response.startsWith("227")) {
//            throw new Exception("not request passive mode!");
//        }
//        String ip = null;
//        int port = -1;
//        int opening = response.indexOf('(');
//        int closing = response.indexOf(')', opening + 1);
//        if(closing > 0) {
//            String dataLink = response.substring(opening + 1, closing);
//            StringTokenizer tokenzier = new StringTokenizer(dataLink, ",");
//            try {
//                ip = tokenzier.nextToken() + "." + tokenzier.nextToken() + "."
//                        +  tokenzier.nextToken() + "." + tokenzier.nextToken();
//                port = Integer.parseInt(tokenzier.nextToken()) * 256 +Integer.parseInt(tokenzier.nextToken());;
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                throw new Exception("bad data link after upload!");
//            }
//        }
//        sendLine("STOR " + fileName,writer);
//        Socket dataSocket = new Socket(ip, port);
//        response = readLine(reader);
//        if(!response.startsWith("150")) {
//            throw new Exception("not allowed to send the file!");
//        }
//        BufferedOutputStream output = new BufferedOutputStream(dataSocket.getOutputStream());
//        byte[] buffer = new byte[4096];
//        int bytesRead = 0;
//        while((bytesRead = input.read(buffer)) != -1) {
//            output.write(buffer, 0, bytesRead);
//        }
//        output.flush();
//        output.close();
//        input.close();
//        response = readLine(reader);
//        return response.startsWith("226");
//    }

    public synchronized void get(String remoteFilePath, String localFilePath, TaskLisenter lisenter) throws Exception {
        dataConnect();
        System.out.println("remoteFilePath = " + remoteFilePath + " localFilePath " + localFilePath);
        sendLine("RETR "+remoteFilePath,writer);

        BufferedInputStream bufferedInputStream = new BufferedInputStream(dataSocket.getInputStream());

        if (lisenter!=null)
        lisenter.before(localFilePath,0);
        startDownloadThread(bufferedInputStream,localFilePath,0);
        if (lisenter!=null)
        lisenter.after(localFilePath);
        readLine(reader);
        readLine(reader);
    }


    volatile int loadSize=0;
    private void startRefreshThread(String localFilePath, long totalSize, TaskLisenter lisenter, int type){
        if (type == TYPE_GET){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File localFile = new File(localFilePath);
                    long downloadSize = 0;
                    float fraction = 0 ;
                    while (downloadSize<totalSize){
                        downloadSize = localFile.length();
                        fraction = downloadSize/totalSize;
                        lisenter.onProcessChanged(localFilePath,fraction);
                    }
                }
            }).start();
        }
        else if (type == TYPE_PUT){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadSize = 0;
                    float fraction = 0;
                    while (loadSize < totalSize){
                        fraction = loadSize/totalSize;
                        lisenter.onProcessChanged(localFilePath,totalSize);
                    }
                }
            }).start();
        }
    }

    public synchronized void breakPointGet(String remoteFilePath,String localFilePath,int offset) throws Exception {
        dataConnect();

        sendLine("REST "+offset,writer);
//        startDownloadThread();
    }

    public synchronized long pause(String filePath,int type) throws Exception {
        if (type == TYPE_GET)
            return pauseGet(filePath);
        else if (type == TYPE_PUT)
            return pausePut(filePath);

        throw new Exception("Pause Filed Exception");
    }

    public synchronized long pauseGet(String localFilePath) throws Exception {

//        sendLine("SIZE "+"/ftpDir/zz.dmg",writer);
//        readLine(reader);
        destroyDataConnection();
        long size = getFileSize(localFilePath,TYPE_GET);
        return size;
    }

    public synchronized long pausePut(String remoteFilePath) throws Exception {
        destroyDataConnection();
        long size = getFileSize(remoteFilePath,TYPE_PUT);
        return size;
    }


    public static int TYPE_GET = 1;
    public static int TYPE_PUT = 2;

    private void destroyDataConnection() throws IOException {
        dataWriter.close();
        dataReader.close();
        dataSocket.close();

    }

    private long getFileSize(String desFilePath,int type) throws Exception {
        long result = 0;
        if (type==TYPE_GET){
            File file = new File(desFilePath);
            result = file.length();

        }
        else if (type==TYPE_PUT){
            sendLine("SIZE "+desFilePath,writer);
            String response = readLine(reader);
            Scanner scanner = new Scanner(response);
            scanner.next();
            result = scanner.nextLong();
        }
        return result;
    }


    public synchronized void load(String remoteFilePath,String localFilePath) throws Exception {
        dataConnect();

        System.out.println("remoteFilePath = " + remoteFilePath + " localFilePath " + localFilePath);
        sendLine("STOR "+remoteFilePath,writer);


        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(dataSocket.getOutputStream());

        startLoadThread(bufferedOutputStream,localFilePath,0);
        readLine(reader);

    }

    private void startDownloadThread(BufferedInputStream inputStream,String localFilePath,int offset){
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[1024];
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(localFilePath));
                    int length=-1;
                    System.out.println("begin");
                    while ( (length = inputStream.read(buf)) != -1 ){
                        outputStream.write(buf,offset,length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("finish");
            }
        }).start();
    }

    private void startLoadThread(BufferedOutputStream outputStream,String localFilePath,int offset){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                        FileInputStream fileInputStream = new FileInputStream(new File(localFilePath));
                        int length=-1;
                        System.out.println("begin");
                        byte[] buf = new byte[1024];
                        while ( (length = fileInputStream.read(buf,0,1024) ) != -1 ){
                            outputStream.write(buf,offset,length);
                            loadSize+=length;
                        }
                    outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                System.out.println("finish");
            }
        }).start();
    }

    public synchronized void serverHelp() throws Exception {
        sendLine("help",writer);
        readAll(reader);
    }

    public synchronized void ls() throws Exception{
//        passiveDataConnect();
        dataConnect();
        sendLine("NLST"+" ./",writer);
        readLine(reader);
        readAll(dataReader);
        readLine(reader);
    }

    private void passiveDataConnect() throws Exception {
//        if (dataSocket==null || !dataSocket.isConnected() || dataSocket.){
            sendLine("PASV",writer);
            String response = readLine(reader);
            int port = -1;
            String ip;

            int opening = response.indexOf('(');
            int closing = response.indexOf(')', opening + 1);
            String dataLink = response.substring(opening + 1, closing);
            System.out.println(dataLink);
            StringTokenizer tokenzier = new StringTokenizer(dataLink, ",");
            ip = tokenzier.nextToken() + "." + tokenzier.nextToken() + "."
                    +  tokenzier.nextToken() + "." + tokenzier.nextToken();
            port = Integer.parseInt(tokenzier.nextToken()) * 256 +Integer.parseInt(tokenzier.nextToken());;
            System.out.println("remote port = "+ port);
            dataSocket = new Socket(ip,port);
            dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));

    }


    private void dataConnect() throws Exception {
        if (dataConncetType ==0)
            passiveDataConnect();
        else
            portDataConnect();
    }

    //注意发过去的格式
    private void portDataConnect() throws Exception {
        String ip = "127,0,0,1";
        int port = 9981;
        String sPort = "38,253";

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataSocket = new ServerSocket(port).accept();
                    System.out.println("ac");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        sendLine("PORT "+ip+","+sPort,writer);
//        dataSocket = new ServerSocket(port).accept();
        System.out.println("x");
//        readLine(reader);
        readLine(reader);
//        readLine(reader);
        dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
        dataWriter = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
    }

    public void user() throws Exception {
        sendLine("USER"+" "+user,writer);
        readAll(reader);
    }

    public void quit() throws Exception {
        sendLine("QUIT",writer);
        readAll(reader);
    }


    public void cwd(String path) throws Exception{
        sendLine("CWD "+path,writer);
        readLine(reader);
    }

    public void passive(){
        this.dataConncetType = 0;
        System.out.println("Passive Mode On");
    }

    public void port(){
        this.dataConncetType = 1;
        System.out.println("Port Mode On");
    }

    public static enum Instruction{
        get,put,pause,ftp,
        bye,
        user,pwd,ls,cd,
        help,serverHelp,
        passive,port;
    }

    public interface TaskLisenter{

        public void before(String localFile,int offset);

        public void onProcessChanged(String localFile,float processed);

        public void after(String localFile);

    }

}