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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AddNewMatchActivity extends AppCompatActivity {
    private static final int DIALOG_ID = 0;
    private static final int REQUEST_IMAGE = 100;
    private int year_x, month_x, day_x, y, m, d, hour, minute;
    private Calendar c;
    private String format;
    TimePickerDialog timepickerdialog;
    private Toolbar toolbar;
    private EditText et_title, et_date, et_time, et_map, et_version, et_type, et_winning_amount, et_per_kill, et_total_spots, et_status,
    et_key, et_image_link, et_entry_fee, et_show_id, et_watch_link;
    private Button btn_submit;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_match);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.rgb(13, 35, 71));
            window.setStatusBarColor(getResources().getColor(R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Find the toolbar view inside the activity layout
        toolbar = findViewById(R.id.activity_add_new_match_toolbar);
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
        myRef = firebaseDatabase.getReference("Matches");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateTitle())
                    return;
                if (!validateDate())
                    return;
                if (!validateTime())
                    return;
                if (!validateKey())
                    return;
                if(!validateLink())
                    return;
                if (!validateMap())
                    return;
                if (!validateVersion())
                    return;
                if (!validateType())
                    return;
                if (!validateWinnings())
                    return;
                if (!validatePerkill())
                    return;
                if (!validateEntryfee())
                    return;
                if (!validateTotalspot())
                    return;
                if (!validateStatus())
                    return;
                if(!validateShowID())
                    return;
                if(!validateWatchLink())
                    return;
                progressDialog.show();
                SendDataToMatches(myRef);
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

                timepickerdialog = new TimePickerDialog(AddNewMatchActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        et_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(AddNewMatchActivity.this, et_map);
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
                PopupMenu popup = new PopupMenu(AddNewMatchActivity.this, et_version);
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
                PopupMenu popup = new PopupMenu(AddNewMatchActivity.this, et_type);
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
                PopupMenu popup = new PopupMenu(AddNewMatchActivity.this, et_status);
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
                PopupMenu popup = new PopupMenu(AddNewMatchActivity.this, et_show_id);
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
        final int totalSpot, winningPrize, perKill, entryFee, occupiedSpot = 0;
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
        showId = et_show_id.getText().toString().trim();
        watchLink = et_watch_link.getText().toString().trim();

        MatchDetail matchDetail = new MatchDetail(key, title, date, time, type, version, map,
                status, winningPrize, perKill, entryFee, totalSpot, occupiedSpot, imageLink, showId, watchLink);

        myRef.child(key).setValue(matchDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddNewMatchActivity.this, "Match Added Successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddNewMatchActivity.this, "Match Adding Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Init() {
        et_date = findViewById(R.id.add_match_date);
        et_title = findViewById(R.id.add_match_title);
        et_time = findViewById(R.id.add_match_time);
        et_entry_fee = findViewById(R.id.add_match_entry_fee);
        et_total_spots = findViewById(R.id.add_match_total_spot);
        et_type = findViewById(R.id.add_match_type);
        et_version = findViewById(R.id.add_match_version);
        et_map = findViewById(R.id.add_match_map);
        et_status = findViewById(R.id.add_match_status);
        et_image_link = findViewById(R.id.add_match_pic_link);
        et_entry_fee = findViewById(R.id.add_match_entry_fee);
        et_per_kill = findViewById(R.id.add_match_per_kill);
        et_winning_amount = findViewById(R.id.add_match_winning_prize);
        et_key = findViewById(R.id.add_match_key);
        et_show_id = findViewById(R.id.add_match_show_id);
        et_watch_link = findViewById(R.id.add_match_watch_link);
        btn_submit = findViewById(R.id.add_submit);
        firebaseDatabase = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(AddNewMatchActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        c = Calendar.getInstance();

        et_title.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_title));
        et_date.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_date));
        et_time.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_time));
        et_key.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_key));
        et_map.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_map));
        et_type.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_type));
        et_version.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_version));
        et_image_link.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_image_link));
        et_status.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_status));
        et_total_spots.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_total_spots));
        et_entry_fee.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_entry_fee));
        et_per_kill.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_per_kill));
        et_winning_amount.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_winning_amount));
        et_show_id.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_show_id));
        et_watch_link.addTextChangedListener(new AddNewMatchActivity.MyTextWatcher(et_watch_link));
    }

    //Focus on edit text
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //********** Text Watcher for Validation *******************//
    private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.add_match_title:
                    validateTitle();
                    break;
                case R.id.add_match_date:
                    validateDate();
                    break;
                case R.id.add_match_time:
                    validateTime();
                    break;
                case R.id.add_match_key:
                    validateKey();
                    break;
                case R.id.add_match_pic_link:
                    validateLink();
                    break;
                case R.id.add_match_map:
                    validateMap();
                    break;
                case R.id.add_match_version:
                    validateVersion();
                    break;
                case R.id.add_match_type:
                    validateType();
                    break;
                case R.id.add_match_winning_prize:
                    validateWinnings();
                    break;
                case R.id.add_match_per_kill:
                    validatePerkill();
                    break;
                case R.id.add_match_entry_fee:
                    validateEntryfee();
                    break;
                case R.id.add_match_total_spot:
                    validateTotalspot();
                    break;
                case R.id.add_match_status:
                    validateStatus();
                    break;
                case R.id.add_match_show_id:
                    validateStatus();
                    break;
                case R.id.add_match_watch_link:
                    validateStatus();
                    break;


            }
        }
    }

    private boolean validateTitle() {
        if(et_title.getText().toString().trim().isEmpty()){
            et_title.setError("Please enter a title.");
            requestFocus(et_title);
            return false;
        }
        else{
            et_title.setError(null);
        }
        return true;
    }

    private boolean validateDate() {
        if(et_date.getText().toString().trim().isEmpty()){
            et_date.setError("Please select match date.");
            requestFocus(et_date);
            return false;
        }
        else{
            et_date.setError(null);
        }
        return true;
    }

    private boolean validateTime() {
        if(et_time.getText().toString().trim().isEmpty()){
            et_time.setError("Please select match time.");
            requestFocus(et_time);
            return false;
        }
        else{
            et_time.setError(null);
        }
        return true;
    }

    private boolean validateKey() {
        if(et_key.getText().toString().trim().isEmpty()){
            et_key.setError("Please enter match key.");
            requestFocus(et_key);
            return false;
        }
        else{
            et_key.setError(null);
        }
        return true;
    }

    private boolean validateLink() {
        if(et_image_link.getText().toString().trim().isEmpty()){
            et_image_link.setError("Please enter match image link.");
            requestFocus(et_image_link);
            return false;
        }
        else{
            et_image_link.setError(null);
        }
        return true;
    }

    private boolean validateMap() {
        if(et_map.getText().toString().trim().isEmpty()){
            et_map.setError("Please select map.");
            requestFocus(et_map);
            return false;
        }
        else{
            et_map.setError(null);
        }
        return true;
    }

    private boolean validateVersion() {
        if(et_version.getText().toString().trim().isEmpty()){
            et_version.setError("Please select match version");
            requestFocus(et_version);
            return false;
        }
        else{
            et_version.setError(null);
        }
        return true;
    }

    private boolean validateType() {
        if(et_type.getText().toString().trim().isEmpty()){
            et_type.setError("Please select match type.");
            requestFocus(et_type);
            return false;
        }
        else{
            et_type.setError(null);
        }
        return true;
    }

    private boolean validateWinnings() {
        if(et_winning_amount.getText().toString().trim().isEmpty()){
            et_winning_amount.setError("Please enter winning prize.");
            requestFocus(et_winning_amount);
            return false;
        }
        else{
            et_winning_amount.setError(null);
        }
        return true;
    }

    private boolean validatePerkill() {
        if(et_per_kill.getText().toString().trim().isEmpty()){
            et_per_kill.setError("Please enter per kill");
            requestFocus(et_per_kill);
            return false;
        }
        else{
            et_per_kill.setError(null);
        }
        return true;
    }

    private boolean validateEntryfee() {
        if(et_entry_fee.getText().toString().trim().isEmpty()){
            et_entry_fee.setError("Please enter match entry fee.");
            requestFocus(et_entry_fee);
            return false;
        }
        else{
            et_entry_fee.setError(null);
        }
        return true;
    }

    private boolean validateTotalspot() {
        if(et_total_spots.getText().toString().trim().isEmpty()){
            et_total_spots.setError("Please enter match total spot.");
            requestFocus(et_total_spots);
            return false;
        }
        else{
            et_total_spots.setError(null);
        }
        return true;
    }

    private boolean validateStatus() {
        if(et_status.getText().toString().trim().isEmpty()){
            et_status.setError("Please select match status.");
            requestFocus(et_status);
            return false;
        }
        else{
            et_status.setError(null);
        }
        return true;
    }
    private boolean validateShowID() {
        if(et_show_id.getText().toString().trim().isEmpty()){
            et_show_id.setError("Please select match show ID.");
            requestFocus(et_show_id);
            return false;
        }
        else{
            et_show_id.setError(null);
        }
        return true;
    }
    private boolean validateWatchLink() {
        if(et_watch_link.getText().toString().trim().isEmpty()){
            et_watch_link.setError("Please enter match watch link.");
            requestFocus(et_watch_link);
            return false;
        }
        else{
            et_watch_link.setError(null);
        }
        return true;
    }

}
