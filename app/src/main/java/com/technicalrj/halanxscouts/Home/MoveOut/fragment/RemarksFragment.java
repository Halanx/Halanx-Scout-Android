package com.technicalrj.halanxscouts.Home.MoveOut.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.technicalrj.halanxscouts.R;

public class RemarksFragment extends Fragment {

    private EditText remarks;
    private Button done_button;

    public static RemarksFragment newInstance() {

        Bundle args = new Bundle();

        RemarksFragment fragment = new RemarksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remarks, container, false);
        remarks = view.findViewById(R.id.remarks);
        done_button   = view.findViewById(R.id.done_button);


        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().equals("")){
                    enableButton(false);
                }else {
                    enableButton(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void enableButton(boolean val){

        if(val){
            done_button.setEnabled(true);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape));
        }else {
            done_button.setEnabled(false);
            done_button.setBackground(getResources().getDrawable(R.drawable.button_shape_dark_grey));

        }

    }

    public void submitRemark(View view) {

//        String rem = remarks.getText().toString();
//
//        startActivity(new Intent(this, HomeActivity.class));
//        finishAffinity();

    }

    public void cancelTask(View view) {
    }
}
