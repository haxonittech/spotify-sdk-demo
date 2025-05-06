package com.example.spotifynormalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Import the Spotify SDK classes
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

/**
 * A normal Spotify integration app that follows standard implementation practices.
 * This app is vulnerable only because of the Spotify SDK's inherent vulnerabilities,
 * not because of any mistakes in our implementation.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyNormalApp";
    
    // Using the client ID from the user's Spotify API credentials
    private static final String CLIENT_ID = "d10d78b2f32d4e4cab9b42019de911a6";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "spotify-sdk://auth";

    private TextView statusTextView;
    private TextView nowPlayingTextView;
    private Button loginButton;
    private Button logoutButton;
    private LinearLayout playbackControls;
    private Button playPauseButton;
    
    private String accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        statusTextView = findViewById(R.id.status_text);
        nowPlayingTextView = findViewById(R.id.now_playing_text);
        loginButton = findViewById(R.id.login_button);
        logoutButton = findViewById(R.id.logout_button);
        playbackControls = findViewById(R.id.playback_controls);
        playPauseButton = findViewById(R.id.play_pause_button);
        Button previousButton = findViewById(R.id.previous_button);
        Button nextButton = findViewById(R.id.next_button);

        // Set up click listeners
        loginButton.setOnClickListener(v -> authenticateWithSpotify());
        logoutButton.setOnClickListener(v -> logout());
        playPauseButton.setOnClickListener(v -> togglePlayPause());
        previousButton.setOnClickListener(v -> previousTrack());
        nextButton.setOnClickListener(v -> nextTrack());
    }

    /**
     * Initiates the Spotify authentication process using the Spotify SDK.
     * Note: This is using the SDK as intended, but the SDK itself has vulnerabilities.
     */
    private void authenticateWithSpotify() {
        Log.d(TAG, "Authenticating with Spotify...");
        Log.d(TAG, "Using CLIENT_ID: " + CLIENT_ID);
        Log.d(TAG, "Using REDIRECT_URI: " + REDIRECT_URI);
        
        try {
            // Using the SDK's standard authentication flow
            AuthorizationRequest request = new AuthorizationRequest.Builder(
                    CLIENT_ID, 
                    AuthorizationResponse.Type.TOKEN, // Using implicit flow as per SDK docs
                    REDIRECT_URI)
                    .setScopes(new String[]{"user-read-private", "user-read-email", "streaming"})
                    .build();
            
            Log.d(TAG, "Authorization request built successfully");
            Log.d(TAG, "Request URI: " + request.toUri());
            
            // This is the standard way to use the SDK, but it's vulnerable
            Log.d(TAG, "Opening LoginActivity...");
            AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
        } catch (Exception e) {
            Log.e(TAG, "Error during authentication: " + e.getMessage(), e);
            Toast.makeText(this, "Authentication error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Handle the authentication response
        if (requestCode == REQUEST_CODE) {
            // Process the response from Spotify authentication
            if (intent != null) {
                Log.d(TAG, "Received activity result with resultCode: " + resultCode);
                
                // This is where we would normally use AuthorizationClient.getResponse
                // but we're handling the response data directly from the intent extras
                String responseType = intent.getStringExtra("response_type");
                String token = intent.getStringExtra("access_token");
                String error = intent.getStringExtra("error");
                
                if (responseType != null && responseType.equals("TOKEN")) {
                    // Successfully got token
                    accessToken = token;
                    Log.d(TAG, "Got access token: " + accessToken);
                    onAuthenticationSuccess();
                } else if (error != null) {
                    // Auth flow returned an error
                    Log.e(TAG, "Auth error: " + error);
                    statusTextView.setText(getString(R.string.login_failed, error));
                } else {
                    // Auth flow probably cancelled
                    Log.d(TAG, "Auth result: " + responseType);
                }
            }
        }
    }

    private void onAuthenticationSuccess() {
        // Update UI to show logged-in state
        statusTextView.setText(R.string.login_success);
        loginButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.VISIBLE);
        playbackControls.setVisibility(View.VISIBLE);
        
        // In a real app, we would fetch user profile and start playing music
        // For this demo, we'll just simulate it
        simulateSpotifyFunctionality();
    }
    
    private void simulateSpotifyFunctionality() {
        // This would normally use the Spotify Web API with the token
        nowPlayingTextView.setText(getString(R.string.now_playing, "Bohemian Rhapsody", "Queen"));
        nowPlayingTextView.setVisibility(View.VISIBLE);
    }

    private void logout() {
        // Clear token and reset UI
        accessToken = null;
        statusTextView.setText(R.string.not_logged_in);
        loginButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.GONE);
        playbackControls.setVisibility(View.GONE);
        nowPlayingTextView.setVisibility(View.GONE);
    }
    
    // Playback control methods (simulated)
    private void togglePlayPause() {
        if (playPauseButton.getText().equals(getString(R.string.play_button))) {
            playPauseButton.setText(R.string.pause_button);
            Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
        } else {
            playPauseButton.setText(R.string.play_button);
            Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void previousTrack() {
        Toast.makeText(this, "Previous track", Toast.LENGTH_SHORT).show();
    }
    
    private void nextTrack() {
        Toast.makeText(this, "Next track", Toast.LENGTH_SHORT).show();
    }
}
