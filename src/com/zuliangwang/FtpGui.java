package com.zuliangwang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zuliangwang on 17/4/11.
 */
public class FtpGui {



    private SimpleFTP simpleFTP = new SimpleFTP();
    private java.util.List<Task> taskList = new ArrayList<>();
    private Queue<Task> taskQueue = new ArrayBlockingQueue<Task>(100);
    FileHelper helper = new FileHelper(new File(simpleFTP.getDefaultLocalFileDir()));;
    ArrayList<String> curDir;
    JTextField localFileInput;
    JTaskListView taskListView;


    private void createAndShowGui(){
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("FtpClient");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(200,200);
        frame.setMinimumSize(new Dimension(1000,700));
//        frame.setLayout(new GridLayout(2,1));


        JPanel jPanel = new JPanel();
        placeComponents(jPanel);

        JFileListView localFileList = new JFileListView();
        taskListView = new JTaskListView();

        frame.getContentPane().setLayout(new GridLayout(2,1));
        frame.getContentPane().add(jPanel);

        JPanel bottomPanel = new JPanel(new GridLayout(1,2));
        bottomPanel.add(localFileList);
        bottomPanel.add(taskListView);
        localFileList.setDoubleClickLisenter(new JFileListView.OnDoubleClickLister() {
            @Override
            public void onDoubleClicke(int index, JList list, DefaultListModel model) {
//                DefaultListModel defaultListModel = (DefaultListModel) list.getModel();
//                System.out.println(index+"");
                String s = curDir.get(index);
                System.out.println(s);
//                System.out.println(new File("./test.txt").getName());
                Scanner scanner = new Scanner(s);
                String name = scanner.next();
                scanner.next();
                String isDir =  scanner.next();
                if (isDir.equals("目录"))
                curDir = helper.pwd(name);
                else if(isDir.equals("文件")){
                    System.out.println("name");
                    localFileInput.setText(name);
                }

                localFileList.updateData(curDir);
            }
        });


        frame.getContentPane().add(bottomPanel);

        curDir = helper.pwd("");
        localFileList.updateData(curDir);

        frame.pack();
        frame.setVisible(true);
    }

    private void placeComponents(JPanel jPanel){
        JLabel ipLabel  = new JLabel("主机:");
        ipLabel.setBounds(20,20,40,40);
        JTextField ipInput = new JTextField();
        ipInput.setBounds(70,20,100,40);

        JLabel portLable = new JLabel("端口:");
        portLable.setBounds(200,20,40,40);
        JTextField portInput = new JTextField();
        portInput.setBounds(250,20,100,40);

        JLabel userLable  = new JLabel("用户名:");
        userLable.setBounds(380,20,50,40);
        JTextField userInput = new JTextField();
        userInput.setBounds(430,20,100,40);

        JLabel passwordLable = new JLabel("密码:");
        passwordLable.setBounds(560,20,40,40);
        JTextField passwordInput = new JTextField();
        passwordInput.setBounds(610,20,100,40);


        JButton connectButton = new JButton("连接");
        connectButton.setBounds(800,20,100,40);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Click");
                String ip = ipInput.getText();
                int port = Integer.parseInt(portInput.getText());
                try {
                    simpleFTP.connect(ip,port,"zuliangwang","wdde123");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton anonymousButton = new JButton("匿名登录");
        anonymousButton.setBounds(920,20,100,40);
        anonymousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Click");
                String ip = ipInput.getText();
                int port = Integer.parseInt(portInput.getText());
                try {
                    //Anonymous
                    simpleFTP.connect(ip,port,"Anonymous",null);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JLabel remoteFileLable = new JLabel("远程文件");
        remoteFileLable.setBounds(20,120,100,40);
        JTextField remoteFileInput = new JTextField();
        remoteFileInput.setBounds(140,120,100,40);

        JLabel localFileLable = new JLabel("本地文件");
        localFileLable.setBounds(20,170,100,40);
        localFileInput = new JTextField();
        localFileInput.setBounds(140,170,100,40);

        JButton downloadButton = new JButton("下载");
        downloadButton.setBounds(250,120,100,40);
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    simpleFTP.get(remoteFileInput.getText(),helper.curDir+"/"+localFileInput.getText(),null);
                    taskList.add(new Task(localFileInput.getText(),remoteFileInput.getText()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        JButton uploadButton = new JButton("上传");
        uploadButton.setBounds(250,170,100,40);
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    simpleFTP.load(remoteFileInput.getText(),helper.curDir+"/"+localFileInput.getText());
                    taskList.add(new Task(localFileInput.getText(),remoteFileInput.getText()));
                    taskQueue.add(new Task(localFileInput.getText(),remoteFileInput.getText()));
//                    taskListView.updateData();
//                    ArrayList<String> stringArrayList = new ArrayList<String>();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton changeTypeButton = new JButton("切换连接模式");
        changeTypeButton.setBounds(400,170,100,40);
        JLabel typeLable = new JLabel("PASV");
        typeLable.setBounds(450,120,40,40);
        changeTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (typeLable.getText().equals("PASV")){
                    typeLable.setText("PORT");
                    simpleFTP.port();
                }
                else {
                    typeLable.setText("PASV");
                    simpleFTP.passive();
                }

            }
        });




        jPanel.setLayout(null);


        jPanel.add(ipLabel);
        jPanel.add(ipInput);
        jPanel.add(portLable);
        jPanel.add(portInput);
        jPanel.add(userLable);
        jPanel.add(userInput);
        jPanel.add(passwordLable);
        jPanel.add(passwordInput);
        jPanel.add(connectButton);
        jPanel.add(anonymousButton);

        jPanel.add(remoteFileInput);
        jPanel.add(remoteFileLable);
        jPanel.add(localFileInput);
        jPanel.add(localFileLable);
        jPanel.add(downloadButton);
        jPanel.add(uploadButton);

        jPanel.add(changeTypeButton);
        jPanel.add(typeLable);


    }

    public static void main(String args[]){
        FtpGui gui = new FtpGui();



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.createAndShowGui();
            }
        });
    }
}
