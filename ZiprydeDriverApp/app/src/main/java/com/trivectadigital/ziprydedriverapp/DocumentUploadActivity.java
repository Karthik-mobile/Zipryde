package com.trivectadigital.ziprydedriverapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.CircleImageView;
import com.trivectadigital.ziprydedriverapp.assist.ImageLoadingUtils;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfPercentage;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity {

    Button continueBtn;
    ZiprydeApiInterface apiService;
    EditText licenseEdit, restriEdit;
    public static EditText issuedDateEdit, expiryDateEdit;
    public static String textField = "issued";
    ImageView uploadImgFront, uploadImgBack;
    //    Spinner percentageSpinner;
    CircleImageView uploadProfilePic;

    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FRONT = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK = 101;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_PROFILE = 102;
    File finalmediaFile;

    File finalmediaFileFront, finalmediaFileBack, finalmediaFileProfile;

    String phoneno, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_upload);
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titleback, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Upload Driver License Details");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        continueBtn = (Button) findViewById(R.id.continueBtn);
        uploadProfilePic = (CircleImageView) findViewById(R.id.uploadProfilePic);

        licenseEdit = (EditText) findViewById(R.id.licenseEdit);
        restriEdit = (EditText) findViewById(R.id.restriEdit);
        issuedDateEdit = (EditText) findViewById(R.id.issuedDateEdit);
        expiryDateEdit = (EditText) findViewById(R.id.expiryDateEdit);

        uploadImgFront = (ImageView) findViewById(R.id.uploadImgFront);
        uploadImgBack = (ImageView) findViewById(R.id.uploadImgBack);

        TextView dlnText = (TextView) findViewById(R.id.dlnText);
        dlnText.setText(getRedManditoty(dlnText.getText().toString().trim()));
        TextView resText = (TextView) findViewById(R.id.resText);
        resText.setText(getRedManditoty(resText.getText().toString().trim()));
        TextView iseText = (TextView) findViewById(R.id.iseText);
        iseText.setText(getRedManditoty(iseText.getText().toString().trim()));
        TextView exyText = (TextView) findViewById(R.id.exyText);
        exyText.setText(getRedManditoty(exyText.getText().toString().trim()));

//        percentageSpinner = (Spinner) findViewById(R.id.percentageSpinner);
        getAllNYOPList();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String license = licenseEdit.getText().toString().trim();
                String restri = restriEdit.getText().toString().trim();
                String issuedDate = issuedDateEdit.getText().toString().trim();
                String expiryDate = expiryDateEdit.getText().toString().trim();
//                int percentage = percentageSpinner.getSelectedItemPosition();
                if (license.isEmpty()) {
                    showInfoDlg("Information", "Please enter the License number", "OK", "info");
                } else if (restri.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Restrictions", "OK", "info");
                } else if (issuedDate.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Issue date", "OK", "info");
                } else if (expiryDate.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Expiry date", "OK", "info");
                }
//                else if (percentage == 0) {
//                    showInfoDlg("Information", "Please select the Percentage", "OK", "info");
//                }
                else if (finalmediaFileProfile == null || !finalmediaFileProfile.isFile()) {
                    showInfoDlg("Information", "Please upload profile Image", "OK", "info");
                }
