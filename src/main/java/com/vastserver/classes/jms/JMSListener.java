package com.vastserver.classes.jms;
import com.vastserver.classes.sqlStatements.SQLQueryBuilder;
import com.vastserver.config.JMSOperations;
import com.vastserver.db.DBHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.vastserver.config.SQLConstants.id;
import static com.vastserver.config.VastConfig.jmsTopicName;
import static com.vastserver.config.VastErrorMessages.unknownJmsMessage;

@Component
public class JMSListener {
    private final Logger log = LogManager.getLogger(JMSListener.class);

    @Autowired
    private DBHandler db;

    @JmsListener(destination = jmsTopicName, containerFactory = "topicListenerFactory")
    public void receiveTopicMessage(JMSOperations message) {
        this.messageHandler(message);
    }

    private void messageHandler(JMSOperations operation) {
        log.info("Received message <" + operation.toString() + ">");
        switch (operation) {
            case CHECK_OUT_OF_BUDGET_ADVERTISERS: {
                this.performBudgetCheck();
                break;
            }
            case AGGREGATE_STATS: {
                this.aggregateStats();
                break;
            }
            case CLEAR_AGGREGATED_STATS_TABLE: {
                this.clearAggregatedStatsTable();
                break;
            }
            default: {
                log.warn(unknownJmsMessage);
            }
        }
    }

    private List<String> getAdvertisersOutOfBudget() {
        List<String> advIdsList = new ArrayList<>();
        String query = SQLQueryBuilder.getQueryForAdvertisersOutOfBudget();

        db.getJdbcTemplate().query(query, (resultSet, rowName) -> {
            return advIdsList.add(resultSet.getString(id));
        });

        return advIdsList;
    }

    private void performBudgetCheck() {
        List<String> advertiserIds = getAdvertisersOutOfBudget();
        if (!advertiserIds.isEmpty()) {
            for (String advId: advertiserIds) {
                try {
                    db.deactivateAdvertiser(advId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private void aggregateStats() {
        this.db.updateAggregatedStats();
    }

    private void clearAggregatedStatsTable() {
        this.db.clearAggregatedStats();
    }
}
