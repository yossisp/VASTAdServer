package com.vastserver.classes.JSONObjects;

public class StatsRow {
    private int hour;
    private String pubName;
    private String advName;
    private int impressions;
    private float spend;
    private float revenue;
    private float profit;

    public StatsRow(int hour, String pubName, String advName,
                    int impressions, float spend, float revenue,
                    float profit) {
        this.hour = hour;
        this.pubName = pubName;
        this.advName = advName;
        this.impressions = impressions;
        this.spend = spend;
        this.revenue = revenue;
        this.profit = profit;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getPubName() {
        return pubName;
    }

    public void setPubName(String pubName) {
        this.pubName = pubName;
    }

    public String getAdvName() {
        return advName;
    }

    public void setAdvName(String advName) {
        this.advName = advName;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public float getSpend() {
        return spend;
    }

    public void setSpend(float spend) {
        this.spend = spend;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public float getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    @Override
    public String toString() {
        return "StatsRow{" +
                "hour=" + hour +
                ", pubName='" + pubName + '\'' +
                ", advName='" + advName + '\'' +
                ", impressions=" + impressions +
                ", spend=" + spend +
                ", revenue=" + revenue +
                ", profit=" + profit +
                '}';
    }
}
