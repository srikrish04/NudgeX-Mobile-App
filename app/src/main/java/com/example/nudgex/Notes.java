package com.example.nudgex;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.board.viewmodel.BoardsViewModel;
import com.example.nudgex.database.AppDatabase;
import com.example.nudgex.notes.entities.Note;
import com.example.nudgex.notes.NoteAdapter;
import com.example.nudgex.notes.views.NoteViewActivity;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Notes extends Fragment {
    List<Note> notesList;
    RecyclerView lvNotes;
    NoteAdapter notesAdapter;
    private BoardsViewModel boardModel;

    public Notes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        boardModel = ViewModelProviders.of(getActivity()).get(BoardsViewModel.class);

        boardModel.updateActionBarTitle("Notes");
        lvNotes = v.findViewById(R.id.notesList);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setAlignItems(AlignItems.FLEX_START);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        lvNotes.setLayoutManager(layoutManager);
        notesAdapter = new NoteAdapter(getContext(), new ArrayList<>());
        lvNotes.setAdapter(notesAdapter);
        FloatingActionButton btnAddNote = v.findViewById(R.id.btnAddNote);
        AppDatabase appDb = AppDatabase.getOfflineDatabase(getActivity());
        appDb.notesDao().getLiveNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                System.out.println("List Changed");
                notesList = notes;
                if (notesList.size() == 0) {
                    v.findViewById(R.id.notesList).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.msgEmpty).setVisibility(View.VISIBLE);
                } else {
                    v.findViewById(R.id.msgEmpty).setVisibility(View.INVISIBLE);
                    v.findViewById(R.id.notesList).setVisibility(View.VISIBLE);
                    notesAdapter.setNotes(notes);
                    notesAdapter.notifyDataSetChanged();
                }
            }
        });
        btnAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoteViewActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
