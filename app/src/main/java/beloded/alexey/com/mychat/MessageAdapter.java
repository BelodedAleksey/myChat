package beloded.alexey.com.mychat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseReference;

    public MessageAdapter(List<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout_of_users, parent, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String message_sender_id = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);
        String fromUserId = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(fromUserId);
        UsersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("user_name").getValue().toString();
                String userImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                Picasso.get().load(userImage).placeholder(R.drawable.default_profile)
                        .into(holder.userProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")) {
            holder.messagePicture.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(message_sender_id)) {
                holder.messageText.setBackgroundResource(R.drawable.message_text_background_two);
                holder.messageText.setTextColor(Color.BLACK);
                holder.messageText.setGravity(Gravity.RIGHT);
            } else {
                holder.messageText.setBackgroundResource(R.drawable.message_text_background);
                holder.messageText.setTextColor(Color.WHITE);
                holder.messageText.setGravity(Gravity.LEFT);
            }

            holder.messageText.setText(messages.getMessage());
        } else {
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageText.setPadding(0, 0, 0, 0);

            Picasso.get().load(messages.getMessage())
                    .placeholder(R.drawable.default_profile).into(holder.messagePicture);
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView userProfileImage;
        public ImageView messagePicture;

        public MessageViewHolder(View view) {
            super(view);

            messageText = view.findViewById(R.id.message_text);
            messagePicture = view.findViewById(R.id.message_image_view);
            userProfileImage = view.findViewById(R.id.messages_profile_image);
        }
    }
}
