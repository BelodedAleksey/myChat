package beloded.alexey.com.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartPageActivity extends AppCompatActivity {

    private Button NeedNewAccountButton, AlreadyHaveAccountButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        NeedNewAccountButton = findViewById(R.id.need_account_button);
        AlreadyHaveAccountButton = findViewById(R.id.already_have_account_button);

        NeedNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(StartPageActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        AlreadyHaveAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(StartPageActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}
