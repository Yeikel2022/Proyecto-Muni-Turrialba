package com.proyectotcu.muniturrialba.moduloReporteria;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityReporteriaBinding;
import com.proyectotcu.muniturrialba.moduloEmpleados.MensajeFragment;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ReporteriaActivity extends AppCompatActivity {

    private ActivityReporteriaBinding reporteriaBinding;
    public static Boolean mensajeReporteria = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reporteriaBinding = ActivityReporteriaBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(reporteriaBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Reporteria), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = this.getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            String[] partesToken = tokenGuardado.split("\\.");

            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(cuerpoToken);

            Integer campoRol = Integer.parseInt(json.optString("rol"));
            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));

            Menu itemsMenu = reporteriaBinding.btnVBarraNavegacionReporteria.getMenu();
            itemsMenu.findItem(R.id.itm_Reportes_Mensaje).setVisible(false);


            //Moderador o administrador.
            if (campoRol == 1 || campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisos(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);

                if (respuestaPermisos == 5) {
                    Intent seccionRecorrida = getIntent();
                    String seccionMostrar = seccionRecorrida.getStringExtra("Seccion_A_Mostrar");

                    if ("Usuarios".equals(seccionMostrar)) {
                        //ESTO NO SE BORRA
                        /*ArrayList<Uri> documentosRecorridos =
                                getIntent().getParcelableArrayListExtra("Documentos_PDF");*/
                        String documentoPDF = seccionRecorrida.getStringExtra("Documento_PDF");

                        Bundle bundleReporte = new Bundle();
                        ReporteUsuarioFragment reporteUsuarioFragment = new ReporteUsuarioFragment();

                        bundleReporte.putString("Documento-PDF", documentoPDF);
                        reporteUsuarioFragment.setArguments(bundleReporte);

                        Fragmentos(reporteUsuarioFragment);

                        //Fragmentos(new ReporteUsuarioFragment());
                        reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Usuarios);

                    }  else if ("Salarios".equals(seccionMostrar)) {
                        Fragmentos(new ReporteSalarioFragment());
                        reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Salarios);

                    } else if ("Permisos_Tiempo".equals(seccionMostrar)) {
                        Fragmentos(new ReportePermisoTiempoFragment());
                        reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Permisos_Tiempo);

                    } else {
                        Fragmentos(new ReporteUsuarioFragment());
                    }


                    reporteriaBinding.btnVBarraNavegacionReporteria.setOnItemSelectedListener(menuItem -> {
                        int idItem = menuItem.getItemId();

                        if (idItem == R.id.itm_Reportes_Usuarios) {
                            Fragmentos(new ReporteUsuarioFragment());

                        } else if (idItem == R.id.itm_Reportes_Salarios) {
                            Fragmentos(new ReporteSalarioFragment());

                        } else if (idItem == R.id.itm_Reportes_Permisos_Tiempo) {
                            Fragmentos(new ReportePermisoTiempoFragment());
                        }

                        return true;
                    });
                }
            }

        } catch (Exception error) {
            Menu itemsMenu = reporteriaBinding.btnVBarraNavegacionReporteria.getMenu();
            itemsMenu.findItem(R.id.itm_Reportes_Usuarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Salarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Permisos_Tiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Mensaje).setVisible(true);

            Bundle bundleMensaje = new Bundle();
            MensajeFragment mensajeFragment = new MensajeFragment();

            bundleMensaje.putString("Tipo_Mensaje", "Error");
            mensajeFragment.setArguments(bundleMensaje);

            Fragmentos(mensajeFragment);
            reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Mensaje);

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteriaActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private Integer ValidarPermisos(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {

        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            Menu itemsMenu = reporteriaBinding.btnVBarraNavegacionReporteria.getMenu();
            itemsMenu.findItem(R.id.itm_Reportes_Usuarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Salarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Permisos_Tiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Mensaje).setVisible(true);

            Fragmentos(new MensajeFragment());
            reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Mensaje);

            if (mensajeReporteria != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteriaActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados de reporteria.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeReporteria = true;
                    }});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

            return 0;
        }


        if(Leer == false) {
            Menu itemsMenu = reporteriaBinding.btnVBarraNavegacionReporteria.getMenu();
            itemsMenu.findItem(R.id.itm_Reportes_Usuarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Salarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Permisos_Tiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_Reportes_Mensaje).setVisible(true);

            Fragmentos(new MensajeFragment());
            reporteriaBinding.btnVBarraNavegacionReporteria.setSelectedItemId(R.id.itm_Reportes_Mensaje);

            if (mensajeReporteria != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteriaActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados de reporteria.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeReporteria = true;
                    }});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

            return 0;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    private void Fragmentos(Fragment fragmentoSeleccionado){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framL_PlantillaReporteria, fragmentoSeleccionado).commit();
    }
}