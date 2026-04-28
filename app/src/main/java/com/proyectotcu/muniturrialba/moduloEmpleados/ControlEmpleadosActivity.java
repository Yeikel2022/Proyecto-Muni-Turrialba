package com.proyectotcu.muniturrialba.moduloEmpleados;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityControlEmpleadosBinding;
import com.proyectotcu.muniturrialba.databinding.ActivityMainBinding;

public class ControlEmpleadosActivity extends AppCompatActivity {

    private ActivityControlEmpleadosBinding controlEmpleadosBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controlEmpleadosBinding = ActivityControlEmpleadosBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(controlEmpleadosBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ControlEmpleados), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}