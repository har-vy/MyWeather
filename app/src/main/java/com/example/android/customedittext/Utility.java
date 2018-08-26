package com.example.android.customedittext;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Utility {

    public static String buildConnection(String targetUrl){
        URL url;
        HttpURLConnection connection=null;

        try{
         url=new URL(targetUrl);
         //Opens a connection and casts it to HTTPUrlConnection
         connection= (HttpURLConnection)url.openConnection();
         connection.setRequestProperty("content-type", "application/json;  charset=utf-8");
         connection.setRequestProperty("Content-Language", "en-US");
         connection.setUseCaches(false);
         //Allow input stream
         connection.setDoInput(true);
         //Allows us to set an output on the specified URL
         connection.setDoOutput(false);

            InputStream is=connection.getInputStream();
            int status=connection.getResponseCode();

            if(status!=HttpURLConnection.HTTP_OK)
              //Error stream is used when there is a failed connection but the url still returns
               // useful information
              is=connection.getErrorStream();
            else is=connection.getInputStream();

            BufferedReader br=new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response=new StringBuffer();

            while((line=br.readLine())!=null){
             response.append(line);
             response.append("\r");
            }
            return response.toString();
        }
        catch(Exception e){
          return null;
        }
        finally {
          if(connection!=null)
              connection.disconnect();
        }
    }

    public static String setWeatherIcon(int actualId,long sunrise,long sunset){
     int id=actualId/100;
     String icon="";

     if(actualId==800){
         long currentTime= new Date().getTime();
         if(currentTime>=sunrise && currentTime<sunset)
             icon="&#xf00d;";
         else
             icon="&#xf02e;";
     }
     else{
      switch(id){
          case 2: icon="&#xf01e;";
          break;
          case 3: icon="&#xf01c;";
          break;
          case 7: icon="&#xf014;";
          break;
          case 5: icon="&#xf019;";
          break;
          case 8: icon="&#xf013;";
          break;
          case 6: icon="&#xf01b;";
          break;
      }
     }
     return icon;
    }
    public static boolean isNetworkAvailable(Context context)
    {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
