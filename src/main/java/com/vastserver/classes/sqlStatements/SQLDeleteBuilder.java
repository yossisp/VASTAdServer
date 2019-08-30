package com.vastserver.classes.sqlStatements;

import com.vastserver.classes.VastLogger;
import org.apache.logging.log4j.Logger;

public class SQLDeleteBuilder implements SQLStatement {
    private Logger log = VastLogger.getLogger(SQLDeleteBuilder.class);

    private String from;
    private String where;

    public SQLDeleteBuilder(String from, String where) {
        this.from = from;
        this.where = where;
    }

    public String getSql() {
        String sql = String.format("DELETE FROM %s WHERE %s", this.from, this.where);
        return sql;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
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

}
