package com.example.exporter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SqlSession {

    private String sqlId;
    private String sqlType;
    private List<TableUsage> tables;

    public SqlSession() {
        this.tables = new ArrayList<>();
    }

    public SqlSession(String sqlId, String sqlType) {
        this.sqlId = sqlId;
        this.sqlType = sqlType;
        this.tables = new ArrayList<>();
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public List<TableUsage> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public void setTables(List<TableUsage> tables) {
        this.tables = new ArrayList<>(tables);
    }

    public void addTable(TableUsage table) {
        this.tables.add(table);
    }
}
