package br.ufrpe.ikaro.openmap;

import android.content.Context;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.text.Normalizer;

class Requests {
    private static Requests mInstance;
    private RequestQueue mQueue;

    private Requests(Context context) {
        mQueue = Volley.newRequestQueue(context);
    }

    static Requests getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Requests(context);
        }

        return mInstance;
    }

    private static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replace(" ", "%20");
    }

    void getObject(String url, Response.Listener<JSONObject> callback, Response.ErrorListener error) {
        url = removerAcentos(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, callback, error);
        request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

}