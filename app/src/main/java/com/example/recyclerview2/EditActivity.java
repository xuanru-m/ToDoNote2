package com.example.recyclerview2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
        private boolean tagChange=false;


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

            Spinner mySpinner=(Spinner)findViewById(R.id.spinner);
            SharedPreferences sharedPreferences=getSharedPreferences("tagList",MODE_PRIVATE);
            List<String> tagList= Arrays.asList(sharedPreferences.getString("tagList",null).substring(1,sharedPreferences.getString("tagList",null).length()-1).split(","));
            Log.d("pre",sharedPreferences.getString("tagList",null));
            ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,R.layout.spinner_item,tagList);
            myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(myAdapter);

            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tag=(int)id+1;
                    Log.d("newtag",Integer.toString(tag));
                    tagChange=true;
                    content=et.getText().toString();
//                    Note note=new Note(content,time,tag);
//                    id=getIntent.getLongExtra("id",0);
//                    Log.d("note","id "+id);
//                    note.setId(id);
//                    CRUD op=new CRUD(getApplicationContext());
//                    op.open();
//                    op.updateNote(note);
//                    Log.d("note","ID "+note.getId());
//                    Log.d("note","content "+note.getContent());
//                    Log.d("note","tag "+Integer.toString(note.getTag()));
//                    op.close();
//                    String content = data.getExtras().getString("content");
//                    String time = data.getExtras().getString("time");
//                    int tag = data.getExtras().getInt("tag", 1);
//                    Note newNote = new Note(content, time, tag);
//                    newNote.setId(note_Id);
//                    CRUD op = new CRUD(context);
//                    op.open();
//                    op.updateNote(newNote);
//                    achievement.editNote(op.getNote(note_Id).getContent(), content);
//                    op.close();
                    id=getIntent().getLongExtra("id",0);
                    CRUD op=new CRUD(getApplicationContext());
                    op.open();
                    Note note=new Note(id,content,time,tag);
                    op.updateNote(note);
                    op.close();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CRUD op=new CRUD(getApplicationContext());
                    op.open();
                    tag=op.getNote(id).getTag();
                    content=et.getText().toString();
                    long id=getIntent().getLongExtra("id",0);
                    Note note=new Note(id,content,time,tag);
                    op.updateNote(note);
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
                CRUD op=new CRUD(getApplicationContext());
                op.open();
                tag=op.getNote(id).getTag();
                content=et.getText().toString();
                long id=getIntent().getLongExtra("id",0);
                Note note=new Note(id,content,time,tag);
                op.updateNote(note);
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