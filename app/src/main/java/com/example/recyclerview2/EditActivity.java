package com.example.recyclerview2;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

        EditText et;
        Toolbar myToolbar;

        private String old_content;
        private String old_time="";
        private int old_Tag=1;
        private long id=0;
        private int openMode=0;
        private int tag=1;
        private String content;
        private String time;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_layout);
            et=findViewById(R.id.et);
            Intent getIntent=getIntent();
            myToolbar=findViewById(R.id.myToolbar);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    content=et.getText().toString();
                    Note note=new Note(content,time,1);
                    CRUD op=new CRUD(getApplicationContext());
                    op.open();

                    op.removeNote(id);
                    op.addNote(note);
                    op.close();
                    finish();

                }
            });

            id=getIntent.getLongExtra("id",0);
            this.id=id;
            this.time=dataToStr();
            old_content=getIntent.getStringExtra("content");
            old_time=getIntent.getStringExtra("time");
            et.setText(old_content);
            et.setSelection(old_content.length());
      }

      public boolean onKeyDown(int keyCode, KeyEvent event){
            if(keyCode==KeyEvent.KEYCODE_HOME){
                return true;
            }else if(keyCode==KeyEvent.KEYCODE_BACK){
                this.content=et.getText().toString();
                Note note=new Note(content,time,1);
                CRUD op=new CRUD(getApplicationContext());
                op.open();

                op.removeNote(id);
                op.addNote(note);
                op.close();


                finish();

                return true;
            }
            return true;
      }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete:
                new AlertDialog.Builder(EditActivity.this).setMessage("true to delete?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CRUD op=new CRUD(getApplicationContext());
                        op.open();
                        op.removeNote(id);
                        op.close();
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public String dataToStr(){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
    }