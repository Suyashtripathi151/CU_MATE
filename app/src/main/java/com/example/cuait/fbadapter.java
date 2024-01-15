package com.example.cuait;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class fbadapter extends FirebaseRecyclerAdapter<issuemodel,fbadapter.myviewholder> {
    public fbadapter(@NonNull FirebaseRecyclerOptions<issuemodel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull issuemodel model) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String currentUser=user.getUid();
        String querykey=getRef(position).getKey();
        DatabaseReference reference,queryref,dbuserdata,dbrefreaduserid;
        reference=FirebaseDatabase.getInstance().getReference();
        queryref=reference.child("query").child(querykey).child("voters");
        dbuserdata=reference.child("userdata");
        dbrefreaduserid = reference.child("usernames");
        String Userkey=getRef(position).getKey();
        final String[] userID = new String[1];

        String key =queryref.push().getKey();

        queryref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()){
                    DataSnapshot dataSnapshot= task.getResult();
                    int voters= (int) dataSnapshot.getChildrenCount();
                    holder.upvotesnum.setText(String.valueOf(voters)+" Upvotes");

                    if(dataSnapshot.hasChild(currentUser)){
                        holder.upvote.setText("UNDO");
                        holder.upvote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                queryref.child(currentUser).removeValue();

                            }
                        });
                    }

                }


                else {
//                    Toast.makeText(Homepage.this, "NOT FOUND", Toast.LENGTH_SHORT).show();

                }
            }
        });


        if(model.getUsername().matches(currentUser)){
            holder.upvote.setText("DELETE");
            holder.upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.disable,null);


                    builder.setCancelable(true);
                    builder.setView(dialogView);

                    Button ok=dialogView.findViewById(R.id.ok);
                    Button dismiss=dialogView.findViewById(R.id.Dismiss);
                    TextView message=dialogView.findViewById(R.id.txtWarning);

                    message.setText("Are you sure you want to delete your your note? Once you click \"OK,\" your report and all associated data will be permanently deleted. This action cannot be undone");

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
                                            assert Userkey != null;
                                            reference.child("userdata").child(userID).child("myqueries").child(Userkey).removeValue();
                                            reference.child("query").child(Userkey).removeValue();

                                        }
                                    }
                                }
                            });

                            alertDialogimage.dismiss();
                        }
                    });
                }
            });
        }
        else {
            holder.upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("key",key);
                    map.put("username",currentUser);


                    queryref.child(currentUser).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                }
            });
        }


        holder.query.setText(model.getQuery());


        String UId=model.getUserID();
        if(!(UId ==null)){
            dbuserdata.child(UId).child("imageUrl").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.getResult().exists()){
                        DataSnapshot dataSnapshot= task.getResult();
//                        String link=dataSnapshot.toString();
                       String link= String.valueOf(dataSnapshot.getValue());
                        if(model.usertype.matches("- User")){
                            Picasso.get().load(link).into(holder.pic);
                        }
                        else {
                            Picasso.get().cancelRequest(holder.pic);
                            holder.pic.setImageResource(R.drawable.ic_baseline_person_24);
                        }
                    }
                    else {
                        Picasso.get().cancelRequest(holder.pic);
                        holder.pic.setImageResource(R.drawable.ic_baseline_person_24);
                    }
                }

            });
        }

        String usertype=model.getUsertype();
        if(usertype.matches("- Anonymous")){
            holder.userid.setText("- Anonymous");
        }
        else {
            holder.userid.setText(model.getUserID());
        }

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.issuesitem,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        ImageView pic;
        TextView query,userid,upvotesnum;
        Button upvote;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            query=(TextView)itemView.findViewById(R.id.inptcaption);
            userid=(TextView)itemView.findViewById(R.id.txtuser2);

            upvote=(Button)itemView.findViewById(R.id.btnupvote);
            pic=(ImageView)itemView.findViewById(R.id.profilepicr);
            upvotesnum=(TextView)itemView.findViewById(R.id.txtupvotes);


        }

    }

    }


