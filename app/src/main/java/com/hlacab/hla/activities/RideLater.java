package com.hlacab.hla.activities;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hlacab.hla.R;

import java.util.Calendar;

public class RideLater extends AppCompatActivity {
    PlaceAutocompleteFragment pickup, destination;
    EditText time;
    Button request;
    String request_time;
    LatLng destinationLatLng, pickupLatLng;
    String destination_text, pickup_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_later);

        pickup = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        destination = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_two);

        time = (EditText) findViewById(R.id.time_picker);
        request = (Button) findViewById(R.id.ride_later_request);


        time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(RideLater.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time.setText(hourOfDay + ":" + minute);
                                request_time = hourOfDay + ":" + minute;
                            }
                        }, hour, minute, false);
                timePickerDialog.show();

            }
        });
        pickup.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                destination_text = place.getName().toString();
                pickupLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }


        });


        destination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                pickup_text = place.getName().toString();
                destinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {

            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("rideLaterRequest");

                GeoFire geoFire = new GeoFire(ref);


                if (pickup != null) {
                    geoFire.setLocation(userId, new GeoLocation(pickupLatLng.latitude, pickupLatLng.longitude));
                    ref.child(userId).child("time").setValue(request_time);
                    pickupLatLng = new LatLng(pickupLatLng.latitude, pickupLatLng.longitude);

                    Toast.makeText(getApplicationContext(), "Our Captain will reach you at time", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
