package com.tatusafety.matuba.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by dickson-incentro on 4/18/18.
 */

public class MetaStringRequest extends JsonRequest<MetaStringRequest.MetaResponse> {


    private Response.Listener<MetaStringRequest.MetaResponse> mListener;

    public MetaStringRequest(int method, String url, String jsonBody, Response.Listener<MetaStringRequest.MetaResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonBody, listener, errorListener);
        this.mListener = listener;
    }


    @Override
    protected void deliverResponse(MetaResponse response) {
        this.mListener.onResponse(response);
    }

    @Override
    protected Response<MetaResponse> parseNetworkResponse(NetworkResponse response) {
        String parsed;

        try {
            parsed = new String(response.data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        MetaResponse responseM = new MetaResponse();
        responseM.headers = response.headers;
        responseM.response = parsed;

        return Response.success(responseM, HttpHeaderParser.parseCacheHeaders(response));
    }


    public static class MetaResponse {
        Map<String, String> headers;
        String response;

        public String getResponse() {
            return response;
        }
        public Map<String, String> getHeaders() {
            return headers;
        }
    }

}