//                else if (finalmediaFileFront == null || !finalmediaFileFront.isFile()) {
//                    showInfoDlg("Information", "Please upload License Front Image", "OK", "info");
//                }else if (finalmediaFileBack == null || !finalmediaFileBack.isFile()) {
//                    showInfoDlg("Information", "Please upload License Back Image", "OK", "info");
//                }
                else {
                    Intent intent = getIntent();
                    String firstname = intent.getStringExtra("firstName");
                    String lastname = intent.getStringExtra("lastName");
                    String emailadd = intent.getStringExtra("emailId");
                    phoneno = intent.getStringExtra("mobileNumber");
                    password = intent.getStringExtra("password");
//                    String vehicleno = intent.getStringExtra("vehicleno");

                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                    String regId = pref.getString("regId", null);
                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "DRIVER";
                    loginCredentials.firstName = firstname;
                    loginCredentials.lastName = lastname;
                    loginCredentials.emailId = emailadd;
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.licenseNo = license;
//                    loginCredentials.vehicleNumber = vehicleno;
                    loginCredentials.licenseValidUntil = expiryDate;
                    loginCredentials.licenseIssuedOn = issuedDate;
                    loginCredentials.alternateNumber = "";
                    loginCredentials.status = "REQUESTED";
//                    loginCredentials.defaultPercentageAccepted = percentageSpinner.getSelectedItem().toString();

                    loginCredentials.deviceToken = regId;
                    Gson gson = new Gson();
                    String json = gson.toJson(loginCredentials);
                    Log.e("json", "" + json);
                    callMobileService(loginCredentials);
                }
            }
        });

        isStoragePermissionGranted();

        uploadImgFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FRONT);
                }
            }
        });

        uploadImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK);
                }
            }
        });

        uploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_PROFILE);
                }
            }
        });

        issuedDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textField = "issued";
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "issueddatePicker");
            }
        });

        expiryDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textField = "expiry";
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "expirydatePicker");
            }
        });
    }

    public SpannableStringBuilder getRedManditoty(String text) {
        String simple = text;
        String colored = " *";
        SpannableStringBuilder builder = new SpannableStringBuilder();

        builder.append(simple);
        int start = builder.length();
        builder.append(colored);
        int end = builder.length();

        builder.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    @Override
    public void onBackPressed() {
        Utils.gpsLocationService.stopUsingGPS();
        finish();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            if (textField.equalsIgnoreCase("issued")) {
                dialog.getDatePicker().setMaxDate(new Date().getTime());
            } else {
                dialog.getDatePicker().setMinDate(new Date().getTime());
            }
            // Create a new instance of DatePickerDialog and return it
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month + 1;
            String mm = "" + month;
            if (("" + month).length() < 2) {
                mm = "0" + month;
            }
            String dd = "" + day;
            if (("" + day).length() < 2) {
                dd = "0" + day;
            }
            if (textField.equalsIgnoreCase("issued")) {
                issuedDateEdit.setText(mm + "-" + dd + "-" + year);
            } else {
                expiryDateEdit.setText(mm + "-" + dd + "-" + year);
            }
        }
    }

    private void getAllNYOPList() {
        if (Utils.connectivity(DocumentUploadActivity.this)) {
            final Dialog dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<LinkedList<ListOfPercentage>> call = apiService.getAllNYOPList(Utils.verifyLogInUserMobileInstantResponse.getAccessToken());
            call.enqueue(new Callback<LinkedList<ListOfPercentage>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfPercentage>> call, Response<LinkedList<ListOfPercentage>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getAllNYOPListInstantResponse = response.body();
                        Log.e("Size", "" + Utils.getAllNYOPListInstantResponse.size());
                        LinkedList<String> percentageList = new LinkedList<String>();
                        percentageList.add("");
//                    percentageList.add("0");
                        for (int i = 0; i < Utils.getAllNYOPListInstantResponse.size(); i++) {
                            percentageList.add("" + Utils.getAllNYOPListInstantResponse.get(i).getPercentage());
                        }
//                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DocumentUploadActivity.this, android.R.layout.simple_spinner_item, percentageList);
//                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    percentageSpinner.setAdapter(dataAdapter);
//                    percentageSpinner.setSelection(0);
                        //showInfoDlg("Success..", "Successfully registered.", "OK", "success");
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "logout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfPercentage>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission", "Permission is granted");
                return true;
            } else {

                Log.v("Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            fileUri = getOutputMediaFileUri();
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            // start the image capture Intent
//            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    public Uri getOutputMediaFileUri() {
        Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getOutputMediaFile());
        return photoURI;
    }

    /**
     * returning image / video
     */
    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Img");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DOCUMENT", "Oops! Failed create "
                        + "Img" + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        finalmediaFile = mediaFile;
        return mediaFile;
    }

    public void insertDriverSession() {
        if (Utils.connectivity(DocumentUploadActivity.this)) {
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.userId = "" + Utils.saveUserMobileInstantResponse.getUserId();
            loginCredentials.fromLatitude = "" + Utils.gpsLocationService.getLatitude();
            loginCredentials.fromLongitude = "" + Utils.gpsLocationService.getLongitude();

            Call<Void> call = apiService.insertDriverSession(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "logout");

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                  //  Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    public void callMobileService(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DocumentUploadActivity.this)) {
            final Dialog dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            final String mobileno = loginCredentials.mobileNumber;
            final String pwd = loginCredentials.password;

            RequestBody userType = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.userType);
            RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.firstName);
            RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.lastName);
            RequestBody emailId = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.emailId);
            RequestBody mobileNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.mobileNumber);
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.password);
            RequestBody licenseNo = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.licenseNo);
