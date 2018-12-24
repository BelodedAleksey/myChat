package beloded.alexey.com.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference UsersReference;
    private ViewPager myViewpager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String online_user_id = currentUser.getUid();
            UsersReference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(online_user_id);
        }

        //Tabs for MainActivity
        myViewpager = findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewpager.setAdapter(myTabsPagerAdapter);
        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewpager);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("myChAt");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            LogOutUser();
        } else if (currentUser != null) {
            UsersReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser != null) {
            UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void LogOutUser() {
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_button) {
            if (currentUser != null) {
                UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
            }
            mAuth.signOut();
            LogOutUser();
        }

        if (item.getItemId() == R.id.main_account_settings_button) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        if (item.getItemId() == R.id.main_all_users_button) {
            Intent allUsersIntent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUsersIntent);
        }
        return true;
    }
}
