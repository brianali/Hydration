package itp341.liang.briana.finalproject;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import itp341.liang.briana.finalproject.model.managers.UserManager;
import itp341.liang.briana.finalproject.model.objects.UserInfo;

public class SettingsActivity extends AppCompatActivity {
    private ListView settingsListView;
    private SettingsAdapter adapter;
    private AlertDialog settingsDialog;
    private FirebaseAuth auth;
    private String username;
    private UserInfo user;
    private FirebaseUser currUser;

    // settings dialog members
    private TextView textField;
    private EditText editField;
    private Button saveBtn, cancelBtn;


    private String [] titles = {"Change Username", "Change Weight", "Change Daily Water Goal"};
    private String [] desc = {"Set New Username", "Set New Weight (lbs)", "Set New Daily Water Goal (oz)"};
    private ArrayList<SettingsData> settingsList;
    private String [] textList = {"Username", "Weight", "Daily Water Goal", "Sign Out"};
    private String[] fieldList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(R.string.title_settings);
        auth = FirebaseAuth.getInstance();
        currUser = auth.getCurrentUser();
        username = auth.getCurrentUser().getEmail();
        ArrayList<UserInfo> all = UserManager.getDefaultManager().getAllUserInfos();

        user = UserManager.getDefaultManager().getUserInfoWithName(username);
        setUpSettingsList();
        settingsListView = (ListView)findViewById(R.id.settings_list_view);
        adapter = new SettingsAdapter(getApplicationContext(), R.layout.settings_list_item, settingsList);
        settingsListView.setAdapter(adapter);

        settingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0: // username
                        showSettingsDialog(titles[0], desc[0], i);
                        break;
                    case 1: // weight:
                        showSettingsDialog(titles[1], desc[1], i);
                        break;
                    case 2: // daily water goal:
                        showSettingsDialog(titles[2], desc[2], i);
                        break;
                    case 3: // logout:
                        logOut();
                        break;
                }
            }
        });
    }

    private class SettingsAdapter extends ArrayAdapter<SettingsData> {
        ArrayList<SettingsData> settingsList;
        private SettingsAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<SettingsData> list) {
            super(context, resource, list);
            settingsList = list;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder = null; // view lookup cache stored in tag
            //create new row view if null
            SettingsData data = settingsList.get(position);
            if (view == null) {
                // If there's no view to re-use, inflate a brand new view for row
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.settings_list_item, parent, false);
                viewHolder = new ViewHolder(view);
                // Cache the viewHolder object inside the fresh view
                view.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) view.getTag();
            }
            // Populate the data from the data object via the viewHolder object
            // into the template view.
            viewHolder.field.setText(data.getField());
            viewHolder.text.setText(data.getText());

            // Return the completed view to render on screen
            return view;
        }
        // View lookup cache that populates the listview
        public class ViewHolder {
            TextView text;
            TextView field;
            public ViewHolder(View v) {
                text = v.findViewById(R.id.text);
                field = v.findViewById(R.id.field);
            }
        }
    }

    /**
     * edit existing settings dialog
     * @param title create or edit settings item title
     * @param desc desc of existing settings item
     */
    private void showSettingsDialog(String title, final String desc, final int position) {
        settingsDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(R.layout.create_edit_settings_dialog)
                .create();
        settingsDialog.show();
        textField = (TextView) settingsDialog.findViewById(R.id.settings_text);
        editField = (EditText) settingsDialog.findViewById(R.id.settings_edit_text);
        saveBtn = (Button) settingsDialog.findViewById(R.id.create_edit_settings_btn);

        textField.setText(desc);
        String text = "";
        if (position == 0){
            text = username;
        } else if (position == 1){
            text = Double.toString(user.getWeight());
        } else if (position == 2) {
            text = Double.toString(user.getDailyWaterGoal());
        }
        editField.setText(text);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
                didEditField(editField.getText().toString(), position);
            }
        });

        cancelBtn = (Button) settingsDialog.findViewById(R.id.create_edit_settings_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.dismiss();
            }
        });
    }

    private void didEditField(String editField, int position) {
        // if username, set username
        if (position ==0){
            if (editField.equals("")) {
                // Don't accept blank names, show an error
                this.showErrorDialog("Invalid Name", "Please provide a proper name");
                return;
            }
            String newEmail = editField.trim();
            if (currUser != null && !newEmail.equals("")) {
                currUser.updateEmail(newEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email address is updated. Please sign in with new email!", Toast.LENGTH_LONG).show();
                                    logOut();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to update email!", Toast.LENGTH_LONG).show();
                                    logOut();
                                }
                            }
                        });
            }
            user.setName(newEmail);
        } else if (position == 1 || position == 2){
            double value;
            try{
                value = Double.parseDouble(editField);
            }catch (NumberFormatException e){
                this.showErrorDialog("Invalid Form", "Please provide a proper value");
                e.printStackTrace();
                return;
            }
            if (position ==1){
                user.setWeight(value);
            } else if (position ==2){
                user.setDailyWaterGoal(value);
            }
        }
        UserManager.getDefaultManager().setUserInfo(user);
        fieldList = new String[]{username, Double.toString(user.getWeight()) + " lbs",
                Double.toString(user.getDailyWaterGoal()) + " oz", ""};
        settingsList.set(position, new SettingsData(textList[position], fieldList[position]));
        adapter.notifyDataSetChanged();
    }
    private void logOut(){
        auth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
    private void setUpSettingsList(){
        settingsList = new ArrayList<>();
        fieldList = new String[]{username, Double.toString(user.getWeight()) + " lbs",
                Double.toString(user.getDailyWaterGoal()) + " oz", ""};
        for (int i = 0; i<textList.length; i++){
            settingsList.add(new SettingsData(textList[i], fieldList[i]));
        }
    }
    private class SettingsData{
        String text;
        String field;

        public SettingsData(String text, String field) {
            this.text = text;
            this.field = field;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
