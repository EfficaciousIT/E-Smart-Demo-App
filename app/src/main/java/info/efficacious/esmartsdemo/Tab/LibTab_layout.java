package info.efficacious.esmartsdemo.Tab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import info.efficacious.esmartsdemo.R;
import info.efficacious.esmartsdemo.fragment.AssignBookList_Fragment;
import info.efficacious.esmartsdemo.fragment.BookList_Fragment;
import info.efficacious.esmartsdemo.fragment.ReturnBookList_Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 18,May,2020
 */
public class LibTab_layout extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    public static String value;
    private static final String PREFRENCES_NAME = "myprefrences";
    SharedPreferences settings;
    String role_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_calender, container, false);
        settings = getActivity().getSharedPreferences(PREFRENCES_NAME, Context.MODE_PRIVATE);
        role_id = settings.getString("TAG_USERTYPEID", "");
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);


        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity.getSupportActionBar() != null;
        // activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lightorange)));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupViewPager(viewPager);
        // after you set the adapter you have to check if view is laid out, i did a custom method for it
        if (ViewCompat.isLaidOut(tabLayout)) {
            setViewPagerListener();
        } else {
            tabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    setViewPagerListener();
                    tabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }
            viewPager.setCurrentItem(1);
    }

    private void setViewPagerListener() {
        if (viewPager!=null)
            tabLayout.setupWithViewPager(viewPager);
        else
            tabLayout.setupWithViewPager(viewPager);

        // use class TabLayout.ViewPagerOnTabSelectedListener
        // note that it's a class not an interface as OnTabSelectedListener, so you can't implement it in your activity/fragment
        // methods are optional, so if you don't use them, you can not override them (e.g. onTabUnselected)
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab!=null)
                    super.onTabSelected(tab);
                else
                    super.onTabSelected(tab);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        try {

            if (role_id.contentEquals("5")) {
                viewPagerAdapter.addFragment(new BookList_Fragment(), "Book List");
                viewPagerAdapter.addFragment(new AssignBookList_Fragment(), "Assign Book");
                viewPagerAdapter.addFragment(new ReturnBookList_Fragment(), "Return");
            } else if (role_id.contentEquals("3")) {
                viewPagerAdapter.addFragment(new BookList_Fragment(), "Book List");
                viewPagerAdapter.addFragment(new ReturnBookList_Fragment(), "Assign Book");
            } else if (role_id.contentEquals("2") || role_id.contentEquals("1")) {
                viewPagerAdapter.addFragment(new BookList_Fragment(), "Book List");
                viewPagerAdapter.addFragment(new ReturnBookList_Fragment(), "Assign Book");

            }
        } catch (Exception ex) {

        }


        viewPager.setAdapter(viewPagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
}