package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityPermisosTiempoCrearBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoTiempoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoTiempoInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermisoTiempoCrearActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityPermisosTiempoCrearBinding permisosTiempoCrearBinding;

    //Variables globales:
    String nombreRecorrido, apellidosRecorrido, cedulaRecorrido, departamentoRecorrido, tipoPermisoIngresada,
           descripcionIngresada, fechaAsignacionIngresada, fechaFinalizacionIngresada;


    //Interfaz que contiene los métodos de la entidad FAQ.
    PermisoTiempoInterface permisoTiempoInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permisosTiempoCrearBinding = ActivityPermisosTiempoCrearBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(permisosTiempoCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_CrearPermisosTiempo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        permisosTiempoCrearBinding.imgFotoCrearPermisosTiempo.setVisibility(GONE);
        permisosTiempoCrearBinding.txtMensajeCrearPermisosTiempo.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            Intent recuperarUsuarioEmpleado = getIntent();

            nombreRecorrido = recuperarUsuarioEmpleado.getStringExtra("Nombre");
            apellidosRecorrido = recuperarUsuarioEmpleado.getStringExtra("Apellidos");
            cedulaRecorrido = recuperarUsuarioEmpleado.getStringExtra("Cedula");
            departamentoRecorrido = recuperarUsuarioEmpleado.getStringExtra("Departamento");

            permisosTiempoCrearBinding.txtNombrePermisosTiempoCrear.setText(nombreRecorrido);
            permisosTiempoCrearBinding.txtApellidosPermisosTiempoCrear.setText(apellidosRecorrido);
            permisosTiempoCrearBinding.txtCedulaPermisosTiempoCrear.setText(cedulaRecorrido);
            permisosTiempoCrearBinding.txtDepartamentoPermisosTiempoCrear.setText(departamentoRecorrido);

            permisosTiempoCrearBinding.btnSeleccionarUsuarioEmpleadoPermisosTiempo.setOnClickListener(v -> VistaAñadirEmpleado());
            permisosTiempoCrearBinding.btnCrearPermisosTiempo.setOnClickListener(v -> CrearNuevoPermisoTiempo(tokenGuardado));

        } catch (Exception error) {
            permisosTiempoCrearBinding.txtTituloCrearPermisosTiempo.setVisibility(GONE);
            permisosTiempoCrearBinding.txtNombrePermisosTiempoCrear.setVisibility(GONE);
            permisosTiempoCrearBinding.txtApellidosPermisosTiempoCrear.setVisibility(GONE);

            permisosTiempoCrearBinding.txtCedulaPermisosTiempoCrear.setVisibility(GONE);
            permisosTiempoCrearBinding.txtDepartamentoPermisosTiempoCrear.setVisibility(GONE);
            permisosTiempoCrearBinding.edtxtTipoPermiso.setVisibility(GONE);

            permisosTiempoCrearBinding.edtxtDescripcionPermisosTiempo.setVisibility(GONE);
            permisosTiempoCrearBinding.edtxtFechaAsignacionPermisosTiempo.setVisibility(GONE);
            permisosTiempoCrearBinding.edtxtFechaFinalizacionPermisosTiempo.setVisibility(GONE);

            permisosTiempoCrearBinding.btnSeleccionarUsuarioEmpleadoPermisosTiempo.setVisibility(GONE);
            permisosTiempoCrearBinding.btnCrearPermisosTiempo.setVisibility(GONE);

            permisosTiempoCrearBinding.imgFotoCrearPermisosTiempo.setVisibility(VISIBLE);
            permisosTiempoCrearBinding.txtMensajeCrearPermisosTiempo.setVisibility(VISIBLE);

            permisosTiempoCrearBinding.imgFotoCrearPermisosTiempo.setImageResource(R.drawable.icono_contenido_no_disponible);
            permisosTiempoCrearBinding.txtMensajeCrearPermisosTiempo.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible finalizar el proceso en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        //METODO PARA DETECTAR SI EL USUARIO POR X O Y RAZÓN, DESEA REGRESARSE.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentPermisosTiempo = new Intent(PermisoTiempoCrearActivity.this, ControlEmpleadosActivity.class);

                intentPermisosTiempo.putExtra("Seccion_A_Mostrar", "Permiso_Tiempo");

                startActivity(intentPermisosTiempo);

                finish();
            }
        });
    }


    private void CrearNuevoPermisoTiempo(String tokenUsuario) {
        tipoPermisoIngresada = permisosTiempoCrearBinding.edtxtTipoPermiso.getText().toString().trim();
        descripcionIngresada = permisosTiempoCrearBinding.edtxtDescripcionPermisosTiempo.getText().toString().trim();

        fechaAsignacionIngresada = permisosTiempoCrearBinding.edtxtFechaAsignacionPermisosTiempo.getText().toString().trim();
        fechaFinalizacionIngresada = permisosTiempoCrearBinding.edtxtFechaFinalizacionPermisosTiempo.getText().toString().trim();

        boolean respuestaValidacion = ValidarPermisoTiempo(nombreRecorrido, apellidosRecorrido, cedulaRecorrido,
                departamentoRecorrido, tipoPermisoIngresada, descripcionIngresada, fechaAsignacionIngresada,
                fechaFinalizacionIngresada);

        if(respuestaValidacion != false) {
            permisoTiempoInterface = ConexionAPI.Conexion_API_Permiso_Tiempo(this);

            PermisoTiempoEntitie permisoTiempoEntitie = new PermisoTiempoEntitie(tipoPermisoIngresada, descripcionIngresada, fechaAsignacionIngresada, fechaFinalizacionIngresada);

            Call<PermisoTiempoEntitie> crearNuevopermisoTiempo = permisoTiempoInterface.crearPermisosTiempo(permisoTiempoEntitie, cedulaRecorrido, tokenUsuario);


            crearNuevopermisoTiempo.enqueue(new Callback<PermisoTiempoEntitie>() {
                @Override
                public void onResponse(Call<PermisoTiempoEntitie> call, Response<PermisoTiempoEntitie> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PermisoTiempoCrearActivity.this,
                                "¡El nuevo permiso de tiempo se creo exitosamente!", Toast.LENGTH_LONG).show();

                        nombreRecorrido = null;
                        apellidosRecorrido = null;
                        cedulaRecorrido = null;
                        departamentoRecorrido = null;

                        tipoPermisoIngresada = null;
                        descripcionIngresada = null;
                        fechaAsignacionIngresada = null;
                        fechaFinalizacionIngresada = null;

                        VistaRegreso();
                    } else {
                        try {
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible crear el permiso de tiempo debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();

                        } catch (Exception error) {
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible crear el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();
                        }
                    }
                }


                @Override
                public void onFailure(Call<PermisoTiempoEntitie> call, Throwable t) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible crear el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                }
            });
        }
    }


    private boolean ValidarPermisoTiempo(String nombreUsuario, String apellidosUsuario, String cedulaUsuario, String departamentoUsuario,
                                         String tipoPermiso, String descripcion, String fechaAsignacion, String fechaFinalizacion) {

        if (nombreUsuario == null && apellidosUsuario == null && cedulaUsuario == null && departamentoUsuario == null &&
                tipoPermiso.isEmpty() && descripcion.isEmpty() && fechaAsignacion.isEmpty() && fechaFinalizacion.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (nombreUsuario == null && apellidosUsuario == null && cedulaUsuario == null && departamentoUsuario == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que no se selecciono a ningún usuario(a)." + "\n\nPor favor, seleccione un usuario(a) e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (tipoPermiso.isEmpty() || tipoPermiso == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que el tipo de permiso que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez su tipo de permiso e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (descripcion.isEmpty() || descripcion == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que la descripción se encuentra vacia." + "\n\nPor favor, digite otra vez su descripción e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaAsignacion.isEmpty() || fechaAsignacion == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que la fecha de asignación se encuentra vacia." + "\n\nPor favor, digite otra vez su fecha de asignación e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaFinalizacion.isEmpty() || fechaFinalizacion == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el permiso de tiempo debido a que la fecha de finalización se encuentra vacia." + "\n\nPor favor, digite otra vez su fecha de finalización e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        return true;
    }


    private void VistaRegreso() {
        Intent intentPermisosTiempo = new Intent(PermisoTiempoCrearActivity.this, ControlEmpleadosActivity.class);

        intentPermisosTiempo.putExtra("Seccion_A_Mostrar", "Permiso_Tiempo");

        startActivity(intentPermisosTiempo);

        finish();
    }


    private void VistaAñadirEmpleado() {
        Intent intentUsuarioEmpleado = new Intent(PermisoTiempoCrearActivity.this, UsuarioEmpleadoActivity.class);

        intentUsuarioEmpleado.putExtra("Tipo_De_Accion", "Crear_PermisoTiempo");
        intentUsuarioEmpleado.putExtra("Regresar", "PermisoTiempo");

        startActivity(intentUsuarioEmpleado);

        finish();
    }
}
