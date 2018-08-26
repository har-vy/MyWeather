package com.example.android.customedittext;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    TextView cityField,updatedField,selectCity,weatherIcon,detailsField,currTemp,humidityField,
    pressureField;

    ProgressBar loader;
    Typeface weatherFont;

    String CITY="Bhopal, IND";
    final String APIKEY="841662a5523ac209a79a7baef7f043c5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the action bar that contains buttons to go back
        //or show current running activities
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        loader=(ProgressBar) findViewById(R.id.loader);
        cityField=(TextView) findViewById(R.id.city_field);
        updatedField=(TextView) findViewById(R.id.updated_field);
        selectCity=(TextView) findViewById(R.id.selectCity);
        weatherIcon=(TextView) findViewById(R.id.weatherIcon);
        detailsField=(TextView) findViewById(R.id.detailsField);
        currTemp=(TextView) findViewById(R.id.currTemp);
        humidityField=(TextView) findViewById(R.id.humidityField);
        pressureField=(TextView) findViewById(R.id.pressureField);
        weatherFont= Typeface.createFromAsset(getAssets(),"fonts/weathericons-regular-webfont.ttf");

        weatherIcon.setTypeface(weatherFont);
        taskLoadUp(CITY);

        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog= new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Change City");
                final EditText input=new EditText(MainActivity.this);
                input.setText(CITY);

                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                 LinearLayout.LayoutParams.MATCH_PARENT,
                 LinearLayout.LayoutParams.MATCH_PARENT);

                input.setLayoutParams(lp);
                dialog.setView(input);

                dialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      CITY=input.getText().toString();
                      taskLoadUp(CITY);
                    }
                });
                dialog.show();

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.cancel();
                    }
                });
                dialog.show();
            }
        });


    }
    public void taskLoadUp(String city){
     if(Utility.isNetworkAvailable(getApplicationContext())){
      DownloadWeather task=new DownloadWeather();
       task.execute(city);
     }
     else
         Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
    }

    class DownloadWeather extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
         String xml=Utility.buildConnection("http://api.openweathermap.org/data/2.5/weather?q=" + strings[0] +
                 "&units=metric&appid=" + APIKEY);
         return xml;
        }
        @Override
        protected void onPostExecute(String xml){
         try{
             JSONObject json= new JSONObject(xml);

             if(json!=null){
              JSONObject details=json.getJSONArray("weather").getJSONObject(0);
              JSONObject main=json.getJSONObject("main");
              DateFormat df=DateFormat.getDateTimeInstance();

              cityField.setText(json.getString("name").toUpperCase(Locale.US)+","+
              json.getJSONObject("sys").getString("country"));

                 detailsField.setText(details.getString("description").toUpperCase(Locale.US));
                 currTemp.setText(String.format("%.2f", main.getDouble("temp")) + "°");
                 humidityField.setText("Humidity: " + main.getString("humidity") + "%");
                 pressureField.setText("Pressure: " + main.getString("pressure") + " hPa");
                 updatedField.setText(df.format(new Date(json.getLong("dt") * 1000)));
                 weatherIcon.setText(Html.fromHtml(Utility.setWeatherIcon(details.getInt("id"),
                         json.getJSONObject("sys").getLong("sunrise") * 1000,
                         json.getJSONObject("sys").getLong("sunset") * 1000)));

                 loader.setVisibility(View.GONE);

             }
         } catch (JSONException e) {
             Toast.makeText(getApplicationContext(), "Error, Check City", Toast.LENGTH_SHORT).show();
             }
         }
        }
    }

