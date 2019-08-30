package com.vastserver.controllers;

import com.vastserver.classes.JSONObjects.TrackingEvent;
import com.vastserver.classes.VastLogger;
import com.vastserver.config.Routes;
import com.vastserver.config.VastConfig;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.vastserver.config.Routes.trackingEventPath;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

//events supported: Tracking, Impression and Error.
@RestController
public class TrackingEventController {
    private Logger log = VastLogger.getLogger(TrackingEventController.class);

    @Autowired
    private DBHandler db;

    @Autowired
    private JmsTemplate jmsTemplate;

    @CrossOrigin(origins = VastConfig.allowedAllOrigins)
    @RequestMapping(method=GET, path=trackingEventPath)
    public ResponseEntity<Object> handleTrackingEvent(
            @RequestParam(name = Routes.event) String event,
            @RequestParam(name = Routes.pubId) String pubId,
            @RequestParam(name = Routes.advId) String advId) {
        log.info(String.format("%s=%s, %s=%s, %s=%s",
                Routes.event, event,
                Routes.pubId, pubId,
                Routes.advId, advId));

        TrackingEvent tr = new TrackingEvent(event, pubId, advId);
        this.db.insertTrackingEvent(tr);

        return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.OK);
    }
}
