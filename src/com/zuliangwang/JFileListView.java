package com.zuliangwang;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by zuliangwang on 17/4/11.
 */
public class JFileListView extends JPanel{

    public void setDoubleClickLisenter(OnDoubleClickLister mLisener) {
        this.mLisener = mLisener;
    }

    private OnDoubleClickLister mLisener;
    private DefaultListModel listModel;
    private JList list;
//    private FileListModel listModel;

    public JFileListView() {
        super(new BorderLayout());

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel nameLabel = new JLabel("文件名");
        nameLabel.setBounds(new Rectangle(new Point(40,40),nameLabel.getPreferredSize()));
        nameLabel.setSize(40,40);
//        nameLabel.setLocation(0,0);

        JLabel sizeLabel = new JLabel("文件大小");
        sizeLabel.setSize(40,40);
//        sizeLabel.setLocation(sizeLabel.getPreferredSize());


        JLabel isDircetoryLabel = new JLabel("文件类型");
        isDircetoryLabel.setSize(40,40);

        JLabel lastChangedTime = new JLabel("最近修改");
        lastChangedTime.setSize(100,40);
//        label.setHorizontalAlignment();


        list = new JList();
//        list.setLocation(0,50);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        list.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                System.out.println(e.toString());
//            }
//        });
        list.addMouseListener(new MouseAdapter() {
                                  @Override
                                  public void mouseClicked(MouseEvent e) {
                                      JList jList = (JList) e.getSource();
                                      if (e.getClickCount() == 2){
                                          int index = list.locationToIndex(e.getPoint());
                                          System.out.println(index+"");
                                          mLisener.onDoubleClicke(index,list,listModel);
                                      }
                                  }
                              }
        );

//        list.listener

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


    public static interface OnDoubleClickLister{
        public void onDoubleClicke(int index,JList list,DefaultListModel model);
    }
}
