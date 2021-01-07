package com.example.urfu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CategoryActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    ImageButton btnBack;
    SharedPreferences settings;
    int position;
    HashMap<Integer, Point> hashMapPoints = new HashMap<>();
    ImageButton settingsButton;
    Button campusButton;
    Button attractionsButton;
    Button searchButton;
    boolean isSearchAudience;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int currentRadioButtonId = 0;
    String currentLanguage;
    String searchQuery = "";
    TextView classroomHint;
    ArrayList<String> lettersOfCampus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argument = getIntent().getExtras();

        if (argument != null) {
            position = argument.getInt("pos");
        }
        loadSettings();


        // Disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.category_activity);


        campusButton = findViewById(R.id.campus);
        attractionsButton = findViewById(R.id.attractions);
        searchButton = findViewById(R.id.search_classroom);

        searchView = findViewById(R.id.searchView);
        settingsButton = findViewById(R.id.settingsButton);

        radioGroup = findViewById(R.id.radio_group);
        classroomHint = findViewById(R.id.classRoomHint);


        if (position != 0) {
            radioGroup.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            classroomHint.setVisibility(View.GONE);
            LinearLayout.LayoutParams parameter = (LinearLayout.LayoutParams) searchView.getLayoutParams();
            parameter.setMargins(parameter.leftMargin, 175, parameter.rightMargin, parameter.bottomMargin);
            searchView.setLayoutParams(parameter);

            changeLanguageSportAndDormCategory(position);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = radioGroup.findViewById(checkedId);
                int index = radioGroup.indexOfChild(radioButton);
                currentRadioButtonId = index;
                Log.e("index", String.valueOf(index));
                Log.e("id", String.valueOf(radioButton.getId()));

                switchCategory(index);
            }
        });


        try {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    searchQuery = newText;
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e("Search error", e.getMessage());
        }

        btnBack = findViewById(R.id.ButtonBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedClassroom = getCodeFromClassroom(searchQuery);
                Log.e("currentLanguage", currentLanguage);

                if (lettersOfCampus.contains(typedClassroom.toLowerCase())) {
                    for (int i = 0; i < hashMapPoints.size(); i++) {
                        Point point = hashMapPoints.get(i);
                        if (point.getClassroom().toLowerCase().equals(typedClassroom.toLowerCase())) {
                            new MapActivityHandler(i).run();
                            break;
                        }
                    }
                } else if (typedClassroom.isEmpty()) {
                    switch (Integer.parseInt(currentLanguage)) {
                        case 0:
                            Toast.makeText(CategoryActivity.this, R.string.ToastEmptyStringCh, Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(CategoryActivity.this, R.string.ToastEmptyStringEng, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    switch (Integer.parseInt(currentLanguage)) {
                        case 0:
                            Toast.makeText(CategoryActivity.this, R.string.ToastNotFoundStringCh, Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(CategoryActivity.this, R.string.ToastNotFoundStringEng, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private String getCodeFromClassroom(String classroom) {
        int startIndex = 0;
        for (int i = 0; i < classroom.length(); i++) {
            Log.e("Index= ", String.valueOf(i));
            if (tryParseInt(String.valueOf(classroom.charAt(i)))) {
                startIndex = i;
                break;
            }
        }
        return startIndex > 0 ? classroom.substring(0, startIndex) : classroom;
    }

    boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void switchCategory(int value) {
        switch (value) {
            // Первый radio-button (корпуса)
            case 0:
                listView.setVisibility(ListView.VISIBLE);
                searchButton.setVisibility(View.GONE);
                classroomHint.setVisibility(View.GONE);
                if (currentLanguage.equals(Language.Chinese.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintChCampus));
                } else if (currentLanguage.equals(Language.English.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintEngCampus));
                }
                getPointsFromHost();
                break;
            // Второй radio-button (аудитории)
            case 1:
                listView.setVisibility(ListView.GONE);
                searchButton.setVisibility(View.VISIBLE);
                classroomHint.setVisibility(View.VISIBLE);
                if (currentLanguage.equals(Language.Chinese.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintCh2Campus));
                } else if (currentLanguage.equals(Language.English.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintEng2Campus));
                }
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        try {

            Log.e("In try catch", "Yes");
            switch (currentRadioButtonId) {
                // Первый radio-button (корпуса)
                case 0:
                    getPointsFromHost();
                    break;
                // Второй radio-button (аудитории)
                case 1:
                    //createAuditoriumAdapter();
                    break;
            }

        } catch (Exception e) {
            Log.e("Resume category error", e.getMessage());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putInt("RadioButton", currentRadioButtonId);
        prefEditor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Category Activ. Resume", "Method has resumed");
        currentLanguage = settings.getString("Language", "N/A");
        changeLanguageCampusButton(currentLanguage);
        changeLanguageAttractionsButton(currentLanguage);
        reloadLanguageForRadioButtonsAndSearchView();

        if (listView != null) {

            disableProgressBar();

            if (currentRadioButtonId == 0) {
                listView.setVisibility(ListView.VISIBLE);
            }

        }
    }


    private void reloadLanguageForRadioButtonsAndSearchView() {

        Log.e("current radio b", String.valueOf(currentRadioButtonId));
        if (currentLanguage.equals(Language.Chinese.getId())) {
            RadioButton radioButton1 = findViewById(R.id.find_education_buildings);
            radioButton1.setText(R.string.queryHintChCampus);
            RadioButton radioButton2 = findViewById(R.id.find_education_audience);
            radioButton2.setText(R.string.queryHintCh2Campus);

        } else if (currentLanguage.equals(Language.English.getId())) {
            RadioButton radioButton1 = findViewById(R.id.find_education_buildings);
            radioButton1.setText(R.string.queryHintEngCampus);
            RadioButton radioButton2 = findViewById(R.id.find_education_audience);
            radioButton2.setText(R.string.queryHintEng2Campus);

        }

        if (position == 0) {
            switch (currentRadioButtonId) {
                default:
                    if (currentLanguage.equals(Language.Chinese.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintChCampus));
                    } else if (currentLanguage.equals(Language.English.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintEngCampus));
                    }
                    break;
                case 1:
                    if (currentLanguage.equals(Language.Chinese.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintCh2Campus));
                    } else if (currentLanguage.equals(Language.English.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintEng2Campus));
                    }
                    break;
            }
        } else {
            changeLanguageSportAndDormCategory(position);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putInt("RadioButton", currentRadioButtonId);
        prefEditor.apply();

        Log.e("Category Activ. Pause", "Method has paused");
    }

    public void getPointsFromHost() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        final String baseHostApiUrl = "http://roadtourfu.ai-info.ru/api";

        // Конечный ресурс, где идёт обработка логина и пароля
        String url = baseHostApiUrl + "/data/get_points.php";
        String readLanguageSetting = settings.getString("Language", "N/A");

        FormBody formBody = new FormBody.Builder()
                .add("category", String.valueOf(position))
                .add("lang", readLanguageSetting)
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            // Обработка полученного ответа от сервера.
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    final String myResponse = response.body().string();

                    try {
                        // Объявляется экземпляр класса JSONObject, где аргумент -
                        // это полученная строка от сервера.
                        Log.e("Response", myResponse);

                        JSONArray jsonArray = new JSONArray(myResponse);

                        // Обязательно запускать через этот поток, иначе будет ошибка изменения элементов вне потока
                        // Формируется Categories из Json
                        CategoryActivity.this.runOnUiThread(() -> buildPointsByJson(jsonArray));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void buildPointsByJson(JSONArray jsonArray) {
        /*
            point_id
            point_name
            point_alt_name
            point_latitude
            point_longitude
            point_image
            point_description
            point_alt_description
        */


        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                // Помещение точек в список.
                assert object != null;

                int category_id = object.getInt("point_id");//getIdFromString(object);

                String name = object.getString("point_name");

                String alt_name = object.getString("point_alt_name");

                double latitude = object.getDouble("point_latitude");

                double longitude = object.getDouble("point_longitude");

                String classroom = object.getString("point_classroom");

                String description = object.getString("point_description");

                String alt_description = object.getString("point_alt_description");

                String address = object.getString("point_address");

                String contacts = object.getString("point_contacts");

                String site = object.getString("point_site");

                hashMapPoints.put(i, new Point(category_id, name, alt_name, latitude, longitude, classroom, description, alt_description, address, contacts, site));
                if (classroom != null & !classroom.isEmpty()) {
                    lettersOfCampus.add(classroom);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("array list", String.valueOf(lettersOfCampus));

        String[] local_points = new String[hashMapPoints.size()];

        for (int i = 0; i < hashMapPoints.size(); i++) {
            String name = Objects.requireNonNull(hashMapPoints.get(i)).getName();

            String alt_name = Objects.requireNonNull(hashMapPoints.get(i)).getAltName();

            String full_name = alt_name + " " + "\n" + " " + name;

            local_points[i] = full_name;
        }

        setupAdapterAndListview(local_points);
    }

    private int getIdFromString(JSONObject object) throws JSONException {
        return Integer.parseInt(object.getString("point_id"));
    }

    private void setupAdapterAndListview(String[] points) {
        try {
            disableProgressBar();

            searchView = findViewById(R.id.searchView);

            listView = findViewById(R.id.myList);

            adapter = new ArrayAdapter<>(this,
                    R.layout.array_adapter_custom_layout, points);

            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {

                new MapActivityHandler(position).run();

                disableProgressBar();

            });
        } catch (Exception e) {
        }
    }

    private void disableProgressBar() {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.GONE);
    }

    private void enableProgressBar() {
        ProgressBar progress = findViewById(R.id.progressbar);
        progress.setVisibility(ProgressBar.VISIBLE);
    }

    public void showLanguageWindow(View v) {
        PopupMenu languageMenu = new PopupMenu(this, v);

        languageMenu.setOnMenuItemClickListener(this);
        languageMenu.inflate(R.menu.language_menu);
        languageMenu.show();
    }

    @SuppressLint({"ShowToast", "NonConstantResourceId"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chinese_lang:
                Toast.makeText(this, "选择中文", Toast.LENGTH_SHORT).show();
                changeLanguage(Language.Chinese.getId());
                return true;

            case R.id.english_lang:
                Toast.makeText(this, "English language selected", Toast.LENGTH_SHORT).show();
                changeLanguage(Language.English.getId());
                return true;

            default:
                return false;
        }
    }

    private void changeLanguage(String idLanguage) {
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Language", idLanguage);
        prefEditor.apply();
        currentLanguage = idLanguage;

        changeLanguageCampusButton(idLanguage);
        changeLanguageAttractionsButton(idLanguage);

        if (idLanguage.equals(Language.Chinese.getId())) {
            RadioButton radioButton1 = findViewById(R.id.find_education_buildings);
            radioButton1.setText(R.string.queryHintChCampus);
            RadioButton radioButton2 = findViewById(R.id.find_education_audience);
            radioButton2.setText(R.string.queryHintCh2Campus);
        } else if (idLanguage.equals(Language.English.getId())) {
            RadioButton radioButton1 = findViewById(R.id.find_education_buildings);
            radioButton1.setText(R.string.queryHintEngCampus);
            RadioButton radioButton2 = findViewById(R.id.find_education_audience);
            radioButton2.setText(R.string.queryHintEng2Campus);
        }

        if (position == 0) {
            switch (currentRadioButtonId) {
                default:
                    if (idLanguage.equals(Language.Chinese.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintChCampus));
                    } else if (idLanguage.equals(Language.English.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintEngCampus));
                    }
                    break;
                case 1:
                    if (idLanguage.equals(Language.Chinese.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintCh2Campus));
                    } else if (idLanguage.equals(Language.English.getId())) {
                        searchView.setQueryHint(getString(R.string.queryHintEng2Campus));
                    }
                    break;
            }
        } else {
            changeLanguageSportAndDormCategory(position);
        }

        try {

            Log.e("In try catch", "Yes");
            switch (currentRadioButtonId) {
                // Первый radio-button (корпуса)
                case 0:
                    getPointsFromHost();
                    break;
                // Второй radio-button (аудитории)
                case 1:
                    break;
            }

        } catch (Exception e) {
            Log.e("Resume category error", e.getMessage());
        }

    }

    private void changeLanguageSportAndDormCategory(int pos) {
        switch (pos) {
            case 1:
                if (currentLanguage.equals(Language.Chinese.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintChSportComplex));
                } else if (currentLanguage.equals(Language.English.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintEngSportComplex));
                }
                break;
            case 2:
                if (currentLanguage.equals(Language.Chinese.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintChDorm));
                } else if (currentLanguage.equals(Language.English.getId())) {
                    searchView.setQueryHint(getString(R.string.queryHintEngDorm));
                }
                break;
        }
    }

    private void changeLanguageCampusButton(String idLanguage) {

        if (idLanguage.equals(Language.Chinese.getId())) {
            campusButton.setText(R.string.campus_ch);
            campusButton.setTextSize(14);
        } else if (idLanguage.equals(Language.English.getId())) {
            campusButton.setText(R.string.campus_eng);
            campusButton.setTextSize(13);
        }
    }

    private void changeLanguageAttractionsButton(String idLanguage) {

        if (idLanguage.equals(Language.Chinese.getId())) {
            attractionsButton.setText(R.string.attractions_ch);
        } else if (idLanguage.equals(Language.English.getId())) {
            attractionsButton.setText(R.string.attractions_eng);
        }
    }

    private void loadSettings() {
        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        isSearchAudience = settings.getBoolean("StateButtonCategorySearch", false);
        currentLanguage = settings.getString("Language", "N/A");
    }


    class MapActivityHandler implements Runnable {
        private final int position;

        public MapActivityHandler(int position) {
            this.position = position;
        }

        @Override
        public void run() {

            Handler mapActivityHandler = new Handler();
            mapActivityHandler.post(new Runnable() {
                @Override
                public void run() {

                    listView.setVisibility(ListView.GONE);

                    enableProgressBar();

                    Intent intent = new Intent(CategoryActivity.this, MapActivity.class);

                    Log.e("position", String.valueOf(position));

                    Log.e("hashMapPoints", String.valueOf(hashMapPoints.size()));

                    Log.e("hashMapPoints getname", hashMapPoints.get(position).getName());

                    Log.e("hash getaltname", hashMapPoints.get(position).getAltName());

                    intent.putExtra("point", hashMapPoints.get(position));

                    startActivity(intent);
                }

            });
        }
    }
}