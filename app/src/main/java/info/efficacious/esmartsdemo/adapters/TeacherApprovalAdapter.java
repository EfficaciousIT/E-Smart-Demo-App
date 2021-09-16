package info.efficacious.esmartsdemo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import info.efficacious.esmartsdemo.Interface.DataService;
import info.efficacious.esmartsdemo.R;
import info.efficacious.esmartsdemo.Tab.AdminApproval_Tab;
import info.efficacious.esmartsdemo.activity.MainActivity;
import info.efficacious.esmartsdemo.common.ConnectionDetector;

import info.efficacious.esmartsdemo.dialogbox.image_zoom_dialog;
import info.efficacious.esmartsdemo.entity.LeaveDetail;
import info.efficacious.esmartsdemo.webApi.RetrofitInstance;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class TeacherApprovalAdapter extends RecyclerView.Adapter<TeacherApprovalAdapter.LeaveListHolder> {

    private ArrayList<LeaveDetail> itemsArrayList;
    private Context mcontext;
    String int_Approval, LeaveType;
    ConnectionDetector cd;
    int TotalLeaveCOunt = 0;
    String LeaveApplication_id, intTeacher_id;
    private ProgressDialog progress;
    public TeacherApprovalAdapter(ArrayList<LeaveDetail> dataList,Context context) {
        this.itemsArrayList = dataList;
        this.mcontext = context;

    }

    @Override
    public LeaveListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_teacherleaveapply, parent, false);
        return new LeaveListHolder(view);
    }

    @Override
    public void onBindViewHolder(final LeaveListHolder holder, final int position) {

        try {
            cd = new ConnectionDetector(mcontext);
            holder.Leave_count.setText("CL :" + itemsArrayList.get(position).getIntCL() + " ML :" + itemsArrayList.get(position).getIntML());
           LeaveApplication_id = String.valueOf(itemsArrayList.get(position).getIntLeaveApplocationId());
            holder.fromDate.setText("Date: " + itemsArrayList.get(position).getDtFromDate()+" - "+ itemsArrayList.get(position).getDtToDate());
            holder.Reason.setText("Reason: " + itemsArrayList.get(position).getVchReason());
            holder.Name.setText( itemsArrayList.get(position).getName());
            holder.Days.setText(String.valueOf(itemsArrayList.get(position).getIntTotalDays()));
            String url = RetrofitInstance.Image_URL + itemsArrayList.get(position).getVchProfile();
            Glide.with(mcontext)
                    .load(url)
                    .fitCenter()// image url
                    .error(R.mipmap.profile)
                    .into(holder.imageteacher);
            holder.approval_checkbx.setVisibility(View.VISIBLE);
            holder.rejected_checkbx.setVisibility(View.VISIBLE);

            holder.imageteacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mcontext,image_zoom_dialog.class);
                    intent.putExtra("path",itemsArrayList.get(position).getVchProfile());
                    mcontext.startActivity(intent);
                }
            });
        } catch (Exception ex) {
        }

        holder.approval_checkbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    intTeacher_id = String.valueOf(itemsArrayList.get(position).getIntTeacherId());
                    int_Approval = "1";
                } catch (Exception ex) {

                }

                if (!cd.isConnectingToInternet()) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);
                    alert.setMessage("No InternetConnection");
                    alert.setPositiveButton("OK", null);
                    alert.show();

                } else {
                    try {
                        TotalLeaveCOunt = 0;
                        LeaveType = itemsArrayList.get(position).getVchLeaveType();
                        if (LeaveType.contentEquals("CL")) {
                            TotalLeaveCOunt = (Integer.parseInt(String.valueOf(itemsArrayList.get(position).getIntCL())) + Integer.parseInt(String.valueOf(itemsArrayList.get(position).getIntTotalDays())));
                        } else {
                            TotalLeaveCOunt = (Integer.parseInt(String.valueOf(itemsArrayList.get(position).getIntML())) + Integer.parseInt(String.valueOf(itemsArrayList.get(position).getIntTotalDays())));
                        }
                        AdminAsync();
                    } catch (Exception ex) {

                    }


                }
            }
        });
        holder.rejected_checkbx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    intTeacher_id = String.valueOf(itemsArrayList.get(position).getIntTeacherId());
                    int_Approval = "2";
                    if (!cd.isConnectingToInternet()) {

                        AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);
                        alert.setMessage("No InternetConnection");
                        alert.setPositiveButton("OK", null);
                        alert.show();

                    } else {

                        AdminAsync ();

                    }
                } catch (Exception ex) {

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsArrayList.size();
    }

    class LeaveListHolder extends RecyclerView.ViewHolder {
        TextView fromDate,Days,Reason,Name,Leave_count;
        CircleImageView imageteacher;
        CheckBox approval_checkbx,rejected_checkbx;
        LeaveListHolder(View rowView) {
            super(rowView);
             fromDate = (TextView) rowView.findViewById(R.id.from_todate);
             Reason = (TextView) rowView.findViewById(R.id.reasontv);
             Name = (TextView) rowView.findViewById(R.id.nametv);
             imageteacher = (CircleImageView) rowView.findViewById(R.id.fabprofil);
             approval_checkbx = (CheckBox) rowView.findViewById(R.id.approval_checkbx);
            Days=(TextView) rowView.findViewById(R.id.leaveCountdays);
             Leave_count = (TextView) rowView.findViewById(R.id.clMlcount);
             rejected_checkbx = (CheckBox) rowView.findViewById(R.id.reject_checkbx1);
        }

    }
    public void  AdminAsync (){
        try {
            progress = new ProgressDialog(mcontext);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage("loading...");
            progress.show();
            DataService service = RetrofitInstance.getRetrofitInstance().create(DataService.class);
            LeaveDetail leaveDetail = new LeaveDetail(Integer.parseInt(LeaveApplication_id),int_Approval);
            Observable<ResponseBody> call = service.updateLeaveDetail("UpdateStatus",leaveDetail);
            call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable disposable) {
                    progress.show();
                }

                @Override
                public void onNext(ResponseBody body) {
                    try {

                    } catch (Exception ex) {
                        progress.dismiss();
                        Toast.makeText(mcontext, "Response Taking Time,Seems Network issue!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    progress.dismiss();
                    Toast.makeText(mcontext, "Response Taking Time,Seems Network issue!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onComplete() {
                    progress.dismiss();
                    try {
                        if (int_Approval.contentEquals("1")) {
                            LeaveCountAsync ();
                        } else {
                                Toast.makeText(mcontext, "Leave Rejected Successfully", Toast.LENGTH_LONG).show();
                                AdminApproval_Tab adminApproval_tab = new AdminApproval_Tab();
                                MainActivity.fragmentManager.beginTransaction().replace(R.id.content_main, adminApproval_tab).commitAllowingStateLoss();
                        }

                    } catch (Exception ex) {
                        ex.getMessage();
                    }
                }
            });
        } catch (Exception ex) {
            ex.getMessage();
            progress.dismiss();
        }
    }
    public void  LeaveCountAsync (){
        try {
            progress = new ProgressDialog(mcontext);
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.setMessage("loading...");
            progress.show();
            String command;
            if (LeaveType.contentEquals("CL")) {
                command="UpdateCLCount";
            } else {
                command="UpdateMLCount";
            }
            DataService service = RetrofitInstance.getRetrofitInstance().create(DataService.class);
            LeaveDetail leaveDetail = new LeaveDetail(TotalLeaveCOunt,TotalLeaveCOunt,Integer.parseInt(intTeacher_id));
            Observable<ResponseBody> call = service.updateLeaveDetail(command,leaveDetail);
            call.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable disposable) {
                    progress.show();
                }

                @Override
                public void onNext(ResponseBody body) {
                    try {

                    } catch (Exception ex) {
                        progress.dismiss();
                        Toast.makeText(mcontext, "Response Taking Time,Seems Network issue!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    progress.dismiss();
                    Toast.makeText(mcontext, "Response Taking Time,Seems Network issue!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onComplete() {
                    progress.dismiss();
                    Toast.makeText(mcontext, "Leave Approved Successfully", Toast.LENGTH_LONG).show();
                    AdminApproval_Tab adminApproval_tab = new AdminApproval_Tab();
                    MainActivity.fragmentManager.beginTransaction().replace(R.id.content_main, adminApproval_tab).commitAllowingStateLoss();
                }
            });
        } catch (Exception ex) {
            progress.dismiss();
            ex.getMessage();
        }
    }
}