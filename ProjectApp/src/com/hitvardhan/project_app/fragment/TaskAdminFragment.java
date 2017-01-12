package com.hitvardhan.project_app.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.R;

import hitAndTrial.Tab1;
import hitAndTrial.Tab2;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskAdminFragment extends Fragment {


    View fragmentTaskAdminView;

    public TaskAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentTaskAdminView =  inflater.inflate(R.layout.fragment_task_admin, container, false);


        ViewPager viewPager = (ViewPager) fragmentTaskAdminView.findViewById(R.id.pager_for_admin);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());

        Tab1 tab1 = new Tab1();
        Tab2 tab2 = new Tab2();
        adapter.addFragment(tab1,"Assigned");
        adapter.addFragment(tab2,"Not Assigned");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) fragmentTaskAdminView.findViewById(R.id.tab_for_task_fragment_admin);

        tabLayout.setupWithViewPager(viewPager);



        return fragmentTaskAdminView;
    }

}
