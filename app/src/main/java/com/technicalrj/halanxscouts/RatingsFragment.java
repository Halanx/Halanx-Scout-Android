package com.technicalrj.halanxscouts;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.ReviewTag;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RatingsFragment extends Fragment {

    String key;
    GridView gridView;

    public RatingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ratings");
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        gridView = view.findViewById(R.id.things_to_improve);
        final TextView rating = view.findViewById(R.id.rating);
        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call = availabilityInterface.getProfile("Token "+key);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){

                    Profile profile = response.body();
                    rating.setText(profile.getRating()+"");
                    ;

                    ArrayList<ReviewTag> reviewTags = (ArrayList<ReviewTag>) profile.getReviewTags();
                    String[] strings = new String[reviewTags.size()];
                    int i=0;
                    for (ReviewTag review :reviewTags) {
                        strings[i] = review.getName();
                    }
                    ArrayAdapter<String > adapter = new ArrayAdapter<String>(getActivity(), R.layout.ratings_row, R.id.tag,strings);
                    gridView.setAdapter(adapter);


                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                progressDialog.dismiss();
            }
        });







        return view;

    }

}
