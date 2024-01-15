package com.example.cuait;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class LostActivity extends AppCompatActivity  implements DatePickerDialog.OnDateSetListener {
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c=Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.YEAR,year);

        String currentDateString= DateFormat.getDateInstance().format(c.getTime());

        TextView textViewdate=(TextView) findViewById(R.id.losttextViewdate);
        textViewdate.setText(currentDateString);
    }


    private EditText inpname,inpphne,inpitem,inpplace,inpdescription;

    String Strphone,Strname,Stritem,Strplace,Strdesc,Strdate;
    private TextView name,phone,item,place,description,welcome,txtdate;


    private Button submit,upload;

    private Switch owner;
    private ImageButton datebtn,menu;

    private ImageView profilepic,missingimg,logo;

    private FirebaseAuth uauth;
    FirebaseUser user;
    private DatabaseReference reference;
    private DatabaseReference dbref2,dbref3,dbrefreaduserid,dbprofpic,dbbug,dbmyitem;
    private StorageReference storageRef;
    String currentUser,userID;
    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";

    public static final String Shared_Pref = "SharedPref";
    Drawable a;

    Boolean isOwner;
    String lookingFor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost);

        uauth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        currentUser = uauth.getCurrentUser().getUid();

        storageRef = FirebaseStorage.getInstance().getReference();

        inpname=findViewById(R.id.lostinp_owner);
        inpdescription=findViewById(R.id.lostinp_descrip);
        inpplace=findViewById(R.id.lostinp_place);
        inpitem=findViewById(R.id.lostinp_itemname);
        inpphne=findViewById(R.id.lostinp_phone);

        name=findViewById(R.id.losttextViewowner);
        phone=findViewById(R.id.losttextviewphone);
        item=findViewById(R.id.losttextviewitem);
        description=findViewById(R.id.losttextViewDescrip);
        place=findViewById(R.id.losttextViewplace);
        welcome=findViewById(R.id.losttextViewwelome2);
        txtdate=findViewById(R.id.losttextViewdate);

        submit=findViewById(R.id.lostbtnsubmit);
        upload=findViewById(R.id.lostbtnuploadmissing);
        datebtn=findViewById(R.id.lostbuttondate);

        owner=findViewById(R.id.lostswitch);

        menu=findViewById(R.id.lostmenubtn2);

        profilepic=findViewById(R.id.lostimgprofilepic2);
        missingimg=findViewById(R.id.lostimgmissing);
        logo=findViewById(R.id.lostlogo);



        getuserid();
        a=missingimg.getDrawable();



        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker=new DatePickerFragement();
                datePicker.show(getSupportFragmentManager(),"PICK YOUR DATE OF BIRTH");
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (owner.isChecked()) {
                    owner.setText("I am Looking for owner of missing item");

                }
                else{
                    owner.setText("I am Looking For missing item");
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Drawable b=missingimg.getDrawable();
                if(a==b){
                    uploadonlydetails();
                }
                else {
                    uploaddetails();
                }


            }});

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosepic();
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
        HopmePage2 hop=new HopmePage2();
        PopupMenu popupMenu = new PopupMenu(LostActivity.this, v);
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
                    Intent intent=new Intent(LostActivity.this,ForgotActivity.class);
                    startActivity(intent);

                }

                if (item.getItemId() == R.id.reportbug) {
                    reportBug();

                }

                if (item.getItemId() == R.id.developer) {
                    gotourl("https://www.linkedin.com/in/suyash-tripathi-3b2345239/");

                }

                if (item.getItemId() == R.id.logout) {
//                    logout();
                    hop.logout();

                }


                return false;
            }
        });
        popupMenu.show();
    }

    private void deleteAccount() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(LostActivity.this);
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
                            Toast.makeText(LostActivity.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LostActivity.this,MainActivity.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(LostActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(LostActivity.this);
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

                Intent intent = new Intent(LostActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void reportBug(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(LostActivity.this);
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
                                        Toast.makeText(LostActivity.this, "Bug Reported Succesfully !", Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }

                                    else {
                                        Toast.makeText(LostActivity.this, "Failed to Report Bug !"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        alertDialogimage.dismiss();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    alertDialogimage.dismiss();

                                }
                            });
                }

            }
        });

    }


    private void uploaddetails() {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbref2 = reference.child("Itemlost");
        String key = dbref2.push().getKey();



        // Convert the ImageView to a byte array
        byte[] imageData = imagetobyte(missingimg);

        // Create a reference to the location where you want to store the image in Firebase Storage
        StorageReference imageRef = storageRef.child("imageslost/" + key);

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

                        // Create a HashMap to store the image data
                        HashMap<String, Object> map = new HashMap<>();

                        Strphone=inpphne.getText().toString();
                        Strdesc=inpdescription.getText().toString();
                        Strplace=inpplace.getText().toString();
                        Stritem=inpitem.getText().toString();
                        Strname=inpname.getText().toString();


                        if (owner.isChecked()) {
                           isOwner=false;
                           lookingFor="owner";

                        }
                        else{
                           isOwner=true;
                            lookingFor="item";

                        }
                        int time=(int) (System.currentTimeMillis());
                        Timestamp tsTemp=new Timestamp(time);
                        String ts=tsTemp.toString();

//
                        map.put("phone",Strphone);
                        map.put("owner",Strname);
                        map.put("place", Strplace);
                        map.put("item", Stritem);
                        map.put("description", Strdesc);
                        map.put("Status","Not Found");
                        map.put("User",currentUser);
                        map.put("UserID",userID);
                        Strdate=txtdate.getText().toString();
                        map.put("date",Strdate);
                        map.put("lookingForOwner",lookingFor);
                        map.put("ImageURL",imageUrl);

                        // Save the image data to the Firebase Realtime Database
                        dbref2.child(ts).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    dbmyitem=reference.child("userdata").child(userID).child("myitem");
                                    dbmyitem.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {


                                                Toast.makeText(LostActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Toast.makeText(LostActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LostActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(LostActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadonlydetails(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Details");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        dbref2 = reference.child("Itemlost");
        dbmyitem=reference.child("userdata").child(userID).child("myitem");
        String key = dbref2.push().getKey();

        HashMap<String, Object> map = new HashMap<>();

        Strphone=inpphne.getText().toString();
        Strdesc=inpdescription.getText().toString();
        Strplace=inpplace.getText().toString();
        Stritem=inpitem.getText().toString();
        Strname=inpname.getText().toString();

        if (owner.isChecked()) {
            isOwner=false;
            lookingFor="owner";

        }
        else{
            isOwner=true;
            lookingFor="item";

        }


        map.put("phone",Strphone);
        map.put("owner",Strname);
        map.put("place", Strplace);
        map.put("item", Stritem);
        map.put("description", Strdesc);
        map.put("Status","Not Found");
        map.put("User",currentUser);
        map.put("UserID",userID);
        Strdate=txtdate.getText().toString();
        map.put("date",Strdate);
        map.put("lookingForOwner",lookingFor);


        // Save the image data to the Firebase Realtime Database
        dbref2.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    dbmyitem.child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {


                                Toast.makeText(LostActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    Toast.makeText(LostActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LostActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LostActivity.this, "USER ID NOT FOUND", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(LostActivity.this, "NOT SUCCESSFULL", Toast.LENGTH_SHORT).show();
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

    public void choosepic(){
        final AlertDialog.Builder builder=new AlertDialog.Builder(LostActivity.this);
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
                    missingimg.setImageURI(selectedImageUri);
                }
                break;
            case 2:
                if(resultCode==RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmapImage=(Bitmap) bundle.get("data");
                    missingimg.setImageBitmap(bitmapImage);
                }
        }
    }

    private boolean checkAndRequestPer() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(LostActivity.this, android.Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(LostActivity.this, new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    private void onRequestPermissionResult(int resultcode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(resultcode,permissions,grantResults);
        if(resultcode==20 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
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