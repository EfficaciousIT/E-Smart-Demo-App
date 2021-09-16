package info.efficacious.esmartsdemo.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;

import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import info.efficacious.esmartsdemo.R;
import info.efficacious.esmartsdemo.entity.DashboardDetail;


public class NoticeBoardAdapter extends RecyclerView.Adapter<NoticeBoardAdapter.ViewHolder> {
    private ArrayList<DashboardDetail> data;
    Context context;
    public NoticeBoardAdapter(ArrayList<DashboardDetail> dataList, Context context) {
        data = dataList;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView= LayoutInflater.from(parent.getContext()).inflate(R.layout.noticeboard_row,null);
        ViewHolder viewHolder=new ViewHolder(itemLayoutView);
        return viewHolder;
    }



    public void onBindViewHolder(final ViewHolder holder, int position) {
        try
        {
            holder.Lastdate.setTextColor(Color.RED);
            holder.Issuedate.setText(data.get(position).getIssueDate().toString());
            holder.Lastdate.setText(data.get(position).getEndDate().toString());
            holder.Notice.setText("Notice:"+data.get(position).getNotice().toString());
            Linkify.addLinks(holder.Notice, Linkify.WEB_URLS);
            holder.Subject.setText("Subject:"+data.get(position).getSubject().toString());
        }catch (Exception ex)
        {

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Issuedate,Lastdate,Notice,Subject;
        public ViewHolder(View itemView) {
            super(itemView);
            Issuedate=(TextView)itemView.findViewById(R.id.fromdate_noticeboard);
            Lastdate=(TextView)itemView.findViewById(R.id.todate_noticeboard);
            Notice=(TextView)itemView.findViewById(R.id.message_noticeboard);
            Subject=(TextView)itemView.findViewById(R.id.title_noticeboard);
        }
    }
}
