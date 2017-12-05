package itp341.liang.briana.finalproject;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import itp341.liang.briana.finalproject.model.managers.ActivityManager;
import itp341.liang.briana.finalproject.model.managers.FluidManager;
import itp341.liang.briana.finalproject.model.managers.UserManager;
import itp341.liang.briana.finalproject.model.objects.Fluid;
import itp341.liang.briana.finalproject.model.objects.UserInfo;


/**
 * Overview Fragment for displaying the day's current drink status
 */
public class OverviewFragment extends Fragment {
    private static final String ADD_FLUID_TITLE = "Add Fluid Intake";
    private static final String EDIT_FLUID_TITLE = "Edit Fluid Intake";
    private ProgressBar dailyProgress;
    private TextView currOz, totalOz;
    private TextView morningOz, afternoonOz, eveningOz;
    private FloatingActionButton addDrinkBtn;
    private ListView fluidListView; 
    private PieChart fluidPieChart;
    private ArrayList<Fluid> fluidsList;
    private FluidAdapter fluidAdapter;
    private android.app.AlertDialog fluidDialog;
    private UserInfo user;
    // dialog members:
    EditText fluidName, fluidAmount;
    Button addBtn, delBtn, cancelBtn;
    boolean isCreate = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_overview, container, false);
        dailyProgress = view.findViewById(R.id.daily_goal_progress_bar);
        currOz = view.findViewById(R.id.daily_goal_progress_text);
        totalOz = view.findViewById(R.id.daily_goal_total_text);
        morningOz = view.findViewById(R.id.morning_ounces_text);
        afternoonOz = view.findViewById(R.id.afternoon_ounces_text);
        eveningOz = view.findViewById(R.id.evening_ounces_text);
        addDrinkBtn = view.findViewById(R.id.add_drink_btn);
        fluidListView = view.findViewById(R.id.fluid_list_view);
        fluidPieChart = view.findViewById(R.id.total_pie_chart);

        fluidsList = FluidManager.getDefaultManager().getAllFluids();
        fluidAdapter = new FluidAdapter(getContext(), R.layout.dailyfluid_list_item, fluidsList);
        fluidListView.setAdapter(fluidAdapter);
        //pull up dialog for adding a drink to the day
        addDrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFluidDialog(ADD_FLUID_TITLE, -1);
            }
        });
        FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
        user = UserManager.getDefaultManager().getUserInfoWithName(currUser.getEmail());

        setOverviewData();
        return view;
    }

    // set the daily overview
    private void setOverviewData(){
        checkReset();
        setTotalOz();
        currOz.setText(getFluidIntake());
        dailyProgress.setMax((int)user.getDailyWaterGoal());
        dailyProgress.setProgress((int)Double.parseDouble(getFluidIntake()));
        morningOz.setText(Double.toString(FluidManager.getDefaultManager().getTotalMorningFluids()));
        afternoonOz.setText(Double.toString(FluidManager.getDefaultManager().getTotalAfternoonFluids()));
        eveningOz.setText(Double.toString(FluidManager.getDefaultManager().getTotalEveningFluids()));
    }
    private void checkReset(){
        Calendar curr = Calendar.getInstance();
    }
    private void setTotalOz(){
        double total = user.getDailyWaterGoal();
        ActivityManager.getDefaultManager().getActiveActivities();
        totalOz.setText(Double.toString(total));
    }
    private String getFluidIntake(){
        double total = 0;
        for (Fluid fluid: fluidsList){
            total += fluid.getAmount();
        }
        return  Double.toString(total);
    }
    private class FluidAdapter extends ArrayAdapter<Fluid> {
        private List<Fluid> fluids;
        private FluidAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Fluid> fluids) {
            super(context, resource, fluids);
            this.fluids = fluids;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            // Get the data item for this position
            final Fluid fluid = fluids.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder = null; // view lookup cache stored in tag
            //create new row view if null
            if (view == null) {
                // If there's no view to re-use, inflate a brand new view for row
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.dailyfluid_list_item, parent, false);
                viewHolder = new ViewHolder(view);
                // Cache the viewHolder object inside the fresh view
                view.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) view.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.fluidType.setText(fluid.getName());
            viewHolder.amount.setText(Double.toString(fluid.getAmount()));
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // show edit Fluid dialog
                    showFluidDialog(EDIT_FLUID_TITLE, position);
                }
            });
            // Return the completed view to render on screen
            return view;
        }
        // View lookup cache that populates the listview
        public class ViewHolder {
            TextView fluidType;
            TextView amount;
            Button editBtn;
            public ViewHolder(View v) {
                fluidType = v.findViewById(R.id.fluid_name);
                amount = v.findViewById(R.id.fluid_amount);
                editBtn = v.findViewById(R.id.edit_fluid_button);
            }
        }
    }

    /**
     * create new Fluid/edit existing Fluid dialog
     * @param title create or edit Fluid title
     * @param position position of existing Fluid
     */
    private void showFluidDialog(String title, final int position) {
        fluidDialog = new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setView(R.layout.create_edit_fluid_dialog)
                .create();
        fluidDialog.show();
        fluidName = (EditText) fluidDialog.findViewById(R.id.fluid_name);
        fluidAmount = (EditText) fluidDialog.findViewById(R.id.water_amount);
        addBtn = (Button) fluidDialog.findViewById(R.id.create_edit_fluid_btn);
        Button delBtn = fluidDialog.findViewById(R.id.delete_fluid_btn);

        if (title.equals(ADD_FLUID_TITLE)){
            addBtn.setText(R.string.add);
            delBtn.setVisibility(View.INVISIBLE);
            delBtn.setEnabled(false);
            isCreate = true;
        } else if (title.equals(EDIT_FLUID_TITLE)){
            addBtn.setText(R.string.save);
            isCreate = false;
            Fluid fluid = fluidsList.get(position);
            fluidName.setText(fluid.getName());
            fluidAmount.setText(Double.toString(fluid.getAmount()));
        }


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fluidName.getText().toString();
                String amount = fluidAmount.getText().toString();
                fluidDialog.dismiss();
                didCreateEditFluid(name, amount, position);
            }
        });

        cancelBtn = (Button) fluidDialog.findViewById(R.id.create_edit_fluid_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fluidDialog.dismiss();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fluid editedFluid = fluidsList.get(position);
                FluidManager.getDefaultManager().removeFluid(editedFluid);
                fluidsList.remove(editedFluid);
                fluidAdapter.notifyDataSetChanged();
                fluidDialog.dismiss();
                setOverviewData();
            }
        });
    }

    private void didCreateEditFluid(String name, String amount, int position) {
        double amt = 0;
        try{
            amt = Double.parseDouble(amount);
        }catch (NumberFormatException e){
            Log.e("ERROR", "LOL");
            this.showErrorDialog("Invalid Form", "Please provide the proper fields");
            e.printStackTrace();
            return;
        }

        if (name.equals("")) {
            // Don't accept blank names, show an error
            this.showErrorDialog("Invalid Name", "Please provide a proper name for the Fluid");
            return;
        }
        if (isCreate){ //create a new Fluid
            Fluid newFluid = new Fluid(name, amt);
            FluidManager.getDefaultManager().setFluid(newFluid);
            fluidsList.add(newFluid);
            fluidAdapter.notifyDataSetChanged();
        } else {
            // edit an Fluid
            Fluid editedFluid = fluidsList.get(position);
            int mins = editedFluid.getTimestamp().get(Calendar.MINUTE);
            int hours = editedFluid.getTimestamp().get(Calendar.HOUR_OF_DAY);
            int currMinOfDay = ((hours * 60) + mins);
            editedFluid.setName(name);
            editedFluid.setAmount(amt);
            FluidManager.getDefaultManager().setFluid(editedFluid);
            fluidsList.set(position, editedFluid);
            fluidAdapter.notifyDataSetChanged();
        }
        setOverviewData();
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
