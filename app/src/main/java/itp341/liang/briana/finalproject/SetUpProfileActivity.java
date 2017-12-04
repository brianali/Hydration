package itp341.liang.briana.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
                i.putExtra(WEIGHT, weightField.getText().toString());
                i.putExtra(WATER_GOAL, dailyWaterField.getText().toString());
                startActivity(i);
                finish();
            }
        });
    }
}
