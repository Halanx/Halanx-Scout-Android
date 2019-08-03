package com.technicalrj.halanxscouts.Home.MoveOut.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.technicalrj.halanxscouts.Adapters.AmenitiesAdapter;
import com.technicalrj.halanxscouts.Home.MoveOut.AmenitiesResponse;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AmenitiesFragment extends Fragment implements AmenitiesAdapter.OnAmenityCheckedListener {


    private RecyclerView amenitiesRecycler;
    private AmenitiesAdapter amenitiesAdapter;
    private Button done_button;
    private TextView backTextView;
    private CardView rootCardView;
    private TextView noAmenitiesTextView;

    private OnAmenitiesInteractionListener listener;
    private int taskId;
    private String key;

    private PropertyDetailsFragment.OnPropertyDetailsInteractionListener refreshTaskListListener;

    private ArrayList<AmenitiesResponse.Amenity> amenityArrayList;

    private RetrofitAPIClient.DataInterface dataInterface;
    private String TAG = AmenitiesFragment.class.getSimpleName();

    private AmenitiesResponse amenitiesResponse;

    public static AmenitiesFragment newInstance(int taskId) {

        Bundle args = new Bundle();
        args.putInt("id", taskId);
        AmenitiesFragment fragment = new AmenitiesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskId = getArguments().getInt("id");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amenities, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        rootCardView = view.findViewById(R.id.root_card_view);
        done_button   = view.findViewById(R.id.done_button);
        backTextView = view.findViewById(R.id.cancel_action);
        amenitiesRecycler = view.findViewById(R.id.amenities_recyclerview);
        noAmenitiesTextView = view.findViewById(R.id.no_amenities_text_view);

        amenityArrayList = new ArrayList<>();
        amenitiesAdapter = new AmenitiesAdapter(getActivity(), amenityArrayList, this);
        amenitiesRecycler.setAdapter(amenitiesAdapter);
        amenitiesRecycler.setNestedScrollingEnabled(false);
        amenitiesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onLoadRemarksClicked(amenitiesResponse.getAmenityJsonData());
                }
            }
        });

        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onAmenitiesBackPressed();
                }
            }
        });

        dataInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);

        fetchDetails();

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


    private void fetchDetails(){

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        rootCardView.setVisibility(View.INVISIBLE);

        Log.d(TAG, "fetchDetails: "+taskId);

        dataInterface.getListOfAmenities("Token "+key, taskId)
                .enqueue(new Callback<AmenitiesResponse>() {
                    @Override
                    public void onResponse(Call<AmenitiesResponse> call, Response<AmenitiesResponse> response) {
                        progressDialog.dismiss();
                        if(response.body() != null){
                            amenitiesResponse = response.body();
                            rootCardView.setVisibility(View.VISIBLE);
                            amenityArrayList.addAll(amenitiesResponse.getListOfAmenity());

                            if(amenityArrayList.size() > 0) {
                                noAmenitiesTextView.setVisibility(View.GONE);

                                for (int i = 0; i < amenityArrayList.size(); i++) {
                                    AmenitiesResponse.Amenity amenity = amenityArrayList.get(i);
                                    amenity.setStatus(AmenitiesResponse.STATUS_NOT_SELECTED);
                                    amenityArrayList.set(i, amenity);
                                }
                                amenitiesAdapter.notifyDataSetChanged();
                                if (checkIfAllAmenitiesSelected()) {
                                    enableButton(true);
                                }
                            } else {
                                noAmenitiesTextView.setVisibility(View.VISIBLE);
                                enableButton(true);
                            }
                        } else {
                            rootCardView.setVisibility(View.INVISIBLE);
                            showErrorDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<AmenitiesResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        rootCardView.setVisibility(View.INVISIBLE);
                        Log.d(TAG, "onFailure: ");
                        t.printStackTrace();
                        showErrorDialog();
                    }
                });
    }

    private void showErrorDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setCancelable(false)
                .setMessage("Couldn't load data!")
                .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        fetchDetails();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if(refreshTaskListListener != null){
                            refreshTaskListListener.onRefreshTaskList();
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof PropertyDetailsFragment.OnPropertyDetailsInteractionListener){
            refreshTaskListListener = (PropertyDetailsFragment.OnPropertyDetailsInteractionListener) context;
        }

        if(context instanceof OnAmenitiesInteractionListener){
            listener = (OnAmenitiesInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        refreshTaskListListener = null;
    }

    @Override
    public void onAmenityChecked(final RadioGroup radioGroup, final int radioButtonId, final View rootView) {
        int position = amenitiesRecycler.getChildAdapterPosition(rootView);
        AmenitiesResponse.Amenity amenity = amenityArrayList.get(position);
        if(radioButtonId == R.id.ok1){
            amenity.setStatus(AmenitiesResponse.STATUS_OK);
        } else if(radioButtonId == R.id.damaged1){
            amenity.setStatus(AmenitiesResponse.STATUS_DAMAGED);
        } else if(radioButtonId == R.id.missing1){
            amenity.setStatus(AmenitiesResponse.STATUS_MISSING);
        } else {
            amenity.setStatus(AmenitiesResponse.STATUS_NOT_SELECTED);
        }

        if(checkIfAllAmenitiesSelected()){
            enableButton(true);
        }

    }

    private boolean checkIfAllAmenitiesSelected(){
        for(AmenitiesResponse.Amenity amenity : amenityArrayList){
            if(amenity.getStatus() == null || amenity.getStatus().equalsIgnoreCase("") ||
                amenity.getStatus().equalsIgnoreCase(AmenitiesResponse.STATUS_NOT_SELECTED)){
                return false;
            }
        }
        return true;
    }

    public interface OnAmenitiesInteractionListener{

        void onLoadRemarksClicked(AmenitiesResponse.AmenityJsonData amenityJsonData);

        void onAmenitiesBackPressed();
    }
}
