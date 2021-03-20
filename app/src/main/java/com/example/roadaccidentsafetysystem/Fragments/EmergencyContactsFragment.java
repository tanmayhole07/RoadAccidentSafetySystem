package com.example.roadaccidentsafetysystem.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roadaccidentsafetysystem.Adapters.AdapterEmergencyContact;
import com.example.roadaccidentsafetysystem.LoginActivity;
import com.example.roadaccidentsafetysystem.Models.ModelEmergencyContact;
import com.example.roadaccidentsafetysystem.Models.ModelViewAccident;
import com.example.roadaccidentsafetysystem.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;

    ProgressDialog pd;

    String mUID = "uid";
    String name, number;

    private final int REQUEST_CODE = 99;

    CardView policeCv, ambulanceCv, fireBrigadeCv, childHelplineCv;

    RelativeLayout tabHelplineRv, tabContactsRv, helpLineBorderRv, contactsBorderRv, emergencyContactRv;
    ScrollView helpLineNumberRv;
    TextView helplinetext, contactstext;

    RecyclerView emergencyRv;

    Button addPersonBtn;

    private ArrayList<ModelEmergencyContact> emergencyContactList;
    AdapterEmergencyContact adapterEmergencyContact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        policeCv = view.findViewById(R.id.policeCv);
        ambulanceCv = view.findViewById(R.id.ambulanceCv);
        fireBrigadeCv = view.findViewById(R.id.fireBrigadeCv);
        childHelplineCv = view.findViewById(R.id.childHelplineCv);

        emergencyContactList = new ArrayList<>();

        tabHelplineRv = view.findViewById(R.id.tabHelplineRv);
        tabContactsRv = view.findViewById(R.id.tabContactsRv);
        helpLineBorderRv = view.findViewById(R.id.helpLineBorderRv);
        contactsBorderRv = view.findViewById(R.id.contactsBorderRv);
        helplinetext = view.findViewById(R.id.helplinetext);
        contactstext = view.findViewById(R.id.contactstext);

        addPersonBtn = view.findViewById(R.id.addPersonBtn);

        helpLineNumberRv = view.findViewById(R.id.helpLineNumberRv);
        emergencyContactRv = view.findViewById(R.id.emergencyContactRv);
        emergencyRv =  view.findViewById(R.id.emergencyRv);

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

        tabHelplineRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelplineUI();
            }
        });

        tabContactsRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactUI();
            }
        });

        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialogAddPerson();
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please wait");
        pd.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        showHelplineUI();


        databaseReference = firebaseDatabase.getReference("Users");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        emergencyRv.setLayoutManager(layoutManager);




        return view;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    private void loadEmergencyContacts() {

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("EmergencyContacts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        emergencyContactList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelEmergencyContact modelEmergencyContact = ds.getValue(ModelEmergencyContact.class);
                            emergencyContactList.add(modelEmergencyContact);
                        }
                        adapterEmergencyContact = new AdapterEmergencyContact(getActivity(), emergencyContactList);
                        emergencyRv.setAdapter(adapterEmergencyContact);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    private void dialogAddPerson() {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_contact, null);
        final TextView nameContactTv = view.findViewById(R.id.nameContactTv);
        final TextView addPersonTv = view.findViewById(R.id.addPersonTv);
        Button addPersonAndContactBtn = view.findViewById(R.id.addPersonAndContactBtn);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(view);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        addPersonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(i, 101);
            }
        });

        nameContactTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameContactTv.setText(name + " , " + number);
            }
        });




        addPersonAndContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogName, dialogNumber;
                dialogName = name;
                dialogNumber = number;

                if (TextUtils.isEmpty(dialogName)) {
                    Toast.makeText(getActivity(), "Please select a contact", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
                addPerson(dialogName, dialogNumber);
            }
        });
    }

    private void addPerson(String dialogName, String dialogNumber) {


        final String timeStamp = "" + System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "" + dialogName);
        hashMap.put("contact", "" + dialogNumber);
        hashMap.put("timeStamp", "" + timeStamp);

        databaseReference.child(user.getUid()).child("EmergencyContacts").child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Added "+dialogName + " " + dialogNumber, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101 && resultCode == Activity.RESULT_OK){
            Uri uri =  data.getData();
            String cols[] = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor rs = getActivity().getContentResolver().query(uri,cols,null,null,null);
            if(rs.moveToFirst()){
                number = (rs.getString(0));
                name = (rs.getString(1));
            }
        }

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private void showContactUI() {

        helpLineBorderRv.setVisibility(View.INVISIBLE);
        contactsBorderRv.setVisibility(View.VISIBLE);

        emergencyContactRv.setVisibility(View.VISIBLE);
        helpLineNumberRv.setVisibility(View.GONE);

        checkUserStatus();

        helplinetext.setTextColor(getResources().getColor(R.color.white));
        //tabOrderDetailsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        contactstext.setTextColor(getResources().getColor(R.color.primaryTextColor));
        //tabOrderedProductsTv.setBackgroundResource(R.drawable.shape_rect04);

    }

    private void showHelplineUI() {

        contactsBorderRv.setVisibility(View.INVISIBLE);
        helpLineBorderRv.setVisibility(View.VISIBLE);

        emergencyContactRv.setVisibility(View.GONE);
        helpLineNumberRv.setVisibility(View.VISIBLE);

        contactstext.setTextColor(getResources().getColor(R.color.white));
        //tabOrderDetailsTv.setBackgroundResource(R.drawable.shape_rect04);

        helplinetext.setTextColor(getResources().getColor(R.color.primaryTextColor));
        //tabOrderedProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    private void callNow(String phoneNumber) {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber))));
        Toast.makeText(getActivity(), "Calling " + phoneNumber + " now", Toast.LENGTH_SHORT).show();

    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            mUID = user.getUid();
            loadEmergencyContacts();

        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

}