package com.proyectotcu.muniturrialba.index;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityMenuPrincipalBinding;

public class MenuPrincipalActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityMenuPrincipalBinding activityMenuPrincipalBinding;

    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
     * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityMenuPrincipalBinding = ActivityMenuPrincipalBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(activityMenuPrincipalBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_MenuPrincipal), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Fragmentos(new MenuFragment());

        activityMenuPrincipalBinding.btnVBarraNavegacion.setOnItemSelectedListener(menuItem -> {

            int idItem = menuItem.getItemId();

            if(idItem == R.id.itm_Menu) {
                Fragmentos(new MenuFragment());

            } else if (idItem == R.id.itm_Perfil) {
                Fragmentos(new PerfilFragment());

            } else if (idItem == R.id.itm_CarnetVirtual) {
                Fragmentos(new CarnetVirtualFragment());

            } else if (idItem == R.id.itm_Ayuda) {
                Fragmentos(new AyudaFragment());
            }

            return true;
        });

    }


    private void Fragmentos(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framL_PlantillaMenu, fragment).commit();
    }

}