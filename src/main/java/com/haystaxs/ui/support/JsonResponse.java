package com.haystaxs.ui.support;

import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;

/**
 * Created by adnan on 11/20/15.
 */
public class JsonResponse {
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String DONE = "done";

    private String result;
    private String message;
    private String additionalInfo;

    public JsonResponse(String result) {
        this.result = result;
    }

    public JsonResponse(String result, String message) {
        this(result);
        this.message = message;
    }

    public  JsonResponse(String result, String message, String additionalInfo) {
        this(result, message);
        this.additionalInfo = additionalInfo;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
