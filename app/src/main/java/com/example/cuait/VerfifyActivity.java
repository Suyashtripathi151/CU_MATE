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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerfifyActivity extends AppCompatActivity {
    private EditText inpemail;
    private Button verify;
    private TextView multi,title;
    private ImageView logo;


    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref;
    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";
    String EmailPattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verfify);

        inpemail=findViewById(R.id.inpemail4);
        verify=findViewById(R.id.verifybutton);
        multi=findViewById(R.id.multilinetext4);
        title=findViewById(R.id.verifytext4);
        logo=findViewById(R.id.logo4);



        uauth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = uauth.getCurrentUser();


        verify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email=inpemail.getText().toString();

                if(email.matches(EmailPattern2)){

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(VerfifyActivity.this, "SENT", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(VerfifyActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VerfifyActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });


//                    uauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(VerfifyActivity.this, "Check Your  E-Mail", Toast.LENGTH_LONG).show();
//                                Intent intent=new Intent(VerfifyActivity.this,MainActivity.class);
//                                startActivity(intent);
//                            }
//                            else {
//                              Toast.makeText(VerfifyActivity.this, "E-Mai Not Sentl", Toast.LENGTH_LONG).show();
//                                inpemail.setText(" ");
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(VerfifyActivity.this, "Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    });



                }


                else if(!email.matches(EmailPattern)){
                    verify.setError("Email is not Valid !");
                }

                else if (email.isEmpty()) {
                    inpemail.setError("Required");
                }

                else if (email.matches(EmailPattern)) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(VerfifyActivity.this, "SENT", Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(VerfifyActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(VerfifyActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                        }
                    });



                }



            }
        });



        };

    }
