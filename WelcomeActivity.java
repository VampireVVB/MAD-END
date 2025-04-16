package com.example.databasetry1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    EditText editName, editPhone;
    RadioGroup genderGroup;
    CheckBox checkMath, checkScience;
    Spinner spinnerClass;
    Button btnAdd, btnView;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        genderGroup = findViewById(R.id.genderGroup);
        checkMath = findViewById(R.id.checkMath);
        checkScience = findViewById(R.id.checkScience);
        spinnerClass = findViewById(R.id.spinnerClass);
        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);

        db = new DBHelper(this);

        String[] classes = {"Class 1", "Class 2", "Class 3"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        spinnerClass.setAdapter(spinnerAdapter);

        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = editPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }


            int selectedGenderId = genderGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(this, "Select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = ((RadioButton) findViewById(selectedGenderId)).getText().toString();
            String subjects = "";
            if (checkMath.isChecked()) subjects += "Math ";
            if (checkScience.isChecked()) subjects += "Science ";
            String classSelected = spinnerClass.getSelectedItem().toString();

            db.insertStudent(name, phone, gender, subjects.trim(), classSelected);
            Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show();
            editName.setText("");
            genderGroup.clearCheck();
            checkMath.setChecked(false);
            checkScience.setChecked(false);
            spinnerClass.setSelection(0);
        });

        btnView.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }
}