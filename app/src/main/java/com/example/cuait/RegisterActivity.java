package com.example.cuait;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class RegisterActivity extends AppCompatActivity {
    private EditText NAME,PERSONALEMAIL,PHONE;
    private CheckBox check;
    private Button alreadybtm,nextbtm;

    private ImageView img;




    String EmailPattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    String EmailPattern2="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@cuchd\\.in$";

    Boolean ExistingStudent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        img=findViewById(R.id.logo4);

        check=findViewById(R.id.checkBox);

        alreadybtm=findViewById(R.id.alreadybutton2);
        nextbtm=findViewById(R.id.next2);

        NAME=findViewById(R.id.inpname2);

        PERSONALEMAIL=findViewById(R.id.inpemail2);
        PHONE=findViewById(R.id.inphone2);


        alreadybtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        nextbtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkreg();
            }
        });



    }
    public void checkreg(){
        String Name_=NAME.getText().toString();
        String Personal_=PERSONALEMAIL.getText().toString();
        String Phone_=PHONE.getText().toString();
        String[] Data={Name_,Personal_,Phone_};
        EditText[] allinputs={NAME,PERSONALEMAIL,PHONE};
        if(Personal_.isEmpty() || Name_.isEmpty()  || Phone_.isEmpty()){
            for(int i=0;i<+3;i++){
                if (Data[i].isEmpty()) {
                    allinputs[i].setError("This Field Is Missing ! ");
                }
            }
            //
        }
        else if(Personal_.matches(EmailPattern2)){
            PERSONALEMAIL.setError("Enter Your Personal E-Mail ");
        }
        else if(!Personal_.matches(EmailPattern)){
            PERSONALEMAIL.setError("Email is not Valid !");
        }

        else {
            if(check.isChecked()){
                ExistingStudent=true;
                Intent intent=new Intent(RegisterActivity.this,RegisterActivity2.class);
                intent.putExtra("keyname",Name_);
                intent.putExtra("keypersonal",Personal_);
                intent.putExtra("keyphone",Phone_);
                intent.putExtra("Existing",true);
                startActivity(intent);
            }
            else{
                ExistingStudent=false;
                Intent intent=new Intent(RegisterActivity.this,RegisterActivity2.class);
                intent.putExtra("keyname",Name_);
                intent.putExtra("keypersonal",Personal_);
                intent.putExtra("keyphone",Phone_);
                intent.putExtra("Existing",false);
                startActivity(intent);
            }
        }

    }
}