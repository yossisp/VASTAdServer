package com.vastserver.classes;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.vastserver.config.Routes;
import com.vastserver.config.VastConfig;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import static com.vastserver.config.Routes.auction;
import static com.vastserver.config.Routes.tracking;
import static com.vastserver.config.VastConfig.*;

public class VASTXMLParserBuilder {
    private Logger log = VastLogger.getLogger(VASTXMLParserBuilder.class);
    private String auctionWinnerXml;
    private Document parsedVastXml;
    private String xmlToParse;
    private Map<String, Node> trackingEventsMap;
    private String pubId;
    private String advId;

    public VASTXMLParserBuilder(String xmlToParse,
                                String pubId,
                                String advId) throws Exception {
        this.xmlToParse = xmlToParse;
        this.parsedVastXml = this.stringToDocument();
        this.trackingEventsMap = new HashMap<>();
        this.pubId = pubId;
        this.advId = advId;
    }

    public Document stringToDocument() throws Exception {

        DocumentBuilder db;
        Document document = null;

        db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(this.xmlToParse));

        document = db.parse(is);
        return document;
    }

    /*
    @param - vastTrackingEvent - the name of tracking event we want to generate.
    3 types of events are supported: Tracking, Impression and Error.
     */
    private void handleTrackingEvent(String vastTrackingEvent) {
        try {
            NodeList trackingEventsList = parsedVastXml.getElementsByTagName(vastTrackingEvent);
            int trackingEventsCount = trackingEventsList.getLength();
            Node node;
            String eventType;

            for (int i = 0; i < trackingEventsCount; i++) {
                node = trackingEventsList.item(i);

                if (this.hasAttribute(node)) {
                    eventType = node.getAttributes().item(0).getNodeValue();
                } else {
                    eventType = vastTrackingEvent;
                }

                this.trackingEventsMap.put(eventType, node);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private boolean hasAttribute(Node node) {
        return (node.getAttributes() != null) &&
                (node.getAttributes().item(0) != null);
    }

    public String getAuctionWinnerXml() {
        this.handleTrackingEvent(vastImpressionEvent);
        this.handleTrackingEvent(vastErrorEvent);
        this.handleTrackingEvent(vastTrackingEvent);
        this.addVastTracking();
        return this.auctionWinnerXml;
    }

    private String getTrackingEventUrl(String trackingEvent) {
        return String.format("%s%s%s?%s=%s&%s=%s&%s=%s", serverBaseUrl,
                auction, tracking,
                Routes.pubId, this.pubId,
                Routes.event, trackingEvent,
                Routes.advId, this.advId);
    }

    private void addVastTracking() {
        Node node, sibling;
        String trackingEventUrl;
        for (String trackingEvent: VastConfig.trackingEventsNames) {
            node = this.trackingEventsMap.get(trackingEvent);
            sibling = node.cloneNode(true);
            trackingEventUrl = this.getTrackingEventUrl(trackingEvent);
            log.info("trackingEventUrl: " + trackingEventUrl);
            this.setVastNodeValue(trackingEventUrl, sibling);
            this.addVastNodeSibling(sibling, node);
        }
        this.auctionWinnerXml = this.documentToString(this.parsedVastXml);
    }

    private void setVastNodeValue(String content, Node node) {
        if (content != null) {
            Node firstChild = node.getFirstChild();
            firstChild.setNodeValue(content);
        }
    }

    private void addVastNodeSibling(Node newSibling, Node current) {
        current.getParentNode().insertBefore(newSibling, current.getNextSibling());
    }

    private String documentToString(Document doc) {
        String xml = null;
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            xml = writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return xml;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public void setAuctionWinnerXml(String auctionWinnerXml) {
        this.auctionWinnerXml = auctionWinnerXml;
    }

    public Map<String, Node> getTrackingEventsMap() {
        return trackingEventsMap;
    }

    public void setTrackingEventsMap(Map<String, Node> trackingEventsMap) {
        this.trackingEventsMap = trackingEventsMap;
    }

    public String getPubId() {
        return pubId;
    }

    public void setPubId(String pubId) {
        this.pubId = pubId;
    }

    public Document getParsedVastXml() {
        return parsedVastXml;
    }

    public void setParsedVastXml(Document parsedVastXml) {
        this.parsedVastXml = parsedVastXml;
    }

    public String getXmlToParse() {
        return xmlToParse;
    }

    public void setXmlToParse(String xmlToParse) {
        this.xmlToParse = xmlToParse;
    }
}
