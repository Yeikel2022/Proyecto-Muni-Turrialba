package com.proyectotcu.muniturrialba.index;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityMainBinding;
import com.proyectotcu.muniturrialba.databinding.ActivityRegistroBinding;


public class RegistroActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityRegistroBinding registroBinding;

    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
     * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registroBinding = ActivityRegistroBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(registroBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

}
