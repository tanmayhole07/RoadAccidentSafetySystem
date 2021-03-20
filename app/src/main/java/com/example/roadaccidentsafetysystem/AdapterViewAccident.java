package com.example.roadaccidentsafetysystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdapterViewAccident extends RecyclerView.Adapter<AdapterViewAccident.MyHolder> {

    private Context context;
    public ArrayList<ModelViewAccident> viewAccidentList;

    private ProgressDialog pd;
    FirebaseAuth firebaseAuth;

    public AdapterViewAccident(Context context, ArrayList<ModelViewAccident> viewAccidentList) {
        this.context = context;
        this.viewAccidentList = viewAccidentList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_view_accident, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        ModelViewAccident modelAccident = viewAccidentList.get(position);
        String peopleInjured = modelAccident.getPeopleInjured();
        String description = modelAccident.getAccDescription();
        String city = modelAccident.getCity();
        String subLocal = modelAccident.getSubLocal();
        String pTimestamp = modelAccident.getTimestamp();
        String latitude = modelAccident.getLatitudeAccident();
        String longitude = modelAccident.getLongitudeAccident();
        String postedBy = modelAccident.getAccidentPostedBy();

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm:aa", calendar).toString();

        holder.accDescriptionTv.setText(description);
        holder.peopleInjuredTv.setText(peopleInjured);
        holder.cityTv.setText(city);
        holder.subLocaleTv.setText(subLocal);
        holder.timeTv.setText(pTime);

        holder.accidentMapIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenMap(latitude, longitude);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                LayoutInflater factory = LayoutInflater.from(context);

                final View view2 = factory.inflate(R.layout.edit_accident_info, null);

                final TextView header = view2.findViewById(R.id.headerTv);
                final EditText peopleInjuredEt = (EditText) view2.findViewById(R.id.peopleInjuredEt);
                final EditText accDescriptionEt = (EditText) view2.findViewById(R.id.accDescriptionEt);

                peopleInjuredEt.setText(peopleInjured, EditText.BufferType.EDITABLE);
                accDescriptionEt.setText(description, EditText.BufferType.EDITABLE);

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(view2).setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                String peopleInjured = peopleInjuredEt.getText().toString();
                                String description = accDescriptionEt.getText().toString();
                                /* User clicked OK so do some stuff */

                                inputData(peopleInjured, description, pTimestamp);

                            }
                        }).setNegativeButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                deleteInfo(firebaseAuth.getUid(), pTimestamp);
                            }
                        });
                alert.show();

                return true;
            }
        });


    }


    private void inputData(String peopleInjured, String description, String pTimestamp) {
        if (TextUtils.isEmpty(peopleInjured)) {
            Toast.makeText(context, "Enter No. of people Injured", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(context, "Enter Short Description", Toast.LENGTH_SHORT).show();
            return;
        }

        updateToFirebase(peopleInjured, description, pTimestamp);
    }

    private void updateToFirebase(String peopleInjured, String description, String pTimestamp) {

        pd = new ProgressDialog(context);
        pd.setMessage("Updating Data...");
        pd.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accidents").child(pTimestamp);
        ref.orderByChild("accidentPostedBy").equalTo(firebaseAuth.getUid());

        HashMap<String, Object> result = new HashMap<>();
        result.put("peopleInjured", peopleInjured);
        result.put("accDescription", description);
//
//        ref.child("peopleInjured").setValue(peopleInjured);
//        ref.child("accDescription").setValue(description);

        ref.updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        pd.dismiss();

    }

    private void deleteInfo(String postedBy, String pTimestamp) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Accidents").child(pTimestamp);
        //ref.orderByChild("accidentPostedBy").equalTo(firebaseAuth.getUid())
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                            Toast.makeText(context, "Deleted Info Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Cannot Delete accident posted by other user", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void OpenMap(String latitude, String longitude) {
        String address = "https://maps.google.com/maps?saar=" + latitude + "," + longitude + "&daddr=" + latitude + "," + longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return viewAccidentList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        TextView peopleInjuredTv, accDescriptionTv, cityTv, subLocaleTv, timeTv;
        ImageButton accidentMapIb;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            peopleInjuredTv = itemView.findViewById(R.id.peopleInjuredTv);
            accDescriptionTv = itemView.findViewById(R.id.accDescriptionTv);
            cityTv = itemView.findViewById(R.id.cityTv);
            subLocaleTv = itemView.findViewById(R.id.subLocaleTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            accidentMapIb = itemView.findViewById(R.id.accidentMapIb);

        }
    }
}
