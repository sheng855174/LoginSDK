package tw.playground.sheng.http.post;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


/**
 * Https Post請求
 */
public class HttpsPostThread extends AsyncTask<String, String, String> implements ILoginHttpsPostAdapter,IRegHttpsPostAdapter{

    private String httpUrl;
    private List<NameValuePair> valueList;
    private int mWhat;
    private TYPE type;
    private String result;

    public static final int ERROR = 404;
    public static final int SUCCESS = 200;

    public enum TYPE{
        LOGIN, REG, QUICK_REG
    }

    public HttpsPostThread(){
        super();
    }

    public HttpsPostThread(String httpUrl,
                           List<NameValuePair> list,TYPE type) {
        super();
        this.httpUrl = httpUrl;
        this.valueList = list;
        this.mWhat = SUCCESS;
        this.type = type;
    }

    /**
     * 獲取HttpClient
     *
     * @param params
     * @return
     */
    public static HttpClient getHttpClient(HttpParams params) {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            HttpProtocolParams.setUseExpectContinue(params, true);

            // 設置http https支持
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));// SSL/TSL的認證過程，端口為443
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient(params);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        switch (this.type)
        {
            case LOGIN:
                connect(this.httpUrl);
                break;
            case REG:
                break;
            case QUICK_REG:
                connect(this.httpUrl);
                break;
        }
        return this.result;
    }

    public int connect(String httpUrl){
        String result = null;
        try {
            HttpParams httpParameters = new BasicHttpParams();
            // 設置連接管理器的超時
            ConnManagerParams.setTimeout(httpParameters, 10000);
            // 設置連接超時
            HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
            // 設置socket超時
            HttpConnectionParams.setSoTimeout(httpParameters, 10000);
            HttpClient hc = getHttpClient(httpParameters);
            HttpPost post = new HttpPost(httpUrl);
            post.setEntity(new UrlEncodedFormEntity(valueList, HTTP.UTF_8));
            post.setParams(httpParameters);
            HttpResponse response = null;
            try {
                response = hc.execute(post);
            } catch (UnknownHostException e) {
                throw new Exception("Unable to access "
                        + e.getLocalizedMessage());
            } catch (SocketException e) {
                throw new Exception(e.getLocalizedMessage());
            }
            int sCode = response.getStatusLine().getStatusCode();
            if (sCode == HttpStatus.SC_OK) {
                this.result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);// 請求成功
                Log.d("sheng050", "https response code : " + mWhat);

            } else {
                this.result = "請求失敗" + sCode; // 請求失敗
                // 404 - 未找到
                Log.d("sheng050", String.valueOf(ERROR));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.result = "請求失敗,異常退出";
            Log.d("sheng050","請求失敗,異常退出");
        }
        return 0;
    }


    @Override
    protected void onPostExecute(String result) {
        Log.d("sheng050", "https response content : " + result);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);

            /*JSONObject quizJSON = jsonObject.getJSONObject("quiz");
            Log.d("sheng050", quizJSON.toString());
            JSONObject sportJSON = quizJSON.getJSONObject("sport");
            Log.d("sheng050", sportJSON.toString());

            JSONObject postJSON = jsonObject.getJSONObject("post");
            Log.d("sheng050", postJSON.toString());
            Log.d("sheng050", postJSON.getString("username"));
            Log.d("sheng050", postJSON.getString("password"));*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int login(String username, String password) {
        List<NameValuePair> parameters = new ArrayList<>();
        //POST 參數名稱
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        new HttpsPostThread("https://lsjtest.playground.tw:8088/mobileLogin/",parameters, TYPE.LOGIN).execute();
        return 0;
    }

    @Override
    public int register(String username, String password) {
        List<NameValuePair> parameters = new ArrayList<>();
        //POST 參數名稱
        parameters.add(new BasicNameValuePair("username", username));
        parameters.add(new BasicNameValuePair("password", password));
        new HttpsPostThread("https://lsjtest.playground.tw:8088/mobileLogin/",parameters,TYPE.REG).execute();
        return 0;
    }
    @Override
    public int quick_refister(String username, String password){

        return 0;
    }
}