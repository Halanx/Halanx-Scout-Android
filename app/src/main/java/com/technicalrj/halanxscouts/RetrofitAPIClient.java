package com.technicalrj.halanxscouts;

import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.Home.Chat.Messages;
import com.technicalrj.halanxscouts.Home.Chat.Result;
import com.technicalrj.halanxscouts.Home.MoveOut.AmenitiesResponse;
import com.technicalrj.halanxscouts.Home.ScheduleAvailability;
import com.technicalrj.halanxscouts.Home.TaskFolder.ScheduledTask;
import com.technicalrj.halanxscouts.Notification.NoficationPojo.Notification;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.BankDetail;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.Wallet.TaskPayment;

import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RetrofitAPIClient {

    public static final String BASE_URL = "https://scout-api.halanx.com/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface DataInterface {


        //Availability
        @POST("scouts/scheduled_availability/")
        Call<ScheduleAvailability> addSchedule(@Body ScheduleAvailability scheduleAvailability ,  @Header("Authorization") String token);

        @GET("scouts/scheduled_availability/")
        Call<List<ScheduleAvailability>> getSchedule(@Header("Authorization") String token);

        @PATCH("/scouts/scheduled_availability/{id}/")
        Call<ScheduleAvailability> updateSchedule(@Body ScheduleAvailability scheduleAvailability,@Path("id") int id ,  @Header("Authorization") String token );

        @DELETE("/scouts/scheduled_availability/{id}/")
        Call<String> deleteSchedule(@Path("id") int id, @Header("Authorization") String token);



        //-----WALLET

        @GET("/scouts/wallet/")
        Call<JsonObject> getWallet(@Header("Authorization") String token);

        @GET("/scouts/payments/")
        Call<List<TaskPayment>> getPayments(@Query("status") String status, @Header("Authorization") String token);


        //------TASKS
        @GET("/scouts/tasks/")
        Call<List<ScheduledTask>> getAllTasks(@Header("Authorization") String token);

        @GET("/scouts/tasks/{id}/")
        Call<ScheduledTask> getTasksById(@Path("id") int id,@Header("Authorization") String token);

        @PATCH("/scouts/tasks/{id}/")
        Call<Void> setTaskComplete(@Path("id") int id, @Body JsonObject jsonObject, @Header("Authorization") String token);

        @DELETE("/scouts/tasks/{id}/")
        Call<String> cancelTask(@Path("id") int id,@Header("Authorization") String token);





        @GET("/scouts/notifications/")
        Call<List<Notification>> getNotifications(@Header("Authorization") String token);




        @GET("/scouts/")
        Call<Profile> getProfile(@Header("Authorization") String token);

        @PATCH("/scouts/")
        Call<Profile> updateProfileGcmId(@Body HashMap<String,String> map, @Header("Authorization") String token);

        @PATCH("/scouts/")
        Call<Profile> updateBankDetails(@Body Profile profile , @Header("Authorization") String token);

        @PATCH("/scouts/")
        Call<Profile> updateOnlineStatus(@Body JsonObject jsonObject, @Header("Authorization") String token);




        @GET("/chat/conversations/{id}/messages/")
        Call<Messages> getMessages( @Path("id") String id,@Query("page") int page , @Header("Authorization") String token , @Header("PARTICIPANT-TYPE") String particationType);

        @POST("/chat/conversations/{id}/messages/")
        Call<Result> createMessage( @Body HashMap<String,String> map, @Path("id") String id,@Header("Authorization") String token , @Header("PARTICIPANT-TYPE") String particationType,
                                    @Header("Content-Type") String type);


        @PATCH("/scouts/tasks/{id}/request/")
        Call<JsonObject> taskRequest( @Body JsonObject jsonObject ,@Path("id") int id,@Header("Authorization") String token );



        @GET("/scouts/")
        Call<Profile> getRating(@Header("Authorization") String token);

        @PATCH("/scouts/")
        Call<Profile> updateLocation(@Body JsonObject jsonObject , @Header("Authorization") String token);

        @GET("/scouts/tasks/{task_id}/subtask/move_out/amenity_check/")
        Call<AmenitiesResponse> getListOfAmenities(@Header("Authorization") String token,
                                                   @Path("task_id") String taskId);

        @PATCH("/scouts/tasks/{task_id}/subtask/move_out/amenity_check/")
        Call<AmenitiesResponse> updateAmenities(@Header("Authorization") String token,
                                                @Path("task_id") String taskId,
                                                @Body AmenitiesResponse.AmenityJsonData amenityData);

    }


}
