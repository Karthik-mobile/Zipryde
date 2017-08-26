package com.trivectadigital.ziprydeuserapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydeuserapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydeuserapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydeuserapp.assist.Utils;
import com.trivectadigital.ziprydeuserapp.assist.ZiprydeHistoryAdapter;
import com.trivectadigital.ziprydeuserapp.modelget.ListOfBooking;
import com.trivectadigital.ziprydeuserapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YourZiprydeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YourZiprydeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YourZiprydeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public YourZiprydeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YourZiprydeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YourZiprydeFragment newInstance(String param1, String param2) {
        YourZiprydeFragment fragment = new YourZiprydeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ListView history_list;
    ZiprydeApiInterface apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yourzipryde, container, false);

        apiService = ZiprydeApiClient.getClient(Utils.verifyLogInUserMobileInstantResponse.getAccessToken()).create(ZiprydeApiInterface.class);
        history_list = (ListView) view.findViewById(R.id.history_list);

        history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListOfBooking listOfBooking = Utils.getBookingByUserIdResponse.get(position);
                String driverStatus = listOfBooking.getDriverStatusCode();
                String bookingStatus = listOfBooking.getBookingStatusCode();
                if (driverStatus != null) {
                    if (bookingStatus.equals("CANCELLED")) {
                        showInfoDlg(getString(R.string.information),getString(R.string.usermsg_bookingcancelled), getString(R.string.btn_ok), "info");
                    } else if (driverStatus.equals("REQUESTED")) {
                        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_bookingsuccessfull_waitdriver), getString(R.string.btn_ok), "info");
                    } else {
                        Intent ide = new Intent(getActivity(), DriverInfoBookingActivity.class);
                        ide.putExtra("position", position);
                        ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(ide);
                        getActivity().finish();
                    }
                } else {
                    if (bookingStatus.equals("CANCELLED")) {
                        showInfoDlg(getString(R.string.information),getString(R.string.usermsg_bookingcancelled), getString(R.string.btn_ok), "info");
                    } else {
                        showInfoDlg(getString(R.string.information), getString(R.string.usermsg_bookingsuccessfull_waitdriver), getString(R.string.btn_ok), "info");
                    }
                }
            }
        });

        Log.e("UserId", "" + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.customerId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
        getBookingByUserId(loginCredentials);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("onEventMainThread", "" + messageReceivedEvent.message);
        Log.e("Thread title", "" + messageReceivedEvent.title);
        Log.e("PUSH_NOTIFICATION", "PUSH_NOTIFICATION");
        Log.e("UserId", "" + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        if (messageReceivedEvent.title.equals("BOOKING_CANCELLED")) {
            SingleInstantParameters loginCredentials = new SingleInstantParameters();
            loginCredentials.customerId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
            getBookingByUserId(loginCredentials);
        }else if(messageReceivedEvent.title.equals("BOOKING_DRIVER_ONTRIP") || messageReceivedEvent.title.equals("BOOKING_DRIVER_ONSITE"))
        {

            Intent ide = new Intent(getActivity(), DriverInfoBookingActivity.class);
            //Get the booking id from the message
            String str = messageReceivedEvent.message;
            String[] splitStr = str.split("\\s+");
            ide.putExtra("bookingId", splitStr[splitStr.length-1]);
            ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ide);

        }
    }

    public void getBookingByUserId(SingleInstantParameters loginCredentials) {
        if (Utils.connectivity(getActivity())) {
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.loadingimage_layout);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();

            Call<LinkedList<ListOfBooking>> call = apiService.getBookingByUserId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<LinkedList<ListOfBooking>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfBooking>> call, Response<LinkedList<ListOfBooking>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getBookingByUserIdResponse = response.body();
                        Log.e("size", "" + Utils.getBookingByUserIdResponse.size());
                        for (int i = 0; i < Utils.getBookingByUserIdResponse.size(); i++) {
                            Log.e("BookingId", "" + Utils.getBookingByUserIdResponse.get(i).getBookingId());
                            Log.e("CrnNumber", "" + Utils.getBookingByUserIdResponse.get(i).getCrnNumber());
                            Log.e("driverImage", "driverImage : " + Utils.getBookingByUserIdResponse.get(i).getDriverImage());
                            Log.e("distanceInMiles", "" + Utils.getBookingByUserIdResponse.get(i).getGeoLocationResponse().getDistanceInMiles());
                        }
                        ZiprydeHistoryAdapter ziprydeHistoryAdapter = new ZiprydeHistoryAdapter(Utils.getBookingByUserIdResponse, getActivity());
                        history_list.setAdapter(ziprydeHistoryAdapter);
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
                            Toast.makeText(getActivity(), "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfBooking>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();

                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");

                   // Toast.makeText(getActivity(), "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
        }
    }

    Dialog dialog;
    private void showInfoDlg(String title, String content, String btnText, final String navType) {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.infodialog_layout);
        //dialog.setCanceledOnTouchOutside(true);

        Button positiveBtn = (Button) dialog.findViewById(R.id.positiveBtn);
        positiveBtn.setText("" + btnText);

        Button newnegativeBtn = (Button) dialog.findViewById(R.id.newnegativeBtn);
        if (navType.equals("info") || navType.equalsIgnoreCase("server")|| navType.equalsIgnoreCase("logout")) {
            newnegativeBtn.setVisibility(View.GONE);
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
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginCredentials", getActivity().MODE_PRIVATE).edit();
                    editor.remove("phoneNumber");
                    editor.remove("password");
                    editor.commit();
                    SharedPreferences.Editor deditor = getActivity().getSharedPreferences("DisclaimerCredentials", getActivity().MODE_PRIVATE).edit();
                    deditor.putString("disclaimer", "");
                    deditor.commit();
                    Intent ide = new Intent(getActivity(), LoginActivity.class);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
