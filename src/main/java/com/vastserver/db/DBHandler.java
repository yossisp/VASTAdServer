package com.vastserver.db;

import com.vastserver.classes.JSONObjects.StatsRow;
import com.vastserver.classes.JSONObjects.TrackingEvent;
import com.vastserver.classes.JSONObjects.VastAdvertiser;
import com.vastserver.classes.JSONObjects.VastPublisher;
import com.vastserver.classes.sqlStatements.SQLDeleteBuilder;
import com.vastserver.classes.sqlStatements.SQLQueryBuilder;
import com.vastserver.classes.sqlStatements.SQLUpdateBuilder;
import com.vastserver.classes.vastErrorHandling.VastEntityException;
import com.vastserver.classes.VastLogger;
import com.vastserver.config.Routes;
import com.vastserver.config.SQLConstants;
import com.vastserver.config.VastErrorMessages;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.vastserver.config.SQLConstants.*;
import static com.vastserver.config.SQLConstants.active;
import static com.vastserver.config.VastConfig.noRowsInResultSet;
import static com.vastserver.config.VastErrorMessages.entityUpdateUnsuccessfull;

@Component
public class DBHandler {
    private Logger log = VastLogger.getLogger(DBHandler.class);

    @Autowired
    private JdbcTemplate db;

    public DBHandler() {
    }

    private void dropAllTables() {
        log.info("dropping tables");

        this.db.execute("DROP TABLE IF EXISTS " + publishers);
        this.db.execute("DROP TABLE IF EXISTS " + advertisers);
    }

    private void createAllTables() {
        log.info("creating tables");

        this.db.execute("CREATE TABLE IF NOT EXISTS " + publishers + "(" +
                id + " VARCHAR(255) UNIQUE, " +
                pubName + " VARCHAR(255) UNIQUE, " +
                cost + " FLOAT(8), " +
                category + " VARCHAR(255), " +
                active + " BOOLEAN " +
                ")");
        this.db.execute("CREATE TABLE IF NOT EXISTS " + advertisers + "(" +
                id + " VARCHAR(255) UNIQUE, " +
                advName + " VARCHAR(255) UNIQUE, " +
                price + " FLOAT(8), " +
                category + " VARCHAR(255), " +
                budget + " FLOAT(8), " +
                vastUrl + " VARCHAR (500), " +
                active + " BOOLEAN " +
                ")");
        this.db.execute("CREATE TABLE IF NOT EXISTS " + stats + "(" +
                timestamp + " timestamp default current_timestamp, " +
                event + " VARCHAR(255), " +
                Routes.pubId + " VARCHAR(255), " +
                Routes.advId + " VARCHAR(255) " +
                ")");
        this.db.execute("CREATE TABLE IF NOT EXISTS " + aggregatedStats + "(" +
                hour + " smallint, " +
                pubName + " VARCHAR(255), " +
                advName + " VARCHAR(255), " +
                impressions + " integer, " +
                spend + " float, " +
                revenue + " float, " +
                profit + " float " +
                ")");
        log.info("created tables");
    }

    public void initDBTables() {
        this.createAllTables();
    }

    public void insertVastPublisher(VastPublisher pub) {
        List<Object[]> insertPubData = new ArrayList<>();
        insertPubData.add(new Object[] {pub.getId(),
                pub.getPubName(),
                pub.getCost(),
                pub.getCategory(),
                pub.isActive()
        });
        this.db.batchUpdate(String.format("%s %s(%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)",
                "INSERT INTO ", publishers, id, pubName, cost, category, active), insertPubData);
    }

    public void insertVastAdvertiser(VastAdvertiser adv) {
        List<Object[]> insertAdvData = new ArrayList<>();
        insertAdvData.add(new Object[] {adv.getId(), adv.getAdvName(), adv.getPrice(),
                adv.getCategory(), adv.getBudget(), adv.getVastUrl().toString(),
                adv.isActive()
        });
        this.db.batchUpdate(String.format("%s %s(%s, %s, %s, %s, %s, %s, %s) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "INSERT INTO ", advertisers, id, advName, price, category, budget,
                vastUrl, active),
                insertAdvData);
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.db;
    }

    public boolean isEntityExists(String entityName,
                                  String lookupTable,
                                  String tableCol) {
        log.info("entityName: " + entityName);

        // jdbcTemplate is thread safe but using AtomicInteger is
        // more readable than using an array
        AtomicInteger entityFound = new AtomicInteger(noRowsInResultSet);

        SQLQueryBuilder isEntityExists = new SQLQueryBuilder(all, lookupTable,
                String.format("%s = \'%s\'", tableCol, entityName));

        db.query(isEntityExists.getSql(), (rs, rowName) -> {
            return entityFound.incrementAndGet();
        });
        log.info("isEntityExists rowCount = "+ entityFound.get());
        return entityFound.get() > noRowsInResultSet;
    }

