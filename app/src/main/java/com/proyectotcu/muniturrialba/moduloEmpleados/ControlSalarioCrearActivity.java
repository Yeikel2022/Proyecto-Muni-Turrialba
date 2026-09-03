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
import com.proyectotcu.muniturrialba.databinding.ActivityControlSalariosCrearBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.SalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ControlSalarioCrearActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityControlSalariosCrearBinding salariosCrearBinding;

    //Variables globales:
    String nombreRecorrido, apellidosRecorrido, edadRecorrido, cedulaRecorrido, departamentoRecorrido,
           fechaEntregaIngresado, salarioIngresada, descripcionIngresada;


    //Interfaz que contiene los métodos de la entidad FAQ.
    SalarioInterface salarioInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        salariosCrearBinding = ActivityControlSalariosCrearBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(salariosCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_CrearControlSalarios), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        salariosCrearBinding.imgFotoCrearSalarios.setVisibility(GONE);
        salariosCrearBinding.txtMensajeCrearSalarios.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            Intent recuperarUsuarioEmpleado = getIntent();

            nombreRecorrido = recuperarUsuarioEmpleado.getStringExtra("Nombre");
            apellidosRecorrido = recuperarUsuarioEmpleado.getStringExtra("Apellidos");
            edadRecorrido = recuperarUsuarioEmpleado.getStringExtra("Edad");
            cedulaRecorrido = recuperarUsuarioEmpleado.getStringExtra("Cedula");
            departamentoRecorrido = recuperarUsuarioEmpleado.getStringExtra("Departamento");

            salariosCrearBinding.txtNombreSalariosCrear.setText(nombreRecorrido);
            salariosCrearBinding.txtApellidosSalariosCrear.setText(apellidosRecorrido);
            salariosCrearBinding.txtEdadSalariosCrear.setText(edadRecorrido);
            salariosCrearBinding.txtCedulaSalariosCrear.setText(cedulaRecorrido);
            salariosCrearBinding.txtDepartamentoSalariosCrear.setText(departamentoRecorrido);

            salariosCrearBinding.btnSeleccionarUsuarioEmpleadoSalarios.setOnClickListener(v -> VistaUsuarioEmpleado());
            salariosCrearBinding.btnCrearSalario.setOnClickListener(v -> CrearNuevoSalario(tokenGuardado));

        } catch (Exception error) {
            salariosCrearBinding.txtTituloCrearSalarios.setVisibility(GONE);
            salariosCrearBinding.txtNombreSalariosCrear.setVisibility(GONE);
            salariosCrearBinding.txtApellidosSalariosCrear.setVisibility(GONE);

            salariosCrearBinding.txtEdadSalariosCrear.setVisibility(GONE);
            salariosCrearBinding.txtCedulaSalariosCrear.setVisibility(GONE);
            salariosCrearBinding.txtDepartamentoSalariosCrear.setVisibility(GONE);

            salariosCrearBinding.edtxtFechaEntregaSalariosCrear.setVisibility(GONE);
            salariosCrearBinding.edtxtSalarioCrear.setVisibility(GONE);
            salariosCrearBinding.edtxtDescripcionSalariosCrear.setVisibility(GONE);

            salariosCrearBinding.btnSeleccionarUsuarioEmpleadoSalarios.setVisibility(GONE);
            salariosCrearBinding.btnCrearSalario.setVisibility(GONE);

            salariosCrearBinding.imgFotoCrearSalarios.setVisibility(VISIBLE);
            salariosCrearBinding.txtMensajeCrearSalarios.setVisibility(VISIBLE);

            salariosCrearBinding.imgFotoCrearSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            salariosCrearBinding.txtMensajeCrearSalarios.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
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
                Intent intentControlSalario = new Intent(ControlSalarioCrearActivity.this, ControlEmpleadosActivity.class);

                intentControlSalario.putExtra("Seccion_A_Mostrar", "Control_Salario");

                startActivity(intentControlSalario);

                finish();
            }
        });
    }


    private void CrearNuevoSalario(String tokenUsuario) {
        fechaEntregaIngresado = salariosCrearBinding.edtxtFechaEntregaSalariosCrear.getText().toString().trim();
        salarioIngresada = salariosCrearBinding.edtxtSalarioCrear.getText().toString().trim();
        descripcionIngresada = salariosCrearBinding.edtxtDescripcionSalariosCrear.getText().toString().trim();

        boolean respuestaValidacion = ValidarSalario(nombreRecorrido, apellidosRecorrido, edadRecorrido, cedulaRecorrido,
                departamentoRecorrido, fechaEntregaIngresado, salarioIngresada, descripcionIngresada);

        if(respuestaValidacion != false) {
            salarioInterface = ConexionAPI.Conexion_API_Salario(this);

            SalarioEntitie salarioEntitie = new SalarioEntitie(fechaEntregaIngresado, Double.parseDouble(salarioIngresada), descripcionIngresada);

            Call<SalarioEntitie> crearNuevopermisoTiempo = salarioInterface.crearSalarios(salarioEntitie, cedulaRecorrido, tokenUsuario);


            crearNuevopermisoTiempo.enqueue(new Callback<SalarioEntitie>() {
                @Override
                public void onResponse(Call<SalarioEntitie> call, Response<SalarioEntitie> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ControlSalarioCrearActivity.this,
                                "¡El nuevo registro del salario se creo exitosamente!", Toast.LENGTH_SHORT).show();

                        nombreRecorrido = null;
                        apellidosRecorrido = null;
                        edadRecorrido = null;
                        cedulaRecorrido = null;

                        departamentoRecorrido = null;
                        fechaEntregaIngresado = null;
                        salarioIngresada = null;
                        descripcionIngresada = null;

                        VistaRegreso();
                    } else {
                        try {
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible crear el registro del salario debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();

                        } catch (Exception error) {
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible crear el registro del salario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<SalarioEntitie> call, Throwable t) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible crear el registro del salario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    private boolean ValidarSalario(String nombreUsuario, String apellidosUsuario, String edadUsuario, String cedulaUsuario,
                                   String departamentoUsuario, String fechaEntrega, String salario, String descripcion) {

        if (nombreUsuario == null && apellidosUsuario == null && edadUsuario == null && cedulaUsuario == null && departamentoUsuario == null &&
                fechaEntrega.isEmpty() && salario.isEmpty() && descripcion.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el registro del salario debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (nombreUsuario == null && apellidosUsuario == null && edadUsuario == null && cedulaUsuario == null && departamentoUsuario == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el registro del salario debido a que no se selecciono a ningún usuario(a)." + "\n\nPor favor, seleccione un usuario(a) e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (fechaEntrega.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el registro del salario debido a que la fecha de entrega se encuentra vacia." + "\n\nPor favor, digite otra vez su fecha de entrega e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el registro del salario debido a que esta se encuentra vacia o supera los dos digitos permitidos." + "\n\nPor favor, digite otra vez su salario e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }


        if (descripcion.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlSalarioCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el registro del salario debido a que la descripción se encuentra vacia." + "\n\nPor favor, digite otra vez su descripción e intentelo de nuevo.")
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
        Intent intentControlSalario = new Intent(ControlSalarioCrearActivity.this, ControlEmpleadosActivity.class);

        intentControlSalario.putExtra("Seccion_A_Mostrar", "Control_Salario");

        startActivity(intentControlSalario);

        finish();
    }


    private void VistaUsuarioEmpleado() {
        Intent intentUsuarioEmpleado = new Intent(ControlSalarioCrearActivity.this, UsuarioEmpleadoActivity.class);

        intentUsuarioEmpleado.putExtra("Tipo_De_Accion", "Crear_Salario");
        intentUsuarioEmpleado.putExtra("Regresar", "Salario");

        startActivity(intentUsuarioEmpleado);

        finish();
    }
}
