package itp341.liang.briana.finalproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Overview Fragment for displaying the day's current drink status
 */
public class OverviewFragment extends Fragment {
    private ProgressBar dailyProgress;
    private TextView currOz, totalOz;
    private TextView morningOz, afternoonOz, eveningOz;
    private FloatingActionButton addDrinkBtn;

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

        //pull up dialog for adding a drink to the day
        addDrinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(OverviewFragment.this, DrinkActivity.class);
//                startActivityForResult(intent, 0);
            }
        });
        return view;
    }
}
