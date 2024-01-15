package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import java.util.Calendar;
import java.util.Date;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;




import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class PictureOfWeek extends AppCompatActivity {
    private TextView details;
    private Button upload,select;
    private ImageView logo,pow;
    private EditText inpcaptionn;

    Uri imageUri;
    String userID;


    private DatabaseReference reference;
    private DatabaseReference dbref2,dbrefreaduserid,dbrefwwek;
    private StorageReference storageRef;


    Object value;
    private FirebaseAuth uauth;
    FirebaseUser user;
    String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_of_week);

        details=findViewById(R.id.txtpowdetails);
        upload=findViewById(R.id.btnupload);
        select=findViewById(R.id.btnselectimage);
        logo=findViewById(R.id.logo);
        pow=findViewById(R.id.imgselectedpow);
        inpcaptionn=findViewById(R.id.inptcaption);


        storageRef = FirebaseStorage.getInstance().getReference();


        uauth= FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = uauth.getCurrentUser();
        username=currentUser.getUid();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        Date startDate = calendar.getTime();
        Date currentDate = new Date();
        long diff = currentDate.getTime() - startDate.getTime();
        long weeks = diff / (7 * 24 * 60 * 60 * 1000);
        String weekNumberStr = "Week " + (weeks + 1);


        dbref2 = reference.child("userpow");

        dbrefreaduserid=reference.child("usernames");
        dbrefwwek=dbref2.child(weekNumberStr);

        getUserName();

        Drawable a=pow.getDrawable();



        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Drawable b=pow.getDrawable();
               if(b==a){
                   Toast.makeText(PictureOfWeek.this, "You have not selected any picture", Toast.LENGTH_SHORT).show();
               }
               else{
                   verifypow();
               }


            }
        });


    }

    public void getUserName() {
        dbrefreaduserid.child(username).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
                        userID=String.valueOf(dataSnapshot.child("userID").getValue());


                    }

                    else {
                        Toast.makeText(PictureOfWeek.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    Toast.makeText(PictureOfWeek.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void choosepic(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(PictureOfWeek.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.selectpow,null);
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
                    imageUri=data.getData();
                    pow.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmapImage=(Bitmap) bundle.get("data");
                    pow.setImageBitmap(bitmapImage);
                }
        }
    }
    private boolean checkAndRequestPer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(PictureOfWeek.this, android.Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(PictureOfWeek.this, new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    private void onRequestPermissionResult(int resultcode, @NonNull String[] permissions, @NonNull int[] grantResults){
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

    public void uploadpow(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String key = dbrefwwek.child("photos").push().getKey();
        String caption = inpcaptionn.getText().toString();

        byte[] imageData = imagetobyte(pow);

        StorageReference imageRef = storageRef.child("POW/" + key);


        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("key", key);
                        data.put("Caption",caption);
                        data.put("User",username);
                        data.put("userID",userID);
                        data.put("imageUrl", imageUrl);



                        // Save the image data to the Firebase Realtime Database
                        assert userID != null;
                        dbrefwwek.child("photos").child(userID).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(PictureOfWeek.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(PictureOfWeek.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PictureOfWeek.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                Toast.makeText(PictureOfWeek.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checksinglenomination(){
        dbrefwwek.child("photos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.hasChild(userID)) {
                            Toast.makeText(PictureOfWeek.this, "You cannot nominate more than 1 photo", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            uploadpow();

                        }
                    }
                    else {
                        uploadpow();
                    }


                }

            }
        });

    }


    private void verifypow() {

        dbrefwwek.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.getChildrenCount()>=10) {
                            Toast.makeText(PictureOfWeek.this, "Sorry We have reached to the limit of photos for Nomination", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            checksinglenomination();
                        }
                    }
                    else {
                        checksinglenomination();
                    }
                }
            }
        });



    }



}