package itp341.liang.briana.finalproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import itp341.liang.briana.finalproject.model.objects.Activity;

import itp341.liang.briana.finalproject.model.managers.ActivityManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveActivityFragment extends Fragment {
    private final static String EDIT_TITLE = "Edit Daily Activity";
    private ListView activeActivitiesListView;
    private Button addActiveActivityBtn;
    private ArrayList<Activity> activeActivitiesList;
    private ActivityAdapter activitiesAdapter;
    private android.app.AlertDialog activityDialog;

    // edit activity dialog
    private EditText activityName, howLong, addedWater;
    private Button okayBtn, cancelBtn;

    public ActiveActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_active_activities, container, false);
        activeActivitiesListView = view.findViewById(R.id.todays_activity_list_view);
        addActiveActivityBtn = view.findViewById(R.id.add_activity_button);

        activeActivitiesList = ActivityManager.getDefaultManager().getActiveActivities();
        activitiesAdapter = new ActivityAdapter(getContext(), R.layout.dailyactivity_list_item, activeActivitiesList);
        activeActivitiesListView.setAdapter(activitiesAdapter);

        addActiveActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyActivities.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == android.app.Activity.RESULT_OK && data!=null){
            Activity dailyActivity = (Activity) data.getSerializableExtra(MyActivities.ADD_DAILY_ACTIVITY);
//            Activity newDailyActivity = new Activity(dailyActivity.getName(), dailyActivity.getDuration(), dailyActivity.getAddedWater());
            ActivityManager.getDefaultManager().setDailyActivity(dailyActivity);
            activeActivitiesList.add(dailyActivity);
            activitiesAdapter.notifyDataSetChanged();
        }
    }
    private class ActivityAdapter extends ArrayAdapter<Activity> {
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
                view = inflater.inflate(R.layout.dailyactivity_list_item, parent, false);
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
            viewHolder.amount.setText(Double.toString(activity.getAddedWater()));
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show edit activity dialog
                    showActivityDialog(EDIT_TITLE, position);
                }
            });
            // Return the completed view to render on screen
            return view;
        }
        // View lookup cache that populates the listview
        public class ViewHolder {
            TextView activityName;
            TextView duration, amount;
            Button editBtn;
            public ViewHolder(View v) {
                activityName = v.findViewById(R.id.activity_name);
                duration = v.findViewById(R.id.activity_duration);
                amount = v.findViewById(R.id.activity_amount);
                editBtn = v.findViewById(R.id.edit_activity_button);
            }
        }
    }

    /**
     * edit existing activity dialog
     * @param title title of existing activity
     * @param position position of existing activity
     */
    private void showActivityDialog(String title, final int position) {
        activityDialog = new AlertDialog.Builder(getContext())
                .setTitle(EDIT_TITLE)
                .setView(R.layout.create_edit_activity_dialog)
                .create();
        activityDialog.show();

        activityName = (EditText) activityDialog.findViewById(R.id.activity_name);
        howLong = (EditText) activityDialog.findViewById(R.id.how_long_field);
        addedWater = (EditText) activityDialog.findViewById(R.id.added_water_intake_field);
        okayBtn = (Button) activityDialog.findViewById(R.id.create_edit_activity_btn);
        Button delBtn = activityDialog.findViewById(R.id.delete_activity_btn);


        okayBtn.setText(R.string.save);
        Activity activity = activeActivitiesList.get(position);
        activityName.setText(activity.getName());
        howLong.setText(Integer.toString(activity.getDuration()));
        addedWater.setText(Double.toString(activity.getAddedWater()));

        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = activityName.getText().toString();
                String duration = howLong.getText().toString();
                String water = addedWater.getText().toString();
                activityDialog.dismiss();
                didEditActivity(name, duration, water, position);
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
                Activity editedActivity = activeActivitiesList.get(position);
                ActivityManager.getDefaultManager().removeDailyActivity(editedActivity);
                activeActivitiesList.remove(editedActivity);
                ArrayList<Activity> all = ActivityManager.getDefaultManager().getActiveActivities();
                activitiesAdapter.notifyDataSetChanged();
                activityDialog.dismiss();
            }
        });
    }

    private void didEditActivity(String name, String duration, String addedWater, int position) {
        int dur = 0;
        double water = 0;
        try{
            dur = Integer.parseInt(duration);
        }catch (NumberFormatException e){
            this.showErrorDialog("Invalid Value", "Please provide a whole number value (mins)");
            e.printStackTrace();
            return;
        }
        try{
            water = Double.parseDouble(addedWater);
        }catch (NumberFormatException e){
            Log.e("ERROR", "LOL");
            this.showErrorDialog("Invalid Value", "Please provide a proper number value (oz)");
            e.printStackTrace();
            return;
        }

        if (name.equals("")) {
            // Don't accept blank names, show an error
            this.showErrorDialog("Invalid Name", "Please provide a proper name for the edited activity");
            return;
        }

        // edit an activity
        Activity editedActivity = activeActivitiesList.get(position);
        editedActivity.setName(name);
        editedActivity.setDuration(dur);
        editedActivity.setAddedWater(water);
        ActivityManager.getDefaultManager().setDailyActivity(editedActivity);
        activeActivitiesList.set(position, editedActivity);
        activitiesAdapter.notifyDataSetChanged();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
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
