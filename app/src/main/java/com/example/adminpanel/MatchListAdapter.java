package com.example.adminpanel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.snapshot.Index;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<MatchDetail> matchDetails;
    private List<MatchDetail> matchDetailsFiltered;
    private MatchAdapterListener listener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    public MatchListAdapter(Context c, List<MatchDetail> m, MatchAdapterListener listener){
        this.context = c;
        this.matchDetails = m;
        this.matchDetailsFiltered = m;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.match_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchListAdapter.MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tv_title.setText(matchDetailsFiltered.get(i).getMatchTitle());
        myViewHolder.tv_date.setText(matchDetailsFiltered.get(i).getMatchDate());
        myViewHolder.tv_time.setText(matchDetailsFiltered.get(i).getMatchTime());

        try{

            //if image is received then set
            Picasso.get().load(matchDetailsFiltered.get(i).getMatchPic()).networkPolicy(NetworkPolicy.OFFLINE).into(myViewHolder.iv_pic, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(matchDetailsFiltered.get(position).getMatchPic()).into(myViewHolder.iv_pic, new Callback() {
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


        myViewHolder.rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, myViewHolder.iv_more);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub

                        switch (item.getItemId()) {
                            case R.id.view:
                                Toast.makeText(context,
                                        "You Clicked : " + item.getTitle(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.update:
                                Intent intent = new Intent(context, UpdateMatchActivity.class);
                                intent.putExtra("matchkey", matchDetails.get(position).getMatchKey());
                                context.startActivity(intent);
                                break;
                            case R.id.delete:
                                progressDialog = new ProgressDialog(context);
                                progressDialog.setMessage("Deleting");
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                firebaseDatabase = FirebaseDatabase.getInstance();
                                myRef = firebaseDatabase.getReference("Matches").child(matchDetailsFiltered.get(position).getMatchKey());
                                myRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "Match Deleted Successfully",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context,
                                                "Match Deletion Failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                        return true;
                    }
                });
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchDetailsFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    matchDetailsFiltered = matchDetails;
                } else {
                    List<MatchDetail> filteredList = new ArrayList<>();
                    for (MatchDetail row : matchDetails) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getMatchTitle().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    matchDetailsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = matchDetailsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                matchDetailsFiltered = (ArrayList<MatchDetail>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_date, tv_time;
        ImageView iv_pic, iv_more;
        RelativeLayout rl;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.match_list_title);
            tv_date = itemView.findViewById(R.id.match_list_date);
            tv_time = itemView.findViewById(R.id.match_list_time);
            iv_pic = itemView.findViewById(R.id.match_list_pic);
            rl = itemView.findViewById(R.id.match_list_rl);
            iv_more = itemView.findViewById(R.id.match_list_more);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onMatchSelected(matchDetailsFiltered.get(getAdapterPosition()));
                }
            });

        }

    }

    public interface MatchAdapterListener {
        void onMatchSelected(MatchDetail match);
    }

}
