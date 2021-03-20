package com.example.roadaccidentsafetysystem.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadaccidentsafetysystem.Models.ModelEmergencyContact;
import com.example.roadaccidentsafetysystem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterEmergencyContact extends RecyclerView.Adapter<AdapterEmergencyContact.HolderEmergencyContact>{

    private Context context;
    public ArrayList<ModelEmergencyContact> emergencyContactList;

    private ProgressDialog pd;
    FirebaseAuth firebaseAuth;

    public AdapterEmergencyContact(Context context, ArrayList<ModelEmergencyContact> emergencyContactList) {
        this.context = context;
        this.emergencyContactList = emergencyContactList;
    }

    @NonNull
    @Override
    public HolderEmergencyContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_emergency_contact, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new HolderEmergencyContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderEmergencyContact holder, int position) {

        ModelEmergencyContact modelEmergencyContact = emergencyContactList.get(position);
        String name = modelEmergencyContact.getName();
        String phoneNumber = modelEmergencyContact.getContact();
        String timestamp = modelEmergencyContact.getTimeStamp();

        holder.nameTv.setText(name);

        holder.callIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phoneNumber))));
                Toast.makeText(context, "Calling " + name + " now", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener()  {
            @Override
            public boolean onLongClick(View view) {

                LayoutInflater factory = LayoutInflater.from(context);

                final View view2 = factory.inflate(R.layout.dialog_delete_contact, null);

                final TextView header = view2.findViewById(R.id.headerTv);
                final TextView contactNameTv = view2.findViewById(R.id.contactNameTv);
                contactNameTv.setText(name + " , " + phoneNumber);

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setView(view2).setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                deleteContact(timestamp);

                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        });
                alert.show();

                return true;

            }
        });

    }

    private void deleteContact(String timestamp) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("EmergencyContacts").child(timestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                            Toast.makeText(context, "Deleted Contact Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return emergencyContactList.size();
    }

    class HolderEmergencyContact extends RecyclerView.ViewHolder {

        private ImageButton callIb;
        private TextView nameTv;

        public HolderEmergencyContact(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            callIb = itemView.findViewById(R.id.callIb);
        }
    }

}
