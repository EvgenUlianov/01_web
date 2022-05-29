package ru.netology;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Request {

    final private String path;
    final private List<NameValuePair> headers;
    final private List<NameValuePair> querryParams;
    private List<NameValuePair> postParams;
    private String body;
    private int contentLength;
    private ContentType contentType;

    public enum ContentType{
        X_WWW_FORM_URLENCODE,
        MULTIPART_FORM_DATA,
        NONE;

        static public ContentType getContentType(String contentTypeString){
            if(contentTypeString.equals("application/x-www-form-urlencoded"))
                return X_WWW_FORM_URLENCODE;
            if(contentTypeString.equals("multipart/form-data"))
                return MULTIPART_FORM_DATA;
            return NONE;
        }
    }

    public List<String> getHeader(String key){
        return headers.stream()
                .filter((nameValuePair) -> nameValuePair.getName().equals(key))
                .map(NameValuePair::getValue)
                .toList();
    }

    @Deprecated
    public void setHeader(String key, String value){
        headers.add(new BasicNameValuePair(key, value));
        if (key.equals("Content-Length"))
            setContentLength(Integer.parseInt(value));
        if (key.equals("Content-Type"))
            setContentType(ContentType.getContentType(value));
    }

    public void setHeader(NameValuePair nameValuePair){
        headers.add(nameValuePair);
        if (nameValuePair.getName().equals("Content-Length"))
            setContentLength(Integer.parseInt(nameValuePair.getValue()));
        if (nameValuePair.getName().equals("Content-Type"))
            setContentType(ContentType.getContentType(nameValuePair.getValue()));
    }

    public List<String> getQuerryParam(String key){
        return querryParams.stream()
                .filter((nameValuePair) -> nameValuePair.getName().equals(key))
                .map(NameValuePair::getValue)
                .toList();
    }

    @Deprecated
    public void setQuerryParam(String key, String value){
        querryParams.add(new BasicNameValuePair(key, value));
    }

    public void setQuerryParam(NameValuePair nameValuePair){
        querryParams.add(nameValuePair);
    }

    public List<String> getPostParam(String key){
        return postParams.stream()
                .filter((nameValuePair) -> nameValuePair.getName().equals(key))
                .map(NameValuePair::getValue)
                .toList();
    }

    public boolean bodyIsEmpty() {
        if (body == null)
            return true;
        return body.isEmpty();
    }


    Request(String path){
        this.path = path;
        headers = new ArrayList<>();
        querryParams = new ArrayList<>();
//        postParams = new ArrayList<>();
        contentLength = 0;
        contentType = ContentType.NONE;
    }

    @Override
    public String toString(){
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("============ Querry params: ============");
        resultBuilder.append("\r\n");
        for (NameValuePair nameValuePair: getQuerryParams()) {
            resultBuilder.append(nameValuePair.getName());
            resultBuilder.append(": ");
            resultBuilder.append(nameValuePair.getValue());
            resultBuilder.append("\r\n");
        }
        resultBuilder.append("\r\n");
        resultBuilder.append("=============== Headers: ===============");
        resultBuilder.append("\r\n");
        for (NameValuePair nameValuePair: getHeaders()) {
            resultBuilder.append(nameValuePair.getName());
            resultBuilder.append(": ");
            resultBuilder.append(nameValuePair.getValue());
            resultBuilder.append("\r\n");
        }
        if ((getContentType() == Request.ContentType.X_WWW_FORM_URLENCODE)
                && !(getPostParams()==null)) {
            resultBuilder.append("\r\n");
            resultBuilder.append("=== application/x-www-form-urlencoded: ==");
            resultBuilder.append("\r\n");
            for (NameValuePair nameValuePair: getPostParams()) {
                resultBuilder.append(nameValuePair.getName());
                resultBuilder.append(": ");
                resultBuilder.append(nameValuePair.getValue());
                resultBuilder.append("\r\n");
            }
        }else if(!bodyIsEmpty()){
            resultBuilder.append("\r\n");
            resultBuilder.append("================= Body: =================");
            resultBuilder.append("\r\n");
            resultBuilder.append(getBody());
        }
        return  resultBuilder.toString();
    }
}
