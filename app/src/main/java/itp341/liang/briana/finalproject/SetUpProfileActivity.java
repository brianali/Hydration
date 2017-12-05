package itp341.liang.briana.finalproject;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import itp341.liang.briana.finalproject.model.managers.StorageManager;
import itp341.liang.briana.finalproject.model.managers.UserManager;
import itp341.liang.briana.finalproject.model.objects.UserInfo;

public class SetUpProfileActivity extends AppCompatActivity {
    public static final String WEIGHT = "weight";
    public static final String WATER_GOAL = "watergoal";
    private TextView username;
    private EditText weightField, dailyWaterField;
    private Button finishBtn;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(R.string.title_setup);
        username = (TextView)findViewById(R.id.username_text);
        weightField = (EditText)findViewById(R.id.weight_field);
        dailyWaterField = (EditText)findViewById(R.id.daily_water_goal_field);
        finishBtn = (Button)findViewById(R.id.finish_button);

        email = getIntent().getStringExtra(SignUpActivity.EMAIL);
        username.setText(email);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SetUpProfileActivity.this, MainActivity.class);
                UserInfo user = new UserInfo(email, Double.parseDouble(weightField.getText().toString()), Double.parseDouble(dailyWaterField.getText().toString()));
                i.putExtra(WEIGHT, user);
//                i.putExtra(WATER_GOAL, dailyWaterField.getText().toString());
                startActivity(i);
                finish();
            }
        });
    }
}
