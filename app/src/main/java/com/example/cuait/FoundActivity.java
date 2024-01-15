package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FoundActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    ImageView profilepic;
    TextView welcome;
    Button lost;
    Button myitem;

    ImageButton menu;

    String userID,currentUser;

    private FirebaseAuth uauth;
    FirebaseUser user;
    DatabaseReference dbrefreaduserid,reference,dbprofpic,dbbug;
    public static final String Shared_Pref = "SharedPref";

    foundadapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lost=findViewById(R.id.foundbtnsubmit);

        menu=findViewById(R.id.foundmenubtn2);


        welcome=findViewById(R.id.foundtextViewwelome2);
        profilepic=findViewById(R.id.foundimgprofilepic2);

        myitem=findViewById(R.id.foundbuttonitem);


        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();


        getuserid();

        FirebaseRecyclerOptions<foundmodel> options=new FirebaseRecyclerOptions.Builder<foundmodel>().setQuery(FirebaseDatabase.getInstance().getReference().child("Itemlost"),foundmodel.class).build();

        adapter=new foundadapter(options);
        recyclerView.setAdapter(adapter);






        lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoundActivity.this,LostActivity.class);
                startActivity(intent);
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmenu(v);
            }
        });

        myitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseRecyclerOptions<foundmodel> options = new FirebaseRecyclerOptions.Builder<foundmodel>()
//                        .setQuery(FirebaseDatabase.getInstance().getReference().child("userdata").child(userID).child("myitem"), foundmodel.class)
//                        .build();
//
//                try {
//                    adapter.updateOptions(options);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Intent intent=new Intent(FoundActivity.this,MyItem.class);
                intent.putExtra("ui",userID);
                intent.putExtra("cu",currentUser);
                startActivity(intent);
            }
        });




    }

    private void showmenu(View v) {
        PopupMenu popupMenu = new PopupMenu(FoundActivity.this, v);
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
                    Intent intent=new Intent(FoundActivity.this,ForgotActivity.class);
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(FoundActivity.this);
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
                            Toast.makeText(FoundActivity.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(FoundActivity.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(FoundActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(FoundActivity.this);
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

                Intent intent = new Intent(FoundActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void reportBug(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(FoundActivity.this);
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
                                        Toast.makeText(FoundActivity.this, "Bug Reported Succesfully !", Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }

                                    else {
                                        Toast.makeText(FoundActivity.this, "Failed to Report Bug !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(FoundActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    alertDialogimage.dismiss();

                                }
                            });
                }

            }
        });

    }

    public void getuserid() {
        uauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        String currentUser = uauth.getCurrentUser().getUid();
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
                        Toast.makeText(FoundActivity.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(FoundActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
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
                        Picasso.get().load(Profpic).into(profilepic);
                    }
                    else {
                        profilepic.setImageResource(R.drawable.jester);
                    }
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