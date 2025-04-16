package com.example.databasetry1;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> studentList;
    ArrayAdapter<String> adapter;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        gridView = findViewById(R.id.gridView);
        db = new DBHelper(this);
        studentList = new ArrayList<>();

        refreshList();

        // Set long click listener to show popup menu
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            showPopupMenu(view, position);
            return true;
        });
    }

    private void refreshList() {
        studentList.clear();
        studentList.addAll(db.getAllStudents());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        gridView.setAdapter(adapter);
    }

    private void showPopupMenu(View anchorView, int position) {
        PopupMenu popupMenu = new PopupMenu(this, anchorView);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());

        String data = studentList.get(position);
        String id = data.split(":")[0];

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.update) {
                showUpdateDialog(id, data);
                return true;
            } else if (item.getItemId() == R.id.delete) {
                db.deleteStudent(id);
                Toast.makeText(this, "Student Deleted", Toast.LENGTH_SHORT).show();
                refreshList();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showUpdateDialog(String id, String oldData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Student");

        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);

        EditText editName = view.findViewById(R.id.editUpdateName);
        RadioGroup genderGroup = view.findViewById(R.id.genderUpdateGroup);
        CheckBox checkMath = view.findViewById(R.id.checkUpdateMath);
        CheckBox checkScience = view.findViewById(R.id.checkUpdateScience);
        Spinner spinnerClass = view.findViewById(R.id.spinnerUpdateClass);
        EditText editPhone = view.findViewById(R.id.editUpdatePhone);


        String[] classes = {"Class 1", "Class 2", "Class 3"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classes);
        spinnerClass.setAdapter(spinnerAdapter);

        // Parse existing data
        String[] parts = oldData.split(":")[1].trim().split(", ");
        editName.setText(parts[0]);

        if (parts[1].equals("Male")) genderGroup.check(R.id.radioUpdateMale);
        else if (parts[1].equals("Female")) genderGroup.check(R.id.radioUpdateFemale);

        if (parts[2].contains("Math")) checkMath.setChecked(true);
        if (parts[2].contains("Science")) checkScience.setChecked(true);

        for (int i = 0; i < classes.length; i++) {
            if (classes[i].equals(parts[3])) {
                spinnerClass.setSelection(i);
                break;
            }
        }

        builder.setView(view);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = editName.getText().toString().trim();
            int selectedGenderId = genderGroup.getCheckedRadioButtonId();

            if (name.isEmpty() || selectedGenderId == -1) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = editPhone.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Phone number required", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender = ((RadioButton) view.findViewById(selectedGenderId)).getText().toString();
            String subjects = "";
            if (checkMath.isChecked()) subjects += "Math ";
            if (checkScience.isChecked()) subjects += "Science ";
            String classSelected = spinnerClass.getSelectedItem().toString();

            db.updateStudent(id, name, phone, gender, subjects.trim(), classSelected);
            Toast.makeText(this, "Student Updated", Toast.LENGTH_SHORT).show();
            refreshList();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}


