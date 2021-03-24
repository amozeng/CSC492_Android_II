package com.amozeng.a1_rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.amozeng.a1_rewards.Runnable.CreateProfileAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity {

    private static final String TAG = "CreateProfile";

    private ImageView profileImage;

    private SharedPreferences.Editor editor;  // to save text

    // text count
    private static final int MAX_LEN = 360;
    private EditText story;
    private TextView textCountDisplay;

    // image gallery
    private final int REQUEST_IMAGE_GALLERY = 1;
    public static Bitmap selectedImage;

    // profile
    private EditText username, password, firstName, lastName, department, position;

    private static int CREATE_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Utilities.setupHomeIndicator(getSupportActionBar()); // Home/Up Nav

        setTitle("Create Profile");

        textCountDisplay = findViewById(R.id.tv_yourStory);
        story = findViewById(R.id.et_story);
        profileImage = findViewById(R.id.iv_profile);

        // profile
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        firstName = findViewById(R.id.et_profile_fName);
        lastName = findViewById(R.id.et_profile_lName);
        department = findViewById(R.id.et_department);
        position = findViewById(R.id.et_position);
        setupEditText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save_profile:
                saveProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

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


    // connect with View
    public void saveProfile(){

        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        String fNameStr = firstName.getText().toString();
        String lNameStr = lastName.getText().toString();
        String dpStr = department.getText().toString();
        String posStr = position.getText().toString();
        String storyStr = story.getText().toString();
        String remainingPoint = "1000";

        if(selectedImage == null) {
            invalidImage();
            return;
        }

        // validate the input
        if(usernameStr == null || usernameStr.isEmpty() || usernameStr.length() > 20) {
            //Toast.makeText(this, "Invalid Username! Please enter again!", Toast.LENGTH_LONG).show();
            invalidInputDialog("Username");
            username.setText(" ");
            return;
        }

        if(fNameStr == null || fNameStr.isEmpty() || fNameStr.length() > 20 ){
            invalidInputDialog("First Name");
            firstName.setText(" ");
            return;
        }

        if(lNameStr == null || lNameStr.isEmpty() || lNameStr.length() > 20 ){
            invalidInputDialog("Last Name");
            lastName.setText(" ");
            return;
        }

        if(dpStr == null || dpStr.isEmpty() || dpStr.length() > 20 ){
            invalidInputDialog("Department");
            department.setText(" ");
            return;
        }

        if(storyStr == null || storyStr.isEmpty() || storyStr.length() > 360 ){
            invalidInputDialog("Story");
            story.setText(" ");
            return;
        }

        if(posStr == null || posStr.isEmpty() || posStr.length() > 20 ){
            invalidInputDialog("Position");
            position.setText(" ");
            return;
        }

        if(passwordStr == null || passwordStr.isEmpty() || passwordStr.length() > 20 ){
            invalidInputDialog("Password");
            password.setText(" ");
            return;
        }


        Profile newProfile = new Profile(usernameStr);
        newProfile.setPassword(passwordStr);
        newProfile.setFirstName(fNameStr);
        newProfile.setLastName(lNameStr);
        newProfile.setDepartment(dpStr);
        newProfile.setPosition(posStr);
        newProfile.setStory(storyStr);
        newProfile.setPointsToAward(remainingPoint);
        newProfile.setLocation(MainActivity.locationString);

        String imageBase64 = makeImageBase64();
        newProfile.setImageBytes(imageBase64);


        new Thread(new CreateProfileAPIRunnable(this, fNameStr, lNameStr, usernameStr, passwordStr, dpStr, posStr, storyStr, remainingPoint, MainActivity.locationString, imageBase64)).start();

        //Intent intent = new Intent();
        Intent intent = new Intent(this, DisplayProfile.class);
        intent.putExtra("NEW_PROFILE", newProfile);
        //setResult(Activity.RESULT_OK, intent);
        //startActivity(intent);
        startActivityForResult(intent, CREATE_REQUEST);

    }

    public void invalidInputDialog(String invalidInfo) {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Invalid " + invalidInfo + " format, please enter again");
        builder.setTitle("Invalid Input");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void invalidImage() {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Please upload image");
        builder.setTitle("Invalid Image");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
    }

    public void addImageFromGallery(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
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
        profileImage.setImageBitmap(selectedImage);
        Toast.makeText(this, "processGallery: " + String.format(Locale.getDefault(),
                "Gallery Image Size:%n%,d bytes", selectedImage.getByteCount()), Toast.LENGTH_LONG).show();
    }



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