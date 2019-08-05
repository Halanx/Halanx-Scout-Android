package com.technicalrj.halanxscouts.Home.MoveOut.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.technicalrj.halanxscouts.Home.ChatWindow;
import com.technicalrj.halanxscouts.Home.TaskActivity;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Home.TaskFolder.SubTask;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PropertyDetailsFragment extends Fragment {

    private Button done_button;
    private OnPropertyDetailsInteractionListener listener;

    private int taskId;
    private String key;
    private ScheduledTask scheduledTask;

    private TextView date;
    private TextView time;
    private TextView address1;
    private TextView address2;
    private TextView address3;
    private TextView customer_name;
    private ImageView house_pic;
    private ImageView customer_img;
    private CardView customer_card;
    private String firstName,lastName;
    private CardView rootCardView;

    private RetrofitAPIClient.DataInterface availabilityInterface;

    public PropertyDetailsFragment() {
        // Required empty public constructor
    }

    public static PropertyDetailsFragment newInstance(int taskId) {
        
        Bundle args = new Bundle();
        args.putInt("id", taskId);
        PropertyDetailsFragment fragment = new PropertyDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskId = getArguments().getInt("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_property_details, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);

        rootCardView = view.findViewById(R.id.root_card_view);
        done_button   = view.findViewById(R.id.done_button);
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        house_pic = view.findViewById(R.id.house_pic);
        address1 = view.findViewById(R.id.address1);
        address2 = view.findViewById(R.id.address2);
        address3 = view.findViewById(R.id.address3);
        customer_name = view.findViewById(R.id.customer_name);
        customer_img = view.findViewById(R.id.customer_img);
        customer_card = view.findViewById(R.id.customer_card);
        TextView cancelTextView = view.findViewById(R.id.cancel_action);
        TextView reminderTextView = view.findViewById(R.id.reminder);
        ImageView callImageView = view.findViewById(R.id.call_image_view);
        ImageView chatImageView = view.findViewById(R.id.chat_image_view);
        ImageView houseLocationImageView = view.findViewById(R.id.house_location_image_view);
        TextView moreInfoTextView = view.findViewById(R.id.more_info_text_view);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    enableButton(true);
                }else {
                    enableButton(false);
                }
            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onCheckAmenitiesClicked();
                }
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTask();
            }
        });

        reminderTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setReminder();
            }
        });

        chatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatCustomer();
            }
        });

        callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callCustomer();
            }
        });

        houseLocationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                houseLocation();
            }
        });

        moreInfoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreInfo();
            }
        });

        availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        fetchDetails();

        return view;
    }


    private void updateTaskDetails() {


        String[] parts = scheduledTask.getScheduledAt().split(" ") ;
        date.setText(parts[0]+" "+parts[1].substring(0,3).toUpperCase());
        time.setText( parts[3]+" "+parts[4]);

        if(listener != null){
            listener.setAmount(scheduledTask.getEarning());
        }


        if( scheduledTask.getHouse().getCoverPicUrl()!=null )
            Picasso.get().load( scheduledTask.getHouse().getCoverPicUrl().toString()).into(house_pic);


        address1.setText(scheduledTask.getHouse().getAddress().getStreetAddress());
        address2.setText(scheduledTask.getHouse().getAddress().getCity() +", "+ scheduledTask.getHouse().getAddress().getState());
        address3.setText(scheduledTask.getHouse().getAddress().getPincode());

        //if not previous day then make it invisible
        if(!isPreviousDay(scheduledTask.getScheduledAt())){
            customer_card.setVisibility(View.GONE);
        }else {

            firstName = scheduledTask.getCustomer().getUser().getFirstName();
            lastName = scheduledTask.getCustomer().getUser().getLastName();
            customer_name.setText(firstName.substring(0,1).toUpperCase() + firstName.substring(1)  +" "+ lastName.substring(0,1).toUpperCase() + lastName.substring(1));

            Picasso.get()
                    .load( scheduledTask.getCustomer().getProfilePicThumbnailUrl())
                    .into(customer_img);

        }

    }


    private void houseLocation() {

        double latitute = scheduledTask.getHouse().getAddress().getLatitude();
        double longitute =scheduledTask.getHouse().getAddress().getLongitude();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:28.6514074,77.2400794?q="+latitute+","+longitute+"(House)"));
        startActivity(intent);

    }

    private void callCustomer() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_VIEW );
            callIntent.setData(Uri.parse("tel:"+scheduledTask.getCustomer().getPhoneNo()));
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Toast.makeText(getActivity(),"Calling a Phone Number Call failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void chatCustomer() {
        startActivity(new Intent(getActivity(), ChatWindow.class)
                .putExtra("conversation",scheduledTask.getId())
                .putExtra("first_name",firstName)
                .putExtra("last_name",lastName)
                .putExtra("profile_pic_url", scheduledTask.getCustomer().getProfilePicUrl())
                .putExtra("phone_number",scheduledTask.getCustomer().getPhoneNo()));

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

    private void cancelTask() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
                        Call<String> call = availabilityInterface.cancelTask(taskId ,"Token "+key);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                progressDialog.dismiss();
                                Log.i("InfoText","cancelTask:"+response.body());

                                if(listener != null){
                                    listener.onRefreshTaskList();
                                }
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Error, Try again!", Toast.LENGTH_SHORT).show();

                            }
                        });



                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
        builder.setMessage("Are you sure to Cancel this task?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();



    }

    private void setReminder() {

        String givenDateString =scheduledTask.getScheduledAt();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            /*scheduledTask.getScheduledAt()*/
            intent.putExtra("beginTime", timeInMilliseconds);
            intent.putExtra("allDay", false);
            intent.putExtra("endTime", timeInMilliseconds+20*60*1000);
            intent.putExtra("title", scheduledTask.getCategory().getName()+" Task");
            startActivity(intent);

        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    private void moreInfo() {

        String url = "https://halanx.com/house/"+scheduledTask.getHouse().getId();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void fetchDetails(){
        rootCardView.setVisibility(View.INVISIBLE);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Call<ScheduledTask> call = availabilityInterface.getTasksById(taskId,"Token "+key);
        call.enqueue(new Callback<ScheduledTask>() {
            @Override
            public void onResponse(Call<ScheduledTask> call, Response<ScheduledTask> response) {
                progressDialog.dismiss();
                if(response.body() != null){
                    rootCardView.setVisibility(View.VISIBLE);
                    scheduledTask = response.body();
                    updateTaskDetails();
                } else {
                    showErrorDialog();
                }
            }

            @Override
            public void onFailure(Call<ScheduledTask> call, Throwable t) {
                rootCardView.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
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
                        if(listener != null){
                            listener.onRefreshTaskList();
                        }
                    }
                })
                .create();
        alertDialog.show();
    }

    private boolean isPreviousDay(String scheduledDate){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm a");
        try {
            Date date = dateFormat.parse(scheduledDate);
            if(date.getTime()-System.currentTimeMillis()<=24 * 60 * 60 * 1000)
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnPropertyDetailsInteractionListener){
            listener = (OnPropertyDetailsInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnPropertyDetailsInteractionListener{

        void onCheckAmenitiesClicked();

        void setAmount(int amount);

        void onRefreshTaskList();
    }

}
