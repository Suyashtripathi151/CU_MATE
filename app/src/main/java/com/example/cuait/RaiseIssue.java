package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RaiseIssue extends AppCompatActivity {
    RecyclerView recyclerView;
    fbadapter adapter;
    String username,query,currentUser,userID,key;
    Button raise,myquery;
    ImageButton menu;
    TextView welcome;
    ImageView profile;

    private DatabaseReference reference;
    private DatabaseReference dbref2,dbref3,dbrefreaduserid,dbprofpic,dbbug;

    private FirebaseAuth uauth;
    FirebaseUser user;
    Integer upvotes;

    public static final String Shared_Pref = "SharedPref";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);

        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbref2=reference.child("userqueries");

        raise=findViewById(R.id.btnraise);
        menu=findViewById(R.id.raisemenubtn2);
        profile=findViewById(R.id.imgprofilepicraise);
        welcome=findViewById(R.id.textViewwelomeraise);
        myquery=findViewById(R.id.btnmyquery);

        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        key =dbref2.push().getKey();



        getuserid();



        FirebaseRecyclerOptions<issuemodel> options=new FirebaseRecyclerOptions.Builder<issuemodel>().setQuery(FirebaseDatabase.getInstance().getReference().child("query"),issuemodel.class).build();

        adapter=new fbadapter(options);
        recyclerView.setAdapter(adapter);

        myquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(RaiseIssue.this,MyQuery.class);
              intent.putExtra("ui",userID);
              intent.putExtra("cu",currentUser);
              startActivity(intent);
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                raiseyourquery();
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmenu(v);
            }
        });


    }

    private void showmenu(View v) {
        PopupMenu popupMenu = new PopupMenu(RaiseIssue.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.disableaccount) {
                    disableAccount();
                }

                if (item.getItemId() == R.id.deleteaccount) {
                    deleteAccount();

                }

                if (item.getItemId() == R.id.resetpassword) {
                    Intent intent=new Intent(RaiseIssue.this,ForgotActivity.class);
                    startActivity(intent);

                }

                if (item.getItemId() == R.id.reportbug) {
                    reportBug();

                }

                if (item.getItemId() == R.id.developer) {
                    gotourl("https://www.linkedin.com/in/suyash-tripathi-3b2345239/");

                }

                if (item.getItemId() == R.id.logout) {
                    logout();

                }


                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteAccount() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(RaiseIssue.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.disable, null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        Button ok=dialogView.findViewById(R.id.ok);
        Button dismiss=dialogView.findViewById(R.id.Dismiss);
        TextView warning=dialogView.findViewById(R.id.txtWarning);

        warning.setText("Are you sure you want to delete your account? Once you click \"OK,\" your account and all associated data will be permanently deleted. This action cannot be undone. Please note that you will lose access to all your saved information, including profile details, preferences, and any associated content.");



        AlertDialog alertDialogimage = builder.create();
        alertDialogimage.show();

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogimage.dismiss();

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RaiseIssue.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(RaiseIssue.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(RaiseIssue.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void disableAccount() {
    }

    public void gotourl(String link) {
        Uri uri = Uri.parse(link);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));

    }

    private void logout() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(RaiseIssue.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.disable, null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        Button ok=dialogView.findViewById(R.id.ok);
        Button dismiss=dialogView.findViewById(R.id.Dismiss);
        TextView message=dialogView.findViewById(R.id.txtWarning);

        message.setText("Are you sure you want to log out? Once you click \"OK,\" you will be logged out of your account and will need to sign in again to access your account.");

        AlertDialog alertDialogimage = builder.create();
        alertDialogimage.show();

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogimage.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(Shared_Pref, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Pass", "");
                editor.apply();

                Intent intent = new Intent(RaiseIssue.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void reportBug(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(RaiseIssue.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.report_bug, null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        Button report=dialogView.findViewById(R.id.btnReportBug);
        EditText inpBug=dialogView.findViewById(R.id.InpBug);
        CheckBox confirmation=dialogView.findViewById(R.id.confirmation);


        AlertDialog alertDialogimage = builder.create();
        alertDialogimage.show();

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!confirmation.isChecked()){
                    confirmation.setError("Kindly Confirm to report");
                }
                else {
                    String Bug=inpBug.getText().toString();
                    dbbug=reference.child("Bugs");
                    String key =dbbug.push().getKey();

                    HashMap<String,Object> map=new HashMap<>();
                    map.put("key",key);
                    map.put("Bug",Bug);
                    map.put("User",currentUser);



                    dbbug.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RaiseIssue.this, "Bug Reported Succesfully !", Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }

                                    else {
                                        Toast.makeText(RaiseIssue.this, "Failed to Report Bug !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RaiseIssue.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    alertDialogimage.dismiss();

                                }
                            });
                }

            }
        });

    }

    public void getuserid() {

        dbrefreaduserid = reference.child("usernames");
        dbrefreaduserid.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        userID = String.valueOf(dataSnapshot.child("userID").getValue());
                        welcome.setText("Welcome\n" + userID);

                        getpic();

                    } else {
                        Toast.makeText(RaiseIssue.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(RaiseIssue.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getpic() {
        dbprofpic = reference.child("userdata").child(userID);
        dbprofpic.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    String Profpic = String.valueOf(dataSnapshot.child("imageUrl").getValue());
                    if((dataSnapshot.child("imageUrl").exists())){
                        Picasso.get().load(Profpic).into(profile);
                    }
                    else {
                        profile.setImageResource(R.drawable.jester);
                    }
                }
            }
        });
    }

    private void uploadquery(String s){

        upvotes=0;
        HashMap<String,Object> map=new HashMap<>();
        dbref2=reference.child("query");

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

                    myquery(s);



                    Toast.makeText(RaiseIssue.this, "DONE !", Toast.LENGTH_SHORT).show();
//                     openMain();
                    Intent intent=new Intent(RaiseIssue.this,HopmePage2.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(RaiseIssue.this, "Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RaiseIssue.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void myquery(String s){
        dbref3=reference.child("userdata").child(userID).child("myqueries");

        upvotes=0;


        HashMap<String,Object> map=new HashMap<>();
        map.put("key",key);
        map.put("username",currentUser);
        map.put("query",query);
        map.put("usertype",s);
        map.put("upvotes",upvotes);
        map.put("userID",userID);

        dbref3.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(RaiseIssue.this, "DONE !", Toast.LENGTH_SHORT).show();
//                     openMain();
                    Intent intent=new Intent(RaiseIssue.this,HopmePage2.class);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(RaiseIssue.this, "Failed !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RaiseIssue.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void raiseyourquery(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(RaiseIssue.this);
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
                    Toast.makeText(RaiseIssue.this, "Submitted as Anonymous", Toast.LENGTH_SHORT).show();
                    alertDialogimage.cancel();
                }

                else{
                    username="- User";
                    uploadquery(username);
                    Toast.makeText(RaiseIssue.this, "Submitted as User", Toast.LENGTH_SHORT).show();
                    alertDialogimage.cancel();
                }



            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }


}