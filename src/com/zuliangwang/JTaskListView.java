package com.zuliangwang;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by zuliangwang on 17/4/12.
 */
public class JTaskListView extends JPanel {

    private DefaultListModel listModel;
    private JList list;
//    private FileListModel listModel;

    public JTaskListView() {
        super(new BorderLayout());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel nameLabel = new JLabel("文件名");
        nameLabel.setBounds(new Rectangle(new Point(40,40),nameLabel.getPreferredSize()));
        nameLabel.setSize(40,40);
//        nameLabel.setLocation(0,0);

        JLabel sizeLabel = new JLabel("操作类型");
        sizeLabel.setSize(40,40);
//        sizeLabel.setLocation(sizeLabel.getPreferredSize());


        JLabel isDircetoryLabel = new JLabel("总大小");
        isDircetoryLabel.setSize(40,40);

        JLabel lastChangedTime = new JLabel("完成进度");
        lastChangedTime.setSize(40,40);


        list = new JList();
//        list.setLocation(0,50);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        labelPanel.add(nameLabel);
        labelPanel.add(sizeLabel);
        labelPanel.add(isDircetoryLabel);
        labelPanel.add(lastChangedTime);

        JScrollPane scrollPane = new JScrollPane(list);
        add(labelPanel,BorderLayout.NORTH);
//        add(sizeLabel,BorderLayout.NORTH);
        add(scrollPane,BorderLayout.CENTER);
    }



    public void updateData(ArrayList<String> data){
        list.setListData(data.toArray());
        list.updateUI();
    }
}
