package info.efficacious.esmartsdemo.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import info.efficacious.esmartsdemo.R;
import info.efficacious.esmartsdemo.adapters.MessageCenterAdapter;
import info.efficacious.esmartsdemo.database.Databasehelper;


public class MessageCenter extends Fragment {
    View myview;
    private static final String PREFRENCES_NAME = "myprefrences";
    SharedPreferences settings;
    Databasehelper mydb;
    RecyclerView mrecyclerView;
    RecyclerView.Adapter madapter;
    HashMap<Object, Object> map;
    private ArrayList<HashMap<Object, Object>> dataList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myview=inflater.inflate(R.layout.notification_activity_layout,null);
        mrecyclerView=(RecyclerView)myview.findViewById(R.id.chat_recyclerview);
        mydb=new Databasehelper(getActivity(),"Notifications",null,1);

        dataList = new ArrayList<HashMap<Object, Object>>();
        try
        {
            MessageCenterAsync messageCenterAsync=new MessageCenterAsync();
            messageCenterAsync.execute();
        }catch (Exception ex)
        {

        }

        return myview;
    }


    private class MessageCenterAsync extends AsyncTask<Void, Void, Void> {
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected Void doInBackground(Void... params) {

            try
            {
              Cursor  cursor =mydb.querydata("Select Message,MessageDate from MessageCenter ");
                int count=cursor.getCount();
                if(count==0)
                {
                    map = new HashMap<Object, Object>();
                    map.put("Message","No Data Available");
                    map.put("MessageDate","");
                    dataList.add(map);
                }

                cursor.moveToFirst();
                if (cursor != null) {

                    if (cursor.moveToFirst()) {
                        do {
                            map = new HashMap<Object, Object>();
                            map.put("Message",cursor.getString(cursor.getColumnIndex("Message")));
                            map.put("MessageDate",cursor.getString(cursor.getColumnIndex("MessageDate")));
                            dataList.add(map);
                        } while (cursor.moveToNext());

                    }

                }

            }
            catch (Exception e)
            {
                map = new HashMap<Object, Object>();
                map.put("Message","No Data Available");
                map.put("MessageDate","");
                dataList.add(map);

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try
            {
                mrecyclerView.setHasFixedSize(true);
                mrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                madapter=new MessageCenterAdapter(dataList,"MessageCenter");
                mrecyclerView.setAdapter(madapter);
            }catch (Exception ex)
            {

            }

            this.dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage("Processing...");
            dialog.show();
        }
    }

}


