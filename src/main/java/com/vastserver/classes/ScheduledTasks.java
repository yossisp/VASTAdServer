package com.vastserver.classes;

import com.vastserver.config.JMSOperations;
import com.vastserver.config.VastConfig;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.vastserver.config.VastConfig.*;

@Component
public class ScheduledTasks {
    private Logger log = VastLogger.getLogger(ScheduledTasks.class);
    private Timer budgetCheckTimer = new Timer();
    private Timer statsAggregationTimer = new Timer();
    private Timer clearAggregatedStatsTimer = new Timer();

    @Autowired
    private JmsTemplate jmsTemplate;

    public void start() {
        this.budgetCheckTimer.scheduleAtFixedRate(askJmsConsumerToCheckBudget,
                budgetCheckInterval,
                budgetCheckInterval);
        this.statsAggregationTimer.scheduleAtFixedRate(askJmsConsumerToAggregateStats,
                statsAggregationIntervalInMillis,
                statsAggregationIntervalInMillis);
        this.clearAggregatedStats();

    }

    private TimerTask askJmsConsumerToCheckBudget = new TimerTask() {
        @Override
        public void run() {
            log.info("scheduling " +
                    JMSOperations.CHECK_OUT_OF_BUDGET_ADVERTISERS.toString());

            jmsTemplate.convertAndSend(VastConfig.jmsTopicName,
                    JMSOperations.CHECK_OUT_OF_BUDGET_ADVERTISERS);
        }
    };

    private TimerTask askJmsConsumerToAggregateStats = new TimerTask() {
        @Override
        public void run() {
            log.info("scheduling " +
                    JMSOperations.AGGREGATE_STATS.toString());

            jmsTemplate.convertAndSend(VastConfig.jmsTopicName,
                    JMSOperations.AGGREGATE_STATS);
        }
    };

    private Calendar getTimeToClearAggregatedStats() {
        Calendar date = Calendar.getInstance();

        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date;
    }

    private void clearAggregatedStats() {
        Calendar date = this.getTimeToClearAggregatedStats();
        this.clearAggregatedStatsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                jmsTemplate.convertAndSend(VastConfig.jmsTopicName,
                        JMSOperations.CLEAR_AGGREGATED_STATS_TABLE);
            }
        }, clearStatsAggregationIntervalInMillis,
                clearStatsAggregationIntervalInMillis);
    };

    public Timer getBudgetCheckTimer() {
        return budgetCheckTimer;
    }

    public void setBudgetCheckTimer(Timer budgetCheckTimer) {
        this.budgetCheckTimer = budgetCheckTimer;
    }

    public Timer getStatsAggregationTimer() {
        return statsAggregationTimer;
    }

    public void setStatsAggregationTimer(Timer statsAggregationTimer) {
        this.statsAggregationTimer = statsAggregationTimer;
    }

    public Timer getClearAggregatedStatsTimer() {
        return clearAggregatedStatsTimer;
    }

    public void setClearAggregatedStatsTimer(Timer clearAggregatedStatsTimer) {
        this.clearAggregatedStatsTimer = clearAggregatedStatsTimer;
    }
}
