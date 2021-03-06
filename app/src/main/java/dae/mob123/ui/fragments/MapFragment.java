package dae.mob123.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import java.util.List;

import dae.mob123.R;
import dae.mob123.ui.util.LocationConverter;
import dae.mob123.model.Mural;
import dae.mob123.model.MuralViewModel;
import dae.mob123.model.util.MuralType;

/* Authors: AB & DG */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap myMap;
    private FragmentActivity myContext;
    private final LatLng COORD_BXL = new LatLng(50.8503463, 4.3517211);
    private Location currentLocation;
    private LatLng currentLocationCoordinates;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private Bundle dataFromDetail;
    private Mural muralFromDetail;
//    private ArrayList<LatLng> listLocations = new ArrayList<>();
//    private final String API_KEY = "AIzaSyCbqIu4_8D21F4XI4X_O3uH8gPSYVzuK4A";

    private GoogleMap.OnInfoWindowClickListener infoWindowListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            Mural mural = (Mural) marker.getTag();
            if (mural != null) {
                Bundle data = new Bundle();
                data.putSerializable("mural_to_detail", mural);
                Navigation.findNavController(mapView).navigate(R.id.detail_fragment, data);
            }
        }
    };

    public MapFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = (FragmentActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        dataFromDetail = getArguments();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(myContext);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        /*Button to reset map after orientation changed*/
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        drawUserLocationMarker();
        setMarkerAdapter();
        drawMuralMarkers();
        myMap.setOnInfoWindowClickListener(infoWindowListener);
        /*Button to center map on user location*/
        myMap.setMyLocationEnabled(true);

        if (dataFromDetail.getSerializable("mural_to_map_zoom") != null) {
            muralFromDetail = (Mural) dataFromDetail.getSerializable("mural_to_map_zoom");
            CameraUpdate moveToMural = CameraUpdateFactory.newLatLngZoom(muralFromDetail.getCoordinates(), 19);
            myMap.animateCamera((moveToMural));
//        } else if (dataFromDetail.getSerializable("mural_to_map_route") != null) {
//            muralFromDetail = (Mural) dataFromDetail.getSerializable("mural_to_map_route");
//            LatLng muralDestinationCoordinates = muralFromDetail.getCoordinates();
//            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
//            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if (location != null) {
//                        currentLocation = location;
//                        currentLocationCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//                        listLocations.add(currentLocationCoordinates);
//                        listLocations.add(muralDestinationCoordinates);
//                        if (listLocations.size() == 2) {
//                            String url = getRequestUrl(listLocations.get(0), listLocations.get(1));
//                            Log.d("DIRECTIONS REQUEST", url);
//                        }
//                    }
//                }
//            });
        } else {
            CameraUpdate moveToBXL = CameraUpdateFactory.newLatLngZoom(COORD_BXL, 15);
            myMap.animateCamera(moveToBXL);
        }
    }

//    private String getRequestUrl(LatLng origin, LatLng destination) {
//        String output = "json";
//        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
//        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
//        String strMode = "mode=walking";
//        String strParam = strOrigin + "&" + strDestination + "&" + strMode + "&key=";
//
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + strParam  + API_KEY;
//        return url;
//    }
//
//    private String requestDirections(String requestUrl) throws IOException {
//        String strResponse = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//        try {
//            URL url = new URL(requestUrl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            strResponse = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//            httpURLConnection.disconnect();
//        }
//        return strResponse;
//    }

    private void setMarkerAdapter() {
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View cardView = getActivity().getLayoutInflater().inflate(R.layout.mural_marker_card, null, false);
                TextView titleTV = cardView.findViewById(R.id.tv_mural_marker_card_title);
                TextView addressTV = cardView.findViewById(R.id.tv_mural_marker_card_snippet);
                titleTV.setText(marker.getTitle());
                addressTV.setText(marker.getSnippet());
                return cardView;
            }
        });
    }

    private void drawMuralMarkers() {
        MuralViewModel muralViewModel = new ViewModelProvider(myContext).get(MuralViewModel.class);
        muralViewModel.getMurals().observe(myContext, new Observer<List<Mural>>() {
            @Override
            public void onChanged(List<Mural> murals) {
                for (Mural mural : murals) {
                    Marker marker = myMap.addMarker(new MarkerOptions()
                            .position(mural.getCoordinates()));
                    if (mural.getMuralType() == MuralType.COMIC_BOOK) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_comicbook));
                    } else {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker_streetart));
                    }
                    marker.setTitle(mural.getCharacterTitle());
                    LocationConverter myConverter = new LocationConverter();
                    marker.setSnippet(myConverter.convertCoordinatesToAddress(myContext, mural.getCoordinates()));
                    marker.setTag(mural);
                }
            }
        });
    }

    private void drawUserLocationMarker() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(myContext, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    currentLocationCoordinates = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    Marker markerUserLocation = myMap.addMarker(new MarkerOptions()
                            .position(currentLocationCoordinates));
                    markerUserLocation.hideInfoWindow();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    drawUserLocationMarker();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
//    public class TaskRequestDirections extends AsyncTask<String, Void, String>{
//        @Override
//        protected String doInBackground(String... strings) {
//            String strResponse = "";
//            try {
//                strResponse = requestDirections(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return strResponse;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//        }
//    }

//    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>>{
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject = null;
//            List<List<HashMap<String, String>>> routes = null;
//
//        }
//    }
}