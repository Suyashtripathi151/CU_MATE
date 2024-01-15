package com.example.cuait;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);

        String currentDateString= DateFormat.getDateInstance().format(c.getTime());

        TextView textViewdate=(TextView) findViewById(R.id.textViewdate);
        textViewdate.setText(currentDateString);
    }

    private EditText name,phone,email;
    private TextView txtname,txtemail,txtphone,cutitle,edittitle,course,semester,department,dob,doboutput,txtusernme;
    private Button update,upload;

    private ImageButton date;
    private ImageView profile,background;

    private Spinner spinnerc,spinnerd,spinners;
    Object value;

    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref2,dbref3,dbrefreaduserid;
    private StorageReference storageRef;
    String currentUser,userID,FCUMAIL,FPASS,FGUEST;

    Boolean isProfile;

    Boolean uploadOnlyData;

    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";

    Drawable a2,b2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        uauth=FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();



        name = findViewById(R.id.inp_name);
        phone = findViewById(R.id.inp_phone);
        email = findViewById(R.id.inp_email);

        txtname = findViewById(R.id.textviewname);
        txtphone = findViewById(R.id.textviewphone);
        txtemail = findViewById(R.id.textViewemail);

        edittitle = findViewById(R.id.edittext);
        course = findViewById(R.id.textViewCourse);
//        semester = findViewById(R.id.textViewSemester);
        department = findViewById(R.id.textViewDepart);
        txtusernme=findViewById(R.id.txtusername);

        dob = findViewById(R.id.textViewDOB2);
        doboutput=findViewById(R.id.textViewdate);


        profile = findViewById(R.id.profilepic);
        background = findViewById(R.id.imgprofilepic2);

        update = findViewById(R.id.updatebutton);
        upload = findViewById(R.id.uploadimagebtm);
        date=findViewById(R.id.buttondate);

        spinnerc = findViewById(R.id.spinnercourse);
        spinnerd = findViewById(R.id.spinnerdepart);
//        spinners = findViewById(R.id.spinnersemester);



        dbrefreaduserid=reference.child("usernames");
        dbrefreaduserid.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
                        userID=String.valueOf(dataSnapshot.child("userID").getValue());
                        dbref3=reference.child("userdata");
                        dbref3.child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().exists()){
                                        DataSnapshot dataSnapshot= task.getResult();
                                        String Fname=String.valueOf(dataSnapshot.child("stname").getValue());
                                        String Fphone=String.valueOf(dataSnapshot.child("stphone").getValue());
                                        String Femail=String.valueOf(dataSnapshot.child("stemail").getValue());


                                        String Fcourse=String.valueOf(dataSnapshot.child("stcourse").getValue());
                                        String Fdept=String.valueOf(dataSnapshot.child("stdept").getValue());
                                        String Fsem=String.valueOf(dataSnapshot.child("ssemester").getValue());
                                        String Fdob=String.valueOf(dataSnapshot.child("stdob").getValue());

                                        FCUMAIL=String.valueOf(dataSnapshot.child("CUMAIL").getValue());
                                        FGUEST=String.valueOf(dataSnapshot.child("Guest").getValue());
                                        FPASS=String.valueOf(dataSnapshot.child("stpassword").getValue());

                                        if((dataSnapshot.child("imageUrl").exists())){
                                            String Profpic=String.valueOf(dataSnapshot.child("imageUrl").getValue());
                                            Picasso.get().load(Profpic).into(profile);
                                            isProfile=true;
                                        }
                                        else {
                                            isProfile=false;
                                            profile.setImageResource(R.drawable.jester);
                                            a2=profile.getDrawable();
                                        }



                                        txtusernme.setText(userID);

                                        name.setText(Fname);
                                        phone.setText(Fphone);
                                        email.setText(Femail);
                                        doboutput.setText(Fdob);



                                    }

                                    else {
                                        Toast.makeText(ProfileActivity.this, "NOT FOUND", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                else {

                                }
                            }
                        });


                    }

                    else {
                        Toast.makeText(ProfileActivity.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(ProfileActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });



        final String[] coursearray = {"", "bsc", "cse","ait"};
        final String[] deptarray = {"", "bda", "ml", "is"};
        final String[] semarray = {"", "1", "2", "3", "4", "5", "6", "7", "8"};


        ArrayAdapter<String> adaptdept = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_style, deptarray);
        adaptdept.setDropDownViewResource(R.layout.spinner_custom_style);


        ArrayAdapter<String> adaptcourse = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_custom_style, coursearray);
        adaptcourse.setDropDownViewResource(R.layout.spinner_custom_style);

        spinnerc.setAdapter(adaptcourse);
