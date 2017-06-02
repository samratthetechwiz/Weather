package com.example.samrat.weather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Timestamp;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTextView;

    public void getWeather(View view) {

        InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        if(cityName.getText().toString() == ""){
            Toast.makeText(this,"Please Enter a City Name",Toast.LENGTH_SHORT).show();
        }else{
            try {
                String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

                DownloadTask task = new DownloadTask();
                task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=5d388abec03d14220547b0231b9ded4c");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText) findViewById(R.id.editText);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;

                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String message1 = "";
                String message2 = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");
                    if (main != "" && description != "") {
                        message1 += main + ": " + description + "\r\n";
                    }
                }

                JSONObject main = jsonObject.getJSONObject("main");
                String temp = "";
                String temp_min = "";
                String temp_max = "";

                temp = main.getString("temp");
                temp_min = main.getString("temp_min");
                temp_max = main.getString("temp_max");

                if (temp != "" || temp_min != "" || temp_max != "") {
                    message2 += "Temperature : " + temp +
                            "\r\nTemperature Minimum : " + temp_min +
                            "\r\nTemperature Maximum : " + temp_max;
                }

                if (message1 != "" || message2 != "") {
                    resultTextView.setText(message1 + message2);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }
        }
    }

}
