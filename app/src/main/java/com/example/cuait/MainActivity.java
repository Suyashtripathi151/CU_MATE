package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private Button forgot,login,create;
    private EditText uid,password;

    private ImageView img;

    FirebaseAuth uauth;
    FirebaseUser user;
    public static final String Shared_Pref="SharedPref";

    String EmailPattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img=findViewById(R.id.logo1);

        forgot=findViewById(R.id.forgotbutton1);
        login=findViewById(R.id.loginbutton1);
        create=findViewById(R.id.alreadybutton1);

        uid=findViewById(R.id.inpemail1);
        password=findViewById(R.id.inppass1);


        uauth=FirebaseAuth.getInstance();
        user=uauth.getCurrentUser();


        String Password_=password.getText().toString();
        String uid_=uid.getText().toString();

        checkBox();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ForgotActivity.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NonEmpty();
            }
        });

    }
    public void NonEmpty(){
        String Password_=password.getText().toString();
        String uid_=uid.getText().toString();

        if(uid_.isEmpty()){
            uid.setError("This Field Is Emty ! ");
        }

        if(Password_.isEmpty()){
            password.setError("This Field Is Emty ! ");
        }
        if(Password_.length()<6){
            password.setError("Password Must be at keast 6 digit long");
        }

        if(uid_.isEmpty() || Password_.isEmpty()){
            Toast.makeText(MainActivity.this, "All Fileds are Mandatory !", Toast.LENGTH_SHORT).show();
        }

        if((!uid_.matches(EmailPattern)))  {
            uid.setError("Email is not Valid !");
        }

        else{
            checklogin();
        }
    }

    public void checklogin(){
        String Password_=password.getText().toString();
        String uid_=uid.getText().toString();




        uauth.signInWithEmailAndPassword(uid_,Password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    SharedPreferences sharedPreferences=getSharedPreferences(Shared_Pref,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("Pass","true");
                    editor.apply();


                    FirebaseUser user = uauth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        Intent intent=new Intent(MainActivity.this,HopmePage2.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Please Verify Your E-Mail", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(MainActivity.this,VerfifyActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_SHORT).show();
                }






            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage()+ "", Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void checkBox(){
        SharedPreferences sharedPreferences=getSharedPreferences(Shared_Pref,MODE_PRIVATE);
        String check=sharedPreferences.getString("Pass","");
        if(check.equals("true")){
            Intent intent=new Intent(MainActivity.this,HopmePage2.class);
            startActivity(intent);
            finish();
        }
    }
}