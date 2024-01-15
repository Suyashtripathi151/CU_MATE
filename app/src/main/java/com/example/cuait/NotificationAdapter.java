package com.example.cuait;

import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class NotificationAdapter extends FirebaseRecyclerAdapter<NotificationModel, NotificationAdapter.myviewholder> {
    String Userkey;
    FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
    String currentUser=user.getUid();
    DatabaseReference ref=FirebaseDatabase.getInstance().getReference();

    public NotificationAdapter(@NonNull FirebaseRecyclerOptions<NotificationModel> options) {
        super(options);
    }



    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull NotificationModel model) {

        int reversedPosition = getItemCount() - position - 1;
        NotificationModel reversedModel = getItem(reversedPosition);
        holder.bind(reversedModel);


        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String currentUser=user.getUid();
        Userkey=getRef(position).getKey();

        String substringToDelete="You received this message because you are subscribed to the Google Groups\"20AIT.BDA4\" group.To unsubscribe from this group and stop receiving emails from it, send an emailto 20aitbda4+unsubscribe@googlegroups.com.To view this discussion on the web visithttps://groups.google.com/d/msgid/20aitbda4/CAOxj5NPz0GfDDqc_PkL5qqz4mVHsJKvkVcazVFt%3D%3D1Cdr-Lp8Q%40mail.gmail.com[https://groups.google.com/d/msgid/20aitbda4/CAOxj5NPz0GfDDqc_PkL5qqz4mVHsJKvkVcazVFt%3D%3D1Cdr-Lp8Q%40mail.gmail.com?utm_medium=email&utm_source=footer].For more options, visit https://groups.google.com/d/optout";
        String a=model.getName();;
        String modifiedString = a.replace(substringToDelete, "");

        String substringToDelete2="Fwd: Notice for a Campus Placement Drive - ";
        String a2=model.getSubject();
        String modifiedString2 = a2.replace(substringToDelete2, "");




        assert Userkey != null;
        ref.child("JobAlert").child(Userkey).child("seen").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot dataSnapshot=task.getResult();
                        if(dataSnapshot.hasChild(currentUser)){
                            String n="NEW";
                            holder.deadline.setText("NEW");
                        }
                        else {
                            holder.deadline.setEnabled(false);
                            holder.deadline.setVisibility(View.INVISIBLE);
//                            holder.deadline.setText("SEEN");
                        }
                    }

                    else {
//                        holder.deadline.setEnabled(false);
//                        holder.deadline.setVisibility(View.INVISIBLE);
                    }
                }


            }
        });

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationitem,parent,false);
        return new myviewholder(view);
    }

    class myviewholder extends RecyclerView.ViewHolder{

        TextView subject,preview,deadline;
        Button view;


        public myviewholder(@NonNull View itemView) {
            super(itemView);
            subject=(TextView)itemView.findViewById(R.id.notifCompName);
            deadline=(TextView)itemView.findViewById(R.id.notifDeadline);
            preview=(TextView)itemView.findViewById(R.id.notificPreview);
            view=(Button)itemView.findViewById(R.id.jobview);


        }

        public void bind(NotificationModel model) {

            String substringToDelete="You received this message because you are subscribed to the Google Groups\"20AIT.BDA4\" group.To unsubscribe from this group and stop receiving emails from it, send an emailto 20aitbda4+unsubscribe@googlegroups.com.To view this discussion on the web visithttps://groups.google.com/d/msgid/20aitbda4/CAOxj5NPz0GfDDqc_PkL5qqz4mVHsJKvkVcazVFt%3D%3D1Cdr-Lp8Q%40mail.gmail.com[https://groups.google.com/d/msgid/20aitbda4/CAOxj5NPz0GfDDqc_PkL5qqz4mVHsJKvkVcazVFt%3D%3D1Cdr-Lp8Q%40mail.gmail.com?utm_medium=email&utm_source=footer].For more options, visit https://groups.google.com/d/optout";
            String a=model.getName();;
            String modifiedString = a.replace(substringToDelete, "");

            String substringToDelete2="Fwd: Notice for a Campus Placement Drive - ";
            String a2=model.getSubject();
            String modifiedString2 = a2.replace(substringToDelete2, "");

            subject.setText(modifiedString2);
            preview.setText(model.getBodyPreview());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                    View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.jobdetails,null);
                    builder.setCancelable(true);
                    builder.setView(dialogView);

                    TextView message=dialogView.findViewById(R.id.jobicPreview);
                    message.setText(modifiedString);
                    TextView compName=dialogView.findViewById(R.id.jobCompName);
                    String re=ref.getKey();
                    compName.setText(modifiedString2);
                    compName.setMovementMethod(LinkMovementMethod.getInstance());

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("USERID", currentUser);


                    ref.child("JobAlert").child(Userkey).child("seen").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                        }
                    });


                    AlertDialog alertDialogimage = builder.create();
                    alertDialogimage.show();
                }
            });






        }



    }

}
