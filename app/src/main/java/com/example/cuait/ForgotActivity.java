package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotActivity extends AppCompatActivity {

    private EditText inpusermail;
    private ImageView logo;
    private TextView resettxt;
    private Button reset;
    String usermail;

    private FirebaseAuth uauth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        uauth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = uauth.getCurrentUser();

        inpusermail=findViewById(R.id.inpuseremail);
        logo=findViewById(R.id.logoimg);
        resettxt=findViewById(R.id.forgottxt);
        reset=findViewById(R.id.btnreset);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usermail=inpusermail.getText().toString();
                if(usermail.isEmpty()){
                    inpusermail.setError("Required");
                }
                else{
                    uauth.sendPasswordResetEmail(usermail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                          if(task.isSuccessful()){
                              Toast.makeText(ForgotActivity.this, "Check Your  E-Mail", Toast.LENGTH_LONG).show();
                              Intent intent=new Intent(ForgotActivity.this,MainActivity.class);
                              startActivity(intent);
                          }
                          else {
                              Toast.makeText(ForgotActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
                              inpusermail.setText(" ");
                          }
                        }
                    });


                }
            }
        });


    }
}