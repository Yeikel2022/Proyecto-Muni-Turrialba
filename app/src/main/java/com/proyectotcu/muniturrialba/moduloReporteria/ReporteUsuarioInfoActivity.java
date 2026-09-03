package com.proyectotcu.muniturrialba.moduloReporteria;

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
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityListaUsuariosInfoBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionInicioSesionEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.InicioSesionInterface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteUsuarioInfoActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityListaUsuariosInfoBinding listaUsuarioInfoBinding;

    //Variables globales para esta clase.
    TextView campoNombre, campoApellidos, campoCedula, campoCorreo, campoDepartamento, campoRol, campoFechaCreacion,
             campoFechaInicioSesion, campoUltimaConexion;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
            margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;

    TableRow.LayoutParams parametrosCheckBox, parametrosContenido;

    TableRow nuevaFila;
    SearchView buscadorReportesUsuarios;
    CheckBox campoCheckBox;

    List<ExtensionInicioSesionEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionInicioSesionEntitie> Lista_Tabla = new ArrayList<>();


    //Interfaz que contiene los métodos de la entidad FAQ.
    InicioSesionInterface inicioSesionInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listaUsuarioInfoBinding = ActivityListaUsuariosInfoBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(listaUsuarioInfoBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_InfoUsuarioLista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(GONE);
        listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = this.getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                        buscadorReportesUsuarios.clearFocus();
                        return true;
                    }
                });

            listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                        buscadorReportesUsuarios.clearFocus();
                        buscadorReportesUsuarios.setIconifiedByDefault(true);
                        return false;
                    }
                });

            listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setOnClickListener(v -> VistaConfirmarRegistro());

            MostrarReportesUsuario(tokenGuardado, null, false);

        } catch (Exception error) {
            listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
            listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
            listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intentRegreso = new Intent(ReporteUsuarioInfoActivity.this, ReporteUsuarioGenerarActivity.class);

                /* Si regresa a: ReporteUsuarioGenerarActivity, quiere decir que el usuario no quiso añadir otro -
                 * dato, por lo que entonces se indica que la autorización estaria en falso para que muestre los -
                 * datos que el usuario selecciono originalmente. */
                ReporteUsuarioGenerarActivity.Autorizacion = false;

                startActivity(intentRegreso);

                finish();
            }
        });
    }


    private void BuscarPrioridad(String tokenUsuario, List<ExtensionInicioSesionEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionInicioSesionEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarReportesUsuario(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionInicioSesionEntitie inicioSesionEntitie : listaDatos) {
                    String nombreEmpleado = inicioSesionEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(inicioSesionEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el reporte de usuario debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarReportesUsuario(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarReportesUsuario(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
            listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
            listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
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


    private void MostrarReportesUsuario(String tokenUsuario, List<ExtensionInicioSesionEntitie> listaActualizada, Boolean autorizacion) {

        inicioSesionInterface = ConexionAPI.Conexion_API_Inicio_Sesion(this);
        Call<List<ExtensionInicioSesionEntitie>> mostrarUsuarios = inicioSesionInterface.obtenerUsuarios(tokenUsuario);

        mostrarUsuarios.enqueue(new Callback<List<ExtensionInicioSesionEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionInicioSesionEntitie>> call, Response<List<ExtensionInicioSesionEntitie>> response) {
                if (response.isSuccessful()) {
                    listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.removeAllViews();
                    listaUsuarioInfoBinding.tbrPrimeraFilaContenidoInfoUsuariosLista.setVisibility(GONE);
                    listaUsuarioInfoBinding.btnSeleccionDatoInfoUsuariosLista.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionInicioSesionEntitie>() {
                        @Override
                        public int compare(ExtensionInicioSesionEntitie o1, ExtensionInicioSesionEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(ReporteUsuarioInfoActivity.this);
                            nuevaFila.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReporteUsuarioInfoActivity.this);
                            campoNombre = new TextView(ReporteUsuarioInfoActivity.this);
                            campoApellidos = new TextView(ReporteUsuarioInfoActivity.this);
                            campoCedula = new TextView(ReporteUsuarioInfoActivity.this);
                            campoCorreo = new TextView(ReporteUsuarioInfoActivity.this);
                            campoDepartamento = new TextView(ReporteUsuarioInfoActivity.this);
                            campoRol = new TextView(ReporteUsuarioInfoActivity.this);
                            campoFechaCreacion = new TextView(ReporteUsuarioInfoActivity.this);
                            campoFechaInicioSesion = new TextView(ReporteUsuarioInfoActivity.this);
                            campoUltimaConexion = new TextView(ReporteUsuarioInfoActivity.this);


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 180);
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


                            ExtensionInicioSesionEntitie inicioSesionEntitie = listaActualizada.get(i);
                            String Nombre  = inicioSesionEntitie.getNombre().trim();
                            String Apellidos = inicioSesionEntitie.getApellido_1().trim() + " " + inicioSesionEntitie.getApellido_2().trim();
                            String Cedula = inicioSesionEntitie.getCedula().trim();
                            String Correo = inicioSesionEntitie.getCorreo_Electronico().trim();
                            String Departamento = inicioSesionEntitie.getDepartamento().trim();
                            String NombreRol = inicioSesionEntitie.getNombre_Rol().trim();
                            String FechaCreacion = inicioSesionEntitie.getFecha_Creacion().trim().replace("T", " ");
                            String FechaInicioSesion = inicioSesionEntitie.getFecha_Inicio_Sesion().trim().substring(0, 10);
                            String UltimaConexion = inicioSesionEntitie.getUltima_Conexion().trim().replace("T", " ");

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(inicioSesionEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoCorreo.setText(Correo);
                            campoCorreo.setWidth(LargoContenido);
                            campoCorreo.setHeight(AnchoContenido);
                            campoCorreo.setLayoutParams(parametrosContenido);
                            campoCorreo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCorreo.setTextColor(Color.BLACK);
                            campoCorreo.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoRol.setText(NombreRol);
                            campoRol.setWidth(LargoContenido);
                            campoRol.setHeight(AnchoContenido);
                            campoRol.setLayoutParams(parametrosContenido);
                            campoRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRol.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoRol.setTextColor(Color.BLACK);
                            campoRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(FechaCreacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

                            campoFechaInicioSesion.setText(FechaInicioSesion);
                            campoFechaInicioSesion.setWidth(LargoContenido);
                            campoFechaInicioSesion.setHeight(AnchoContenido);
                            campoFechaInicioSesion.setLayoutParams(parametrosContenido);
                            campoFechaInicioSesion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaInicioSesion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaInicioSesion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaInicioSesion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaInicioSesion.setTextColor(Color.BLACK);
                            campoFechaInicioSesion.setTextSize(TamañoLetraContenido);

                            campoUltimaConexion.setText(UltimaConexion);
                            campoUltimaConexion.setWidth(LargoContenido);
                            campoUltimaConexion.setHeight(AnchoContenido);
                            campoUltimaConexion.setLayoutParams(parametrosContenido);
                            campoUltimaConexion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoUltimaConexion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoUltimaConexion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoUltimaConexion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoUltimaConexion.setTextColor(Color.BLACK);
                            campoUltimaConexion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoCorreo);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoRol);
                            nuevaFila.addView(campoFechaCreacion);
                            nuevaFila.addView(campoFechaInicioSesion);
                            nuevaFila.addView(campoUltimaConexion);
                            listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.addView(nuevaFila);
                        }

                    } else {
                        listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(ReporteUsuarioInfoActivity.this);
                            nuevaFila.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReporteUsuarioInfoActivity.this);
                            campoNombre = new TextView(ReporteUsuarioInfoActivity.this);
                            campoApellidos = new TextView(ReporteUsuarioInfoActivity.this);
                            campoCedula = new TextView(ReporteUsuarioInfoActivity.this);
                            campoCorreo = new TextView(ReporteUsuarioInfoActivity.this);
                            campoDepartamento = new TextView(ReporteUsuarioInfoActivity.this);
                            campoRol = new TextView(ReporteUsuarioInfoActivity.this);
                            campoFechaCreacion = new TextView(ReporteUsuarioInfoActivity.this);
                            campoFechaInicioSesion = new TextView(ReporteUsuarioInfoActivity.this);
                            campoUltimaConexion = new TextView(ReporteUsuarioInfoActivity.this);


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 180);
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


                            ExtensionInicioSesionEntitie inicioSesionEntitie = datosOrdenados.get(i);
                            String Nombre  = inicioSesionEntitie.getNombre().trim();
                            String Apellidos = inicioSesionEntitie.getApellido_1().trim() + " " + inicioSesionEntitie.getApellido_2().trim();
                            String Cedula = inicioSesionEntitie.getCedula().trim();
                            String Correo = inicioSesionEntitie.getCorreo_Electronico().trim();
                            String Departamento = inicioSesionEntitie.getDepartamento().trim();
                            String NombreRol = inicioSesionEntitie.getNombre_Rol().trim();
                            String FechaCreacion = inicioSesionEntitie.getFecha_Creacion().trim().replace("T", " ");
                            String FechaInicioSesion = inicioSesionEntitie.getFecha_Inicio_Sesion().trim().substring(0, 10);
                            String UltimaConexion = inicioSesionEntitie.getUltima_Conexion().trim().replace("T", " ");

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(inicioSesionEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoCorreo.setText(Correo);
                            campoCorreo.setWidth(LargoContenido);
                            campoCorreo.setHeight(AnchoContenido);
                            campoCorreo.setLayoutParams(parametrosContenido);
                            campoCorreo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCorreo.setTextColor(Color.BLACK);
                            campoCorreo.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoRol.setText(NombreRol);
                            campoRol.setWidth(LargoContenido);
                            campoRol.setHeight(AnchoContenido);
                            campoRol.setLayoutParams(parametrosContenido);
                            campoRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRol.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoRol.setTextColor(Color.BLACK);
                            campoRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(FechaCreacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

                            campoFechaInicioSesion.setText(FechaInicioSesion);
                            campoFechaInicioSesion.setWidth(LargoContenido);
                            campoFechaInicioSesion.setHeight(AnchoContenido);
                            campoFechaInicioSesion.setLayoutParams(parametrosContenido);
                            campoFechaInicioSesion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaInicioSesion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaInicioSesion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaInicioSesion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaInicioSesion.setTextColor(Color.BLACK);
                            campoFechaInicioSesion.setTextSize(TamañoLetraContenido);

                            campoUltimaConexion.setText(UltimaConexion);
                            campoUltimaConexion.setWidth(LargoContenido);
                            campoUltimaConexion.setHeight(AnchoContenido);
                            campoUltimaConexion.setLayoutParams(parametrosContenido);
                            campoUltimaConexion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoUltimaConexion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoUltimaConexion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoUltimaConexion.setBackground(ReporteUsuarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoUltimaConexion.setTextColor(Color.BLACK);
                            campoUltimaConexion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoCorreo);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoRol);
                            nuevaFila.addView(campoFechaCreacion);
                            nuevaFila.addView(campoFechaInicioSesion);
                            nuevaFila.addView(campoUltimaConexion);
                            listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
                        listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

                        listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
                        listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

                        listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
                        listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

                        listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
                        listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

                        listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
                        listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

                        listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
                        listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

                        listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
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
            }


            @Override
            public void onFailure(Call<List<ExtensionInicioSesionEntitie>> call, Throwable t) {
                listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
                listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

                listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
                listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

                listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
                listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

                listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible visualizar la información en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    private void VistaConfirmarRegistro() {
        try {
            Integer cantidadChecks = 0;
            ExtensionInicioSesionEntitie datoSeleccionado = null;

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_Tabla", el cual contiene la lista con los nuevos datos que el usuario selecciono. */
            for (int i = 0; i < listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaUsuarioInfoBinding.tblTablaContenidoInfoUsuariosLista.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionInicioSesionEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }


            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteUsuarioGenerar = new Intent(ReporteUsuarioInfoActivity.this, ReporteUsuarioGenerarActivity.class);

                /* Luego de eso, simplemente se llevaria la nueva lista a: ReporteUsuarioGenerarActivity, para que el usuario, -
                 * pueda ver los nuevos datos.
                 *
                 * Además, también se manda un true en su autorización para que así en: ReporteUsuarioGenerarActivity, pueda -
                 * realizar el procedimiento que corresponde para mostrar la nueva lista. */
                intentReporteUsuarioGenerar.putParcelableArrayListExtra("Tabla_ReportesUsuarios_Guardado", Lista_Tabla);

                ReporteUsuarioGenerarActivity.Autorizacion = true;

                startActivity(intentReporteUsuarioGenerar);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible generar el reporte de usuario debido a que no se selecciono ningún dato.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch(Exception error) {
            listaUsuarioInfoBinding.svBuscarInfoUsuarioLista.setVisibility(GONE);
            listaUsuarioInfoBinding.txtTituloInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.hsvScrollHorizontalInfoUsuariosLista.setVisibility(GONE);
            listaUsuarioInfoBinding.btnConfirmarInfoUsuarioLista.setVisibility(GONE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setVisibility(VISIBLE);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setVisibility(VISIBLE);

            listaUsuarioInfoBinding.imgFotoInfoUsuarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaUsuarioInfoBinding.txtMensajeInfoUsuarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteUsuarioInfoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible generar el reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}
