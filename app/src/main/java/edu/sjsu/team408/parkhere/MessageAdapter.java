package edu.sjsu.team408.parkhere;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;
    private String side;

    /**
     * Construct a new MessageAdapter.
     * @param context an Activity context
     * @param resource a resource number
     * @param objects a list of ChatBubble object
     */
    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource; // determined by view type
        ChatBubble ChatBubble = getItem(position);
        int viewType = getItemViewType(position);

        if (ChatBubble.getIsMyMessage()) {
            layoutResource = R.layout.right_chat_bubble;
            side = "right";
        } else {
            layoutResource = R.layout.left_chat_bubble;
            side = "left";
        }

        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        //set message content
        holder.msg.setText(ChatBubble.getMessage().getContent());

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    private class ViewHolder {
        private TextView msg;

        public ViewHolder(View v) {
            if (side.equals("right"))
                msg = v.findViewById(R.id.txt_msg);
            else if (side.equals("left"))
                msg = v.findViewById(R.id.txt_msg);
        }
    }

}
