package com.example.nudgex.board.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nudgex.AddBoardActivity;
import com.example.nudgex.R;
import com.example.nudgex.board.entities.Board;
import com.example.nudgex.board.view.BoardViewActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BoardAdapter extends ArrayAdapter<Board>
{

    private List<Board> dataset;
    ListView listView;
    Context mContext;
    public BoardAdapter(Context context, List<Board> objects) {
        super(context, 0, objects);
        this.dataset = objects;
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_card_item, parent,false);

        final Board board = dataset.get(position);
        MaterialButton btnEditBorad = listItem.findViewById(R.id.btnEditBoard);
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(board.name);
                Intent intent = new Intent(mContext, BoardViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("boardId", board.boardId);
                mContext.startActivity(intent);

            }
        });
        btnEditBorad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddBoardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("boardId", board.boardId);
                mContext.startActivity(intent);

            }
        });
        TextView boardName = (TextView) listItem.findViewById(R.id.name);
        boardName.setText(board.name);
        return listItem;
    }

    public void updateBoards(List<Board> board){
        this.dataset = board;
        this.notifyDataSetChanged();

    }
}
