package com.example.weathermap;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {

    private String temperature;
    private String city;
    private String wind;
    private String humidity;
    private String pressure;
    private String description;
    private String country;

    public static Weather fromJson(JSONObject jsonObject) {

        try {

            Weather weather = new Weather();

            weather.city = jsonObject.getString("name");
            weather.country = jsonObject.getJSONObject("sys").getString("country");

            weather.description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

            double pressure = jsonObject.getJSONObject("main").getDouble("pressure");
            weather.pressure = Double.toString(pressure);

            double humidity = jsonObject.getJSONObject("main").getDouble("humidity");
            weather.humidity = Double.toString(humidity);

            double wind = jsonObject.getJSONObject("wind").getDouble("speed");
            weather.wind = Double.toString(wind);

            double temperatureInKelvin = jsonObject.getJSONObject("main").getDouble("temp");
            double temperatureInCelsius = temperatureInKelvin - 273.15;

            int approximateTemperature = (int) Math.rint(temperatureInCelsius);
            weather.temperature = Integer.toString(approximateTemperature);

            return weather;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTemperature() {
        return temperature + "°C";
    }

    public String getCity() {
        return city;
    }

    public String getWind() {
        return wind + "m/s";
    }

    public String getHumidity() {
        return humidity + "%";
    }

    public String getPressure() {
        return pressure + "hpa";
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }

}