package com.akira.advenceui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnMarkerClickListener {

	private Bundle bundle;
	private Context context;

	private GoogleMap mMap;
	private LocationManager mLocationManager;
	private Location location;
	private String tel;

	public static final int LOCATION_UPDATE_MIN_DISTANCE = 10;
	public static final int LOCATION_UPDATE_MIN_TIME = 5000;

//	Google Map With Marker & User Location
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		context = this;
		
		new Thread(new ProgressRunnable(context)).start();
		// Obtain the SupportMapFragment and get notified when the map is ready
		// to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		bundle = getIntent().getExtras();
		addMyLocationIcon();

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// get bundle info
		String address = (String) bundle.get("address");
		double X = Double.parseDouble((String) bundle.get("X"));
		double Y = Double.parseDouble((String) bundle.get("Y"));
		String title = (String) bundle.get("title");
		tel = (String) bundle.get("tel");

		String msg = Y + " " + X + " " + title + " " + tel;
		Log.i("BBBBBBBB", msg);

		LatLng place = new LatLng(X, Y);
		moveMap(place);
		addMarker(place, title, tel);

		mMap.setMyLocationEnabled(true);
		// mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
		// addMyLocationIcon(latLng);
	}

	private void moveMap(LatLng place) {
		CameraPosition cameraPosition = new CameraPosition.Builder().target(place).zoom(14).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void addMyLocationIcon() {

		int googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (googlePlayStatus != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(googlePlayStatus, this, -1).show();
			finish();
		} else {

			// Obtain the SupportMapFragment and get notified when the map is
			// ready to be used.
			SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map);
			mapFragment.getMapAsync(this);
		}

		getCurrentLocation();

	}

	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				Log.i("Location", location.getLatitude() + " " + location.getLongitude());
				// drawMarker(location);
			} else {
				Log.i("Location", "Location is null");
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
	};

	private void getCurrentLocation() {

		boolean isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetWorkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		location = null;

		if (!(isGPSEnable || isNetWorkEnable)) {
			Toast.makeText(this, "無法獲取位置訊息,請開啟ＧＰＳ定位", Toast.LENGTH_SHORT).show();
		} else {

			if (isNetWorkEnable) {
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME,
						LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
				location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				if (isGPSEnable) {
					mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME,
							LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
					location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}

			}
		}

	}

	private void addMarker(LatLng place, String title, String content) {

		BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.man1);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(place).title(title).snippet(content);
		// .icon(icon);
		mMap.addMarker(markerOptions).showInfoWindow();
		;
		mMap.setOnMarkerClickListener(this);

		// WindowClick
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {

				PhoneCall(tel);
			}
		});
	}

	// MarkerClick
	@Override
	public boolean onMarkerClick(Marker arg0) {
		PhoneCall(tel);
		return false;
	}

	public void PhoneCall(String tel) {
		if (!tel.contains("無電話")) {
			Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel));
			Toast.makeText(context, "撥出電話", Toast.LENGTH_SHORT).show();
			startActivity(call);
		} else {
			Toast.makeText(this, "無電話資訊", Toast.LENGTH_SHORT).show();
		}
	}

}
