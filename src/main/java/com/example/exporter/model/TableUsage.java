package com.example.exporter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableUsage {

    private String crudType;
    private List<String> tableNames;

    public TableUsage() {
        this.tableNames = new ArrayList<>();
    }

    public TableUsage(String crudType, List<String> tableNames) {
        this.crudType = crudType;
        this.tableNames = new ArrayList<>(tableNames);
    }

    public String getCrudType() {
        return crudType;
    }

    public void setCrudType(String crudType) {
        this.crudType = crudType;
    }

    public List<String> getTableNames() {
        return Collections.unmodifiableList(tableNames);
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = new ArrayList<>(tableNames);
    }

    public void addTableName(String tableName) {
        this.tableNames.add(tableName);
    }
}
