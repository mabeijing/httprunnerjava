package com.httprunnerjava;

import com.alibaba.fastjson.JSONException;
import com.httprunnerjava.Common.Component.Enum.MethodEnum;
import com.httprunnerjava.Common.Component.Headers;
import com.httprunnerjava.Common.Component.LazyContent.LazyContent;
import com.httprunnerjava.Common.Component.LazyContent.LazyString;
import com.httprunnerjava.Common.Component.Params;
import com.httprunnerjava.Common.Component.TRequest;
import com.httprunnerjava.Common.Model.Intf.reqOrResp;
import com.httprunnerjava.Common.Model.ResponseObject;
import com.httprunnerjava.Common.Model.RunningAttribute.ReqRespData;
import com.httprunnerjava.Common.Model.RunningAttribute.RequestData;
import com.httprunnerjava.Common.Model.RunningAttribute.ResponseData;
import com.httprunnerjava.Common.Model.RunningAttribute.SessionData;
import com.httprunnerjava.exceptions.HrunExceptionFactory;
import lombok.Data;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
public class HttpSession {

    static Logger logger = LoggerFactory.getLogger(HttpSession.class);

    private OkHttpClient okHttpClient;

    private SessionData data;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public CookieJar getCookieJar(){
        return new CookieJar(){
            @Override
            public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                cookieStore.put(httpUrl.host(), list);
            }

            @Override
            public @NotNull List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                List<Cookie> cookies = cookieStore.get(httpUrl.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };
    }

    //TODO:下面的拦截器方法可以获取ip
    private Interceptor getInterceptor(){
        return chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);

