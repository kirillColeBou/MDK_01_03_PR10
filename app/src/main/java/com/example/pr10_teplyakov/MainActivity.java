package com.example.pr10_teplyakov;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.os.NetworkOnMainThreadException;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.View;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public class Info {
        @SerializedName("Name")
        public String Name;
        @SerializedName("Value")
        public String Value;
        @SerializedName("CharCode")
        public String CharCode;
    }

    public class Value {
        @SerializedName("Valute")
        public Map<String, Info> Valute;

    }

    Map<String, Info> info = new HashMap<String, Info>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner spinnerFrom = findViewById(R.id.spinner);
        Spinner spinnerTo = findViewById(R.id.spinner2);

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        LoadInfoValue();
    }

    public void AlertDialogs(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) { dialog.cancel(); }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void LoadInfoValue() {
        new AsyncTask<Void, Void, Value>() {
            @Override
            protected Value doInBackground(Void... voids) {
                try {
                    Document doc_b = (Document) Jsoup.connect("https://www.cbr-xml-daily.ru/daily_json.js").ignoreContentType(true).get();
                    return new Gson().fromJson(doc_b.text(), Value.class);
                } catch (IOException e) {
                    runOnUiThread(() -> AlertDialogs("Уведомление", "Ошибка."));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Value value) {
                if (value != null && value.Valute != null) {
                    for (Info item : value.Valute.values()) {
                        info.put(item.Name, item);
                    }
                    LoadItem();
                } else {
                    AlertDialogs("Уведомление", "Ошибка!");
                }
            }
        }.execute();
    }

    public void LoadItem(){
        Spinner spinnerFrom = findViewById(R.id.spinner);
        Spinner spinnerTo = findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (String item : info.keySet()) {
            adapter1.add(item);
        }
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTo.setAdapter(adapter1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (String item : info.keySet()) {
            adapter2.add(item);
        }
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter2);
    }

    public void Consider(View view){
        Spinner spinnerFrom = findViewById(R.id.spinner);
        Spinner spinnerTo = findViewById(R.id.spinner2);
        Info infoTo =  info.get(spinnerTo.getSelectedItem());
        Info infoFrom =  info.get(spinnerFrom.getSelectedItem());
        EditText count = findViewById(R.id.editText2);
        TextView tv = findViewById(R.id.textView4);

        if(infoTo != null){
            if(infoFrom != null){
                if(count.getText().length() > 0){
                    float f_infoTo= Float.parseFloat(String.valueOf(infoTo.Value));
                    float f_infoFrom = Float.parseFloat(String.valueOf(infoFrom.Value));
                    float f_count = Float.parseFloat(String.valueOf(count.getText()));
                    float composition = 0;

                    composition = f_infoTo / f_infoFrom * f_count;
                    tv.setText(Float.parseFloat(String.format("%.2f", composition)) + " " + infoFrom.CharCode);
                } else AlertDialogs("Уведомление", "Введите кол-во денежных единиц.");
            } else AlertDialogs("Уведомление", "Выберите валюту, из которой сделать перевод.");
        } else AlertDialogs("Уведомление", "Выберите валюту, в которую сделать перевод.");
    }

    public void onHowCourse(View view){
        Spinner spinnerFrom = findViewById(R.id.spinner);
        Spinner spinnerTo = findViewById(R.id.spinner2);
        Info infoTo =  info.get(spinnerTo.getSelectedItem());
        Info infoFrom =  info.get(spinnerFrom.getSelectedItem());
        if(infoTo != null){
            if(infoFrom != null) {
                AlertDialogs("Курс валют", infoFrom.Name + " курс: " + infoFrom.Value + " р.\n" + infoTo.Name + " курс: " + infoTo.Value + " р.");
            } else AlertDialogs("Уведомление", "Выберите валюту, из которой сделать перевод.");
        } else AlertDialogs("Уведомление", "Выберите валюту, в которую сделать перевод.");
    }
}