package com.rockstar.medcab.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rockstar.medcab.R;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        TextView instructionsTextView = findViewById(R.id.instructions_text_view);
        Button closeButton = findViewById(R.id.close_button);

        // Set the instructions text
        instructionsTextView.setText("Here are the instructions for using the feature: ...");

        // Close button click listener
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // Close the activity
            }
        });
    }
}
