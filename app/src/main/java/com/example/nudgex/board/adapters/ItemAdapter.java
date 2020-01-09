/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.nudgex.board.adapters;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nudgex.R;
import com.example.nudgex.board.entities.Task;
import com.example.nudgex.board.view.AddTaskActivity;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

public class ItemAdapter extends DragItemAdapter<Pair<Integer, Task>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private ViewGroup parent;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    public ItemAdapter(ArrayList<Pair<Integer, Task>> list, int layoutId, int grabHandleId,
                       boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Task task = mItemList.get(position).second;
        holder.mText.setText(task.name);
        holder.itemView.setTag(mItemList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), AddTaskActivity.class);
                intent.putExtra("taskId", task.taskId);
                intent.putExtra("columnId", task.columnId);
//                intent.setFlags()
                Context context = parent.getContext();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView mText;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            mText = (TextView) itemView.findViewById(R.id.text);

        }

//        @Override
//        public void onItemClicked(View view) {
//            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
//        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
