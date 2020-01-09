package com.example.nudgex.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.R;
import com.example.nudgex.database.AppDatabase;
import com.example.nudgex.notes.entities.Image;
import com.example.nudgex.notes.entities.Note;
import com.example.nudgex.notes.views.NoteViewActivity;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.File;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    public class NoteHolder extends RecyclerView.ViewHolder {
        ActionMode actionMode;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                // Called when the user long-clicks on someView
                public boolean onLongClick(View view) {
                    if (actionMode != null) {
                        return false;
                    }

                    // Start the CAB using the ActionMode.Callback defined above
                    actionMode = ((AppCompatActivity)view.getContext()).startActionMode(actionModeCallback);
                    view.setSelected(true);
                    return true;
                }
            });

//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    System.out.println("Long Click working");
//                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
//                    popupMenu.inflate(R.menu.menu_board);
//                    popupMenu.show();
//                    return false;
//                }
//            });
        }

        private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.note_item_menu, menu);
                return true;
            }

            // Called each time the action mode is shown. Always called after onCreateActionMode, but
            // may be called multiple times if the mode is invalidated.
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.deleteNote:
                        deleteNote(getAdapterPosition());
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                actionMode = null;
            }
        };
    }

    List<Note> notes;
    public Context context;
    AppDatabase appDB;

    public NoteAdapter(Context context, List<Note> objects) {
        this.context = context;
        notes = objects;
        appDB = AppDatabase.getOfflineDatabase(context);
    }



    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_card_item, parent,
                        false);
        NoteHolder holder = new NoteHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        TextView tvTitle = holder.itemView.findViewById(R.id.title);
        ImageView ivImage = holder.itemView.findViewById(R.id.image);
        Note note = notes.get(position);

        List<Image> images = appDB.noteImageDao().getImagesByNoteId(note.id);

        if (note.title.isEmpty()) {
            if (note.content.isEmpty() && images.size() > 0) {
                showImage(ivImage, images.get(0).imageUrl);
                ivImage.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(note.content);
            }
        } else {

            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(note.title);
            if (images.size() > 0) {
                showImage(ivImage, images.get(0).imageUrl);
            }
        }
        System.out.println(note.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoteViewActivity.class);
                intent.putExtra("noteId", note.id);
                context.startActivity(intent);
            }
        });
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof FlexboxLayoutManager.LayoutParams) {
            FlexboxLayoutManager.LayoutParams flexboxLp =
                    (FlexboxLayoutManager.LayoutParams) lp;
            flexboxLp.setFlexBasisPercent(0.46f);
        }
    }

    public void showImage(ImageView iv, String imagePath) {
        File imgFile = new File(imagePath);
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            Bitmap scaledBitmap = ImageNoteAdapter.scaleDown(bitmap, 500, true);
            iv.setImageBitmap(bitmap);
            iv.setVisibility(View.VISIBLE);
        }
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    void deleteNote(int position){
        Note noteToBeDeleted = notes.get(position);
        appDB.notesDao().deleteNote(noteToBeDeleted);
    }

}
