package com.proyectotcu.muniturrialba.moduloAdministracionArchivos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityAdministracionArchivoBinding;
import com.proyectotcu.muniturrialba.databinding.ActivityReporteriaBinding;

public class AdministracionArchivosActivity extends AppCompatActivity {

    private ActivityAdministracionArchivoBinding activityAdministracionArchivoBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityAdministracionArchivoBinding = ActivityAdministracionArchivoBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activityAdministracionArchivoBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_AdministracionArchivo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}