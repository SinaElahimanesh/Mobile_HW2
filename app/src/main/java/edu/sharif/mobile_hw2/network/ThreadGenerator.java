package edu.sharif.mobile_hw2.network;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.sharif.mobile_hw2.search.ListViewAdapter;
import edu.sharif.mobile_hw2.R;
import edu.sharif.mobile_hw2.search.SearchPlaces;
import okhttp3.Response;


public class ThreadGenerator {

    public static Thread getPlaces(Activity activity, String places, ListViewAdapter adapter, Handler handler) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("ENTER", "getCoinDetail");
                Response response = Requester.getInstance().RequestPlaces(places);
                System.out.println(response);
                try {
                    if (response == null) {
                        return;
                    }
                    String placesString = response.body().string();
                    Log.d("placesString", "" + placesString);

                    JSONArray jsonArray = new JSONArray(placesString);
                    ArrayList<SearchPlaces> placesArray = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject placeObject = jsonArray.getJSONObject(i);
                        String displayName = placeObject.getString("display_name");
                        JSONObject address = placeObject.getJSONObject("address");
                        String country = "Iran";
                        if (address.has("country")) {
                            country = address.getString("country");
                        }
                        double longitude = placeObject.getDouble("lon");
                        double latitude = placeObject.getDouble("lat");

                        String city = "";//address.getString("city");
                        String neighbourhood = "neighbourhood";//address.getString("neighbourhood");

                        // use keys() iterator, you don't need to know what keys are there/missing
                        Iterator<String> iter = address.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            String lightObject = address.getString(key);
                            System.out.println("key: " + key + ", OBJECT " + lightObject);
                            neighbourhood = lightObject;
                            break;

                        }


                        if (country.contains("ایران") || country.contains("Iran") || country.contains("IR")) {
                            placesArray.add(new SearchPlaces(neighbourhood, city, displayName, latitude, longitude));
                        }
                    }


                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            mMap.addPolyline(options);
//
//                            LatLngBounds bounds = builder.build();
//                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//                            mMap.animateCamera(cu);
                            adapter.setArraylist(placesArray);
                            adapter.filter();

                            ProgressBar progressBar = activity.findViewById(R.id.progressBar);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                    Message message = new Message();
                    message.what = MessageResult.SUCCESSFUL;
                    message.obj = placesArray;
                    handler.sendMessage(message);


                } catch (JSONException | IOException e) {
                    Message message = new Message();
                    message.what = MessageResult.FAILED;
                    handler.sendMessage(message);
                    e.printStackTrace();
                }
            }
        });
    }


}
