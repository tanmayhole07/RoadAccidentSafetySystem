package com.example.roadaccidentsafetysystem.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roadaccidentsafetysystem.Models.ModelEmergencyContact;
import com.example.roadaccidentsafetysystem.R;

import java.util.ArrayList;

public class AdapterEmergencyContact extends RecyclerView.Adapter<AdapterEmergencyContact.HolderEmergencyContact>{

    private Context context;
    public ArrayList<ModelEmergencyContact> emergencyContactList;

    public AdapterEmergencyContact(Context context, ArrayList<ModelEmergencyContact> emergencyContactList) {
        this.context = context;
        this.emergencyContactList = emergencyContactList;
    }

    @NonNull
    @Override
    public HolderEmergencyContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_emergency_contact, parent, false);

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
