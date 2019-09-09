package com.example.weathermap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    TextView txtLocation, txtTemperature, txtWindSpeed, txtHumidity, txtPressure, txtWeatherDescription;
    Marker marker;
    SupportMapFragment mapFragment;
    SearchView searchView;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String API_KEY = "18f6f9fd18eb09dc3fdf619828db8a97";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        txtLocation = findViewById(R.id.name);
        txtTemperature = findViewById(R.id.temperature);
        txtWindSpeed = findViewById(R.id.wind);
        txtHumidity = findViewById(R.id.humidity);
        txtPressure = findViewById(R.id.pressure);
        txtWeatherDescription = findViewById(R.id.desc);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                map.clear();
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(MainActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    }   catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    marker = map.addMarker(new MarkerOptions().position(latLng).title(location));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                    String lat = String.valueOf(marker.getPosition().latitude);
                    String lon = String.valueOf(marker.getPosition().longitude);
                    //Toast.makeText(getApplicationContext(),lat + " , " + lon,Toast.LENGTH_LONG).show();

                    getWeather(lat,lon);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point){
                map.clear();
                marker = map.addMarker(new MarkerOptions().position(point));
                String lat = String.valueOf(marker.getPosition().latitude);
                String lon = String.valueOf(marker.getPosition().longitude);
                //Toast.makeText(getApplicationContext(),lat + " , " + lon,Toast.LENGTH_LONG).show();

                getWeather(lat,lon);
            }
        });
    }

    private void apiCall(RequestParams requestParams) {
        if (isConnected()) {

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            asyncHttpClient.get(WEATHER_URL, requestParams, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    Weather weather = Weather.fromJson(response);
                    if(weather == null){
                        Toast.makeText(getApplicationContext(),"No Data, Try again!",Toast.LENGTH_LONG).show();
                    }
                    else
                    updateWeatherDetails(weather);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);

                    Toast.makeText(getApplicationContext(), "Error occurred while making request!", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(this, "No internet connection! Try to connect to a working internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            return true;
        }

        return false;
    }

    private void updateWeatherDetails(Weather weather) {

        txtTemperature.setText(weather.getTemperature());
        txtWindSpeed.setText(weather.getWind());
        txtHumidity.setText(weather.getHumidity());
        txtPressure.setText(weather.getPressure());
        txtWeatherDescription.setText(weather.getDescription());
        txtLocation.setText(weather.getCity() + ", " + weather.getCountry());

    }

    private void getWeather(String lat, String lon){
        RequestParams requestParams = new RequestParams();

        requestParams.put("lat", lat);
        requestParams.put("lon", lon);
        requestParams.put("appid", API_KEY);

        apiCall(requestParams);
    }
}
