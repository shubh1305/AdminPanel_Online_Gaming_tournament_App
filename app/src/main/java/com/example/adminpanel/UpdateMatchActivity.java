package com.example.adminpanel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UpdateMatchActivity extends AppCompatActivity {
    private static final int DIALOG_ID = 0;
    private static final int REQUEST_IMAGE = 100;
    private int year_x, month_x, day_x, y, m, d, hour, minute;
    private Calendar c;
    private String format, match_key;
    TimePickerDialog timepickerdialog;
    private Toolbar toolbar;
    private EditText et_title, et_date, et_time, et_map, et_version, et_type, et_winning_amount, et_per_kill, et_total_spots, et_status,
            et_key, et_image_link, et_entry_fee, et_occupied_spot, et_watch_link, et_show_id;
    private Button btn_submit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef, myRef2;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_match);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.activity_update_match_toolbar);
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
        myRef = firebaseDatabase.getReference("Matches").child(match_key);
        myRef2 = firebaseDatabase.getReference("Matches");
        progressDialog.show();
        RetrieveData(myRef);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                SendDataToMatches(myRef2);
                finish();
            }
        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                timepickerdialog = new TimePickerDialog(UpdateMatchActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        }
                        else if (hourOfDay == 12) {
                            format = "PM";
                        }
                        else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        }
                        else {
                            format = "AM";
                        }
                        if(hourOfDay<10){
                            if(minute<10)
                                et_time.setText("0"+hourOfDay+":0"+minute+" "+format);
                            else
                                et_time.setText("0"+hourOfDay+":"+minute+" "+format);
                        }
                        else{
                            if(minute<10)
                                et_time.setText(hourOfDay+":0"+minute+" "+format);
                            else
                                et_time.setText(hourOfDay+":"+minute+" "+format);
                        }

                    }
                },hour, minute, false);
                timepickerdialog.show();
            }
        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });

        et_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateMatchActivity.this, et_map);
                popup.getMenuInflater().inflate(R.menu.map_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.map_erangel:
                                et_map.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.map_miramar:
                                et_map.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.map_sanhok:
                                et_map.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.map_vikendi:
                                et_map.setText(menuItem.getTitle().toString());
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        et_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateMatchActivity.this, et_version);
                popup.getMenuInflater().inflate(R.menu.version_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.version_tpp:
                                et_version.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.version_fpp:
                                et_version.setText(menuItem.getTitle().toString());
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        et_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateMatchActivity.this, et_type);
                popup.getMenuInflater().inflate(R.menu.type_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.type_solo:
                                et_type.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.type_duo:
                                et_type.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.type_squad:
                                et_type.setText(menuItem.getTitle().toString());
                                break;
                        }
                        return true;
                    }
                });
            }
        });

        et_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateMatchActivity.this, et_status);
                popup.getMenuInflater().inflate(R.menu.status_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.status_upcoming:
                                et_status.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.status_ongoing:
                                et_status.setText(menuItem.getTitle().toString());
                                break;
                            case R.id.status_result:
                                et_status.setText(menuItem.getTitle().toString());
                                break;
                        }
                        return true;
                    }
                });
            }
        });
        et_show_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(UpdateMatchActivity.this, et_show_id);
                popup.getMenuInflater().inflate(R.menu.winner_menu, popup.getMenu());
                popup.show();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.winner_yes:
                                et_show_id.setText("y");
                                break;
                            case R.id.winner_no:
                                et_show_id.setText("n");
                                break;
                        }
                        return true;
                    }
                });
            }
        });
        et_image_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(UpdateMatchActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()){
                                    showImagePickerOptions();
                                }

                                if(report.isAnyPermissionPermanentlyDenied()){
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grant Permission");
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton("GOTO SETTINGS", ((dialog, which) -> {
            dialog.cancel();
            openSettings();
        }));
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                LaunchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                LaunchGallaryIntent();
            }
        });
    }

    private void LaunchGallaryIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 20);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void LaunchCameraIntent() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 3); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 2);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_COMPRESSION_QUALITY, 10);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);


        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                UploadProfilePic(uri);
            }
        }
    }

    private void UploadProfilePic(Uri uri) {

        //show progress
        progressDialog.show();
        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://pubg-battle-b640c.appspot.com")
                .child("Matches").child(match_key).child("DisplayPic");
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url and store in user's database
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //Check if image is uploaded or not and url is received
                        if(uriTask.isSuccessful()){
                            //image uploaded
                            //UserProfile userProfile = new UserProfile(downloadUri.toString());
                            HashMap<String, Object> results = new HashMap<>();
                            results.put("matchPic", downloadUri.toString());
                            myRef.updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //url in the database of user is added successfully
                                    //dismiss progressbar
                                    progressDialog.dismiss();
                                    Toast.makeText(UpdateMatchActivity.this,"ImageLoaded", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding url to database of user
                                            //dismiss progressbar
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateMatchActivity.this,"Error in Uploading Profile Picture", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(UpdateMatchActivity.this,"Some error occurred",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateMatchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_ID) {
            y = c.get(Calendar.YEAR);
            m = c.get(Calendar.MONTH);
            d = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, dpickerListner, y, m, d);
        }
        else
            return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListner = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month+1;
            day_x = dayOfMonth;
            if(month_x <10){
                if(day_x <10)
                    et_date.setText("0"+day_x+"/"+"0"+month_x+"/"+year_x);
                else
                    et_date.setText(day_x+"/"+"0"+month_x+"/"+year_x);
            }
            else{
                if(day_x<10)
                    et_date.setText("0"+day_x+"/"+month_x+"/"+year_x);
                else
                    et_date.setText(day_x+"/"+month_x+"/"+year_x);
            }
            //Toast.makeText(EditProfileActivity.this, day_x+"/"+month_x+"/"+year_x,Toast.LENGTH_LONG).show();
        }
    };

    private void SendDataToMatches(DatabaseReference myRef) {
        final String title, date, time, map, type, version, status, key, imageLink, showId, watchLink;
        final int totalSpot, winningPrize, perKill, entryFee, occupiedSpot;
        title = et_title.getText().toString().trim();
        date = et_date.getText().toString().trim();
        time = et_time.getText().toString().trim();
        map = et_map.getText().toString().trim();
        version = et_version.getText().toString().trim();
        type = et_type.getText().toString().trim();
        status = et_status.getText().toString().trim();
        key = et_key.getText().toString().trim();
        imageLink = et_image_link.getText().toString().trim();
        totalSpot = Integer.parseInt(et_total_spots.getText().toString().trim());
        winningPrize = Integer.parseInt(et_winning_amount.getText().toString().trim());
        perKill = Integer.parseInt(et_per_kill.getText().toString().trim());
        entryFee = Integer.parseInt(et_entry_fee.getText().toString().trim());
        occupiedSpot = Integer.parseInt(et_occupied_spot.getText().toString().trim());
        showId = et_show_id.getText().toString().trim();
        watchLink = et_watch_link.getText().toString().trim();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("matchTitle", title);
        hashMap.put("matchDate", date);
        hashMap.put("matchTime", time);
        hashMap.put("matchKey", key);
        //hashMap.put("matchPic", imageLink);
        hashMap.put("matchMap", map);
        hashMap.put("matchVersion", version);
        hashMap.put("matchType", type);
        hashMap.put("matchTotalSpot", totalSpot);
        hashMap.put("matchEntryFee", entryFee);
        hashMap.put("matchStatus", status);
        hashMap.put("matchOccupiedSpot", occupiedSpot);
        hashMap.put("matchWinningPrize", winningPrize);
        hashMap.put("matchPerKill", perKill);
        hashMap.put("matchIdShow", showId);
        hashMap.put("matchWatchLink", watchLink);

        myRef.child(key).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UpdateMatchActivity.this, "Match Updated Successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UpdateMatchActivity.this, "Match Updating Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void RetrieveData(DatabaseReference myRef) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MatchDetail matchDetail = dataSnapshot.getValue(MatchDetail.class);
                et_title.setText(matchDetail.getMatchTitle());
                et_date.setText(matchDetail.getMatchDate());
                et_time.setText(matchDetail.getMatchTime());
                et_type.setText(matchDetail.getMatchType());
                et_map.setText(matchDetail.getMatchMap());
                et_version.setText(matchDetail.getMatchVersion());
                et_status.setText(matchDetail.getMatchStatus());
                et_image_link.setText(matchDetail.getMatchPic());
                et_winning_amount.setText(""+ matchDetail.getMatchWinningPrize());
                et_total_spots.setText(""+ matchDetail.getMatchTotalSpot());
                et_entry_fee.setText(""+ matchDetail.getMatchEntryFee());
                et_per_kill.setText(""+ matchDetail.getMatchPerKill());
                et_key.setText(matchDetail.getMatchKey());
                et_occupied_spot.setText(""+ matchDetail.getMatchOccupiedSpot());
                et_show_id.setText(matchDetail.getMatchIdShow());
                et_watch_link.setText(matchDetail.getMatchWatchLink());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    private void Init() {
        et_date = findViewById(R.id.update_match_date);
        et_title = findViewById(R.id.update_match_title);
        et_time = findViewById(R.id.update_match_time);
        et_entry_fee = findViewById(R.id.update_match_entry_fee);
        et_total_spots = findViewById(R.id.update_match_total_spot);
        et_type = findViewById(R.id.update_match_type);
        et_version = findViewById(R.id.update_match_version);
        et_map = findViewById(R.id.update_match_map);
        et_status = findViewById(R.id.update_match_status);
        et_image_link = findViewById(R.id.update_match_pic_link);
        et_entry_fee = findViewById(R.id.update_match_entry_fee);
        et_per_kill = findViewById(R.id.update_match_per_kill);
        et_winning_amount = findViewById(R.id.update_match_winning_prize);
        et_key = findViewById(R.id.update_match_key);
        et_occupied_spot = findViewById(R.id.update_match_occupied_spot);
        et_show_id = findViewById(R.id.update_match_show_id);
        et_watch_link = findViewById(R.id.update_match_watch_link);
        btn_submit = findViewById(R.id.update_submit);
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(UpdateMatchActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        c = Calendar.getInstance();

        match_key = getIntent().getStringExtra("matchkey");
    }


}