            logger.info(chain.connection().socket().getRemoteSocketAddress().toString());
            return response ;
        };
    }

    private X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public HttpSession(HttpRunner httpRunner) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { getTrustManager() }, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        if(httpRunner.getConfig().getIsDebug().equals(true))
            okHttpClientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)));

        Integer timeOut = httpRunner.getConfig().getTimeOut();
        okHttpClient = okHttpClientBuilder
                //设置读取超时时间
                .readTimeout(timeOut, TimeUnit.SECONDS)
                //设置写的超时时间
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, getTrustManager())
                .addNetworkInterceptor(getInterceptor())
                .cookieJar(getCookieJar())
                .build();
    }

    public Response request(MethodEnum method, String url, TRequest request)
            throws Exception {
        this.data = new SessionData();

        long start_timestamp = System.currentTimeMillis();
        Response response = this._send_request_safe_mode(method, url, request);
        long response_time_ms = System.currentTimeMillis() - start_timestamp;

        //TODO：低优先级 获取ip地址
        // try:z
        //        client_ip, client_port = response.raw.connection.sock.getsockname()

        long content_size = (response.body() != null &&response.body().contentLength() == -1) ?
                0 : response.body().contentLength();

        // record the consumed time
        this.getData().getStat().setResponse_time_ms((float) response_time_ms);
        this.getData().getStat().setContent_size((float) content_size);

        // record request and response histories, include 30X redirection
        //TODO: response.history记录的是请求重定向的内容，暂时不支持重定向
        // response_list = response.history + [response]

        //TODO: 低优先级 这里需要对接口请求的status_code做一些判断处理，异常请求需要抛异常
        // try:
        //     response.raise_for_status()

        this.getData().setReq_resps(Collections.singletonList(get_req_resp_record(response)));

        logger.info(String.format("status_code: %s, response_time(ms): %s ms, response_length: %s bytes",
                response.code(),
                response_time_ms,
                content_size));

        return response;
    }

    public Response _send_request_safe_mode(MethodEnum method, String url, TRequest tRequest)
            throws Exception
    {
        Response response = null;
        switch(method){
            case GET:
                try {
                    Request.Builder requestBuilder = new Request.Builder();
                    addHeaders(tRequest.getHeaders(),requestBuilder);

                    Request request = requestBuilder.url(parseUrl(url,tRequest.getParams())).get().build();
                    response = okHttpClient.newCall(request).execute();
                }catch (IOException e){
                    logger.error("请求接口报错，请检查");
                    throw e;
                }
                break;
            case POST:
                try {
                    Request.Builder requestBuilder = new Request.Builder();
                    addHeaders(tRequest.getHeaders(),requestBuilder);
                    RequestBody requestBody = null;
                    String contentType = Optional.ofNullable(tRequest.getHeaders().getContent().get("Content-Type")).
                            orElse(new LazyString("")).getEvalValue().toString();
                    if(contentType.contains("application/json")){
                        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                        requestBody = RequestBody.create(tRequest.getReq_json().getEvalString(),mediaType);
                    }else if(contentType.contains("application/form-data")){

                    }else if(contentType.contains("application/x-www-form-urlencoded")){
                        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
                        requestBody = RequestBody.create(tRequest.getData().getEvalString(),mediaType);
                    }else{
                        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
                        requestBody = RequestBody.create(tRequest.getData().getEvalString(),mediaType);
                    }

                    Request request = requestBuilder.url(parseUrl(url,tRequest.getParams())).post(requestBody).build();
                    response = okHttpClient.newCall(request).execute();
                }catch (Exception e){
                    logger.error("请求接口报错，请检查");
                    throw e;
                }
        }

        return response;
    }

    public void addHeaders(Headers headers, Request.Builder requestBuilder){
        if(headers == null || headers.isEmpty())
            return;
        for(Map.Entry<String, LazyContent> entry : headers.getContent().entrySet()){
            requestBuilder.addHeader(entry.getKey(),String.valueOf(entry.getValue().getEvalValue()));
        }
    }

    public HttpUrl parseUrl(String url, Params params){
        if(params == null || params.getContent().size() == 0)
            return HttpUrl.parse(url);

        StringBuilder urlBuilder = new StringBuilder(url);
        if(params.getContent().size() != 0)
            urlBuilder.append("?");

        for(Map.Entry<String, LazyContent<?>> entry : params.getContent().entrySet()){
            urlBuilder.append(entry.getKey())
                      .append("=")
                      .append(entry.getValue().getEvalValue())
                      .append("&");
        }

        url = urlBuilder.toString();
        if(url.endsWith("&"))
            url = url.substring(0,url.length()-1);

        return HttpUrl.parse(url);
    }

    public ReqRespData get_req_resp_record(Response resp_obj){
        Headers request_headers = new Headers(resp_obj.request().headers());
        String request_cookies = Optional.ofNullable(resp_obj.request().header("Cookie")).orElse("");

        String request_body_str = "";
        RequestBody request_body = resp_obj.request().body();
        try{
            String str = "";
            if(request_body != null && request_body.contentLength() != 0L){
                final Buffer buffer = new Buffer();
                request_body.writeTo(buffer);
                str = buffer.readUtf8();
            }

            request_body_str =
                    (request_body != null && request_body.contentLength() != 0L) ? str : "";

            LazyContent<?> request_content_type = request_headers.getContent().get("content-type");
            //TODO:低优先级
            // if request_content_type and "multipart/form-data" in request_content_type:
            //     request_body = "upload file stream (OMITTED)"
        }catch (IOException e){
            HrunExceptionFactory.create("E0065");
        }

        RequestData request_data = new RequestData(resp_obj.request().method(),
                resp_obj.request().url().toString(),
                request_headers,
                request_cookies,
                request_body_str
                );
        log_print(request_data,"request");

        Headers resp_headers = new Headers(resp_obj.headers());
        LazyContent<?> content_type = resp_headers.getContent().get("content-type");
        //TODO:
        // if "image" in content_type:
        String resp_text = "";
        try{
            resp_text = resp_obj.body().string();
            ResponseObject.setCurrentRespBody(resp_text);
        }catch (IOException e){

        }catch(JSONException e){

        }

        ResponseData response_data = new ResponseData(resp_obj.code(),
                resp_headers,
                "",
                resp_obj.body().contentType().toString(),
                resp_obj.header("content-type"),
                resp_text
        );
        log_print(response_data,"response");

        return new ReqRespData(request_data, response_data);
    }

    public void log_print(reqOrResp req_or_resp, String r_type){
        String msg = String.format("\n================== %s details ==================\n",r_type);

        msg += req_or_resp.toString();
        logger.debug(msg);
    }
}

