//package lofy.fpt.edu.vn.lofy_ver108.Modules;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Mai Thanh Hiep on 4/3/2016.
// */
//public class DirectionFinder {
//    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
//    private static final String GOOGLE_API_KEY = "AIzaSyDnwLF2-WfK8cVZt9OoDYJ9Y8kspXhEHfI";
//    private DirectionFinderListener listener;
//    private String origin;
//    private String rest;
//    private String destination;
//
//    public DirectionFinder(DirectionFinderListener listener, String origin, String rest, String destination) {
//        this.listener = listener;
//        this.origin = origin;
//        this.rest = rest;
//        this.destination = destination;
//    }
//
//    public void execute() throws UnsupportedEncodingException {
//        listener.onDirectionFinderStart();
//        if (rest == null) {
//            new DownloadRawData().execute(createUrl());
//        } else {
//            new DownloadRawData().execute(createUrlRest());
//            new DownloadRawData().execute(createUrlRestToDes());
//        }
//    }
//
//    private String createUrl() throws UnsupportedEncodingException {
//        String urlOrigin = URLEncoder.encode(origin, "utf-8");
//        String urlDestination = URLEncoder.encode(destination, "utf-8");
//        String direction = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn";
//        Log.d("direction", direction);
//        return direction;
//    }
//    private String createUrlRest() throws UnsupportedEncodingException {
//        String urlOrigin = URLEncoder.encode(origin, "utf-8");
//        String urlRest = URLEncoder.encode(rest, "utf-8");
//        String direction = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlRest + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn";
//        Log.d("toRest", direction);
//        return direction;
//    }
//
//    private String createUrlRestToDes() throws UnsupportedEncodingException {
//        String urlRest = URLEncoder.encode(rest, "utf-8");
//        String urlDestination = URLEncoder.encode(destination, "utf-8");
//        String direction = DIRECTION_URL_API + "origin=" + urlRest + "&destination=" + urlDestination + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn";
//        Log.d("toDes", direction);
//        return direction;
//    }
//
//    private class DownloadRawData extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            String link = params[0];
//            try {
//                URL url = new URL(link);
//                InputStream is = url.openConnection().getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line + "\n");
//                }
//
//                return buffer.toString();
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String res) {
//            try {
//                parseJSon(res);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void parseJSon(String data) throws JSONException {
//        if (data == null)
//            return;
//
//        List<Route> routes = new ArrayList<Route>();
//        JSONObject jsonData = new JSONObject(data);
//        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
//        for (int i = 0; i < jsonRoutes.length(); i++) {
//            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
//            Route route = new Route();
//
//            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
//            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
//            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
//            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
//            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
//            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
//            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
//
//            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
//            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
//            route.endAddress = jsonLeg.getString("end_address");
//            route.startAddress = jsonLeg.getString("start_address");
//            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
//            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
//            route.points = decodePolyLine(overview_polylineJson.getString("points"));
//
//            routes.add(route);
//        }
//
//        listener.onDirectionFinderSuccess(routes);
//    }
//
//
//    private List<LatLng> decodePolyLine(final String poly) {
//        int len = poly.length();
//        int index = 0;
//        List<LatLng> decoded = new ArrayList<LatLng>();
//        int lat = 0;
//        int lng = 0;
//
//        while (index < len) {
//            int b;
//            int shift = 0;
//            int result = 0;
//            do {
//                b = poly.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do {
//                b = poly.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            decoded.add(new LatLng(
//                    lat / 100000d, lng / 100000d
//            ));
//        }
//
//        return decoded;
//    }
//}
package lofy.fpt.edu.vn.lofy_ver108.Modules;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lofy.fpt.edu.vn.lofy_ver108.entity.Distance;
import lofy.fpt.edu.vn.lofy_ver108.entity.Duration;
import lofy.fpt.edu.vn.lofy_ver108.entity.Route;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBEIiySPSYD4Y0E0mGqlpYvErj99oj77fE";
    private DirectionFinderListener listener;
    private String origin;
    private String rest;
    private String points;
    private List<Marker> tenMarkers = new ArrayList<>();
    private String destination;

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

    public DirectionFinder(DirectionFinderListener listener, String origin, String rest, List<Marker> tenMarkers, String destination) {
        this.listener = listener;
        this.origin = origin;
        this.rest = rest;
        this.tenMarkers = tenMarkers;
        this.destination = destination;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        if (tenMarkers == null) {
            new DownloadRawData().execute(createUrl());
        } else if (tenMarkers.size() > 1) {
            rest = tenMarkers.get(0).getPosition().latitude + "," + tenMarkers.get(0).getPosition().longitude;
            for (int i = 1; i < tenMarkers.size(); i++) {
                new DownloadRawData().execute(createUrlRest(origin, rest));
                origin = rest;
                rest = tenMarkers.get(i).getPosition().latitude + "," + tenMarkers.get(i).getPosition().longitude;
            }
            new DownloadRawData().execute(createUrlRest(origin, rest));
            new DownloadRawData().execute(createUrlDestination(rest, destination));
        } else {
            new DownloadRawData().execute(createUrlRest(origin, rest));
            new DownloadRawData().execute(createUrlDestination(rest, destination));
        }
    }

    private String createUrl() throws UnsupportedEncodingException {
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        String direction = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn";
        Log.d("direction", direction);
        return direction;
    }

    private String createUrlRest(String origin, String rest) throws UnsupportedEncodingException {
        this.origin = origin;
        this.rest = rest;
        String urlOrigin = URLEncoder.encode(origin, "utf-8");
        String urlRest = URLEncoder.encode(rest, "utf-8");
        String direction = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlRest + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn";
        Log.d("direction", direction);
        return direction;
    }

    private String createUrlDestination(String rest, String destination) throws UnsupportedEncodingException {
        this.rest = rest;
        this.destination = destination;
        String urlRest = URLEncoder.encode(rest, "utf-8");
        String urlDestination = URLEncoder.encode(destination, "utf-8");
        String direction = DIRECTION_URL_API + "origin=" + urlRest + "&destination=" + urlDestination + "&sensor=false&void=highways&alternatives=true&mode=driving&region=vn&keyapi=AIzaSyBEIiySPSYD4Y0E0mGqlpYvErj99oj77fE";
        Log.d("direction", direction);
        return direction;
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
            route.endAddress = jsonLeg.getString("end_address");
            route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }


    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
