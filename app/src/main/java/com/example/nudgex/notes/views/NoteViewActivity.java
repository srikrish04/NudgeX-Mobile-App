package com.example.nudgex.notes.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.R;
import com.example.nudgex.notes.ImageNoteAdapter;
import com.example.nudgex.notes.NoteViewModel;
import com.example.nudgex.notes.entities.Image;
import com.example.nudgex.notes.entities.Note;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NoteViewActivity extends AppCompatActivity {
    final int IMAGE_CODE = 17;
    final int SCRIBBLE_CODE = 18;
    TextInputEditText edtTextTitle, edtTextContent;
    RecyclerView viewImage;
    ImageNoteAdapter viewImageAdapter;
    boolean insertImageFlag = false;
    long noteId;
    NoteViewModel model;
    Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        model = ViewModelProviders.of(this).get(NoteViewModel.class);
        model.setActivity(this);
        setContentView(R.layout.add_note);
        viewImage = findViewById(R.id.imageList);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getApplicationContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        layoutManager.setJustifyContent(JustifyContent.CENTER);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        viewImage.setLayoutManager(layoutManager);
        viewImageAdapter = new ImageNoteAdapter(this, new ArrayList<>());
        viewImage.setAdapter(viewImageAdapter);
        edtTextTitle = findViewById(R.id.noteTitle);
        edtTextContent = findViewById(R.id.noteContent);

        Intent intent = getIntent();
        noteId = intent.getIntExtra("noteId", -1);
        if (noteId != -1) {
            note = model.appDB.notesDao().getNoteById((int) noteId);
            edtTextTitle.setText(note.title);
            edtTextContent.setText(note.content);
        }
        model.setNoteId(noteId);
        System.out.println("Note Id: " + noteId);
        ImageView ivImage = findViewById(R.id.image);
        if (noteId != -1) {
            observeImageList(noteId);
        }
    }

    private void observeImageList(long noteId) {
        model.getDatabaseImageUriList(noteId).removeObservers(this);
        model.getDatabaseImageUriList(noteId).observe(this, new Observer<List<Image>>() {
            @Override
            public void onChanged(List<Image> strings) {
                System.out.println("ImageUri Changed");
                System.out.println(strings);
                ArrayList<String> paths = (ArrayList<String>) strings.stream().map(image -> {
                    return image.imageUrl;
                }).collect(Collectors.toList());
                viewImageAdapter.setImages(paths);
                viewImageAdapter.notifyDataSetChanged();
            }
        });
    }

    // https://src-bin.com/en/q/33e76b
    @SuppressLint("ObsoleteSdkInt")
    public String getPathFromURI(Uri uri) {
        String realPath = "";
// SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int column_index = 0;
            String result = "";
            if (cursor != null) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                realPath = cursor.getString(column_index);
            }
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                realPath = cursor.getString(column_index);
            }
        }
        // SDK > 19 (Android 4.4)
        else {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
            int columnIndex = 0;
            if (cursor != null) {
                columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    realPath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return realPath;
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.insertImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(intent, "Select A Picture"),
                        IMAGE_CODE);
                return true;
            case R.id.insertScribble:
                Intent scribbleIntent = new Intent(this, AddScribbleActivity.class);
                startActivityForResult(scribbleIntent, SCRIBBLE_CODE);

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case (IMAGE_CODE):
                    Uri uri = data.getData();
                    System.out.println(getPathFromURI(uri));
                    model.insertImageUri(getPathFromURI(uri));
                    if (noteId == -1) {
                        String title = edtTextTitle.getText().toString();
                        String content = edtTextContent.getText().toString();
                        note = new Note(title, content);
                        noteId = model.appDB.notesDao().insertNote(note);
                    }
                    saveImageToDatabase(noteId);
                    model.clearList();
                    observeImageList(noteId);
                    break;
                case (SCRIBBLE_CODE):
                    String filePath = data.getStringExtra("filePath");
                    System.out.println("Scribble.." + filePath);
                    model.insertImageUri(filePath);
                    if (noteId == -1) {
                        String title = edtTextTitle.getText().toString();
                        String content = edtTextContent.getText().toString();
                        note = new Note(title, content);
                        noteId = model.appDB.notesDao().insertNote(note);
                    }
                    saveImageToDatabase(noteId);
                    model.clearList();
                    observeImageList(noteId);
                    break;
                default:
                    return;
            }
        }
    }

    private void saveImageToDatabase(long noteId) {
        for (String uri : model.getImageUriList()) {
            Image image = new Image();
            image.imageUrl = uri;
            image.noteId = (int) noteId;
            model.appDB.noteImageDao().insertImage(image);
        }
    }

    @Override
    protected void onStop() {
        System.out.println("OnStop....");
        super.onStop();
        String title = edtTextTitle.getText().toString();
        String content = edtTextContent.getText().toString();
        if (noteId != -1 && note != null) {
            Note note = model.appDB.notesDao().getNoteById((int) noteId);
            note.title = title;
            note.content = content;
            model.appDB.notesDao().updateNote(note);
        } else if (!(title.isEmpty() && content.isEmpty())) {
            note = new Note(title, content);
            noteId = model.appDB.notesDao().insertNote(note);
        }
    }
}
