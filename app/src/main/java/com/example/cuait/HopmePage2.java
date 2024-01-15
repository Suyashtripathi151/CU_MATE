package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class HopmePage2 extends AppCompatActivity {


    private TextView attention, txtissue, txtuser, txtpow, txtcredits, welcome,txtcredits2,txtcapt,txttaptovote;
    private Button btmraise, btmuploadpow, vote;

    ImageButton editprof;
    private ImageView pow, logo, profilepic;
    private ImageSlider mainslider, powslider;
    String name;

    private ImageButton menu;
    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    SmoothBottomBar btmbar;

    private DatabaseReference dbprofpic, dbrefreaduserid, dbslides, dbpow, dbCurrentWeek,dbbug,dbuserdata,dbprevweek;
    Object value;

    String username, query, getquery, getusername, currentUser, userID;
    long maxVotes = 0;
    String postWithMaxVotes = "";
    Boolean AlreadyVoted;

    public static final String Shared_Pref = "SharedPref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hopme_page2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        txtpow = findViewById(R.id.txtpow2);

        txtcredits = findViewById(R.id.txtwinner2);
        txtcredits2=findViewById(R.id.txtwinner);
        welcome = findViewById(R.id.textViewwelome2);
        txtcapt=findViewById(R.id.txtpowcaption);
        txttaptovote=findViewById(R.id.txtpow3);


        pow = findViewById(R.id.img_picture_of_week_i2);

        menu = findViewById(R.id.menubtn2);


        profilepic = findViewById(R.id.imgprofilepic2);

        vote = findViewById(R.id.btnvote2);
        btmbar = findViewById(R.id.bottomBarv);

        uauth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        Date startDate = calendar.getTime();
        Date currentDate = new Date();
        long diff = currentDate.getTime() - startDate.getTime();
        long weeks = diff / (7 * 24 * 60 * 60 * 1000);
        String weekNumberStr = "Week " + (weeks + 1);
        String weekNumberStrprev = "Week " + (weeks);

        dbCurrentWeek=reference.child("userpow").child(weekNumberStr);
        dbpow = dbCurrentWeek.child("photos");
        dbprevweek=reference.child("userpow").child(weekNumberStrprev).child("photos");



        mainslider = findViewById(R.id.image_slider);
        powslider = findViewById(R.id.image_slider2);


        getuserid();

        final List<SlideModel> remoteimages = new ArrayList<>();
        dbslides = reference.child("Slider");
        dbslides.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren())
                    remoteimages.add(new SlideModel(data.child("Url")
                            .getValue().toString(), data.child("Title").getValue().toString(), ScaleTypes.FIT));
                mainslider.setImageList(remoteimages, ScaleTypes.FIT);
                mainslider.setSlideAnimation(AnimationTypes.CUBE_OUT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        powslide();
        getPow();


        btmbar.setSelected(true);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HopmePage2.this,ProfileActivity.class);
                startActivity(intent);
            }
        });

        btmbar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                switch (i) {
                    case 0:
                        Intent intent2 = new Intent(HopmePage2.this, HopmePage2.class);
                        startActivity(intent2);
                        break;

                    case 1:
                        Intent intent3 = new Intent(HopmePage2.this, FoundActivity.class);
                        startActivity(intent3);
                        break;

                    case 2:
                        dbuserdata=reference.child("userdata").child(userID);
                        dbuserdata.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.getResult().exists()) {
                                    DataSnapshot dataSnapshot=task.getResult();
                                    if(String.valueOf(dataSnapshot.child("Guest").getValue()).matches("true")){
                                        Intent intent = new Intent(HopmePage2.this, Notification.class);
                                        startActivity(intent);

                                    }

                                    else {

                                        Toast.makeText(HopmePage2.this, "You are not authorized to use this feature, Kindly log in through CUMAIL", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        break;


                    default:
                        return false;
                }

                return false;
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showmenu(v);
            }


        });

        vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HopmePage2.this, PictureOfWeek.class);
                startActivity(intent);
            }

        });

    }

    private void powslide() {

        final List<SlideModel> remoteimages2 = new ArrayList<>();
        dbpow.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    remoteimages2.add(new SlideModel(
                            data.child("imageUrl").getValue().toString(),
                            data.child("userID").getValue().toString(),
                            ScaleTypes.FIT));

                }
                powslider.setImageList(remoteimages2, ScaleTypes.FIT);
                powslider.setSlideAnimation(AnimationTypes.CUBE_OUT);

                powslider.setItemClickListener(new ItemClickListener() {

                    @Override
                    public void onItemSelected(int i) {
                        powslider.stopSliding();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(HopmePage2.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.disable, null);
                        builder.setCancelable(true);
                        builder.setView(dialogView);

                        Button ok=dialogView.findViewById(R.id.ok);
                        Button dismiss=dialogView.findViewById(R.id.Dismiss);
                        TextView message=dialogView.findViewById(R.id.txtWarning);

                        message.setText("Are you sure you want to cast your vote for this picture for the Best Picture? Once you click \"OK,\" your vote will be submitted and cannot be changed. ");

                        AlertDialog alertDialogimage = builder.create();
                        alertDialogimage.show();

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialogimage.dismiss();
                                powslider.startSliding(1700);
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference votersRef = dbCurrentWeek.child("voters");
                                votersRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                DataSnapshot dataSnapshot = task.getResult();
                                                if (dataSnapshot.hasChild(userID)) {
                                                    AlreadyVoted = true;
                                                    Toast.makeText(HopmePage2.this, "You have voted akready", Toast.LENGTH_SHORT).show();

                                                }

                                                else {
                                                    AlreadyVoted = false;
                                                    if (i >= 0 && i < remoteimages2.size()) {
                                                        SlideModel currentSlide = remoteimages2.get(i);
                                                        String POSTKEY = currentSlide.getTitle();


                                                        assert POSTKEY != null;
                                                        dbpow.child(POSTKEY).child("votes").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {


                                                                    Toast.makeText(HopmePage2.this, "Voted Successfully", Toast.LENGTH_SHORT).show();

                                                                } else {
                                                                    Toast.makeText(HopmePage2.this, "Failed to vote !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(HopmePage2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                        dbCurrentWeek.child("voters").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                            }
                                                        });



                                                    }
                                                }
                                            } else {
                                                AlreadyVoted = false;
                                                if (i >= 0 && i < remoteimages2.size()) {
                                                    SlideModel currentSlide = remoteimages2.get(i);
                                                    String POSTKEY = currentSlide.getTitle();


                                                    assert POSTKEY != null;
                                                    dbpow.child(POSTKEY).child("votes").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {


                                                                Toast.makeText(HopmePage2.this, "DONE !", Toast.LENGTH_SHORT).show();

                                                            } else {
                                                                Toast.makeText(HopmePage2.this, "Failed !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(HopmePage2.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                    dbCurrentWeek.child("voters").child(userID).setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                        }
                                                    });



                                                }
                                            }
                                        }

                                        else{
                                            Toast.makeText(HopmePage2.this, "You Have Already Voted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                alertDialogimage.dismiss();
                                powslider.startSliding(1700);

                            }
                        });
                    }

                    @Override
                    public void doubleClick(int i) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayPow() {
        dbprevweek.child(postWithMaxVotes).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();

                        String Caption = String.valueOf(dataSnapshot.child("Caption").getValue());
                        String WinnerId = String.valueOf(dataSnapshot.child("userID").getValue());
                        String PicUrl = String.valueOf(dataSnapshot.child("imageUrl").getValue());


                        dbrefreaduserid = reference.child("userdata");

                        dbrefreaduserid.child(WinnerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        DataSnapshot dataSnapshot = task.getResult();
                                        name=String.valueOf(dataSnapshot.child("stname").getValue());
                                        txtcredits.setText("Winner :        " + name+ "\n@"+WinnerId);

                                    }
                                }
                            }
                        });



                        Picasso.get().load(PicUrl)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(pow);




                        txtcredits2.setText("Total Votes :        " + maxVotes);
                        txtcapt.setText(Caption);
                    } else {
                        Toast.makeText(HopmePage2.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HopmePage2.this, "NOT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getPow(){
        dbprevweek.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Iterate through the dataSnapshot to find the student with maximum marks
                for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                    String studentName = photoSnapshot.getKey();
                    long marks = photoSnapshot.child("votes").getChildrenCount();

                    if (marks > maxVotes) {
                        maxVotes = marks;
                        postWithMaxVotes = studentName;
                    }
                }

                // Here, you have the student with the maximum marks
                if (!postWithMaxVotes.isEmpty()) {
                    // Do something with the studentWithMaxMarks
                    displayPow();

                }

                else{
                    txtcredits.setVisibility(View.GONE);
                    txtcredits.setEnabled(false);

                    txtcredits2.setVisibility(View.GONE);
                    txtcredits2.setEnabled(false);

                    pow.setVisibility(View.GONE);
                    pow.setEnabled(false);


                    txtpow.setVisibility(View.GONE);
                    txtpow.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });


    }

    public void showmenu(View v) {
        PopupMenu popupMenu = new PopupMenu(HopmePage2.this, v);
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
                   Intent intent=new Intent(HopmePage2.this,ForgotActivity.class);
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

    public void deleteAccount() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HopmePage2.this);
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
                alertDialogimage.dismiss();
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HopmePage2.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(HopmePage2.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(HopmePage2.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    public void disableAccount() {
        Intent intent=new Intent(HopmePage2.this,RaiseIssue.class);
        startActivity(intent);
    }

    public void logout() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(HopmePage2.this);
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
                alertDialogimage.dismiss();

                Intent intent = new Intent(HopmePage2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void reportBug(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(HopmePage2.this);
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
                                        Toast.makeText(HopmePage2.this, "Bug Reported Succesfully !", Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }

                                    else {
                                        Toast.makeText(HopmePage2.this, "Failed to Report Bug !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HopmePage2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    alertDialogimage.dismiss();

                                }
                            });
                }

            }
        });

    }

    private void getuserid() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Your Data");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(HopmePage2.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(HopmePage2.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getpic() {
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

    public void gotourl(String link) {
        Uri uri = Uri.parse(link);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));

    }



}