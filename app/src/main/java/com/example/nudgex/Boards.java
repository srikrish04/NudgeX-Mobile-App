package com.example.nudgex;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.nudgex.board.adapters.BoardAdapter;
import com.example.nudgex.board.entities.Board;
import com.example.nudgex.board.viewmodel.BoardsViewModel;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Boards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Boards extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BoardsViewModel boardModel;
    private BoardAdapter boardAdapter;
    private ListView listBoardsView;
    private FloatingActionButton btnAddBoard;
    private List<Board> bordList;
    private Boards fragment;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Boards() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Boards.
     */
    // TODO: Rename and change types and number of parameters
    public static Boards newInstance(String param1, String param2) {
        Boards fragment = new Boards();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_boards, container, false);
        fragment = this;
        listBoardsView = view.findViewById(R.id.listBoards);
        boardModel = ViewModelProviders.of(getActivity()).get(BoardsViewModel.class);

        boardModel.updateActionBarTitle("Boards");

        AppDatabase appDB = AppDatabase.getOfflineDatabase(getActivity());

        boardModel.boards.observe(this, new Observer<List<Board>>() {
            @Override
            public void onChanged(List<Board> boards) {
                bordList = boards;
                if (boards.size() == 0){
                    view.findViewById(R.id.empty_list_msg).setVisibility(View.VISIBLE);
                    listBoardsView.setVisibility(View.INVISIBLE);
                }
                else if (boards != null) {
                    view.findViewById(R.id.empty_list_msg).setVisibility(View.INVISIBLE);
                    listBoardsView.setVisibility(View.VISIBLE);
                    System.out.println("Instantiate Adapter");
                    boardAdapter = new BoardAdapter(
                            getActivity().getApplicationContext(),
                            bordList);
                    listBoardsView.setAdapter(boardAdapter);
                    boardAdapter.updateBoards(boards);
                }
            }
        });




        btnAddBoard = view.findViewById(R.id.btnAddBoard);
        btnAddBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddBoardActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
