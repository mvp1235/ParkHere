package edu.sjsu.team408.parkhere;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robg on 11/21/17.
 */
public class AutocompleteAdapter extends ArrayAdapter<User> {
    private int resourceId;
    private ArrayList<User> users;

    public AutocompleteAdapter(Context context, int resource, ArrayList<User> users) {
        super(context, resource, users);
        this.users = users;
        this.resourceId = resource;
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resourceId, parent);
        }
        User user = users.get(position);
        if (user != null) {
            TextView userNameLabel = view.findViewById(android.R.id.text1);
            if (userNameLabel != null) {
                userNameLabel.setText(user.getName());
            }
        }


        return view;
    }
}


/*
public class AutocompleteAdapter
        extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {
    private List<User> listOfUsers;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            userNameLabel = itemView.findViewById(android.R.id.text1);
        }
    }

    public AutocompleteAdapter(List<User> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }

    @Override
    public int getItemCount() {
        return listOfUsers.size();
    }

    @Override
    public AutocompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.userNameLabel.setText(listOfUsers.get(position).getName());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
*/