//        spinners.setAdapter(adaptsem);


        spinnerc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position ==2) {
//                    spinnerd.setAdapter(adaptdept);
//                } else {
//                    spinnerd.setAdapter(adaptsem);
//                }

                switch (position){
                    case 0:
                        ////
                        break;
                    case 1:
//                        spinnerd.setAdapter(adaptsem);
                        break;
                    case 3:
                        spinnerd.setAdapter(adaptdept);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value= parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker=new DatePickerFragement();
                datePicker.show(getSupportFragmentManager(),"PICK YOUR DATE OF BIRTH");
            }
        });

        update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                b2=profile.getDrawable();
              if(!isProfile){
                  if(a2==b2){
//                      Toast.makeText(ProfileActivity.this, "You have not selected any picture", Toast.LENGTH_SHORT).show();
                      uploadOnlyData=true;
                      uploadProfData();
                  }
                  else{
                      uploadOnlyData=false;
                      uploadquery();
                  }
              }
              else {
                  uploadquery();
              }




            }});


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
            }
        });

    }

    private void uploadquery() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbref2 = reference.child("userdata");
        String key = dbref2.push().getKey();

        String stname = name.getText().toString();
        String stphone = phone.getText().toString();
        String stemal = email.getText().toString();

        String stcourse = spinnerc.getSelectedItem().toString();
        String stdepart = spinnerd.getSelectedItem().toString();
//        String stsem = spinners.getSelectedItem().toString();

        String DB=doboutput.getText().toString();

        // Convert the ImageView to a byte array
        byte[] imageData = imagetobyte(profile);

        // Create a reference to the location where you want to store the image in Firebase Storage
        StorageReference imageRef = storageRef.child("Profile Pictures/" +userID+ key);

        // Upload the image to Firebase Storage
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("stname",stname);
                        map.put("stphone",stphone);
                        map.put("stemail",stemal);
                        map.put("stcourse",stcourse);
                        map.put("stdepart",stdepart);
                        map.put("imageUrl",imageUrl);
                        map.put("stdob",DB);
                        map.put("CUMAIL",FCUMAIL);
                        map.put("Guest",FGUEST);
                        map.put("stpassword",FPASS);

                        // Save the image data to the Firebase Realtime Database
                        dbref2.child(userID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(ProfileActivity.this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(ProfileActivity.this,HopmePage2.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Failed to upload Data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
//                Toast.makeText(PictureOfWeek.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                Toast.makeText(ProfileActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadProfData(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        dbref2 = reference.child("userdata");
        String key = dbref2.push().getKey();

        String stname = name.getText().toString();
        String stphone = phone.getText().toString();
        String stemal = email.getText().toString();

        String stcourse = spinnerc.getSelectedItem().toString();
        String stdepart = spinnerd.getSelectedItem().toString();
//        String stsem = spinners.getSelectedItem().toString();

        String DB=doboutput.getText().toString();


        HashMap<String, Object> map = new HashMap<>();
        map.put("stname",stname);
        map.put("stphone",stphone);
        map.put("stemail",stemal);
        map.put("stcourse",stcourse);
        map.put("stdepart",stdepart);
//                        map.put("stsemester",stsem);
        map.put("stdob",DB);
        map.put("CUMAIL",FCUMAIL);
        map.put("Guest",FGUEST);
        map.put("stpassword",FPASS);

        // Save the image data to the Firebase Realtime Database
        dbref2.child(userID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Data uploaded successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(ProfileActivity.this,HopmePage2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to upload Data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void choosepic(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.alertdialog,null);
        builder.setCancelable(true);
        builder.setView(dialogView);

        ImageView gallery=dialogView.findViewById(R.id.gallery);
        ImageView camera=dialogView.findViewById(R.id.camera);
        TextView txtgallery=dialogView.findViewById(R.id.textTitle);
        TextView txtcamera=dialogView.findViewById(R.id.textcamera);

        AlertDialog alertDialogimage = builder.create();
        alertDialogimage.show();

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takegallery();
                alertDialogimage.cancel();

            }
        });



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPer()){
                    takecamera();
                    alertDialogimage.cancel();
                }

            }
        });



    }


    private void takegallery(){
        Intent pickphoto=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickphoto,1);

    }

    private void takecamera(){
        Intent pickpicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pickpicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(pickpicture,2);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch ((requestCode)){
            case 1:
                if(resultCode==RESULT_OK){
                    Uri selectedImageUri=data.getData();
                    profile.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmapImage=(Bitmap) bundle.get("data");
                    profile.setImageBitmap(bitmapImage);
                }
        }
    }
    private boolean checkAndRequestPer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }



    private void onRequestPermissionResult(int resultcode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(resultcode,permissions,grantResults);
        if(resultcode==20 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            takecamera();
        }
        else {
            Toast.makeText(this, "OKAY", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] imagetobyte(ImageView imageView){
        Bitmap bitmap;
        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    }

