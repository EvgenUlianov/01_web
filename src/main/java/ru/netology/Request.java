package ru.netology;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Request {

    final private String path;
    final private Map<String, String> headers;
    final private Map<String, String> querryParams;
    private String body;
    private int contentLength;

    public String getHeader(String key){
        return headers.get(key);
    }
    public void setHeader(String key, String value){
        headers.put(key, value);
        if (key.equals("Content-Length"))
            setContentLength(Integer.valueOf(value));
    }

    public String getQuerryParam(String key){
        return querryParams.get(key);
    }
    public void setQuerryParam(String key, String value){
        querryParams.put(key, value);
    }

    public boolean bodyIsEmpty() {
        if (body == null)
            return true;
        if (body.isEmpty())
            return true;
        return false;
    }


    Request(String path){
        this.path = path;
        headers = new HashMap<>();
        querryParams = new HashMap<>();
        contentLength = 0;
    }
}
