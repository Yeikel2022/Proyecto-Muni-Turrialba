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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityListaPermisosTiempoInfoBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionPermisoTiempoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoTiempoInterface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportePermisoTiempoInfoActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityListaPermisosTiempoInfoBinding listaPermisoTiempoInfoBinding;

    //Variables globales para esta clase.
    TextView campoNombre, campoApellidos, campoCedula, campoDepartamento, campoTipoPermiso, campoDescripcion,
             campoFechaAsignacion, campoFechaFinalizacion;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
            margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;

    TableRow.LayoutParams parametrosCheckBox, parametrosContenido;

    String tipoAccionRecorrido, tipoRegresoRecorrido;
    TableRow nuevaFila;
    CheckBox campoCheckBox;

    List<ExtensionPermisoTiempoEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionPermisoTiempoEntitie> Lista_Tabla = new ArrayList<>();


    //Interfaz que contiene los métodos de la entidad FAQ.
    PermisoTiempoInterface permisoTiempoInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listaPermisoTiempoInfoBinding = ActivityListaPermisosTiempoInfoBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(listaPermisoTiempoInfoBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_InfoPermisoTiempoLista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(GONE);
        listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            tipoAccionRecorrido = getIntent().getStringExtra("Tipo_De_Accion");
            tipoRegresoRecorrido = getIntent().getStringExtra("Regresar");

            SharedPreferences archivoXML = this.getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.clearFocus();
                    return true;
                }
            });

            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.clearFocus();
                    listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setIconifiedByDefault(true);
                    return false;
                }
            });

            if("Generar_ReporteTiempo".equals(tipoAccionRecorrido)) {
                listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("¿Esta completamente seguro(a) de añadir estos registros extras?")
                            .setTitle("Confirmar Registro Extra.");

                    construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VistaConfirmarRegistroGenerar();
                        }
                    });

                    construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ReportePermisoTiempoInfoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            if("Editar_ReporteTiempo".equals(tipoAccionRecorrido)) {
                listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("¿Esta completamente seguro(a) de añadir estos registros extras?")
                            .setTitle("Confirmar Registro Extra.");

                    construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            VistaConfirmarRegistroEditar();
                        }
                    });

                    construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ReportePermisoTiempoInfoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            MostrarReportesTiempo(tokenGuardado, null, false);

        } catch (Exception error) {
            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
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
                if("ReporteTiempoGenerar".equals(tipoRegresoRecorrido)) {
                    Intent intentRegreso = new Intent(ReportePermisoTiempoInfoActivity.this, ReportePermisoTiempoGenerarActivity.class);

                    /* Si regresa a: ReporteUsuarioGenerarActivity, quiere decir que el usuario no quiso añadir otro -
                     * dato, por lo que entonces se indica que la autorización estaria en falso para que muestre los -
                     * datos que el usuario selecciono originalmente. */
                    ReportePermisoTiempoGenerarActivity.AutorizacionTiempo = false;

                    startActivity(intentRegreso);

                    finish();
                }

                if("ReporteTiempoEditar".equals(tipoRegresoRecorrido)) {
                    Intent intentRegreso = new Intent(ReportePermisoTiempoInfoActivity.this, ReportePermisoTiempoEditarActivity.class);

                    /* Si regresa a: ReporteUsuarioGenerarActivity, quiere decir que el usuario no quiso añadir otro -
                     * dato, por lo que entonces se indica que la autorización estaria en falso para que muestre los -
                     * datos que el usuario selecciono originalmente. */
                    ReportePermisoTiempoEditarActivity.AutorizacionTiempoEditar = false;

                    startActivity(intentRegreso);

                    finish();
                }
            }
        });
    }


    private void BuscarPrioridad(String tokenUsuario, List<ExtensionPermisoTiempoEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionPermisoTiempoEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarReportesTiempo(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionPermisoTiempoEntitie permisoTiempoEntitie : listaDatos) {
                    String nombreEmpleado = permisoTiempoEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(permisoTiempoEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el permiso de tiempo debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarReportesTiempo(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarReportesTiempo(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
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


    private void MostrarReportesTiempo(String tokenUsuario, List<ExtensionPermisoTiempoEntitie> listaActualizada, Boolean autorizacion) {

        permisoTiempoInterface = ConexionAPI.Conexion_API_Permiso_Tiempo(this);
        Call<List<ExtensionPermisoTiempoEntitie>> mostrarPermisosTiempo = permisoTiempoInterface.obtenerPermisosTiempo(tokenUsuario);

        mostrarPermisosTiempo.enqueue(new Callback<List<ExtensionPermisoTiempoEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionPermisoTiempoEntitie>> call, Response<List<ExtensionPermisoTiempoEntitie>> response) {
                if (response.isSuccessful()) {
                    listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.removeAllViews();
                    listaPermisoTiempoInfoBinding.tbrPrimeraFilaContenidoInfoPermisosTiempoLista.setVisibility(GONE);
                    listaPermisoTiempoInfoBinding.btnSeleccionDatoInfoPermisosTiempoLista.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionPermisoTiempoEntitie>() {
                        @Override
                        public int compare(ExtensionPermisoTiempoEntitie o1, ExtensionPermisoTiempoEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(ReportePermisoTiempoInfoActivity.this);
                            nuevaFila.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReportePermisoTiempoInfoActivity.this);
                            campoNombre = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoApellidos = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoCedula = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoDepartamento = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoTipoPermiso = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoDescripcion = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoFechaAsignacion = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoFechaFinalizacion = new TextView(ReportePermisoTiempoInfoActivity.this);


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


                            ExtensionPermisoTiempoEntitie permisoTiempoEntitie = listaActualizada.get(i);
                            String Nombre = permisoTiempoEntitie.getNombre().trim();
                            String Apellidos = permisoTiempoEntitie.getApellido_1().trim() + " " + permisoTiempoEntitie.getApellido_2().trim();
                            String Cedula = permisoTiempoEntitie.getCedula().trim();
                            String Departamento = permisoTiempoEntitie.getDepartamento().trim();
                            String TipoPermiso = permisoTiempoEntitie.getTipoPermiso().trim();
                            String Descripcion = permisoTiempoEntitie.getDescripcion().trim();
                            String FechaAsignacion = permisoTiempoEntitie.getFechaAsignacion().trim().replace("T", " ");
                            String FechaFinalizacion = permisoTiempoEntitie.getFechaFinalizacion().trim().replace("T", " ");

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(permisoTiempoEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoTipoPermiso.setText(TipoPermiso);
                            campoTipoPermiso.setWidth(LargoContenido);
                            campoTipoPermiso.setHeight(AnchoContenido);
                            campoTipoPermiso.setLayoutParams(parametrosContenido);
                            campoTipoPermiso.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTipoPermiso.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTipoPermiso.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTipoPermiso.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoTipoPermiso.setTextColor(Color.BLACK);
                            campoTipoPermiso.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);

                            campoFechaAsignacion.setText(FechaAsignacion);
                            campoFechaAsignacion.setWidth(LargoContenido);
                            campoFechaAsignacion.setHeight(AnchoContenido);
                            campoFechaAsignacion.setLayoutParams(parametrosContenido);
                            campoFechaAsignacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaAsignacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaAsignacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaAsignacion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaAsignacion.setTextColor(Color.BLACK);
                            campoFechaAsignacion.setTextSize(TamañoLetraContenido);

                            campoFechaFinalizacion.setText(FechaFinalizacion);
                            campoFechaFinalizacion.setWidth(LargoContenido);
                            campoFechaFinalizacion.setHeight(AnchoContenido);
                            campoFechaFinalizacion.setLayoutParams(parametrosContenido);
                            campoFechaFinalizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaFinalizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaFinalizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaFinalizacion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaFinalizacion.setTextColor(Color.BLACK);
                            campoFechaFinalizacion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoTipoPermiso);
                            nuevaFila.addView(campoDescripcion);
                            nuevaFila.addView(campoFechaAsignacion);
                            nuevaFila.addView(campoFechaFinalizacion);
                            listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.addView(nuevaFila);
                        }

                    } else {
                        listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(ReportePermisoTiempoInfoActivity.this);
                            nuevaFila.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReportePermisoTiempoInfoActivity.this);
                            campoNombre = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoApellidos = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoCedula = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoDepartamento = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoTipoPermiso = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoDescripcion = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoFechaAsignacion = new TextView(ReportePermisoTiempoInfoActivity.this);
                            campoFechaFinalizacion = new TextView(ReportePermisoTiempoInfoActivity.this);


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


                            ExtensionPermisoTiempoEntitie permisoTiempoEntitie = datosOrdenados.get(i);
                            String Nombre = permisoTiempoEntitie.getNombre().trim();
                            String Apellidos = permisoTiempoEntitie.getApellido_1().trim() + " " + permisoTiempoEntitie.getApellido_2().trim();
                            String Cedula = permisoTiempoEntitie.getCedula().trim();
                            String Departamento = permisoTiempoEntitie.getDepartamento().trim();
                            String TipoPermiso = permisoTiempoEntitie.getTipoPermiso().trim();
                            String Descripcion = permisoTiempoEntitie.getDescripcion().trim();
                            String FechaAsignacion = permisoTiempoEntitie.getFechaAsignacion().trim().replace("T", " ");
                            String FechaFinalizacion = permisoTiempoEntitie.getFechaFinalizacion().trim().replace("T", " ");

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(permisoTiempoEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoTipoPermiso.setText(TipoPermiso);
                            campoTipoPermiso.setWidth(LargoContenido);
                            campoTipoPermiso.setHeight(AnchoContenido);
                            campoTipoPermiso.setLayoutParams(parametrosContenido);
                            campoTipoPermiso.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTipoPermiso.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTipoPermiso.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTipoPermiso.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoTipoPermiso.setTextColor(Color.BLACK);
                            campoTipoPermiso.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);

                            campoFechaAsignacion.setText(FechaAsignacion);
                            campoFechaAsignacion.setWidth(LargoContenido);
                            campoFechaAsignacion.setHeight(AnchoContenido);
                            campoFechaAsignacion.setLayoutParams(parametrosContenido);
                            campoFechaAsignacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaAsignacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaAsignacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaAsignacion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaAsignacion.setTextColor(Color.BLACK);
                            campoFechaAsignacion.setTextSize(TamañoLetraContenido);

                            campoFechaFinalizacion.setText(FechaFinalizacion);
                            campoFechaFinalizacion.setWidth(LargoContenido);
                            campoFechaFinalizacion.setHeight(AnchoContenido);
                            campoFechaFinalizacion.setLayoutParams(parametrosContenido);
                            campoFechaFinalizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaFinalizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaFinalizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaFinalizacion.setBackground(ReportePermisoTiempoInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaFinalizacion.setTextColor(Color.BLACK);
                            campoFechaFinalizacion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoTipoPermiso);
                            nuevaFila.addView(campoDescripcion);
                            nuevaFila.addView(campoFechaAsignacion);
                            nuevaFila.addView(campoFechaFinalizacion);
                            listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
                        listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

                        listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
                        listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

                        listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
                        listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

                        listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
                        listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

                        listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
                        listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

                        listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
                        listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

                        listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
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
            public void onFailure(Call<List<ExtensionPermisoTiempoEntitie>> call, Throwable t) {
                listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
                listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

                listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
                listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

                listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
                listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

                listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
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


    private void VistaConfirmarRegistroGenerar() {
        try {
            Integer cantidadChecks = 0;
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_Tabla", el cual contiene la lista con los nuevos datos que el usuario selecciono. */
            for (int i = 0; i < listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }


            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteTiempoGenerar = new Intent(ReportePermisoTiempoInfoActivity.this, ReportePermisoTiempoGenerarActivity.class);

                /* Luego de eso, simplemente se llevaria la nueva lista a: ReporteUsuarioGenerarActivity, para que el usuario, -
                 * pueda ver los nuevos datos.
                 *
                 * Además, también se manda un true en su autorización para que así en: ReporteUsuarioGenerarActivity, pueda -
                 * realizar el procedimiento que corresponde para mostrar la nueva lista. */
                intentReporteTiempoGenerar.putParcelableArrayListExtra("Tabla_ReportesTiempo_Guardado", Lista_Tabla);

                ReportePermisoTiempoGenerarActivity.AutorizacionTiempo = true;

                startActivity(intentReporteTiempoGenerar);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible añadir otro registro al reporte debido a que no se selecciono ningún dato.")
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
            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible añadir otro registro al reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void VistaConfirmarRegistroEditar() {
        try {
            Integer cantidadChecks = 0;
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_Tabla", el cual contiene la lista con los nuevos datos que el usuario selecciono. */
            for (int i = 0; i < listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaPermisoTiempoInfoBinding.tblTablaContenidoInfoPermisosTiempoLista.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }


            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteTiempoEditar = new Intent(ReportePermisoTiempoInfoActivity.this, ReportePermisoTiempoEditarActivity.class);

                /* Luego de eso, simplemente se llevaria la nueva lista a: ReporteUsuarioGenerarActivity, para que el usuario, -
                 * pueda ver los nuevos datos.
                 *
                 * Además, también se manda un true en su autorización para que así en: ReporteUsuarioGenerarActivity, pueda -
                 * realizar el procedimiento que corresponde para mostrar la nueva lista. */
                intentReporteTiempoEditar.putParcelableArrayListExtra("ListaDatos_DocumentoPDF_Seleccionado", Lista_Tabla);

                ReportePermisoTiempoEditarActivity.AutorizacionTiempoEditar = true;

                startActivity(intentReporteTiempoEditar);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible añadir otro registro al reporte debido a que no se selecciono ningún dato.")
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
            listaPermisoTiempoInfoBinding.svBuscarInfoPermisoTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.txtTituloInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.hsvScrollHorizontalInfoPermisosTiempoLista.setVisibility(GONE);
            listaPermisoTiempoInfoBinding.btnConfirmarInfoPermisoTiempoLista.setVisibility(GONE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setVisibility(VISIBLE);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setVisibility(VISIBLE);

            listaPermisoTiempoInfoBinding.imgFotoInfoPermisoTiempoLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaPermisoTiempoInfoBinding.txtMensajeInfoPermisoTiempoLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoInfoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible añadir otro registro al reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}
