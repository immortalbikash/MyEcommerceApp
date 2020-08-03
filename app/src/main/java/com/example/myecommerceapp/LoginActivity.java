package com.example.myecommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.UiAutomation;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myecommerceapp.Model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;

    ProgressDialog pd;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(this);

        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        LoginButton = (Button) findViewById(R.id.login_btn);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

    }

    private void LoginUser() {
        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            InputPassword.setError("Phone number is Empty");
            InputPassword.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            InputPassword.setError("Password is Empty");
            InputPassword.requestFocus();
            return;
        } else {
            pd.setTitle("Login Account");
            pd.setMessage("Please wait, we are checking the credentials.");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            AllowAccessToAccount(phone, password);
        }

    }

    private void AllowAccessToAccount(final String phone, final String password) {
        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()) {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {
                        if (usersData.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            pd.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Account with " + phone + " does not exists.", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
