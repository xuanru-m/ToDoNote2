package com.example.recyclerview2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.LayoutInflaterCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private NoteDatabase dbHelper;
    private NoteAdapter adapter;

    private List<Note> noteList = new ArrayList<Note>();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = new Date();
    String dataStr=df.format(date);
    EditText text;
    Button addButton;
    Toolbar myToolbar;
    RecyclerView recyclerView;

    private PopupWindow popupWindow;
    private PopupWindow popupCover;
    private ViewGroup customView;
    private ViewGroup coverView;
    private LinearLayout main;
    private LayoutInflater layoutInflater;
    private WindowManager wm;
    private DisplayMetrics metrics;

    private SharedPreferences sharedPreferences;
    private ListView lv_tag;
    private TagAdapter tagAdapter;
    private ImageButton addtag_button;
    private EditText addtag_text;
    int tag_now=1;
    List<String> tagList;


    public void initPopUpView(){
        layoutInflater=(LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView=(ViewGroup)layoutInflater.inflate(R.layout.setting_layout,null);
        coverView=(ViewGroup)layoutInflater.inflate(R.layout.setting_cover,null);
        main=findViewById(R.id.main_layout);
        wm=getWindowManager();
        metrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);//get height and width
    }

    public void showPopUpView(){
        int width=metrics.widthPixels;
        int height=metrics.heightPixels;
        popupCover=new PopupWindow(coverView,width,height,false);
        popupWindow=new PopupWindow(customView,(int)(width*0.8),height,true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //加载主界面成功之后，显示弹出

        findViewById(R.id.main_layout).post(new Runnable(){
            @Override
            public void run(){
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY,0,0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY,0,0);
                //final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags

                lv_tag=customView.findViewById(R.id.lv_tag);
                addtag_button=customView.findViewById(R.id.addtag_button);
                addtag_text=customView.findViewById(R.id.addtag_text);


                sharedPreferences=getSharedPreferences("tagList",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(sharedPreferences.toString().length()==0){//share=0,tag=0
                    List<String> tagList_temp=new ArrayList<>();
                    tagList_temp.add("tasks");
                    tagList_temp.add("My Day");
                    tagList_temp.add("Important");
                    tagList_temp.add("Planned");
                    tagList_temp.add("Assigned to you");
                    editor.putString("tagList",tagList_temp.toString());
                    editor.commit();

                    Log.d("numoftag","share size(=0) before "+Integer.toString(sharedPreferences.toString().length()));


                    Log.d("numoftag","share size(=0) list "+sharedPreferences.toString());

                }else if(sharedPreferences.toString().length()==41){//share=origin,tag

                    //String share =editor.getClass();
                    //editor.commit();
                    SharedPreferences sp_read = getSharedPreferences("tagList",MODE_PRIVATE);
                    String list_string=sp_read.getString("tagList","");
                    tagList= Arrays.asList(sharedPreferences.getString("tagList",null).substring(1,sharedPreferences.getString("tagList",null).length()-1).split(","));
                    Log.d("numoftag","share size "+Integer.toString(sharedPreferences.toString().length()));
                    Log.d("numoftag","share size(=0) list "+tagList.toString());
                }else{//share>0,taglist got elements from share
                     Log.d("numoftag","share size(>>5) list "+tagList.toString());
                    tagList= Arrays.asList(sharedPreferences.getString("tagList",null).substring(1,sharedPreferences.getString("tagList",null).length()-1).split(","));

                }

                //Log.d("tag",tagList.toString());
                tagAdapter = new TagAdapter(getApplicationContext(), tagList, numOfTagNotes(tagList));


                lv_tag.setAdapter(tagAdapter);

                addtag_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String new_tag=addtag_text.getText().toString();
                        Log.d("tag","tagList: "+tagList.toString());
                        List<String> realtagList=new ArrayList<String>(tagList);
                        realtagList.add(new_tag);
                        Log.d("tag",realtagList.toString());
                        //tagList.add(new_tag);
                        sharedPreferences=getSharedPreferences("tagList",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("tagList",realtagList.toString());
                        editor.commit();

                        tagAdapter = new TagAdapter(getApplicationContext(), realtagList, numOfTagNotes(realtagList));
                        lv_tag.setAdapter(tagAdapter);
                        Toast.makeText(getApplicationContext(), tagList.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                lv_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(view.getContext(), "menu " +position, Toast.LENGTH_SHORT).show();
//                      List<String> tagList = Arrays.asList(sharedPreferences.getString("tagListString", null).split("_")); //获取tags
                        int tag = position + 1;
                        List<Note> temp = new ArrayList<>();
                        for (int i = 0; i < noteList.size(); i++) {
                            if(tag==1){
                                    Note note = noteList.get(i);
                                    temp.add(note);

                            }
                            else if (noteList.get(i).getTag() == tag) {
                                Note note = noteList.get(i);
                                temp.add(note);
                            }

                        }

                        NoteAdapter tempAdapter = new NoteAdapter(temp);
                        recyclerView.setAdapter(tempAdapter);
                        //myToolbar.setTitle(tagList.get(position));
                        popupWindow.dismiss();
                        //Log.d(TAG, position + "");
                        tag_now=tag;
                   }
                });

               coverView.setOnTouchListener(new View.OnTouchListener() {
                   @Override
                   public boolean onTouch(View v, MotionEvent event) {
                       popupWindow.dismiss();
                       return true;
                   }
               });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                    }
                });

            }
        });
    }
    public List<Integer> numOfTagNotes(List<String> noteStringList){
        Integer[] numbers = new Integer[noteStringList.size()];
        for(int i = 0; i < numbers.length; i++) numbers[i] = 0;
        for(int i = 0; i < noteList.size(); i++){
            //Log.d("numoftag","numbers "+numbers.toString());
            //Log.d("numoftag","i "+i);
            //Log.d("numoftag","taglist.size "+Integer.toString(noteStringList.size()));
            //Log.d("numoftag","noteList.get(i).getTag() "+noteList.get(i).getTag());
            numbers[noteList.get(i).getTag() - 1] ++;
        }
        return Arrays.asList(numbers);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text=findViewById(R.id.textView);
        addButton=findViewById(R.id.addButton);
        myToolbar=findViewById(R.id.myToolbar);

        //adapter=new NoteAdapter(noteList);
        //initFruits();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //Gridlayout
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(noteList);

        refreshListView();
        recyclerView.setAdapter(adapter);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initPopUpView();
        myToolbar.setNavigationIcon(R.drawable.dehaze);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopUpView();
            }
        });



        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshListView();
                Log.d("refresh","refresh");
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent();
                //intent.putExtra("input",text.getText().toString());//new note
                String content=text.getText().toString();
                String time=dataToStr();
                Note note=new Note(content,time,tag_now);
                CRUD op=new CRUD(getApplicationContext());
                op.open();
                op.addNote(note);
                op.close();
                noteList.add(note);
                Toast.makeText(v.getContext(), "add task: " + text.getText().toString(), Toast.LENGTH_SHORT).show();
                //v.getContext().startActivity(intent);
                refreshListView();
            }
        });
    }
    public String dataToStr(){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public void refreshListView(){
        CRUD op=new CRUD(getApplicationContext());
        op.open();

        if(noteList.size()>0) noteList.clear();
        noteList.addAll(op.getAllNotes());

        op.close();
        adapter.notifyDataSetChanged();
        List<Note> temp = new ArrayList<>();
        for (int i = 0; i < noteList.size(); i++) {
            if(tag_now==1){
                Note note = noteList.get(i);
                temp.add(note);
            }
            else if (noteList.get(i).getTag() == tag_now) {
                Note note = noteList.get(i);
                temp.add(note);
            }

        }

        NoteAdapter tempAdapter = new NoteAdapter(temp);
        recyclerView.setAdapter(tempAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("refresh","onResume");
        refreshListView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete_all:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("delete all notes?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper=new NoteDatabase(getApplicationContext());
                                SQLiteDatabase db=dbHelper.getWritableDatabase();
                                db.delete("notes",null,null);
                                db.execSQL("update sqlite_sequence set seq=0 where name='notes'");
                                refreshListView();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            //case R.id.normalNote:
            //   Toast.makeText(getApplicationContext(),"sadfasf",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }




}