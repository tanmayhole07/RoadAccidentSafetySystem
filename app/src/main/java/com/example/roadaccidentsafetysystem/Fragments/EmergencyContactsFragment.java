package com.example.roadaccidentsafetysystem.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.roadaccidentsafetysystem.LoginActivity;
import com.example.roadaccidentsafetysystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmergencyContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmergencyContactsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EmergencyContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EmergencyContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EmergencyContactsFragment newInstance(String param1, String param2) {
        EmergencyContactsFragment fragment = new EmergencyContactsFragment();
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

    CardView policeCv, ambulanceCv, fireBrigadeCv, childHelplineCv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        policeCv = view.findViewById(R.id.policeCv);
        ambulanceCv = view.findViewById(R.id.ambulanceCv);
        fireBrigadeCv = view.findViewById(R.id.fireBrigadeCv);
        childHelplineCv = view.findViewById(R.id.childHelplineCv);

        policeCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "100";
                callNow(phoneNumber);

            }
        });

        ambulanceCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "102";
                callNow(phoneNumber);

            }
        });

        fireBrigadeCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "101";
                callNow(phoneNumber);

            }
        });

        childHelplineCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "1098";
                callNow(phoneNumber);

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();


        return view;
    }

    private void callNow(String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber))));
        Toast.makeText(getActivity(), "Calling " + phoneNumber + " now", Toast.LENGTH_SHORT).show();

    }

}