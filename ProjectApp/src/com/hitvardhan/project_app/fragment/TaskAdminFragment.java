package com.hitvardhan.project_app.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hitvardhan.project_app.Adapters.ViewPagerAdapter;
import com.hitvardhan.project_app.ImageCache.ImageLoader;
import com.hitvardhan.project_app.R;
import com.hitvardhan.project_app.activity.MainActivity;
import com.hitvardhan.project_app.activity.MakeNewTaskActivity;
import com.hitvardhan.project_app.activity.TaskDetailsActivity;
import com.hitvardhan.project_app.constants.SoqlQueries;
import com.hitvardhan.project_app.interfaces.NetworkCallbackInterface;
import com.hitvardhan.project_app.response_classes.Response;
import com.hitvardhan.project_app.utils.CommanUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class TaskAdminFragment extends Fragment {


    private View fragmentTaskAdminView;
    private CardView addTaskFloatingButton;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    public TaskAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentTaskAdminView = inflater.inflate(R.layout.fragment_task_admin, container, false);
        viewPager = (ViewPager) fragmentTaskAdminView
                .findViewById(R.id.pager_for_admin);
        adapter = new ViewPagerAdapter(getFragmentManager());
        TabLayout tabLayout = (TabLayout) fragmentTaskAdminView
                .findViewById(R.id.tab_for_task_fragment_admin);


        if(CommanUtils.isNetworkAvailable(getContext())) {
            getTheDetailsForAdmin();
        }
        else{
            Toast.makeText(getContext(), "No Network", Toast.LENGTH_SHORT).show();
        }

        addTaskFloatingButton =
                (CardView) fragmentTaskAdminView.findViewById(R.id.add_task_admin);
        tabLayout.setupWithViewPager(viewPager);
        addTaskFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createANewTaskActivity = new Intent(getActivity(), MakeNewTaskActivity.class);
                startActivity(createANewTaskActivity);
            }
        });
        return fragmentTaskAdminView;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getTheDetailsForAdmin() {
        try {
            CommanUtils.getDetailsofTaskForAdmin(getContext(), ((MainActivity) getActivity()).client,
                    new NetworkCallbackInterface() {
                        @Override
                        public void onSuccess(Response response) {
                            final Response res = response;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //creating a bundle
                                    Bundle bundle = new Bundle();
                                    //adding data to bundle
                                    bundle.putSerializable("TaskListss", res);
                                    AssignedTaskFragment assignedTaskFragmentTab =
                                            new AssignedTaskFragment();
                                    assignedTaskFragmentTab.setArguments(bundle);
                                    NotAssignedTaskFragment notAssignedTaskFragmentTab = new NotAssignedTaskFragment();
                                    notAssignedTaskFragmentTab.setArguments(bundle);
                                    adapter.addFragment(assignedTaskFragmentTab, "Assigned");
                                    adapter.addFragment(notAssignedTaskFragmentTab, "Not Assigned");
                                    viewPager.setAdapter(adapter);
                                }
                            });


                        }

                        @Override
                        public void onError() {
                            //do nothing
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
