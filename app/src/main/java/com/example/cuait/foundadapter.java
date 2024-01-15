package com.example.cuait;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class foundadapter extends FirebaseRecyclerAdapter<foundmodel, foundadapter.myviewholder> {
    public foundadapter(@NonNull FirebaseRecyclerOptions<foundmodel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull foundmodel model) {

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String currentUser=user.getUid();
        String Userkey=getRef(position).getKey();
        DatabaseReference reference,Userref,dbrefreaduserid;
        reference=FirebaseDatabase.getInstance().getReference();
        Userref=reference.child("Itemlost").child(Userkey).child("Found");
//        final String[] userID = new String[1];
//        String u=userID[0];
        dbrefreaduserid = reference.child("usernames");

        holder.msg.setVisibility(View.INVISIBLE);
        holder.msg.setEnabled(false);




        holder.status.setText(model.getStatus());
        holder.userid.setText(model.getUserid());

        if(Objects.equals(model.getLookingForOwner(), "owner")){
            holder.looking.setText("Looking For Owner");
            holder.found.setText("Claim");

        }
        else {
            holder.looking.setText("Looking For Missing Item");
        }

        holder.txtdate.setText("Missing From : "+model.getDate());

        holder.phone.setText("Phone Number : "+model.getPhone());
        holder.inpdescription.setText(model.getDescription());

        holder.item.setText("Missing Item : "+model.getItem());

        holder.name.setText("Name Of Owner : "+model.getOwner());

        holder.place.setText("Place : "+model.getPlace());

        String Profpic = model.getImageURL() ;




        if (Profpic == null || Profpic.isEmpty()) {
            // Set the default picture here
            holder.img.setImageResource(R.drawable.lost);
        } else {
            Picasso.get().load(Profpic).into(holder.img);
        }



        String key =Userref.push().getKey();

        if(model.getUser().matches(currentUser)){
            holder.found.setText("DELETE");
            final int[] i = {1};

            holder.found.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.disable,null);


                        builder.setCancelable(true);
                        builder.setView(dialogView);

                        Button ok=dialogView.findViewById(R.id.ok);
                        Button dismiss=dialogView.findViewById(R.id.Dismiss);
                        TextView message=dialogView.findViewById(R.id.txtWarning);

                    message.setText("Are you sure you want to delete your your report? Once you click \"OK,\" your report and all associated data will be permanently deleted. This action cannot be undone");

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
                                dbrefreaduserid.child(currentUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                DataSnapshot dataSnapshot = task.getResult();
                                                String userID = String.valueOf(dataSnapshot.child("userID").getValue());
                                                reference.child("userdata").child(userID).child("myitem").child(Userkey).removeValue();
                                                reference.child("Itemlost").child(Userkey).removeValue();
                                            }
                                        }
                                    }
                                });

                                alertDialogimage.dismiss();
                            }
                        });
                }
            });
            Userref.orderByKey().limitToFirst(i[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        for (DataSnapshot si:snapshot.getChildren()){
                            String k2=si.getKey();

                            Userref.child(k2).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    DataSnapshot dataSnapshot= task.getResult();
                                    if(dataSnapshot.exists()){
                                        holder.msg.setVisibility(View.VISIBLE);
                                        holder.msg.setEnabled(true);

                                        holder.msg.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String name=String.valueOf(dataSnapshot.child("name").getValue());
                                                String phone=String.valueOf(dataSnapshot.child("phone").getValue());
                                                String msg=String.valueOf(dataSnapshot.child("message").getValue());

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                                                View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.foundoutput,null);
                                                builder.setCancelable(true);
                                                builder.setView(dialogView);



                                                EditText Message=dialogView.findViewById(R.id.fdteditTextmsg);
                                                TextView Name=dialogView.findViewById(R.id.fdttextname);
                                                TextView Phone=dialogView.findViewById(R.id.fdttextphone);

                                                Name.setText("Name : "+name);
                                                Phone.setText("Phone : "+phone);
                                                Message.setText(msg);


                                                Button submit=dialogView.findViewById(R.id.fdtsubmitbtn);

                                                AlertDialog alertDialogimage = builder.create();
                                                alertDialogimage.show();


                                                submit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        i[0] = i[0] +1;
//                                                        alertDialogimage.dismiss();
                                                    }
                                                });



                                            }




                                        });

                                    }
                                }
                            });

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            holder.found.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.founddetails,null);
                    builder.setCancelable(true);
                    builder.setView(dialogView);



                    EditText Message=dialogView.findViewById(R.id.f7editTextmsg);
                    EditText Name=dialogView.findViewById(R.id.f7inp_name);
                    EditText Phone=dialogView.findViewById(R.id.f7inp_phone);

                    CheckBox check=dialogView.findViewById(R.id.f7checkBox);

                    Button submit=dialogView.findViewById(R.id.f7submitbtn);

                    AlertDialog alertDialogimage = builder.create();
                    alertDialogimage.show();


                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (check.isChecked()) {
                                String msg=Message.getText().toString();
                                String name=Name.getText().toString();
                                String phone=Phone.getText().toString();


                                HashMap<String,Object> map=new HashMap<>();
                                map.put("username",currentUser);
                                map.put("message",msg);
                                map.put("name",name);
                                map.put("phone",phone);
//                                map.put("userId", userID);

                                Userref.child(currentUser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            alertDialogimage.dismiss();
                                        }

                                        else {
                                            alertDialogimage.dismiss();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//

                                    }
                                });
                            }
                            else {
                                check.setError("Confirm to Continue");
                            }


                        }
                    });



                }

            });
        }
//
        Userref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()){
                    DataSnapshot dataSnapshot= task.getResult();
                    int voters= (int) dataSnapshot.getChildrenCount();

                    if(dataSnapshot.hasChild(currentUser)){
                        holder.found.setText("UNDO");
                        holder.found.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Userref.child(currentUser).removeValue();

                            }
                        });
                    }




                }

            }
        });



    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.founditem,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        ImageView img;
        TextView User,userid,upvotesnum;
        Button found;
        ImageButton msg;


        EditText inpdescription;

        String Strphone,Strname,Stritem,Strplace,Strdesc,Strdate;
        TextView name,phone,item,place,description,welcome,txtdate,status,user,looking;



        public myviewholder(@NonNull View itemView) {
            super(itemView);
            userid=(TextView)itemView.findViewById(R.id.fitemtxtuser2);
            status=(TextView)itemView.findViewById(R.id.fitemtxtstatus);

            found=(Button)itemView.findViewById(R.id.fitembtnfound);

           img=(ImageView)itemView.findViewById(R.id.fitemlostimgmissing);


            inpdescription=(EditText)itemView.findViewById(R.id.fitemlostinp_descrip);


            name=(TextView)itemView.findViewById(R.id.fitemlosttextViewowner);
            phone=(TextView)itemView.findViewById(R.id.fitemlosttextviewphone);
            item=(TextView)itemView.findViewById(R.id.fitemlosttextviewitem);
            description=(TextView)itemView.findViewById(R.id.fitemlosttextViewDescrip);
            place=(TextView)itemView.findViewById(R.id.fitemlosttextViewplace);
            txtdate=(TextView) itemView.findViewById(R.id.fitemlosttextViewmissingfrom);
            looking=(TextView) itemView.findViewById(R.id.fitemlosttextviewowner);
            msg=(ImageButton) itemView.findViewById(R.id.imageButtonmsg2);

//            welcome=(TextView)itemView.findViewById(R.id.fitemlosttextViewwelome2);





        }

    }

}
