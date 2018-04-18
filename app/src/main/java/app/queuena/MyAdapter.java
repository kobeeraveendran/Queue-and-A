package app.queuena;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<String> messageData;
    private ArrayList<String> timeData;

    public static final int VIEW_TYPE_MESSAGE_SENT = 1;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ArrayList<CustomMessage> msgList;

    public MyAdapter(Context ctx, ArrayList<CustomMessage> messageList) {
        mContext = ctx;
        msgList = messageList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView timeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.tvSelfMessageBody);
            timeTextView = itemView.findViewById(R.id.text_message_time);
        }

        void bind(CustomMessage message) {
            messageTextView.setText(message.getMessage());
            String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
            timeTextView.setText(currentDateTime);
        }
    }

    @Override
    public int getItemViewType(int position) {
                /*
        if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
        */
        return VIEW_TYPE_MESSAGE_SENT;
    }


    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if(viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_self, parent, false);
            return new ViewHolder(view);
        }


        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
        CustomMessage message = msgList.get(position);
        ((ViewHolder) holder).bind(message);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
