package com.example.roadaccidentsafetysystem.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadaccidentsafetysystem.LoginActivity;
import com.example.roadaccidentsafetysystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAccidentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAccidentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddAccidentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAccidentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAccidentFragment newInstance(String param1, String param2) {
        AddAccidentFragment fragment = new AddAccidentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FirebaseAuth firebaseAuth;
    String mUID = "uid";

    EditText injuredNoAccidentEt, accidentDescriptionEt, dateEt, timeEt, addressEt;
    Button addAccidentBtn, gpsLocationBtn;
    String cityDb;
    String subLocalDb;

    private ProgressDialog pd;
    private TextView userNameTv;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_accident, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        injuredNoAccidentEt = view.findViewById(R.id.injuredNoAccidentEt);
        accidentDescriptionEt = view.findViewById(R.id.accidentDescriptionEt);
        //dateEt = view.findViewById(R.id.dateEt);
        //timeEt = view.findViewById(R.id.timeEt);
        addressEt = view.findViewById(R.id.addressEt);
        addAccidentBtn = view.findViewById(R.id.addAccidentBtn);
        gpsLocationBtn = view.findViewById(R.id.gpsLocationBtn);

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please Wait");
        pd.setCanceledOnTouchOutside(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        addAccidentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });

        gpsLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnnLocation();
            }
        });

        checkUserStatus();

        return view;
    }

    private double latitudeAccident, longitudeAccident;

    private void turnOnnLocation() {
        pd.setMessage("Accessing location");
        pd.show();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocationUser();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getLocationUser() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        latitudeAccident = addresses.get(0).getLatitude();
                        longitudeAccident = addresses.get(0).getLongitude();

                        cityDb = (addresses.get(0).getLocality());
                        subLocalDb = (addresses.get(0).getSubLocality());

                        addressEt.setText(addresses.get(0).getAddressLine(0));

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                pd.dismiss();
                            }
                        }, 2000);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }/////////////////////////////////////////////////////////////////////////////////

    private String peopleInjured, accDescription, city, subLocal, address, timeInMillis;

    private void inputData() {
        peopleInjured = injuredNoAccidentEt.getText().toString();
        accDescription = accidentDescriptionEt.getText().toString();
//        date = dateEt.getText().toString();
//        time = timeEt.getText().toString();
        address = addressEt.getText().toString();

        if (TextUtils.isEmpty(peopleInjured)) {
            Toast.makeText(getActivity(), "Please enter No. of people injured in accident", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(accDescription)) {
            Toast.makeText(getActivity(), "Please enter short description of accident", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (TextUtils.isEmpty(date)) {
//            Toast.makeText(getActivity(), "Please Enter country Name", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (TextUtils.isEmpty(time)) {
//            Toast.makeText(getActivity(), "Please enter locality Name", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (TextUtils.isEmpty(cityDb)) {
            Toast.makeText(getActivity(), "Please enter city Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(subLocalDb)) {
            Toast.makeText(getActivity(), "Please enter city Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(getActivity(), "Please enter address where accident occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        saveToFirebase();
    }

    private void saveToFirebase() {
        pd.setMessage("Adding accident info");
        pd.show();

        // String pTime =
        final String timeStamp = "" + System.currentTimeMillis();

        String pTime = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("peopleInjured", "" + peopleInjured);
        hashMap.put("accDescription", "" + accDescription);
        hashMap.put("city", "" + cityDb);
        hashMap.put("subLocal", "" + subLocalDb);
        hashMap.put("address", "" + address);
        hashMap.put("timestamp", "" + timeStamp + "");
        hashMap.put("latitudeAccident", "" + latitudeAccident);
        hashMap.put("longitudeAccident", "" + longitudeAccident);
        hashMap.put("accidentPostedBy",""+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accidents");
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                pd.dismiss();

                                Fragment newFragment = new ViewAccidentFragment();
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                                //getActivity().finish();
                            }
                        }, 2000);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    /////////////////////////////////////////////////////////////////////////////////


    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();

        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

}