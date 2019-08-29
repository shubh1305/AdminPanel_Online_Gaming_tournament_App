package com.example.adminpanel;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.MyViewHolder> implements Filterable {

    private Context context;
    private List<Participant> participants;
    private List<Participant> participantsFiltered;
    private ParticipantAdapterListner listener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    public ParticipantAdapter(Context context, List<Participant> participants, ParticipantAdapterListner listener) {
        this.context = context;
        this.participants = participants;
        this.participantsFiltered = participants;
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    participantsFiltered = participants;
                } else {
                    List<Participant> filteredList = new ArrayList<>();
                    for (Participant row : participants) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getPubgUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    participantsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = participantsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                participantsFiltered = (ArrayList<Participant>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_username;
        ImageView iv_pic, iv_more;
        RelativeLayout rl;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.participants_list_name);
            tv_username = itemView.findViewById(R.id.participants_list_pubg_username);
            iv_pic = itemView.findViewById(R.id.participants_list_pic);
            rl = itemView.findViewById(R.id.participants_list_rl);
            iv_more = itemView.findViewById(R.id.participants_list_more);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onParticipantSelected(participantsFiltered.get(getAdapterPosition()));
                }
            });
        }

    }

    @NonNull
    @Override
    public ParticipantAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.participants_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ParticipantAdapter.MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv_name.setText(participantsFiltered.get(i).getName());
        myViewHolder.tv_username.setText(participantsFiltered.get(i).getPubgUsername());
        try{

            //if image is received then set
            Picasso.get().load(participantsFiltered.get(i).getProfilePic()).networkPolicy(NetworkPolicy.OFFLINE).into(myViewHolder.iv_pic, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(participantsFiltered.get(position).getProfilePic()).into(myViewHolder.iv_pic , new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            });

        }catch (Exception e){
            //if there is any exception while getting image then set default
            Picasso.get().load(R.drawable.userprofile).into(myViewHolder.iv_pic);
        }

    }

    @Override
    public int getItemCount() {
        return participantsFiltered.size();
    }

    public interface ParticipantAdapterListner {
        void onParticipantSelected(Participant participant);
    }
}
