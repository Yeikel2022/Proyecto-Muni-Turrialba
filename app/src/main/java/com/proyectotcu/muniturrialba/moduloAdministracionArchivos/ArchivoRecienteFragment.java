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

public class ArchivoRecienteFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtArchivo, txtNombreArchivo, txtFormatoArchivo, txtFechaSubida, txtFechaActualizacion, txtUsuarioOriginario, txtCorreoOriginario,
            txtDepartamentoOriginario, txtUsuarioDestinatario, txtCorreoDestinatario, txtDepartamentoDestinatario, txtEstadoArchivo, campoArchivo,
            campoNombreArchivo, campoFormatoArchivo, campoFechaSubida, campoFechaActualizacion, campoUsuarioOriginario, campoCorreoOriginario,
            campoDepartamentoOriginario, campoUsuarioDestinatario, campoCorreoDestinatario, campoDepartamentoDestinatario, campoEstadoArchivo,
            txtMensaje;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido, margenContenido,
             margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido, paddingEndContenido;
    Button botonCrear, botonActualizar, botonEliminar, botonCompartir, botonVistaPrevia;

    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones;
    TableRow.LayoutParams parametrosCheckBox, parametrosContenido;
    TableRow tbrPrimeraFila, nuevaFila;
    CheckBox botonSeleccion, campoCheckBox;

    TableLayout tblTablaArchivosRecientes;
    ImageView logitoArchivosRecientes;
    SearchView buscadorArchivosRecientes;
    List<RegistroArchivoEntitie> datosOrdenados = new ArrayList<>();

    public static Boolean mensajeArchivoReciente = false;

    //Interfaz que contiene los métodos de la entidad FAQ.
    RegistroArchivoInterface registroArchivoInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_archivo_reciente, container, false);
        logitoArchivosRecientes = view.findViewById(R.id.img_fotoArchivosRecientes);
        buscadorArchivosRecientes = view.findViewById(R.id.sv_buscarArchivosRecientes);

        botonCrear = view.findViewById(R.id.btn_AñadirArchivosRecientes);
        botonActualizar = view.findViewById(R.id.btn_EditarArchivosRecientes);
        botonEliminar = view.findViewById(R.id.btn_EliminarArchivosRecientes);
        botonCompartir = view.findViewById(R.id.btn_CompartirArchivosRecientes);
        botonVistaPrevia = view.findViewById(R.id.btn_VisualizarArchivosRecientes);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDato_ArchivosRecientes);

        txtArchivo = view.findViewById(R.id.txt_Archivo_ArchivosRecientes);
        txtNombreArchivo = view.findViewById(R.id.txt_NombreArchivo_ArchivosRecientes);
        txtFormatoArchivo = view.findViewById(R.id.txt_FormatoArchivo_ArchivosRecientes);
        txtFechaSubida = view.findViewById(R.id.txt_FechaSubida_ArchivosRecientes);
        txtFechaActualizacion = view.findViewById(R.id.txt_InfoFechaActualizacion_ArchivosRecientes);
        txtUsuarioOriginario = view.findViewById(R.id.txt_UsuarioOriginario_ArchivosRecientes);
        txtCorreoOriginario = view.findViewById(R.id.txt_CorreoOriginario_ArchivosRecientes);
        txtDepartamentoOriginario = view.findViewById(R.id.txt_DepartamentoOriginario_ArchivosRecientes);

        txtUsuarioDestinatario = view.findViewById(R.id.txt_UsuarioDestinatario_ArchivosRecientes);
        txtCorreoDestinatario = view.findViewById(R.id.txt_CorreoDestinatario_ArchivosRecientes);
        txtDepartamentoDestinatario = view.findViewById(R.id.txt_DepartamentoDestinatario_ArchivosRecientes);
        txtEstadoArchivo = view.findViewById(R.id.txt_EstadoArchivo_ArchivosRecientes);
        txtMensaje = view.findViewById(R.id.txt_MensajeArchivosRecientes);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontal_ArchivosRecientes);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_ArchivosRecientes);

        tblTablaArchivosRecientes = view.findViewById(R.id.tbl_TablaContenido_ArchivosRecientes);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaContenido_ArchivosRecientes);

        logitoArchivosRecientes.setVisibility(GONE);
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
            Boolean campoPermisoCompartir = Boolean.parseBoolean(json.optString("permiso_Compartir"));
            String campoCorreo = json.optString("correo");


            //Moderador o administrador:
            if (campoRol == 1 || campoRol == 2 || campoRol == 3) {
                buscadorArchivosRecientes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, query, campoCorreo);
                        buscadorArchivosRecientes.clearFocus();
                        return true;
                    }
                });

                buscadorArchivosRecientes.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        BuscarPrioridad(tokenGuardado, datosOrdenados, "true", campoCorreo);
                        buscadorArchivosRecientes.clearFocus();
                        buscadorArchivosRecientes.setIconifiedByDefault(true);
                        return false;
                    }
                });

                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar,
                        campoPermisoEliminar, campoPermisoCompartir);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearRegistroArchivo());
                    botonActualizar.setOnClickListener(v -> VistaActualizarRegistroArchivo());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este registro de forma permanente?")
                                .setTitle("Eliminar Registro de Archivo.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarRegistroArchivo(tokenGuardado, campoCorreo);
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
                    botonCompartir.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_compartir);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de compartir este archivo respectivamente?")
                                .setTitle("Compartir Archivo.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CompartirArchivo(tokenGuardado, campoCorreo);
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se compartio el archivo!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });
                    botonVistaPrevia.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_archivo_ver);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de visualizar este archivo respectivamente?")
                                .setTitle("Visualizar Archivo Previo.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VerRegistroArchivo();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se pudo ver el archivo previo!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });

                    MostrarRegistrosArchivos(tokenGuardado,null, false, campoCorreo);
                }
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private Integer ValidarPermisosAdmin(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar, Boolean Compartir) {
        ArrayList<String> listaMensaje = new ArrayList<String>();
        String mensajeProveniente = "";

        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false && Compartir == false) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            if(mensajeArchivoReciente != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeArchivoReciente = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }


        if(Leer == false) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(GONE);
            scrollHorizontal.setVisibility(GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));

            listaMensaje.add("- Visualizar esta información.\n\n");
        }


        if(Crear == false) {
            botonCrear.setVisibility(GONE);
            listaMensaje.add("- Crear un nuevo registro de archivo dentro de este apartado.\n\n");
        }


        if(Actualizar == false) {
            botonActualizar.setVisibility(GONE);
            listaMensaje.add("- Actualizar un registro de archivo respectivamente.\n\n");
        }


        if(Eliminar == false) {
            botonEliminar.setVisibility(GONE);
            listaMensaje.add("- Eliminar un registro de archivo respectivamente.\n\n");
        }


        if(Compartir == false) {
            botonCompartir.setVisibility(GONE);
            listaMensaje.add("- Compartir un archivo respectivamente.\n\n");
        }


        if(Leer != true || Crear != true || Actualizar != true || Eliminar != true || Compartir != true) {
            if(listaMensaje.size() == 4) {
                scrollHorizontalBotones.setVisibility(GONE);
            }

            if(mensajeArchivoReciente != true) {
                for(int i = 0; i < listaMensaje.size(); i++) {
                    mensajeProveniente += listaMensaje.get(i);
                }

                AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                construirAlertaCrear.setIcon(R.drawable.icono_error);
                construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente + "Por favor, intentelo más tarde.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeArchivoReciente = true;
                    }
                });

                AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                ejecutarMensajeCrear.show();
            }
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    private void BuscarPrioridad(String tokenUsuario, List<RegistroArchivoEntitie> listaDatos, String textoIngresado, String correoOriginario) {
        try {
            List<RegistroArchivoEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarRegistrosArchivos(tokenUsuario, datosFiltrados, false, correoOriginario);

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

                    MostrarRegistrosArchivos(tokenUsuario, datosFiltrados, false, correoOriginario);

                } else {
                    MostrarRegistrosArchivos(tokenUsuario, datosFiltrados, true, correoOriginario);
                }
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void MostrarRegistrosArchivos(String tokenUsuario, List<RegistroArchivoEntitie> listaActualizada, Boolean autorizacion, String correoOriginario) {

        Activity nombreActividad = getActivity();

        registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(nombreActividad);

        Call<List<RegistroArchivoEntitie>> mostrarArchivos = registroArchivoInterface.obtenerArchivos(tokenUsuario);

        mostrarArchivos.enqueue(new Callback<List<RegistroArchivoEntitie>>() {
            @Override
            public void onResponse(Call<List<RegistroArchivoEntitie>> call, Response<List<RegistroArchivoEntitie>> response) {
                if (response.isSuccessful()) {
                    Boolean mostrarInfoDefecto = false;

                    tblTablaArchivosRecientes.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<RegistroArchivoEntitie>() {
                        @Override
                        public int compare(RegistroArchivoEntitie o1, RegistroArchivoEntitie o2) {
                            return o1.getFechaSubida().compareToIgnoreCase(o2.getFechaSubida());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaArchivosRecientes.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoArchivo = new TextView(getActivity());
                            campoNombreArchivo = new TextView(getActivity());
                            campoFormatoArchivo = new TextView(getActivity());
                            campoFechaSubida = new TextView(getActivity());
                            campoFechaActualizacion = new TextView(getActivity());
                            campoUsuarioOriginario = new TextView(getActivity());
                            campoCorreoOriginario = new TextView(getActivity());
                            campoDepartamentoOriginario = new TextView(getActivity());
                            campoUsuarioDestinatario = new TextView(getActivity());
                            campoCorreoDestinatario = new TextView(getActivity());
                            campoDepartamentoDestinatario = new TextView(getActivity());
                            campoEstadoArchivo = new TextView(getActivity());

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


                            RegistroArchivoEntitie registroArchivo = listaActualizada.get(i);
                            String Archivo  = registroArchivo.getArchivo().trim();
                            String NombreArchivo  = registroArchivo.getNombreArchivo().trim();
                            String FormatoArchivo = registroArchivo.getFormatoArchivo();

                            String FechaSubida = registroArchivo.getFechaSubida().trim().replace("T", " ");
                            String FechaActualizacion = registroArchivo.getFechaActualizacion();

                            String UsuarioOriginario = registroArchivo.getNombreOriginario().trim() + " " + registroArchivo.getApellido_1_Originario().trim() + " " + registroArchivo.getApellido_2_Originario().trim();
                            String CorreoOriginario = registroArchivo.getCorreoElectronico_Originario().trim();
                            String DepartamentoOriginario = registroArchivo.getDepartamentoOriginario().trim();

                            String UsuarioDestinatario = registroArchivo.getNombreDestinatario().trim() + " " + registroArchivo.getApellido_1_Destinatario().trim() + " " + registroArchivo.getApellido_2_Destinatario().trim();
                            String CorreoDestinatario = registroArchivo.getCorreoElectronico_Destinatario().trim();
                            String DepartamentoDestinatario = registroArchivo.getDepartamentoDestinatario().trim();
                            String EstadoArchivo = registroArchivo.getEstadoArchivo().trim();

                            if(Objects.equals(correoOriginario, CorreoOriginario)) {
                                mostrarInfoDefecto = true;

                                campoCheckBox.setWidth(LargoCheckBox);
                                campoCheckBox.setHeight(AnchoCheckBox);
                                campoCheckBox.setLayoutParams(parametrosCheckBox);
                                campoCheckBox.setTop(margenTop);
                                campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                                campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                                campoCheckBox.setTag(registroArchivo);

                                campoArchivo.setText(Archivo);
                                campoArchivo.setWidth(LargoContenido);
                                campoArchivo.setHeight(AnchoContenido);
                                campoArchivo.setLayoutParams(parametrosContenido);
                                campoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoArchivo.setTextColor(Color.BLACK);
                                campoArchivo.setTextSize(TamañoLetraContenido);

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

                                if(FechaActualizacion != null) {
                                    txtFechaActualizacion.setVisibility(VISIBLE);

                                    campoFechaActualizacion.setText(FechaActualizacion.trim().replace("T", " "));
                                    campoFechaActualizacion.setWidth(LargoContenido);
                                    campoFechaActualizacion.setHeight(AnchoContenido);
                                    campoFechaActualizacion.setLayoutParams(parametrosContenido);
                                    campoFechaActualizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaActualizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaActualizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaActualizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaActualizacion.setTextColor(Color.BLACK);
                                    campoFechaActualizacion.setTextSize(TamañoLetraContenido);

                                } else {
                                    campoFechaActualizacion.setText("No esta disponible por el momento.");
                                    campoFechaActualizacion.setWidth(LargoContenido);
                                    campoFechaActualizacion.setHeight(AnchoContenido);
                                    campoFechaActualizacion.setLayoutParams(parametrosContenido);
                                    campoFechaActualizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaActualizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaActualizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaActualizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaActualizacion.setTextColor(Color.BLACK);
                                    campoFechaActualizacion.setTextSize(TamañoLetraContenido);
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


                                nuevaFila.addView(campoCheckBox);
                                nuevaFila.addView(campoArchivo);
                                nuevaFila.addView(campoNombreArchivo);
                                nuevaFila.addView(campoFormatoArchivo);
                                nuevaFila.addView(campoFechaSubida);
                                nuevaFila.addView(campoFechaActualizacion);
                                nuevaFila.addView(campoUsuarioOriginario);
                                nuevaFila.addView(campoCorreoOriginario);
                                nuevaFila.addView(campoDepartamentoOriginario);
                                nuevaFila.addView(campoUsuarioDestinatario);
                                nuevaFila.addView(campoCorreoDestinatario);
                                nuevaFila.addView(campoDepartamentoDestinatario);
                                nuevaFila.addView(campoEstadoArchivo);
                                tblTablaArchivosRecientes.addView(nuevaFila);
                            }
                        }

                        if (mostrarInfoDefecto != true) {
                            tbrPrimeraFila.setVisibility(VISIBLE);
                            tblTablaArchivosRecientes.addView(tbrPrimeraFila);
                            botonSeleccion.setVisibility(VISIBLE);
                        }

                    } else {
                        tblTablaArchivosRecientes.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoArchivo = new TextView(getActivity());
                            campoNombreArchivo = new TextView(getActivity());
                            campoFormatoArchivo = new TextView(getActivity());
                            campoFechaSubida = new TextView(getActivity());
                            campoFechaActualizacion = new TextView(getActivity());
                            campoUsuarioOriginario = new TextView(getActivity());
                            campoCorreoOriginario = new TextView(getActivity());
                            campoDepartamentoOriginario = new TextView(getActivity());
                            campoUsuarioDestinatario = new TextView(getActivity());
                            campoCorreoDestinatario = new TextView(getActivity());
                            campoDepartamentoDestinatario = new TextView(getActivity());
                            campoEstadoArchivo = new TextView(getActivity());

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


                            RegistroArchivoEntitie registroArchivo = datosOrdenados.get(i);
                            String Archivo  = registroArchivo.getArchivo().trim();
                            String NombreArchivo  = registroArchivo.getNombreArchivo().trim();
                            String FormatoArchivo = registroArchivo.getFormatoArchivo();

                            String FechaSubida = registroArchivo.getFechaSubida().trim().replace("T", " ");
                            String FechaActualizacion = registroArchivo.getFechaActualizacion();

                            String UsuarioOriginario = registroArchivo.getNombreOriginario().trim() + " " + registroArchivo.getApellido_1_Originario().trim() + " " + registroArchivo.getApellido_2_Originario().trim();
                            String CorreoOriginario = registroArchivo.getCorreoElectronico_Originario().trim();
                            String DepartamentoOriginario = registroArchivo.getDepartamentoOriginario().trim();

                            String UsuarioDestinatario = registroArchivo.getNombreDestinatario().trim() + " " + registroArchivo.getApellido_1_Destinatario().trim() + " " + registroArchivo.getApellido_2_Destinatario().trim();
                            String CorreoDestinatario = registroArchivo.getCorreoElectronico_Destinatario().trim();
                            String DepartamentoDestinatario = registroArchivo.getDepartamentoDestinatario().trim();
                            String EstadoArchivo = registroArchivo.getEstadoArchivo().trim();

                            if(Objects.equals(correoOriginario, CorreoOriginario)) {
                                mostrarInfoDefecto = true;

                                campoCheckBox.setWidth(LargoCheckBox);
                                campoCheckBox.setHeight(AnchoCheckBox);
                                campoCheckBox.setLayoutParams(parametrosCheckBox);
                                campoCheckBox.setTop(margenTop);
                                campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                                campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                                campoCheckBox.setTag(registroArchivo);

                                campoArchivo.setText(Archivo);
                                campoArchivo.setWidth(LargoContenido);
                                campoArchivo.setHeight(AnchoContenido);
                                campoArchivo.setLayoutParams(parametrosContenido);
                                campoArchivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                campoArchivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                campoArchivo.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                campoArchivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                campoArchivo.setTextColor(Color.BLACK);
                                campoArchivo.setTextSize(TamañoLetraContenido);

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

                                if(FechaActualizacion != null) {
                                    campoFechaActualizacion.setText(FechaActualizacion.trim().replace("T", " "));
                                    campoFechaActualizacion.setWidth(LargoContenido);
                                    campoFechaActualizacion.setHeight(AnchoContenido);
                                    campoFechaActualizacion.setLayoutParams(parametrosContenido);
                                    campoFechaActualizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaActualizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaActualizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaActualizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaActualizacion.setTextColor(Color.BLACK);
                                    campoFechaActualizacion.setTextSize(TamañoLetraContenido);

                                } else {
                                    campoFechaActualizacion.setText("No esta disponible por el momento.");
                                    campoFechaActualizacion.setWidth(LargoContenido);
                                    campoFechaActualizacion.setHeight(AnchoContenido);
                                    campoFechaActualizacion.setLayoutParams(parametrosContenido);
                                    campoFechaActualizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                                    campoFechaActualizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                                    campoFechaActualizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                                    campoFechaActualizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                                    campoFechaActualizacion.setTextColor(Color.BLACK);
                                    campoFechaActualizacion.setTextSize(TamañoLetraContenido);
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


                                nuevaFila.addView(campoCheckBox);
                                nuevaFila.addView(campoArchivo);
                                nuevaFila.addView(campoNombreArchivo);
                                nuevaFila.addView(campoFormatoArchivo);
                                nuevaFila.addView(campoFechaSubida);
                                nuevaFila.addView(campoFechaActualizacion);
                                nuevaFila.addView(campoUsuarioOriginario);
                                nuevaFila.addView(campoCorreoOriginario);
                                nuevaFila.addView(campoDepartamentoOriginario);
                                nuevaFila.addView(campoUsuarioDestinatario);
                                nuevaFila.addView(campoCorreoDestinatario);
                                nuevaFila.addView(campoDepartamentoDestinatario);
                                nuevaFila.addView(campoEstadoArchivo);
                                tblTablaArchivosRecientes.addView(nuevaFila);
                            }
                        }

                        if (mostrarInfoDefecto != true) {
                            tbrPrimeraFila.setVisibility(VISIBLE);
                            tblTablaArchivosRecientes.addView(tbrPrimeraFila);
                            botonSeleccion.setVisibility(VISIBLE);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = " Se finalizo la sesión de su cuenta.";
                        }

                        buscadorArchivosRecientes.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonCompartir.setVisibility(View.GONE);
                        botonVistaPrevia.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoArchivosRecientes.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
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
                        buscadorArchivosRecientes.setVisibility(GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonCompartir.setVisibility(View.GONE);
                        botonVistaPrevia.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoArchivosRecientes.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
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
                buscadorArchivosRecientes.setVisibility(GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonCompartir.setVisibility(View.GONE);
                botonVistaPrevia.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);

                logitoArchivosRecientes.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
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


    private void VistaCrearRegistroArchivo() {
        Intent intentArchivosRecientes = new Intent(getActivity(), ArchivoRecienteCrearActivity.class);

        startActivity(intentArchivosRecientes);

        getActivity().finish();
    }


    private void VistaActualizarRegistroArchivo() {
        try {
            Integer cantidadChecks = 0;
            RegistroArchivoEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaArchivosRecientes.getChildCount(); i++) {
                TableRow reportesDatos = (TableRow) tblTablaArchivosRecientes.getChildAt(i);
                CheckBox seleccionReportes = (CheckBox) reportesDatos.getChildAt(0);


                if(seleccionReportes.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (RegistroArchivoEntitie) seleccionReportes.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                String archivo = datoSeleccionado.getArchivo().trim();
                String nombreArchivo = datoSeleccionado.getNombreArchivo().trim();
                String formatoArchivo = datoSeleccionado.getFormatoArchivo().trim();
                String fechaSubida = datoSeleccionado.getFechaSubida().trim();

                String nombreDestinatario = datoSeleccionado.getNombreDestinatario().trim();
                String primerApellidoDestinatario = datoSeleccionado.getApellido_1_Destinatario().trim();
                String segundoApellidoDestinatario = datoSeleccionado.getApellido_2_Destinatario().trim();

                String correoDestinatario = datoSeleccionado.getCorreoElectronico_Destinatario().trim();
                String departamentoDestinatario = datoSeleccionado.getDepartamentoDestinatario().trim();


                Intent intentArchivoActualizar = new Intent(getActivity(), ArchivoRecienteActualizarActivity.class);

                intentArchivoActualizar.putExtra("archivo_Destinatario", archivo);
                intentArchivoActualizar.putExtra("nombreArchivo_Destinatario", nombreArchivo);
                intentArchivoActualizar.putExtra("formatoArchivo_Destinatario", formatoArchivo);
                intentArchivoActualizar.putExtra("fechaSubida_Destinatario", fechaSubida);

                intentArchivoActualizar.putExtra("nombre_Destinatario", nombreDestinatario);
                intentArchivoActualizar.putExtra("primerApellido_Destinatario", primerApellidoDestinatario);
                intentArchivoActualizar.putExtra("segundoApellido_Destinatario", segundoApellidoDestinatario);

                intentArchivoActualizar.putExtra("correoElectronico_Destinatario", correoDestinatario);
                intentArchivoActualizar.putExtra("correoElectronico_Destinatario_Respaldo", correoDestinatario);
                intentArchivoActualizar.putExtra("departamento_Destinatario", departamentoDestinatario);

                startActivity(intentArchivoActualizar);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar el registro del archivo debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void EliminarRegistroArchivo(String tokenUsuario, String correoOriginario) {
        try {
            Integer cantidadChecks = 0;
            RegistroArchivoEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaArchivosRecientes.getChildCount(); i++) {
                TableRow reportesDatos = (TableRow) tblTablaArchivosRecientes.getChildAt(i);
                CheckBox seleccionReportes = (CheckBox) reportesDatos.getChildAt(0);


                if(seleccionReportes.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (RegistroArchivoEntitie) seleccionReportes.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String correoGuardado = datoSeleccionado.getCorreoElectronico_Destinatario().trim();
                String fechaSubidaOriginarioGuardado = datoSeleccionado.getFechaSubida().trim();

                Activity nombreActividad = getActivity();
                registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(nombreActividad);

                Call<Boolean> eliminarArchivo = registroArchivoInterface.eliminarRegistroArchivo(correoGuardado, fechaSubidaOriginarioGuardado, tokenUsuario);

                eliminarArchivo.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(getActivity(),
                                    "¡El registro del archivo seleccionado ha sido eliminado exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarRegistrosArchivos(tokenUsuario, null, false, correoOriginario);
                        } else {
                            try {
                                /* Esto permite leer el error del Body, de modo -
                                 * que sirva en el debug. */
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = " Se finalizo la sesión de su cuenta.";
                                }

                                /* Esto es para imprimir los mensajes de error. */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible eliminar el registro del archivo debido a que:" + error.replace('"', ' ') + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                                /* Este sirve solo para el logcat:
                                 * System.out.println(error); */
                            } catch (Exception error) {
                                /* El printStackTrace(), sirve para aspectos de depuración.
                                 * Esto debido a que ayuda a entender donde y porque ocurrio -
                                 * un error durante la ejecución del proyecto. En este caso -
                                 * las excepciones respectivamente.
                                 *
                                 * error.printStackTrace(); */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible eliminar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();
                            }
                        }
                    }

                    /* Aqui es para saber si hubo un fallo en dar la respuesta -
                     * por parte del API. */
                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        /* Sirve para imprimir el mensaje que se recibio -
                         * anteriormente.
                         *
                         * NOTA: Este comando es para ver que fallo, el -
                         * usuario no lo debe ver:
                         * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                         * Toast.LENGTH_SHORT).show(); */
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible eliminar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    }
                });

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el registro del archivo debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void CompartirArchivo(String tokenUsuario, String correoOriginario) {
        try {
            Integer cantidadChecks = 0;
            RegistroArchivoEntitie datoSeleccionado = null;

            //En esta primera parte buscara y guardara el documento PDF que el usuario desea descargar.
            for (int i = 0; i < tblTablaArchivosRecientes.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaArchivosRecientes.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if (seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (RegistroArchivoEntitie) seleccionDato.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                String tokenUsuarioRecibido = tokenUsuario;
                String correoOriginarioGuardado = datoSeleccionado.getCorreoElectronico_Originario().trim();

                String fechaSubidaOriginarioGuardado = datoSeleccionado.getFechaSubida().trim();
                String correoDestinatarioGuardado = datoSeleccionado.getCorreoElectronico_Destinatario().trim();

                Activity nombreActividad = getActivity();

                registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(nombreActividad);

                RegistroArchivoEntitie registroArchivoEntitie = new RegistroArchivoEntitie(null, null, null,
                        fechaSubidaOriginarioGuardado, null, null, null, correoOriginarioGuardado, null,
                        null, null, null, correoDestinatarioGuardado, null,
                        "Compartido");

                Call<Boolean> compartirArchivo = registroArchivoInterface.compartirRegistroArchivo(registroArchivoEntitie, tokenUsuarioRecibido);

                compartirArchivo.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(getActivity(),
                                    "¡El archivo seleccionado ha sido compartido exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarRegistrosArchivos(tokenUsuario, null, false, correoOriginario);
                        } else {
                            try {
                                /* Esto permite leer el error del Body, de modo -
                                 * que sirva en el debug. */
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = " Se finalizo la sesión de su cuenta.";
                                }

                                /* Esto es para imprimir los mensajes de error. */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible compartir el archivo debido a que:" + error.replace('"', ' ') + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                                /* Este sirve solo para el logcat:
                                 * System.out.println(error); */
                            } catch (Exception error) {
                                /* El printStackTrace(), sirve para aspectos de depuración.
                                 * Esto debido a que ayuda a entender donde y porque ocurrio -
                                 * un error durante la ejecución del proyecto. En este caso -
                                 * las excepciones respectivamente.
                                 *
                                 * error.printStackTrace(); */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible compartir el archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();
                            }
                        }
                    }

                    /* Aqui es para saber si hubo un fallo en dar la respuesta -
                     * por parte del API. */
                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        /* Sirve para imprimir el mensaje que se recibio -
                         * anteriormente.
                         *
                         * NOTA: Este comando es para ver que fallo, el -
                         * usuario no lo debe ver:
                         * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                         * Toast.LENGTH_SHORT).show(); */
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible compartir el archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    }
                });

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible compartir el archivo debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible compartir el archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void VerRegistroArchivo() {
        try {
            Integer cantidadChecks = 0;
            RegistroArchivoEntitie datoSeleccionado = null;

            //En esta primera parte buscara y guardara el documento PDF que el usuario desea descargar.
            for (int i = 0; i < tblTablaArchivosRecientes.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaArchivosRecientes.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if (seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (RegistroArchivoEntitie) seleccionDato.getTag();
                }
            }


            if (cantidadChecks == 1 && datoSeleccionado != null) {
                String archivoSeleccionado = datoSeleccionado.getArchivo().trim();
                String nombreArchivoSeleccionado = datoSeleccionado.getNombreArchivo().trim();

                byte[] listaArchivo = Base64.decode(archivoSeleccionado, Base64.DEFAULT);

                /* Aqui lo que se esta haciendo es crear un archivo privado en la aplicación, pero dirigido al -
                 * almacenamiento externo. Además, se coloco un null en: getExternalFilesDir(), para que pueda -
                 * traer la raiz de los archivos que contiene la aplicación móvil respectivamente.
                 *
                 * Ahora, con el "Reporte-Usuarios-MuniTurrialba.pdf", es unicamente el nombre que va a llevar -
                 * ese archivo como tal. */
                File archivoPDF = new File(getActivity().getExternalFilesDir(null),
                        nombreArchivoSeleccionado + ".pdf");

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
                construirAlerta.setMessage("Pero en este momento no es posible visualizar el archivo debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorArchivosRecientes.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonCompartir.setVisibility(View.GONE);
            botonVistaPrevia.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoArchivosRecientes.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoArchivosRecientes.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar el archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}