package com.vastserver.classes.sqlStatements;

import com.vastserver.classes.JSONObjects.VastAdvertiser;
import com.vastserver.classes.JSONObjects.VastPublisher;
import com.vastserver.classes.VastLogger;
import com.vastserver.config.SQLConstants;
import org.apache.logging.log4j.Logger;

import static com.vastserver.config.VastConfig.statsAggregationIntervalInHours;

public class SQLQueryBuilder implements SQLStatement {
    private Logger log = VastLogger.getLogger(SQLQueryBuilder.class);
    private String select;
    private String from;
    private String where = null;
    private String groupBy = null;

    public SQLQueryBuilder(String select, String from, String where) {
        this.select = select;
        this.from = from;
        this.where = where;
    }

    public SQLQueryBuilder(String select, String from,
                           String where, String groupBy) {
        this(select, from, groupBy);
        this.groupBy = groupBy;
    }

    public String getSql() {
        String query = "SELECT " + select +
                " FROM " + from;

        if (this.where != null) {
            query += " WHERE " + where;
        }
        if (groupBy != null) {
             query += " GROUP BY " + groupBy;
        }

        return query;
    }

    public static String getWhereClauseForAdvMeetingPubConditions(VastPublisher p) {
        String whereClause = "";
        whereClause += String.format("%s = \'%s\'",
                SQLConstants.category, p.getCategory());
        whereClause += " AND ";
        whereClause += String.format("%s > %f",
                SQLConstants.price, p.getCost());
        whereClause += " AND ";
        whereClause += String.format("%s = %b",
                SQLConstants.active, true);
        return whereClause;
    }

    public static String getClauseForAdvBudget(VastAdvertiser adv) {
        String clause = "";
        clause += String.format("%s = \'%s\'",
                SQLConstants.advId, adv.getId());
        clause += " AND ";
        clause += String.format("%s = \'%s\'",
                SQLConstants.event, SQLConstants.impressionEvent);
        clause += String.format("%s = \'%s\'",
                SQLConstants.groupBy, SQLConstants.advId);
        return clause;
    }

    public static String getQueryForAdvertisersOutOfBudget() {
        return String.format("with\n" +
                "  impsCount as (\n" +
                "  select %s, count(*) as impressionsCount\n" +
                "  from %s\n" +
                "  where %s = \'%s\'\n" +
                "  group by %s\n" +
                "  )\n" +
                "  select %s from impsCount,  %s\n" +
                "  where %s = %s\n" +
                "    and impressionsCount > %s and %s = \'%s\';",
                SQLConstants.advId, SQLConstants.stats,
                SQLConstants.event, SQLConstants.impressionEvent,
                SQLConstants.advId, SQLConstants.id,
                SQLConstants.advertisers, SQLConstants.advId,
                SQLConstants.id, SQLConstants.budget,
                SQLConstants.active, "true"
                );
    }

    public static String getStatsAggregationQuery() {
        return String.format("with pubImps as(\n" +
                        "  SELECT extract(hour from ts) as hour,\n" +
                        "    pubid, advid,\n" +
                        "    count(*) filter (where event = 'Impression') AS impressions\n" +
                        "  FROM stats\n" +
                        "  WHERE ts >= (NOW() - INTERVAL '%d hours' )\n" +
                        "  GROUP BY hour, pubid, advid\n" +
                        "),\n" +
                        "revenue as(\n" +
                        "  select hour, pubname, advname, impressions,\n" +
                        "  (impressions * publishers.cpm / 1000) as spend,\n" +
                        "  (impressions * advertisers.cpm / 1000) as revenue\n" +
                        "from publishers, pubImps, advertisers\n" +
                        "where pubid = publishers.id and advid = advertisers.id\n" +
                        ")\n" +
                        "select hour, pubname, advname, impressions,\n" +
                        "  spend, revenue,\n" +
                        "  round(cast((revenue - spend) as numeric), 2) as profit\n" +
                        "from revenue",
                statsAggregationIntervalInHours
                );
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
 }
