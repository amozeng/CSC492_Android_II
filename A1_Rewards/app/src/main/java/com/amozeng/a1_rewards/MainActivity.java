package com.amozeng.a1_rewards;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amozeng.a1_rewards.Runnable.GetStudentApiKeyRunnable;
import com.amozeng.a1_rewards.Runnable.LoginAPIRunnable;
import com.amozeng.a1_rewards.api.Profile;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // location
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;
    public static String locationString = "Unspecified Location";

    // APIKey
    public static String APIKey;
    private String fNameStr, lNameStr, idStr, emailStr;

    EditText username;
    EditText password;
    private String savedUsername;
    private String savedPassword;

    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Rewards");

        username = findViewById(R.id.et_main_username);
        password = findViewById(R.id.et_main_password);

        checkbox = findViewById(R.id.checkBox);

        // location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();

        readJSONData();

//        if (savedUsername != null) { username.setText(savedUsername); }
//        if (savedPassword != null) { password.setText(savedPassword); }

        if(APIKey != null) {
            boolean APIKeyEmpty = APIKey.isEmpty();
            boolean APIKeyNull = APIKey.equals("null");
        }

        if (APIKey == null || APIKey.isEmpty() || APIKey.equals("null")) {
            requestAPIKey();
        }
    }

    public void login(View v) {
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        writeJSONData();
        new Thread(new LoginAPIRunnable(MainActivity.this, usernameStr, passwordStr)).start();

    }

    public void getProfileFromLogin(Profile p) {
        displayLoginProfile(p);
    }

    public void displayLoginProfile(Profile p) {
        Intent intent = new Intent(this, DisplayProfile.class);
        intent.putExtra("LOGIN_PROFILE", p);
        startActivity(intent);
    }

    public void createProfile(View v) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }


    private void requestAPIKey() {

        // Inflate the dialog's layout
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.api_dialog, null);

        // Dialog START
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText firstName = new EditText(this);
        firstName.setGravity(Gravity.CENTER_HORIZONTAL);
        firstName.setInputType(InputType.TYPE_CLASS_TEXT);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText fName = view.findViewById(R.id.et_fName);
                EditText lName = view.findViewById(R.id.et_lName);
                EditText email = view.findViewById(R.id.et_email);
                EditText id = view.findViewById(R.id.et_id);

                fNameStr = fName.getText().toString();
                 lNameStr = lName.getText().toString();
                 emailStr = email.getText().toString();
                 idStr = id.getText().toString();

                // check if any missing data
                if(fNameStr.length() == 0 || lNameStr .length() == 0 || emailStr.length()  == 0 || idStr.length() == 0){
                    requestAPIKey();
                }

                // check email
                if (Patterns.EMAIL_ADDRESS.matcher(emailStr).matches())
                {
                    String emailDomain = emailStr.substring(emailStr.length()-3);
                    if(emailDomain.equals("edu")){
                        Log.d(TAG, "email domain: " + emailDomain);
                    }else{
                        Toast.makeText(MainActivity.this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
                        requestAPIKey();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Invalid Email Address!", Toast.LENGTH_SHORT).show();
                    requestAPIKey();
                }

                // check student id
                if(idStr.length() > 20) {
                    Toast.makeText(MainActivity.this, "Invalid Student ID!", Toast.LENGTH_SHORT).show();
                    requestAPIKey();
                }

                new Thread(new GetStudentApiKeyRunnable(MainActivity.this, fNameStr, lNameStr, emailStr, idStr)).start();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You have to request an API Key!", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setTitle("API Key Needed");
        builder.setMessage("You need to request an API key:");
        builder.setIcon(R.drawable.icon);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveAPIKey(String apiKey) {
        APIKey = apiKey;
        writeJSONData();
        showAPIKeyResultDialog();
    }

    private void showAPIKeyResultDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // just a OK button
            }
        });

        builder.setTitle("API Key Received and Stored");
        String nameString = "Name: " + fNameStr + " " + lNameStr;
        String idString = "Student ID: " + idStr;
        String emailString = "Email: " + emailStr;
        String apiKey = "API Key: " + APIKey;
        String sum = nameString + "\n" + idString + "\n" + emailString + "\n" + apiKey;

        builder.setMessage(sum);

        builder.setIcon(R.drawable.icon);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteAPIKey(View v) {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.APIKeyJonsFileName), Context.MODE_PRIVATE);

            JsonWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            }
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("APIKey").value("");
            writer.endObject();
            writer.close();


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: "+ e.getMessage());

        }
    }

    private void determineLocation() {
        if (checkPermission()) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationString = getPlace(location);
                            Log.d(TAG, "determineLocation: " + locationString);
                            //textView.setText(locationString);
                        }
                    })
                    .addOnFailureListener(this, e -> Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                    }, LOCATION_REQUEST);
            return false;
        }
        return true;
    }

    private String getPlace(Location loc) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            return city + ", " + state;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                } else {
                    Toast.makeText(MainActivity.this, "Need Location Permission!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    private void writeJSONData() {
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(getString(R.string.APIKeyJonsFileName), Context.MODE_PRIVATE);

            JsonWriter writer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            }
            writer.setIndent("  ");
            writer.beginObject();
            writer.name("APIKey").value(APIKey);


            if(checkbox.isChecked()) {
                writer.name("saveCredentials").value("YES");
                writer.name("username").value(username.getText().toString());
                writer.name("password").value(password.getText().toString());
            }

            writer.endObject();
            writer.close();


        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: "+ e.getMessage());

        }
    }

    private void readJSONData() {
        try {
            FileInputStream fis = getApplicationContext().openFileInput(getString(R.string.APIKeyJonsFileName));

            // Read string content from file
            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data); // size of the file
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            JSONObject nObj = new JSONObject(json);
            if(nObj != null) {
                APIKey = nObj.getString("APIKey");
                if(nObj.has("saveCredentials")) {
                    checkbox.setChecked(true);
                }

                if(nObj.has("username")) {
                    //savedUsername = nObj.getString("username");
                    username.setText(nObj.getString("username"));

                }
                if(nObj.has("password")) {
                    //savedPassword = nObj.getString("password");
                    password.setText(nObj.getString("password"));
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "readJSONData: " + e.getMessage());
        }
    }


    public void invalidLoginInfo() {

        // invalid info dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // just a OK button
            }
        });

        builder.setTitle("Invalid Login info");
        builder.setMessage("Invalid username or password, please enter again");
        builder.setIcon(R.drawable.icon);

        AlertDialog dialog = builder.create();
        dialog.show();

        // clear EditView
        username.setText("");
        password.setText("");
    }
}