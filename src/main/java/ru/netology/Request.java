package ru.netology;

import lombok.Getter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Request {

    final private String method;
    final private String path;
    final private List<NameValuePair> headers;
    final private List<NameValuePair> queryParams;
    final private List<NameValuePair> postParams;
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

    public List<String> getQueryParam(String key){
        return queryParams.stream()
                .filter((nameValuePair) -> nameValuePair.getName().equals(key))
                .map(NameValuePair::getValue)
                .toList();
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

    private void copyPostParams(List<NameValuePair> postParams){
        if (!this.postParams.isEmpty()) this.postParams.clear();
        postParams.stream().forEach(a -> this.postParams.add(new BasicNameValuePair(a.getName(), a.getValue())));
    }

    static public Request getRequest(BufferedReader in) throws IOException{

        //need to check requestLine before creating a request
        final String requestLine = in.readLine();
        if (requestLine == null)
            return null;

        char [] separators = {' ', '?', '&'};
        List<NameValuePair> nameValuePairs =  URLEncodedUtils.parse(requestLine, Charset.defaultCharset(), separators);//forName("UTF-8"));
        if (nameValuePairs.size() < 2)
            return null;

        //at minimal, we have method and path
        return new Request(nameValuePairs, in);
    }

    private Request(List<NameValuePair> nameValuePairs, BufferedReader in) throws IOException {
        this.method = nameValuePairs.get(0).getName();
        this.path = nameValuePairs.get(1).getName();
        this.headers = new ArrayList<>();
        this.queryParams = new ArrayList<>();
        this.postParams = new ArrayList<>();
        this.contentLength = 0;
        this.contentType = ContentType.NONE;

        for (NameValuePair nameValuePair: nameValuePairs) {
            if (nameValuePair.getValue() != null)
                setQueryParam(nameValuePair);
        }
        System.out.println("//");

        NameValuePair requestNextHeader = inReadHeader(in);
        int a =1;
        while(requestNextHeader != null
                && !requestNextHeader.getName().isEmpty()){
            this.setHeader(requestNextHeader);
            //if(!socket.isClosed() && in.ready()){
            if(in.ready()){
                try {
                    requestNextHeader = inReadHeader(in);
                    if (requestNextHeader != null)
                        System.out.printf("%d. %s \n",a++, requestNextHeader);
                } catch  (IOException e) {
                    System.out.println("Exception readLine");
                    e.printStackTrace();
                    break;
                }
            } else{
                System.out.println("Breacked");
                break;
            }

        }
        String requestBodyString = inReadBody(in, getContentLength());
        if (getContentType() == Request.ContentType.X_WWW_FORM_URLENCODE){
            char [] bodySeparators = {'&'};
            copyPostParams(URLEncodedUtils.parse(requestBodyString, Charset.defaultCharset(), bodySeparators));//forName("UTF-8"));

        } else{
            this.body = requestBodyString;
        }

    }

    private void setHeader(NameValuePair nameValuePair){
        headers.add(nameValuePair);
        if (nameValuePair.getName().equals("Content-Length"))
            contentLength = Integer.parseInt(nameValuePair.getValue());
        if (nameValuePair.getName().equals("Content-Type"))
            contentType = ContentType.getContentType(nameValuePair.getValue());
    }

    private void setQueryParam(NameValuePair nameValuePair){
        queryParams.add(nameValuePair);
    }

    private NameValuePair inReadHeader(BufferedReader in) throws IOException{
        String key;
        String value;

        ArrayList<Character> characterArrayKey = new ArrayList<>();
        ArrayList<Character> characterArrayValue = new ArrayList<>();
        boolean addToKey = true;
        boolean addToValue = false;
        final char separator = ':';

        char nextChar = (char) in.read();
        while (nextChar != '\n'){


            if (!addToKey && !addToValue && nextChar != separator)
                addToValue = true;
            if (addToKey && nextChar == separator)
                addToKey = false;

            if (addToKey)
                characterArrayKey.add(nextChar);
            if (addToValue)
                characterArrayValue.add(nextChar);
            nextChar = (char) in.read();
        }
        if (characterArrayKey.isEmpty()
                && characterArrayValue.isEmpty())
            return null;
        if (characterArrayKey.size() == 1
                && characterArrayKey.get(0) == '\r'
                && characterArrayValue.isEmpty())
            return null;

        char [] charArrayKey = new char[characterArrayKey.size()];
        for (int i = 0; i < characterArrayKey.size(); i++)
            charArrayKey[i] = characterArrayKey.get(i);
        key = String.valueOf(charArrayKey);
        char [] charArrayValue = new char[characterArrayValue.size()];
        for (int i = 0; i < characterArrayValue.size(); i++)
            charArrayValue[i] = characterArrayValue.get(i);
        value = String.valueOf(charArrayValue);
        return new BasicNameValuePair(key, parseURL(value));

    }

    private String inReadBody(BufferedReader in, int contentLength) throws IOException{
        String requestBody;
        if (contentLength == 0)
            return "";
        ArrayList<Character> characterArray = new ArrayList<>();
        char nextChar = (char) in.read();
        int index = 0;
        while (true){
            characterArray.add(nextChar);
            index++;
            if(index == contentLength)
                break;
            nextChar = (char) in.read();
        }
        char [] charArray = new char[characterArray.size()];
        for (int i = 0; i < characterArray.size(); i++)
            charArray[i] = characterArray.get(i);
        requestBody = String.valueOf(charArray);
        return requestBody;
    }

    private String parseURL(String unparsed){
        final char [] separators = {'\n'};
        List<NameValuePair> headersBodyValuePairs =  URLEncodedUtils.parse(unparsed, Charset.defaultCharset(), separators);
        StringBuilder builder = new StringBuilder();

        boolean isFirst = true;
        for (NameValuePair headersBodyValuePair:headersBodyValuePairs) {
            if (isFirst){
                isFirst= false;
            }
            else{
                builder.append('\n');
            }
            builder.append(headersBodyValuePair.toString());
        }
        return  builder.toString();
    }

    @Override
    public String toString(){
        StringBuilder resultBuilder = new StringBuilder();
        if (!path.isEmpty()){
            resultBuilder.append("================ Path: =================");
            resultBuilder.append("\r\n");
            resultBuilder.append(path);
            resultBuilder.append("\r\n");
        }
        resultBuilder.append("============ Query params: =============");
        resultBuilder.append("\r\n");
        for (NameValuePair nameValuePair: getQueryParams()) {
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
