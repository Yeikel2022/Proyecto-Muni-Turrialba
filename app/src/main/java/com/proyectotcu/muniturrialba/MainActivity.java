package com.proyectotcu.muniturrialba;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.databinding.ActivityMainBinding;
import com.proyectotcu.muniturrialba.index.MenuPrincipalActivity;
import com.proyectotcu.muniturrialba.index.RegistroActivity;

public class MainActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityMainBinding mainBinding;

    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
    * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
    * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Sirve para llamar el metodo de registro y de inicio de sesion, siempre y cuando -
        *  el usuario presione el boton respectivo.   */
        mainBinding.btnRegistro.setOnClickListener(v -> VistaRegistro());
        mainBinding.btnInicioSesion.setOnClickListener(v -> VistaInicioSesion());
    }

    /* Metodo que sirve para llevar el usuario hacia la vista (que es la pantallita) -
    *  de registro.   */
    private void VistaRegistro() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentRegistro = new Intent(MainActivity.this,
                RegistroActivity.class);

        startActivity(intentRegistro);
    }

    /* Metodo que sirve para llevar el usuario hacia la vista (que es la pantallita) -
     *  de inicio de sesion.   */
    private void VistaInicioSesion() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentInicioSesion = new Intent(MainActivity.this,
                MenuPrincipalActivity.class);

        startActivity(intentInicioSesion);
    }
}