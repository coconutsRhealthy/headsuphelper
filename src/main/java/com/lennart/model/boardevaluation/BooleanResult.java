package com.lennart.model.boardevaluation;

/**
 * Created by Lennart Popma on 23-6-2016.
 */
public class BooleanResult {
    private String functionDescription;
    private boolean result;

    public String getFunctionDescription() {
        return functionDescription;
    }

    public void setFunctionDescription(String functionDescription) {
        this.functionDescription = functionDescription;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
