
package com.example.inviatoserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.inviatoserver.Common.Common;
import com.example.inviatoserver.Common.DirectionJSONParser;
import com.example.inviatoserver.Remote.IGeoCoordenates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int LOCATION_PERMISSION_REQUEST = 1001;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiCliente;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 1000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private IGeoCoordenates mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_oder);

        mService = Common.getGeoCodeService();

        Toast.makeText(getApplicationContext(), Common.currentSolicitudModel.getAddress(), Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RequestRuntimePermission();
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }//if
        }//else

        displayLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }//onCreate

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }//createLocationRequest

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RequestRuntimePermission();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCliente);
            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                //Agregar marcador y movimiento en camara
                LatLng yourLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(yourLocation).title("Mi ubicacion"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

                drawRoute(yourLocation, Common.currentSolicitudModel.getAddress());
                //Toast.makeText(this, Common.currentSolicitudModel.getAddress() + "  " + yourLocation, Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this,"¡No podemos obtener tu localizacion!",Toast.LENGTH_SHORT).show();
            }//else
        }//else
    }//displayLocation

    private void drawRoute(final LatLng yourLocation, String address) {
        mService.getGeoCode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());

                    String lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lat").toString();

                    String lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    //Toast.makeText(this, Common.currentSolicitudModel.getAddress() + "  " + yourLocation, Toast.LENGTH_SHORT).show();

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cesta);
                    bitmap = Common.scaleBitMap(bitmap, 70, 70);

                    MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Orden: " + Common.currentSolicitudModel.getPhone())
                            .position(orderLocation);
                    mMap.addMarker(markerOptions);

                    //Dibujar ruta

                    mService.getDirections(yourLocation.latitude + "," + yourLocation.longitude,
                            orderLocation.latitude + "," + orderLocation.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    new ParseTask().execute(response.body().toString());
                                }//onResponse

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }//onFailure
                            });
                } catch (JSONException e) {
                }//catch

            }//onResponse

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }//onFailure
        });
    }//drawRoute

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "¡Error al otorgar permisos al dispositivo! ", Toast.LENGTH_SHORT).show();
                finish();
            }//else
            return false;
        }//if
        return true;
    }//checkPlayServices

    private void RequestRuntimePermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_PERMISSION_REQUEST);
    }//RequestRuntimePermission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }//if
                }//if
                break;
        }//switch
    }//onRequestPermissionsResult

    private void createLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }//createLocation

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiCliente = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiCliente.connect();
    }//buildGoogleApiClient

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }//onMapReady

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }//onLocationChanged

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }//onConnected

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//if
        //Pendiente
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCliente, mLocationRequest, this);
    }//startLocationUpdates

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiCliente.connect();
    }//onConnectionSuspended

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }//onConnectionFailed

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }//onResume

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiCliente != null) {
            mGoogleApiCliente.connect();
        }//if
    }//onStart

    private class ParseTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog progressDialog = new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Espere un momento ...");
            progressDialog.show();
        }//onPreExecute

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }//catch
            return routes;
        }//doInBackground

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            progressDialog.dismiss();
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = lists.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }//for
                polylineOptions.addAll(points);
                polylineOptions.width(12);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }//for
            mMap.addPolyline(polylineOptions);
        }//onPostExecute
    }//ParseTask

}//FragmentActivity