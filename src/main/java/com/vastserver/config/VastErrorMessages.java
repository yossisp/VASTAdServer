package com.vastserver.config;

import static com.vastserver.config.SQLConstants.advName;
import static com.vastserver.config.SQLConstants.pubName;

public class VastErrorMessages {
    public static final String pubAlreadyExists = VastConfig.entityAlreadyExists(pubName);
    public static final String advAlreadyExists = VastConfig.entityAlreadyExists(advName);
    public static final String noOtherErrors = "No other errors reported.";
    public static final String invalidTagId = "Invalid tag id/publisher id.";
    public static final String xmlProcessingError = "XML could not be parsed.";
    public static final String entityNotExists = "The entity doesn\'t exist.";
    public static final String entityIdMissing = "The entity id is missing.";
    public static final String entitySuccessfullyDeleted = "The entity was succcessfully deleted.";
    public static final String entityNotExistsOrIdMissing =
            String.format("%s or %s", entityNotExists, entityIdMissing);
    public static final String entityNameMissing = "The entity name was not specified. " +
            "Please specify the entity name.";
    public static final String entityUpdateUnsuccessfull =
            "Entity update was unsuccessfull.";
    public static final String noAdsAvailable = "No ads available.";
    public static final String unknownJmsMessage = "Unknown JMS message.";

}
