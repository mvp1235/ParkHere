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
        public TextView timestampLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            chatWithLabel = itemView.findViewById(R.id.chatWithLabel);
            lastMessageLabel = itemView.findViewById(R.id.lastMessageLabel);
            timestampLabel = itemView.findViewById(R.id.timestampLabel);
        }
        public void bindToPost(Chat chat) {
            chatWithLabel.setText(chat.getChatWithName());
            lastMessageLabel.setText(chat.getLastMessage());
            timestampLabel.setText(chat.getTimestamp().toString());
        }
    }

    /**
     * Set listOfChats
     * @param listOfChats a list of chat object
     */
    public ChatAdapter(List<Chat> listOfChats) {
        this.listOfChats = listOfChats;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.chat_item_layout, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder a ViewHolder object
     * @param position the position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.chatWithLabel.setText(listOfChats.get(position).getChatWithName());
        holder.lastMessageLabel.setText(listOfChats.get(position).getLastMessage());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    /**
     * Return the size of the dataset (invoked by the layout manager)
     */
    public int getItemCount() {
        return listOfChats.size();
    }
}
