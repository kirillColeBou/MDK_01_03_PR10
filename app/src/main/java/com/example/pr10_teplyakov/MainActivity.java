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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    Spinner spinnerFrom = findViewById(R.id.spinner);
    Spinner spinnerTo = findViewById(R.id.spinner2);

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

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {Consider();}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {Consider();}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        LoadInfoValue liv = new LoadInfoValue();
        liv.execute();
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

    public class LoadInfoValue extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String body;
            Document doc_b = null;
            try {
                doc_b = Jsoup.connect("https://www.cbr-xml-daily.ru/daily_json.js").get();
            } catch (IOException e) {
                AlertDialogs("Уведомление", "Ошибка в подключении.");
            }
            if (doc_b != null) {
                Value value = new Gson().fromJson(doc_b.text(), Value.class);
                for (Info item : value.Valute.values()) {
                    info.put(item.Name, item);
                }
            } else body = "Ошибка!";
            LoadItem();
            return null;
        }
    }

    public void LoadItem(){
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

    public void Consider(){
        Info infoTo =  info.get(spinnerTo.getSelectedItem());
        Info infoFrom =  info.get(spinnerFrom.getSelectedItem());
        EditText count = findViewById(R.id.editText2);
        TextView tv = findViewById(R.id.textView3);

        if(infoTo != null){
            if(infoFrom != null){
                if(count.getText().length() > 0){
                    float f_infoTo= Float.parseFloat(String.valueOf(infoTo.Value));
                    float f_infoFrom = Float.parseFloat(String.valueOf(infoFrom.Value));
                    float f_count = Float.parseFloat(String.valueOf(count.getText()));
                    float composition = 0;

                    composition = f_infoTo / f_infoFrom * f_count;
                    tv.setText(composition + " " + infoTo.CharCode);
                }
                else AlertDialogs("Уведомление", "Введите кол-во денежных единиц.");
            } else AlertDialogs("Уведомление", "Выберите валюту, из которой сделать перевод.");
        } else AlertDialogs("Уведомление", "Выберите валюту, в которую сделать перевод.");
    }

    public void URL(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sberbank.ru/ru/quotes/currencies"));
        startActivity(intent);
    }
}