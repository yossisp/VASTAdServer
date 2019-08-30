package com.vastserver.classes.sqlStatements;

import java.util.Map;
import java.util.Set;

public class SQLUpdateBuilder implements SQLStatement {
    private Map<String, Object> update;
    private String entityTable;
    private Object condition;

    public SQLUpdateBuilder(Map<String, Object> update,
                            String entityTable,
                            Object condition) {
        this.update = update;
        this.entityTable = entityTable;
        this.condition = condition;
    }

    public String getSql() {
        String sql = String.format("UPDATE %s\nSET\n", this.entityTable);
        Set<String> keys = update.keySet();

        for (String key: keys) {
            sql += String.format("%s = \'%s\',\n", key, update.get(key));
        }

        sql = sql.substring(0, sql.length() - 2); //remove the "," before WHERE clause
        sql = sql + "\n"; //add newline before WHERE clause
        sql += String.format("WHERE %s = \'%s\'", this.condition,
                update.get(this.condition).toString());
        return sql;
    }

    public Map<String, Object> getUpdate() {
        return update;
    }

    public void setUpdate(Map<String, Object> update) {
        this.update = update;
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }

    public Object getCondition() {
        return condition;
    }

    public void setCondition(Object condition) {
        this.condition = condition;
    }
}
