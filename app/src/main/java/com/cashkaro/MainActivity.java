package com.cashkaro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cashkaro.utils.CustomTypefaceSpan;
import com.cashkaro.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int[] sampleImages = {R.drawable.slide_1, R.drawable.slide_2, R.drawable.slide_3, R.drawable.slide_4, R.drawable.slide_5};
    TwitterAuthClient mTwitterAuthClient;
    private CarouselView mCarouselView;
    private String TAG = "Login";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AppCompatButton mLoginButton;
    private TextView mStatusTextView;
    private TextView mDetailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                "mTGfzAUGWKLonCdXWMZwt1But",
                "b60vylv2XyJQru4TPbcuOK2BJnjjjIfhU7MybS6x9aLQXlGonc");
        Fabric.with(this, new Twitter(authConfig));

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_main);

        Utils.setFontAllView((ViewGroup) findViewById(R.id.drawer_layout));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar s = Snackbar.make(view, "Loading awesome deals for you!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                TextView st = (TextView) s.getView().findViewById(android.support.design.R.id.snackbar_text);
                st.setTypeface(Utils.regularFont);
                s.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Views
        mStatusTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.status);
        mDetailTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.detail);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START initialize_twitter_login]
        mLoginButton = (AppCompatButton) navigationView.getHeaderView(0).findViewById(R.id.button_twitter_login);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTwitterAuthClient = new TwitterAuthClient();
                mTwitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>() {
                    @Override
                    public void success(final Result<TwitterSession> result) {
                        Log.d(TAG, "twitterLogin:success" + result);
                        handleTwitterSession(result.data);
                    }

                    @Override
                    public void failure(final TwitterException e) {
                        Log.w(TAG, "twitterLogin:failure", e);
                        updateUI(null);
                    }
                });
            }
        });
        // [END initialize_twitter_login]

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleImages[position]);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        };

        mCarouselView = (CarouselView) findViewById(R.id.carouselView);
        mCarouselView.setPageCount(sampleImages.length);
        mCarouselView.setImageListener(imageListener);

        Utils.setFontAllView((ViewGroup) navigationView.getHeaderView(0));
        setFonts(navigationView.getMenu());
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the Twitter login button.
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }
    // [END on_activity_result]

    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_twitter]

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Log.d(TAG, user.getDisplayName());
            Log.d(TAG, user.getEmail());
            Log.d(TAG, String.valueOf(user.getPhotoUrl()));
            mStatusTextView.setText(user.getDisplayName());
            mDetailTextView.setText(user.getEmail());

            mLoginButton.setVisibility(View.GONE);
            mStatusTextView.setVisibility(View.VISIBLE);
            mDetailTextView.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setVisibility(View.GONE);
            mDetailTextView.setVisibility(View.GONE);

            mLoginButton.setVisibility(View.VISIBLE);
        }
    }

    private void setFonts(final Menu menu) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Utils.setFontAllView((ViewGroup) findViewById(R.id.drawer_layout));
                for (int i = 0; i < menu.size(); i++) {
                    MenuItem mi = menu.getItem(i);
                    //for applying a font to subMenu
                    SubMenu subMenu = mi.getSubMenu();
                    if (subMenu != null && subMenu.size() > 0) {
                        for (int j = 0; j < subMenu.size(); j++) {
                            MenuItem subMenuItem = subMenu.getItem(j);
                            applyFontToMenuItem(subMenuItem);
                        }
                    }
                    applyFontToMenuItem(mi);
                }
            }
        });
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Montserrat-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        setFonts(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            mAuth.signOut();
            Twitter.logOut();
            updateUI(null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
