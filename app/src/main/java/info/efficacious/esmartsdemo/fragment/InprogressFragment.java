package info.efficacious.esmartsdemo.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.efficacious.esmartsdemo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InprogressFragment extends Fragment {


    public InprogressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inprogress, container, false);
    }

}
