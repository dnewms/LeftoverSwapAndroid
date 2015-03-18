package com.teespire.leftoverswap.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.parse.SignUpCallback;
import com.teespire.leftoverswap.LS_Manager;
import com.teespire.leftoverswap.R;


public class LS_SignupActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    Button SignUpButton;
    Button CancelButton;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);


        usernameEditText = (EditText) findViewById(R.id.editText_userName);
        usernameEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateDoneButtonState();

            }
        });
        emailEditText = (EditText) findViewById(R.id.editText_email);
        emailEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateDoneButtonState();
            }
        });

        // Set up the submit button click handler
         SignUpButton = (Button) findViewById(R.id.btn_done);
        SignUpButton.setEnabled(false);

        SignUpButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                if( LS_Manager.NetworkAvailable(LS_SignupActivity.this))
                {
                    //signUp();
                    new SignUp().execute();
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
    }

    private void updateDoneButtonState ()

    {
        int length_Username = usernameEditText.getText().toString().trim().length();
        int length_Password = passwordEditText.getText().toString().trim().length();
        int length_Email = emailEditText.getText().toString().trim().length();
        boolean enabled = length_Username > 0 && length_Password > 0 && length_Email>0;
        SignUpButton.setEnabled(enabled);
    }

   /* public void signUp()
    {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Wait");
        dialog.show();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        // Set up a new Parse user
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                 dialog.dismiss();

                if (e != null)
                {
                    Toast.makeText(LS_SignupActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();


                } else if(e==null)
                {


                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(LS_SignupActivity.this, LS_DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });

    }*/


    private class SignUp extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(LS_SignupActivity.this);
            dialog.setMessage("Signing Up...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }

        @Override
        protected Void  doInBackground(Void... params)
        {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            // Set up a new Parse user
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            // Call the Parse signup method
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {


                    if (e != null)
                    {
                        Toast.makeText(LS_SignupActivity.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();


                    } else if(e==null)
                    {


                        // Start an intent for the dispatch activity
                        Intent intent = new Intent(LS_SignupActivity.this, LS_DispatchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                }
            });
            return  null;

        }



    }
}
