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
import com.proyectotcu.muniturrialba.databinding.ActivityPermisosTiempoActualizarBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoTiempoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoTiempoInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermisoTiempoActualizarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityPermisosTiempoActualizarBinding permisosTiempoActualizarBinding;

    //Variables globales:
    String nombreRecorrido, apellidosRecorrido, cedulaRecorrido, tipoPermisoRecorrido, descripcionRecorrido,
           fechaAsignacionRecorrido, fechaFinalizacionRecorrido, tipoPermisoActualizada, descripcionActualizada,
           fechaAsignacionActualizada, fechaFinalizacionActualizada;


    //Interfaz que contiene los métodos de la entidad FAQ.
    PermisoTiempoInterface permisoTiempoInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permisosTiempoActualizarBinding = ActivityPermisosTiempoActualizarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(permisosTiempoActualizarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ActualizarPermisosTiempo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        permisosTiempoActualizarBinding.imgFotoActualizarPermisoTiempo.setVisibility(GONE);
        permisosTiempoActualizarBinding.txtMensajeActualizarPermisoTiempo.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            Intent recuperarUsuarioEmpleado = getIntent();

            nombreRecorrido = recuperarUsuarioEmpleado.getStringExtra("Nombre");
            apellidosRecorrido = recuperarUsuarioEmpleado.getStringExtra("Apellidos");
            cedulaRecorrido = recuperarUsuarioEmpleado.getStringExtra("Cedula");

            tipoPermisoRecorrido = recuperarUsuarioEmpleado.getStringExtra("TipoPermiso");
            descripcionRecorrido = recuperarUsuarioEmpleado.getStringExtra("Descripcion");
            fechaAsignacionRecorrido = recuperarUsuarioEmpleado.getStringExtra("FechaAsignacion");
            fechaFinalizacionRecorrido = recuperarUsuarioEmpleado.getStringExtra("FechaFinalizacion");

            permisosTiempoActualizarBinding.txtNombrePermisoTiempoActualizado.setText(nombreRecorrido);
            permisosTiempoActualizarBinding.txtApellidosPermisoTiempoActualizado.setText(apellidosRecorrido);
            permisosTiempoActualizarBinding.txtCedulaPermisosTiempoActualizado.setText(cedulaRecorrido);

            permisosTiempoActualizarBinding.edtxtTipoPermisoActualizada.setText(tipoPermisoRecorrido);
            permisosTiempoActualizarBinding.edtxtDescripcionPermisoTiempoActualizado.setText(descripcionRecorrido);

            permisosTiempoActualizarBinding.edtxtFechaAsignacionPermisosTiempoActualizada.setText(fechaAsignacionRecorrido);
            permisosTiempoActualizarBinding.edtxtFechaFinalizacionPermisosTiempoActualizada.setText(fechaFinalizacionRecorrido);


            permisosTiempoActualizarBinding.btnActualizarPermisoTiempo.setOnClickListener(v -> {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_actualizar);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de actualizar este permiso de tiempo?")
                        .setTitle("Actualizar Permiso Tiempo.");


                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarPermisoTiempo(tokenGuardado);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PermisoTiempoActualizarActivity.this, "¡No se continuo con la actualización!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });

        } catch (Exception error) {
            permisosTiempoActualizarBinding.txtTituloActualizarPermisosTiempo.setVisibility(GONE);
            permisosTiempoActualizarBinding.txtNombrePermisoTiempoActualizado.setVisibility(GONE);
            permisosTiempoActualizarBinding.txtApellidosPermisoTiempoActualizado.setVisibility(GONE);

            permisosTiempoActualizarBinding.txtCedulaPermisosTiempoActualizado.setVisibility(GONE);
            permisosTiempoActualizarBinding.edtxtTipoPermisoActualizada.setVisibility(GONE);
            permisosTiempoActualizarBinding.edtxtDescripcionPermisoTiempoActualizado.setVisibility(GONE);

            permisosTiempoActualizarBinding.edtxtFechaAsignacionPermisosTiempoActualizada.setVisibility(GONE);
            permisosTiempoActualizarBinding.edtxtFechaFinalizacionPermisosTiempoActualizada.setVisibility(GONE);
            permisosTiempoActualizarBinding.btnActualizarPermisoTiempo.setVisibility(GONE);

            permisosTiempoActualizarBinding.imgFotoActualizarPermisoTiempo.setVisibility(VISIBLE);
            permisosTiempoActualizarBinding.txtMensajeActualizarPermisoTiempo.setVisibility(VISIBLE);

            permisosTiempoActualizarBinding.imgFotoActualizarPermisoTiempo.setImageResource(R.drawable.icono_contenido_no_disponible);
            permisosTiempoActualizarBinding.txtMensajeActualizarPermisoTiempo.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
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
                Intent intentPermisosTiempo = new Intent(PermisoTiempoActualizarActivity.this, ControlEmpleadosActivity.class);

                intentPermisosTiempo.putExtra("Seccion_A_Mostrar", "Permiso_Tiempo");

                startActivity(intentPermisosTiempo);

                finish();
            }
        });
    }


    private void ActualizarPermisoTiempo(String tokenUsuario) {
        tipoPermisoActualizada = permisosTiempoActualizarBinding.edtxtTipoPermisoActualizada.getText().toString().trim();
        descripcionActualizada = permisosTiempoActualizarBinding.edtxtDescripcionPermisoTiempoActualizado.getText().toString().trim();

        fechaAsignacionActualizada = permisosTiempoActualizarBinding.edtxtFechaAsignacionPermisosTiempoActualizada.getText().toString().trim();
        fechaFinalizacionActualizada = permisosTiempoActualizarBinding.edtxtFechaFinalizacionPermisosTiempoActualizada.getText().toString().trim();

        boolean respuestaValidacion = ValidarPermisoTiempo(nombreRecorrido, apellidosRecorrido, cedulaRecorrido,
                tipoPermisoActualizada, descripcionActualizada, fechaAsignacionActualizada, fechaFinalizacionActualizada);

        if(respuestaValidacion != false) {
            permisoTiempoInterface = ConexionAPI.Conexion_API_Permiso_Tiempo(this);

            PermisoTiempoEntitie permisoTiempoEntitie = new PermisoTiempoEntitie(tipoPermisoActualizada, descripcionActualizada,
                    fechaAsignacionActualizada, fechaFinalizacionActualizada);

            Call<Boolean> actualizarPermisosTiempo = permisoTiempoInterface.actualizarPermisosTiempo(permisoTiempoEntitie, cedulaRecorrido, tokenUsuario);


            actualizarPermisosTiempo.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PermisoTiempoActualizarActivity.this,
                                "¡El registro del permiso de tiempo se actualizo exitosamente!", Toast.LENGTH_SHORT).show();

                        nombreRecorrido = null;
                        apellidosRecorrido = null;
                        cedulaRecorrido = null;

                        tipoPermisoActualizada = null;
                        descripcionActualizada = null;
                        fechaAsignacionActualizada = null;
                        fechaFinalizacionActualizada = null;

                        VistaRegreso();
                    } else {
                        try {
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible actualizar el permiso de tiempo debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();

                        } catch (Exception error) {
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible actualizar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<Boolean> call, Throwable t) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible actualizar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    private boolean ValidarPermisoTiempo(String nombreUsuario, String apellidosUsuario, String cedulaUsuario,
                                         String tipoPermiso, String descripcion, String fechaAsignacion, String fechaFinalizacion) {

        if (nombreUsuario.equals(nombreRecorrido) && apellidosUsuario.equals(apellidosRecorrido) && cedulaUsuario.equals(cedulaRecorrido) &&
                tipoPermiso.equals(tipoPermisoRecorrido) && descripcion.equals(descripcionRecorrido) && fechaAsignacion.equals(fechaAsignacionRecorrido) &&
                fechaFinalizacion.equals(fechaFinalizacionRecorrido)) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que están los mismos datos." + "\n\nPor favor, ingrese los nuevos cambios que desea e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (nombreUsuario == null && apellidosUsuario == null && cedulaUsuario == null && tipoPermiso.isEmpty() &&
                descripcion.isEmpty() && fechaAsignacion.isEmpty() && fechaFinalizacion.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (tipoPermiso.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que el tipo de permiso que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez su tipo de permiso e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (descripcion.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que la descripción se encuentra vacia." + "\n\nPor favor, digite otra vez su descripción e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaAsignacion.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que la fecha de asignación se encuentra vacia." + "\n\nPor favor, digite otra vez su fecha de asignación e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaFinalizacion.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisoTiempoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el permiso de tiempo debido a que la fecha de finalización se encuentra vacia." + "\n\nPor favor, digite otra vez su fecha de finalización e intentelo de nuevo.")
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
        Intent intentPermisosTiempo = new Intent(PermisoTiempoActualizarActivity.this, ControlEmpleadosActivity.class);

        intentPermisosTiempo.putExtra("Seccion_A_Mostrar", "Permiso_Tiempo");

        startActivity(intentPermisosTiempo);

        finish();
    }
}
