package com.example.root.projet_velov;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] drawerItemsList;
    private ListView myDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        drawerItemsList = new String[]{"Afficher Stations"};
        myDrawer = (ListView) findViewById(R.id.my_drawerMaps);
        ArrayList<String> elemDrawer = new ArrayList<>();
        myDrawer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemsList));
        myDrawer.setOnItemClickListener(new DrawerItemClickListener());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Connexion con = new Connexion();
        ArrayList<Station> StationList = new ArrayList<>();
        try {
            StationList = con.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < StationList.size(); i++){
            LatLng pos = new LatLng(StationList.get(i).getLat(), StationList.get(i).getLng());
            MarkerOptions mark = new MarkerOptions().position(pos).title(StationList.get(i).getName());
            mark.snippet("Velo dispo : " + String.valueOf(StationList.get(i).getAvailable_bike()));
            mMap.addMarker(mark);

        }
        final ArrayList<Station> finalStationList = StationList;
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String mark = marker.getTitle();
                Intent evt = new Intent(MapsActivity.this, InfoStation.class);
                evt.putExtra("mark", mark);
                evt.putExtra("StationList", finalStationList);
                startActivity(evt);
            }
        });
        LatLng lyon = new LatLng(45.750000, 4.850000);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lyon));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12.5f));
    }
}
