package com.vastserver.classes;

import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;

public class VastUrlToXmlConverter {
    private Logger log = VastLogger.getLogger(VastUrlToXmlConverter.class);

    private String vastUrl;

    public VastUrlToXmlConverter(String vastUrl) {
        this.vastUrl = vastUrl;
    }

    /*
    makes a GET request for the vastUrl which was configured in the advertiser
     */
    public String getXmlResponse() {
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(this.vastUrl, String.class);

        log.info(result);
        return result;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getVastUrl() {
        return vastUrl;
    }

    public void setVastUrl(String vastUrl) {
        this.vastUrl = vastUrl;
    }
}
