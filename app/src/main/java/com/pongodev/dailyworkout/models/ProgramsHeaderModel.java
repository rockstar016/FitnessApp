package com.pongodev.dailyworkout.models;

/**
 * Created by RockStar0116 on 2016.09.16.
 */
public class ProgramsHeaderModel {
    private String circuitNumber, roundNumber, dayId;

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public String getCircuitNumber() {
        return circuitNumber;
    }

    public void setCircuitNumber(String circuitNumber) {
        this.circuitNumber = circuitNumber;
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }
}
