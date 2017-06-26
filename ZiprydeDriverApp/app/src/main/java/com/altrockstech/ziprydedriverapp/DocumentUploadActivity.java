package com.altrockstech.ziprydedriverapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.altrockstech.ziprydedriverapp.apis.ZiprydeApiClient;
import com.altrockstech.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.altrockstech.ziprydedriverapp.assist.ImageLoadingUtils;
import com.altrockstech.ziprydedriverapp.assist.Utils;
import com.altrockstech.ziprydedriverapp.modelget.ListOfPercentage;
import com.altrockstech.ziprydedriverapp.modelget.SingleInstantResponse;
import com.altrockstech.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentUploadActivity extends AppCompatActivity {

    Button continueBtn;
    ZiprydeApiInterface apiService;
    EditText licenseEdit, restriEdit, issuedDateEdit, expiryDateEdit;
    ImageView uploadImgFront, uploadImgBack;
    Spinner percentageSpinner;

    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FRONT = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK = 101;
    File finalmediaFile;

    File finalmediaFileFront, finalmediaFileBack;

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
        titleText.setText("Upload Driver License");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        continueBtn = (Button) findViewById(R.id.continueBtn);

        licenseEdit = (EditText) findViewById(R.id.licenseEdit);
        restriEdit = (EditText) findViewById(R.id.restriEdit);
        issuedDateEdit = (EditText) findViewById(R.id.issuedDateEdit);
        expiryDateEdit = (EditText) findViewById(R.id.expiryDateEdit);

        uploadImgFront = (ImageView) findViewById(R.id.uploadImgFront);
        uploadImgBack = (ImageView) findViewById(R.id.uploadImgBack);

        percentageSpinner = (Spinner) findViewById(R.id.percentageSpinner);
        getAllNYOPList();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String license = licenseEdit.getText().toString().trim();
                String restri = restriEdit.getText().toString().trim();
                String issuedDate = issuedDateEdit.getText().toString().trim();
                String expiryDate = expiryDateEdit.getText().toString().trim();
                if (license.isEmpty()) {
                    showInfoDlg("Information", "Please enter the License number", "Ok", "info");
                } else if (restri.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Restrictions", "Ok", "info");
                }else if (issuedDate.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Issue date", "Ok", "info");
                }else if (expiryDate.isEmpty()) {
                    showInfoDlg("Information", "Please enter the Expiry date", "Ok", "info");
                }else {
                    Intent intent = getIntent();
                    String firstname = intent.getStringExtra("firstName");
                    String lastname = intent.getStringExtra("lastName");
                    String emailadd = intent.getStringExtra("emailId");
                    String phoneno = intent.getStringExtra("mobileNumber");
                    String password = intent.getStringExtra("password");
                    String vehicleno = intent.getStringExtra("vehicleno");

                    SingleInstantParameters loginCredentials = new SingleInstantParameters();
                    loginCredentials.userType = "DRIVER";
                    loginCredentials.firstName = firstname;
                    loginCredentials.lastName = lastname;
                    loginCredentials.emailId = emailadd;
                    loginCredentials.mobileNumber = phoneno;
                    loginCredentials.password = password;
                    loginCredentials.licenseNo = license;
                    loginCredentials.vehicleNumber = vehicleno;
                    loginCredentials.licenseValidUntil = expiryDate;
                    loginCredentials.licenseIssuedOn = issuedDate;
                    loginCredentials.alternateNumber = "";
                    loginCredentials.status = "REQUESTED";
                    loginCredentials.defaultPercentageAccepted = percentageSpinner.getSelectedItem().toString();
                    callMobileService(loginCredentials);
                }
            }
        });

        isStoragePermissionGranted();

        uploadImgFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
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
                if(isStoragePermissionGranted()){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    fileUri = getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK);
                }
            }
        });
    }

    private void getAllNYOPList(){
        final Dialog dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<LinkedList<ListOfPercentage>> call = apiService.getAllNYOPList();
        call.enqueue(new Callback<LinkedList<ListOfPercentage>>() {
            @Override
            public void onResponse(Call<LinkedList<ListOfPercentage>> call, Response<LinkedList<ListOfPercentage>> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.getAllNYOPListInstantResponse = response.body();
                    Log.e("Size",""+ Utils.getAllNYOPListInstantResponse.size());
                    LinkedList<String> percentageList = new LinkedList<String>();
                    percentageList.add("0");
                    for(int i = 0; i < Utils.getAllNYOPListInstantResponse.size(); i++){
                        percentageList.add(""+Utils.getAllNYOPListInstantResponse.get(i).getPercentage());
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(DocumentUploadActivity.this, android.R.layout.simple_spinner_item, percentageList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    percentageSpinner.setAdapter(dataAdapter);
                    percentageSpinner.setSelection(0);
                    //showInfoDlg("Success..", "Successfully registered.", "Ok", "success");
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", ""+jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<LinkedList<ListOfPercentage>> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission","Permission is granted");
                return true;
            } else {

                Log.v("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Permission","Permission: "+permissions[0]+ "was "+grantResults[0]);
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

    public void callMobileService(SingleInstantParameters loginCredentials) {
        final Dialog dialog = new Dialog(DocumentUploadActivity.this, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.saveUser(loginCredentials);
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
                    showInfoDlg("Success..", "Successfully registered.", "Ok", "success");
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
                    } catch (Exception e) {
                        showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("onFailure", t.toString());
                dialog.dismiss();
                showInfoDlg("Error..", "Either there is no network connectivity or server is not available.. Please try again later..", "Ok", "server");
            }
        });
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
        if (navType.equalsIgnoreCase("info") || navType.equalsIgnoreCase("error")) {
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
            String[] dataPath = { MediaStore.Images.Media.DATA };
            mediaCursor = getContentResolver().query(contentUri,  dataPath, null, null, null);
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
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FRONT) {
            if (resultCode == RESULT_OK) {
                new ImageCompressionAsyncTask("frontimageFile").execute(finalmediaFile.getAbsolutePath());
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

        }else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_BACK) {
            if (resultCode == RESULT_OK) {
                new ImageCompressionAsyncTask("backimageFile").execute(finalmediaFile.getAbsolutePath());
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
                Log.e("filename",""+filename);
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
            if(result.equals("")){

            }else{
                finalmediaFile = new File(result);
                try {
                    Log.e("fromGallery",""+fromGallery);
                    InputStream ims = new FileInputStream(finalmediaFile);
                    if(fromGallery.equalsIgnoreCase("frontimageFile")) {
                        finalmediaFileFront = finalmediaFile;
                        uploadImgFront.setImageBitmap(BitmapFactory.decodeStream(ims));
                    }else {
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
