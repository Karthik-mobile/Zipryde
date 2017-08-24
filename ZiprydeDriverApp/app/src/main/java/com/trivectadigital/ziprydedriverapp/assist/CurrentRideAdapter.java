package com.trivectadigital.ziprydedriverapp.assist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.LoginActivity;
import com.trivectadigital.ziprydedriverapp.OnGoingBookingActivity;
import com.trivectadigital.ziprydedriverapp.R;
import com.trivectadigital.ziprydedriverapp.RideActivity;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hari on 18-06-2017.
 */

public class CurrentRideAdapter extends BaseAdapter {

    public LinkedList<ListOfRequestedBooking> currentRideDetailsList;
    public Context context;
    ZiprydeApiInterface apiService;

    public CurrentRideAdapter(LinkedList<ListOfRequestedBooking> currentRideDetailsList, Context context) {
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        this.currentRideDetailsList = currentRideDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView bookingCRN, bookingDate, bookingStarting, bookingEnding, bookingStatus;
        Button positiveBtn, newnegativeBtn;
    }

    @Override
    public int getCount() {
        return currentRideDetailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.currentzipryde_listitem, parent, false);
            holder = new ViewHolder();

            holder.bookingCRN = (TextView) view.findViewById(R.id.bookingCRN);
            holder.bookingDate = (TextView) view.findViewById(R.id.bookingDate);
            holder.bookingStarting = (TextView) view.findViewById(R.id.bookingStarting);
            holder.bookingEnding = (TextView) view.findViewById(R.id.bookingEnding);
            holder.bookingStatus = (TextView) view.findViewById(R.id.bookingStatus);
            holder.positiveBtn = (Button) view.findViewById(R.id.positiveBtn);
            holder.newnegativeBtn = (Button) view.findViewById(R.id.newnegativeBtn);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ListOfRequestedBooking currentRideDetails = currentRideDetailsList.get(position);

        String crnNumber = currentRideDetails.getCrnNumber();
        String dateTime = currentRideDetails.getBookingDateTime();
        String pickupLocation = currentRideDetails.getFrom();
        String dropLocation = currentRideDetails.getTo();
        String bookingStatus = currentRideDetails.getBookingStatus();

        Calendar calendar = Calendar.getInstance();
        TimeZone zone = calendar.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date dat = sdf.parse(dateTime);
            sdf = new SimpleDateFormat("EEE MMM dd hh:mm a");
            sdf.setTimeZone(zone);
            dateTime = sdf.format(dat);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        holder.bookingCRN.setText("" + crnNumber);
        holder.bookingDate.setText("" + dateTime);
        holder.bookingStarting.setText("" + pickupLocation);
        holder.bookingEnding.setText("" + dropLocation);
        holder.bookingStatus.setText("" + bookingStatus);

        holder.positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
                loginCredentials.bookingId = currentRideDetails.getBookingId();
                loginCredentials.driverStatus = "ACCEPTED";
                Gson gson = new Gson();
                String json = gson.toJson(loginCredentials);
                Log.e("json", "" + json);
                updateBookingDriverStatus(loginCredentials, position);
            }
        });
        holder.newnegativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).finish();
            }
        });

        return view;
    }

    public void updateBookingDriverStatus(final SingleInstantParameters loginCredentials, final int position) {
        if (Utils.connectivity(context)) {
            final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<SingleInstantResponse> call = apiService.updateBookingDriverStatus(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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
                        Utils.updateBookingDriverStatusInstantResponse = response.body();
                        Log.e("CustomerName", "" + Utils.updateBookingDriverStatusInstantResponse.getCustomerName());
                        Log.e("DistanceInMiles", "" + Utils.updateBookingDriverStatusInstantResponse.getGeoLocationResponse().getDistanceInMiles());
                        showInfoDlg("Success..", "Request Accepted Successfully.", "Ok", "success", position);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(response.code() == Utils.NETWORKERR_SESSIONTOKEN_EXPIRED){


                                // JSONObject jObjError = new JSONObject(response.errorBody().string());
                                // Toast.makeText(LoginActivity.this, jObjError.toString(), Toast.LENGTH_LONG).show();
                                //if(jObjError.getString("message"))
                                showInfoDlg(context.getString(R.string.error), "" + jObjError.getString("message"), context.getString(R.string.btn_ok), "logout",position);

                            }else {
                                showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error",position);
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                   // Toast.makeText(context, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                    showInfoDlg(context.getString(R.string.error), "" + context.getString(R.string.errmsg_sessionexpired), context.getString(R.string.btn_ok), "logout",position);
                }
            });
        } else {
            Toast.makeText(context, "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;

    private void showInfoDlg(String title, String content, String btnText, final String navType, final int position) {
        dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        ImageView headerIcon = (ImageView) dialog.findViewById(R.id.headerIcon);
        if (navType.equalsIgnoreCase("server")) {
            headerIcon.setImageResource(R.drawable.erroricon);
        }

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equalsIgnoreCase("error")) {
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
                    Intent ide = new Intent(context, OnGoingBookingActivity.class);
                    ide.putExtra("position", position);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(ide);
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
                if (navType.equalsIgnoreCase("error")) {
                    Intent ide = new Intent(context, RideActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(ide);
                    ((AppCompatActivity) context).finish();
                }else if(navType.equalsIgnoreCase("logout")){
                    SharedPreferences.Editor editor = context.getSharedPreferences("LoginCredentials", context.MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = context.getSharedPreferences("DisclaimerCredentials", context.MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(context, LoginActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(ide);
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

}