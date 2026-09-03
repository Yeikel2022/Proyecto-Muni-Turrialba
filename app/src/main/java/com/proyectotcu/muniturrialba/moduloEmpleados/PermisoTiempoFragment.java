package com.proyectotcu.muniturrialba.moduloEmpleados;

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
import android.os.Bundle;

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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionPermisoTiempoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoTiempoInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PermisoTiempoFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombre, txtApellidos, txtCedula, txtDepartamento, txtTipoPermiso,
             txtDescripcion, txtFechaAsignacion, txtFechaFinalizacion, campoNombre,
             campoApellidos, campoCedula, campoDepartamento, campoTipoPermiso,
             campoDescripcion, campoFechaAsignacion, campoFechaFinalizacion,
             txtMensaje;
    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido,
            margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingEndContenido;

    Button botonCrear, botonActualizar, botonEliminar;
    TableRow tbrInfoFila, tbrPrimeraFila, nuevaFila;

    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones;
    TableRow.LayoutParams parametrosContenido, parametrosCheckBox;
    CheckBox botonSeleccion, campoCheckBox;

    TableLayout tblTablaPermisosTiempo;
    ImageView logitoPermisos;
    SearchView buscadorPermisos;
    List<ExtensionPermisoTiempoEntitie> datosOrdenados;

    public static Boolean mensajePermisosTiempo = false;

    //Interfaz que contiene los métodos de la entidad FAQ.
    PermisoTiempoInterface permisoTiempoInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_permisos_tiempo, container, false);
        logitoPermisos = view.findViewById(R.id.img_fotoPermisosTiempo);
        buscadorPermisos = view.findViewById(R.id.sv_buscarPermisosTiempo);

        botonCrear = view.findViewById(R.id.btn_AñadirPermisosTiempo);
        botonActualizar = view.findViewById(R.id.btn_EditarPermisosTiempo);
        botonEliminar = view.findViewById(R.id.btn_EliminarPermisosTiempo);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDatoPermisosTiempo);

        txtNombre = view.findViewById(R.id.txt_NombrePermisosTiempo);
        txtApellidos = view.findViewById(R.id.txt_ApellidosPermisosTiempo);
        txtCedula = view.findViewById(R.id.txt_CedulaPermisosTiempo);
        txtDepartamento = view.findViewById(R.id.txt_DepartamentoPermisosTiempo);
        txtTipoPermiso = view.findViewById(R.id.txt_TipoPermisoTiempo);
        txtDescripcion = view.findViewById(R.id.txt_DescripcionPermisosTiempo);
        txtFechaAsignacion = view.findViewById(R.id.txt_FechaAsignacion_PermisosTiempo);
        txtFechaFinalizacion = view.findViewById(R.id.txt_FechaFinalizacion_PermisosTiempo);
        txtMensaje = view.findViewById(R.id.txt_MensajePermisosTiempo);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontalPermisosTiempo);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_PermisosTiempo);

        tblTablaPermisosTiempo = view.findViewById(R.id.tbl_TablaPermisosTiempo);
        tbrInfoFila = view.findViewById(R.id.tbr_InfoFilaPermisosTiempo);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaPermisosTiempo);

        logitoPermisos.setVisibility(GONE);
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


            buscadorPermisos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    buscadorPermisos.clearFocus();
                    return true;
                }
            });

            buscadorPermisos.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    buscadorPermisos.clearFocus();
                    buscadorPermisos.setIconifiedByDefault(true);
                    return false;
                }
            });


            //Moderador:
            if (campoRol == 1) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearPermisosTiempo());
                    botonActualizar.setOnClickListener(v -> VistaActualizarPermisosTiempo());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este permiso de tiempo de forma permanentemente?")
                                .setTitle("Eliminar Permiso de Tiempo.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarPermisosTiempo(tokenGuardado);
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

                    MostrarPermisosTiempo(tokenGuardado, null, false);
                }
            }


            //Administrador:
            if (campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearPermisosTiempo());
                    botonActualizar.setVisibility(GONE);
                    botonEliminar.setVisibility(GONE);

                    MostrarPermisosTiempo(tokenGuardado, null, false);
                }
            }


            //Empleado:
            if (campoRol == 3) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    scrollHorizontalBotones.setVisibility(GONE);
                    MostrarPermisosTiempo(tokenGuardado, null, false);
                }
            }

        } catch (Exception error) {
            buscadorPermisos.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoPermisos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información de los permisos de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        return view;
    }


    private Integer ValidarPermisosAdmin(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar, Integer Rol) {
        ArrayList<String> listaMensaje = new ArrayList<String>();
        String mensajeProveniente = "";

        switch (Rol) {
            //Moderador:
            case 1:
                if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
                    buscadorPermisos.setVisibility(GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoPermisos.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    if (mensajePermisosTiempo != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajePermisosTiempo = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }

                if(Leer == false) {
                    buscadorPermisos.setVisibility(GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(GONE);
                    scrollHorizontal.setVisibility(GONE);

                    logitoPermisos.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }

                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo permiso de tiempo dentro de este apartado.\n\n");
                }


                if(Actualizar == false) {
                    botonActualizar.setVisibility(GONE);
                    listaMensaje.add("- Actualizar un permiso de tiempo respectivamente.\n\n");
                }


                if(Eliminar == false) {
                    botonEliminar.setVisibility(GONE);
                    listaMensaje.add("- Eliminar un permiso de tiempo respectivamente.");
                }


                if(Leer != true || Crear != true || Actualizar != true || Eliminar != true) {
                    if (listaMensaje.size() == 3) {
                        scrollHorizontalBotones.setVisibility(GONE);
                    }

                    if (mensajePermisosTiempo != true) {
                        for (int i = 0; i < listaMensaje.size(); i++) {
                            mensajeProveniente += listaMensaje.get(i);
                        }

                        AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                        construirAlertaCrear.setIcon(R.drawable.icono_error);

                        construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente)
                                .setTitle("¡Lo sentimos!");

                        construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajePermisosTiempo = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;

            //Administrador:
            case 2:
                if(Leer == false && Crear == false) {
                    buscadorPermisos.setVisibility(GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoPermisos.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    if (mensajePermisosTiempo != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajePermisosTiempo = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }

                if(Leer == false) {
                    buscadorPermisos.setVisibility(GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(GONE);
                    scrollHorizontal.setVisibility(GONE);

                    logitoPermisos.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }


                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo permiso de tiempo dentro de este apartado.");
                }


                if(Leer != true || Crear != true) {
                    scrollHorizontalBotones.setVisibility(GONE);

                    if (mensajePermisosTiempo != true) {
                        for (int i = 0; i < listaMensaje.size(); i++) {
                            mensajeProveniente += listaMensaje.get(i);
                        }

                        AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                        construirAlertaCrear.setIcon(R.drawable.icono_error);

                        construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente)
                                .setTitle("¡Lo sentimos!");

                        construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajePermisosTiempo = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;

            //Empleado:
            case 3:
                if(Leer == false) {
                    buscadorPermisos.setVisibility(GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(GONE);
                    scrollHorizontal.setVisibility(GONE);

                    logitoPermisos.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    if (mensajePermisosTiempo != true) {
                        AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                        construirAlertaCrear.setIcon(R.drawable.icono_error);

                        construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para visualizar esta información.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajePermisosTiempo = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }

                    return 0;
                }

                break;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }

    private void BuscarPrioridad(String tokenUsuario, List<ExtensionPermisoTiempoEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionPermisoTiempoEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarPermisosTiempo(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionPermisoTiempoEntitie permisoTiempoEntitie : listaDatos) {
                    String nombreEmpleado = permisoTiempoEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(permisoTiempoEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el permiso de tiempo debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarPermisosTiempo(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarPermisosTiempo(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            buscadorPermisos.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoPermisos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información del permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void MostrarPermisosTiempo(String tokenUsuario, List<ExtensionPermisoTiempoEntitie> listaActualizada, Boolean autorizacion) {
        Activity nombreActividad = getActivity();

        permisoTiempoInterface = ConexionAPI.Conexion_API_Permiso_Tiempo(nombreActividad);

        Call<List<ExtensionPermisoTiempoEntitie>> mostrarPermisosTiempo = permisoTiempoInterface.obtenerPermisosTiempo(tokenUsuario);

        mostrarPermisosTiempo.enqueue(new Callback<List<ExtensionPermisoTiempoEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionPermisoTiempoEntitie>> call, Response<List<ExtensionPermisoTiempoEntitie>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size() == 1) {
                        tblTablaPermisosTiempo.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            ExtensionPermisoTiempoEntitie permisosTiempo = response.body().get(i);
                            String Nombre  = permisosTiempo.getNombre().trim();
                            String Apellidos = permisosTiempo.getApellido_1().trim() + " " + permisosTiempo.getApellido_2().trim();
                            String Cedula = permisosTiempo.getCedula().trim();
                            String Departamento = permisosTiempo.getDepartamento().trim();
                            String Tipo_Permiso = permisosTiempo.getTipoPermiso().trim();
                            String Descripcion = permisosTiempo.getDescripcion().trim();
                            String Fecha_Asignacion = permisosTiempo.getFechaAsignacion().trim();
                            String Fecha_Finalizacion = permisosTiempo.getFechaFinalizacion().trim();


                            txtNombre.setText(Nombre);
                            txtApellidos.setText(Apellidos);
                            txtCedula.setText(Cedula);
                            txtDepartamento.setText(Departamento);
                            txtTipoPermiso.setText(Tipo_Permiso);
                            txtDescripcion.setText(Descripcion);
                            txtFechaAsignacion.setText(Fecha_Asignacion);
                            txtFechaFinalizacion.setText(Fecha_Finalizacion);

                            botonSeleccion.setTag(permisosTiempo);
                            botonSeleccion.setVisibility(VISIBLE);
                        }
                    }

                    tblTablaPermisosTiempo.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionPermisoTiempoEntitie>() {
                        @Override
                        public int compare(ExtensionPermisoTiempoEntitie o1, ExtensionPermisoTiempoEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaPermisosTiempo.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoTipoPermiso = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());
                            campoFechaAsignacion = new TextView(getActivity());
                            campoFechaFinalizacion = new TextView(getActivity());


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


                            ExtensionPermisoTiempoEntitie permisosTiempo = listaActualizada.get(i);
                            String Nombre  = permisosTiempo.getNombre().trim();
                            String Apellidos = permisosTiempo.getApellido_1().trim() + " " + permisosTiempo.getApellido_2().trim();
                            String Cedula = permisosTiempo.getCedula().trim();
                            String Departamento = permisosTiempo.getDepartamento().trim();
                            String Tipo_Permiso = permisosTiempo.getTipoPermiso().trim();
                            String Descripcion = permisosTiempo.getDescripcion().trim();
                            String Fecha_Asignacion = permisosTiempo.getFechaAsignacion().trim().replace("T", " ");
                            String Fecha_Finalizacion = permisosTiempo.getFechaFinalizacion().trim().replace("T", " ");


                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(permisosTiempo);

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

                            campoTipoPermiso.setText(Tipo_Permiso);
                            campoTipoPermiso.setWidth(LargoContenido);
                            campoTipoPermiso.setHeight(AnchoContenido);
                            campoTipoPermiso.setLayoutParams(parametrosContenido);
                            campoTipoPermiso.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTipoPermiso.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTipoPermiso.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTipoPermiso.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoTipoPermiso.setTextColor(Color.BLACK);
                            campoTipoPermiso.setTextSize(TamañoLetraContenido);

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

                            campoFechaAsignacion.setText(Fecha_Asignacion);
                            campoFechaAsignacion.setWidth(LargoContenido);
                            campoFechaAsignacion.setHeight(AnchoContenido);
                            campoFechaAsignacion.setLayoutParams(parametrosContenido);
                            campoFechaAsignacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaAsignacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaAsignacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaAsignacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaAsignacion.setTextColor(Color.BLACK);
                            campoFechaAsignacion.setTextSize(TamañoLetraContenido);

                            campoFechaFinalizacion.setText(Fecha_Finalizacion);
                            campoFechaFinalizacion.setWidth(LargoContenido);
                            campoFechaFinalizacion.setHeight(AnchoContenido);
                            campoFechaFinalizacion.setLayoutParams(parametrosContenido);
                            campoFechaFinalizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaFinalizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaFinalizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaFinalizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
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
                            tblTablaPermisosTiempo.addView(nuevaFila);
                        }

                    } else {
                        tblTablaPermisosTiempo.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoTipoPermiso = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());
                            campoFechaAsignacion = new TextView(getActivity());
                            campoFechaFinalizacion = new TextView(getActivity());


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


                            ExtensionPermisoTiempoEntitie permisosTiempo = datosOrdenados.get(i);
                            String Nombre  = permisosTiempo.getNombre().trim();
                            String Apellidos = permisosTiempo.getApellido_1().trim() + " " + permisosTiempo.getApellido_2().trim();
                            String Cedula = permisosTiempo.getCedula().trim();
                            String Departamento = permisosTiempo.getDepartamento().trim();
                            String Tipo_Permiso = permisosTiempo.getTipoPermiso().trim();
                            String Descripcion = permisosTiempo.getDescripcion().trim();
                            String Fecha_Asignacion = permisosTiempo.getFechaAsignacion().trim().replace("T", " ");
                            String Fecha_Finalizacion = permisosTiempo.getFechaFinalizacion().trim().replace("T", " ");


                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(permisosTiempo);

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

                            campoTipoPermiso.setText(Tipo_Permiso);
                            campoTipoPermiso.setWidth(LargoContenido);
                            campoTipoPermiso.setHeight(AnchoContenido);
                            campoTipoPermiso.setLayoutParams(parametrosContenido);
                            campoTipoPermiso.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTipoPermiso.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTipoPermiso.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTipoPermiso.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoTipoPermiso.setTextColor(Color.BLACK);
                            campoTipoPermiso.setTextSize(TamañoLetraContenido);

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

                            campoFechaAsignacion.setText(Fecha_Asignacion);
                            campoFechaAsignacion.setWidth(LargoContenido);
                            campoFechaAsignacion.setHeight(AnchoContenido);
                            campoFechaAsignacion.setLayoutParams(parametrosContenido);
                            campoFechaAsignacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaAsignacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaAsignacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaAsignacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaAsignacion.setTextColor(Color.BLACK);
                            campoFechaAsignacion.setTextSize(TamañoLetraContenido);

                            campoFechaFinalizacion.setText(Fecha_Finalizacion);
                            campoFechaFinalizacion.setWidth(LargoContenido);
                            campoFechaFinalizacion.setHeight(AnchoContenido);
                            campoFechaFinalizacion.setLayoutParams(parametrosContenido);
                            campoFechaFinalizacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaFinalizacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaFinalizacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaFinalizacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
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
                            tblTablaPermisosTiempo.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        buscadorPermisos.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoPermisos.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información de los permisos de tiempo debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        buscadorPermisos.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoPermisos.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible visualizar la información de los permisos de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                buscadorPermisos.setVisibility(View.GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);

                logitoPermisos.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
                txtMensaje.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible visualizar la información de los permisos de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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

    private void VistaCrearPermisosTiempo() {
        Intent intentCrearPermisosTiempo = new Intent(getActivity(), PermisoTiempoCrearActivity.class);

        startActivity(intentCrearPermisosTiempo);

        getActivity().finish();
    }

    private void VistaActualizarPermisosTiempo() {
        try {
            Integer cantidadChecks = 0;
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaPermisosTiempo.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaPermisosTiempo.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Nombre = datoSeleccionado.getNombre().trim();
                String primerApellido = datoSeleccionado.getApellido_1().trim();
                String segundoApellido = datoSeleccionado.getApellido_2().trim();

                String Cedula = datoSeleccionado.getCedula().trim();
                String TipoPermiso = datoSeleccionado.getTipoPermiso().trim();

                String Descripcion = datoSeleccionado.getDescripcion().trim();
                String FechaAsignacion = datoSeleccionado.getFechaAsignacion().trim().replace("T", " ");
                String FechaFinalizacion = datoSeleccionado.getFechaFinalizacion().trim().replace("T", " ");


                Intent intentActualizarPermisoTiempo = new Intent(getActivity(), PermisoTiempoActualizarActivity.class);

                intentActualizarPermisoTiempo.putExtra("Nombre", Nombre);
                intentActualizarPermisoTiempo.putExtra("Apellidos", primerApellido + " " + segundoApellido);
                intentActualizarPermisoTiempo.putExtra("Cedula", Cedula);

                intentActualizarPermisoTiempo.putExtra("TipoPermiso", TipoPermiso);
                intentActualizarPermisoTiempo.putExtra("Descripcion", Descripcion);

                intentActualizarPermisoTiempo.putExtra("FechaAsignacion", FechaAsignacion);
                intentActualizarPermisoTiempo.putExtra("FechaFinalizacion", FechaFinalizacion);

                startActivity(intentActualizarPermisoTiempo);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar el permiso de tiempo debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorPermisos.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoPermisos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void EliminarPermisosTiempo(String tokenUsuario) {
        try {
            Integer cantidadChecks = 0;
            ExtensionPermisoTiempoEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaPermisosTiempo.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaPermisosTiempo.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionPermisoTiempoEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String cedulaGuardado = datoSeleccionado.getCedula().trim();

                Activity nombreActividad = getActivity();

                permisoTiempoInterface = ConexionAPI.Conexion_API_Permiso_Tiempo(nombreActividad);

                Call<Boolean> eliminarPermisoTiempo = permisoTiempoInterface.eliminarPermisosTiempo(cedulaGuardado, tokenUsuario);

                eliminarPermisoTiempo.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    "¡El permiso de tiempo seleccionado ha sido eliminado exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarPermisosTiempo(tokenUsuario,  null, false);
                        } else {
                            try {
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = "Se finalizo la sesión de su cuenta.";
                                }

                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible eliminar el permiso de tiempo debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                            } catch (Exception error) {
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible eliminar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible eliminar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el permiso de tiempo debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorPermisos.setVisibility(GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoPermisos.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoPermisos.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar el permiso de tiempo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}