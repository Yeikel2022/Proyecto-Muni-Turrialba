package com.proyectotcu.muniturrialba.index;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;

public class PerfilFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        Button botonCerrar = view.findViewById(R.id.btn_CerrarSesion);

        botonCerrar.setOnClickListener(v -> VistaCerrarSesion());

        return view;
        //return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    private void VistaCerrarSesion() {
        SharedPreferences archivoXML = getContext().getSharedPreferences("Archivo_Autenticacion", Context.MODE_PRIVATE);
        archivoXML.edit().clear().apply();
        MainActivity.botonActivado = false;

        /* Esto solo seria si quisiera ver que pantalla es en el Debug.
        Activity nombreActividad = getActivity();

        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPrincipal = new Intent(nombreActividad, MainActivity.class);
        startActivity(intentPrincipal);
        nombreActividad.finish(); */

        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPrincipal = new Intent(getActivity(), MainActivity.class);
        startActivity(intentPrincipal);
        getActivity().finish();
    }
}