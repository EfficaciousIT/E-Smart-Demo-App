package info.efficacious.esmartsdemo.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import info.efficacious.esmartsdemo.Interface.DataService;
import info.efficacious.esmartsdemo.R;
import info.efficacious.esmartsdemo.WeeklyCalender.WeekCalendar;
import info.efficacious.esmartsdemo.WeeklyCalender.listener.OnDateClickListener;
import info.efficacious.esmartsdemo.adapters.OnlineClassDetailAdapter;
import info.efficacious.esmartsdemo.common.ConnectionDetector;
import info.efficacious.esmartsdemo.database.Databasehelper;
import info.efficacious.esmartsdemo.entity.OnlineClassDetailPojo;
import info.efficacious.esmartsdemo.webApi.RetrofitInstance;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class OnlineClassDetail extends Fragment {

    private static final String PREFRENCES_NAME = "myprefrences";
    SharedPreferences settings;
    Databasehelper mydb;
    RecyclerView rv_online_classes;
    RecyclerView.Adapter madapter;
    String Schooli_id,role_id,intStandard_id,academic_id, teacher_Id;
    View myview;
    ConnectionDetector cd;
    private ProgressDialog progress;
    WeekCalendar weekCalendar;
    String strSelectedDt;
    TextView month_tv;
    String stand_id;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview=inflater.inflate(R.layout.online_class,null);
        settings = getActivity().getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        weekCalendar=myview.findViewById(R.id.weekCalendar);

        Schooli_id= settings.getString("TAG_SCHOOL_ID", "");
        role_id = settings.getString("TAG_USERTYPEID", "");
        academic_id = settings.getString("TAG_ACADEMIC_ID", "");
        teacher_Id = settings.getString("TAG_USERID", "");
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = new Date();
        strSelectedDt = currentDate.format(todayDate);


        try{
            if(role_id.contentEquals("1")||role_id.contentEquals("2")){
                intStandard_id= settings.getString("TAG_STANDERDID", "");
            }
        }catch (Exception ex){}

        try {

            stand_id = getArguments().getString("std_id");
            Log.d("RESULT123",stand_id);
        } catch (Exception ex) {

        }

        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setMessage("loading...");
        progress.show();
        rv_online_classes=(RecyclerView)myview.findViewById(R.id.rv_online_classes);
        mydb=new Databasehelper(getActivity(),"Notifications",null,1);
        cd = new ConnectionDetector(getActivity().getApplicationContext());

        if (!cd.isConnectingToInternet()){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("No Internet Connection");
            alert.setPositiveButton("OK",null);
            alert.show();
        }
        else {
            try{
                OnlineClassDetailAsync ();
            }catch (Exception ex){}
        }
        weekCalendar.reset();
        weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                strSelectedDt=setDateTimeFormatFromServer(dateTime.toString());
//                Toast.makeText(getActivity(),
//                        "You Selected " + setDateTimeFormatFromServer(dateTime.toString()), Toast.LENGTH_SHORT).show();
                OnlineClassDetailAsync();
            }

        });
        return myview;
    }


    public void OnlineClassDetailAsync() {
        try {
            Observable<OnlineClassDetailPojo> call = null;
            DataService service = RetrofitInstance.getRetrofitInstance().create(DataService.class);

            //Parents and Students
            if (role_id.contentEquals("1") || role_id.contentEquals("2")) {
                call = service.getOnlineClassDetails("StudentNotification", academic_id, Schooli_id, intStandard_id,strSelectedDt);
            }
            // Admin
            else if(role_id.contentEquals("5") || role_id.contentEquals("3")){
                call = service.getOnlineClassDetails("StudentNotification", academic_id, Schooli_id,stand_id,strSelectedDt);
            }
            //Principal
            else if(role_id.contentEquals("6")){
                call = service.getOnlineClassDetails("StudentNotification", academic_id, String.valueOf(0),stand_id,strSelectedDt);
            }
            //Teachers
//            else if(role_id.contentEquals("3")){
//                call = service.getOnlineClassDetailsT("TeacherNotification", academic_id, Schooli_id,teacher_Id,strSelectedDt);
//            }

            call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<OnlineClassDetailPojo>() {
                @Override
                public void onSubscribe(Disposable disposable) {
                    progress.show();
                }

                @Override
                public void onNext(OnlineClassDetailPojo body) {
                    try {
                        generateNoticeList((ArrayList<OnlineClassDetailPojo.OnlineSchedule>) body.getOnlineSchedule());
                    } catch (Exception ex) {
                        progress.dismiss();
                       // Toast.makeText(getActivity(), "Response taking time seems Network issue!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    progress.dismiss();
                   // Toast.makeText(getActivity(), "Response taking time seems Network issue!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onComplete() {
                    progress.dismiss();
                }
            });
        } catch (Exception ex) {
            progress.dismiss();
        }
    }

    public void generateNoticeList(ArrayList<OnlineClassDetailPojo.OnlineSchedule> taskListDataList) {
        try {
            if ((taskListDataList != null && !taskListDataList.isEmpty())) {
                rv_online_classes.setHasFixedSize(true);
                rv_online_classes.setLayoutManager(new LinearLayoutManager(getActivity()));
                madapter = new OnlineClassDetailAdapter(taskListDataList);
                rv_online_classes.setAdapter(madapter);
            } else {
                try {
                    rv_online_classes.setLayoutManager(new LinearLayoutManager(getActivity()));
                    madapter = new OnlineClassDetailAdapter(taskListDataList);
                    rv_online_classes.setAdapter(madapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast toast = Toast.makeText(getActivity(),
                        "No Data Available",
                        Toast.LENGTH_SHORT);
                View toastView = toast.getView();
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toastView.setBackgroundResource(R.drawable.no_data_available);
                toast.show();
            }

        } catch (Exception ex) {
            progress.dismiss();
            Toast.makeText(getActivity(), "Response taking time seems Network issue!", Toast.LENGTH_SHORT).show();
        }
    }
    public String setDateTimeFormatFromServer(String strdate) {
        String formattedDate = "";
        try {
            //String curerentdate = latest SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(latest Date());
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;

            try {
                date = originalFormat.parse(strdate);
                formattedDate = targetFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            return formattedDate;
        }
        return formattedDate;
    }
}