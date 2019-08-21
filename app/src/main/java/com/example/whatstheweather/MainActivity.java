package com.example.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    Button button;
    TextView weatherView;
    TextView cityText;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String result = "";
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();

                while (data != -1){
                    result += (char) data;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                weatherView.setText("");
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather content", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);

                String resultWeather = "";

                for(int i=0; i<array.length(); i++){
                    JSONObject object = array.getJSONObject(i);

                    String main = object.getString("main");
                    String description = object.getString("description");

                    if(! main.equals("") && ! description.equals("")){
                        resultWeather += main + "   :   " + description + "\n";
                    }

                }

                if(! resultWeather.equals("")){
                    weatherView.setText(resultWeather);
                }else{
                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    weatherView.setText("");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                weatherView.setText("");
            }

        }
    }

    public void tellWeather(View view){

        try {
        DownloadTask task = new DownloadTask();

        // For converting spaces ibn city name into %20  which is URL format
        String encodedCityName = URLEncoder .encode(cityText.getText().toString() , "UTF-8" );

            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=b6907d289e10d714a6e88b30761fae22").get();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
            weatherView.setText("");
        }

        // For hiding Keyboard as soon as Button is pressed
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(weatherView.getWindowToken(), 0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        weatherView = findViewById(R.id.weatherView);
        cityText = findViewById(R.id.cityText);

    }
}