//          RequestBody vehicleNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.vehicleNumber);
            RequestBody licenseValidUntil = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.licenseValidUntil);
            RequestBody licenseIssuedOn = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.licenseIssuedOn);
            RequestBody alternateNumber = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.alternateNumber);
            RequestBody status = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.status);
//          RequestBody defaultPercentageAccepted = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.defaultPercentageAccepted);
            RequestBody deviceToken = RequestBody.create(MediaType.parse("text/plain"), loginCredentials.deviceToken);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), finalmediaFileProfile);
            MultipartBody.Part profileBody = MultipartBody.Part.createFormData("userImage", finalmediaFileProfile.getName(), reqFile);

            Call<SingleInstantResponse> call;

            if (finalmediaFileFront == null || !finalmediaFileFront.isFile()) {
                if (finalmediaFileBack == null || !finalmediaFileBack.isFile()) {
                    call = apiService.saveUser(profileBody, userType, firstName, lastName,
                            emailId, mobileNumber, password, licenseNo, licenseValidUntil, licenseIssuedOn, alternateNumber, status, deviceToken);
                } else {
                    reqFile = RequestBody.create(MediaType.parse("image/*"), finalmediaFileBack);
                    MultipartBody.Part backBody = MultipartBody.Part.createFormData("licenseBackImage", finalmediaFileBack.getName(), reqFile);

                    call = apiService.saveUser(profileBody, backBody, userType, firstName, lastName,
                            emailId, mobileNumber, password, licenseNo,
                            licenseValidUntil, licenseIssuedOn, alternateNumber, status, deviceToken);
                }
            } else {
                if (finalmediaFileBack == null || !finalmediaFileBack.isFile()) {
                    reqFile = RequestBody.create(MediaType.parse("image/*"), finalmediaFileFront);
                    MultipartBody.Part frontBody = MultipartBody.Part.createFormData("licenseFrontImage", finalmediaFileFront.getName(), reqFile);

                    call = apiService.saveUser(profileBody, frontBody, userType, firstName, lastName,
                            emailId, mobileNumber, password, licenseNo,
                            licenseValidUntil, licenseIssuedOn, alternateNumber, status, deviceToken);

                } else {
                    reqFile = RequestBody.create(MediaType.parse("image/*"), finalmediaFileFront);
                    MultipartBody.Part frontBody = MultipartBody.Part.createFormData("licenseFrontImage", finalmediaFileFront.getName(), reqFile);

                    reqFile = RequestBody.create(MediaType.parse("image/*"), finalmediaFileBack);
                    MultipartBody.Part backBody = MultipartBody.Part.createFormData("licenseBackImage", finalmediaFileBack.getName(), reqFile);

                    call = apiService.saveUser(profileBody, frontBody, backBody, userType, firstName, lastName,
                            emailId, mobileNumber, password, licenseNo,
                            licenseValidUntil, licenseIssuedOn, alternateNumber, status, deviceToken);
                }
            }

            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.saveUserMobileInstantResponse = response.body();
                        Log.e("licenseFrontImage", "" + Utils.saveUserMobileInstantResponse.getLicenseFrontImage());
                        Log.e("licenseBackImage", "" + Utils.saveUserMobileInstantResponse.getLicenseBackImage());
