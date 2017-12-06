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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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


    private String [] titles = {"Change Email", "Change Weight", "Change Target Fluid Goal", "Change Password"};
    private String [] desc = {"Set New Email", "Set New Weight (lbs)", "Set New Target Fluid Goal (oz)", "Set New Password"};
    private ArrayList<SettingsData> settingsList;
    private String [] textList = {"Email", "Weight", "Daily Fluid Goal", "Change Password", "Sign Out"};
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
                    case 3: // change password:
                        changePassword();
                        break;
                    case 4: // logout:
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
    private EditText oPField, nPassField;
    private String nPass, oP;
    private void changePassword(){
        final AlertDialog passwordDialog = new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(R.layout.change_password_dialog)
                .create();
        passwordDialog.show();
        oPField = passwordDialog.findViewById(R.id.provide_old_pass_field);
        nPassField = passwordDialog.findViewById(R.id.provide_new_pass_field);
        Button changeBtn = passwordDialog.findViewById(R.id.change_pass_btn);
        Button cancelBtn = passwordDialog.findViewById(R.id.change_pass_cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordDialog.dismiss();
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oP = oPField.getText().toString();
                nPass = nPassField.getText().toString();
                if (oP.trim().equals("") || nPass.trim().equals("")){
                    // Don't accept blank names, show an error
                    showErrorDialog("Invalid Password", "Please fill out the fields");
                    return;
                }
                else if (oP.trim().length() <6 || nPass.trim().length() <6){
                    showErrorDialog("Invalid Password", "Minimum password length is 6");
                    return;
                } else {
                    updatePassword();
                    passwordDialog.dismiss();
                }
            }
        });
    }
    private void updatePassword(){

        final String email = currUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oP);

        currUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    currUser.updatePassword(nPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password has been updated. Please sign in with new password.", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to update password!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(), "Failed to update password!", Toast.LENGTH_LONG).show();
                }
            }
        });
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
    private EditText oldPassField;
    boolean isChanged = true;
    private String newEmail, oldPass;
    private void didEditField(String editField, final int position) {
        // if username, change email
        if (position ==0){
            if (editField.equals("")) {
                // Don't accept blank names, show an error
                this.showErrorDialog("Invalid Name", "Please provide a proper name");
                return;
            }
            newEmail = editField.trim();
            final AlertDialog passwordDialog = new AlertDialog.Builder(this)
                    .setTitle("Confirm Email Change")
                    .setView(R.layout.change_email_dialog)
                    .create();
            passwordDialog.show();
            oldPassField = passwordDialog.findViewById(R.id.provide_new_pass_field);
            Button changeBtn = passwordDialog.findViewById(R.id.change_pass_btn);
            Button cancelBtn = passwordDialog.findViewById(R.id.change_pass_cancel_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passwordDialog.dismiss();
                }
            });
            changeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    oldPass = oldPassField.getText().toString();
                    if (oldPass.trim().equals("")){
                        // Don't accept blank names, show an error
                        showErrorDialog("Invalid Password", "Please provide a proper password");
                        isChanged = false;
                        return;
                    }
                    else if (oldPass.trim().length() <6){
                        showErrorDialog("Invalid Password", "Minimum password length is 6");
                        isChanged = false;
                        return;
                    } else {
                        updateEmail(position);
                        passwordDialog.dismiss();
                    }
                }
            });
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
        if (isChanged){
            UserManager.getDefaultManager().setUserInfo(user);
            fieldList = new String[]{username, Double.toString(user.getWeight()) + " lbs",
                    Double.toString(user.getDailyWaterGoal()) + " oz", ""};
            settingsList.set(position, new SettingsData(textList[position], fieldList[position]));
            adapter.notifyDataSetChanged();
        }
    }
    private boolean updateEmail(final int position){
        final boolean[] updated = {false};
        // Prompt the user to re-provide their sign-in credentials
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        final String email = currUser.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);

        currUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    currUser.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Email address is updated. Please sign in with new email!", Toast.LENGTH_LONG).show();
                                        updated[0] = true;
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to update email!", Toast.LENGTH_LONG).show();
                                        updated[0] = false;
                                    }
                                }
                            });
                    updated[0] = true;
                    user.setName(newEmail);
                    UserManager.getDefaultManager().setUserInfo(user);
                    fieldList = new String[]{username, Double.toString(user.getWeight()) + " lbs",
                            Double.toString(user.getDailyWaterGoal()) + " oz", ""};
                    settingsList.set(position, new SettingsData(textList[position], fieldList[position]));
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return updated[0];
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
                Double.toString(user.getDailyWaterGoal()) + " oz", "", ""};
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
