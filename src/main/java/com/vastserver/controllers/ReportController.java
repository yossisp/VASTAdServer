package com.vastserver.controllers;

import com.vastserver.classes.*;
import com.vastserver.classes.JSONObjects.StatsRow;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vastserver.config.VastConfig;

import java.util.List;

import static com.vastserver.config.Routes.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ReportController {
    private Logger log = VastLogger.getLogger(ReportController.class);

    @Autowired
    private DBHandler db;

    @CrossOrigin(origins = VastConfig.allowedSingleDomain)
    @RequestMapping(method=GET, path= "/" + dailyReport)
    public ResponseEntity<Object> getDailyReport() {
        log.info("getting report data...");
        List<StatsRow> list = this.db.getTodayAggregatedStats();
        return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
    }
}
