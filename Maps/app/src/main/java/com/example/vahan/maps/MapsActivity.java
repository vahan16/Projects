package com.example.vahan.maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONException;
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
import java.util.Random;

import static com.example.vahan.maps.R.id.map;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback {

    Button taxi,getRoutes,Details;
    LatLng stateLocation;
    JSONObject name,objectLoc;
    static final Integer LOCATION = 0x1;
    PolylineOptions lineOptions;
    ArrayList<LatLng> points,position;
    Polyline polyline;
    List<Polyline> polylines;
    private GoogleMap mMap;
    GoogleApiClient client;
    LatLng ltlg;
    DB db;
    Circle mCircle;
    double radiusInMeters =0;
    String radio , checked;
    List<Marker> markers;
    Bitmap CafeMarker,MuseumMarker,PharmacyMarker,TheatreMarker,CinemaMarker,CheckedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        db = new DB(this);
        db.open();

        taxi=(Button)findViewById(R.id.button6);
        getRoutes = (Button)findViewById(R.id.button7);
        Details = (Button)findViewById(R.id.button8);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }
        LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener mlocListener = new MyLocationListener();
        try {
            mlocManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mlocListener, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        position = new ArrayList<>();

        getRoutes.setVisibility(View.GONE);
        taxi.setVisibility(View.GONE);
        Details.setVisibility(View.GONE);



        Intent intent = getIntent();
       checked = intent.getStringExtra("checked");
        radio = intent.getStringExtra("radio");

        int height = 60;
        int width = 60;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.rest);
        Bitmap b=bitmapdraw.getBitmap();
        CafeMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDrawable bitmapdraw1=(BitmapDrawable)getResources().getDrawable(R.drawable.museum);
        Bitmap b1=bitmapdraw1.getBitmap();
        MuseumMarker = Bitmap.createScaledBitmap(b1, width, height, false);
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.theatre);
        Bitmap b2=bitmapdraw2.getBitmap();
        TheatreMarker = Bitmap.createScaledBitmap(b2, width, height, false);
        BitmapDrawable bitmapdraw3=(BitmapDrawable)getResources().getDrawable(R.drawable.cinema);
        Bitmap b3=bitmapdraw3.getBitmap();
        CinemaMarker = Bitmap.createScaledBitmap(b3, width, height, false);
        BitmapDrawable bitmapdraw4=(BitmapDrawable)getResources().getDrawable(R.drawable.pharmacy);
        Bitmap b4=bitmapdraw4.getBitmap();
        PharmacyMarker = Bitmap.createScaledBitmap(b4, width, height, false);
        BitmapDrawable bitmapdraw5=(BitmapDrawable)getResources().getDrawable(R.drawable.checked);
        Bitmap b5=bitmapdraw5.getBitmap();
        CheckedMarker = Bitmap.createScaledBitmap(b5, 30, 30, false);

        markers= new ArrayList<>();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                if (lastOpened != null) {
//                    lastOpened.hideInfoWindow();
//                    if (lastOpened.equals(marker)) {
//                        lastOpened = null;
//                        return true;
//                    }
//                }
//                marker.showInfoWindow();
//                lastOpened = marker; //not to animate to marker

                getRoutes.setVisibility(View.VISIBLE);
                Details.setVisibility(View.VISIBLE);
                taxi.setVisibility(View.GONE);

                position.add(0,ltlg);
                position.add(marker.getPosition());
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(CheckedMarker)));


                if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                    taxi.setVisibility(View.VISIBLE);

                    String origin = null;
                    String destination = null;
                    if (ltlg != null && marker.getPosition() != null) {
                        origin = ltlg.latitude + "," + ltlg.longitude;
                        destination = marker.getPosition().latitude + ","
                                + marker.getPosition().longitude;
                    }
                    String url="https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
                            origin+
                            "&destinations=" +
                            destination+
                            "&mode=driving&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";
                    RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                    JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject result) {
                                    try {
                                        JSONObject objectLoc1 = result.getJSONArray("rows").getJSONObject(0)
                                                .getJSONArray("elements").getJSONObject(0);
                                        JSONObject distOb = objectLoc1.getJSONObject("distance");
//                                        d+=distOb.getInt("value");
                                        JSONObject timeOb = objectLoc1.getJSONObject("duration");
                                        Details.setText("Details: "+distOb.getString("text")+
                                                " km , "+timeOb.getString("text"));
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    queue.add(stateReq);


                    getRoutes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < position.size() - 1; i++) {
                                if(i!=i+1) {
                                    String url = getDirectionsUrl(position.get(i), position.get(i + 1));
                                    DownloadTask downloadTask = new DownloadTask();
                                    downloadTask.execute(url);
                                }
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(getBaseContext(),
                            "No Internet connection available", Toast.LENGTH_LONG).show();
                }
                taxi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/search?q=taxi%20yerevan")));
                    }
                });
                return true;
            }
        });



        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                taxi.setVisibility(View.GONE);
