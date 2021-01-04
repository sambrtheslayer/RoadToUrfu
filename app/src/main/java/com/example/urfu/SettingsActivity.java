package com.example.urfu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private String[] languages = { "中文", "English" };

    private SharedPreferences settings;

    private Language loadedLanguage;
    private Language selectedLanguage;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();

        setContentView(R.layout.settings);

        Spinner spinner = findViewById(R.id.languages);

        submitButton = findViewById(R.id.submit);

        setDefaultTextButtonPre();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitChanges();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Применяем адаптер к элементу spinner.
        spinner.setAdapter(adapter);

        spinner.setSelection(Integer.valueOf(loadedLanguage.getId()));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)parent.getItemAtPosition(position);

                setNewLanguage(position);

                if(selectedLanguage != loadedLanguage) {

                    Log.e("Lang", "Changed");
                    Log.e("Lang select", selectedLanguage.toString());
                    Log.e("Lang loaded", loadedLanguage.toString());

                    String buttonText;
                    switch(selectedLanguage) {
                        default: buttonText = "保存"; break;
                        case English: buttonText = "Save"; break;
                    }
                    submitButton.setText(buttonText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setNewLanguage(int language) {
        switch(language){
            case 1: selectedLanguage = Language.English; break;
            default: selectedLanguage = Language.Chinese; break;
        }
    }

    private void submitChanges() {
        if(selectedLanguage != loadedLanguage) {

            SharedPreferences.Editor prefEditor = settings.edit();
            prefEditor.putString("Language", selectedLanguage.getId());
            prefEditor.apply();

            setDefaultTextButtonPost();
        }
    }

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);

        String readLanguageSetting = settings.getString("Language", "N/A");

        switch(readLanguageSetting)
        {
            case "1": loadedLanguage = Language.English; break;
            default: loadedLanguage = Language.Chinese; break;
        }
    }

    private void setDefaultTextButtonPre() {

        String buttonText;
        switch(loadedLanguage) {
            case English: buttonText = "Submit"; break;
            default: buttonText = "確認"; break;
        }
        submitButton.setText(buttonText);
    }

    private void setDefaultTextButtonPost() {

        String buttonText;
        switch(selectedLanguage) {
            case English: buttonText = "Submit"; break;
            default: buttonText = "確認"; break;
        }
        submitButton.setText(buttonText);
    }
}
