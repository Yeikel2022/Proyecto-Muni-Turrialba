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
import com.proyectotcu.muniturrialba.databinding.ActivityListaSalariosInfoBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteSalarioInfoActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityListaSalariosInfoBinding listaSalarioInfoBinding;

    //Variables globales para esta clase.
    TextView campoNombre, campoApellidos, campoEdad, campoCedula, campoDepartamento, campoFechaEntrega,
             campoSalario, campoDescripcion;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
            margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;

    TableRow.LayoutParams parametrosCheckBox, parametrosContenido;

    String tipoAccionRecorrido, tipoRegresoRecorrido;
    TableRow nuevaFila;
    CheckBox campoCheckBox;

    List<ExtensionSalarioEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionSalarioEntitie> Lista_Tabla = new ArrayList<>();


    //Interfaz que contiene los métodos de la entidad FAQ.
    SalarioInterface salarioInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listaSalarioInfoBinding = ActivityListaSalariosInfoBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(listaSalarioInfoBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_InfoSalarioLista), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(GONE);
        listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            tipoAccionRecorrido = getIntent().getStringExtra("Tipo_De_Accion");
            tipoRegresoRecorrido = getIntent().getStringExtra("Regresar");

            SharedPreferences archivoXML = this.getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    listaSalarioInfoBinding.svBuscarInfoSalarioLista.clearFocus();
                    return true;
                }
            });

            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    listaSalarioInfoBinding.svBuscarInfoSalarioLista.clearFocus();
                    listaSalarioInfoBinding.svBuscarInfoSalarioLista.setIconifiedByDefault(true);
                    return false;
                }
            });

            if("Generar_ReporteSalario".equals(tipoAccionRecorrido)) {
                listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
                            Toast.makeText(ReporteSalarioInfoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            if("Editar_ReporteSalario".equals(tipoAccionRecorrido)) {
                listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
                            Toast.makeText(ReporteSalarioInfoActivity.this, "¡Cancelado!", Toast.LENGTH_LONG).show();
                        }
                    });


                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                });
            }

            MostrarReportesSalario(tokenGuardado, null, false);

        } catch (Exception error) {
            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
            listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
            listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
                if("ReporteSalarioGenerar".equals(tipoRegresoRecorrido)) {
                    Intent intentRegreso = new Intent(ReporteSalarioInfoActivity.this, ReporteSalarioGenerarActivity.class);

                    /* Si regresa a: ReporteUsuarioGenerarActivity, quiere decir que el usuario no quiso añadir otro -
                     * dato, por lo que entonces se indica que la autorización estaria en falso para que muestre los -
                     * datos que el usuario selecciono originalmente. */
                    ReporteSalarioGenerarActivity.AutorizacionSalario = false;

                    startActivity(intentRegreso);

                    finish();
                }

                if("ReporteSalarioEditar".equals(tipoRegresoRecorrido)) {
                    Intent intentRegreso = new Intent(ReporteSalarioInfoActivity.this, ReporteSalarioEditarActivity.class);

                    /* Si regresa a: ReporteUsuarioGenerarActivity, quiere decir que el usuario no quiso añadir otro -
                     * dato, por lo que entonces se indica que la autorización estaria en falso para que muestre los -
                     * datos que el usuario selecciono originalmente. */
                    ReporteSalarioEditarActivity.AutorizacionSalarioEditar = false;

                    startActivity(intentRegreso);

                    finish();
                }
            }
        });
    }


    private void BuscarPrioridad(String tokenUsuario, List<ExtensionSalarioEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionSalarioEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarReportesSalario(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionSalarioEntitie salarioEntitie : listaDatos) {
                    String nombreEmpleado = salarioEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(salarioEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el salario debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarReportesSalario(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarReportesSalario(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
            listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
            listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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


    private void MostrarReportesSalario(String tokenUsuario, List<ExtensionSalarioEntitie> listaActualizada, Boolean autorizacion) {

        salarioInterface = ConexionAPI.Conexion_API_Salario(this);
        Call<List<ExtensionSalarioEntitie>> mostrarSalarios = salarioInterface.obtenerSalarios(tokenUsuario);

        mostrarSalarios.enqueue(new Callback<List<ExtensionSalarioEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionSalarioEntitie>> call, Response<List<ExtensionSalarioEntitie>> response) {
                if (response.isSuccessful()) {
                    listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.removeAllViews();
                    listaSalarioInfoBinding.tbrPrimeraFilaContenidoInfoSalariosLista.setVisibility(GONE);
                    listaSalarioInfoBinding.btnSeleccionDatoInfoSalariosLista.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionSalarioEntitie>() {
                        @Override
                        public int compare(ExtensionSalarioEntitie o1, ExtensionSalarioEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(ReporteSalarioInfoActivity.this);
                            nuevaFila.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReporteSalarioInfoActivity.this);
                            campoNombre = new TextView(ReporteSalarioInfoActivity.this);
                            campoApellidos = new TextView(ReporteSalarioInfoActivity.this);
                            campoEdad = new TextView(ReporteSalarioInfoActivity.this);
                            campoCedula = new TextView(ReporteSalarioInfoActivity.this);
                            campoDepartamento = new TextView(ReporteSalarioInfoActivity.this);
                            campoFechaEntrega = new TextView(ReporteSalarioInfoActivity.this);
                            campoSalario = new TextView(ReporteSalarioInfoActivity.this);
                            campoDescripcion = new TextView(ReporteSalarioInfoActivity.this);


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


                            ExtensionSalarioEntitie salarioEntitie = listaActualizada.get(i);
                            String Nombre  = salarioEntitie.getNombre().trim();
                            String Apellidos = salarioEntitie.getApellido_1().trim() + " " + salarioEntitie.getApellido_2().trim();
                            String Edad = salarioEntitie.getEdad().toString().trim();
                            String Cedula = salarioEntitie.getCedula().trim();
                            String Departamento = salarioEntitie.getDepartamento().trim();
                            String FechaEntrega = salarioEntitie.getFechaEntrega().trim().replace("T", " ");
                            String Salario = new BigDecimal(salarioEntitie.getSalario().toString().trim()).toPlainString();
                            String Descripcion = salarioEntitie.getDescripcion().trim();

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(salarioEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoEdad.setText(Edad);
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.addView(nuevaFila);
                        }

                    } else {
                        listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(ReporteSalarioInfoActivity.this);
                            nuevaFila.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(ReporteSalarioInfoActivity.this);
                            campoNombre = new TextView(ReporteSalarioInfoActivity.this);
                            campoApellidos = new TextView(ReporteSalarioInfoActivity.this);
                            campoEdad = new TextView(ReporteSalarioInfoActivity.this);
                            campoCedula = new TextView(ReporteSalarioInfoActivity.this);
                            campoDepartamento = new TextView(ReporteSalarioInfoActivity.this);
                            campoFechaEntrega = new TextView(ReporteSalarioInfoActivity.this);
                            campoSalario = new TextView(ReporteSalarioInfoActivity.this);
                            campoDescripcion = new TextView(ReporteSalarioInfoActivity.this);


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


                            ExtensionSalarioEntitie salarioEntitie = datosOrdenados.get(i);
                            String Nombre  = salarioEntitie.getNombre().trim();
                            String Apellidos = salarioEntitie.getApellido_1().trim() + " " + salarioEntitie.getApellido_2().trim();
                            String Edad = salarioEntitie.getEdad().toString().trim();
                            String Cedula = salarioEntitie.getCedula().trim();
                            String Departamento = salarioEntitie.getDepartamento().trim();
                            String FechaEntrega = salarioEntitie.getFechaEntrega().trim().replace("T", " ");
                            String Salario = new BigDecimal(salarioEntitie.getSalario().toString().trim()).toPlainString();
                            String Descripcion = salarioEntitie.getDescripcion().trim();

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(salarioEntitie);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoEdad.setText(Edad);
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(ReporteSalarioInfoActivity.this.getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
                        listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

                        listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
                        listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

                        listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
                        listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

                        listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
                        listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

                        listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
                        listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

                        listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
                        listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

                        listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                        listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
            public void onFailure(Call<List<ExtensionSalarioEntitie>> call, Throwable t) {
                listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
                listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

                listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
                listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

                listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
                listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

                listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
                listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
            ExtensionSalarioEntitie datoSeleccionado = null;

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_Tabla", el cual contiene la lista con los nuevos datos que el usuario selecciono. */
            for (int i = 0; i < listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionSalarioEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }


            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteSalarioGenerar = new Intent(ReporteSalarioInfoActivity.this, ReporteSalarioGenerarActivity.class);

                /* Luego de eso, simplemente se llevaria la nueva lista a: ReporteUsuarioGenerarActivity, para que el usuario, -
                 * pueda ver los nuevos datos.
                 *
                 * Además, también se manda un true en su autorización para que así en: ReporteUsuarioGenerarActivity, pueda -
                 * realizar el procedimiento que corresponde para mostrar la nueva lista. */
                intentReporteSalarioGenerar.putParcelableArrayListExtra("Tabla_ReportesSalarios_Guardado", Lista_Tabla);

                ReporteSalarioGenerarActivity.AutorizacionSalario = true;

                startActivity(intentReporteSalarioGenerar);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible añadir otro registro al reporte debido a que no se selecciono ningún dato.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch(Exception error) {
            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
            listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
            listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
            ExtensionSalarioEntitie datoSeleccionado = null;

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_Tabla", el cual contiene la lista con los nuevos datos que el usuario selecciono. */
            for (int i = 0; i < listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) listaSalarioInfoBinding.tblTablaContenidoInfoSalariosLista.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionSalarioEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }


            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteSalarioEditar = new Intent(ReporteSalarioInfoActivity.this, ReporteSalarioEditarActivity.class);

                /* Luego de eso, simplemente se llevaria la nueva lista a: ReporteUsuarioGenerarActivity, para que el usuario, -
                 * pueda ver los nuevos datos.
                 *
                 * Además, también se manda un true en su autorización para que así en: ReporteUsuarioGenerarActivity, pueda -
                 * realizar el procedimiento que corresponde para mostrar la nueva lista. */
                intentReporteSalarioEditar.putParcelableArrayListExtra("ListaDatos_DocumentoPDF_Seleccionado", Lista_Tabla);

                ReporteSalarioEditarActivity.AutorizacionSalarioEditar = true;

                startActivity(intentReporteSalarioEditar);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible añadir otro registro al reporte debido a que no se selecciono ningún dato.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch(Exception error) {
            listaSalarioInfoBinding.svBuscarInfoSalarioLista.setVisibility(GONE);
            listaSalarioInfoBinding.txtTituloInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.hsvScrollHorizontalInfoSalariosLista.setVisibility(GONE);
            listaSalarioInfoBinding.btnConfirmarInfoSalarioLista.setVisibility(GONE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setVisibility(VISIBLE);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setVisibility(VISIBLE);

            listaSalarioInfoBinding.imgFotoInfoSalarioLista.setImageResource(R.drawable.icono_contenido_no_disponible);
            listaSalarioInfoBinding.txtMensajeInfoSalarioLista.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReporteSalarioInfoActivity.this);
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
