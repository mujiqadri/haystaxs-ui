package com.haystaxs.ui.support;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

/**
 * Created by adnan on 11/20/15.
 */
public class JsonResponse {
    private String result;
    private String additionalInfo;
    private String exceptionMessage;

    public JsonResponse(String result) {
        this.result = result;
    }

    public JsonResponse(String result, String additionalInfo) {
        this(result);
        this.additionalInfo = additionalInfo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
