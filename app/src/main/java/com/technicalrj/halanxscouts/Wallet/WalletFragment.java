package com.technicalrj.halanxscouts.Wallet;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends Fragment {


    TextView earning,paid,due;
    String key;

    public WalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Wallet");
        View view  = inflater.inflate(R.layout.fragment_wallet, container, false);

        earning = view.findViewById(R.id.earning);
        paid = view.findViewById(R.id.paid);
        due = view.findViewById(R.id.due);


        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);




        updatePayment();


        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(),PaidActivity.class);
                startActivity(intent);


            }
        });

        due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DueActivity.class);
                startActivity(intent);
            }
        });



        return view;

    }

    private void updatePayment() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();


        RetrofitAPIClient.DataInterface dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<JsonObject> call1 = dataInterface.getWallet("Token "+key);


        call1.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JsonObject jsonObject = response.body();
                Log.i("InfoText","inside onResponse "+jsonObject.toString());

                double credit =  jsonObject.get("credit").getAsDouble();
                double pending_withdrawal =  jsonObject.get("pending_withdrawal").getAsDouble();
                double debit =  jsonObject.get("debit").getAsDouble();

                earning.setText(""+credit);
                paid.setText(""+pending_withdrawal);
                due.setText(""+debit);

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Log.i("InfoText","inside onFailure "+t.getMessage());
                progressDialog.dismiss();
            }
        });

    }

}
