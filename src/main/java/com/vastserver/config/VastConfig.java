package com.vastserver.config;

public class VastConfig {
    // --- CONSTANTS ---

    public static final String domain = "localhost";
    public static final String httpScheme = "http";
    public static final String clientPort = "3000";
    public static final String serverPort = "8080";
    public static final String allowedAllOrigins = "*";
    public static final String allowedSingleDomain = httpScheme + "://" + domain + ":" + clientPort;
    public static final String uninitializedPubIdVal = "";
    public static final int noRowsInResultSet = 0;
    public static final String emptyStr = "";
    public static final String vastTrackingEvent = "Tracking";
    public static final String vastImpressionEvent = "Impression";
    public static final String vastErrorEvent = "Error";
    public static final String serverBaseUrl = httpScheme + "://" + domain + ":" + serverPort;
    public static final String[] trackingEventsNames = {
            "start", "firstQuartile", "midpoint", "thirdQuartile",
            "complete", "mute", "unmute", "rewind",
            "pause", "resume", "fullscreen",
            "creativeView", "exitFullscreen", "acceptInvitationLinear",
            "closeLinear", "Impression", "Error",
    };

    public static final String jmsTopicName = "trackingEventsTopic";
    public static final int budgetCheckInterval = 5 * 60 * 1000; // 5 min
    public static final int statsAggregationIntervalInHours = 1;
    public static final int statsAggregationIntervalInMillis =
            statsAggregationIntervalInHours * 60 * 60 * 1000; // 1 hour

    public static final int clearStatsAggregationIntervalInMillis =
            1000 * 60 * 60 * 24; // day

    // --- CONSTANTS ---

    // --- METHODS ---
    public static String entityAlreadyExists(String entity) {
        return String.format("The %s with this name already exists. Please use another name.",
                entity);
    }

    // --- METHODS ---

}
