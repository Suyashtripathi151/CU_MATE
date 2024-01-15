package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Homepage extends AppCompatActivity {

//    private MyFragmentAdapter adapter;
    private TextView attention,txtissue,txtuser,txtpow,txtcredits,txtcaption,welcome;
    private Button btmraise,btmuploadpow;
    private ImageView pow,selectedpow;

    private ImageButton menu;
    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref2,dbref3,dbrefreaduserid;
    Object value;

    String username,query,getquery,getusername,currentUser,userID;
    Integer upvotes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

       attention=findViewById(R.id.txtattention2);
       txtcaption=findViewById(R.id.inptcaption);
       txtissue=findViewById(R.id.inptcaption);
       txtpow=findViewById(R.id.txtpow2);
       txtuser=findViewById(R.id.txtuser2);
       txtcredits=findViewById(R.id.txtwinner2);
       welcome=findViewById(R.id.textwelcome);

       btmraise=findViewById(R.id.btnupvote);
       btmuploadpow=findViewById(R.id.btnuploadpow2);

       pow=findViewById(R.id.img_picture_of_week_i2);

       menu=findViewById(R.id.menubtn2);

        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        dbrefreaduserid=reference.child("usernames");
        dbrefreaduserid.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
                        userID=String.valueOf(dataSnapshot.child("userID").getValue());
                        welcome.setText("Welcome\n"+userID);

                    }

                    else {
                        Toast.makeText(Homepage.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(Homepage.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Homepage.this,ProfileActivity.class);
                startActivity(intent);
            }
        });



       btmraise.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               raiseyourquery();

           }
       });

        btmuploadpow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Homepage.this,PictureOfWeek.class);
                startActivity(intent);
            }

        });


    }
    private void logout(){

    }
    public void raiseyourquery(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(Homepage.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.raisequery,null);
//        builder.setCancelable(false);
        builder.setCancelable(true);
        builder.setView(dialogView);

        TextView title=dialogView.findViewById(R.id.txtattention2);
        TextView user=dialogView.findViewById(R.id.txtuser2);
        EditText inpqury=dialogView.findViewById(R.id.inptcaption);

        Switch anonymous=dialogView.findViewById(R.id.switch1);
        user.setText(userID);


        anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anonymous.isChecked()) {
                    username="- Anonymous";
                    user.setText(username);
                }
                else{
                    user.setText(userID);

                }
            }
        });


        Button submit=dialogView.findViewById(R.id.btnupvote);
        AlertDialog alertDialogimage = builder.create();

        alertDialogimage.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query=inpqury.getText().toString();
                if (anonymous.isChecked()) {
                    username="- Anonymous";
                    uploadquery(username);
                    Toast.makeText(Homepage.this, "Submitted as Anonymous", Toast.LENGTH_SHORT).show();
                }

                else{
                    username="- User";
                   uploadquery(username);
                   Toast.makeText(Homepage.this, "Submitted as User", Toast.LENGTH_SHORT).show();
                }

                alertDialogimage.cancel();

            }
        });




    }
    private void uploadquery(String s){
        dbref2=reference.child("userqueries");
        String key =dbref2.push().getKey();
        upvotes=0;


        HashMap<String,Object> map=new HashMap<>();
        map.put("key",key);
        map.put("username",currentUser);
        map.put("query",query);
        map.put("usertype",s);
        map.put("upvotes",upvotes);
        map.put("userID",userID);

        dbref2.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Homepage.this, "DONE !", Toast.LENGTH_SHORT).show();
//                     openMain();
                    Intent intent=new Intent(Homepage.this,Homepage.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(Homepage.this, "Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Homepage.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




    }

}