package com.example.recyclerview2;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{

    private List<Note> mNoteList;
    private Context mContent;


    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        Button important;
        Button finish;
        TextView content;
        TextView time;


        public ViewHolder(View view) {
            super(view);
            this.view = view;
            important = (Button) view.findViewById(R.id.important);
            finish = (Button) view.findViewById(R.id.finish);
            content=(TextView)view.findViewById(R.id.content);
            time=(TextView)view.findViewById(R.id.time);
        }
    }

    public NoteAdapter(List<Note> noteList) {
        mNoteList = noteList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);
                String text=note.getContent();
                Toast.makeText(v.getContext(), "you clicked finish " + text, Toast.LENGTH_SHORT).show();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);
                Intent intent=new Intent(v.getContext(),EditActivity.class);
                intent.putExtra("content",note.getContent());
                intent.putExtra("time",note.getTime());
                intent.putExtra("tag",note.getTag());
                intent.putExtra("id",note.getId());
                v.getContext().startActivity(intent);
                Toast.makeText(v.getContext(), "you clicked view " + note.getContent(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.important.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note fruit = mNoteList.get(position);
                Toast.makeText(v.getContext(), "you clicked important " + fruit.getContent(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note fruit = mNoteList.get(position);
        holder.time.setText(fruit.getTime());
        holder.content.setText(fruit.getContent());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

}
