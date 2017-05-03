package com.cashkaro;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cashkaro.utils.AppStatus;
import com.cashkaro.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

import butterknife.ButterKnife;

public class StoreActivity extends AppCompatActivity {

    int[] sampleImages = {R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3, R.drawable.slide_4, R.drawable.slide_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        FloatingActionButton fab = ButterKnife.findById(this, R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar s = Snackbar.make(view, "Thanks for your love!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                TextView st = (TextView) s.getView().findViewById(android.support.design.R.id.snackbar_text);
                st.setTypeface(Utils.regularFont);
                s.show();
            }
        });
        Intent i = getIntent();
        ImageView image = ButterKnife.findById(this, R.id.image);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(i.getStringExtra("NAME"));
            image.setBackground(ContextCompat.getDrawable(this, sampleImages[i.getIntExtra("POSITION", 0)]));
        }
        Utils.setFontAllView((ViewGroup) findViewById(R.id.root));
    }

    public void loadDeal(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            if (AppStatus.getInstance(this).isOnline()) {
                Random r = new Random();
                startActivity(new Intent(StoreActivity.this, WebViewActivity.class).putExtra("URL", getIntent().getStringExtra("URL")).putExtra("NAME", getIntent().getStringExtra("NAME")));
            } else
                Toast.makeText(this, "You are offline!", Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(StoreActivity.this, "Please login first!", Toast.LENGTH_LONG).show();
        }
    }
}
