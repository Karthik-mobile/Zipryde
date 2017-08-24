package com.trivectadigital.ziprydeuserapp.assist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Base64;
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

import com.trivectadigital.ziprydeuserapp.NavigationMenuActivity;
import com.trivectadigital.ziprydeuserapp.R;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hari on 09-06-2017.
 */

public class ZiprydeHistoryAdapter extends BaseAdapter {

    LinkedList<ListOfBooking> ziprydeHistoryDetailsList;
    public Context context;
    ZiprydeApiInterface apiService;

    public ZiprydeHistoryAdapter(LinkedList<ListOfBooking> ziprydeHistoryDetailsList, Context context) {
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        this.ziprydeHistoryDetailsList = ziprydeHistoryDetailsList;
        this.context = context;
    }

    private class ViewHolder {
        TextView bookingDatetime, bookingCRN, bookingStarting, bookingEnding, bookingPrice, bookingOfferPrice, bookingStatus;
        ImageView bookingCarType, cancelBookingImg;
        CircleImageView bookingProfileImg;
    }

    @Override
    public int getCount() {
        return ziprydeHistoryDetailsList.size();
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
            view = li.inflate(R.layout.yourzipryde_listitem, parent, false);
            holder = new ViewHolder();

            holder.bookingDatetime = (TextView) view.findViewById(R.id.bookingDatetime);
            holder.bookingCRN = (TextView) view.findViewById(R.id.bookingCRN);
            holder.bookingStarting = (TextView) view.findViewById(R.id.bookingStarting);
            holder.bookingEnding = (TextView) view.findViewById(R.id.bookingEnding);
            holder.bookingPrice = (TextView) view.findViewById(R.id.bookingPrice);
            holder.bookingOfferPrice = (TextView) view.findViewById(R.id.bookingOfferPrice);

            holder.bookingCarType = (ImageView) view.findViewById(R.id.bookingCarType);
            holder.bookingProfileImg = (CircleImageView) view.findViewById(R.id.bookingProfileImg);
            holder.cancelBookingImg = (ImageView) view.findViewById(R.id.cancelBookingImg);
            holder.bookingStatus = (TextView) view.findViewById(R.id.bookingStatus);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ListOfBooking ziprydeHistoryDetails = ziprydeHistoryDetailsList.get(position);

//        String ziprydeBookingCarType = ziprydeHistoryDetails.getZiprydeBookingCarType();
        String ziprydeBookingDateTime = ziprydeHistoryDetails.getBookingDateTime();
        String ziprydeBookingCRN = ziprydeHistoryDetails.getCrnNumber();
        String ziprydeBookingStarting = ziprydeHistoryDetails.getFrom();
        String ziprydeBookingEnding = ziprydeHistoryDetails.getTo();
        String ziprydeBookingPrice = ziprydeHistoryDetails.getSuggestedPrice();
        String ziprydeBookingOfferPrice = ziprydeHistoryDetails.getOfferedPrice();
        String ziprydeBookingProfileImg = "" + ziprydeHistoryDetails.getDriverImage();

        holder.bookingDatetime.setText("" + ziprydeBookingDateTime);
        holder.bookingCRN.setText("" + ziprydeBookingCRN);
        holder.bookingStarting.setText("" + ziprydeBookingStarting);
        holder.bookingEnding.setText("" + ziprydeBookingEnding);
        holder.bookingPrice.setText(context.getString(R.string.currencysymbol) + ziprydeBookingPrice);
        holder.bookingOfferPrice.setText(context.getString(R.string.offer)+context.getString(R.string.currencysymbol)+" "+ ziprydeBookingOfferPrice);

        holder.cancelBookingImg.setVisibility(View.GONE);

        if (ziprydeBookingProfileImg != null) {
            if (!ziprydeBookingProfileImg.equalsIgnoreCase("null")) {
                byte[] decodedString = Base64.decode(ziprydeBookingProfileImg, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.bookingProfileImg.setImageBitmap(decodedByte);
            } else {
                holder.bookingProfileImg.setImageResource(R.drawable.person);
            }
        }

        String bookingStatus = ziprydeHistoryDetails.getBookingStatus();
        holder.bookingStatus.setText("" + bookingStatus);
        Log.e("bookingStatus", "" + bookingStatus);
        if (bookingStatus != null) {
            if (bookingStatus.equals("SCHEDULED")) {
                holder.cancelBookingImg.setVisibility(View.VISIBLE);
            }
        }

        holder.cancelBookingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleInstantParameters loginCredentials = new SingleInstantParameters();
                loginCredentials.bookingId = ziprydeHistoryDetails.getBookingId();
                loginCredentials.bookingStatus = "CANCELLED";
                updateBookingStatus(loginCredentials);
            }
        });

        return view;
    }

    private void updateBookingStatus(SingleInstantParameters loginCredentials) {
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

            Call<SingleInstantResponse> call = apiService.updateBookingStatus(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
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
                        Utils.updateBookingStatusInstantResponse = response.body();
                        Log.e("BookingStatus", "" + Utils.updateBookingStatusInstantResponse.getBookingStatus());
                        showInfoDlg(context.getString(R.string.success), context.getString(R.string.usermsg_bookingcancelled), context.getString(R.string.btn_ok), "success");
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "Ok", "error");
                        } catch (Exception e) {
                            Toast.makeText(context, context.getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<SingleInstantResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    Toast.makeText(context, context.getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(context, context.getString(R.string.errmsg_network_noconnection), Toast.LENGTH_LONG).show();
        }
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
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);

        if (navType.equalsIgnoreCase("success")) {
            headerIcon.setImageResource(R.drawable.successicon);
            positiveBtn.setVisibility(View.GONE);
            newnegativeBtn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Intent ide = new Intent(context, NavigationMenuActivity.class);
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
