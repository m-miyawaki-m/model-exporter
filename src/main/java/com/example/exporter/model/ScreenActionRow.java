package com.example.exporter.model;

import com.opencsv.bean.CsvBindByName;

public class ScreenActionRow {

    @CsvBindByName(column = "ITEM")
    private String item;

    @CsvBindByName(column = "SCREEN_ID")
    private String screenId;

    @CsvBindByName(column = "ACTION_ID")
    private String actionId;

    @CsvBindByName(column = "FUNCTION_PATH")
    private String functionPath;

    @CsvBindByName(column = "DEFINITION_LINES")
    private int definitionLines;

    @CsvBindByName(column = "EFFECTIVE_LINES")
    private int effectiveLines;

    @CsvBindByName(column = "SQL_ID")
    private String sqlId;

    @CsvBindByName(column = "SQL_TYPE")
    private String sqlType;

    @CsvBindByName(column = "CRUD_TYPE")
    private String crudType;

    @CsvBindByName(column = "TABLE_NAME")
    private String tableName;

    public ScreenActionRow() {
    }

    public ScreenActionRow(String item, String screenId, String actionId,
                           String functionPath, int definitionLines, int effectiveLines,
                           String sqlId, String sqlType, String crudType, String tableName) {
        this.item = item;
        this.screenId = screenId;
        this.actionId = actionId;
        this.functionPath = functionPath;
        this.definitionLines = definitionLines;
        this.effectiveLines = effectiveLines;
        this.sqlId = sqlId;
        this.sqlType = sqlType;
        this.crudType = crudType;
        this.tableName = tableName;
    }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getScreenId() { return screenId; }
    public void setScreenId(String screenId) { this.screenId = screenId; }

    public String getActionId() { return actionId; }
    public void setActionId(String actionId) { this.actionId = actionId; }

    public String getFunctionPath() { return functionPath; }
    public void setFunctionPath(String functionPath) { this.functionPath = functionPath; }

    public int getDefinitionLines() { return definitionLines; }
    public void setDefinitionLines(int definitionLines) { this.definitionLines = definitionLines; }

    public int getEffectiveLines() { return effectiveLines; }
    public void setEffectiveLines(int effectiveLines) { this.effectiveLines = effectiveLines; }

    public String getSqlId() { return sqlId; }
    public void setSqlId(String sqlId) { this.sqlId = sqlId; }

    public String getSqlType() { return sqlType; }
    public void setSqlType(String sqlType) { this.sqlType = sqlType; }

    public String getCrudType() { return crudType; }
    public void setCrudType(String crudType) { this.crudType = crudType; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
}
