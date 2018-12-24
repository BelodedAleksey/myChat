package beloded.alexey.com.mychat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView myFriendsList;
    private DatabaseReference FriendsReference, UsersReference;
    private FirebaseAuth mAuth;
    String online_user_id;
    private View myMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendsList = myMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        FriendsReference = FirebaseDatabase.getInstance().getReference().child("Friends")
                .child(online_user_id);
        FriendsReference.keepSynced(true);
        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersReference.keepSynced(true);
        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(FriendsReference, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                        holder.setDate(model.getDate());

                        final String list_user_id = getRef(position).getKey();

                        UsersReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("user_name").getValue().toString();
                                String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();

                                if (dataSnapshot.hasChild("online")) {
                                    String online_status = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(online_status);
                                }

                                holder.setUserName(userName);
                                holder.setThumbImage(thumbImage, getContext());

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence options[] = new CharSequence[]{
                                                userName + "'s Profile",
                                                "Send Message"
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");

                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int position) {
                                                if (position == 0) {
                                                    Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                                    profileIntent.putExtra("visit_user_id", list_user_id);
                                                    startActivity(profileIntent);
                                                }

                                                if (position == 1) {
                                                    if (dataSnapshot.child("online").exists()) {
                                                        Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                        chatIntent.putExtra("visit_user_id", list_user_id);
                                                        chatIntent.putExtra("user_name", userName);
                                                        startActivity(chatIntent);
                                                    } else {
                                                        UsersReference.child(list_user_id).child("online")
                                                                .setValue(ServerValue.TIMESTAMP).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                                chatIntent.putExtra("visit_user_id", list_user_id);
                                                                chatIntent.putExtra("user_name", userName);
                                                                startActivity(chatIntent);
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_users_display_layout, parent, false);
                        return new FriendsViewHolder(view);
                    }
                };

        myFriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image = mView.findViewById(R.id.all_users_profile_image);

            //не так как на видео
            Picasso.get().load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(thumbImage).placeholder(R.drawable.default_profile).into(thumb_image);
                        }
                    });
        }

        public void setDate(String date) {
            TextView sinceFriendsDate = mView.findViewById(R.id.all_users_status);
            sinceFriendsDate.setText("Friends Since: \n" + date);
        }

        public void setUserName(String userName) {
            TextView userNameDisplay = mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public void setUserOnline(String online_status) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);
            if (online_status.equals("true")) {
                onlineStatusView.setVisibility(View.VISIBLE);
            } else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
