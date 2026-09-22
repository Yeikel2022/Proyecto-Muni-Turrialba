package com.proyectotcu.muniturrialba.moduloReporteria;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityReportePermisosTiempoGenerarBinding;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionPermisoTiempoEntitie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class ReportePermisoTiempoGenerarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityReportePermisosTiempoGenerarBinding reporteTiempoGenerarBinding;

    //Variables globales:
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido,
            margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingEndContenido;

    TextView campoNombreGuardado, campoApellidosGuardado, campoCedulaGuardado, campoDepartamentoGuardado,
             campoTipoPermisoGuardado, campoDescripcionGuardado, campoFechaAsignacionGuardado, campoFechaFinalizacionGuardado;

    TableRow.LayoutParams parametrosContenido, parametrosCheckBox;

    TableRow filaGuardada;
    CheckBox campoCheckBoxGuardado;
    Gson gson = new Gson();

    ArrayList<ExtensionPermisoTiempoEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionPermisoTiempoEntitie> tablaRecorrida = new ArrayList<>();
    ArrayList<ExtensionPermisoTiempoEntitie> Lista_PermisosTiempo = new ArrayList<>();

    private static String listaTemporal;
    protected static boolean AutorizacionTiempo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reporteTiempoGenerarBinding = ActivityReportePermisosTiempoGenerarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(reporteTiempoGenerarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ReportesTiempo_Generar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setVisibility(GONE);
        reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setVisibility(GONE);
        reporteTiempoGenerarBinding.tbrPrimeraFilaReporteTiempo.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            //Obtiene la posible lista que fue enviada, ya sea desde ReporteUsuarioFragment o ReporteUsuarioInfoActivity.
            tablaRecorrida = getIntent().getParcelableArrayListExtra("Tabla_ReportesTiempo_Guardado");

            /* Si resulta que la tabla recorrida tiene un nulo como respuesta, eso quiere decir que, por parte de la clase: -
             * ReporteUsuarioInfoActivity, no se confirmo otro registro sobre los inicios de sesión de los usuarios. Por lo -
             * que entonces se colocaria en pantalla la lista original que el usuario habia hecho previamente.
             *
             * NOTA: Esto se hace para evitar que en la siguiente validación, la variable: tablaRecorrida genere un error, porque -
             * puede contener un nulo si no recibe nada por parte del intent. */
            if(tablaRecorrida == null) {
                Type listaParseada = new TypeToken<ArrayList<ExtensionPermisoTiempoEntitie>>(){}.getType();
                ArrayList<ExtensionPermisoTiempoEntitie> tablaOriginal = gson.fromJson(listaTemporal, listaParseada);

                datosOrdenados = tablaOriginal;

            } else {
                /* Aqui significa que si la variable: Autorizacion contiene un true como respuesta, entonces quiere decir que -
                 * el usuario habia confirmado con añadir otro registro a la lista, y este mismo llego a la variable: tablaRecorrida, -
                 * de forma que se realiza un parseo para obtener la lista previa y añadirla a esos nuevos datos, teniendo de esta -
                 * manera, la lista con los datos actualizados. */
                if(AutorizacionTiempo != false) {
                    Type listaParseada = new TypeToken<ArrayList<ExtensionPermisoTiempoEntitie>>(){}.getType();
                    ArrayList<ExtensionPermisoTiempoEntitie> tablaActualizada = gson.fromJson(listaTemporal, listaParseada);

                    for(int i = 0; i < tablaActualizada.size(); i++) {
                        tablaRecorrida.add(tablaActualizada.get(i));
                    }
                }

                datosOrdenados = tablaRecorrida;
            }


            //Aqui ordena los datos de forma alfabetica:
            datosOrdenados.sort(new Comparator<ExtensionPermisoTiempoEntitie>() {
                @Override
                public int compare(ExtensionPermisoTiempoEntitie o1, ExtensionPermisoTiempoEntitie o2) {
                    return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                }
            });

            //Aqui se encarga de mostrar los datos que el usuario selecciono:
            for (ExtensionPermisoTiempoEntitie extensionPermisoTiempoEntitie : datosOrdenados) {
                filaGuardada = new TableRow(ReportePermisoTiempoGenerarActivity.this);
                filaGuardada.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table));
                campoCheckBoxGuardado = new CheckBox(ReportePermisoTiempoGenerarActivity.this);
                campoNombreGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoApellidosGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoCedulaGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoDepartamentoGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoTipoPermisoGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoDescripcionGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoFechaAsignacionGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);
                campoFechaFinalizacionGuardado = new TextView(ReportePermisoTiempoGenerarActivity.this);


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


                String Nombre = extensionPermisoTiempoEntitie.getNombre().trim();
                String Apellidos = extensionPermisoTiempoEntitie.getApellido_1().trim() + " " + extensionPermisoTiempoEntitie.getApellido_2().trim();
                String Cedula = extensionPermisoTiempoEntitie.getCedula().trim();
                String Departamento = extensionPermisoTiempoEntitie.getDepartamento().trim();
                String TipoPermiso = extensionPermisoTiempoEntitie.getTipoPermiso().trim();
                String Descripcion = extensionPermisoTiempoEntitie.getDescripcion().trim();
                String FechaAsignacion = extensionPermisoTiempoEntitie.getFechaAsignacion().trim().replace("T", " ");
                String FechaFinalizacion = extensionPermisoTiempoEntitie.getFechaFinalizacion().trim().replace("T", " ");


                campoCheckBoxGuardado.setChecked(true);
                campoCheckBoxGuardado.setWidth(LargoCheckBox);
                campoCheckBoxGuardado.setHeight(AnchoCheckBox);
                campoCheckBoxGuardado.setLayoutParams(parametrosCheckBox);
                campoCheckBoxGuardado.setTop(margenTop);
                campoCheckBoxGuardado.setPaddingRelative(0, paddingTopContenido, 0, 0);
                campoCheckBoxGuardado.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                campoCheckBoxGuardado.setTag(extensionPermisoTiempoEntitie);

                campoNombreGuardado.setText(Nombre);
                campoNombreGuardado.setWidth(LargoContenido);
                campoNombreGuardado.setHeight(AnchoContenido);
                campoNombreGuardado.setLayoutParams(parametrosContenido);
                campoNombreGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoNombreGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoNombreGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoNombreGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoNombreGuardado.setTextColor(Color.BLACK);
                campoNombreGuardado.setTextSize(TamañoLetraContenido);

                campoApellidosGuardado.setText(Apellidos);
                campoApellidosGuardado.setWidth(LargoContenido);
                campoApellidosGuardado.setHeight(AnchoContenido);
                campoApellidosGuardado.setLayoutParams(parametrosContenido);
                campoApellidosGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoApellidosGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoApellidosGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoApellidosGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoApellidosGuardado.setTextColor(Color.BLACK);
                campoApellidosGuardado.setTextSize(TamañoLetraContenido);

                campoCedulaGuardado.setText(Cedula);
                campoCedulaGuardado.setWidth(LargoContenido);
                campoCedulaGuardado.setHeight(AnchoContenido);
                campoCedulaGuardado.setLayoutParams(parametrosContenido);
                campoCedulaGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoCedulaGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoCedulaGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoCedulaGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoCedulaGuardado.setTextColor(Color.BLACK);
                campoCedulaGuardado.setTextSize(TamañoLetraContenido);

                campoDepartamentoGuardado.setText(Departamento);
                campoDepartamentoGuardado.setWidth(LargoContenido);
                campoDepartamentoGuardado.setHeight(AnchoContenido);
                campoDepartamentoGuardado.setLayoutParams(parametrosContenido);
                campoDepartamentoGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoDepartamentoGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoDepartamentoGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoDepartamentoGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoDepartamentoGuardado.setTextColor(Color.BLACK);
                campoDepartamentoGuardado.setTextSize(TamañoLetraContenido);

                campoTipoPermisoGuardado.setText(TipoPermiso);
                campoTipoPermisoGuardado.setWidth(LargoContenido);
                campoTipoPermisoGuardado.setHeight(AnchoContenido);
                campoTipoPermisoGuardado.setLayoutParams(parametrosContenido);
                campoTipoPermisoGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoTipoPermisoGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoTipoPermisoGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoTipoPermisoGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoTipoPermisoGuardado.setTextColor(Color.BLACK);
                campoTipoPermisoGuardado.setTextSize(TamañoLetraContenido);

                campoDescripcionGuardado.setText(Descripcion);
                campoDescripcionGuardado.setWidth(LargoContenido);
                campoDescripcionGuardado.setHeight(AnchoContenido);
                campoDescripcionGuardado.setLayoutParams(parametrosContenido);
                campoDescripcionGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoDescripcionGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoDescripcionGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoDescripcionGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoDescripcionGuardado.setTextColor(Color.BLACK);
                campoDescripcionGuardado.setTextSize(TamañoLetraContenido);

                campoFechaAsignacionGuardado.setText(FechaAsignacion);
                campoFechaAsignacionGuardado.setWidth(LargoContenido);
                campoFechaAsignacionGuardado.setHeight(AnchoContenido);
                campoFechaAsignacionGuardado.setLayoutParams(parametrosContenido);
                campoFechaAsignacionGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoFechaAsignacionGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoFechaAsignacionGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoFechaAsignacionGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoFechaAsignacionGuardado.setTextColor(Color.BLACK);
                campoFechaAsignacionGuardado.setTextSize(TamañoLetraContenido);

                campoFechaFinalizacionGuardado.setText(FechaFinalizacion);
                campoFechaFinalizacionGuardado.setWidth(LargoContenido);
                campoFechaFinalizacionGuardado.setHeight(AnchoContenido);
                campoFechaFinalizacionGuardado.setLayoutParams(parametrosContenido);
                campoFechaFinalizacionGuardado.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoFechaFinalizacionGuardado.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoFechaFinalizacionGuardado.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoFechaFinalizacionGuardado.setBackground(ReportePermisoTiempoGenerarActivity.this.getDrawable(R.drawable.border_table_row));
                campoFechaFinalizacionGuardado.setTextColor(Color.BLACK);
                campoFechaFinalizacionGuardado.setTextSize(TamañoLetraContenido);


                filaGuardada.addView(campoCheckBoxGuardado);
                filaGuardada.addView(campoNombreGuardado);
                filaGuardada.addView(campoApellidosGuardado);
                filaGuardada.addView(campoCedulaGuardado);
                filaGuardada.addView(campoDepartamentoGuardado);
                filaGuardada.addView(campoTipoPermisoGuardado);
                filaGuardada.addView(campoDescripcionGuardado);
                filaGuardada.addView(campoFechaAsignacionGuardado);
                filaGuardada.addView(campoFechaFinalizacionGuardado);

                reporteTiempoGenerarBinding.tblTablaReporteTiempo.addView(filaGuardada);
            }


            reporteTiempoGenerarBinding.btnSeleccionarPermisoTiempoExtra.setOnClickListener(v -> VistaOtroRegistro());
            reporteTiempoGenerarBinding.btnGenerarReporteTiempo.setOnClickListener(v -> GenerarNuevoReporte());

        } catch (Exception error) {
            reporteTiempoGenerarBinding.txtTituloGenerarTiempo.setVisibility(GONE);
            reporteTiempoGenerarBinding.hsvScrollHorizontalReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.btnSeleccionarPermisoTiempoExtra.setVisibility(GONE);
            reporteTiempoGenerarBinding.btnGenerarReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setVisibility(VISIBLE);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setVisibility(VISIBLE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setImageResource(R.drawable.icono_contenido_no_disponible);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoGenerarActivity.this);
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
                //Esto se hace para evitar problemas, además de ser una buena práctica.
                if(tablaRecorrida != null) {
                    tablaRecorrida.clear();
                }

                if(datosOrdenados != null) {
                    datosOrdenados.clear();
                }

                if(Lista_PermisosTiempo != null) {
                    Lista_PermisosTiempo.clear();
                }

                listaTemporal = null;
                AutorizacionTiempo = false;

                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentGenerarReporte = new Intent(ReportePermisoTiempoGenerarActivity.this, ReporteriaActivity.class);

                intentGenerarReporte.putExtra("Seccion_A_Mostrar", "Permisos_Tiempo");

                //Le indica que ejecute el hipervinculo.
                startActivity(intentGenerarReporte);

                /* Sirve para evitar que el usuario se regrese después.
                 * Esto por temas de buenas prácticas. */
                finish();
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


    private void GenerarNuevoReporte() {
        try {
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;

            //En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: "Lista_Usuarios".
            for(int i = 0; i < reporteTiempoGenerarBinding.tblTablaReporteTiempo.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) reporteTiempoGenerarBinding.tblTablaReporteTiempo.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                    Lista_PermisosTiempo.add(datoSeleccionado);
                }
            }


            /* Una vez que llega aqui, se empezara a configurar y crear un documento PDF con los datos seleccionados.
             * Eso si, dicho documento seria de forma temporal, esto porque el usuario debe tener la opción de si querer -
             * descargar ese documento o no. */
            if(datoSeleccionado != null) {

                /* |=============================| Creación de variables: |=============================| */
                String Nombre = null;
                String Apellidos = null;
                String Cedula = null;
                String Departamento = null;
                String TipoPermiso = null;
                String Descripcion = null;
                String FechaAsignacion = null;
                String FechaFinalizacion = null;

                //Medidas que están relacionadas a las hojas (osea las páginas) del documento PDF.
                int largoPagina = 1315;
                int alturaPagina = 842;
                int numeroPagina = 1;

                //Medidas que están relacionadas a las celdas de las tablas que están en el documento PDF.
                int alturaCeldaFija = 100;
                int alturaCeldaActual = 185;
                int anchoCelda = 186;
                int columnas = 7;

                /* Medidas que están relacionadas a las posiciones que estarian las tablas y la información -
                 * dentro del documento PDF. */

                /* Esta medida está relacionada principalmente para saber si es necesario crear nuevas páginas en el -
                 * documento, esto por medio de una suma constante que involucra a las variables: "posicionY_Actual" -
                 * y "alturaCelda", esto para poder determinar en una condición, si es necesario realizar otra página -
                 * para colocar la información restante. Además se utiliza también para que la tabla y su información -
                 * puedan estar en orden respectivamente.
                 *
                 * NOTA: Si bien se coloca esta suma: "posicionY_Actual += alturaCelda" después de cada for, esta -
                 * no serviria de nada sin esta validación: "posicionY_Actual + alturaCelda > alturaPagina" (y lo -
                 * mismo seria al reves), por lo que esos dos códigos son importantes para que el metodo pueda saber -
                 * cuando es necesario crear una nueva hoja o no respectivamente. */
                int posicionY_Actual = 5;

                /* Esta medida está relacionada a la posición de la tabla, tal como la variable: "margenY_Superior", -
                 * solo que este se enfoca a la parte izquierda de la hoja al cual se va a situar la primera fila, -
                 * y de ahí las demás con la información respectiva.
                 *
                 * Además, a diferencia de la variable: "margenY_Superior", este es tomanda en cuenta en TODOS los -
                 * procesos para colocar y ordenar la información, tanto para los encabezados, como de los datos que -
                 * el usuario haya seleccionado previamente. */
                int margenX_Izquierdo = 5;

                /* Esta medida está relacionada a la posición de la tabla, más especificamente, este sirve para la primera -
                 * fila de dicha tabla (osea los encabezados), sirviendo como un punto de partida para que la variable: -
                 * "posicionY_Actual", pueda tener una referencia para colocar ordenadamente la información. Además, se -
                 * coloco un 5 para que estuviera lo más cercano a la parte de arriba de la hoja al cual se va a situar. */
                int margenY_Superior = 5;


                ByteArrayOutputStream lectorArchivo = new ByteArrayOutputStream();
                PdfDocument reportePDF = new PdfDocument();


                /* |=============================| Creación de las páginas del documento PDF: |=============================| */

                //Configuración de la primera página:
                PdfDocument.PageInfo configuracionPaginaIntroductoria = new PdfDocument.PageInfo.Builder(largoPagina, alturaPagina, 1).create();
                PdfDocument.Page paginaIntroductoria = reportePDF.startPage(configuracionPaginaIntroductoria);

                Canvas constructorGraficoIntroductoria = paginaIntroductoria.getCanvas();
                TextPaint textoDescriptivo = new TextPaint();
                Paint brochaTitulo = new Paint();

                brochaTitulo.setTextSize(42);
                textoDescriptivo.setTextSize(32);

                //Esto es para indicarle el tipo de letra y si quiere agregarle un estilo como una negrita, cursiva, etc.
                brochaTitulo.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
                constructorGraficoIntroductoria.drawText("Reporte de Tiempo", 480, 200, brochaTitulo);

                String descripcion = "Saludos estimados(as). \nEste documento corresponde a un reporte sobre los permisos de tiempo que son pertene- cientes a " +
                        "los empleados(as) que utilizan la aplicación móvil de la municipalidad de Turri- alba, los cuales están desglosados a partir de las siguientes tablas.";

                StaticLayout plantillaIntroductoria = StaticLayout.Builder
                        .obtain(descripcion, 0, descripcion.length(), textoDescriptivo, largoPagina)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL).build();

                constructorGraficoIntroductoria.save();

                /* Esto sirve para poder indicar, donde se debe de mover el objeto en los ejes: (X, Y), -
                 * respectivamente. En este caso, se le esta colocando este comando para que la descripción -
                 * y el titulo principal sepan donde tienen que dirigirse en la primera página. */
                constructorGraficoIntroductoria.translate(25, 300);
                plantillaIntroductoria.draw(constructorGraficoIntroductoria);
                constructorGraficoIntroductoria.restore();
                reportePDF.finishPage(paginaIntroductoria);


                //Configuración de la segunda página:
                PdfDocument.PageInfo configuracionPagina = new PdfDocument.PageInfo.Builder(largoPagina, alturaPagina, 2).create();
                PdfDocument.Page pagina = reportePDF.startPage(configuracionPagina);

                Canvas constructorGrafico = pagina.getCanvas();
                TextPaint brochaContenido = new TextPaint();
                Paint brochaEncabezado = new Paint();
                Paint brochaBorde = new Paint();

                brochaContenido.setColor(Color.BLACK);
                brochaContenido.setTextSize(21);

                brochaEncabezado.setColor(Color.GREEN);
                brochaEncabezado.setStyle(Paint.Style.FILL);

                brochaBorde.setColor(Color.BLACK);
                brochaBorde.setStyle(Paint.Style.STROKE);
                brochaBorde.setStrokeWidth(5);

                String[] Titulos = { "Nombre Apellido:", "Cédula:", "Departamento:", "Tipo Permiso:", "Descripción:",
                        "Fecha Asignación:", "Fecha Finalización:" };

                //Este for se encarga de colocar los encabezados dentro de la primera tabla.
                for(int a = 0; a < columnas; a++) {
                    int izquierdaEncabezado = margenX_Izquierdo + a * anchoCelda;
                    int derechaEncabezado = izquierdaEncabezado + anchoCelda;
                    int arribaEncabezado = margenY_Superior;
                    int abajoEncabezado = arribaEncabezado + alturaCeldaFija;

                    constructorGrafico.drawRect(izquierdaEncabezado, arribaEncabezado, derechaEncabezado, abajoEncabezado, brochaEncabezado);
                    constructorGrafico.drawRect(izquierdaEncabezado, arribaEncabezado, derechaEncabezado, abajoEncabezado, brochaBorde);

                    StaticLayout configuracionTitulo = StaticLayout.Builder.
                            obtain(Titulos[a], 0, Titulos[a].length(), brochaContenido, anchoCelda - 20).
                            setAlignment(Layout.Alignment.ALIGN_CENTER).build();

                    constructorGrafico.save();

                    /* Esto sirve para poder indicar, donde se debe de mover el objeto en los ejes: (X, Y), -
                     * respectivamente. En este caso, se le esta colocando este comando para que los titulos -
                     * sepan donde tienen que dirigirse, los cuales, deberian estar en las celdas que son de -
                     * color verde. */
                    constructorGrafico.translate(izquierdaEncabezado + 10, arribaEncabezado + 20);
                    configuracionTitulo.draw(constructorGrafico);
                    constructorGrafico.restore();
                }

                posicionY_Actual += alturaCeldaActual;

                /* Este for se encarga de obtener todos los datos que selecciono el usuario -
                 * que están dentro de una lista, para que así se puedan colocar dentro de -
                 * una tabla con otro for respectivamente. */
                for(int b = 0; b < Lista_PermisosTiempo.size(); b++) {
                    ExtensionPermisoTiempoEntitie registro = Lista_PermisosTiempo.get(b);

                    Nombre = registro.getNombre().trim();
                    Apellidos = registro.getApellido_1().trim() + " " + registro.getApellido_2().trim();
                    Cedula = registro.getCedula().trim();

                    Departamento = registro.getDepartamento().trim();
                    TipoPermiso = registro.getTipoPermiso().trim();
                    Descripcion = registro.getDescripcion().trim();

                    FechaAsignacion = registro.getFechaAsignacion().trim().replace("T", " ");
                    FechaFinalizacion = registro.getFechaFinalizacion().trim().replace("T", " ");

                    String[] Datos = {
                            Nombre + " " + Apellidos, Cedula, Departamento, TipoPermiso, Descripcion, FechaAsignacion,
                            FechaFinalizacion
                    };

                    /* Esta validación sirve para saber, si la posición actual de Y (que esta -
                     * relacionada aqui con la información), es mayor a la altura de la página, -
                     * quiere decir que se necesita hacer otra página para poder seguir colocando -
                     * la información que hace falta respectivamente. Además, la razón por la cual -
                     * la variable: "posicionY_Actual", esta sumando otra vez la altura de la celda, -
                     * es porque dicha suma permite evitar que la información pueda sobrepasarse del -
                     * limite de una página. Por ejemplo:
                     *
                     * Digamos que el usuario ha selecionado 30 datos sobre los inicios de sesión y presiono el botón de confirmar, -
                     * en ese momento, el sistema haria el proceso con total normalidad. Sin embargo, si no contara con que la posicion -
                     * Y actual sume la altura de la celda, y solo fuera esta validación: "posicionY_Actual > alturaPagina", la aplicación -
                     * seguiria colocando los datos en las tablas y en un punto, los datos restantes se estarian colocando fuera de los limites -
                     * que contenia dicha página (o hoja), haciendo que se pierda esa información. Por esa razón, es que es necesario sumar otra -
                     * vez la posición Y actual con la altura de la celda respectivamente. */
                    if(posicionY_Actual + alturaCeldaActual > alturaPagina) {
                        reportePDF.finishPage(pagina);
                        numeroPagina++;

                        configuracionPagina = new PdfDocument.PageInfo.Builder(largoPagina, alturaPagina, numeroPagina).create();
                        pagina = reportePDF.startPage(configuracionPagina);
                        constructorGrafico = pagina.getCanvas();

                        /* La razón por la cual la posicion Y actual guarda la medida que tiene el margen Y superior -
                         * es para poder resetearlo y evitar que vuelva a colocar una fila con los encabezados de una -
                         * tabla respectivamente. */
                        posicionY_Actual = margenY_Superior;

                        //Este for se encarga de colocar nuevamente los encabezados dentro de una tabla.
                        for(int c = 0; c < columnas; c++) {
                            int izquierdaEncabezado = margenX_Izquierdo + c * anchoCelda;
                            int derechaEncabezado = izquierdaEncabezado + anchoCelda;
                            int arribaEncabezado = posicionY_Actual;
                            int abajoEncabezado = arribaEncabezado + alturaCeldaFija;

                            constructorGrafico.drawRect(izquierdaEncabezado, arribaEncabezado, derechaEncabezado, abajoEncabezado, brochaEncabezado);
                            constructorGrafico.drawRect(izquierdaEncabezado, arribaEncabezado, derechaEncabezado, abajoEncabezado, brochaBorde);

                            StaticLayout configuracionTitulo = StaticLayout.Builder.
                                    obtain(Titulos[c], 0, Titulos[c].length(), brochaContenido, anchoCelda - 20).
                                    setAlignment(Layout.Alignment.ALIGN_CENTER).build();

                            constructorGrafico.save();

                            /* Esto sirve para poder indicar, donde se debe de mover el objeto en los ejes: (X, Y), -
                             * respectivamente. En este caso, se le esta colocando este comando para que los titulos -
                             * sepan donde tienen que dirigirse, los cuales, deberian estar en las celdas que son de -
                             * color verde. */
                            constructorGrafico.translate(izquierdaEncabezado + 10, arribaEncabezado + 20);
                            configuracionTitulo.draw(constructorGrafico);
                            constructorGrafico.restore();
                        }

                        posicionY_Actual += alturaCeldaActual;
                    }

                    /* Este for se encarga de colocar todos los datos que selecciono el usuario -
                     * dentro de la tabla que se esta creando. */
                    for(int d = 0; d < Datos.length; d++) {
                        int izquierda = margenX_Izquierdo + d * anchoCelda;
                        int arriba = posicionY_Actual - 82;
                        int derecha = izquierda + anchoCelda;
                        int abajo = arriba + alturaCeldaActual;

                        constructorGrafico.drawRect(izquierda, arriba, derecha, abajo, brochaBorde);

                        StaticLayout configuracionTexto = StaticLayout.Builder.
                                obtain(Datos[d], 0, Datos[d].length(), brochaContenido, anchoCelda - 20).
                                setAlignment(Layout.Alignment.ALIGN_NORMAL).build();

                        constructorGrafico.save();

                        /* Esto sirve para poder indicar, donde se debe de mover el objeto en los ejes: (X, Y), -
                         * respectivamente. En este caso, se le esta colocando este comando para que los datos que -
                         * habia seleccionado el usuario(a) sepan donde tienen que dirigirse, los cuales, deberian -
                         * estar en las celdas que son de color blanco. */
                        constructorGrafico.translate(izquierda + 10, arriba + 10);
                        configuracionTexto.draw(constructorGrafico);
                        constructorGrafico.restore();
                    }

                    posicionY_Actual += alturaCeldaActual;
                }


                reportePDF.finishPage(pagina);
                reportePDF.writeTo(lectorArchivo);
                reportePDF.close();


                /* |=============================| Creación del documento PDF de forma temporal: |=============================| */
                byte[] bytesArchivoPDF = lectorArchivo.toByteArray();
                int numero = ReportePermisoTiempoFragment.contadorNumeroReporteTiempo += 1;

                /* Aqui lo que se esta haciendo es crear un archivo en blanco para poder almacenar, los datos del -
                 * archivo PDF. Además, esto se esta colocando dentro del cache del proyecto (osea la aplicación), -
                 * para así tenerlo almacenado de forma temporal respectivamente. */
                File archivoPDF = new File(getCacheDir(), "Temporal-Reporte-PermisosTiempo-MuniTurrialba-" + numero + ".pdf");

                /* Aqui lo que se esta haciendo es crear un lector para poder acceder a dicho archivo que se creo -
                 * anteriormente. Y luego de eso, con dicho lector se podra colocar la información (que son los -
                 * bytes) sobre el documento PDF. Además se cierra para evitar problemas y por buenas prácticas. */
                FileOutputStream lectorArchivoTemporal = new FileOutputStream(archivoPDF);
                lectorArchivoTemporal.write(bytesArchivoPDF);
                lectorArchivoTemporal.close();

                /* Aqui lo que se esta haciendo es crear un URI que nos servira para poder acceder de forma segura y temporal -
                 * al documento PDF, esto por medio del comando: FileProvider.getUriForFile, el cual nos permitira acceder a -
                 * la ruta exacta de donde esta el archivo que contiene ese documento PDF.
                 *
                 * NOTA: Ahora, cabe aclarar que el getPackageName() + ".provider" es para tener el permiso y el acceso a esa ruta, -
                 * el cual funciona gracias a la configuración que se realizo en el manifest llamado: <provider>. Además, es -
                 * necesario crear un archivo XML para colocar las rutas, en este caso para el cache. Ahora, si resulta que -
                 * uno de esos dos no esta, entonces no se podria realizar este proceso respectivamente. */
                Uri uriGenerado = FileProvider.getUriForFile(this, getPackageName() + ".provider", archivoPDF);

                /* Luego de eso, simplemente se usa un intent para llevar ese URI hacia a: ReporteUsuarioFragment. */
                Intent intentGenerarReporte = new Intent(ReportePermisoTiempoGenerarActivity.this, ReporteriaActivity.class);

                intentGenerarReporte.putExtra("Documento_PDF", uriGenerado.toString());
                intentGenerarReporte.putParcelableArrayListExtra("DocumentoPDF_Y_ListaDatos", Lista_PermisosTiempo);
                intentGenerarReporte.putExtra("Seccion_A_Mostrar", "Permisos_Tiempo");

                //Esto se hace para evitar problemas, además de ser una buena práctica.
                if(tablaRecorrida != null) {
                    tablaRecorrida.clear();
                }

                datosOrdenados.clear();
                listaTemporal = null;
                AutorizacionTiempo = false;

                startActivity(intentGenerarReporte);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoGenerarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible generar el reporte debido a que no se selecciono ningún dato respectivamente.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            reporteTiempoGenerarBinding.txtTituloGenerarTiempo.setVisibility(GONE);
            reporteTiempoGenerarBinding.hsvScrollHorizontalReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.btnSeleccionarPermisoTiempoExtra.setVisibility(GONE);
            reporteTiempoGenerarBinding.btnGenerarReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setVisibility(VISIBLE);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setVisibility(VISIBLE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setImageResource(R.drawable.icono_contenido_no_disponible);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoGenerarActivity.this);
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


    private void VistaOtroRegistro() {
        try {
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;
            ArrayList<ExtensionPermisoTiempoEntitie> Lista_PermisosTiempo_Temporal = new ArrayList<>();

            /* En esta primera parte buscara y guardara los datos seleccionados dentro de una lista llamada: -
             * "Lista_PermisosTiempo_Temporal", el cual contiene la lista original previo a los nuevos datos que el -
             * usuario podria añadir. */
            for(int i = 0; i < reporteTiempoGenerarBinding.tblTablaReporteTiempo.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) reporteTiempoGenerarBinding.tblTablaReporteTiempo.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                    Lista_PermisosTiempo_Temporal.add(datoSeleccionado);
                }
            }


            if(datoSeleccionado != null) {
                /* Se transforma esa lista original en un Json para que se guarde en la variable estatica: listaTemporal, -
                 * para así contener dichos datos y evitar que se pierdan. */
                listaTemporal = null;
                listaTemporal = gson.toJson(Lista_PermisosTiempo_Temporal);

                //Esto se hace para evitar problemas, además de ser una buena práctica.
                if(tablaRecorrida != null) {
                    tablaRecorrida.clear();
                }

                datosOrdenados.clear();
                Lista_PermisosTiempo.clear();
                AutorizacionTiempo = false;

                //Aqui debe llevarlo a otra vista.
                Intent intentOtroRegistro = new Intent(ReportePermisoTiempoGenerarActivity.this, ReportePermisoTiempoInfoActivity.class);

                intentOtroRegistro.putExtra("Tipo_De_Accion", "Generar_ReporteTiempo");
                intentOtroRegistro.putExtra("Regresar", "ReporteTiempoGenerar");

                startActivity(intentOtroRegistro);

                finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoGenerarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible añadir otro registro debido a que no se selecciono ningún dato respectivamente.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            reporteTiempoGenerarBinding.txtTituloGenerarTiempo.setVisibility(GONE);
            reporteTiempoGenerarBinding.hsvScrollHorizontalReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.btnSeleccionarPermisoTiempoExtra.setVisibility(GONE);
            reporteTiempoGenerarBinding.btnGenerarReporteTiempo.setVisibility(GONE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setVisibility(VISIBLE);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setVisibility(VISIBLE);

            reporteTiempoGenerarBinding.imgFotoGenerarReportesTiempo.setImageResource(R.drawable.icono_contenido_no_disponible);
            reporteTiempoGenerarBinding.txtMensajeGenerarReportesTiempo.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ReportePermisoTiempoGenerarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible añadir otro registro en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}
