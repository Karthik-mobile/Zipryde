package com.trivectadigital.ziprydedriverapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trivectadigital.ziprydedriverapp.assist.ZiprydeHistoryAdapter;
import com.trivectadigital.ziprydedriverapp.assist.ZiprydeHistoryDetails;

import java.util.LinkedList;


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
    LinkedList<ZiprydeHistoryDetails> ziprydeHistoryDetailsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yourzipryde, container, false);

        history_list = (ListView) view.findViewById(R.id.history_list);

        ziprydeHistoryDetailsList = new LinkedList<ZiprydeHistoryDetails>();
        ziprydeHistoryDetailsList.add(new ZiprydeHistoryDetails("101", "Mini", "Sat, May 26, 18.26", "Mini CRN 70210525", "Airport, Blvd, Austin", "Hyde Park, Austin", "120 $", "Offer 110 $", ""));
        ziprydeHistoryDetailsList.add(new ZiprydeHistoryDetails("102", "Mini", "Sun, May 27, 22.15", "Mini CRN 82154205", "Hyde Park, Austin", "Airport, Blvd, Austin", "190 $", "Offer 160 $", ""));

        ZiprydeHistoryAdapter ziprydeHistoryAdapter = new ZiprydeHistoryAdapter(ziprydeHistoryDetailsList, getActivity());
        history_list.setAdapter(ziprydeHistoryAdapter);

        history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ide = new Intent(getActivity(), ZiprydeBookingDetailsActivity.class);
                ide.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ide);
            }
        });

        return view;
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
