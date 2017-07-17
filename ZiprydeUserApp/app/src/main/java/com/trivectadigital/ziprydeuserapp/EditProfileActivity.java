package com.trivectadigital.ziprydeuserapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trivectadigital.ziprydeuserapp.assist.Utils;

public class EditProfileActivity extends AppCompatActivity {

    EditText firstnameEdit, lastnameEdit, emailaddEdit, mobileEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_titleback, null);
        toolbar.setContentInsetsAbsolute(0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        toolbar.addView(mCustomView, layoutParams);
        TextView titleText = (TextView) mCustomView.findViewById(R.id.titleText);
        titleText.setText("Edit Profile");
        ImageView backImg = (ImageView) mCustomView.findViewById(R.id.backImg);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firstnameEdit = (EditText) findViewById(R.id.firstnameEdit);
        firstnameEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getFirstName());

        lastnameEdit = (EditText) findViewById(R.id.lastnameEdit);
        lastnameEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getLastName());

        emailaddEdit = (EditText) findViewById(R.id.emailaddEdit);
        emailaddEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getEmailId());

        mobileEdit = (EditText) findViewById(R.id.mobileEdit);
        mobileEdit.setText(""+ Utils.verifyLogInUserMobileInstantResponse.getMobileNumber());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}