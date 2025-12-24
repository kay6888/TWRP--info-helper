package com.pasta.twrp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RecoveryFinderActivity extends AppCompatActivity {

    private Spinner recoveryTypeSpinner;
    private Button searchButton;
    private ProgressBar progressBar;
    private LinearLayout resultsLayout;
    private ScrollView resultsScrollView;
    private TextView statusTextView;
    
    private String deviceCodename = "";
    private String deviceManufacturer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_finder);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Find Custom Recovery");
        }

        // Get device info from intent
        deviceCodename = getIntent().getStringExtra("codename");
        deviceManufacturer = getIntent().getStringExtra("manufacturer");

        initializeViews();
        setupRecoveryTypeSpinner();
        setupSearchButton();
        
        // Auto-fill device info
        TextView deviceInfoText = findViewById(R.id.deviceInfoText);
        deviceInfoText.setText(String.format("Device: %s %s", 
            deviceManufacturer != null ? deviceManufacturer : "Unknown",
            deviceCodename != null ? deviceCodename : "Unknown"));
    }

    private void initializeViews() {
        recoveryTypeSpinner = findViewById(R.id.recoveryTypeSpinner);
        searchButton = findViewById(R.id.searchButton);
        progressBar = findViewById(R.id.progressBar);
        resultsLayout = findViewById(R.id.resultsLayout);
        resultsScrollView = findViewById(R.id.resultsScrollView);
        statusTextView = findViewById(R.id.statusTextView);
    }

    private void setupRecoveryTypeSpinner() {
        String[] recoveryTypes = {"All Recoveries", "TWRP", "OrangeFox", "PBRP"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, recoveryTypes);
        recoveryTypeSpinner.setAdapter(adapter);
    }

    private void setupSearchButton() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRecoveries();
            }
        });
    }

    private void searchRecoveries() {
        if (deviceCodename == null || deviceCodename.isEmpty()) {
            Toast.makeText(this, "Device codename not available. Please collect device info first.", 
                          Toast.LENGTH_LONG).show();
            return;
        }

        String recoveryType = recoveryTypeSpinner.getSelectedItem().toString();
        new SearchRecoveriesTask().execute(recoveryType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SearchRecoveriesTask extends AsyncTask<String, Void, List<RecoveryResult>> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            resultsLayout.removeAllViews();
            resultsScrollView.setVisibility(View.GONE);
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText("Searching for recoveries...");
            searchButton.setEnabled(false);
        }

        @Override
        protected List<RecoveryResult> doInBackground(String... params) {
            String recoveryType = params[0];
            List<RecoveryResult> results = new ArrayList<>();

            try {
                // Search GitHub
                results.addAll(searchGitHub(recoveryType));
                
                // Search SourceForge
                results.addAll(searchSourceForge(recoveryType));
                
            } catch (Exception e) {
                e.printStackTrace();
            }

            return results;
        }

        @Override
        protected void onPostExecute(List<RecoveryResult> results) {
            progressBar.setVisibility(View.GONE);
            searchButton.setEnabled(true);
            statusTextView.setVisibility(View.GONE);

            if (results.isEmpty()) {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText("No recoveries found for " + deviceCodename + 
                                     "\n\nTry:\n• Checking device codename\n• Building custom recovery yourself\n• Asking on XDA Developers");
            } else {
                resultsScrollView.setVisibility(View.VISIBLE);
                displayResults(results);
                Toast.makeText(RecoveryFinderActivity.this, 
                             "Found " + results.size() + " results!", Toast.LENGTH_SHORT).show();
            }
        }

        private List<RecoveryResult> searchGitHub(String recoveryType) {
            List<RecoveryResult> results = new ArrayList<>();
            
            try {
                String searchQuery = "";
                if (recoveryType.equals("All Recoveries")) {
                    searchQuery = deviceCodename + " recovery";
                } else {
                    searchQuery = recoveryType + " " + deviceCodename;
                }

                String urlString = "https://api.github.com/search/repositories?q=" + 
                                 URLEncoder.encode(searchQuery, "UTF-8") + "&per_page=10";
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray items = jsonResponse.getJSONArray("items");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject repo = items.getJSONObject(i);
                        
                        RecoveryResult result = new RecoveryResult();
                        result.name = repo.getString("name");
                        result.description = repo.optString("description", "No description");
                        result.url = repo.getString("html_url");
                        result.source = "GitHub";
                        result.stars = repo.optInt("stargazers_count", 0);
                        result.lastUpdated = repo.optString("updated_at", "Unknown");
                        
                        // Detect recovery type
                        String fullText = (result.name + " " + result.description).toLowerCase();
                        if (fullText.contains("twrp") || fullText.contains("team win")) {
                            result.type = "TWRP";
                        } else if (fullText.contains("orangefox") || fullText.contains("ofox")) {
                            result.type = "OrangeFox";
                        } else if (fullText.contains("pbrp") || fullText.contains("pitchblack")) {
                            result.type = "PBRP";
                        } else {
                            result.type = "Unknown";
                        }
                        
                        results.add(result);
                    }
                }
                conn.disconnect();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return results;
        }

        private List<RecoveryResult> searchSourceForge(String recoveryType) {
            List<RecoveryResult> results = new ArrayList<>();
            
            try {
                String searchQuery = "";
                if (recoveryType.equals("All Recoveries")) {
                    searchQuery = deviceCodename + " recovery";
                } else {
                    searchQuery = recoveryType + " " + deviceCodename;
                }

                String urlString = "https://sourceforge.net/rest/search/projects/?q=" + 
                                 URLEncoder.encode(searchQuery, "UTF-8") + "&limit=5";
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray projects = jsonResponse.optJSONArray("projects");
                    
                    if (projects != null) {
                        for (int i = 0; i < projects.length(); i++) {
                            JSONObject project = projects.getJSONObject(i);
                            
                            RecoveryResult result = new RecoveryResult();
                            result.name = project.getString("name");
                            result.description = project.optString("summary", "No description");
                            result.url = project.getString("url");
                            result.source = "SourceForge";
                            result.type = "Unknown";
                            
                            // Detect recovery type
                            String fullText = (result.name + " " + result.description).toLowerCase();
                            if (fullText.contains("twrp")) {
                                result.type = "TWRP";
                            } else if (fullText.contains("orangefox")) {
                                result.type = "OrangeFox";
                            } else if (fullText.contains("pbrp")) {
                                result.type = "PBRP";
                            }
                            
                            results.add(result);
                        }
                    }
                }
                conn.disconnect();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return results;
        }
    }

    private void displayResults(List<RecoveryResult> results) {
        for (RecoveryResult result : results) {
            View resultCard = getLayoutInflater().inflate(R.layout.item_recovery_result, resultsLayout, false);
            
            TextView nameText = resultCard.findViewById(R.id.recoveryName);
            TextView typeText = resultCard.findViewById(R.id.recoveryType);
            TextView sourceText = resultCard.findViewById(R.id.recoverySource);
            TextView descText = resultCard.findViewById(R.id.recoveryDescription);
            TextView starsText = resultCard.findViewById(R.id.recoveryStars);
            Button openButton = resultCard.findViewById(R.id.openButton);
            
            nameText.setText(result.name);
            typeText.setText(result.type);
            sourceText.setText(result.source);
            descText.setText(result.description);
            
            if (result.stars > 0) {
                starsText.setVisibility(View.VISIBLE);
                starsText.setText("⭐ " + result.stars);
            } else {
                starsText.setVisibility(View.GONE);
            }
            
            final String url = result.url;
            openButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.content.Intent browserIntent = new android.content.Intent(
                        android.content.Intent.ACTION_VIEW, 
                        android.net.Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
            
            resultsLayout.addView(resultCard);
        }
    }

    private static class RecoveryResult {
        String name;
        String type;
        String description;
        String url;
        String source;
        int stars;
        String lastUpdated;
    }
}
