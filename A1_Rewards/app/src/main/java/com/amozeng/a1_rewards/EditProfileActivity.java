package com.amozeng.a1_rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amozeng.a1_rewards.Runnable.UpdateProfileAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText username, password, firstName, lastName, department, position, story;
    private ImageView portrait;

    // Image Gallery
    private final int REQUEST_IMAGE_GALLERY = 1;
    public static Bitmap selectedImage;

    // location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locationString = "Unspecified Location";

    //private Profile editedProfile;
    private Profile profileHolder;

    private boolean imageChanged = false;

    private TextView textCountDisplay;
    private static final int MAX_LEN = 360;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Utilities.setupHomeIndicator(getSupportActionBar()); // Home/Up Nav

        setTitle("Edit Profile");

        // location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //determineLocation();

        // profile
        username = findViewById(R.id.et_username_edit);
        username.setEnabled(false);
        password = findViewById(R.id.et_password_edit);
        firstName = findViewById(R.id.et_fName_edit);
        lastName = findViewById(R.id.et_lName_edit);
        department = findViewById(R.id.et_department_edit);
        position = findViewById(R.id.et_position_edit);
        story = findViewById(R.id.et_story_edit);
        textCountDisplay = findViewById(R.id.tv_edit_yourStory);


        portrait = findViewById(R.id.iv_edit_portrait);


        // get profile from DisplayProfile
        Intent intent = getIntent();
        if (intent.hasExtra("EDIT_PROFILE")) {
            profileHolder = (Profile)intent.getSerializableExtra("EDIT_PROFILE");
            if(profileHolder != null) {
                loadProfile(profileHolder);
            }
        }
        locationString = MainActivity.locationString;

        setupEditText();

    }

    private void loadProfile(Profile p) {
        firstName.setText(p.getFirstName());
        lastName.setText(p.getLastName());
        username.setText(p.getUsername());
        department.setText(p.getDepartment());
        position.setText(p.getPosition());
        password.setText(p.getPassword());
        String storyTmp = p.getStory();
        story.setText(p.getStory());
        textToImage(p.getImageBytes());

    }

    private void updateProfile(){
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        String fNameStr = firstName.getText().toString();
        String lNameStr = lastName.getText().toString();
        String dpStr = department.getText().toString();
        String posStr = position.getText().toString();
        String storyStr = story.getText().toString();

        //editedProfile = new Profile(usernameStr);
        profileHolder.setPassword(passwordStr);
        profileHolder.setFirstName(fNameStr);
        profileHolder.setLastName(lNameStr);
        profileHolder.setDepartment(dpStr);
        profileHolder.setPosition(posStr);
        profileHolder.setStory(storyStr);
        profileHolder.setLocation(MainActivity.locationString);

        if(imageChanged) {
            String imageBase64 = makeImageBase64();
            profileHolder.setImageBytes(imageBase64);
        }

        new Thread(new UpdateProfileAPIRunnable(this, profileHolder)).start();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_edit:
                saveDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getUpdatedProfile(String s) {
        profileHolder.setPoints(s);
        backToDisplayActivity();
    }

    public void backToDisplayActivity(){
        Intent intent = new Intent();
        //Intent intent = new Intent(this, DisplayProfile.class);
        intent.putExtra("EDIT_PROFILE", profileHolder);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateProfile();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });



        builder.setTitle("Save Changes?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // update image:
    public void displayProfileDialog(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                addImageFromGallery();
            }
        });

        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setTitle("Profile Picture");
        builder.setMessage("Take picture from:");

        AlertDialog dialog = builder.create();
        dialog.show();

        imageChanged = true;
    }


    // ------- IMAGE -------//
    private String makeImageBase64() {
        // Remember - API requirements:
        // Profile image (as Base64 String) – Not null or empty, 100000 character maximum
        ByteArrayOutputStream byteArrayOutputStream;
        int value = 50;
        while (value > 0) {
            byteArrayOutputStream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
            String b64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            Log.d(TAG, "makeImageBase64: " + b64.length());
            if (b64.length() > 100000) {
                value -= 10;
            } else {
                Log.d(TAG, "makeImageBase64: " + value);
                return b64;
            }
        }
        return null;
    }

    public void addImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    public void textToImage(String imgStr64) {
        if (imgStr64 == null) return;

        byte[] imageBytes = Base64.decode(imgStr64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        portrait.setImageBitmap(bitmap);
    }

    private void processGallery(Intent data){
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null) return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        selectedImage = BitmapFactory.decodeStream(imageStream);
        portrait.setImageBitmap(selectedImage);
        Toast.makeText(this, "processGallery: " + String.format(Locale.getDefault(),
                "Gallery Image Size:%n%,d bytes", selectedImage.getByteCount()), Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    // ------- IMAGE END -------//


    private void setupEditText() {
        story.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN)
        });

        story.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // executes after completion of typing a char
                        int len = s.toString().length();
                        String countText = "Your Story: (" + len + " of " + MAX_LEN + ")";
                        textCountDisplay.setText(countText);
                    }
                }
        );
    }

}