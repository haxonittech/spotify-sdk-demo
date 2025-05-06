package com.example.spotifynormalapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationResponse;

/**
 * This activity handles the callback from Spotify authentication.
 * It uses the standard implementation as recommended by Spotify,
 * but is vulnerable due to the SDK's design flaws.
 */
public class SpotifyCallbackActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyCallback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Handle the incoming intent
        handleIntent(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // Handle the incoming intent
        handleIntent(intent);
    }
    
    private void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            Log.d(TAG, "Received callback URI: " + uri.toString());
            
            // This is the standard way to handle the callback as per Spotify's docs
            // But the SDK doesn't verify which app is receiving this intent
            AuthorizationResponse response = AuthorizationResponse.fromUri(uri);
            
            // Forward the response back to MainActivity
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mainActivityIntent.putExtra("response_type", response.getType().toString());
            mainActivityIntent.putExtra("access_token", response.getAccessToken());
            mainActivityIntent.putExtra("error", response.getError());
            startActivity(mainActivityIntent);
            
            // Close this activity
            finish();
        } else {
            Log.e(TAG, "Received intent without URI data");
            finish();
        }
    }
}
