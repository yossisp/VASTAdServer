package com.vastserver.classes.JSONObjects;

import java.sql.Timestamp;
import java.util.Date;

public class TrackingEvent {
    private String event;
    private String pubId;
    private String advId;
    private long timestamp;

    // default constructor for Jackson
    public TrackingEvent() {

    }

    public TrackingEvent(String event, String pubId, String advId) {
        this.timestamp = this.getTimestamp();
        this.event = event;
        this.pubId = pubId;
        this.advId = advId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public long getTimestamp() {
        Date d = new Date();
        Timestamp t = new Timestamp(d.getTime());
        return t.getTime();
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TrackingEvent{" +
                "event='" + event + '\'' +
                ", pubId='" + pubId + '\'' +
                ", advId='" + advId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }


}