    public VastAdvertiser findOneVastAdv(String id) throws VastEntityException {
        if (!isEntityExists(id, advertisers, SQLConstants.id)) {
            throw new VastEntityException(VastErrorMessages.entityNotExists);
        }

        List<VastAdvertiser> list = new ArrayList<>();
        SQLQueryBuilder query = new SQLQueryBuilder(all, advertisers,
                String.format("%s = \'%s\'", SQLConstants.id, id));
        db.query(query.getSql(), (resultSet, rowName) -> {
            return this.addVastAdvertiserToList(resultSet.getString(advName),
                    resultSet.getString(category),
                    resultSet.getFloat(price),
                    id,
                    resultSet.getInt(budget),
                    resultSet.getString(vastUrl),
                    resultSet.getBoolean(active),
                    list,
                    resultSet);
        });
        return !list.isEmpty() ? list.get(0) : null;
    }

    private VastPublisher addVastPublisherToList(String pubName,
                                           String category,
                                           float cost,
                                           String id,
                                           boolean active,
                                           List<VastPublisher> list,
                                           ResultSet resultSet) {
        VastPublisher p = null;
        try {
            p = new VastPublisher(pubName,
                    category,
                    cost,
                    id,
                    active);
            list.add(p);
        } catch (VastEntityException e) {
            log.error(e.getMessage());
        }
        return p;
    }

    private VastAdvertiser addVastAdvertiserToList(String advName,
                                                 String category,
                                                 float price,
                                                 String id,
                                                 int budget,
                                                 String vastUrl,
                                                 boolean active,
                                                 List<VastAdvertiser> list,
                                                 ResultSet resultSet) {
        VastAdvertiser adv = null;
        try {
            adv = new VastAdvertiser(advName,
                    price,
                    category,
                    budget,
                    vastUrl,
                    id,
                    active);
            list.add(adv);
        } catch (VastEntityException e) {
            log.error(e.getMessage());
        }
        return adv;
    }

    public VastPublisher findOneVastPub(String id) throws VastEntityException {
        if (!isEntityExists(id, publishers, SQLConstants.id)) {
            throw new VastEntityException(VastErrorMessages.entityNotExists);
        }

        List<VastPublisher> list = new ArrayList<>();
        SQLQueryBuilder query = new SQLQueryBuilder(all, publishers,
                String.format("%s = \'%s\'", SQLConstants.id, id));
        db.query(query.getSql(), (resultSet, rowName) -> {
            return this.addVastPublisherToList(resultSet.getString(pubName),
                    resultSet.getString(category),
                    resultSet.getFloat(cost),
                    id,
                    resultSet.getBoolean(active),
                    list, resultSet);
        });
        return !list.isEmpty() ? list.get(0) : null;
    }

    public List<String> findAllVastAdvertisersByCondition(String whereClause) {
        List<String> advIdsList = new ArrayList<>();
        SQLQueryBuilder query = new SQLQueryBuilder(SQLConstants.id, advertisers,
                whereClause);

        db.query(query.getSql(), (resultSet, rowName) -> {
            return advIdsList.add(resultSet.getString(id));
        });

        return advIdsList;
    }

    /*
    @param condition can be a String or number
     */
    public int updateEntity(Map<String, Object> update, String entityTable,
                                      Object condition)
            throws DataAccessException, VastEntityException {
        int rowsUpdated = 0;
        SQLUpdateBuilder sqlUpdateBuilder =
                new SQLUpdateBuilder(update, entityTable, condition);
        String sql = sqlUpdateBuilder.getSql();
        log.info("update sql: " + sql);
        rowsUpdated = db.update(sql);

        if (rowsUpdated == 0) {
            String errorMsg = String.format("%s %s %s: %s",
                    entityTable,
                    condition,
                    update.get(condition).toString(),
                    entityUpdateUnsuccessfull);
            throw new VastEntityException(errorMsg);
        }
        return rowsUpdated;
    }

    public int deleteOnePublisher(String pubId)
    throws VastEntityException {
        VastPublisher pub;
        int rowsUpdated = 0;

        pub = this.findOneVastPub(pubId);
        String whereClause = String.format("%s = \'%s\'",
                SQLConstants.id, pubId);
        SQLDeleteBuilder sqlBuilder = new SQLDeleteBuilder(publishers,
                whereClause);
        rowsUpdated = this.db.update(sqlBuilder.getSql());

        if (rowsUpdated == 0) {
            String errorMsg = String.format("Couldn\'t delete %s: %s",
                    Routes.pubId, pubId);
            throw new VastEntityException(errorMsg);
        }

        return rowsUpdated;
    }

