package itp341.liang.briana.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import itp341.liang.briana.finalproject.model.managers.ActivityManager;
import itp341.liang.briana.finalproject.model.managers.StorageManager;
import itp341.liang.briana.finalproject.model.objects.Activity;

public class MyActivities extends AppCompatActivity {
    private static final String CREATE_TITLE = "Create New Activity" ;
    private static final String EDIT_TITLE = "Edit Activity" ;
    public static final String ADD_DAILY_ACTIVITY = "Add Daily Activity";

    private ListView myActivitiesListView;
    private Button createActivityBtn;
    private ArrayList<Activity> myActivitiesList;
    private AlertDialog activityDialog;
    private ActivityAdapter activitiesAdapter;

    // create/edit activity dialog
    private EditText activityName, howLong, addedWater;
    private Button okayBtn, cancelBtn;
    boolean isCreate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myactivities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar !=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(R.string.title_my_activities);

        myActivitiesListView = (ListView)findViewById(R.id.myactivities_list_view);
        createActivityBtn = (Button)findViewById(R.id.create_new_activity_btn);
        createActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActivityDialog(CREATE_TITLE, -1);
            }
        });
        myActivitiesList = ActivityManager.getDefaultManager().getAllActivities();
        activitiesAdapter = new ActivityAdapter(getApplicationContext(), R.layout.myactivity_list_item, myActivitiesList);
        myActivitiesListView.setAdapter(activitiesAdapter);
    }
    
    private class ActivityAdapter extends ArrayAdapter<Activity>{
        private List<Activity> activities;
        private ActivityAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Activity> activities) {
            super(context, resource, activities);
            this.activities = activities;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            // Get the data item for this position
            final Activity activity = activities.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder = null; // view lookup cache stored in tag
            //create new row view if null
            if (view == null) {
                // If there's no view to re-use, inflate a brand new view for row
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.myactivity_list_item, parent, false);
                viewHolder = new ViewHolder(view);
                // Cache the viewHolder object inside the fresh view
                view.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) view.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.activityName.setText(activity.getName());
            viewHolder.duration.setText(Integer.toString(activity.getDuration()));
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show edit activity dialog
                    showActivityDialog(EDIT_TITLE, position);
                }
            });
            viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    i.putExtra(ADD_DAILY_ACTIVITY, activity);
                    setResult(android.app.Activity.RESULT_OK, i);
                    finish();
                }
            });
            // Return the completed view to render on screen
            return view;
        }
        // View lookup cache that populates the listview
        public class ViewHolder {
            TextView activityName;
            TextView duration;
            Button editBtn, addBtn;
            public ViewHolder(View v) {
                activityName = v.findViewById(R.id.activity_name);
                duration = v.findViewById(R.id.activity_duration);
                editBtn = v.findViewById(R.id.edit_activity_button);
                addBtn = v.findViewById(R.id.add_activity_button);
            }
        }
    }

    /**
     * create new activity/edit existing activity dialog
     * @param title create or edit activity title
     * @param position position of existing activity
     */
    private void showActivityDialog(String title, final int position) {
        activityDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(R.layout.create_edit_activity)
                .create();
        activityDialog.show();

        activityName = (EditText) activityDialog.findViewById(R.id.activity_name);
        howLong = (EditText) activityDialog.findViewById(R.id.how_long_field);
        addedWater = (EditText) activityDialog.findViewById(R.id.added_water_intake_field);
        okayBtn = (Button) activityDialog.findViewById(R.id.create_edit_activity_btn);
        Button delBtn = activityDialog.findViewById(R.id.delete_activity_btn);

        if (title.equals(CREATE_TITLE)){
            okayBtn.setText(R.string.create);
            delBtn.setVisibility(View.INVISIBLE);
            delBtn.setEnabled(false);
            isCreate = true;
        } else if (title.equals(EDIT_TITLE)){
            okayBtn.setText(R.string.save);
            isCreate = false;
            Activity activity = myActivitiesList.get(position);
            activityName.setText(activity.getName());
            howLong.setText(Integer.toString(activity.getDuration()));
            addedWater.setText(Double.toString(activity.getAddedWater()));
        }


        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = activityName.getText().toString();
                String duration = howLong.getText().toString();
                String water = addedWater.getText().toString();
                activityDialog.dismiss();
                didCreateEditActivity(name, duration, water, position);
            }
        });

        cancelBtn = (Button) activityDialog.findViewById(R.id.create_edit_activity_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityDialog.dismiss();

            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity editedActivity = myActivitiesList.get(position);
                ActivityManager.getDefaultManager().removeActivity(editedActivity);
                myActivitiesList.remove(editedActivity);
                activitiesAdapter.notifyDataSetChanged();
                activityDialog.dismiss();
            }
        });
    }
    
    private void didCreateEditActivity(String name, String duration, String addedWater, int position) {
        int dur = 0;
        double water = 0;
        try{
            dur = Integer.parseInt(duration);
            water = Double.parseDouble(addedWater);
        }catch (NumberFormatException e){
            Log.e("ERROR", "LOL");
            this.showErrorDialog("Invalid Form", "Please provide the proper fields");
            e.printStackTrace();
        }

        if (name.equals("")) {
            // Don't accept blank names, show an error
            this.showErrorDialog("Invalid Name", "Please provide a proper name for the activity");
            return;
        }
        if (isCreate){ //create a new activity
            Activity newActivity = new Activity(name, dur, water);
            ActivityManager.getDefaultManager().setActivity(newActivity);
            myActivitiesList.add(newActivity);
            activitiesAdapter.notifyDataSetChanged();
        } else {
            // edit an activity
            Activity editedActivity = myActivitiesList.get(position);
            editedActivity.setName(name);
            editedActivity.setDuration(dur);
            editedActivity.setAddedWater(water);
            ActivityManager.getDefaultManager().setActivity(editedActivity);
            myActivitiesList.set(position, editedActivity);
            activitiesAdapter.notifyDataSetChanged();
        }
    }
    
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which) {

                    }

                })
                .show();
    }
}
