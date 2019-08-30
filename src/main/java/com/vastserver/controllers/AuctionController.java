package com.vastserver.controllers;

import com.vastserver.classes.*;
import com.vastserver.classes.JSONObjects.VastAdvertiser;
import com.vastserver.classes.JSONObjects.VastPublisher;
import com.vastserver.classes.sqlStatements.SQLQueryBuilder;
import com.vastserver.classes.vastErrorHandling.ApiError;
import com.vastserver.classes.vastErrorHandling.VastEntityException;
import com.vastserver.config.VastErrorMessages;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vastserver.config.VastConfig;

import java.util.ArrayList;
import java.util.List;

import static com.vastserver.config.Routes.*;
import static com.vastserver.config.VastErrorMessages.noOtherErrors;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class AuctionController {
    private Logger log = VastLogger.getLogger(AuctionController.class);

    @Autowired
    private DBHandler db;

    private VastAdvertiser auctionWinner;

    @CrossOrigin(origins = VastConfig.allowedAllOrigins)
    @RequestMapping(method=GET, path=auction)
    public ResponseEntity<Object> performAuction(@RequestParam(name = "pubId") String pubId) {
        VastPublisher p;
        String auctionWinnerXml = null;
        String vastXml;
        log.info("starting auction for pubId = " + pubId);

        try {
            p = db.findOneVastPub(pubId);
        } catch (VastEntityException e) {
            log.error(e.getMessage());
            ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,
                    VastErrorMessages.invalidTagId, noOtherErrors);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        boolean isWinnerFound = this.setAuctionWinner(p);

        if (!isWinnerFound) {
            log.info("no winner found for auction");
            ApiError apiError = new ApiError(HttpStatus.OK,
                    VastErrorMessages.noAdsAvailable, noOtherErrors);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        VastUrlToXmlConverter vastUrlToXmlConverter =
                new VastUrlToXmlConverter(this.auctionWinner.getVastUrl());
        vastXml = vastUrlToXmlConverter.getXmlResponse();

        try {
            VASTXMLParserBuilder vastParser = new VASTXMLParserBuilder(vastXml,
                    p.getId(), this.auctionWinner.getId());
            auctionWinnerXml = vastParser.getAuctionWinnerXml();
        } catch (Exception e) {
            log.error(e.getMessage());
            ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY,
                    VastErrorMessages.xmlProcessingError, noOtherErrors);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        return new VastXmlResponse(auctionWinnerXml, new HttpHeaders(), HttpStatus.OK);
    }

    private boolean setAuctionWinner(VastPublisher p) {
        String whereClause = SQLQueryBuilder.getWhereClauseForAdvMeetingPubConditions(p);
        List<String> list = db.findAllVastAdvertisersByCondition(whereClause);
        log.info(list);
        if (!list.isEmpty()) {
            List<VastAdvertiser> advList = this.convertAdvIdsToAdvList(list);
            this.auctionWinner = this.findAdvWithHighestPrice(advList);
            log.info("The auction was won by " + auctionWinner);
            return true;
        }

        return false;
    }

    private List<VastAdvertiser> convertAdvIdsToAdvList(List<String> advIdsList) {
        List<VastAdvertiser> advList = new ArrayList<>();
        advIdsList.forEach(advId -> {
            try {
                VastAdvertiser adv = db.findOneVastAdv(advId);
                log.info(adv);
                advList.add(adv);
            } catch (VastEntityException e) {
                log.error(e.getMessage());
            }
        });
        return advList;
    }

    private VastAdvertiser findAdvWithHighestPrice(List<VastAdvertiser> advList) {
        VastAdvertiser advWithHighestPrice = advList.get(0);
        for (VastAdvertiser adv : advList) {
            if (advWithHighestPrice.getPrice() < adv.getPrice()) {
                advWithHighestPrice = adv;
            }
        }
        return advWithHighestPrice;
    }
}
