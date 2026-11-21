package com.proyectotcu.muniturrialba.moduloAdministracionArchivos;

import static android.view.View.GONE;
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
import com.proyectotcu.muniturrialba.index.MenuFragment;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.RegistroArchivoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.RegistroArchivoInterface;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialArchivoFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombreArchivo, txtFormatoArchivo, txtFechaSubida, txtFechaEnvio, txtUsuarioOriginario, txtCorreoOriginario,
            txtDepartamentoOriginario, txtUsuarioDestinatario, txtCorreoDestinatario, txtDepartamentoDestinatario,
            txtEstadoArchivo, campoNombreArchivo, campoFormatoArchivo, campoFechaSubida, campoFechaEnvio, campoUsuarioOriginario,
            campoCorreoOriginario, campoDepartamentoOriginario, campoUsuarioDestinatario, campoCorreoDestinatario,
            campoDepartamentoDestinatario, campoEstadoArchivo, txtArchivoCompartido, txtNombreArchivoCompartido, txtFormatoArchivoCompartido,
            txtUsuarioOriginarioCompartido, txtCorreoOriginarioCompartido, txtDepartamentoOriginarioCompartido, txtUsuarioDestinatarioCompartido, txtCorreoDestinatarioCompartido,
            txtDepartamentoDestinatarioCompartido, txtEstadoArchivoCompartido, campoArchivoCompartido, campoNombreArchivoCompartido, campoFormatoArchivoCompartido, campoUsuarioOriginarioCompartido, campoCorreoOriginarioCompartido, campoDepartamentoOriginarioCompartido, campoUsuarioDestinatarioCompartido,
            campoCorreoDestinatarioCompartido, campoDepartamentoDestinatarioCompartido, campoEstadoArchivoCompartido, txtMensaje;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
            margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;
    TableRow tbrPrimeraFila, tbrPrimeraFilaCompartida, nuevaFila, nuevaFilaCompartido;
    CheckBox botonSeleccionCompartido, campoCheckBoxCompartido;

    HorizontalScrollView scrollHorizontal, scrollHorizontalCompartido, scrollHorizontalBotones;
    TableRow.LayoutParams parametrosCheckBox, parametrosContenido;

    Button botonDescargar;
    TableLayout tblTablaHistorialArchivos;
    TableLayout tblTablaHistorialArchivosCompartido;
    ImageView logitoHistorialArchivos;
    SearchView buscadorHistorialArchivos;
    List<RegistroArchivoEntitie> datosOrdenados = new ArrayList<>();

    public static Boolean mensajeHistorialArchivo = false;


    //Interfaz que contiene los métodos de la entidad FAQ.
    RegistroArchivoInterface registroArchivoInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_historial_archivo, container, false);
        logitoHistorialArchivos = view.findViewById(R.id.img_fotoHistorialArchivos);
        buscadorHistorialArchivos = view.findViewById(R.id.sv_buscarHistorialArchivos);

        botonDescargar = view.findViewById(R.id.btn_Descargar_HistorialArchivos);
        botonSeleccionCompartido = view.findViewById(R.id.btn_SeleccionDato_HistorialArchivos_Compartido);

        txtNombreArchivo = view.findViewById(R.id.txt_NombreArchivo_HistorialArchivos);
        txtFormatoArchivo = view.findViewById(R.id.txt_FormatoArchivo_HistorialArchivos);
        txtFechaSubida = view.findViewById(R.id.txt_FechaSubida_HistorialArchivos);
        txtFechaEnvio = view.findViewById(R.id.txt_InfoFechaEnvio_HistorialArchivos);
        txtUsuarioOriginario = view.findViewById(R.id.txt_UsuarioOriginario_HistorialArchivos);
        txtCorreoOriginario = view.findViewById(R.id.txt_CorreoOriginario_HistorialArchivos);
        txtDepartamentoOriginario = view.findViewById(R.id.txt_DepartamentoOriginario_HistorialArchivos);

        txtUsuarioDestinatario = view.findViewById(R.id.txt_UsuarioDestinatario_HistorialArchivos);
        txtCorreoDestinatario = view.findViewById(R.id.txt_CorreoDestinatario_HistorialArchivos);
        txtDepartamentoDestinatario = view.findViewById(R.id.txt_DepartamentoDestinatario_HistorialArchivos);
        txtEstadoArchivo = view.findViewById(R.id.txt_EstadoArchivo_HistorialArchivos);

        txtArchivoCompartido = view.findViewById(R.id.txt_Archivo_HistorialArchivos_Compartido);
        txtNombreArchivoCompartido = view.findViewById(R.id.txt_NombreArchivo_HistorialArchivos_Compartido);
        txtFormatoArchivoCompartido = view.findViewById(R.id.txt_FormatoArchivo_HistorialArchivos_Compartido);

        txtUsuarioOriginarioCompartido = view.findViewById(R.id.txt_UsuarioOriginario_HistorialArchivos_Compartido);
        txtCorreoOriginarioCompartido = view.findViewById(R.id.txt_CorreoOriginario_HistorialArchivos_Compartido);
        txtDepartamentoOriginarioCompartido = view.findViewById(R.id.txt_DepartamentoOriginario_HistorialArchivos_Compartido);
        txtUsuarioDestinatarioCompartido = view.findViewById(R.id.txt_UsuarioDestinatario_HistorialArchivos_Compartido);
        txtCorreoDestinatarioCompartido = view.findViewById(R.id.txt_CorreoDestinatario_HistorialArchivos_Compartido);
        txtDepartamentoDestinatarioCompartido = view.findViewById(R.id.txt_DepartamentoDestinatario_HistorialArchivos_Compartido);

        txtEstadoArchivoCompartido = view.findViewById(R.id.txt_EstadoArchivo_HistorialArchivos_Compartido);
        txtMensaje = view.findViewById(R.id.txt_MensajeHistorialArchivos);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontal_HistorialArchivos);
        scrollHorizontalCompartido = view.findViewById(R.id.hsv_ScrollHorizontal_HistorialArchivos_Compartido);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_HistorialArchivos);

        tblTablaHistorialArchivos = view.findViewById(R.id.tbl_TablaContenido_HistorialArchivos);
        tblTablaHistorialArchivosCompartido = view.findViewById(R.id.tbl_TablaContenido_HistorialArchivos_Compartido);

        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaContenido_HistorialArchivos);
        tbrPrimeraFilaCompartida = view.findViewById(R.id.tbr_PrimeraFilaContenido_HistorialArchivos_Compartido);

        logitoHistorialArchivos.setVisibility(GONE);
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
            String campoCorreo = json.optString("correo");

            //Moderador o administrador:
            if (campoRol == 1 || campoRol == 2 || campoRol == 3) {
                buscadorHistorialArchivos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, query, campoCorreo);
                        buscadorHistorialArchivos.clearFocus();
                        return true;
                    }
                });

                buscadorHistorialArchivos.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, "true", campoCorreo);
                        buscadorHistorialArchivos.clearFocus();
                        buscadorHistorialArchivos.setIconifiedByDefault(true);
                        return false;
                    }
                });

                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 5) {
                    botonDescargar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_descargar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de descargar este registro de archivo de forma permanente?")
                                .setTitle("Descargar Registro de Archivo.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DescargarRegistroArchivo();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se continuo con la descarga!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });

                    MostrarHistorialArchivos(tokenGuardado,null, false, campoCorreo);
                    MostrarHistorialArchivosCompartidos(MenuFragment.listaArchivoCompartido);
                }
            }

        } catch (Exception error) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
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
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            if(mensajeHistorialArchivo != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeHistorialArchivo = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }


        if(Leer == false) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            if(mensajeHistorialArchivo != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para visualizar esta información.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeHistorialArchivo = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    private void BuscarPrioridad(String tokenUsuario, List<RegistroArchivoEntitie> listaDatos, String textoIngresado, String correoOriginario) {
        try {
            List<RegistroArchivoEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarHistorialArchivos(tokenUsuario, datosFiltrados, false, correoOriginario);

            } else {
                for (RegistroArchivoEntitie registroArchivoEntitie : listaDatos) {
                    String nombreEmpleado = registroArchivoEntitie.getNombreDestinatario().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(registroArchivoEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el registro del archivo debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarHistorialArchivos(tokenUsuario, datosFiltrados, false, correoOriginario);

                } else {
                    MostrarHistorialArchivos(tokenUsuario, datosFiltrados, true, correoOriginario);
                }
            }

        } catch (Exception error) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información del registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void MostrarHistorialArchivos(String tokenUsuario, List<RegistroArchivoEntitie> listaActualizada, Boolean autorizacion, String correoOriginario) {

        Activity nombreActividad = getActivity();

        registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(nombreActividad);

        Call<List<RegistroArchivoEntitie>> mostrarArchivos = registroArchivoInterface.obtenerArchivos(tokenUsuario);

        mostrarArchivos.enqueue(new Callback<List<RegistroArchivoEntitie>>() {
            @Override
            public void onResponse(Call<List<RegistroArchivoEntitie>> call, Response<List<RegistroArchivoEntitie>> response) {
                if (response.isSuccessful()) {
                    Boolean mostrarInfoDefecto = false;

                    tblTablaHistorialArchivos.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<RegistroArchivoEntitie>() {
                        @Override
                        public int compare(RegistroArchivoEntitie o1, RegistroArchivoEntitie o2) {
                            return o1.getFechaSubida().compareToIgnoreCase(o2.getFechaSubida());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaHistorialArchivos.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoNombreArchivo = new TextView(getActivity());
                            campoFormatoArchivo = new TextView(getActivity());
                            campoFechaSubida = new TextView(getActivity());
                            campoFechaEnvio = new TextView(getActivity());
                            campoUsuarioOriginario = new TextView(getActivity());
                            campoCorreoOriginario = new TextView(getActivity());
                            campoDepartamentoOriginario = new TextView(getActivity());
                            campoUsuarioDestinatario = new TextView(getActivity());
                            campoCorreoDestinatario = new TextView(getActivity());
                            campoDepartamentoDestinatario = new TextView(getActivity());
                            campoEstadoArchivo = new TextView(getActivity());

                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 390);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);

                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            parametrosContenido.setMarginStart(margenContenido);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);


                            RegistroArchivoEntitie registroArchivo = listaActualizada.get(i);
                            String NombreArchivo  = registroArchivo.getNombreArchivo().trim();
                            String FormatoArchivo = registroArchivo.getFormatoArchivo();

                            String FechaSubida = registroArchivo.getFechaSubida().trim().replace("T", " ");
                            String FechaEnvio = registroArchivo.getFechaEnvio();

                            String UsuarioOriginario = registroArchivo.getNombreOriginario().trim() + " " + registroArchivo.getApellido_1_Originario().trim() + " " + registroArchivo.getApellido_2_Originario().trim();
                            String CorreoOriginario = registroArchivo.getCorreoElectronico_Originario().trim();
                            String DepartamentoOriginario = registroArchivo.getDepartamentoOriginario().trim();

                            String UsuarioDestinatario = registroArchivo.getNombreDestinatario().trim() + " " + registroArchivo.getApellido_1_Destinatario().trim() + " " + registroArchivo.getApellido_2_Destinatario().trim();
                            String CorreoDestinatario = registroArchivo.getCorreoElectronico_Destinatario().trim();
                            String DepartamentoDestinatario = registroArchivo.getDepartamentoDestinatario().trim();
                            String EstadoArchivo = registroArchivo.getEstadoArchivo().trim();

                            if(Objects.equals(correoOriginario, CorreoOriginario)) {
                                mostrarInfoDefecto = true;

                                campoNombreArchivo.setText(NombreArchivo);
                                campoNombreArchivo.setWidth(LargoContenido);
                                campoNombreArchivo.setHeight(AnchoContenido);
                                campoNombreArchivo.setLayoutParams(parametrosContenido);
                                campoNombreArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoNombreArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoNombreArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoNombreArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoNombreArchivo.setTextColor(Color.BLACK);
                                campoNombreArchivo.setTextSize(TamañoLetraContenido);

                                campoFormatoArchivo.setText(FormatoArchivo);
                                campoFormatoArchivo.setWidth(LargoContenido);
                                campoFormatoArchivo.setHeight(AnchoContenido);
                                campoFormatoArchivo.setLayoutParams(parametrosContenido);
                                campoFormatoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoFormatoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoFormatoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoFormatoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoFormatoArchivo.setTextColor(Color.BLACK);
                                campoFormatoArchivo.setTextSize(TamañoLetraContenido);

                                campoFechaSubida.setText(FechaSubida);
                                campoFechaSubida.setWidth(LargoContenido);
                                campoFechaSubida.setHeight(AnchoContenido);
                                campoFechaSubida.setLayoutParams(parametrosContenido);
                                campoFechaSubida.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoFechaSubida.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoFechaSubida.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoFechaSubida.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoFechaSubida.setTextColor(Color.BLACK);
                                campoFechaSubida.setTextSize(TamañoLetraContenido);

                                if(FechaEnvio != null) {
                                    txtFechaEnvio.setVisibility(VISIBLE);

                                    campoFechaEnvio.setText(FechaEnvio.trim().replace("T", " "));
                                    campoFechaEnvio.setWidth(LargoContenido);
                                    campoFechaEnvio.setHeight(AnchoContenido);
                                    campoFechaEnvio.setLayoutParams(parametrosContenido);
                                    campoFechaEnvio.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaEnvio.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaEnvio.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaEnvio.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaEnvio.setTextColor(Color.BLACK);
                                    campoFechaEnvio.setTextSize(TamañoLetraContenido);

                                } else {
                                    campoFechaEnvio.setText("No esta disponible por el momento.");
                                    campoFechaEnvio.setWidth(LargoContenido);
                                    campoFechaEnvio.setHeight(AnchoContenido);
                                    campoFechaEnvio.setLayoutParams(parametrosContenido);
                                    campoFechaEnvio.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaEnvio.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaEnvio.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaEnvio.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaEnvio.setTextColor(Color.BLACK);
                                    campoFechaEnvio.setTextSize(TamañoLetraContenido);
                                }

                                campoUsuarioOriginario.setText(UsuarioOriginario);
                                campoUsuarioOriginario.setWidth(LargoContenido);
                                campoUsuarioOriginario.setHeight(AnchoContenido);
                                campoUsuarioOriginario.setLayoutParams(parametrosContenido);
                                campoUsuarioOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoUsuarioOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoUsuarioOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoUsuarioOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoUsuarioOriginario.setTextColor(Color.BLACK);
                                campoUsuarioOriginario.setTextSize(TamañoLetraContenido);

                                campoCorreoOriginario.setText(CorreoOriginario);
                                campoCorreoOriginario.setWidth(LargoContenido);
                                campoCorreoOriginario.setHeight(AnchoContenido);
                                campoCorreoOriginario.setLayoutParams(parametrosContenido);
                                campoCorreoOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoCorreoOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoCorreoOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoCorreoOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoCorreoOriginario.setTextColor(Color.BLACK);
                                campoCorreoOriginario.setTextSize(TamañoLetraContenido);

                                campoDepartamentoOriginario.setText(DepartamentoOriginario);
                                campoDepartamentoOriginario.setWidth(LargoContenido);
                                campoDepartamentoOriginario.setHeight(AnchoContenido);
                                campoDepartamentoOriginario.setLayoutParams(parametrosContenido);
                                campoDepartamentoOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoDepartamentoOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoDepartamentoOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoDepartamentoOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoDepartamentoOriginario.setTextColor(Color.BLACK);
                                campoDepartamentoOriginario.setTextSize(TamañoLetraContenido);

                                campoUsuarioDestinatario.setText(UsuarioDestinatario);
                                campoUsuarioDestinatario.setWidth(LargoContenido);
                                campoUsuarioDestinatario.setHeight(AnchoContenido);
                                campoUsuarioDestinatario.setLayoutParams(parametrosContenido);
                                campoUsuarioDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoUsuarioDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoUsuarioDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoUsuarioDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoUsuarioDestinatario.setTextColor(Color.BLACK);
                                campoUsuarioDestinatario.setTextSize(TamañoLetraContenido);

                                campoCorreoDestinatario.setText(CorreoDestinatario);
                                campoCorreoDestinatario.setWidth(LargoContenido);
                                campoCorreoDestinatario.setHeight(AnchoContenido);
                                campoCorreoDestinatario.setLayoutParams(parametrosContenido);
                                campoCorreoDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoCorreoDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoCorreoDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoCorreoDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoCorreoDestinatario.setTextColor(Color.BLACK);
                                campoCorreoDestinatario.setTextSize(TamañoLetraContenido);

                                campoDepartamentoDestinatario.setText(DepartamentoDestinatario);
                                campoDepartamentoDestinatario.setWidth(LargoContenido);
                                campoDepartamentoDestinatario.setHeight(AnchoContenido);
                                campoDepartamentoDestinatario.setLayoutParams(parametrosContenido);
                                campoDepartamentoDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoDepartamentoDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoDepartamentoDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoDepartamentoDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoDepartamentoDestinatario.setTextColor(Color.BLACK);
                                campoDepartamentoDestinatario.setTextSize(TamañoLetraContenido);

                                campoEstadoArchivo.setText(EstadoArchivo);
                                campoEstadoArchivo.setWidth(LargoContenido);
                                campoEstadoArchivo.setHeight(AnchoContenido);
                                campoEstadoArchivo.setLayoutParams(parametrosContenido);
                                campoEstadoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoEstadoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoEstadoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoEstadoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoEstadoArchivo.setTextColor(Color.BLACK);
                                campoEstadoArchivo.setTextSize(TamañoLetraContenido);


                                nuevaFila.addView(campoNombreArchivo);
                                nuevaFila.addView(campoFormatoArchivo);
                                nuevaFila.addView(campoFechaSubida);
                                nuevaFila.addView(campoFechaEnvio);
                                nuevaFila.addView(campoUsuarioOriginario);
                                nuevaFila.addView(campoCorreoOriginario);
                                nuevaFila.addView(campoDepartamentoOriginario);
                                nuevaFila.addView(campoUsuarioDestinatario);
                                nuevaFila.addView(campoCorreoDestinatario);
                                nuevaFila.addView(campoDepartamentoDestinatario);
                                nuevaFila.addView(campoEstadoArchivo);
                                tblTablaHistorialArchivos.addView(nuevaFila);
                            }
                        }

                        if (mostrarInfoDefecto != true) {
                            tbrPrimeraFila.setVisibility(VISIBLE);
                            tblTablaHistorialArchivos.addView(tbrPrimeraFila);
                        }

                    } else {
                        tblTablaHistorialArchivos.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoNombreArchivo = new TextView(getActivity());
                            campoFormatoArchivo = new TextView(getActivity());
                            campoFechaSubida = new TextView(getActivity());
                            campoFechaEnvio = new TextView(getActivity());
                            campoUsuarioOriginario = new TextView(getActivity());
                            campoCorreoOriginario = new TextView(getActivity());
                            campoDepartamentoOriginario = new TextView(getActivity());
                            campoUsuarioDestinatario = new TextView(getActivity());
                            campoCorreoDestinatario = new TextView(getActivity());
                            campoDepartamentoDestinatario = new TextView(getActivity());
                            campoEstadoArchivo = new TextView(getActivity());

                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 390);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);

                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            parametrosContenido.setMarginStart(margenContenido);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);


                            RegistroArchivoEntitie registroArchivo = datosOrdenados.get(i);
                            String NombreArchivo  = registroArchivo.getNombreArchivo().trim();
                            String FormatoArchivo = registroArchivo.getFormatoArchivo();

                            String FechaSubida = registroArchivo.getFechaSubida().trim().replace("T", " ");
                            String FechaEnvio = registroArchivo.getFechaEnvio();

                            String UsuarioOriginario = registroArchivo.getNombreOriginario().trim() + " " + registroArchivo.getApellido_1_Originario().trim() + " " + registroArchivo.getApellido_2_Originario().trim();
                            String CorreoOriginario = registroArchivo.getCorreoElectronico_Originario().trim();
                            String DepartamentoOriginario = registroArchivo.getDepartamentoOriginario().trim();

                            String UsuarioDestinatario = registroArchivo.getNombreDestinatario().trim() + " " + registroArchivo.getApellido_1_Destinatario().trim() + " " + registroArchivo.getApellido_2_Destinatario().trim();
                            String CorreoDestinatario = registroArchivo.getCorreoElectronico_Destinatario().trim();
                            String DepartamentoDestinatario = registroArchivo.getDepartamentoDestinatario().trim();
                            String EstadoArchivo = registroArchivo.getEstadoArchivo().trim();

                            if(Objects.equals(correoOriginario, CorreoOriginario)) {
                                mostrarInfoDefecto = true;

                                campoNombreArchivo.setText(NombreArchivo);
                                campoNombreArchivo.setWidth(LargoContenido);
                                campoNombreArchivo.setHeight(AnchoContenido);
                                campoNombreArchivo.setLayoutParams(parametrosContenido);
                                campoNombreArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoNombreArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoNombreArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoNombreArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoNombreArchivo.setTextColor(Color.BLACK);
                                campoNombreArchivo.setTextSize(TamañoLetraContenido);

                                campoFormatoArchivo.setText(FormatoArchivo);
                                campoFormatoArchivo.setWidth(LargoContenido);
                                campoFormatoArchivo.setHeight(AnchoContenido);
                                campoFormatoArchivo.setLayoutParams(parametrosContenido);
                                campoFormatoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoFormatoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoFormatoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoFormatoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoFormatoArchivo.setTextColor(Color.BLACK);
                                campoFormatoArchivo.setTextSize(TamañoLetraContenido);

                                campoFechaSubida.setText(FechaSubida);
                                campoFechaSubida.setWidth(LargoContenido);
                                campoFechaSubida.setHeight(AnchoContenido);
                                campoFechaSubida.setLayoutParams(parametrosContenido);
                                campoFechaSubida.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoFechaSubida.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoFechaSubida.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoFechaSubida.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoFechaSubida.setTextColor(Color.BLACK);
                                campoFechaSubida.setTextSize(TamañoLetraContenido);

                                if(FechaEnvio != null) {
                                    campoFechaEnvio.setText(FechaEnvio.trim().replace("T", " "));
                                    campoFechaEnvio.setWidth(LargoContenido);
                                    campoFechaEnvio.setHeight(AnchoContenido);
                                    campoFechaEnvio.setLayoutParams(parametrosContenido);
                                    campoFechaEnvio.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaEnvio.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaEnvio.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaEnvio.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaEnvio.setTextColor(Color.BLACK);
                                    campoFechaEnvio.setTextSize(TamañoLetraContenido);

                                } else {
                                    campoFechaEnvio.setText("No esta disponible por el momento.");
                                    campoFechaEnvio.setWidth(LargoContenido);
                                    campoFechaEnvio.setHeight(AnchoContenido);
                                    campoFechaEnvio.setLayoutParams(parametrosContenido);
                                    campoFechaEnvio.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaEnvio.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaEnvio.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaEnvio.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaEnvio.setTextColor(Color.BLACK);
                                    campoFechaEnvio.setTextSize(TamañoLetraContenido);
                                }

                                campoUsuarioOriginario.setText(UsuarioOriginario);
                                campoUsuarioOriginario.setWidth(LargoContenido);
                                campoUsuarioOriginario.setHeight(AnchoContenido);
                                campoUsuarioOriginario.setLayoutParams(parametrosContenido);
                                campoUsuarioOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoUsuarioOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoUsuarioOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoUsuarioOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoUsuarioOriginario.setTextColor(Color.BLACK);
                                campoUsuarioOriginario.setTextSize(TamañoLetraContenido);

                                campoCorreoOriginario.setText(CorreoOriginario);
                                campoCorreoOriginario.setWidth(LargoContenido);
                                campoCorreoOriginario.setHeight(AnchoContenido);
                                campoCorreoOriginario.setLayoutParams(parametrosContenido);
                                campoCorreoOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoCorreoOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoCorreoOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoCorreoOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoCorreoOriginario.setTextColor(Color.BLACK);
                                campoCorreoOriginario.setTextSize(TamañoLetraContenido);

                                campoDepartamentoOriginario.setText(DepartamentoOriginario);
                                campoDepartamentoOriginario.setWidth(LargoContenido);
                                campoDepartamentoOriginario.setHeight(AnchoContenido);
                                campoDepartamentoOriginario.setLayoutParams(parametrosContenido);
                                campoDepartamentoOriginario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoDepartamentoOriginario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoDepartamentoOriginario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoDepartamentoOriginario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoDepartamentoOriginario.setTextColor(Color.BLACK);
                                campoDepartamentoOriginario.setTextSize(TamañoLetraContenido);

                                campoUsuarioDestinatario.setText(UsuarioDestinatario);
                                campoUsuarioDestinatario.setWidth(LargoContenido);
                                campoUsuarioDestinatario.setHeight(AnchoContenido);
                                campoUsuarioDestinatario.setLayoutParams(parametrosContenido);
                                campoUsuarioDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoUsuarioDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoUsuarioDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoUsuarioDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoUsuarioDestinatario.setTextColor(Color.BLACK);
                                campoUsuarioDestinatario.setTextSize(TamañoLetraContenido);

                                campoCorreoDestinatario.setText(CorreoDestinatario);
                                campoCorreoDestinatario.setWidth(LargoContenido);
                                campoCorreoDestinatario.setHeight(AnchoContenido);
                                campoCorreoDestinatario.setLayoutParams(parametrosContenido);
                                campoCorreoDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoCorreoDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoCorreoDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoCorreoDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoCorreoDestinatario.setTextColor(Color.BLACK);
                                campoCorreoDestinatario.setTextSize(TamañoLetraContenido);

                                campoDepartamentoDestinatario.setText(DepartamentoDestinatario);
                                campoDepartamentoDestinatario.setWidth(LargoContenido);
                                campoDepartamentoDestinatario.setHeight(AnchoContenido);
                                campoDepartamentoDestinatario.setLayoutParams(parametrosContenido);
                                campoDepartamentoDestinatario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoDepartamentoDestinatario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoDepartamentoDestinatario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoDepartamentoDestinatario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoDepartamentoDestinatario.setTextColor(Color.BLACK);
                                campoDepartamentoDestinatario.setTextSize(TamañoLetraContenido);

                                campoEstadoArchivo.setText(EstadoArchivo);
                                campoEstadoArchivo.setWidth(LargoContenido);
                                campoEstadoArchivo.setHeight(AnchoContenido);
                                campoEstadoArchivo.setLayoutParams(parametrosContenido);
                                campoEstadoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoEstadoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoEstadoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoEstadoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoEstadoArchivo.setTextColor(Color.BLACK);
                                campoEstadoArchivo.setTextSize(TamañoLetraContenido);


                                nuevaFila.addView(campoNombreArchivo);
                                nuevaFila.addView(campoFormatoArchivo);
                                nuevaFila.addView(campoFechaSubida);
                                nuevaFila.addView(campoFechaEnvio);
                                nuevaFila.addView(campoUsuarioOriginario);
                                nuevaFila.addView(campoCorreoOriginario);
                                nuevaFila.addView(campoDepartamentoOriginario);
                                nuevaFila.addView(campoUsuarioDestinatario);
                                nuevaFila.addView(campoCorreoDestinatario);
                                nuevaFila.addView(campoDepartamentoDestinatario);
                                nuevaFila.addView(campoEstadoArchivo);
                                tblTablaHistorialArchivos.addView(nuevaFila);
                            }
                        }

                        if (mostrarInfoDefecto != true) {
                            tbrPrimeraFila.setVisibility(VISIBLE);
                            tblTablaHistorialArchivos.addView(tbrPrimeraFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = " Se finalizo la sesión de su cuenta.";
                        }

                        buscadorHistorialArchivos.setVisibility(GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalCompartido.setVisibility(View.GONE);

                        logitoHistorialArchivos.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que:" + error.replace('"', ' ') + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        buscadorHistorialArchivos.setVisibility(GONE);
                        botonDescargar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);
                        scrollHorizontalCompartido.setVisibility(View.GONE);

                        logitoHistorialArchivos.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
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
            public void onFailure(Call<List<RegistroArchivoEntitie>> call, Throwable t) {
                buscadorHistorialArchivos.setVisibility(GONE);
                botonDescargar.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);
                scrollHorizontalCompartido.setVisibility(View.GONE);

                logitoHistorialArchivos.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void MostrarHistorialArchivosCompartidos(List<RegistroArchivoEntitie> listaArchivo) {
        try {
            tblTablaHistorialArchivosCompartido.removeAllViews();
            tbrPrimeraFilaCompartida.setVisibility(GONE);
            botonSeleccionCompartido.setVisibility(GONE);

            if (listaArchivo == null || listaArchivo.isEmpty()) {
                tbrPrimeraFilaCompartida.setVisibility(VISIBLE);
                tblTablaHistorialArchivosCompartido.addView(tbrPrimeraFilaCompartida);
                botonSeleccionCompartido.setVisibility(VISIBLE);
            }

            for (int i = 0; i < listaArchivo.size(); i++) {
                nuevaFilaCompartido = new TableRow(getActivity());
                nuevaFilaCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table));
                campoCheckBoxCompartido = new CheckBox(getActivity());
                campoArchivoCompartido = new TextView(getActivity());
                campoNombreArchivoCompartido = new TextView(getActivity());
                campoFormatoArchivoCompartido = new TextView(getActivity());
                campoUsuarioOriginarioCompartido = new TextView(getActivity());
                campoCorreoOriginarioCompartido = new TextView(getActivity());
                campoDepartamentoOriginarioCompartido = new TextView(getActivity());
                campoUsuarioDestinatarioCompartido = new TextView(getActivity());
                campoCorreoDestinatarioCompartido = new TextView(getActivity());
                campoDepartamentoDestinatarioCompartido = new TextView(getActivity());
                campoEstadoArchivoCompartido = new TextView(getActivity());

                LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 390);
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


                RegistroArchivoEntitie registroArchivo = listaArchivo.get(i);
                String Archivo = registroArchivo.getArchivo().trim();
                String NombreArchivo = registroArchivo.getNombreArchivo().trim();
                String FormatoArchivo = registroArchivo.getFormatoArchivo();

                String UsuarioOriginario = registroArchivo.getNombreOriginario().trim() + " " + registroArchivo.getApellido_1_Originario().trim() + " " + registroArchivo.getApellido_2_Originario().trim();
                String CorreoOriginario = registroArchivo.getCorreoElectronico_Originario().trim();
                String DepartamentoOriginario = registroArchivo.getDepartamentoOriginario().trim();

                String UsuarioDestinatario = registroArchivo.getNombreDestinatario().trim() + " " + registroArchivo.getApellido_1_Destinatario().trim() + " " + registroArchivo.getApellido_2_Destinatario().trim();
                String CorreoDestinatario = registroArchivo.getCorreoElectronico_Destinatario().trim();
                String DepartamentoDestinatario = registroArchivo.getDepartamentoDestinatario().trim();
                String EstadoArchivo = registroArchivo.getEstadoArchivo().trim();

                campoCheckBoxCompartido.setWidth(LargoCheckBox);
                campoCheckBoxCompartido.setHeight(AnchoCheckBox);
                campoCheckBoxCompartido.setLayoutParams(parametrosCheckBox);
                campoCheckBoxCompartido.setTop(margenTop);
                campoCheckBoxCompartido.setPaddingRelative(0, paddingTopContenido, 0, 0);
                campoCheckBoxCompartido.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                campoCheckBoxCompartido.setTag(registroArchivo);

                campoArchivoCompartido.setText(Archivo);
                campoArchivoCompartido.setWidth(LargoContenido);
                campoArchivoCompartido.setHeight(AnchoContenido);
                campoArchivoCompartido.setLayoutParams(parametrosContenido);
                campoArchivoCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoArchivoCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoArchivoCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoArchivoCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoArchivoCompartido.setTextColor(Color.BLACK);
                campoArchivoCompartido.setTextSize(TamañoLetraContenido);

                campoNombreArchivoCompartido.setText(NombreArchivo);
                campoNombreArchivoCompartido.setWidth(LargoContenido);
                campoNombreArchivoCompartido.setHeight(AnchoContenido);
                campoNombreArchivoCompartido.setLayoutParams(parametrosContenido);
                campoNombreArchivoCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoNombreArchivoCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoNombreArchivoCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoNombreArchivoCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoNombreArchivoCompartido.setTextColor(Color.BLACK);
                campoNombreArchivoCompartido.setTextSize(TamañoLetraContenido);

                campoFormatoArchivoCompartido.setText(FormatoArchivo);
                campoFormatoArchivoCompartido.setWidth(LargoContenido);
                campoFormatoArchivoCompartido.setHeight(AnchoContenido);
                campoFormatoArchivoCompartido.setLayoutParams(parametrosContenido);
                campoFormatoArchivoCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoFormatoArchivoCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoFormatoArchivoCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoFormatoArchivoCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoFormatoArchivoCompartido.setTextColor(Color.BLACK);
                campoFormatoArchivoCompartido.setTextSize(TamañoLetraContenido);

                campoUsuarioOriginarioCompartido.setText(UsuarioOriginario);
                campoUsuarioOriginarioCompartido.setWidth(LargoContenido);
                campoUsuarioOriginarioCompartido.setHeight(AnchoContenido);
                campoUsuarioOriginarioCompartido.setLayoutParams(parametrosContenido);
                campoUsuarioOriginarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoUsuarioOriginarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoUsuarioOriginarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoUsuarioOriginarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoUsuarioOriginarioCompartido.setTextColor(Color.BLACK);
                campoUsuarioOriginarioCompartido.setTextSize(TamañoLetraContenido);

                campoCorreoOriginarioCompartido.setText(CorreoOriginario);
                campoCorreoOriginarioCompartido.setWidth(LargoContenido);
                campoCorreoOriginarioCompartido.setHeight(AnchoContenido);
                campoCorreoOriginarioCompartido.setLayoutParams(parametrosContenido);
                campoCorreoOriginarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoCorreoOriginarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoCorreoOriginarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoCorreoOriginarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoCorreoOriginarioCompartido.setTextColor(Color.BLACK);
                campoCorreoOriginarioCompartido.setTextSize(TamañoLetraContenido);

                campoDepartamentoOriginarioCompartido.setText(DepartamentoOriginario);
                campoDepartamentoOriginarioCompartido.setWidth(LargoContenido);
                campoDepartamentoOriginarioCompartido.setHeight(AnchoContenido);
                campoDepartamentoOriginarioCompartido.setLayoutParams(parametrosContenido);
                campoDepartamentoOriginarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoDepartamentoOriginarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoDepartamentoOriginarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoDepartamentoOriginarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoDepartamentoOriginarioCompartido.setTextColor(Color.BLACK);
                campoDepartamentoOriginarioCompartido.setTextSize(TamañoLetraContenido);

                campoUsuarioDestinatarioCompartido.setText(UsuarioDestinatario);
                campoUsuarioDestinatarioCompartido.setWidth(LargoContenido);
                campoUsuarioDestinatarioCompartido.setHeight(AnchoContenido);
                campoUsuarioDestinatarioCompartido.setLayoutParams(parametrosContenido);
                campoUsuarioDestinatarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoUsuarioDestinatarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoUsuarioDestinatarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoUsuarioDestinatarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoUsuarioDestinatarioCompartido.setTextColor(Color.BLACK);
                campoUsuarioDestinatarioCompartido.setTextSize(TamañoLetraContenido);

                campoCorreoDestinatarioCompartido.setText(CorreoDestinatario);
                campoCorreoDestinatarioCompartido.setWidth(LargoContenido);
                campoCorreoDestinatarioCompartido.setHeight(AnchoContenido);
                campoCorreoDestinatarioCompartido.setLayoutParams(parametrosContenido);
                campoCorreoDestinatarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoCorreoDestinatarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoCorreoDestinatarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoCorreoDestinatarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoCorreoDestinatarioCompartido.setTextColor(Color.BLACK);
                campoCorreoDestinatarioCompartido.setTextSize(TamañoLetraContenido);

                campoDepartamentoDestinatarioCompartido.setText(DepartamentoDestinatario);
                campoDepartamentoDestinatarioCompartido.setWidth(LargoContenido);
                campoDepartamentoDestinatarioCompartido.setHeight(AnchoContenido);
                campoDepartamentoDestinatarioCompartido.setLayoutParams(parametrosContenido);
                campoDepartamentoDestinatarioCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoDepartamentoDestinatarioCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoDepartamentoDestinatarioCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoDepartamentoDestinatarioCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoDepartamentoDestinatarioCompartido.setTextColor(Color.BLACK);
                campoDepartamentoDestinatarioCompartido.setTextSize(TamañoLetraContenido);

                campoEstadoArchivoCompartido.setText(EstadoArchivo);
                campoEstadoArchivoCompartido.setWidth(LargoContenido);
                campoEstadoArchivoCompartido.setHeight(AnchoContenido);
                campoEstadoArchivoCompartido.setLayoutParams(parametrosContenido);
                campoEstadoArchivoCompartido.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                campoEstadoArchivoCompartido.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                campoEstadoArchivoCompartido.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                campoEstadoArchivoCompartido.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                campoEstadoArchivoCompartido.setTextColor(Color.BLACK);
                campoEstadoArchivoCompartido.setTextSize(TamañoLetraContenido);


                nuevaFilaCompartido.addView(campoCheckBoxCompartido);
                nuevaFilaCompartido.addView(campoArchivoCompartido);
                nuevaFilaCompartido.addView(campoNombreArchivoCompartido);
                nuevaFilaCompartido.addView(campoFormatoArchivoCompartido);
                nuevaFilaCompartido.addView(campoUsuarioOriginarioCompartido);
                nuevaFilaCompartido.addView(campoCorreoOriginarioCompartido);
                nuevaFilaCompartido.addView(campoDepartamentoOriginarioCompartido);
                nuevaFilaCompartido.addView(campoUsuarioDestinatarioCompartido);
                nuevaFilaCompartido.addView(campoCorreoDestinatarioCompartido);
                nuevaFilaCompartido.addView(campoDepartamentoDestinatarioCompartido);
                nuevaFilaCompartido.addView(campoEstadoArchivoCompartido);
                tblTablaHistorialArchivosCompartido.addView(nuevaFilaCompartido);
            }
        } catch (Exception error) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void DescargarRegistroArchivo() {
        try {
            Integer cantidadChecks = 0;
            RegistroArchivoEntitie datoSeleccionado = null;

            //En esta primera parte buscara y guardara el documento PDF que el usuario desea descargar.
            for (int i = 0; i < tblTablaHistorialArchivosCompartido.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaHistorialArchivosCompartido.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if (seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (RegistroArchivoEntitie) seleccionDato.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                String archivoCompartido = datoSeleccionado.getArchivo().trim();
                String nombreArchivoCompartido = datoSeleccionado.getNombreArchivo().trim();

                byte[] listaArchivo = Base64.decode(archivoCompartido, Base64.DEFAULT);

                /* Aqui lo que se esta haciendo es crear un archivo privado en la aplicación, pero dirigido al -
                 * almacenamiento externo. Además, se coloco un null en: getExternalFilesDir(), para que pueda -
                 * traer la raiz de los archivos que contiene la aplicación móvil respectivamente.
                 *
                 * Ahora, con el "Reporte-Usuarios-MuniTurrialba.pdf", es unicamente el nombre que va a llevar -
                 * ese archivo como tal. */
                File archivoPDF = new File(getActivity().getExternalFilesDir(null),
                        nombreArchivoCompartido + ".pdf");

                /* Aqui lo que se esta haciendo es usar un FileOutputStream para poder leer el archivo que se -
                 * acaba de crear, de forma que, se pueda colocar toda la información sobre el documento PDF -
                 * que se creo temporalmente. Además, se coloco un: flush() para que todos los datos se fueran -
                 * directamente al archivo que se creo, de forma que asi se pueda evitar que los datos del documento -
                 * PDF se pierdan respectivamente.
                 *
                 * Ahora, también se coloco un close() para pdoer cerrar el lector y que no quedara abierto. */
                FileOutputStream lectorArchivo = new FileOutputStream(archivoPDF);
                lectorArchivo.write(listaArchivo);
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
                construirAlerta.setMessage("Pero en este momento no es posible descargar el archivo debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorHistorialArchivos.setVisibility(GONE);
            botonDescargar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);
            scrollHorizontalCompartido.setVisibility(View.GONE);

            logitoHistorialArchivos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoHistorialArchivos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible descargar el archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}