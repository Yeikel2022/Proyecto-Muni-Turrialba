package com.proyectotcu.muniturrialba.moduloReporteria;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_CENTER;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionReporteSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ReporteSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReporteSalarioFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombre, txtApellidos, txtCedula, txtDepartamento, txtFechaEntrega, txtSalario, txtDescripcion,
             campoNombre, campoApellidos, campoCedula, campoDepartamento, campoFechaEntrega, campoSalario, campoDescripcion,
             campoNumeroReporte, campoReporteSalario, txtMensaje;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, LargoNumeroReporte, AnchoNumeroReporte,
            TamañoLetraContenido, margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingStartCheckBox, paddingEndContenido;

    Button botonCrear, botonActualizar, botonEliminar, botonDescargar;
    TableRow tbrPrimeraFila, tbrPrimeraFilaReportesPDF, nuevaFila, filaGuardada;

    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones, scrollHorizontalReportesPDF;
    TableRow.LayoutParams parametrosCheckBox, parametrosNumeroReporte, parametrosContenido;
    CheckBox botonSeleccion, campoCheckBox, campoCheckBoxReporte;

    TableLayout tblTablaReportesSalarios, tblTablaReportesPDF;

    ImageView logitoReportesSalarios;
    SearchView buscadorReportesSalarios;
    Uri documentoPDF;
    String idDocumento;

    List<ExtensionSalarioEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionSalarioEntitie> Lista_Tabla = new ArrayList<>();
    ArrayList<ExtensionSalarioEntitie> ListaDatos_DocumentoPDF = new ArrayList<>();

    public static ArrayList<ReporteSalarioEntitie> documentosPDFSalarios = new ArrayList<>();
    public static ArrayList<ExtensionReporteSalarioEntitie> respaldoReporteSalario = new ArrayList<>();
    public static Boolean mensajeReportesSalarios = false;
    public static Boolean autorizacionMantenerReporteSalarios = false;
    public static Integer contadorNumeroReporteSalarios = 0;

    //Interfaz que contiene los métodos de la entidad FAQ.
    SalarioInterface salarioInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_reporte_salarios, container, false);
        logitoReportesSalarios = view.findViewById(R.id.img_fotoReportesSalariales);
        buscadorReportesSalarios = view.findViewById(R.id.sv_buscarReportesSalariales);

        botonCrear = view.findViewById(R.id.btn_GenerarReportesSalariales);
        botonActualizar = view.findViewById(R.id.btn_EditarReportesSalariales);
        botonEliminar = view.findViewById(R.id.btn_EliminarReportesSalariales);
        botonDescargar = view.findViewById(R.id.btn_DescargarReportesSalariales);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDatoReportes_Salariales);

        txtNombre = view.findViewById(R.id.txt_NombreReportes_Salariales);
        txtApellidos = view.findViewById(R.id.txt_ApellidosReportes_Salariales);
        txtCedula = view.findViewById(R.id.txt_CedulaReportes_Salariales);
        txtDepartamento = view.findViewById(R.id.txt_DepartamentoReportes_Salariales);
        txtFechaEntrega = view.findViewById(R.id.txt_FechaEntregaReportes_Salariales);
        txtSalario = view.findViewById(R.id.txt_SalarioReportes_Salariales);
        txtDescripcion = view.findViewById(R.id.txt_DescripcionReportes_Salariales);
        txtMensaje = view.findViewById(R.id.txt_MensajeReportesSalariales);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontalReportesSalariales);
        scrollHorizontalReportesPDF = view.findViewById(R.id.hsv_ScrollHorizontalReportesPDF_Salariales);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_ReportesSalariales);

        tblTablaReportesSalarios = view.findViewById(R.id.tbl_TablaContenido_ReportesSalariales);
        tblTablaReportesPDF = view.findViewById(R.id.tbl_TablaReportesPDF_Salariales);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaContenido_ReportesSalariales);
        tbrPrimeraFilaReportesPDF = view.findViewById(R.id.tbr_PrimeraFilaContenido_ReportesPDF_Salariales);

        logitoReportesSalarios.setVisibility(GONE);
        txtMensaje.setVisibility(GONE);

        try {
            SharedPreferences archivoXML = getActivity().getSharedPreferences(
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


            //Moderador o administrador:
            if (campoRol == 1 || campoRol == 2) {
                buscadorReportesSalarios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                        buscadorReportesSalarios.clearFocus();
                        return true;
                    }
                });

                buscadorReportesSalarios.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                        buscadorReportesSalarios.clearFocus();
                        buscadorReportesSalarios.setIconifiedByDefault(true);
                        return false;
                    }
                });

                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaGenerarReportesSalario());
                    botonActualizar.setOnClickListener(v -> VistaEditarReportesSalario());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este reporte de forma permanente?")
                                .setTitle("Eliminar Reporte Salarial.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarReportesSalario();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se continuo con la eliminación!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });
                    botonDescargar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_descargar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de descargar este reporte salarial?")
                                .setTitle("Descargar Reporte Salarial.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DescargarDocumentoPDF();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se continuo con la descarga del reporte!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });

                    if(getArguments() != null) {
                        documentoPDF = Uri.parse(getArguments().getString("DocumentoPDF_Salario"));
                        ListaDatos_DocumentoPDF = getArguments().getParcelableArrayList("ListaDatos_DocumentoPDF_SalarioGenerado");
                        idDocumento = getArguments().getString("idDocumentoSalario");

                        if(idDocumento == null) {
                            documentosPDFSalarios.add(new ReporteSalarioEntitie(contadorNumeroReporteSalarios.toString(), documentoPDF.toString()));

                        } else {
                            documentosPDFSalarios.add(new ReporteSalarioEntitie(idDocumento, documentoPDF.toString()));
                        }

                        respaldoReporteSalario.add(new ExtensionReporteSalarioEntitie(documentoPDF, ListaDatos_DocumentoPDF));
                        autorizacionMantenerReporteSalarios = true;
                    }

                    MostrarReportesSalariales(tokenGuardado,null, false);

                    if(autorizacionMantenerReporteSalarios != false) {
                        MostrarDocumento_ReportesSalariales();
                    }
                }
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        return view;
    }


    private Integer ValidarPermisosAdmin(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        ArrayList<String> listaMensaje = new ArrayList<String>();
        String mensajeProveniente = "";

        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            if(mensajeReportesSalarios != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeReportesSalarios = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }


        if(Leer == false) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(GONE);
            scrollHorizontal.setVisibility(GONE);
            scrollHorizontalReportesPDF.setVisibility(GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            listaMensaje.add("- Visualizar esta información.\n\n");
        }


        if(Crear == false) {
            botonCrear.setVisibility(GONE);
            botonDescargar.setVisibility(GONE);

            listaMensaje.add("- Generar un nuevo reporte dentro de este apartado.\n\n");
            listaMensaje.add("- Descargar un reporte respectivamente.\n\n");
        }


        if(Actualizar == false) {
            botonActualizar.setVisibility(GONE);
            listaMensaje.add("- Actualizar un reporte respectivamente.\n\n");
        }


        if(Eliminar == false) {
            botonEliminar.setVisibility(GONE);
            listaMensaje.add("- Eliminar un reporte respectivamente.");
        }


        if(Leer != true || Crear != true || Actualizar != true || Eliminar != true) {
            if(listaMensaje.size() == 4) {
                scrollHorizontalBotones.setVisibility(GONE);
            }

            if(mensajeReportesSalarios != true) {
                for(int i = 0; i < listaMensaje.size(); i++) {
                    mensajeProveniente += listaMensaje.get(i);
                }

                AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                construirAlertaCrear.setIcon(R.drawable.icono_error);

                construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente)
                        .setTitle("¡Lo sentimos!");

                construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeReportesSalarios = true;
                    }
                });

                AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                ejecutarMensajeCrear.show();
            }
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    private void BuscarPrioridad(String tokenUsuario, List<ExtensionSalarioEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionSalarioEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarReportesSalariales(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionSalarioEntitie salarioEntitie : listaDatos) {
                    String nombreEmpleado = salarioEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(salarioEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el salario debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarReportesSalariales(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarReportesSalariales(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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


    private void MostrarReportesSalariales(String tokenUsuario, List<ExtensionSalarioEntitie> listaActualizada, Boolean autorizacion) {

        Activity nombreActividad = getActivity();

        salarioInterface = ConexionAPI.Conexion_API_Salario(nombreActividad);

        Call<List<ExtensionSalarioEntitie>> mostrarSalarios = salarioInterface.obtenerSalarios(tokenUsuario);


        mostrarSalarios.enqueue(new Callback<List<ExtensionSalarioEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionSalarioEntitie>> call, Response<List<ExtensionSalarioEntitie>> response) {
                if (response.isSuccessful()) {
                    tblTablaReportesSalarios.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionSalarioEntitie>() {
                        @Override
                        public int compare(ExtensionSalarioEntitie o1, ExtensionSalarioEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaReportesSalarios.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoFechaEntrega = new TextView(getActivity());
                            campoSalario = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());


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
                            campoNombre.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            tblTablaReportesSalarios.addView(nuevaFila);
                        }

                    } else {
                        tblTablaReportesSalarios.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoFechaEntrega = new TextView(getActivity());
                            campoSalario = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());

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
                            campoNombre.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            tblTablaReportesSalarios.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        buscadorReportesSalarios.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalReportesPDF.setVisibility(View.GONE);

                        logitoReportesSalarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        buscadorReportesSalarios.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalReportesPDF.setVisibility(View.GONE);

                        logitoReportesSalarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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
                buscadorReportesSalarios.setVisibility(GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonDescargar.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);
                scrollHorizontalReportesPDF.setVisibility(View.GONE);

                logitoReportesSalarios.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                txtMensaje.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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


    private void MostrarDocumento_ReportesSalariales() {
        try {
            tblTablaReportesPDF.removeAllViews();

            if(documentosPDFSalarios.isEmpty()) {
                tblTablaReportesPDF.addView(tbrPrimeraFilaReportesPDF);
            }

            for (int i = 0; i < documentosPDFSalarios.size(); i++) {
                filaGuardada = new TableRow(getActivity());
                filaGuardada.setBackground(getActivity().getDrawable(R.drawable.border_table));
                campoCheckBoxReporte = new CheckBox(getActivity());
                campoNumeroReporte = new TextView(getActivity());
                campoReporteSalario = new TextView(getActivity());

                LargoNumeroReporte = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 80);
                AnchoNumeroReporte = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 75);
                LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 950);
                AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 75);
                LargoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                AnchoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);

                parametrosNumeroReporte = new TableRow.LayoutParams(LargoNumeroReporte, AnchoNumeroReporte);
                parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                parametrosCheckBox = new TableRow.LayoutParams(LargoCheckBox, AnchoCheckBox);

                margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                margenCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                paddingStartCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                parametrosNumeroReporte.setMarginStart(margenContenido);
                parametrosContenido.setMarginStart(margenContenido);
                parametrosCheckBox.setMarginStart(margenCheckBox);
                parametrosCheckBox.setMarginEnd(margenCheckBox);


                ReporteSalarioEntitie ReporteSalario = documentosPDFSalarios.get(i);
                String numeroReporte = ReporteSalario.getNumeroDocumento().trim();
                String documentoPDF = ReporteSalario.getDocumentoPDF().trim();

                campoCheckBoxReporte.setWidth(LargoCheckBox);
                campoCheckBoxReporte.setHeight(AnchoCheckBox);
                campoCheckBoxReporte.setLayoutParams(parametrosCheckBox);
                campoCheckBoxReporte.setTop(margenTop);
                campoCheckBoxReporte.setPaddingRelative(0, paddingTopContenido, 0, 0);
                campoCheckBoxReporte.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                campoCheckBoxReporte.setTag(ReporteSalario);

                campoNumeroReporte.setText(numeroReporte);
                campoNumeroReporte.setWidth(LargoContenido);
                campoNumeroReporte.setHeight(AnchoContenido);
                campoNumeroReporte.setLayoutParams(parametrosNumeroReporte);
                campoNumeroReporte.setPaddingRelative(0, paddingTopContenido, paddingEndContenido, 0);
                campoNumeroReporte.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                campoNumeroReporte.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                campoNumeroReporte.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoNumeroReporte.setTextColor(Color.BLACK);
                campoNumeroReporte.setTextSize(TamañoLetraContenido);

                campoReporteSalario.setText(documentoPDF);
                campoReporteSalario.setWidth(LargoContenido);
                campoReporteSalario.setHeight(AnchoContenido);
                campoReporteSalario.setLayoutParams(parametrosContenido);
                campoReporteSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoReporteSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoReporteSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoReporteSalario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoReporteSalario.setTextColor(Color.BLACK);
                campoReporteSalario.setTextSize(TamañoLetraContenido);


                filaGuardada.addView(campoCheckBoxReporte);
                filaGuardada.addView(campoNumeroReporte);
                filaGuardada.addView(campoReporteSalario);
                tblTablaReportesPDF.addView(filaGuardada);
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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


    private void VistaGenerarReportesSalario() {
        try {
            //En esta primera parte buscara y guardara aquellos datos que el usuario haya seleccionado con el check.
            Integer cantidadChecks = 0;
            ExtensionSalarioEntitie datoSeleccionado = null;

            for (int i = 0; i < tblTablaReportesSalarios.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaReportesSalarios.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionSalarioEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }

            /* Una vez hecho eso si pasa la validación, entonces enviaria ese dato (o datos) que el usuario selecciono -
             * en forma de una lista. */
            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteSalario = new Intent(getActivity(), ReporteSalarioGenerarActivity.class);

                intentReporteSalario.putParcelableArrayListExtra("Tabla_ReportesSalarios_Guardado", Lista_Tabla);

                startActivity(intentReporteSalario);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible generar el reporte debido a que no se selecciono ningún dato.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch(Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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


    private void VistaEditarReportesSalario() {
        try {
            Integer cantidadChecks = 0;
            ReporteSalarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaReportesPDF.getChildCount(); i++) {
                TableRow reportesDatos = (TableRow) tblTablaReportesPDF.getChildAt(i);
                CheckBox seleccionReportes = (CheckBox) reportesDatos.getChildAt(0);


                if(seleccionReportes.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ReporteSalarioEntitie) seleccionReportes.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null && respaldoReporteSalario != null) {
                ArrayList<ExtensionSalarioEntitie> listaDatos = new ArrayList<>();
                Uri reporteSeleccionado = Uri.parse(datoSeleccionado.getDocumentoPDF());
                String idReporteSeleccionado = datoSeleccionado.getNumeroDocumento();

                for(ExtensionReporteSalarioEntitie listaReporte : respaldoReporteSalario) {
                    Uri documentoPDF_Recorrido = listaReporte.getDocumentoPDF();

                    if(documentoPDF_Recorrido.equals(reporteSeleccionado)) {
                        for(int i = 0; i < listaReporte.getListaDatos_DocumentoPDF().size(); i++) {
                            listaDatos.add(listaReporte.getListaDatos_DocumentoPDF().get(i));
                        }
                    }
                }


                Intent intentReporteEditarSalario = new Intent(getActivity(), ReporteSalarioEditarActivity.class);

                intentReporteEditarSalario.putParcelableArrayListExtra("ListaDatos_DocumentoPDF_Seleccionado", listaDatos);
                intentReporteEditarSalario.putExtra("Documento_PDF_Seleccionado", reporteSeleccionado.toString());
                intentReporteEditarSalario.putExtra("idDocumento_PDF_Seleccionado", idReporteSeleccionado);

                startActivity(intentReporteEditarSalario);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar el reporte debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void EliminarReportesSalario() {
        try {
            Integer cantidadChecks = 0;
            ReporteSalarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaReportesPDF.getChildCount(); i++) {
                TableRow reportesDatos = (TableRow) tblTablaReportesPDF.getChildAt(i);
                CheckBox seleccionReportes = (CheckBox) reportesDatos.getChildAt(0);


                if(seleccionReportes.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ReporteSalarioEntitie) seleccionReportes.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                Uri datoEliminar = Uri.parse(datoSeleccionado.getDocumentoPDF());
                ExtensionReporteSalarioEntitie respaldoDatoEliminar = null;

                for(ExtensionReporteSalarioEntitie listaReporte : respaldoReporteSalario) {
                    Uri documentoPDF_Recorrido = listaReporte.getDocumentoPDF();

                    if(documentoPDF_Recorrido.equals(datoEliminar)) {
                        respaldoDatoEliminar = listaReporte;
                    }
                }


                documentosPDFSalarios.remove(datoSeleccionado);
                respaldoReporteSalario.remove(respaldoDatoEliminar);
                MostrarDocumento_ReportesSalariales();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el reporte debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar el reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void DescargarDocumentoPDF() {
        try {
            Integer cantidadChecks = 0;
            ReporteSalarioEntitie datoSeleccionado = null;

            //En esta primera parte buscara y guardara el documento PDF que el usuario desea descargar.
            for (int i = 0; i < tblTablaReportesPDF.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaReportesPDF.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if (seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ReporteSalarioEntitie) seleccionDato.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                Uri documentoDescargar = Uri.parse(datoSeleccionado.getDocumentoPDF());

                //Esto es para poder leer y acceder al URI del documento PDF que fue seleccionado(a):
                InputStream lector = getActivity().getContentResolver().openInputStream(documentoDescargar);
                byte[] documentoPDF = null;

                //Esto es para que se pueda ejecutar bien en versiones de Android 13 en adelante.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    documentoPDF = lector.readAllBytes();
                }


                /* Aqui lo que se esta haciendo es crear un archivo privado en la aplicación, pero dirigido al -
                 * almacenamiento externo. Además, se coloco un null en: getExternalFilesDir(), para que pueda -
                 * traer la raiz de los archivos que contiene la aplicación móvil respectivamente.
                 *
                 * Ahora, con el "Reporte-Usuarios-MuniTurrialba.pdf", es unicamente el nombre que va a llevar -
                 * ese archivo como tal. */
                File archivoPDF = new File(getActivity().getExternalFilesDir(null),
                        "Reporte-Salariales-MuniTurrialba.pdf");

                /* Aqui lo que se esta haciendo es usar un FileOutputStream para poder leer el archivo que se -
                 * acaba de crear, de forma que, se pueda colocar toda la información sobre el documento PDF -
                 * que se creo temporalmente. Además, se coloco un: flush() para que todos los datos se fueran -
                 * directamente al archivo que se creo, de forma que asi se pueda evitar que los datos del documento -
                 * PDF se pierdan respectivamente.
                 *
                 * Ahora, también se coloco un close() para pdoer cerrar el lector y que no quedara abierto. */
                FileOutputStream lectorArchivo = new FileOutputStream(archivoPDF);
                lectorArchivo.write(documentoPDF);
                lectorArchivo.flush();
                lectorArchivo.close();

                /* Aqui lo que se esta haciendo es obtener el URI que contiene la ruta del documento PDF de una -
                 * forma segura, de forma que, ahora se pueda compartir con otras aplicaciones para ver dicho -
                 * documento.
                 *
                 * Ahora, cabe aclarar que lo que se puso en el código: "getPackageName() + ".provider"" es para -
                 * poder construir la autoridad que fue definida en el manifest de una forma más dinamica, evitando -
                 * así que surja alguna inconsistencia a la hora de econtrarlo. Ya que dicha autoridad es la que tiene -
                 * el permiso de los file providers (además del XML con las rutas de archivos), sin eso, prácticamente -
                 * no se podria compartir el URI hacia otros archivos, pese que ya este dicho URI en funcionamiento.  */
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".provider", archivoPDF);

                /* Aqui lo que se esta haciendo es hacer una especie de hipervinculo para el documento PDF, esto -
                 * se logra gracias a que el ACTION_VIEW le dice al intent que tiene que mostrar el contenido, -
                 * luego con el setDataAndType lo que estamos haciendo es indicar que contenido queremos mostrar -
                 * y que tipo de contenido es, que en este caso es un documento PDF, de ahi el porque se coloca: -
                 * application/pdf. Sin eso, prácticamente la aplicación (o incluso Android) no podrian saber que -
                 * tipo de contenido es para mostrarlo, de ahi el porque es importante.
                 *
                 * Para finalizar, también se añade un permiso adicional, el cual es: FLAG_GRANT_READ_URI_PERMISSION, -
                 * sin este permiso prácticamente no se podria ver el contenido (osea el URI), a pesar que ya tiene -
                 * los otros permisos para acceder al documento PDF respectivamente. */
                Intent visualizarDocumento = new Intent(Intent.ACTION_VIEW);
                visualizarDocumento.setDataAndType(uri, "application/pdf");
                visualizarDocumento.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(visualizarDocumento);

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible descargar el reporte debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorReportesSalarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible descargar el reporte en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}