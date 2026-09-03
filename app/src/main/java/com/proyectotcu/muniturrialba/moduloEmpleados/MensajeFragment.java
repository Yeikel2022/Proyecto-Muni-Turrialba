package com.proyectotcu.muniturrialba.moduloEmpleados;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyectotcu.muniturrialba.R;


public class MensajeFragment extends Fragment {
    //Variables globales para esta clase.
    TextView txtMensaje;

    ImageView logitoEmpleados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mensaje, container, false);
        logitoEmpleados = view.findViewById(R.id.img_fotoMensajeError);
        txtMensaje = view.findViewById(R.id.txt_MensajeError);

        logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);

        if(getArguments() != null) {
            String tipoMensaje = getArguments().getString("Tipo_Mensaje");

            if("Error".equals(tipoMensaje)) {
                txtMensaje.setText(getString(R.string.ErrorFragment));
            }

        } else {
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */
        }

        return view;
    }
}