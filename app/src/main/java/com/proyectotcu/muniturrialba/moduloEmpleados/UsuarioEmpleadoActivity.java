package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityListaUsuarioEmpleadosBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionEmpleadoUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioEmpleadoActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityListaUsuarioEmpleadosBinding listaUsuarioEmpleadosBinding;

    //Variables globales para esta clase.
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
            margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;
    TextView campoNombre, campoApellidos, campoCedula, campoDepartamento, campoCorreoElectronico, campoNombreRol;

    TableRow.LayoutParams parametrosContenido, parametrosCheckBox;
    String tipoAccionRecorrido, tipoRegresoRecorrido;

    TableRow nuevaFila;
    CheckBox campoCheckBox;
    List<ExtensionEmpleadoUsuarioEntitie> datosOrdenados;


    //Interfaz que contiene los métodos de la entidad FAQ.
    EmpleadoInterface empleadoInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listaUsuarioEmpleadosBinding = ActivityListaUsuarioEmpleadosBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(listaUsuarioEmpleadosBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_UsuarioEmpleadoLista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(GONE);
        listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            tipoAccionRecorrido = getIntent().getStringExtra("Tipo_De_Accion");
            tipoRegresoRecorrido = getIntent().getStringExtra("Regresar");

            SharedPreferences archivoXML = this.getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.clearFocus();
                    return true;
                }
            });
            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.clearFocus();
                    listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setIconifiedByDefault(true);
                    return false;
                }
            });

            if("Crear_PermisoTiempo".equals(tipoAccionRecorrido)) {
                listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("¿Esta completamente seguro(a) de seleccionar este empleado(a)?")
                            .setTitle("Confirmar Usuario.");

                    construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VistaPermisoTiempoCrear();
                        }
                    });

                    construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(UsuarioEmpleadoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            if("Crear_Salario".equals(tipoAccionRecorrido)) {
                listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("¿Esta completamente seguro(a) de seleccionar este empleado(a)?")
                            .setTitle("Confirmar Usuario.");

                    construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VistaSalarioCrear();
                        }
                    });

                    construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(UsuarioEmpleadoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            MostrarUsuarioEmpleados(tokenGuardado,  null, false);

        } catch (Exception error) {
            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
            listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

            listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
            listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información de los empleados(as) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                if("PermisoTiempo".equals(tipoRegresoRecorrido)) {
                    Intent intentPermisoTiempo = new Intent(UsuarioEmpleadoActivity.this, PermisoTiempoCrearActivity.class);

                    startActivity(intentPermisoTiempo);

                    finish();
                }

                if("Salario".equals(tipoRegresoRecorrido)) {
                    Intent intentControlSalario = new Intent(UsuarioEmpleadoActivity.this, ControlSalarioCrearActivity.class);

                    startActivity(intentControlSalario);

                    finish();
                }
            }
        });
    }

    private void BuscarPrioridad(String tokenUsuario, List<ExtensionEmpleadoUsuarioEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionEmpleadoUsuarioEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarUsuarioEmpleados(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionEmpleadoUsuarioEntitie empleadoUsuarioEntitie : listaDatos) {
                    String nombreEmpleado = empleadoUsuarioEntitie.getNombre_Empleado().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(empleadoUsuarioEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el empleado(a) debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarUsuarioEmpleados(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarUsuarioEmpleados(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
            listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

            listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
            listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información del empleado(a) en estos momentos debido a un problema técnico. Por favor intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void MostrarUsuarioEmpleados(String tokenUsuario, List<ExtensionEmpleadoUsuarioEntitie> listaActualizada, Boolean autorizacion) {

        empleadoInterface = ConexionAPI.Conexion_API_Empleado(this);

        Call<List<ExtensionEmpleadoUsuarioEntitie>> mostrarEmpleados = empleadoInterface.obtenerEmpleados(tokenUsuario);


        mostrarEmpleados.enqueue(new Callback<List<ExtensionEmpleadoUsuarioEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionEmpleadoUsuarioEntitie>> call, Response<List<ExtensionEmpleadoUsuarioEntitie>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size() == 1) {
                        listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            ExtensionEmpleadoUsuarioEntitie empleados = response.body().get(i);
                            String Nombre  = empleados.getNombre_Empleado().trim();
                            String Apellidos = empleados.getApellido1_Empleado().trim() + " " + empleados.getApellido2_Empleado().trim();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Departamento = empleados.getDepartamento().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();


                            listaUsuarioEmpleadosBinding.txtNombreUsuarioEmpleado.setText(Nombre);
                            listaUsuarioEmpleadosBinding.txtApellidosUsuarioEmpleado.setText(Apellidos);
                            listaUsuarioEmpleadosBinding.txtCedulaUsuarioEmpleado.setText(Cedula);
                            listaUsuarioEmpleadosBinding.txtDepartamentoUsuarioEmpleado.setText(Departamento);
                            listaUsuarioEmpleadosBinding.txtCorreoUsuarioEmpleado.setText(Correo_Electronico);
                            listaUsuarioEmpleadosBinding.txtRolUsuarioEmpleado.setText(Nombre_Rol);

                            listaUsuarioEmpleadosBinding.btnSeleccionDatoUsuarioEmpleado.setTag(empleados);
                            listaUsuarioEmpleadosBinding.btnSeleccionDatoUsuarioEmpleado.setVisibility(VISIBLE);
                        }
                    }

                    listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.removeAllViews();
                    listaUsuarioEmpleadosBinding.tbrPrimeraFilaUsuarioEmpleado.setVisibility(GONE);
                    listaUsuarioEmpleadosBinding.btnSeleccionDatoUsuarioEmpleado.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionEmpleadoUsuarioEntitie>() {
                        @Override
                        public int compare(ExtensionEmpleadoUsuarioEntitie o1, ExtensionEmpleadoUsuarioEntitie o2) {
                            return o1.getNombre_Empleado().compareToIgnoreCase(o2.getNombre_Empleado());
                        }
                    });


                    if(autorizacion != false) {
                        listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(UsuarioEmpleadoActivity.this);
                            nuevaFila.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(UsuarioEmpleadoActivity.this);
                            campoNombre = new TextView(UsuarioEmpleadoActivity.this);
                            campoApellidos = new TextView(UsuarioEmpleadoActivity.this);
                            campoCedula = new TextView(UsuarioEmpleadoActivity.this);
                            campoDepartamento = new TextView(UsuarioEmpleadoActivity.this);
                            campoCorreoElectronico = new TextView(UsuarioEmpleadoActivity.this);
                            campoNombreRol = new TextView(UsuarioEmpleadoActivity.this);


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            LargoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            AnchoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);


                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            parametrosCheckBox = new TableRow.LayoutParams(LargoCheckBox, AnchoCheckBox);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            margenCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            parametrosContenido.setMarginStart(margenContenido);
                            parametrosCheckBox.setMarginStart(margenCheckBox);
                            parametrosCheckBox.setMarginEnd(margenCheckBox);


                            ExtensionEmpleadoUsuarioEntitie empleados = listaActualizada.get(i);
                            String Nombre  = empleados.getNombre_Empleado().trim();
                            String Apellidos = empleados.getApellido1_Empleado().trim() + " " + empleados.getApellido2_Empleado().trim();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Departamento = empleados.getDepartamento().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();


                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(empleados);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoCorreoElectronico.setText(Correo_Electronico);
                            campoCorreoElectronico.setWidth(LargoContenido);
                            campoCorreoElectronico.setHeight(AnchoContenido);
                            campoCorreoElectronico.setLayoutParams(parametrosContenido);
                            campoCorreoElectronico.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreoElectronico.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreoElectronico.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreoElectronico.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCorreoElectronico.setTextColor(Color.BLACK);
                            campoCorreoElectronico.setTextSize(TamañoLetraContenido);

                            campoNombreRol.setText(Nombre_Rol);
                            campoNombreRol.setWidth(LargoContenido);
                            campoNombreRol.setHeight(AnchoContenido);
                            campoNombreRol.setLayoutParams(parametrosContenido);
                            campoNombreRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombreRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombreRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombreRol.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombreRol.setTextColor(Color.BLACK);
                            campoNombreRol.setTextSize(TamañoLetraContenido);

                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoCorreoElectronico);
                            nuevaFila.addView(campoNombreRol);
                            listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.addView(nuevaFila);
                        }

                    } else {
                        listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(UsuarioEmpleadoActivity.this);
                            nuevaFila.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(UsuarioEmpleadoActivity.this);
                            campoNombre = new TextView(UsuarioEmpleadoActivity.this);
                            campoApellidos = new TextView(UsuarioEmpleadoActivity.this);
                            campoCedula = new TextView(UsuarioEmpleadoActivity.this);
                            campoDepartamento = new TextView(UsuarioEmpleadoActivity.this);
                            campoCorreoElectronico = new TextView(UsuarioEmpleadoActivity.this);
                            campoNombreRol = new TextView(UsuarioEmpleadoActivity.this);


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            LargoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            AnchoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);


                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            parametrosCheckBox = new TableRow.LayoutParams(LargoCheckBox, AnchoCheckBox);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            margenCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            parametrosContenido.setMarginStart(margenContenido);
                            parametrosCheckBox.setMarginStart(margenCheckBox);
                            parametrosCheckBox.setMarginEnd(margenCheckBox);


                            ExtensionEmpleadoUsuarioEntitie empleados = datosOrdenados.get(i);
                            String Nombre  = empleados.getNombre_Empleado().trim();
                            String Apellidos = empleados.getApellido1_Empleado().trim() + " " + empleados.getApellido2_Empleado().trim();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Departamento = empleados.getDepartamento().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();


                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(empleados);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoCorreoElectronico.setText(Correo_Electronico);
                            campoCorreoElectronico.setWidth(LargoContenido);
                            campoCorreoElectronico.setHeight(AnchoContenido);
                            campoCorreoElectronico.setLayoutParams(parametrosContenido);
                            campoCorreoElectronico.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreoElectronico.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreoElectronico.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreoElectronico.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCorreoElectronico.setTextColor(Color.BLACK);
                            campoCorreoElectronico.setTextSize(TamañoLetraContenido);

                            campoNombreRol.setText(Nombre_Rol);
                            campoNombreRol.setWidth(LargoContenido);
                            campoNombreRol.setHeight(AnchoContenido);
                            campoNombreRol.setLayoutParams(parametrosContenido);
                            campoNombreRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombreRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombreRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombreRol.setBackground(UsuarioEmpleadoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombreRol.setTextColor(Color.BLACK);
                            campoNombreRol.setTextSize(TamañoLetraContenido);

                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoCorreoElectronico);
                            nuevaFila.addView(campoNombreRol);
                            listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
                        listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

                        listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
                        listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

                        listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
                        listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

                        listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información de los empleados(as) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
                        listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

                        listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
                        listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

                        listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
                        listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

                        listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible visualizar la información de los empleados(as) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
            public void onFailure(Call<List<ExtensionEmpleadoUsuarioEntitie>> call, Throwable t) {
                listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
                listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

                listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
                listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

                listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
                listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

                listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible visualizar la información de los empleados(as) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }
        });
    }

    private Integer ConvertirPropiedades(int tipoPropiedad, int tamañoPropiedad) {
        int resultado = 0;

        if (tipoPropiedad == TypedValue.COMPLEX_UNIT_DIP) {
            resultado = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tamañoPropiedad,
                    getResources().getDisplayMetrics());
        }

        if(tipoPropiedad == TypedValue.COMPLEX_UNIT_SP) {
            resultado = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tamañoPropiedad,
                    getResources().getDisplayMetrics());
        }

        return resultado;
    }

    private void VistaPermisoTiempoCrear() {
        try {
            Integer cantidadChecks = 0;
            ExtensionEmpleadoUsuarioEntitie datoSeleccionado = null;

            for(int i = 0; i < listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionEmpleadoUsuarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Nombre = datoSeleccionado.getNombre_Empleado().trim();
                String Apellidos = datoSeleccionado.getApellido1_Empleado().trim() + " " + datoSeleccionado.getApellido2_Empleado().trim();

                String Cedula = datoSeleccionado.getCedula_Empleado().trim();
                String Departamento = datoSeleccionado.getDepartamento().trim();


                Intent intentPermisoTiempoCrear = new Intent(UsuarioEmpleadoActivity.this, PermisoTiempoCrearActivity.class);

                intentPermisoTiempoCrear.putExtra("Nombre", Nombre);
                intentPermisoTiempoCrear.putExtra("Apellidos", Apellidos);
                intentPermisoTiempoCrear.putExtra("Cedula", Cedula);
                intentPermisoTiempoCrear.putExtra("Departamento", Departamento);

                startActivity(intentPermisoTiempoCrear);

                finish();
            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible seleccionar el empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
            listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

            listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
            listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible seleccionar el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void VistaSalarioCrear() {
        try {
            Integer cantidadChecks = 0;
            ExtensionEmpleadoUsuarioEntitie datoSeleccionado = null;

            for(int i = 0; i < listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaUsuarioEmpleadosBinding.tblTablaUsuarioEmpleado.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionEmpleadoUsuarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Nombre = datoSeleccionado.getNombre_Empleado().trim();
                String Apellidos = datoSeleccionado.getApellido1_Empleado().trim() + " " + datoSeleccionado.getApellido2_Empleado().trim();

                String Edad = datoSeleccionado.getEdad_Empleado().toString().trim();
                String Cedula = datoSeleccionado.getCedula_Empleado().trim();
                String Departamento = datoSeleccionado.getDepartamento().trim();


                Intent intentSalarioCrear = new Intent(UsuarioEmpleadoActivity.this, ControlSalarioCrearActivity.class);

                intentSalarioCrear.putExtra("Nombre", Nombre);
                intentSalarioCrear.putExtra("Apellidos", Apellidos);
                intentSalarioCrear.putExtra("Edad", Edad);
                intentSalarioCrear.putExtra("Cedula", Cedula);
                intentSalarioCrear.putExtra("Departamento", Departamento);

                startActivity(intentSalarioCrear);

                finish();
            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible seleccionar el empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            listaUsuarioEmpleadosBinding.svBuscarUsuarioEmpleado.setVisibility(GONE);
            listaUsuarioEmpleadosBinding.txtTituloListaUsuarioEmpleado.setVisibility(View.GONE);

            listaUsuarioEmpleadosBinding.hsvScrollHorizontalUsuarioEmpleado.setVisibility(View.GONE);
            listaUsuarioEmpleadosBinding.btnConfirmarUsuarioEmpleado.setVisibility(GONE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setVisibility(VISIBLE);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setVisibility(VISIBLE);

            listaUsuarioEmpleadosBinding.imgFotoUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioEmpleadosBinding.txtMensajeUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(UsuarioEmpleadoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible seleccionar el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}
