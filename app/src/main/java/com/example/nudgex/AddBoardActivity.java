package com.example.nudgex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nudgex.board.entities.Board;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddBoardActivity extends AppCompatActivity {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_board);
        Intent intent = getIntent();
        int boardId = intent.getIntExtra("boardId", -1);
        AppDatabase appDB = AppDatabase.getOfflineDatabase(getApplicationContext());
        MaterialButton btnCreateBoard = findViewById(R.id.btnCreateBoard);
        final TextInputEditText boardName = findViewById(R.id.newBoard);
        final TextInputEditText boardDescription = findViewById(R.id.boardDescription);

        if (boardId != -1){
            btnCreateBoard.setText("Update Board");
            Board board = appDB.boardDao().getBoardById(boardId);
            boardName.setText(board.name);
            boardDescription.setText(board.description);
        }

        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = boardName.getText().toString();
                String description = boardDescription.getText().toString();
                Board board = new Board();
                board.name = name;
                board.description = description;

                if (boardId == -1){
                    appDB.boardDao().insertBoard(board);
                }else{
                    board.boardId = boardId;
                    appDB.boardDao().updateBoard(board);
                }
                finish();

            }
        });
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.add_board, container, false);
//        MaterialButton btnCreateBoard = view.findViewById(R.id.btnCreateBoard);
//        final TextInputEditText boardName = view.findViewById(R.id.newBoard);
//        final TextInputEditText boardDescription = view.findViewById(R.id.boardDescription);
//        btnCreateBoard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = boardName.getText().toString();
//                String description = boardDescription.getText().toString();
////                    System.out.println(name);
////                    System.out.println(description);
//                Board board = new Board();
//                board.name = name;
//                board.description = description;
//                AppDatabase appDB = AppDatabase.getOfflineDatabase(getActivity());
//                appDB.boardDao().insertBoard(board);
//                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.fragmentChange, new Boards());
//                ft.commit();
//            }
//        });
//
//        return view;
//    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }
}
