package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.service.quickaccesswallet.QuickAccessWalletService;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity2 extends AppCompatActivity {

    private EditText Conpass,Pass,UID,CUMAIL;
    private Button Register,Already;
    private ImageView img;

    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref,dbrefusername;

    String currentUser,USERNAME;


    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        img=findViewById(R.id.logo3);

        Conpass=findViewById(R.id.inpconpass3);
        Pass=findViewById(R.id.inppass3);
        UID=findViewById(R.id.inpuid3);
        CUMAIL=findViewById(R.id.inpcumail3);

        Register=findViewById(R.id.register3);
        Already=findViewById(R.id.alreadybutton3);

        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();



        Boolean ExistingStudent= getIntent().getExtras().getBoolean("Existing");


        if(ExistingStudent==true){
            UID.setEnabled(true);
            UID.setVisibility(View.VISIBLE);
            CUMAIL.setEnabled(true);
            CUMAIL.setVisibility(View.VISIBLE);

        }
        else {
            UID.setText("Enter Your Username");
//            UID.setEnabled(false);
//            UID.setVisibility(View.INVISIBLE);
            CUMAIL.setEnabled(false);
            CUMAIL.setVisibility(View.INVISIBLE);
        }
        Already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity2.this,MainActivity.class);
                startActivity(intent);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(uauth.getCurrentUser() !=null){
            openMain();
        }
    }

    private void openMain(){
//        Intent intent=new Intent(RegisterActivity2.this,Homepage.class);
//        startActivity(intent);
    }

    public void check(){
        String Conpass_=Conpass.getText().toString();
        String Pass_=Pass.getText().toString();
        String UID_=UID.getText().toString();
        String CUMAIL_=CUMAIL.getText().toString();
        Boolean ExistingStudent= getIntent().getExtras().getBoolean("Existing");

        if(ExistingStudent){
            StringMatcher matcher = new StringMatcher();
            EditText[] allinputs2={Conpass,Pass,UID,CUMAIL};
            String[] DATA2= {Conpass_,Pass_,UID_,CUMAIL_};


            if(Conpass_.isEmpty() || Pass_.isEmpty()   ||CUMAIL_.isEmpty() || UID_.isEmpty()){
                for(int i=0;i<+4;i++){
                    if (DATA2[i].isEmpty()) {
                        allinputs2[i].setError("This Field Is Missing ! ");
                    }
                }
            }

            else if (UID_.length()!=9) {
                UID.setError("Enter Valid UID");

            }

            else if (Pass_.length()<6) {
                Pass.setError("Password Must be atleast 6 digit long");
            }
            else if (!Pass_.matches(Conpass_)) {
                Conpass.setError("Password do not match");
            }
            else if(!CUMAIL_.matches(EmailPattern2)){
                CUMAIL.setError("Email is not Valid !");
            }

            else if (UID_.length()==9) {

                if (!UID_.substring(0, 9).equalsIgnoreCase(CUMAIL_.substring(0, 9))) {
                    UID.setError("Your Mail and UID Should Belong to Same Candidate");
                    CUMAIL.setError("Your Mail and UID Should Belong to Same Candidate");
                    Toast.makeText(this, "Check Your UID & CUMAIL", Toast.LENGTH_SHORT).show();
                }
                else{
                    regfinal();
                }
            }


            else{
                regfinal();
            }


        }

        else {

            if (Pass_.length() < 6) {
                Pass.setError("Password must be at least 6 characters long");
            } else if (!Pass_.matches(Conpass_)) {
                Conpass.setError("Passwords do not match");
            } else if (UID_.length() < 6 || UID_.length() > 11) {
                UID.setError("User ID must be between 6 and 11 characters");
            } else if (!UID_.matches("^[a-zA-Z][a-zA-Z0-9]{0,3}$")) {
                UID.setError("User ID must start with an alphabet and have at most 4 numeric values");
            }




            else{
                verify_username();
            }

        }

    }


     public void regfinal(){
         Boolean ExistingStudent= getIntent().getExtras().getBoolean("Existing");
         String Personal_=getIntent().getStringExtra("keypersonal");
         String CUMAIL_=CUMAIL.getText().toString();
         String Pass_=Pass.getText().toString();



         if (!ExistingStudent){

             String UID_=UID.getText().toString();

            uauth.createUserWithEmailAndPassword(Personal_,Pass_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        currentUser = uauth.getCurrentUser().getUid();
                        uploadDetails();
                        Toast.makeText(RegisterActivity2.this, "Signing in as Guest ...", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(RegisterActivity2.this,VerfifyActivity.class);
                        startActivity(intent);
                    }


                    else {
                        Toast.makeText(RegisterActivity2.this, "Registration Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity2.this, "Registration Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


         }

         else {
             uauth.createUserWithEmailAndPassword(CUMAIL_,Pass_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                         uploadDetails();
                         currentUser = uauth.getCurrentUser().getUid();
                         Toast.makeText(RegisterActivity2.this, "Signing in as CUian ...", Toast.LENGTH_LONG).show();
                         Intent intent=new Intent(RegisterActivity2.this,VerfifyActivity.class);
                         startActivity(intent);


                     }
                     else {
                         Toast.makeText(RegisterActivity2.this, "Registration Failed !"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }


             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     Toast.makeText(RegisterActivity2.this, "Registration Failed !"+e.getMessage(), Toast.LENGTH_SHORT).show();

                 }
             });
         }


     }

     public void verify_username(){
         String UID_=UID.getText().toString();

         dbrefusername=reference.child("userdata");
         dbrefusername.child(UID_).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {
                 if(task.isSuccessful()){
                     if(task.getResult().exists()){
                         DataSnapshot dataSnapshot= task.getResult();
                         Toast.makeText(RegisterActivity2.this, "Usermail Already Taken", Toast.LENGTH_SHORT).show();

                     }

                     else {
                         Toast.makeText(RegisterActivity2.this, "AVAIL", Toast.LENGTH_SHORT).show();
                         regfinal();


                     }
                 }



                 else {

                 }
             }
         });

     }

     public void uploadUsername(){
         dbrefusername=reference.child("usernames");
         String UID_=UID.getText().toString();


         HashMap<String,Object> map2=new HashMap<>();
         map2.put("User",currentUser);
         map2.put("userID",UID_);

         dbrefusername.child(currentUser).setValue(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if (task.isSuccessful()) {
                     Toast.makeText(RegisterActivity2.this, "DONE !", Toast.LENGTH_SHORT).show();
//                     openMain();
//                            Intent intent=new Intent(ProfileActivity.this,Homepage.class);
//                            startActivity(intent);
                 }

                 else {
                     Toast.makeText(RegisterActivity2.this, "Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(RegisterActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

             }
         });
     }

     public void uploadDetails(){

         ProgressDialog progressDialog = new ProgressDialog(this);
         progressDialog.setTitle("Registering User");
         progressDialog.setMessage("Please wait...");
         progressDialog.setCancelable(false);
         progressDialog.show();

         String Pass_=Pass.getText().toString();
         String UID_=UID.getText().toString();
         String CUMAIL_=CUMAIL.getText().toString();

         String Name_=getIntent().getStringExtra("keyname");
         String Personal_=getIntent().getStringExtra("keypersonal");
         String Phone_=getIntent().getStringExtra("keyphone");
         Boolean ExistingStudent= getIntent().getExtras().getBoolean("Existing");

         dbref=reference.child("userdata");

         String key =dbref.push().getKey();

         HashMap<String,Object> map=new HashMap<>();
         map.put("key",key);
         map.put("stname",Name_);
         map.put("stphone",Phone_);
         map.put("stemail",Personal_);
         map.put("stpassword",Pass_);
         map.put("USERNAME",UID_);
         map.put("CUMAIL",CUMAIL_);
         map.put("Guest",ExistingStudent);


         dbref.child(UID_).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 progressDialog.dismiss();
                 if (task.isSuccessful()) {
                     Toast.makeText(RegisterActivity2.this, "DONE !", Toast.LENGTH_SHORT).show();
                     uploadUsername();
//                     openMain();
//                            Intent intent=new Intent(ProfileActivity.this,Homepage.class);
//                            startActivity(intent);

                 }

                 else {
                     Toast.makeText(RegisterActivity2.this, "Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 progressDialog.dismiss();
                 Toast.makeText(RegisterActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

             }
         });








     }


        public class StringMatcher {
        public boolean validate(String str1, String str2) {
            return str2.startsWith(str1.substring(0, 9));
        }
    }



}