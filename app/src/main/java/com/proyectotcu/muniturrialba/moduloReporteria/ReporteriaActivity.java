package com.proyectotcu.muniturrialba.moduloReporteria;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityMenuPrincipalBinding;
import com.proyectotcu.muniturrialba.databinding.ActivityReporteriaBinding;

public class ReporteriaActivity extends AppCompatActivity {

    private ActivityReporteriaBinding activityReporteriaBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityReporteriaBinding = ActivityReporteriaBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activityReporteriaBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Reporteria), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




}