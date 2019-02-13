package idv.david.getjsondata;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private static final String MY_URL = "http://data.tycg.gov.tw/api/v1/rest/datastore/a1b4714b-3b75-4ff8-a8f2-cc377e4eaa0f?format=json";

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResult = findViewById(R.id.idResult);


    }


    public void onSubmitClick(View view) {
        if (isNetworkConnected()) {
            new GetDataTask().execute(MY_URL);

        } else {
            Toast.makeText(this, "No Network connected", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//從sevice取得網路Manager
        NetworkInfo info = cManager.getActiveNetworkInfo();//拿到網路資訊
        return info != null && info.isConnected();
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            try {
                String url = strings[0];
                URL myURL = new URL(url);
                HttpURLConnection con = (HttpURLConnection) myURL.openConnection();//建立連線
                con.setRequestMethod("GET");//利用GET請求
                con.setDoInput(true);//允許輸入的動作
                con.setUseCaches(false);//是否使用快取

                int statusCode = con.getResponseCode();
                if (statusCode == HttpsURLConnection.HTTP_OK) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));//從字串轉為位元
                    String str;
                    while ((str = br.readLine()) != null) {
                        sb.append(str);

                    }
                } else {
                    Log.e("main", String.valueOf(statusCode));//當出錯顯示訊息
                }


            } catch (IOException ie) {
                Log.e("main", ie.toString());
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ie) {
                        Log.e("main", ie.toString());
                    }
                }
            }


            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            StringBuilder sb = new StringBuilder();
            try {
                JSONObject jo = new JSONObject(s);
                JSONArray jArray = jo.getJSONArray("results");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject data = jArray.getJSONObject(i);
                    JSONArray data1 = data.getJSONArray("records");
                    JSONObject data2=data1.getJSONObject(i);
                    String sna = data2.getString("sna");
                    sb.append(sna);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            tvResult.setText(sb);
        }
    }
}
