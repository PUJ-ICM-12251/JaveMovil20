package com.example.javemovil20;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.javemovil20.utils.DirectionsJSONParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.javemovil20.databinding.ActivityLocationBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityLocationBinding binding;

    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    private static final String TAG = ActivityLocationBinding.class.getName();
    private Logger logger = Logger.getLogger(TAG);

    public final static double RADIUS_OF_EARTH_KM = 6371;
    // Constantes de permisos
    private final int LOCATION_PERMISSION_ID = 103;
    String locationPerm = Manifest.permission.ACCESS_FINE_LOCATION;
    // Variables de localización
    private FusedLocationProviderClient mFusedLocationClient;

    //Ubicaciones
    public static final double lowerLeftLatitude = 4.555039;
    public static final double lowerLeftLongitude = -74.268697;
    public static final double upperRightLatitude = 4.800039;
    public static final double upperRightLongitude = -73.862203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Initialize the sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Initialize th listener
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (mMap != null) {
                    if (event.values[0] < 5000) {
                        Log.i("MAPS", "DARK MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(LocationActivity.this, R.raw.style_night));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0]);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(LocationActivity.this, R.raw.style_day));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
        };


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorListener);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Inicializar el FusedLocationProviderClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestPermission(this, locationPerm, "Permiso para utilizar la localización", LOCATION_PERMISSION_ID);
        setCurrentLocationOnMap();
        if(getIntent().getStringExtra("redirectionFrom").equals("DetalleEdificioActivity")){
            Double latitude = Double.parseDouble(getIntent().getStringExtra("Lat"));
            Double longitude = Double.parseDouble(getIntent().getStringExtra("Lon"));
            String buildingName = getIntent().getStringExtra("BuildingName");
            findBuilding(latitude, longitude, buildingName);
        }

    }

    private void requestPermission(Activity context, String permission, String justification, int id) {
        // Verificar si no hay permisos
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, justification, Toast.LENGTH_SHORT).show();
            }
            // Request the permission
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);
        }
    }

    private void setCurrentLocationOnMap () {
        if (ContextCompat.checkSelfPermission(this, locationPerm) != PackageManager.PERMISSION_GRANTED) {
            logger.warning("Failed to getting the location permission :(");
        } else {
            logger.info("Success getting the location permission :)");
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                logger.info("onSuccessLocation");
                if (location != null) {


                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions()
                            .position(loc)
                            .title("Current location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                    // Zoom
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    // Gestures
                    mMap.getUiSettings().setAllGesturesEnabled(true);
                    // Zoom Buttons
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                }

            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_ID) {
            setCurrentLocationOnMap();
        }
    }

    public void refresh (View view) {
        setCurrentLocationOnMap();
    }


    private void findBuilding(Double latitude, Double longitude, String buildingName) {

        LatLng position = new LatLng(latitude, longitude);
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(position)
                    .title(buildingName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            if (ContextCompat.checkSelfPermission(LocationActivity.this, locationPerm) != PackageManager.PERMISSION_GRANTED) {
                logger.warning("Failed to getting the location permission :(");
            } else {
                logger.info("Success getting the location permission :)");
                mFusedLocationClient.getLastLocation().addOnSuccessListener(LocationActivity.this, currentLocation -> {
                    logger.info("onSuccessLocation");
                    if (currentLocation != null) {

                        //Draws the route from current location to found Geocoder direction
                        LatLng origin = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        LatLng dest = position;

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Handler handler = new Handler(Looper.getMainLooper());

                        executor.execute(() -> {
                            String data = "";
                            JSONObject jObject;
                            List<List<HashMap<String, String>>> routes = null;
                            try {
                                data = downloadUrl(url);
                                jObject = new JSONObject(data);
                                DirectionsJSONParser parser = new DirectionsJSONParser();
                                routes = parser.parse(jObject);
                            } catch (Exception e) {
                                Log.d("Background Task", e.toString());
                                e.printStackTrace();
                            }

                            List<List<HashMap<String, String>>> finalRoutes = routes;
                            handler.post(() -> {
                                //UI Thread work here
                                ArrayList points = null;
                                PolylineOptions lineOptions = null;
                                MarkerOptions markerOptions = new MarkerOptions();

                                for (int i = 0; i < finalRoutes.size(); i++) {
                                    points = new ArrayList();
                                    lineOptions = new PolylineOptions();

                                    List<HashMap<String, String>> path = finalRoutes.get(i);

                                    for (int j = 0; j < path.size(); j++) {
                                        HashMap<String, String> point = path.get(j);

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng pos = new LatLng(lat, lng);

                                        points.add(pos);
                                    }

                                    lineOptions.addAll(points);
                                    //lineOptions.width(12);
                                    lineOptions.color(Color.RED);
                                    lineOptions.geodesic(true);
                                }

                                // Drawing polyline in the Google Map for the i-th route
                                mMap.addPolyline(lineOptions);
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                            });
                        });
                    }
                });
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String apiK = "key=" + BuildConfig.MAPS_API_KEY;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + apiK;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

}