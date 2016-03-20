package com.example.mohamedibrahim.nearbypoints;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SearchPlaces extends AppCompatActivity {

    private SearchPlaces sInstance = this;

    public static final String BASE_URL = "https://api.foursquare.com/v2/venues/search?";
    public static final String ID_PARAM = "client_id=";
    public static final String CLIENT_ID = "ODEH2GFY3M2PVKS3VKM3ODVGFH1VVCACZL2I1BVFAFIIUBRF";
    public static final String SECRET_PARAM = "&client_secret=";
    public static final String CLIENT_SECRET = "X3QYBQ00YTWMU1UTX1BCEOBAYBUPPZEH35J3NNGLEOZ2EGQJ";
    public static final String VERSION_PARAM = "&v=20130815";
    public static final String LOCATION_PARAM = "&ll=";
    public String url;
    double Latitude;
    double Longitude;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);

        //initialization of latitude and longtitude and put it in url to get data in this area
        //and going with value to background task
        LocationListener locationListener = new MyLocationListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
            url = BASE_URL + ID_PARAM + CLIENT_ID + SECRET_PARAM + CLIENT_SECRET + VERSION_PARAM + LOCATION_PARAM + Latitude + "," + Longitude;

            FetchSearchTask SearchTask = new FetchSearchTask();
            SearchTask.execute();
        } else {
            Toast.makeText(this, "Location Not Avalibale", Toast.LENGTH_LONG).show();
        }
    }


    public class FetchSearchTask extends AsyncTask<String, Void, String[]> {

         List<PlaceItem> rowItems = new ArrayList<PlaceItem>();
         ListView listView = (ListView) findViewById(R.id.list);
         ProgressDialog dialog;
         RequestQueue queue = Volley.newRequestQueue(sInstance);

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(SearchPlaces.this);
            dialog.setMessage(getString(R.string.waitingMsg));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
            long delayInMillis = 2000;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, delayInMillis);
        }

        @Override
        protected String[] doInBackground(String... params) {

            try {
                //using volly library to reciving json data
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject json = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));

                                    // These are JSON objects that need to be extracted.
                                    JSONObject JsonResponse = json.getJSONObject("response");
                                    JSONArray venues = JsonResponse.getJSONArray("venues");

                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject place = (JSONObject) venues.get(i);

                                        //name
                                        final String name = place.getString("name");

                                        //location
                                        JSONObject location = place.getJSONObject("location");

                                        JSONArray formattedAddress = location.getJSONArray("formattedAddress");
                                        final String address = formattedAddress.toString();

                                        //icon
                                        JSONArray categories = place.getJSONArray("categories");
                                        JSONObject category = categories.getJSONObject(0);
                                        final JSONObject icon = category.getJSONObject("icon");
                                        String prefix = icon.getString("prefix");
                                        String suffix = icon.getString("suffix");
                                        final String iconURL = prefix + "32" + suffix;

                                        //retrive if selected item or not
                                        SQLiteController controller = new SQLiteController(sInstance);
                                        Cursor c = controller.searchPlace(name.replace("'", " "));
                                        final Boolean isExist;
                                        if (c == null) {
                                            isExist = false;
                                        } else {
                                            isExist = true;
                                        }

                                        ImageRequest request = new ImageRequest(iconURL,
                                                new Response.Listener<Bitmap>() {
                                                    @Override
                                                    public void onResponse(Bitmap bitmap) {
                                                        PlaceItem item = new PlaceItem(bitmap, name, address, isExist, iconURL);
                                                        rowItems.add(item);
                                                        MyAdapter adapter = new MyAdapter(sInstance,
                                                                R.layout.place_list, rowItems);
                                                        listView.setAdapter(adapter);
                                                    }
                                                }, 0, 0, null,
                                                new Response.ErrorListener() {
                                                    public void onErrorResponse(VolleyError error) {
                                                        //mImageView.setImageResource(R.mipmap.ic_launcher);
                                                    }
                                                });

                                        MySingleton.getInstance(sInstance).addToRequestQueue(request);
                                    }
                                } catch (JSONException e) {
                                    //Toast.makeText(sInstance, "Error in reciving Data", Toast.LENGTH_LONG).show();
                                }

                                //selecting itemlist make checkbox mark/unmark and doing thier task
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {
                                        SQLiteController controller = new SQLiteController(sInstance);
                                        CheckBox saveItem = (CheckBox) view.findViewById(R.id.saveItem);
                                        PlaceItem place = (PlaceItem) parent.getItemAtPosition(position);
                                        if (place.isSelected()) {
                                            place.setSelected(false);
                                            saveItem.setChecked(false);
                                            controller.deletePlace(place.getName().replace("'", " "));
                                        } else {
                                            place.setSelected(true);
                                            saveItem.setChecked(true);
                                            HashMap<String, String> values = new HashMap<String, String>();
                                            values.put("PlaceName", place.getName().replace("'", " "));
                                            values.put("PlaceAddress", place.getAddress());
                                            values.put("PlaceIconURL", place.getIconUrl());
                                            controller.insertPlace(values);
                                        }
                                    }
                                });
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(sInstance, "Error in Network", Toast.LENGTH_LONG).show();
                    }
                });
                queue.add(stringRequest);
            } catch (Exception e) {
                Toast.makeText(sInstance, "Error in Reciving Data", Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }

    //class to retrive location in lat/longitude
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Not used for this app
        }
        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                    Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                    Toast.LENGTH_SHORT).show();
        }
    }
}