    public int deleteOneAdvertiser(String advId)
            throws VastEntityException {
        VastAdvertiser adv;
        int rowsUpdated = 0;

        adv = this.findOneVastAdv(advId);
        String whereClause = String.format("%s = \'%s\'",
                SQLConstants.id, advId);
        SQLDeleteBuilder sqlBuilder = new SQLDeleteBuilder(advertisers,
                whereClause);
        rowsUpdated = this.db.update(sqlBuilder.getSql());

        if (rowsUpdated == 0) {
            String errorMsg = String.format("Couldn\'t delete %s: %s",
                    Routes.advId, advId);
            throw new VastEntityException(errorMsg);
        }

        return rowsUpdated;
    }

    public void insertTrackingEvent(TrackingEvent tr) {
        List<Object[]> insertTrData = new ArrayList<>();
        insertTrData.add(new Object[] {tr.getEvent(), tr.getPubId(), tr.getAdvId()});
        this.db.batchUpdate(String.format("%s %s(%s, %s, %s) VALUES (?, ?, ?)",
                "INSERT INTO ", stats, event, Routes.pubId, Routes.advId),
                insertTrData);
    }



    public void deactivateAdvertiser(String id)
        throws VastEntityException {
        Map<String, Object> update = new HashMap<>();
        update.put(active, false);
        update.put(SQLConstants.id, id);

        this.updateEntity(update, advertisers, SQLConstants.id);
    }

    private void insertRowInAggregatedStats(StatsRow row) {
        List<Object[]> insertData = new ArrayList<>();
        insertData.add(new Object[] {row.getHour(), row.getPubName(),
                row.getAdvName(), row.getImpressions(),
                row.getSpend(), row.getRevenue(), row.getProfit()
        });
        this.db.batchUpdate(String.format("%s %s(%s, %s, %s, %s, %s, %s, %s) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "INSERT INTO ", aggregatedStats,
                SQLConstants.hour,
                SQLConstants.pubName, SQLConstants.advName,
                SQLConstants.impressions,
                SQLConstants.spend, SQLConstants.revenue,
                SQLConstants.profit),
                insertData);
    }

    public void updateAggregatedStats() {
        String query = SQLQueryBuilder.getStatsAggregationQuery();
        this.db.query(query, (resultSet, rowName) -> {
            StatsRow row = new StatsRow(
                    resultSet.getInt(hour),
                    resultSet.getString(pubName),
                    resultSet.getString(advName),
                    resultSet.getInt(impressions),
                    resultSet.getFloat(spend),
                    resultSet.getFloat(revenue),
                    resultSet.getFloat(profit)
            );
            this.insertRowInAggregatedStats(row);
            return null;
        });
    }

    public void clearAggregatedStats() {
        String truncateTableStatement = "TRUNCATE " + aggregatedStats;
        this.db.update(truncateTableStatement);
        log.info("truncated " + aggregatedStats);
    }

    public List<StatsRow> getTodayAggregatedStats() {
        List<StatsRow> list = new ArrayList<>();
        SQLQueryBuilder query = new SQLQueryBuilder(SQLConstants.all, aggregatedStats, null);
        this.db.query(query.getSql(), (resultSet, rowName) -> {
            StatsRow row = new StatsRow(
                    resultSet.getInt(hour),
                    resultSet.getString(pubName),
                    resultSet.getString(advName),
                    resultSet.getInt(impressions),
                    resultSet.getFloat(spend),
                    resultSet.getFloat(revenue),
                    resultSet.getFloat(profit)
            );
            list.add(row);
            return null;
        });

        return list;
    }

    public List<VastPublisher> findAllPublishers() {
        List<VastPublisher> list = new ArrayList<>();
        SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SQLConstants.all,
                publishers, null);
        this.db.query(queryBuilder.getSql(), (resultSet, rowName) -> {
            return this.addVastPublisherToList(resultSet.getString(pubName),
                    resultSet.getString(category),
                    resultSet.getFloat(cost),
                    resultSet.getString(id),
                    resultSet.getBoolean(active),
                    list, resultSet);
        });
        return list;
    }

    public List<VastAdvertiser> findAllAdvertisers() {
        List<VastAdvertiser> list = new ArrayList<>();
        SQLQueryBuilder queryBuilder = new SQLQueryBuilder(SQLConstants.all,
                advertisers, null);
        this.db.query(queryBuilder.getSql(), (resultSet, rowName) -> {
            return this.addVastAdvertiserToList(resultSet.getString(advName),
                    resultSet.getString(category),
                    resultSet.getFloat(price),
                    resultSet.getString(id),
                    resultSet.getInt(budget),
                    resultSet.getString(vastUrl),
                    resultSet.getBoolean(active),
                    list,
                    resultSet);
        });
        return list;
    }
}
