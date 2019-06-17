package com.technicalrj.halanxscouts.Home;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Adapters.AvailabilityAdapter;
import com.technicalrj.halanxscouts.Adapters.TaskAdapter;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.HomeActivity;
import com.technicalrj.halanxscouts.LoginActivity;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.support.constraint.Constraints.TAG;
import static com.technicalrj.halanxscouts.LoginActivity.halanxScout;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private ImageView addSchedule;
    AvailabilityAdapter availabilityAdapter;
    String key;
    Button go_online;
    Dialog dialog;
    ArrayList<ScheduledTask> scheduledTaskList;
    LinearLayout avalabilityLayout;
    LayoutInflater inflater;

    public HomeFragment() {
        // Required empty public constructor
    }






    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        this.inflater  = inflater;

        RecyclerView task_recycler = v.findViewById(R.id.task_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        scheduledTaskList = new ArrayList<>();
        final TaskAdapter taskAdapter = new TaskAdapter(getActivity(),scheduledTaskList);
        task_recycler.setLayoutManager(lm);
        task_recycler.setAdapter(taskAdapter);


        addSchedule = v.findViewById(R.id.addSchedule);
        go_online = v.findViewById(R.id.go_online);

        final SharedPreferences prefs = getActivity().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        key = prefs.getString("login_key", null);


        avalabilityLayout = v.findViewById(R.id.availability);



        //Get all available scheudle

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<List<ScheduleAvailability>> call1 = availabilityInterface.getSchedule("Token "+key);

        call1.enqueue(new retrofit2.Callback<List<ScheduleAvailability>>() {
            @Override
            public void onResponse(Call<List<ScheduleAvailability>> call, final Response<List<ScheduleAvailability>> response) {
                final ArrayList<ScheduleAvailability> list = (ArrayList<ScheduleAvailability>) response.body();
                for (int i = 0; i <list.size() ; i++) {




                    addScheduleInList(list.get(i));


                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<ScheduleAvailability>> call, Throwable t) {

            }
        });











        //Get all tasks

        Call<List<ScheduledTask>> call2 = availabilityInterface.getAllTasks("Token "+key);
        call2.enqueue(new Callback<List<ScheduledTask>>() {
            @Override
            public void onResponse(Call<List<ScheduledTask>> call, Response<List<ScheduledTask>> response) {
                scheduledTaskList.addAll( response.body()) ;

                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ScheduledTask>> call, Throwable t) {

            }
        });



        go_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ScoutAcceptanceActivity.class));
            }
        });



        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                View view = inflater.inflate(R.layout.dialog_scout_add_schedule,null);
                dialog.setContentView(view);


                //Adding the gridView
                GridView gridview = (GridView) view.findViewById(R.id.gridview);


                ArrayList<AvailabiltyTime> list = getNextSevenDays();
                availabilityAdapter = new AvailabilityAdapter(getContext(),list);
                gridview.setAdapter(availabilityAdapter);




                //Setting onClickListener

                final Button from = view.findViewById(R.id.from);
                final Button to = view.findViewById(R.id.to);
                final Button save_button = view.findViewById(R.id.save_button);


                from.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                boolean isPM = (selectedHour >= 12);
                                from.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));

                                from.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));
                                if(availabilityAdapter.isDateSelected && from.getText().toString().contains(":") && to.getText().toString().contains(":")){
                                    save_button.setEnabled(true);
                                    save_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                                }


                            }
                        }, hour, minute, false);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();







                    }
                }) ;

                to.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                boolean isPM = (selectedHour >= 12);
                                to.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));
                                to.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));

                                if(availabilityAdapter.isDateSelected && from.getText().toString().contains(":") && to.getText().toString().contains(":")){
                                    save_button.setEnabled(true);
                                    save_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                                }
                            }
                        }, hour, minute, false);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();



                    }
                });


                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String selectedDate = availabilityAdapter.selectedDate;
                        String startTime = from.getText().toString() ;
                        String endTime = to.getText().toString() ;

                        Log.i("InfoText","start:"+selectedDate+" "+startTime +" endtimee:"+ selectedDate+" "+endTime);

                        addScheduleToServer(selectedDate+" "+startTime , selectedDate+" "+endTime);


                    }
                });





                dialog.show();
            }
        });






        return v;



    }

    private void addScheduleInList(final ScheduleAvailability scheduleAvailability) {


        final String startTime = scheduleAvailability.getStartTime();
        final String endTime =scheduleAvailability.getEndTime();


        String dateString = startTime.split(" ")[0];
        String monthString = startTime.split(" ")[1].substring(0,3).toUpperCase();
        String timeString = String.format("%d",Integer.valueOf(startTime.split(" ")[3].split(":")[0]))  + startTime.split(" ")[4];



        final View view = inflater.inflate(R.layout.availability_date,null);
        TextView date = view.findViewById(R.id.date);
        TextView time = view.findViewById(R.id.time);
        final TextView month = view.findViewById(R.id.month);

        date.setText(dateString);
        time.setText(timeString);
        month.setText(monthString);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        (layoutParams).setMargins(23,0,0,0);
        view.setLayoutParams(layoutParams);
        avalabilityLayout.addView(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_scout_schedule);


                final TextView date = dialog.findViewById(R.id.date);
                TextView timeLimit = dialog.findViewById(R.id.time_limit);
                TextView edit_tv = dialog.findViewById(R.id.tv_edit_box);
                TextView delete_tv = dialog.findViewById(R.id.tv_delete_box);


                date.setText(startTime.split(" ")[0] + " " + startTime.split(" ")[1]);
                timeLimit.setText( startTime.split(" ")[3] +" " + startTime.split(" ")[4] + "-"+
                        endTime.split(" ")[3] +" " + endTime.split(" ")[4] );

                edit_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        dialog.dismiss();
                        openEditDialog(startTime,endTime,scheduleAvailability.getId(),view);


                    }
                });

                delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteScheduleFromServer(scheduleAvailability.getId());
                        dialog.dismiss();
                        avalabilityLayout.removeView(view);



                    }
                });




                dialog.show();
            }
        });

    }


    public void openEditDialog(final String startTime, final String endTime, final int id, final View clickedView ){

        Log.i("InfoText","Update :"+startTime+"---"+endTime);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_scout_add_schedule,null);
        dialog.setContentView(view);


        //Adding the gridView
        GridView gridview = (GridView) view.findViewById(R.id.gridview);


        ArrayList<AvailabiltyTime> list = getNextSevenDays();
        availabilityAdapter = new AvailabilityAdapter(getContext(),list);
        gridview.setAdapter(availabilityAdapter);
        String[] parts = startTime.split(" ");
        availabilityAdapter.selectedDate = parts[0] +" "+ parts[1] +" "+ parts[2];




        //Setting onClickListener

        final Button from = view.findViewById(R.id.from);
        final Button to = view.findViewById(R.id.to);
        final Button save_button = view.findViewById(R.id.save_button);







        String fromTime = startTime.split(" ")[3] + " " + startTime.split(" ")[4] ;
        Log.i("InfoText","from time 1part:"+startTime.split(" ")[3]);
        Log.i("InfoText","from time 2part:"+startTime.split(" ")[4]);

        String toTime =     endTime.split(" ")[3] + " " + endTime.split(" ")[4] ;
        from.setText(fromTime);
        to.setText(toTime);




        from.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));
        to.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));

        save_button.setEnabled(true);
        save_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));


















        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parts = startTime.split(" ")[3].split(":");
                int hour = startTime.split(" ")[4].equals("PM") ? 12+Integer.valueOf( removeZero(parts[0])) : Integer.valueOf( removeZero(parts[0]));
                int minute = Integer.valueOf( parts[1]);


                Log.i("InfoTextStart","houe:"+hour+" minute:"+minute+" m:"+startTime.split(" ")[4]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        boolean isPM = (selectedHour >= 12);
                        from.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));

                        from.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));
                        if(availabilityAdapter.isDateSelected && from.getText().toString().contains(":") && to.getText().toString().contains(":")){
                            save_button.setEnabled(true);
                            save_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                        }


                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();







            }
        }) ;

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parts = endTime.split(" ")[3].split(":");
                int hour = endTime.split(" ")[4].equals("PM") ? 12+Integer.valueOf( removeZero(parts[0])) : Integer.valueOf( removeZero(parts[0]));
                int minute = Integer.valueOf( parts[1]);

                Log.i("InfoTextStart","houe:"+hour+" minute:"+minute+" m:"+startTime.split(" ")[4]);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        boolean isPM = (selectedHour >= 12);
                        to.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));
                        to.setBackgroundTintList(getResources().getColorStateList(R.color.colorFace));

                        if(availabilityAdapter.isDateSelected && from.getText().toString().contains(":") && to.getText().toString().contains(":")){
                            save_button.setEnabled(true);
                            save_button.setBackgroundTintList(getResources().getColorStateList(R.color.colorGreen));
                        }
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();



            }
        });


        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String selectedDate = availabilityAdapter.selectedDate;
                String startTime = from.getText().toString() ;
                String endTime = to.getText().toString() ;

                Log.i("InfoText","start:"+selectedDate+" "+startTime +" endtimee:"+ selectedDate+" "+endTime);

                updateScheduleToServer(selectedDate+" "+startTime , selectedDate+" "+endTime,id,clickedView);

            }
        });





        dialog.show();
    }

    private void updateScheduleToServer(final String startTime, String endTime, int id, final View clickedView) {


        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        final ScheduleAvailability scheduleAvailability = new ScheduleAvailability();
        scheduleAvailability.setStartTime(startTime);
        scheduleAvailability.setEndTime(endTime);
        Call<ScheduleAvailability> call1 = availabilityInterface.updateSchedule(scheduleAvailability,id,"Token "+key);





        call1.enqueue(new retrofit2.Callback<ScheduleAvailability>() {
            @Override
            public void onResponse(Call<ScheduleAvailability> call, Response<ScheduleAvailability> response) {

                final ScheduleAvailability newScheduleAvailability = response.body();

                Log.i("InfoText","update response :"+response.message());



                if(response.isSuccessful()){

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Availability Updated", Toast.LENGTH_SHORT).show();
                            updateScheduleInList(newScheduleAvailability,clickedView);

                        }
                    });

                }else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getJSONArray("non_field_errors").get(0)+"", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


                progressDialog.dismiss();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ScheduleAvailability> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });



    }

    private void updateScheduleInList(final ScheduleAvailability newScheduleAvailability, final View view) {

        final String startTime = newScheduleAvailability.getStartTime();
        final String endTime =newScheduleAvailability.getEndTime();


        String dateString = startTime.split(" ")[0];
        String monthString = startTime.split(" ")[1].substring(0,3).toUpperCase();
        String timeString = String.format("%d",Integer.valueOf(startTime.split(" ")[3].split(":")[0]))  + startTime.split(" ")[4];



        TextView date = view.findViewById(R.id.date);
        TextView time = view.findViewById(R.id.time);
        final TextView month = view.findViewById(R.id.month);

        date.setText(dateString);
        time.setText(timeString);
        month.setText(monthString);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        (layoutParams).setMargins(23,0,0,0);
        view.setLayoutParams(layoutParams);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_scout_schedule);


                final TextView date = dialog.findViewById(R.id.date);
                TextView timeLimit = dialog.findViewById(R.id.time_limit);
                TextView edit_tv = dialog.findViewById(R.id.tv_edit_box);
                TextView delete_tv = dialog.findViewById(R.id.tv_delete_box);


                date.setText(startTime.split(" ")[0] + " " + startTime.split(" ")[1]);
                timeLimit.setText( startTime.split(" ")[3] +" " + startTime.split(" ")[4] + "-"+
                        endTime.split(" ")[3] +" " + endTime.split(" ")[4] );

                edit_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        dialog.dismiss();
                        openEditDialog(startTime,endTime,newScheduleAvailability.getId(),view);


                    }
                });

                delete_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteScheduleFromServer(newScheduleAvailability.getId());
                        dialog.dismiss();
                        avalabilityLayout.removeView(view);



                    }
                });




                dialog.show();
            }
        });


    }

    private void deleteScheduleFromServer(int id) {



        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<String> call1 = availabilityInterface.deleteSchedule(id,"Token "+key);

        call1.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Availability Deleted Succesfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Some Error Occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });


    }

    private void addScheduleToServer(String startTime,String endTime) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RetrofitAPIClient.DataInterface availabilityInterface = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        ScheduleAvailability scheduleAvailability = new ScheduleAvailability();
        scheduleAvailability.setStartTime(startTime);
        scheduleAvailability.setEndTime(endTime);
        retrofit2.Call<ScheduleAvailability> call1 = availabilityInterface.addSchedule(scheduleAvailability,"Token "+key);

        call1.enqueue(new retrofit2.Callback<ScheduleAvailability>() {
            @Override
            public void onResponse(retrofit2.Call<ScheduleAvailability> call, final retrofit2.Response<ScheduleAvailability> response) {



                if(response.isSuccessful()){

                    ScheduleAvailability scheduleAvailability1 = response.body();
                    addScheduleInList(scheduleAvailability1);
                    Log.i("InfoText","response :"+response.body().toString());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Availability Added", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(getActivity(), jObjError.getJSONArray("non_field_errors").get(0)+"", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }


                progressDialog.dismiss();
                dialog.dismiss();
            }

            @Override
            public void onFailure(retrofit2.Call<ScheduleAvailability> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });









    }


    private ArrayList<AvailabiltyTime> getNextSevenDays() {

        ArrayList<AvailabiltyTime> list = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy");
        for (int i = 0; i < 7; i++) {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.DATE, i);
            String day = sdf.format(calendar.getTime());

            String day_of_week = day.split(" ")[0].substring(0,3).toUpperCase();
            String date  = day.split(" ")[1];
            String month = day.split(" ")[2].substring(0,3).toUpperCase();
            String onlyDate = day.split(" ",2)[1];

            list.add(new AvailabiltyTime(day_of_week,date,month,onlyDate));

        }

        return list;

    }

    public static String removeZero(String str)
    {
        // Count leading zeros
        int i = 0;
        while (i < str.length() && str.charAt(i) == '0')
            i++;

        // Convert str into StringBuffer as Strings
        // are immutable.
        StringBuffer sb = new StringBuffer(str);

        // The  StringBuffer replace function removes
        // i characters from given index (0 here)
        sb.replace(0, i, "");

        return sb.toString();  // return in String
    }

}
