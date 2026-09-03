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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionInicioSesionEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.InicioSesionInterface;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReporteUsuarioFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombre, txtApellidos, txtCedula, txtCorreo, txtDepartamento, txtRol, txtFechaCreacion,
             txtFechaInicioSesion, txtUltimaConexion, campoNombre, campoApellidos, campoCedula, campoCorreo,
             campoDepartamento, campoRol, campoFechaCreacion, campoFechaInicioSesion, campoUltimaConexion,
             campoNumeroReporte, campoReporteUsuario, txtMensaje;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, LargoNumeroReporte, AnchoNumeroReporte,
            TamañoLetraContenido, margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingStartCheckBox, paddingEndContenido;

    Button botonCrear, botonActualizar, botonEliminar, botonDescargar;
    TableRow tbrPrimeraFila, tbrPrimeraFilaReportesPDF, nuevaFila, filaGuardada;

    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones, scrollHorizontalReportesPDF;
    TableRow.LayoutParams parametrosCheckBox, parametrosNumeroReporte, parametrosContenido;
    CheckBox botonSeleccion, campoCheckBox, campoCheckBoxReporte;

    TableLayout tblTablaReportesUsuarios, tblTablaReportesPDF;

    ImageView logitoReportesUsuarios;
    SearchView buscadorReportesUsuarios;
    Integer contadorNumeroReporte = 0;

    List<ExtensionInicioSesionEntitie> datosOrdenados = new ArrayList<>();
    ArrayList<ExtensionInicioSesionEntitie> Lista_Tabla = new ArrayList<>();

    public static ArrayList<String> documentosPDF = new ArrayList<>();
    public static Boolean mensajeReportesUsuarios = false;
    public static Boolean autorizacionMantenerReporte = false;

    //Interfaz que contiene los métodos de la entidad FAQ.
    InicioSesionInterface inicioSesionInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reporte_usuarios, container, false);
        logitoReportesUsuarios = view.findViewById(R.id.img_fotoReportesUsuarios);
        buscadorReportesUsuarios = view.findViewById(R.id.sv_buscarReportesUsuarios);

        botonCrear = view.findViewById(R.id.btn_GenerarReportesUsuarios);
        botonActualizar = view.findViewById(R.id.btn_EditarReportesUsuarios);
        botonEliminar = view.findViewById(R.id.btn_EliminarReportesUsuarios);
        botonDescargar = view.findViewById(R.id.btn_DescargarReportesUsuario);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDatoReportes_Usuarios);

        txtNombre = view.findViewById(R.id.txt_NombreReportes_Usuarios);
        txtApellidos = view.findViewById(R.id.txt_ApellidosReportes_Usuarios);
        txtCedula = view.findViewById(R.id.txt_CedulaReportes_Usuarios);
        txtCorreo = view.findViewById(R.id.txt_CorreoReportes_Usuario);
        txtDepartamento = view.findViewById(R.id.txt_DepartamentoReportes_Usuarios);
        txtRol = view.findViewById(R.id.txt_RolReportes_Usuarios);
        txtFechaCreacion = view.findViewById(R.id.txt_FechaCreacionReportes_Usuarios);
        txtFechaInicioSesion = view.findViewById(R.id.txt_FechaInicioSesionReportes_Usuarios);
        txtUltimaConexion = view.findViewById(R.id.txt_UltimaConexionReportes_Usuarios);
        txtMensaje = view.findViewById(R.id.txt_MensajeReportesUsuarios);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontalReportesUsuarios);
        scrollHorizontalReportesPDF = view.findViewById(R.id.hsv_ScrollHorizontalReportesPDF);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_ReportesUsuarios);

        tblTablaReportesUsuarios = view.findViewById(R.id.tbl_TablaContenido_ReportesUsuarios);
        tblTablaReportesPDF = view.findViewById(R.id.tbl_TablaReportesPDF);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaContenido_ReportesUsuarios);
        tbrPrimeraFilaReportesPDF = view.findViewById(R.id.tbr_PrimeraFilaContenido_ReportesPDF);

        logitoReportesUsuarios.setVisibility(GONE);
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
                buscadorReportesUsuarios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

                buscadorReportesUsuarios.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                        buscadorReportesUsuarios.clearFocus();
                        buscadorReportesUsuarios.setIconifiedByDefault(true);
                        return false;
                    }
                });

                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaGenerarReportesUsuario());
                    botonActualizar.setOnClickListener(v -> VistaEditarReportesUsuario());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este reporte de usuario de forma permanentemente?")
                                .setTitle("Eliminar Reporte de Usuario.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarReportesUsuario();
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
                    botonDescargar.setOnClickListener(v -> DescargarDocumentoPDF());

                    if(getArguments() != null) {
                        Uri documentoPDF = Uri.parse(getArguments().getString("Documento-PDF"));
                        documentosPDF.add(documentoPDF.toString());
                        autorizacionMantenerReporte = true;
                    }

                    MostrarReportesUsuario(tokenGuardado,null, false);

                    if(autorizacionMantenerReporte != false) {
                        MostrarDocumento_ReportesUsuario();
                    }
                }
            }

        } catch (Exception error) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            if(mensajeReportesUsuarios != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeReportesUsuarios = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }


        if(Leer == false) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(GONE);
            scrollHorizontal.setVisibility(GONE);
            scrollHorizontalReportesPDF.setVisibility(GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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

            if(mensajeReportesUsuarios != true) {
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
                        mensajeReportesUsuarios = true;
                    }
                });

                AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                ejecutarMensajeCrear.show();
            }
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
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
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void MostrarReportesUsuario(String tokenUsuario, List<ExtensionInicioSesionEntitie> listaActualizada, Boolean autorizacion) {

        Activity nombreActividad = getActivity();

        inicioSesionInterface = ConexionAPI.Conexion_API_Inicio_Sesion(nombreActividad);

        Call<List<ExtensionInicioSesionEntitie>> mostrarUsuarios = inicioSesionInterface.obtenerUsuarios(tokenUsuario);


        mostrarUsuarios.enqueue(new Callback<List<ExtensionInicioSesionEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionInicioSesionEntitie>> call, Response<List<ExtensionInicioSesionEntitie>> response) {
                if (response.isSuccessful()) {
                    tblTablaReportesUsuarios.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionInicioSesionEntitie>() {
                        @Override
                        public int compare(ExtensionInicioSesionEntitie o1, ExtensionInicioSesionEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaReportesUsuarios.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoCorreo = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoRol = new TextView(getActivity());
                            campoFechaCreacion = new TextView(getActivity());
                            campoFechaInicioSesion = new TextView(getActivity());
                            campoUltimaConexion = new TextView(getActivity());


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

                            campoCorreo.setText(Correo);
                            campoCorreo.setWidth(LargoContenido);
                            campoCorreo.setHeight(AnchoContenido);
                            campoCorreo.setLayoutParams(parametrosContenido);
                            campoCorreo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCorreo.setTextColor(Color.BLACK);
                            campoCorreo.setTextSize(TamañoLetraContenido);

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

                            campoRol.setText(NombreRol);
                            campoRol.setWidth(LargoContenido);
                            campoRol.setHeight(AnchoContenido);
                            campoRol.setLayoutParams(parametrosContenido);
                            campoRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRol.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoRol.setTextColor(Color.BLACK);
                            campoRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(FechaCreacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

                            campoFechaInicioSesion.setText(FechaInicioSesion);
                            campoFechaInicioSesion.setWidth(LargoContenido);
                            campoFechaInicioSesion.setHeight(AnchoContenido);
                            campoFechaInicioSesion.setLayoutParams(parametrosContenido);
                            campoFechaInicioSesion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaInicioSesion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaInicioSesion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaInicioSesion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaInicioSesion.setTextColor(Color.BLACK);
                            campoFechaInicioSesion.setTextSize(TamañoLetraContenido);

                            campoUltimaConexion.setText(UltimaConexion);
                            campoUltimaConexion.setWidth(LargoContenido);
                            campoUltimaConexion.setHeight(AnchoContenido);
                            campoUltimaConexion.setLayoutParams(parametrosContenido);
                            campoUltimaConexion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoUltimaConexion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoUltimaConexion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoUltimaConexion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
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
                            tblTablaReportesUsuarios.addView(nuevaFila);
                        }

                    } else {
                        tblTablaReportesUsuarios.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoCorreo = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoRol = new TextView(getActivity());
                            campoFechaCreacion = new TextView(getActivity());
                            campoFechaInicioSesion = new TextView(getActivity());
                            campoUltimaConexion = new TextView(getActivity());


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

                            campoCorreo.setText(Correo);
                            campoCorreo.setWidth(LargoContenido);
                            campoCorreo.setHeight(AnchoContenido);
                            campoCorreo.setLayoutParams(parametrosContenido);
                            campoCorreo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCorreo.setTextColor(Color.BLACK);
                            campoCorreo.setTextSize(TamañoLetraContenido);

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

                            campoRol.setText(NombreRol);
                            campoRol.setWidth(LargoContenido);
                            campoRol.setHeight(AnchoContenido);
                            campoRol.setLayoutParams(parametrosContenido);
                            campoRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRol.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoRol.setTextColor(Color.BLACK);
                            campoRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(FechaCreacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

                            campoFechaInicioSesion.setText(FechaInicioSesion);
                            campoFechaInicioSesion.setWidth(LargoContenido);
                            campoFechaInicioSesion.setHeight(AnchoContenido);
                            campoFechaInicioSesion.setLayoutParams(parametrosContenido);
                            campoFechaInicioSesion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaInicioSesion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaInicioSesion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaInicioSesion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaInicioSesion.setTextColor(Color.BLACK);
                            campoFechaInicioSesion.setTextSize(TamañoLetraContenido);

                            campoUltimaConexion.setText(UltimaConexion);
                            campoUltimaConexion.setWidth(LargoContenido);
                            campoUltimaConexion.setHeight(AnchoContenido);
                            campoUltimaConexion.setLayoutParams(parametrosContenido);
                            campoUltimaConexion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoUltimaConexion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoUltimaConexion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoUltimaConexion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
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
                            tblTablaReportesUsuarios.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        buscadorReportesUsuarios.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalReportesPDF.setVisibility(View.GONE);

                        logitoReportesUsuarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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
                        buscadorReportesUsuarios.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalReportesPDF.setVisibility(View.GONE);

                        logitoReportesUsuarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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
            public void onFailure(Call<List<ExtensionInicioSesionEntitie>> call, Throwable t) {
                buscadorReportesUsuarios.setVisibility(GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonDescargar.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);
                scrollHorizontalReportesPDF.setVisibility(View.GONE);

                logitoReportesUsuarios.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void MostrarDocumento_ReportesUsuario() {
        try {
            tblTablaReportesPDF.removeAllViews();

            if(documentosPDF.size() == 0) {
                tblTablaReportesPDF.addView(tbrPrimeraFilaReportesPDF);
            }

            for (int i = 0; i < documentosPDF.size(); i++) {
                filaGuardada = new TableRow(getActivity());
                filaGuardada.setBackground(getActivity().getDrawable(R.drawable.border_table));
                campoCheckBoxReporte = new CheckBox(getActivity());
                campoNumeroReporte = new TextView(getActivity());
                campoReporteUsuario = new TextView(getActivity());

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

                String ReporteUsuario  = documentosPDF.get(i).toString();

                campoCheckBoxReporte.setWidth(LargoCheckBox);
                campoCheckBoxReporte.setHeight(AnchoCheckBox);
                campoCheckBoxReporte.setLayoutParams(parametrosCheckBox);
                campoCheckBoxReporte.setTop(margenTop);
                campoCheckBoxReporte.setPaddingRelative(0, paddingTopContenido, 0, 0);
                campoCheckBoxReporte.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                campoCheckBoxReporte.setTag(ReporteUsuario);

                contadorNumeroReporte++;
                campoNumeroReporte.setText(contadorNumeroReporte.toString());
                campoNumeroReporte.setWidth(LargoContenido);
                campoNumeroReporte.setHeight(AnchoContenido);
                campoNumeroReporte.setLayoutParams(parametrosNumeroReporte);
                campoNumeroReporte.setPaddingRelative(0, paddingTopContenido, paddingEndContenido, 0);
                campoNumeroReporte.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                campoNumeroReporte.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                campoNumeroReporte.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoNumeroReporte.setTextColor(Color.BLACK);
                campoNumeroReporte.setTextSize(TamañoLetraContenido);

                campoReporteUsuario.setText(ReporteUsuario);
                campoReporteUsuario.setWidth(LargoContenido);
                campoReporteUsuario.setHeight(AnchoContenido);
                campoReporteUsuario.setLayoutParams(parametrosContenido);
                campoReporteUsuario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoReporteUsuario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoReporteUsuario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoReporteUsuario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoReporteUsuario.setTextColor(Color.BLACK);
                campoReporteUsuario.setTextSize(TamañoLetraContenido);


                filaGuardada.addView(campoCheckBoxReporte);
                filaGuardada.addView(campoNumeroReporte);
                filaGuardada.addView(campoReporteUsuario);
                tblTablaReportesPDF.addView(filaGuardada);
            }

        } catch (Exception error) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void VistaGenerarReportesUsuario() {
        try {
            //En esta primera parte buscara y guardara aquellos datos que el usuario haya seleccionado con el check.
            Integer cantidadChecks = 0;
            ExtensionInicioSesionEntitie datoSeleccionado = null;

            for (int i = 0; i < tblTablaReportesUsuarios.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaReportesUsuarios.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);

                if (seleccionDato.isChecked()) {
                    cantidadChecks += 1;
                    datoSeleccionado = (ExtensionInicioSesionEntitie) seleccionDato.getTag();
                    Lista_Tabla.add(datoSeleccionado);
                }
            }

            /* Una vez hecho eso si pasa la validación, entonces enviaria ese dato (o datos) que el usuario selecciono -
             * en forma de una lista. */
            if (cantidadChecks != 0 && datoSeleccionado != null) {
                Intent intentReporteUsuario = new Intent(getActivity(), ReporteUsuarioGenerarActivity.class);

                intentReporteUsuario.putParcelableArrayListExtra("Tabla_ReportesUsuarios_Guardado", Lista_Tabla);

                startActivity(intentReporteUsuario);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void VistaEditarReportesUsuario() {
        try {
            //Pendiente.
        } catch (Exception error) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void EliminarReportesUsuario() {
        try {
            Integer cantidadChecks = 0;
            String datoSeleccionado = null;

            for(int i = 0; i < tblTablaReportesPDF.getChildCount(); i++) {
                TableRow reportesDatos = (TableRow) tblTablaReportesPDF.getChildAt(i);
                CheckBox seleccionReportes = (CheckBox) reportesDatos.getChildAt(0);


                if(seleccionReportes.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (String) seleccionReportes.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                documentosPDF.remove(datoSeleccionado);
                contadorNumeroReporte = 0;
                MostrarDocumento_ReportesUsuario();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el reporte de usuario debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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
            String datoSeleccionado = null;

            //En esta primera parte buscara y guardara el documento PDF que el usuario desea descargar.
            for (int i = 0; i < tblTablaReportesPDF.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaReportesPDF.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if (seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (String) seleccionDato.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                //Esto es para poder leer y acceder al URI del documento PDF que fue seleccionado(a):
                InputStream lector = getActivity().getContentResolver().openInputStream(Uri.parse(datoSeleccionado));
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
                        "Reporte-Usuarios-MuniTurrialba.pdf");

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
                construirAlerta.setMessage("Pero en este momento no es posible descargar el reporte de usuario debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorReportesUsuarios.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalReportesPDF.setVisibility(View.GONE);

            logitoReportesUsuarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoReportesUsuarios.setImageResource(R.drawable.icono_contenido_no_disponible);
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