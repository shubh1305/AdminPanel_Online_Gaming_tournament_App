package com.example.adminpanel;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

import kotlin.Result;

public class UpdateResultActivity extends AppCompatActivity {
    private EditText et_name, et_pubgUsername, et_email, et_gender, et_mobile, et_dob, et_uid, et_password, et_pic_link, et_promocode,
    et_matchPlayed, et_totalKills, et_totalWinning, et_walletCoin, et_winner;
    private Toolbar toolbar;
    private String matchKey, uid;
    private Button btn_submit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef, myRef2;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_result);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.activity_update_result_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.main));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        Init();
        myRef = firebaseDatabase.getReference("Users").child(uid);
        myRef2 = firebaseDatabase.getReference("Matches").child(matchKey).child("participants").child(uid);
        FetchUserData(myRef, myRef2);

        et_winner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateResultActivity.this, et_winner);
                popup.getMenuInflater().inflate(R.menu.winner_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.winner_yes:
                                et_winner.setText("y");
                                break;
                            case R.id.winner_no:
                                et_winner.setText("n");
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                UpdateResult(myRef, myRef2);
            }
        });
    }

    private void UpdateResult(final DatabaseReference myRef, DatabaseReference myRef2) {
        final String name, pubgUsername, email, mobileNo, password, promoCode, dob, gender, profilePic, uid, winner;
        final int matchPlayed, totalKills, totalWinnings, walletCoins;
        name = et_name.getText().toString().trim();
        pubgUsername = et_pubgUsername.getText().toString().trim();
        email = et_email.getText().toString().trim();
        mobileNo = et_mobile.getText().toString().trim();
        password = et_password.getText().toString().trim();
        promoCode = et_promocode.getText().toString().trim();
        dob = et_dob.getText().toString().trim();
        if(et_gender.getText().toString().trim().equals("Male"))
            gender = "M";
        else if(et_gender.getText().toString().trim().equals("Female"))
            gender = "F";
        else
            gender = "";
        profilePic = et_pic_link.getText().toString().trim();
        uid = et_uid.getText().toString().trim();
        winner = et_winner.getText().toString();
        matchPlayed = Integer.parseInt(et_matchPlayed.getText().toString().trim());
        totalKills = Integer.parseInt(et_totalKills.getText().toString().trim());
        totalWinnings = Integer.parseInt(et_totalWinning.getText().toString().trim());
        walletCoins = Integer.parseInt(et_walletCoin.getText().toString().trim());

        Participant participantMatch = new Participant(name, pubgUsername, email, mobileNo, password, promoCode, dob, gender, profilePic, uid, winner,
                matchPlayed, totalKills, totalWinnings, walletCoins);

        myRef2.setValue(participantMatch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Participant participant = dataSnapshot.getValue(Participant.class);
                        int totalKill = participant.getTotalKills()+ totalKills;
                        int totalWinning = participant.getTotalWinnings()+ totalWinnings;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("totalKills", totalKill);
                        hashMap.put("totalWinnings", totalWinning);

                        /*Participant p = new Participant(name, pubgUsername, email, mobileNo, password, promoCode, dob, gender, profilePic, uid, winner,
                                matchPlayed, totalKill, totalWinning, walletCoins);*/

                        myRef.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateResultActivity.this, "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateResultActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        });




    }

    private void FetchUserData(DatabaseReference myRef, DatabaseReference myRef2) {
        progressDialog.show();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.show();
                final Participant participant = dataSnapshot.getValue(Participant.class);
                et_name.setText(participant.getName());
                et_pubgUsername.setText(participant.getPubgUsername());
                et_dob.setText(participant.getDob());
                et_password.setText(participant.getPassword());
                et_pic_link.setText(participant.getProfilePic());
                et_promocode.setText(participant.getPromoCode());
                et_email.setText(participant.getEmail());
                if(participant.getGender().equals("M"))
                    et_gender.setText("Male");
                else if(participant.getGender().equals("F"))
                    et_gender.setText("Female");
                else
                    et_gender.setText("N/A");
                et_mobile.setText(participant.getMobileNo());
                et_uid.setText(participant.getUid());
                et_matchPlayed.setText(""+ participant.getMatchPlayed());
                et_walletCoin.setText(""+ participant.getWalletCoins());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.show();
                final Participant participant = dataSnapshot.getValue(Participant.class);
                et_totalKills.setText(""+ participant.getTotalKills());
                et_totalWinning.setText(""+ participant.getTotalWinnings());
                et_winner.setText(participant.getWinner());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void Init() {
        et_name = findViewById(R.id.update_result_name);
        et_pubgUsername = findViewById(R.id.update_result_username);
        et_email = findViewById(R.id.update_result_email);
        et_mobile = findViewById(R.id.update_result_mobile);
        et_pic_link = findViewById(R.id.update_result_profile_pic_link);
        et_gender = findViewById(R.id.update_result_gender);
        et_uid = findViewById(R.id.update_result_uid);
        et_password = findViewById(R.id.update_result_password);
        et_dob = findViewById(R.id.update_result_dob);
        et_promocode = findViewById(R.id.update_result_promo);
        et_matchPlayed = findViewById(R.id.update_result_match_played);
        et_totalKills = findViewById(R.id.update_result_totalkills);
        et_totalWinning = findViewById(R.id.update_result_winnings);
        et_walletCoin = findViewById(R.id.update_result_walletcoins);
        et_winner = findViewById(R.id.update_result_winner);

        btn_submit = findViewById(R.id.result_submit);

        progressDialog = new ProgressDialog(UpdateResultActivity  .this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        matchKey = getIntent().getStringExtra("matchkey");
        uid = getIntent().getStringExtra("uid");

        firebaseDatabase = FirebaseDatabase.getInstance();
    }
}