//                    Gson gson = new Gson();
//                    String json = gson.toJson(Utils.saveUserMobileInstantResponse);
//                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
//                    editor.putString("phoneNumber", phoneno);
//                    editor.putString("password", password);
//                    editor.putString("LoginCredentials", json);
//                    editor.commit();
//                    Utils.verifyLogInUserMobileInstantResponse = Utils.saveUserMobileInstantResponse;
                      //  insertDriverSession();
                       // showInfoDlg("Success..", "Successfully registered.", "OK", "success");
                        SharedPreferences pref = getApplicationContext().getSharedPreferences(Utils.SHARED_PREF, 0);
                        String regId = pref.getString("regId", null);
                        SingleInstantParameters loginCredentials1 = new SingleInstantParameters();
                        loginCredentials1.userType = "DRIVER";
                        loginCredentials1.mobileNumber = mobileno;
                        loginCredentials1.password = pwd;
                        loginCredentials1.deviceToken = regId;
                        loginCredentials1.overrideSessionToken=0;


                        callLoginTogetAccessToken(loginCredentials1);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }


    public void callLoginTogetAccessToken(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(DocumentUploadActivity.this)) {
            final Dialog dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.verifyLogInUser(loginCredentials);
            call.enqueue(new Callback<SingleInstantResponse>() {
                @Override
                public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.verifyLogInUserMobileInstantResponse = response.body();
                        Gson gson = new Gson();
                        String json = gson.toJson(Utils.verifyLogInUserMobileInstantResponse);
                        SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                        editor.putString("phoneNumber", phoneno);
                        editor.putString("password", password);
                        // editor.putString("accesstoken",)
                        editor.putString("LoginCredentials", json);
                        //String s = Utils.verifyLogInUserMobileInstantResponse.getAccessToken();
                        //Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                        editor.commit();
                        //showInfoDlg(getString(R.string.success), getString(R.string.usermsg_successfullogin), getString(R.string.btn_ok), "success");
                        showInfoDlg("Success..", "Successfully registered.", "OK", "success");
                    } else {
                        try {

                            JSONObject jObjError = new JSONObject(response.errorBody().string());

                            if(response.code() == Utils.NETOWRKERR_OVERRIDE_LOGIN){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_yes), "forcelogin");

                            }else {


                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(getString(R.string.error), "" + jObjError.getString("message"), getString(R.string.btn_ok), "error");
                            }
                        } catch (Exception e) {
                            Toast.makeText(DocumentUploadActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    //Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    Toast.makeText(DocumentUploadActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(DocumentUploadActivity.this, getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server") || navType.equalsIgnoreCase("error")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("logout") || navType.equalsIgnoreCase("error") || navType.equalsIgnoreCase("server")) {
            newnegativeBtn.setVisibility(View.GONE);
        }

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent ide = new Intent(DocumentUploadActivity.this, WaitingActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    finish();
                }
            }, 1000);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText("" + title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText("" + content);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if(navType.equalsIgnoreCase("logout")){
                    SharedPreferences.Editor editor = getSharedPreferences("LoginCredentials", MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getSharedPreferences("DisclaimerCredentials", MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(DocumentUploadActivity.this, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(ide);
                    // finish();
                }
            }
        });

        newnegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public String getPathFromURI(Uri contentUri) {
        Cursor mediaCursor = null;
        try {
            String[] dataPath = {MediaStore.Images.Media.DATA};
            mediaCursor = getContentResolver().query(contentUri, dataPath, null, null, null);
            int column_index = mediaCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            mediaCursor.moveToFirst();
            return mediaCursor.getString(column_index);
        } finally {
            if (mediaCursor != null) {
                mediaCursor.close();
            }
        }
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FRONT) {
            if (resultCode == RESULT_OK) {
                if (Utils.connectivity(DocumentUploadActivity.this)) {
                    new ImageCompressionAsyncTask("frontimageFile").execute(finalmediaFile.getAbsolutePath());
                } else {
                    Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK) {
            if (resultCode == RESULT_OK) {
                if (Utils.connectivity(DocumentUploadActivity.this)) {
                    new ImageCompressionAsyncTask("backimageFile").execute(finalmediaFile.getAbsolutePath());
                } else {
                    Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_PROFILE) {
            if (resultCode == RESULT_OK) {
                if (Utils.connectivity(DocumentUploadActivity.this)) {
                    new ImageCompressionAsyncTask("profileimageFile").execute(finalmediaFile.getAbsolutePath());
                } else {
                    Toast.makeText(DocumentUploadActivity.this, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class ImageCompressionAsyncTask extends AsyncTask<String, Void, String> {

        private String fromGallery;
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(DocumentUploadActivity.this);
            pDialog.setMessage(Html.fromHtml("<b>Loading..</b>"));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        public ImageCompressionAsyncTask(String fromGallery) {
            this.fromGallery = fromGallery;
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = compressImage(params[0]);
            return filePath;
        }

        public String compressImage(String imageUri) {

            String filePath = getRealPathFromURI(imageUri);
            Log.v("filePath", "" + filePath);

            File f = new File(filePath);

            if (f.isFile()) {
                Bitmap scaledBitmap = null;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

                int actualHeight = options.outHeight;
                int actualWidth = options.outWidth;
                float maxHeight = 816.0f;
                float maxWidth = 612.0f;
                float imgRatio = actualWidth / actualHeight;
                float maxRatio = maxWidth / maxHeight;

                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight;
                        actualWidth = (int) (imgRatio * actualWidth);
                        actualHeight = (int) maxHeight;
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth;
                        actualHeight = (int) (imgRatio * actualHeight);
                        actualWidth = (int) maxWidth;
                    } else {
                        actualHeight = (int) maxHeight;
                        actualWidth = (int) maxWidth;

                    }
                }

                options.inSampleSize = new ImageLoadingUtils(DocumentUploadActivity.this).calculateInSampleSize(options,
                        actualWidth, actualHeight);
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inTempStorage = new byte[16 * 1024];

                try {
                    bmp = BitmapFactory.decodeFile(filePath, options);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();

                }
                try {
                    scaledBitmap = Bitmap.createBitmap(actualWidth,
                            actualHeight, Bitmap.Config.ARGB_8888);
                } catch (OutOfMemoryError exception) {
                    exception.printStackTrace();
                }

                float ratioX = actualWidth / (float) options.outWidth;
                float ratioY = actualHeight / (float) options.outHeight;
                float middleX = actualWidth / 2.0f;
                float middleY = actualHeight / 2.0f;

                Matrix scaleMatrix = new Matrix();
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

                Canvas canvas = new Canvas(scaledBitmap);
                canvas.setMatrix(scaleMatrix);
                canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY
                        - bmp.getHeight() / 2, new Paint(
                        Paint.FILTER_BITMAP_FLAG));

                ExifInterface exif;
                try {
                    exif = new ExifInterface(filePath);

                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, 0);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                        Log.d("EXIF", "Exif: " + orientation);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                        Log.d("EXIF", "Exif: " + orientation);
                    }
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                            scaledBitmap.getWidth(), scaledBitmap.getHeight(),
                            matrix, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream out = null;
                String filename = getFilename();
                Log.e("filename", "" + filename);
                try {
                    out = new FileOutputStream(filename);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return filename;
            } else {
                return "";
            }
        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = getContentResolver().query(contentUri, null, null,
                    null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
        }

        public String getFilename() {
            //File mFile = new File(ImageConfirmActivity.this.getExternalFilesDir(null), "disashopic.jpg");
            String uriSting = (finalmediaFile.getAbsolutePath());
            return uriSting;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (result.equals("")) {

            } else {
                finalmediaFile = new File(result);
                try {
                    Log.e("fromGallery", "" + fromGallery);
                    InputStream ims = new FileInputStream(finalmediaFile);
                    if (fromGallery.equalsIgnoreCase("frontimageFile")) {
                        finalmediaFileFront = finalmediaFile;
                        uploadImgFront.setImageBitmap(BitmapFactory.decodeStream(ims));
                    } else if (fromGallery.equalsIgnoreCase("profileimageFile")) {
                        finalmediaFileProfile = finalmediaFile;
                        uploadProfilePic.setImageBitmap(BitmapFactory.decodeStream(ims));
                    } else {
                        finalmediaFileBack = finalmediaFile;
                        uploadImgBack.setImageBitmap(BitmapFactory.decodeStream(ims));
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

}
