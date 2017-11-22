package edu.sjsu.team408.parkhere;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by robg on 11/21/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> listOfChats;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chatWithLabel;
        public TextView lastMessageLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            chatWithLabel = itemView.findViewById(R.id.chatWithLabel);
            lastMessageLabel = itemView.findViewById(R.id.lastMessageLabel);
        }
    }

    public ChatAdapter(List<Chat> listOfChats) {
        this.listOfChats = listOfChats;
    }

    @Override
    public int getItemCount() {
        return listOfChats.size();
    }

    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.chatWithLabel.setText(listOfChats.get(position).getChatWithName());
        holder.lastMessageLabel.setText(listOfChats.get(position).getLastMessage());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
