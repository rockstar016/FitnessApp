package com.pongodev.dailyworkout.models;

/**
 * Created by RockStar0116 on 2016.09.16.
 */
public class ProgramsModel {
    private String workOutName,workOutImage,workOutTimes;
    private int progId, workId;

    public int getProgId() {
        return progId;
    }

    public void setProgId(int progId) {
        this.progId = progId;
    }

    public int getWorkId() {
        return workId;
    }

    public void setWorkId(int workId) {
        this.workId = workId;
    }

    public String getWorkOutName() {
        return workOutName;
    }

    public void setWorkOutName(String workOutName) {
        this.workOutName = workOutName;
    }

    public String getWorkOutImage() {
        return workOutImage;
    }

    public void setWorkOutImage(String workOutImage) {
        this.workOutImage = workOutImage;
    }

    public String getWorkOutTimes() {
        return workOutTimes;
    }

    public void setWorkOutTimes(String workOutTimes) {
        this.workOutTimes = workOutTimes;
    }
}
