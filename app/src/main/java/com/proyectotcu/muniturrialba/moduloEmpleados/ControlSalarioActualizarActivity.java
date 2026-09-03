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
import com.proyectotcu.muniturrialba.databinding.ActivityControlSalariosActualizarBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.SalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControlSalarioActualizarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityControlSalariosActualizarBinding salarioActualizarBinding;

    //Variables globales:
    String nombreRecorrido, apellidosRecorrido, cedulaRecorrido, fechaEntregaRecorrido,
           salarioRecorrido, descripcionRecorrido, fechaEntregaActualizada, salarioActualizada,
           descripcionActualizada;


    //Interfaz que contiene los métodos de la entidad FAQ.
    SalarioInterface salarioInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        salarioActualizarBinding = ActivityControlSalariosActualizarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(salarioActualizarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ActualizarControlSalarios), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        salarioActualizarBinding.imgFotoActualizarSalarios.setVisibility(GONE);
        salarioActualizarBinding.txtMensajeActualizarSalarios.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            Intent recuperarUsuarioEmpleado = getIntent();

            nombreRecorrido = recuperarUsuarioEmpleado.getStringExtra("Nombre");
            apellidosRecorrido = recuperarUsuarioEmpleado.getStringExtra("Apellidos");
            cedulaRecorrido = recuperarUsuarioEmpleado.getStringExtra("Cedula");

            fechaEntregaRecorrido = recuperarUsuarioEmpleado.getStringExtra("FechaEntrega");
            salarioRecorrido = recuperarUsuarioEmpleado.getStringExtra("Salario");
            descripcionRecorrido = recuperarUsuarioEmpleado.getStringExtra("Descripcion");

            salarioActualizarBinding.txtNombreSalariosActualizado.setText(nombreRecorrido);
            salarioActualizarBinding.txtApellidosSalariosActualizado.setText(apellidosRecorrido);
            salarioActualizarBinding.txtCedulaSalariosActualizado.setText(cedulaRecorrido);

            salarioActualizarBinding.edtxtFechaEntregaSalarioActualizado.setText(fechaEntregaRecorrido);
            salarioActualizarBinding.edtxtSalarioActualizado.setText(salarioRecorrido);
            salarioActualizarBinding.edtxtDescripcionSalarioActualizado.setText(descripcionRecorrido);

            salarioActualizarBinding.btnActualizarSalarios.setOnClickListener(v -> {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_actualizar);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de actualizar este salario?")
                        .setTitle("Actualizar Salario.");


                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarSalario(tokenGuardado);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ControlSalarioActualizarActivity.this, "¡No se continuo con la actualización!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });

        } catch (Exception error) {
            salarioActualizarBinding.txtTituloActualizarSalarios.setVisibility(GONE);
            salarioActualizarBinding.txtNombreSalariosActualizado.setVisibility(GONE);
            salarioActualizarBinding.txtApellidosSalariosActualizado.setVisibility(GONE);

            salarioActualizarBinding.txtCedulaSalariosActualizado.setVisibility(GONE);
            salarioActualizarBinding.edtxtFechaEntregaSalarioActualizado.setVisibility(GONE);
            salarioActualizarBinding.edtxtSalarioActualizado.setVisibility(GONE);

            salarioActualizarBinding.edtxtDescripcionSalarioActualizado.setVisibility(GONE);
            salarioActualizarBinding.btnActualizarSalarios.setVisibility(GONE);

            salarioActualizarBinding.imgFotoActualizarSalarios.setVisibility(VISIBLE);
            salarioActualizarBinding.txtMensajeActualizarSalarios.setVisibility(VISIBLE);

            salarioActualizarBinding.imgFotoActualizarSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            salarioActualizarBinding.txtMensajeActualizarSalarios.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
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
                Intent intentControlSalario = new Intent(ControlSalarioActualizarActivity.this, ControlEmpleadosActivity.class);

                intentControlSalario.putExtra("Seccion_A_Mostrar", "Control_Salario");

                startActivity(intentControlSalario);

                finish();
            }
        });
    }


    private void ActualizarSalario(String tokenUsuario) {
        fechaEntregaActualizada = salarioActualizarBinding.edtxtFechaEntregaSalarioActualizado.getText().toString().trim();
        salarioActualizada = salarioActualizarBinding.edtxtSalarioActualizado.getText().toString().trim();
        descripcionActualizada = salarioActualizarBinding.edtxtDescripcionSalarioActualizado.getText().toString().trim();

        boolean respuestaValidacion = ValidarSalario(nombreRecorrido, apellidosRecorrido, cedulaRecorrido,
                fechaEntregaActualizada, salarioActualizada, descripcionActualizada);

        if(respuestaValidacion != false) {
            salarioInterface = ConexionAPI.Conexion_API_Salario(this);

            SalarioEntitie salarioEntitie = new SalarioEntitie(fechaEntregaActualizada, Double.parseDouble(salarioActualizada), descripcionActualizada);

            Call<Boolean> actualizarSalarios = salarioInterface.actualizarSalarios(salarioEntitie, cedulaRecorrido, tokenUsuario);


            actualizarSalarios.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ControlSalarioActualizarActivity.this,
                                "¡El registro del salario se actualizo exitosamente!", Toast.LENGTH_SHORT).show();

                        fechaEntregaActualizada = null;
                        salarioActualizada = null;
                        descripcionActualizada = null;

                        nombreRecorrido = null;
                        apellidosRecorrido = null;
                        cedulaRecorrido = null;

                        fechaEntregaRecorrido = null;
                        salarioRecorrido = null;
                        descripcionRecorrido = null;

                        VistaRegreso();
                    } else {
                        try {
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible actualizar el salario debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();

                        } catch (Exception error) {
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible actualizar el salario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible actualizar el salario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    private boolean ValidarSalario(String nombreUsuario, String apellidosUsuario, String cedulaUsuario,
                                   String fechaEntrega, String salario, String descripcion) {

        if (nombreUsuario.equals(nombreRecorrido) && apellidosUsuario.equals(apellidosRecorrido) && cedulaUsuario.equals(cedulaRecorrido) &&
                fechaEntrega.equals(fechaEntregaRecorrido) && salario.equals(salarioRecorrido) && descripcion.equals(descripcionRecorrido)) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el salario debido a que están los mismos datos." + "\n\nPor favor, ingrese los nuevos cambios que desea e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (nombreUsuario == null && apellidosUsuario == null && cedulaUsuario == null &&
                fechaEntrega.isEmpty() && salario.isEmpty() && descripcion.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el salario debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaEntrega.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el salario debido a que la fecha de entrega se encuentra vacia." + "\n\nPor favor, digite otra vez la fecha de entrega e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        Pattern patronSalario = Pattern.compile("^\\d+(\\.\\d{2})?$");
        Matcher compararSalario = patronSalario.matcher(salario);
        if (salario.isEmpty() || !compararSalario.matches()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el salario debido a que esta se encuentra vacia o supera los dos digitos permitidos." + "\n\nPor favor, digite otra vez el salario e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (descripcion.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el salario debido a que la descripción se encuentra vacia." + "\n\nPor favor, digite otra vez la descripción e intentelo de nuevo.")
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
        Intent intentControlSalario = new Intent(ControlSalarioActualizarActivity.this, ControlEmpleadosActivity.class);

        intentControlSalario.putExtra("Seccion_A_Mostrar", "Control_Salario");

        startActivity(intentControlSalario);

        finish();
    }
}
