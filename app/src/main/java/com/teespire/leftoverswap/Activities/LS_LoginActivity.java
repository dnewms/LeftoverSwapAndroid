package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.teespire.leftoverswap.LS_Manager;
import com.teespire.leftoverswap.R;


public class LS_LoginActivity extends Activity {
    // UI references.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Context context;
    Button LoginButton;
    Button CancelButton;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        usernameEditText = (EditText) findViewById(R.id.editText_userName);
        usernameEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateDoneButtonState();

            }
        });

        passwordEditText = (EditText) findViewById(R.id.editText_password);
        passwordEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateDoneButtonState();
            }
        });

        // Set up the submit button click handler
        LoginButton = (Button) findViewById(R.id.btn_done);
        LoginButton.setEnabled(false);
        LoginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if( LS_Manager.NetworkAvailable(LS_LoginActivity.this)) {
                    new LoginTask().execute();
                }
            }
        });

        CancelButton=(Button)findViewById(R.id.btn_cancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        context = this;
    }

    private void updateDoneButtonState ()
    {
        int length_Username = usernameEditText.getText().toString().trim().length();
        int length_Password = passwordEditText.getText().toString().trim().length();
        boolean enabled = length_Username > 0 && length_Password > 0;
        LoginButton.setEnabled(enabled);
    }

    private class LoginTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Logging In...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            String username = usernameEditText.getText().toString().toLowerCase().trim();
            String password = passwordEditText.getText().toString().trim();

            // Call the Parse login method

            try{
                ParseUser.logIn(username, password);
                return null;
            }
            catch (ParseException pe){
                return pe.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(dialog != null) dialog.dismiss();
            if(result == null){
                Intent intent = new Intent(context, LS_DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else{
                Toast.makeText(context, "Login error: " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
