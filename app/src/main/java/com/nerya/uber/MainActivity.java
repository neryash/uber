package com.nerya.uber;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {
private Button signup, onetime;
private EditText password, username, pOd;
private RadioButton passenger, driver;
    enum State {
        SIGNUP, LOGIN
    }
    private State state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup = findViewById(R.id.signup);
        onetime = findViewById(R.id.onetime);

        password = findViewById(R.id.password);
        pOd = findViewById(R.id.PorD);
        username = findViewById(R.id.username);

        passenger = findViewById(R.id.passenger);
        driver = findViewById(R.id.driver);

        if(ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }
state = State.SIGNUP;
signup.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(state == State.SIGNUP){
            if(driver.isChecked() == false && passenger.isChecked() == false){
                Toast.makeText(MainActivity.this, "driver or passenger?", Toast.LENGTH_SHORT).show();
                return;
            }
            ParseUser appUser = new ParseUser();
            appUser.setUsername(username.getText().toString());
            appUser.setPassword(password.getText().toString());
            if(driver.isChecked() == true){
                appUser.put("as", "Driver");

            }else {
                appUser.put("as", "Passenger");
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
            appUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        Toast.makeText(MainActivity.this, "Signed Up successfully!", Toast.LENGTH_SHORT).show();
                        transitionP();
                    }else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user != null && e == null){
                        transitionP();
                        Toast.makeText(MainActivity.this, "logged in succesfully", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
});
onetime.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(pOd.getText().toString().equals("Driver") || pOd.getText().toString().equals("Passenger")){
            if(ParseUser.getCurrentUser() == null){
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user != null && e == null){
                            Toast.makeText(MainActivity.this, "you logged in anonymously", Toast.LENGTH_SHORT).show();
                            user.put("as", pOd.getText().toString());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    transitionP();
                                }
                            });
                        }else{
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }else {
            Toast.makeText(MainActivity.this, "are you a driver or a passenger?", Toast.LENGTH_SHORT).show();
        }
    }
});
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

                if (state == State.SIGNUP) {
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    signup.setText("Log In");
                } else if (state == State.LOGIN) {

                    state = State.SIGNUP;
                    item.setTitle("Log In");
                    signup.setText("Sign Up");
                }


        return super.onOptionsItemSelected(item);
    }

    private void transitionP(){
        if(ParseUser.getCurrentUser() != null){
            if(ParseUser.getCurrentUser().get("as").equals("Passenger")){
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(MainActivity.this, "hello", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
