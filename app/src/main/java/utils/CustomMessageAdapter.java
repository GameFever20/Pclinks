package utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pclinks.tech_creation.com.pclinks.R;

/**
 * Created by bunny on 13/07/17.
 */


public class CustomMessageAdapter extends RecyclerView.Adapter<CustomMessageAdapter.MessageViewHolder> {

    private ArrayList<CustomMessage> messageArrayList;
    Context context;


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText ,messageDevice ,messageTimeStamp;
        CardView backgroundCard;



        public MessageViewHolder(View view) {
            super(view);
            messageText =(TextView)view.findViewById(R.id.customAdapter_messageText_textView);
            messageDevice =(TextView)view.findViewById(R.id.customAdapter_messageDevice_textView);
            messageTimeStamp =(TextView)view.findViewById(R.id.customAdapter_messageTime_textView);

            backgroundCard =(CardView)view.findViewById(R.id.customAdapter_cardView);


        }


    }


    public CustomMessageAdapter(ArrayList<CustomMessage> customMessageArrayList, Context context) {
        this.messageArrayList = customMessageArrayList;
        CustomMessageAdapter.this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custommessage_adapter_row_layout, parent, false);

        return new CustomMessageAdapter.MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {

        CustomMessage customMessage =messageArrayList.get(position);

        holder.messageText.setText(customMessage.getCustomMessageText());
        holder.messageDevice.setText(customMessage.getCustomMessageDevice());

        holder.messageTimeStamp.setText(customMessage.resolveTimeStamp());

        if (customMessage.getMessageType() == 0){
            holder.backgroundCard.setCardBackgroundColor(Color.WHITE);

        }else if(customMessage.getMessageType() == 101) {
            holder.backgroundCard.setCardBackgroundColor(Color.GREEN);
        }else{
            holder.backgroundCard.setCardBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }
}
