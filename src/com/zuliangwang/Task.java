package com.zuliangwang;

/**
 * Created by zuliangwang on 17/4/11.
 */
public class Task {

    private String localFilePath;
    private String remoteFilePath;
    private String type;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    private String size;
    private State state = State.proceesing;

    public static enum State{
        proceesing,failure,finish;
    }

    public Task(String localFilePath, String remoteFilePath) {
        this.localFilePath = localFilePath;
        this.remoteFilePath = remoteFilePath;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }
//    private int id;


    public static class refreshThread extends Thread{
        @Override
        public void run() {

        }
    }

    @Override
    public String toString() {
//        return ;
        return null;
    }
}
