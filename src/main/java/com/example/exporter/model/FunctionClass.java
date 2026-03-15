package com.example.exporter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunctionClass {

    private String functionName;
    private int definitionLines;
    private int effectiveLines;
    private List<FunctionClass> childFunctions;
    private List<SqlSession> sqlSessions;

    public FunctionClass() {
        this.childFunctions = new ArrayList<>();
        this.sqlSessions = new ArrayList<>();
    }

    public FunctionClass(String functionName, int definitionLines, int effectiveLines) {
        this.functionName = functionName;
        this.definitionLines = definitionLines;
        this.effectiveLines = effectiveLines;
        this.childFunctions = new ArrayList<>();
        this.sqlSessions = new ArrayList<>();
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getDefinitionLines() {
        return definitionLines;
    }

    public void setDefinitionLines(int definitionLines) {
        this.definitionLines = definitionLines;
    }

    public int getEffectiveLines() {
        return effectiveLines;
    }

    public void setEffectiveLines(int effectiveLines) {
        this.effectiveLines = effectiveLines;
    }

    public List<FunctionClass> getChildFunctions() {
        return Collections.unmodifiableList(childFunctions);
    }

    public void setChildFunctions(List<FunctionClass> childFunctions) {
        this.childFunctions = new ArrayList<>(childFunctions);
    }

    public void addChildFunction(FunctionClass child) {
        this.childFunctions.add(child);
    }

    public List<SqlSession> getSqlSessions() {
        return Collections.unmodifiableList(sqlSessions);
    }

    public void setSqlSessions(List<SqlSession> sqlSessions) {
        this.sqlSessions = new ArrayList<>(sqlSessions);
    }

    public void addSqlSession(SqlSession sqlSession) {
        this.sqlSessions.add(sqlSession);
    }
}
