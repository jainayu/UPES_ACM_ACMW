package org.upesacm.acmacmw.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.upesacm.acmacmw.R;
import org.upesacm.acmacmw.model.ThankYou;
import org.upesacm.acmacmw.util.FirebaseConfig;

public class ThankYouActivity extends AppCompatActivity {
    TextView heading, msg;
    Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        heading = findViewById(R.id.heading);
        msg = findViewById(R.id.msg);
        continueButton = findViewById(R.id.button_continue);
        continueButton.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConfig.THANKS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ThankYou thankYou = dataSnapshot.getValue(ThankYou.class);
                        heading.setText(thankYou.getHeading());
                        msg.setText(thankYou.getMessage());
                        continueButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThankYouActivity.this.finish();
            }
        });

    }
}
