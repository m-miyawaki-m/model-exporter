package com.example.exporter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScreenAction {

    private String item;
    private String screenId;
    private String actionId;
    private List<FunctionClass> functions;

    public ScreenAction() {
        this.functions = new ArrayList<>();
    }

    public ScreenAction(String item, String screenId, String actionId) {
        this.item = item;
        this.screenId = screenId;
        this.actionId = actionId;
        this.functions = new ArrayList<>();
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public List<FunctionClass> getFunctions() {
        return Collections.unmodifiableList(functions);
    }

    public void setFunctions(List<FunctionClass> functions) {
        this.functions = new ArrayList<>(functions);
    }

    public void addFunction(FunctionClass function) {
        this.functions.add(function);
    }
}