//                try {
//                    for (Polyline line : polylines) {
//                        line.remove();
//                        polylines.clear();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } //remove polylines

            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, ""+permission+" is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
            return "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line ;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    private void drawMarkerWithCircle(LatLng position) {
        int strokeColor = 0x3b2189f1;
        int shadeColor = 0x1f2189f1;
        switch (radio) {
            case "1 km":
                radiusInMeters = 1000;
                break;
            case "2 km":
                radiusInMeters = 2000;
                break;
            case "5 km":
                radiusInMeters = 5000;
                break;
            case "10 km":
                radiusInMeters = 10000;
                break;
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(position)
                .radius(radiusInMeters)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(5);

                try {
            mCircle = mMap.addCircle(circleOptions);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ThematicSubmenu.class);
        startActivity(intent);
        finish();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                if (AppStatus.getInstance(getApplicationContext())
                        .isOnline(getApplicationContext())) {
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();
                    routes = parser.parse(jObject);
                }
                else
                {
                    Toast.makeText(getBaseContext(),
                            "No Internet connection available", Toast.LENGTH_LONG).show();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            String[] arrayColors={"#f44336","#9C27B0","#3F51B5","#2196F3",
                    "#009688","#4CAF50","#FFEB3B","#FF9800","#607D8B"};

            lineOptions = null;
            polylines = new ArrayList<>();
            for(int i=0;i<result.size();i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(15);
                for(int i1 = 0 ;i1<arrayColors.length;i1++) {
                    for (int j1 = 0; j1 < i1; j1++) {
                        if (!(arrayColors[i1].equals(arrayColors[j1]))) {
                            lineOptions.color(Color.parseColor(
                                    arrayColors[new Random().nextInt(arrayColors.length)]));
                        }
                    }
                }
                try {
                    polyline = mMap.addPolyline(lineOptions);
                    polylines.add(polyline);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            ltlg = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ltlg,14));
            try {
                drawMarkerWithCircle(ltlg);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if (checked.contains("Theatres")) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    ltlg.latitude+"%2C"+ltlg.longitude+"&radius="+ (radiusInMeters-150)+"&type="+
                    "&keyword="+"Theatre"+"&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";
            RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
            JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                objectLoc = response.getJSONArray("results").getJSONObject(i)
                                        .getJSONObject("geometry").getJSONObject("location");
                                    name = response.getJSONArray("results").getJSONObject(i);
                                    stateLocation = new LatLng(objectLoc.getDouble("lat"),
                                            objectLoc.getDouble("lng"));

                                    markers.add(mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(stateLocation.latitude, stateLocation.longitude))
                                                .icon(BitmapDescriptorFactory.fromBitmap(TheatreMarker))
                                            .title(name.getString("name"))));
                                    for (Marker marker : markers) {
                                        if (SphericalUtil.computeDistanceBetween(ltlg,
                                                marker.getPosition()) > radiusInMeters) {
                                            marker.setVisible(false);
                                        }
                                    }
                                    }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(stateReq);
        }

            if (checked.contains("Museums")) {
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                    ltlg.latitude+"%2C"+ltlg.longitude+"&radius="+ (radiusInMeters-150)+"&type="+"museum"+
                    "&keyword="+"&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";

            RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
            JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                    objectLoc = response.getJSONArray("results").getJSONObject(i)
                                            .getJSONObject("geometry").getJSONObject("location");
                                    name = response.getJSONArray("results").getJSONObject(i);
                                    stateLocation = new LatLng(objectLoc.getDouble("lat"),
                                            objectLoc.getDouble("lng"));
                                    markers.add(mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(stateLocation.latitude, stateLocation.longitude))
                                                .icon(BitmapDescriptorFactory.fromBitmap(MuseumMarker))
                                            .title(name.getString("name"))));
                                    for (Marker marker : markers) {
                                        if (SphericalUtil.computeDistanceBetween(ltlg,
                                                marker.getPosition()) > radiusInMeters) {
                                            marker.setVisible(false);
                                        }
                                    }

                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            queue.add(stateReq);
        }

            if (checked.contains("Cafeterias")) {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                        ltlg.latitude+"%2C"+ltlg.longitude+"&radius="+ (radiusInMeters-150)+"&type="+"cafe"+
                        "&keyword="+"&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                        objectLoc = response.getJSONArray("results").getJSONObject(i)
                                                .getJSONObject("geometry").getJSONObject("location");
                                        name = response.getJSONArray("results").getJSONObject(i);
                                        stateLocation = new LatLng(objectLoc.getDouble("lat"),
                                                objectLoc.getDouble("lng"));
                                        markers.add(mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(stateLocation.latitude, stateLocation.longitude))
                                                .icon(BitmapDescriptorFactory.fromBitmap(CafeMarker))
                                                .title(name.getString("name"))));
                                        for (Marker marker : markers) {
                                            if (SphericalUtil.computeDistanceBetween(ltlg,
                                                    marker.getPosition()) > radiusInMeters) {
                                                marker.setVisible(false);
                                            }
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stateReq);
            }

            if (checked.contains("Pharmacies")) {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                        ltlg.latitude+"%2C"+ltlg.longitude+"&radius="+ (radiusInMeters-150)+"&type="+"pharmacy"+
                        "&keyword="+"&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                        objectLoc = response.getJSONArray("results").getJSONObject(i)
                                                .getJSONObject("geometry").getJSONObject("location");
                                        name = response.getJSONArray("results").getJSONObject(i);
                                        stateLocation = new LatLng(objectLoc.getDouble("lat"),
                                                objectLoc.getDouble("lng"));

                                        markers.add(mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(stateLocation.latitude, stateLocation.longitude))
                                                .icon(BitmapDescriptorFactory.fromBitmap(PharmacyMarker))
                                                .title(name.getString("name"))));
                                        for (Marker marker : markers) {
                                            if (SphericalUtil.computeDistanceBetween(ltlg,
                                                    marker.getPosition()) > radiusInMeters) {
                                                marker.setVisible(false);
                                            }
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stateReq);
            }

            if (checked.contains("Cinemas")) {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                        ltlg.latitude+"%2C"+ltlg.longitude+"&radius="+ (radiusInMeters-150)+"&type="+"movie_theater"+
                        "&keyword="+"cinema"+"&key=AIzaSyAFjsbOViOoJg6LPFlKxOSXAOFPTVYEamE";
                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    for (int i = 0; i < response.getJSONArray("results").length(); i++) {
                                        objectLoc = response.getJSONArray("results").getJSONObject(i)
                                                .getJSONObject("geometry").getJSONObject("location");
                                        name = response.getJSONArray("results").getJSONObject(i);
                                        stateLocation = new LatLng(objectLoc.getDouble("lat"),
                                                objectLoc.getDouble("lng"));

                                        markers.add(mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(stateLocation.latitude, stateLocation.longitude))
                                                .icon(BitmapDescriptorFactory.fromBitmap(CinemaMarker))
                                                .title(name.getString("name"))));
                                        for (Marker marker : markers) {
                                            if (SphericalUtil.computeDistanceBetween(ltlg,
                                                    marker.getPosition()) > radiusInMeters) {
                                                marker.setVisible(false);
                                            }
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                queue.add(stateReq);
            }


        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}