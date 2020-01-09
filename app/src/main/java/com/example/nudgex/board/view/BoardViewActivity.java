package com.example.nudgex.board.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nudgex.R;
import com.example.nudgex.board.adapters.ItemAdapter;
import com.example.nudgex.board.entities.Board;
import com.example.nudgex.board.entities.Column;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.board.viewmodel.ColumnViewModel;
import com.example.nudgex.database.AppDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.DragItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoardViewActivity extends AppCompatActivity {
    ColumnViewModel columnViewModel;
    int boardId;
    List<Column> cols;
    private BoardView mBoardView;
    private ImageButton btnAddTask;
    private ImageButton btnEditColumn;
    private boolean mGridLayout;
    AppDatabase appDB;
    Board board;
    int columnOldPosition = -1, columnNewPosition = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_layout);
        appDB = AppDatabase.getOfflineDatabase(this);
        Intent intent = getIntent();
        boardId = intent.getIntExtra("boardId", -1);
        board = appDB.boardDao().getBoardById(boardId);
        getSupportActionBar().setTitle(board.name);
//        columnViewModel = ViewModelProviders.of(this).get(ColumnViewModel.class);
        mBoardView = findViewById(R.id.board_view);
        initializeBoardView();
//        resetBoard();
//        loadColums();
        FloatingActionButton btnAddColumn = findViewById(R.id.btnAddColumn);
        btnAddColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddColumnActivity.class);
                intent.putExtra("boardId", boardId);
                startActivity(intent);
            }
        });
    }

    private void loadColums() {
        cols = appDB.columnDao().getColumnByBoardId(boardId);
        if (cols.size() == 0) {
            findViewById(R.id.fragmentChange).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.fragmentChange).setVisibility(View.INVISIBLE);
            for (Column column : cols) {
                makeColumn(column);
            }
        }
    }

    @Override
    protected void onResume() {
        resetBoard();
        loadColums();
        System.out.println("Hello Resume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        System.out.println("On Pause");
        super.onPause();
    }

    private void initializeBoardView() {
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        mBoardView.setBoardListener(new BoardView.BoardListener() {

            @Override
            public void onItemDragStarted(int column, int row) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
                System.out.println("Draging Started");
            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                System.out.println("Drag Item Ended:");
                System.out.println("Column " + fromColumn + " -> " + toColumn + ", Row " + fromRow + " -> " + toRow);
                Task task = appDB.taskDao().getTaskByColumnIdAndRowNumber(fromColumn + 1, fromRow);
                task.columnId = toColumn + 1;
                task.rowNumber = toRow;
                appDB.taskDao().decreaseTaskRowOnMove(fromColumn + 1, fromRow);
                appDB.taskDao().increaseTaskRowOnMove(toColumn + 1, toRow);
                appDB.taskDao().updateTask(task);
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                System.out.println("Item Position Changed:");
                System.out.println("Column " + oldColumn + " -> " + newColumn + ", Row " + oldRow + " -> " + newRow);
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
                TextView itemCount1 = mBoardView.getHeaderView(oldColumn).findViewById(R.id.item_count);
                itemCount1.setText(String.valueOf(mBoardView.getAdapter(oldColumn).getItemCount()));
                TextView itemCount2 = mBoardView.getHeaderView(newColumn).findViewById(R.id.item_count);
                itemCount2.setText(String.valueOf(mBoardView.getAdapter(newColumn).getItemCount()));
            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
                //Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragStarted(int position) {
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
                columnOldPosition = position;
            }

            @Override
            public void onColumnDragChangedPosition(int oldPosition, int newPosition) {
                //Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragEnded(int position) {
                //Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();

                columnNewPosition = position;
                if (columnOldPosition != columnNewPosition) {
                    Column column = appDB.columnDao().getColumnByBoardIdAndSort(boardId, columnOldPosition);
                    column.columnSort = columnNewPosition;
                    if (columnNewPosition > columnOldPosition) {
                        appDB.columnDao().decreaseColumnSort(boardId, columnOldPosition, columnNewPosition);
                    } else {
                        appDB.columnDao().increaseColumnSort(boardId, columnNewPosition, columnOldPosition);
                    }
                    appDB.columnDao().updateColumn(column);
                }

            }
        });
        mBoardView.setBoardCallback(new BoardView.BoardCallback() {
            @Override
            public boolean canDragItemAtPosition(int column, int dragPosition) {
                // Add logic here to prevent an item to be dragged
                return true;
            }

            @Override
            public boolean canDropItemAtPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                // Add logic here to prevent an item to be dropped
                return true;
            }
        });
    }

    private void resetBoard() {
        mBoardView.clearBoard();
        mBoardView.setCustomDragItem(mGridLayout ? null :
                new MyDragItem(this,
                        R.layout.column_item));
        mBoardView.setCustomColumnDragItem(mGridLayout ? null :
                new MyColumnDragItem(this,
                        R.layout.column_drag_layout));
    }

    private void makeColumn(Column column) {

        final ArrayList<Pair<Integer, Task>> mItemArray = new ArrayList<>();
        List<Task> tasks = appDB.taskDao().getTasksByColumnId(column.columnId);
        mItemArray.clear();
        int taskCount = tasks.size();
        ArrayList<Pair<Integer, Task>> taskList = (ArrayList<Pair<Integer, Task>>) tasks.stream()
                .map(
                        task -> {
                            return new Pair<>(task.taskId, task);
                        })
                .collect(Collectors.toList());
        mItemArray.addAll(taskList);
        final ItemAdapter listAdapter = new ItemAdapter(mItemArray,
                mGridLayout ? R.layout.grid_item : R.layout.column_item, R.id.item_layout, true);
        final View header = View.inflate(getApplicationContext(), R.layout.column_header, null);
        ((TextView) header.findViewById(R.id.text)).setText(column.name);
        ((TextView) header.findViewById(R.id.item_count)).setText("" + tasks.size());

        mBoardView.addColumn(listAdapter, header, header, false,
                mGridLayout ? new GridLayoutManager(getApplicationContext(), 4)
                        : new LinearLayoutManager(getApplicationContext())
        );

        btnAddTask = (ImageButton) header.findViewById(R.id.btnAddTask);
        btnEditColumn = (ImageButton) header.findViewById(R.id.btnEditColumn);
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTaskActivity.class);
                intent.putExtra("columnId", column.columnId);
                startActivity(intent);
            }
        });
        btnEditColumn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddColumnActivity.class);
                intent.putExtra("boardId", column.boardId);
                intent.putExtra("columnId", column.columnId);
                startActivity(intent);
            }
        });
    }


    private static class MyColumnDragItem extends DragItem {

        MyColumnDragItem(Context context, int layoutId) {
            super(context, layoutId);
            setSnapToTouch(false);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            LinearLayout clickedLayout = (LinearLayout) clickedView;
            View clickedHeader = clickedLayout.getChildAt(0);
            RecyclerView clickedRecyclerView = (RecyclerView) clickedLayout.getChildAt(1);

            View dragHeader = dragView.findViewById(R.id.drag_header);
            ScrollView dragScrollView = dragView.findViewById(R.id.drag_scroll_view);
            LinearLayout dragLayout = dragView.findViewById(R.id.drag_list);
            dragLayout.removeAllViews();

            ((TextView) dragHeader.findViewById(R.id.text)).setText(((TextView) clickedHeader.findViewById(R.id.text)).getText());
            ((TextView) dragHeader.findViewById(R.id.item_count)).setText(((TextView) clickedHeader.findViewById(R.id.item_count)).getText());
            for (int i = 0; i < clickedRecyclerView.getChildCount(); i++) {
                View view = View.inflate(dragView.getContext(), R.layout.column_item, null);
                ((TextView) view.findViewById(R.id.text)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.text)).getText());
                dragLayout.addView(view);

                if (i == 0) {
                    dragScrollView.setScrollY(-clickedRecyclerView.getChildAt(i).getTop());
                }
            }

            dragView.setPivotY(0);
            dragView.setPivotX(clickedView.getMeasuredWidth() / 2);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            super.onStartDragAnimation(dragView);
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            super.onEndDragAnimation(dragView);
            dragView.animate().scaleX(1).scaleY(1).start();
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence text = ((TextView) clickedView.findViewById(R.id.text)).getText();
            ((TextView) dragView.findViewById(R.id.text)).setText(text);
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);

            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.setForeground(clickedView.getResources().getDrawable(R.drawable.card_view_drag_foreground));
        }

        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft()
                    + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop()
                    + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation",
                    dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation",
                    dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.board_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.deleteBoard:
                deleteBoard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void deleteBoard(){
        appDB.boardDao().deleteBoard(board);
        List<Column> cols = appDB.columnDao().getColumnByBoardId(board.boardId);
        for(Column col: cols){
            List<Task> tasks = appDB.taskDao().getTasksByColumnId(col.columnId);
            for (Task task: tasks){
                appDB.taskDao().deleteTask(task);
            }
            appDB.columnDao().deleteColumn(col);
        }
        finish();
    }

}
