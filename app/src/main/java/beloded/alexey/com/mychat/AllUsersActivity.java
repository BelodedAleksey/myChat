package beloded.alexey.com.mychat;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView allUsersList;
    private DatabaseReference allDatabaseUserReference;
    private EditText SearchInputText;
    private ImageButton SearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.all_users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchButton = findViewById(R.id.search_people_button);
        SearchInputText = findViewById(R.id.search_input_text);

        allUsersList = findViewById(R.id.all_users_list);
        allUsersList.setHasFixedSize(true);
        allUsersList.setLayoutManager(new LinearLayoutManager(this));

        allDatabaseUserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUserReference.keepSynced(true);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchUserName = SearchInputText.getText().toString();
                if (TextUtils.isEmpty(searchUserName)) {
                    Toast.makeText(AllUsersActivity.this, "Please write a user name to search...", Toast.LENGTH_SHORT).show();
                }
                SearchForPeopleAndFriends(searchUserName);
            }
        });
    }

    protected void SearchForPeopleAndFriends(String searchUserName) {
        Toast.makeText(AllUsersActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
        Query searchPeopleAndFriends = allDatabaseUserReference.orderByChild("user_name")
                .startAt(searchUserName).endAt(searchUserName + "\uf8ff");

        //не так как на видео
        FirebaseRecyclerOptions<AllUsers> options = new FirebaseRecyclerOptions.Builder<AllUsers>()
                .setQuery(searchPeopleAndFriends, AllUsers.class)
                .build();

        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllUsersViewHolder holder, final int position, @NonNull AllUsers model) {

                holder.setUser_name(model.getUser_name());
                holder.setUser_status(model.getUser_status());
                holder.setUser_thumb_image(getApplicationContext(), model.getUser_thumb_image());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public AllUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout, parent, false);
                return new AllUsersViewHolder(view);
            }
        };

        allUsersList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name) {
            TextView name = mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }

        public void setUser_status(String user_status) {
            TextView status = mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }

        public void setUser_thumb_image(final Context ctx, final String user_thumb_image) {
            final CircleImageView thumb_image = mView.findViewById(R.id.all_users_profile_image);

            //не так как на видео
            Picasso.get().load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(user_thumb_image).placeholder(R.drawable.default_profile).into(thumb_image);
                        }
                    });
        }
    }
}
