package com.trivectadigital.ziprydedriverapp;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiClient;
import com.trivectadigital.ziprydedriverapp.apis.ZiprydeApiInterface;
import com.trivectadigital.ziprydedriverapp.assist.CurrentRideAdapter;
import com.trivectadigital.ziprydedriverapp.assist.MessageReceivedEvent;
import com.trivectadigital.ziprydedriverapp.assist.Utils;
import com.trivectadigital.ziprydedriverapp.modelget.ListOfRequestedBooking;
import com.trivectadigital.ziprydedriverapp.modelget.SingleInstantResponse;
import com.trivectadigital.ziprydedriverapp.modelpost.SingleInstantParameters;

import org.json.JSONObject;

import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
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

    ListView currentride_list;
    RelativeLayout requestLay;
    View requestHighView;

    ZiprydeApiInterface apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        currentride_list = (ListView) view.findViewById(R.id.requestList);

        requestLay = (RelativeLayout) view.findViewById(R.id.requestLay);

        requestHighView = (View) view.findViewById(R.id.requestHighView);

        currentride_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ide = new Intent(getActivity(), OnGoingBookingActivity.class);
                ide.putExtra("position", position);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        Log.e("onCreateView", "onCreateView");
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginCredentials", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
        prefs = getActivity().getSharedPreferences("URLCredentials", getActivity().MODE_PRIVATE);
        String url = prefs.getString("url", null);
        if (url != null) {
            Utils.defaultIP = url;
        }
        Log.e("Utils.defaultIP", "Utils.defaultIP : "+Utils.defaultIP);
        apiService = ZiprydeApiClient.getClient().create(ZiprydeApiInterface.class);
        Log.e("UserId", "UserId " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
        getBookingRequestedByDriverId(loginCredentials);

        return view;
    }

    @Override
    public void onResume() {
        Log.e("onResume", "getActivity onResume");
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginCredentials", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
        Log.e("UserId", "UserId " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        super.onResume();
    }

    @Override
    public void onStart() {
        Log.e("onStart", "getActivity onStart");
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginCredentials", getActivity().MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("LoginCredentials", "");
        Utils.verifyLogInUserMobileInstantResponse = gson.fromJson(json, SingleInstantResponse.class);
        Log.e("UserId", "UserId " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(MessageReceivedEvent messageReceivedEvent) {
        Log.e("onEventMainThread", "RideActivity : " + messageReceivedEvent.message);
        Log.e("PUSH_NOTIFICATION", "RideActivity PUSH_NOTIFICATION");
        Log.e("UserId", "UserId " + Utils.verifyLogInUserMobileInstantResponse.getUserId());
        SingleInstantParameters loginCredentials = new SingleInstantParameters();
        loginCredentials.driverId = "" + Utils.verifyLogInUserMobileInstantResponse.getUserId();
        getBookingRequestedByDriverId(loginCredentials);
    }

    public void getBookingRequestedByDriverId(SingleInstantParameters loginCredentials) {
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

            Call<LinkedList<ListOfRequestedBooking>> call = apiService.getBookingRequestedByDriverId(Utils.verifyLogInUserMobileInstantResponse.getAccessToken(),loginCredentials);
            call.enqueue(new Callback<LinkedList<ListOfRequestedBooking>>() {
                @Override
                public void onResponse(Call<LinkedList<ListOfRequestedBooking>> call, Response<LinkedList<ListOfRequestedBooking>> response) {
                    int statusCode = response.code();
                    Log.e("statusCode", "" + statusCode);
                    Log.e("response.body", "" + response.body());
                    Log.e("response.errorBody", "" + response.errorBody());
                    Log.e("response.isSuccessful", "" + response.isSuccessful());
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        Utils.getBookingRequestedByDriverIdResponse = response.body();
                        Log.e("size", "" + Utils.getBookingRequestedByDriverIdResponse.size());
                        for (int i = 0; i < Utils.getBookingRequestedByDriverIdResponse.size(); i++) {
                            Log.e("BookingId", "" + Utils.getBookingRequestedByDriverIdResponse.get(i).getBookingId());
                            Log.e("CrnNumber", "" + Utils.getBookingRequestedByDriverIdResponse.get(i).getCrnNumber());
                            Log.e("BookingStatus", "" + Utils.getBookingRequestedByDriverIdResponse.get(i).getBookingStatus());
                            Log.e("BookingStatusCode", "" + Utils.getBookingRequestedByDriverIdResponse.get(i).getBookingStatusCode());
                            Log.e("distanceInMiles", "" + Utils.getBookingRequestedByDriverIdResponse.get(i).getGeoLocationResponse().getDistanceInMiles());
                        }
                        CurrentRideAdapter currentRideAdapter = new CurrentRideAdapter(Utils.getBookingRequestedByDriverIdResponse, getActivity());
                        currentride_list.setAdapter(currentRideAdapter);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            showInfoDlg("Error..", "" + jObjError.getString("message"), "OK", "error");
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LinkedList<ListOfRequestedBooking>> call, Throwable t) {
                    // Log error here since request failed
                    Log.e("onFailure", t.toString());
                    dialog.dismiss();
                    showInfoDlg(getString(R.string.error), "" + getString(R.string.errmsg_sessionexpired), getString(R.string.btn_ok), "logout");
                    //Toast.makeText(getActivity(), "Either there is no network connectivity or server is not available.. Please try again later..", Toast.LENGTH_LONG).show();
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
        if (navType.equalsIgnoreCase("server") || (navType.equalsIgnoreCase("logout"))) {
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
