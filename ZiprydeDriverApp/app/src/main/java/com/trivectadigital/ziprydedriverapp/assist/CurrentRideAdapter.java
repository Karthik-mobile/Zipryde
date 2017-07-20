package com.trivectadigital.ziprydedriverapp.assist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.provider.Settings;
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

import com.trivectadigital.ziprydedriverapp.CashDisplyActivity;
import com.trivectadigital.ziprydedriverapp.NewDashBoardActivity;
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
import java.util.Date;
import java.util.LinkedList;

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
        TextView bookingCRN, bookingDate, bookingStarting, bookingEnding;
        Button positiveBtn;
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
            holder.positiveBtn = (Button) view.findViewById(R.id.positiveBtn);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ListOfRequestedBooking currentRideDetails = currentRideDetailsList.get(position);

        String crnNumber = currentRideDetails.getCrnNumber();
        String dateTime = currentRideDetails.getBookingDateTime();
        String pickupLocation = currentRideDetails.getFrom();
        String dropLocation = currentRideDetails.getTo();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        try {
            Date dat = sdf.parse(dateTime);
            sdf = new SimpleDateFormat("EEE MMM dd HH:mm");
            dateTime = sdf.format(dat);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        holder.bookingCRN.setText("" + crnNumber);
        holder.bookingDate.setText("" + dateTime);
        holder.bookingStarting.setText("" + pickupLocation);
        holder.bookingEnding.setText("" + dropLocation);

        holder.positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.driverId = ""+Utils.verifyLogInUserMobileInstantResponse.getUserId();
                loginCredentials.bookingId = currentRideDetails.getBookingId();
                loginCredentials.driverStatus = "ACCEPTED";
                updateBookingDriverStatus(loginCredentials);
            }
        });

        return view;
    }

    public void updateBookingDriverStatus(final SingleInstantParameters loginCredentials){
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loadingimage_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        Call<SingleInstantResponse> call = apiService.updateBookingDriverStatus(loginCredentials);
        call.enqueue(new Callback<SingleInstantResponse>() {
            @Override
            public void onResponse(Call<SingleInstantResponse> call, Response<SingleInstantResponse> response) {
                int statusCode = response.code();
                Log.e("statusCode",""+statusCode);
                Log.e("response.body",""+response.body());
                Log.e("response.errorBody",""+response.errorBody());
                Log.e("response.isSuccessful",""+response.isSuccessful());
                dialog.dismiss();
                if(response.isSuccessful()){
                    Utils.updateBookingDriverStatusInstantResponse = response.body();
                    Log.e("CustomerName",""+Utils.updateBookingDriverStatusInstantResponse.getCustomerName());
                    Log.e("DistanceInMiles",""+Utils.updateBookingDriverStatusInstantResponse.getGeoLocationResponse().getDistanceInMiles());
                    showInfoDlg("Success..", "Request Accepted Successfully.", "Ok", "success");
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
        positiveBtn.setText(""+btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent ide = new Intent(context, RideActivity.class);
                    ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(ide);
                }
            }, 1000);
        }

        TextView dialogtitleText = (TextView) dialog.findViewById(R.id.dialogtitleText);
        dialogtitleText.setText(""+title);
        TextView dialogcontentText = (TextView) dialog.findViewById(R.id.dialogcontentText);
        dialogcontentText.setText(""+content);

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

}