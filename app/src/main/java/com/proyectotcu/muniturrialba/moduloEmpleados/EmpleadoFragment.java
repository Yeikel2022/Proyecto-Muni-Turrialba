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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionEmpleadoUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EmpleadoFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombre, txtApellidos, txtEdad, txtCedula, txtTelefono, txtCorreo_Electronico,
             txtContraseña, txtNombreRol, txtFechaCreacion, txtDepartamento, txtActivo, campoNombre,
             campoApellidos, campoEdad, campoCedula, campoTelefono, campoCorreo_Electronico, campoContraseña,
             campoNombreRol, campoFechaCreacion, campoDepartamento, campoActivo, txtMensaje;

    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido,
            margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingEndContenido;


    Button botonCrear, botonActualizar, botonEliminar, botonPermisos;
    TableRow tbrInfoFila, tbrPrimeraFila, nuevaFila;

    TableRow.LayoutParams parametrosContenido, parametrosCheckBox;
    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones;
    CheckBox botonSeleccion, campoCheckBox;

    TableLayout tblTablaEmpleados;
    ImageView logitoEmpleados;
    SearchView buscadorEmpleados;
    List<ExtensionEmpleadoUsuarioEntitie> datosOrdenados;

    public static Boolean mensajeEmpleados = false;


    //Interfaz que contiene los métodos de la entidad FAQ.
    EmpleadoInterface empleadoInterface;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_empleados, container, false);
        logitoEmpleados = view.findViewById(R.id.img_fotoEmpleados);
        buscadorEmpleados = view.findViewById(R.id.sv_buscarEmpleados);

        botonCrear = view.findViewById(R.id.btn_CrearEmpleados);
        botonActualizar = view.findViewById(R.id.btn_ActualizarEmpleados);
        botonEliminar = view.findViewById(R.id.btn_EliminarEmpleados);
        botonPermisos = view.findViewById(R.id.btn_PermisosUsuarioEmpleado);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDatoEmpleados);


        txtNombre = view.findViewById(R.id.txt_NombreEmpleados);
        txtApellidos = view.findViewById(R.id.txt_ApellidosEmpleados);
        txtEdad = view.findViewById(R.id.txt_EdadEmpleados);
        txtCedula = view.findViewById(R.id.txt_CedulaEmpleados);
        txtTelefono = view.findViewById(R.id.txt_TelefonoEmpleados);
        txtCorreo_Electronico = view.findViewById(R.id.txt_CorreoEmpleados);
        txtContraseña = view.findViewById(R.id.txt_PasswordEmpleados);
        txtNombreRol = view.findViewById(R.id.txt_RolEmpleados);
        txtFechaCreacion = view.findViewById(R.id.txt_FechaCreacionEmpleados);
        txtDepartamento = view.findViewById(R.id.txt_DepartamentoEmpleados);
        txtActivo = view.findViewById(R.id.txt_ActivoEmpleados);
        txtMensaje = view.findViewById(R.id.txt_MensajeEmpleados);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontalEmpleados);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontal_BotonesEmpleados);
        tblTablaEmpleados = view.findViewById(R.id.tbl_TablaEmpleados);
        tbrInfoFila = view.findViewById(R.id.tbr_InfoFilaEmpleados);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaEmpleados);

        logitoEmpleados.setVisibility(GONE);
        txtMensaje.setVisibility(GONE);

        try {
            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getActivity().getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            /* Aqui lo que se esta haciendo es crear una lista con todas las partes -
             * separadas que contiene el token recibido por parte del API. Esto por -
             * medio del comando: "split("\\.")", el cual nos ayuda a separar dicho -
             * token por los puntos que contiene, de ahi el \\., ya que es una expre-
             * sión regular que nos permite realizar esa acción respectivamente. */
            String[] partesToken = tokenGuardado.split("\\.");

            /* Aqui lo que se esta haciendo es decodificar la parte que contiene los -
             * claims del token, esto porque se necesita saber principalmente el rol -
             * y los permisos de dicho usuario. */
            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
             * para poder almacenar correctamente los datos que fueron gu -
             * ardados en la variable: "cuerpoToken". Esto porque el token -
             * en si, esta en un formato JSON respectivamente. */
            JSONObject json = new JSONObject(cuerpoToken);

            /* Después de crear ese objeto, entonces lo que se haria es -
             * guardar el rol y los permisos del usuario que inicio sesión en el campo respectivo, de modo que así se -
             * puedan utilizar para validar cuales opciones puede acceder -
             * y también para verificar si dicho usuario tiene la autorización -
             * necesaria respectivamente. */
            Integer campoRol = Integer.parseInt(json.optString("rol"));
            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));


            buscadorEmpleados.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    buscadorEmpleados.clearFocus();
                    return true;
                }
            });

            buscadorEmpleados.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    buscadorEmpleados.clearFocus();
                    buscadorEmpleados.setIconifiedByDefault(true);
                    return false;
                }
            });


            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador" -
             * o "Administrador". Y si no, entonces no lo dejaria acceder a las opciones del -
             * perfil. */
            if (campoRol == 1) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearEmpleados());
                    botonActualizar.setOnClickListener(v -> VistaActualizarEmpleados());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este empleado de forma permanentemente?")
                                .setTitle("Eliminar Empleado(a).");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarEmpleados(tokenGuardado);
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
                    botonPermisos.setOnClickListener(v -> VistaPermisosUsuariosEmpleados());

                    MostrarEmpleados(tokenGuardado,  null, false);
                }
            }



            if (campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearEmpleados());
                    botonActualizar.setOnClickListener(v -> VistaActualizarEmpleados());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este empleado de forma permanentemente?")
                                .setTitle("Eliminar Empleado(a).");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarEmpleados(tokenGuardado);
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

                    MostrarEmpleados(tokenGuardado,  null, false);
                }
            }

        } catch (Exception error) {
            buscadorEmpleados.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoEmpleados.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información de los empleados(as) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    buscadorEmpleados.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);
                    botonPermisos.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoEmpleados.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));
                    /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
                     * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
                     * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
                     * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

                    if(mensajeEmpleados != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeEmpleados = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }


                if(Leer == false) {
                    buscadorEmpleados.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);
                    botonPermisos.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(GONE);
                    scrollHorizontal.setVisibility(GONE);

                    logitoEmpleados.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }


                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo empleado(a) dentro de este apartado.\n\n");
                }


                if(Actualizar == false) {
                    botonActualizar.setVisibility(GONE);
                    botonPermisos.setVisibility(GONE);

                    listaMensaje.add("- Actualizar un empleado(a) respectivamente.\n\n");
                    listaMensaje.add("- Asignar permisos a un empleado(a) respectivamente.\n\n");
                }


                if(Eliminar == false) {
                    botonEliminar.setVisibility(GONE);
                    listaMensaje.add("- Eliminar un empleado(a) respectivamente.");
                }


                if(Leer != true || Crear != true || Actualizar != true || Eliminar != true) {
                    if(listaMensaje.size() == 4) {
                        scrollHorizontalBotones.setVisibility(GONE);
                    }

                    if(mensajeEmpleados != true) {
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
                                mensajeEmpleados = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;

            //Administrador:
            case 2:
                botonPermisos.setVisibility(GONE);

                if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
                    buscadorEmpleados.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoEmpleados.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));
                    /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
                     * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
                     * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
                     * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

                    if(mensajeEmpleados != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeEmpleados = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }


                if(Leer == false) {
                    buscadorEmpleados.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(GONE);
                    scrollHorizontal.setVisibility(GONE);

                    logitoEmpleados.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }


                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo empleado(a) dentro de este apartado.\n\n");
                }


                if(Actualizar == false) {
                    botonActualizar.setVisibility(GONE);
                    listaMensaje.add("- Actualizar un empleado(a) respectivamente.\n\n");
                }


                if(Eliminar == false) {
                    botonEliminar.setVisibility(GONE);
                    listaMensaje.add("- Eliminar un empleado(a) respectivamente.");
                }


                if(Leer != true || Crear != true || Actualizar != true || Eliminar != true) {
                    if(listaMensaje.size() == 3) {
                        scrollHorizontalBotones.setVisibility(GONE);
                    }

                    if(mensajeEmpleados != true) {
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
                                mensajeEmpleados = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }

    private void BuscarPrioridad(String tokenUsuario, List<ExtensionEmpleadoUsuarioEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionEmpleadoUsuarioEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarEmpleados(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionEmpleadoUsuarioEntitie empleadoEntitie : listaDatos) {
                    String nombreEmpleado = empleadoEntitie.getNombre_Empleado().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(empleadoEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el registro del empleado(a) debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarEmpleados(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarEmpleados(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            buscadorEmpleados.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoEmpleados.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información del empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");


            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void MostrarEmpleados(String tokenUsuario, List<ExtensionEmpleadoUsuarioEntitie> listaActualizada, Boolean autorizacion) {
        /* Aquí se llama la conexión del API. Además de indicarle -
         * también que en este fragmento (y clase) se esta pidiendo -
         * esa conexión del API como tal, de ahí el porque se esta -
         * pasando lo que tiene la variable: "nombreActividad".
         *
         * NOTA: Se uso un getActivity() en la variable: "nombreActividad" -
         * porque es la manera en la cual se puede saber cual es la actividad -
         * que esta haciendo uso de los fragmentos (que este caso es el menu -
         * principal), haciendo que la aplicacion puede saber donde se esta -
         * haciendo el llamado. Osea una instancia de la clase. */
        Activity nombreActividad = getActivity();
        empleadoInterface = ConexionAPI.Conexion_API_Empleado(nombreActividad);

        /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
         * así poder enviar el correo y el token para que el API lo pueda recibir y obtener -
         * los datos del usuario. Esto por medio de la interfaz respectiva. */
        Call<List<ExtensionEmpleadoUsuarioEntitie>> mostrarEmpleados = empleadoInterface.obtenerEmpleados(tokenUsuario);

        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
         * el metodo GET.
         *
         * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
         * podemos saber la posible respuesta que pudo brindar el API como tal. */
        mostrarEmpleados.enqueue(new Callback<List<ExtensionEmpleadoUsuarioEntitie>>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<List<ExtensionEmpleadoUsuarioEntitie>> call, Response<List<ExtensionEmpleadoUsuarioEntitie>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size() == 1) {
                        tblTablaEmpleados.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            ExtensionEmpleadoUsuarioEntitie empleados = response.body().get(i);
                            String Nombre  = empleados.getNombre_Empleado().trim();
                            String Apellidos = empleados.getApellido1_Empleado().trim() + " " + empleados.getApellido2_Empleado().trim();
                            Integer Edad = empleados.getEdad_Empleado();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Telefono = empleados.getTelefono_Empleado().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Contraseña = empleados.getContraseña_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();
                            String Fecha_Creacion = empleados.getFecha_Creacion_Empleado().trim();
                            String Departamento = empleados.getDepartamento().trim();
                            Boolean Activo = empleados.getActivo();


                            txtNombre.setText(Nombre);
                            txtApellidos.setText(Apellidos);
                            txtEdad.setText(Edad.toString());
                            txtCedula.setText(Cedula);
                            txtTelefono.setText(Telefono);
                            txtCorreo_Electronico.setText(Correo_Electronico);
                            txtContraseña.setText(Contraseña);
                            txtNombreRol.setText(Nombre_Rol);
                            txtFechaCreacion.setText(Fecha_Creacion);
                            txtDepartamento.setText(Departamento);
                            txtActivo.setText(Activo.toString());
                            botonSeleccion.setTag(empleados);
                            botonSeleccion.setVisibility(VISIBLE);
                        }
                    }

                    tblTablaEmpleados.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionEmpleadoUsuarioEntitie>() {
                        @Override
                        public int compare(ExtensionEmpleadoUsuarioEntitie o1, ExtensionEmpleadoUsuarioEntitie o2) {
                            return o1.getNombre_Empleado().compareToIgnoreCase(o2.getNombre_Empleado());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaEmpleados.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoEdad = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoTelefono = new TextView(getActivity());
                            campoCorreo_Electronico = new TextView(getActivity());
                            campoContraseña = new TextView(getActivity());
                            campoNombreRol = new TextView(getActivity());
                            campoFechaCreacion = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoActivo = new TextView(getActivity());


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
                            Integer Edad = empleados.getEdad_Empleado();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Telefono = empleados.getTelefono_Empleado().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Contraseña = empleados.getContraseña_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();
                            String Fecha_Creacion = empleados.getFecha_Creacion_Empleado().trim().replace("T", " ");
                            String Departamento = empleados.getDepartamento().trim();
                            Boolean Activo = empleados.getActivo();


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

                            campoEdad.setText(Edad.toString());
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

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

                            campoTelefono.setText(Telefono);
                            campoTelefono.setWidth(LargoContenido);
                            campoTelefono.setHeight(AnchoContenido);
                            campoTelefono.setLayoutParams(parametrosContenido);
                            campoTelefono.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTelefono.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTelefono.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTelefono.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoTelefono.setTextColor(Color.BLACK);
                            campoTelefono.setTextSize(TamañoLetraContenido);

                            campoCorreo_Electronico.setText(Correo_Electronico);
                            campoCorreo_Electronico.setWidth(LargoContenido);
                            campoCorreo_Electronico.setHeight(AnchoContenido);
                            campoCorreo_Electronico.setLayoutParams(parametrosContenido);
                            campoCorreo_Electronico.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo_Electronico.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo_Electronico.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo_Electronico.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCorreo_Electronico.setTextColor(Color.BLACK);
                            campoCorreo_Electronico.setTextSize(TamañoLetraContenido);

                            campoContraseña.setText(Contraseña);
                            campoContraseña.setWidth(LargoContenido);
                            campoContraseña.setHeight(AnchoContenido);
                            campoContraseña.setLayoutParams(parametrosContenido);
                            campoContraseña.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoContraseña.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoContraseña.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoContraseña.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoContraseña.setTextColor(Color.BLACK);
                            campoContraseña.setTextSize(TamañoLetraContenido);

                            campoNombreRol.setText(Nombre_Rol);
                            campoNombreRol.setWidth(LargoContenido);
                            campoNombreRol.setHeight(AnchoContenido);
                            campoNombreRol.setLayoutParams(parametrosContenido);
                            campoNombreRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombreRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombreRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombreRol.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombreRol.setTextColor(Color.BLACK);
                            campoNombreRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(Fecha_Creacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

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

                            campoActivo.setText(Activo.toString());
                            campoActivo.setWidth(LargoContenido);
                            campoActivo.setHeight(AnchoContenido);
                            campoActivo.setLayoutParams(parametrosContenido);
                            campoActivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoActivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoActivo.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                            campoActivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoActivo.setTextColor(Color.BLACK);
                            campoActivo.setTextSize(TamañoLetraContenido);

                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoTelefono);
                            nuevaFila.addView(campoCorreo_Electronico);
                            nuevaFila.addView(campoContraseña);
                            nuevaFila.addView(campoNombreRol);
                            nuevaFila.addView(campoFechaCreacion);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoActivo);
                            tblTablaEmpleados.addView(nuevaFila);
                        }

                    } else {
                        tblTablaEmpleados.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoEdad = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoTelefono = new TextView(getActivity());
                            campoCorreo_Electronico = new TextView(getActivity());
                            campoContraseña = new TextView(getActivity());
                            campoNombreRol = new TextView(getActivity());
                            campoFechaCreacion = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoActivo = new TextView(getActivity());


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
                            Integer Edad = empleados.getEdad_Empleado();
                            String Cedula = empleados.getCedula_Empleado().trim();
                            String Telefono = empleados.getTelefono_Empleado().trim();
                            String Correo_Electronico = empleados.getCorreo_Electronico_Empleado().trim();
                            String Contraseña = empleados.getContraseña_Empleado().trim();
                            String Nombre_Rol = empleados.getNombre_Rol().trim();
                            String Fecha_Creacion = empleados.getFecha_Creacion_Empleado().trim().replace("T", " ");
                            String Departamento = empleados.getDepartamento().trim();
                            Boolean Activo = empleados.getActivo();


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

                            campoEdad.setText(Edad.toString());
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

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

                            campoTelefono.setText(Telefono);
                            campoTelefono.setWidth(LargoContenido);
                            campoTelefono.setHeight(AnchoContenido);
                            campoTelefono.setLayoutParams(parametrosContenido);
                            campoTelefono.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoTelefono.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoTelefono.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoTelefono.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoTelefono.setTextColor(Color.BLACK);
                            campoTelefono.setTextSize(TamañoLetraContenido);

                            campoCorreo_Electronico.setText(Correo_Electronico);
                            campoCorreo_Electronico.setWidth(LargoContenido);
                            campoCorreo_Electronico.setHeight(AnchoContenido);
                            campoCorreo_Electronico.setLayoutParams(parametrosContenido);
                            campoCorreo_Electronico.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCorreo_Electronico.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCorreo_Electronico.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCorreo_Electronico.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCorreo_Electronico.setTextColor(Color.BLACK);
                            campoCorreo_Electronico.setTextSize(TamañoLetraContenido);

                            campoContraseña.setText(Contraseña);
                            campoContraseña.setWidth(LargoContenido);
                            campoContraseña.setHeight(AnchoContenido);
                            campoContraseña.setLayoutParams(parametrosContenido);
                            campoContraseña.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoContraseña.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoContraseña.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoContraseña.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoContraseña.setTextColor(Color.BLACK);
                            campoContraseña.setTextSize(TamañoLetraContenido);

                            campoNombreRol.setText(Nombre_Rol);
                            campoNombreRol.setWidth(LargoContenido);
                            campoNombreRol.setHeight(AnchoContenido);
                            campoNombreRol.setLayoutParams(parametrosContenido);
                            campoNombreRol.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombreRol.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombreRol.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombreRol.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombreRol.setTextColor(Color.BLACK);
                            campoNombreRol.setTextSize(TamañoLetraContenido);

                            campoFechaCreacion.setText(Fecha_Creacion);
                            campoFechaCreacion.setWidth(LargoContenido);
                            campoFechaCreacion.setHeight(AnchoContenido);
                            campoFechaCreacion.setLayoutParams(parametrosContenido);
                            campoFechaCreacion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaCreacion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaCreacion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaCreacion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaCreacion.setTextColor(Color.BLACK);
                            campoFechaCreacion.setTextSize(TamañoLetraContenido);

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

                            campoActivo.setText(Activo.toString());
                            campoActivo.setWidth(LargoContenido);
                            campoActivo.setHeight(AnchoContenido);
                            campoActivo.setLayoutParams(parametrosContenido);
                            campoActivo.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoActivo.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoActivo.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                            campoActivo.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoActivo.setTextColor(Color.BLACK);
                            campoActivo.setTextSize(TamañoLetraContenido);

                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoTelefono);
                            nuevaFila.addView(campoCorreo_Electronico);
                            nuevaFila.addView(campoContraseña);
                            nuevaFila.addView(campoNombreRol);
                            nuevaFila.addView(campoFechaCreacion);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoActivo);
                            tblTablaEmpleados.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        /* Aqui lo que se hace es ocultar los botones de crear, actualizar, -
                         * eliminar y seleccionar un registro de FAQ. Y luego se coloca el -
                         * logo de contenido por defecto. Esto por temas de buenas prácticas. */
                        buscadorEmpleados.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonPermisos.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoEmpleados.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        /* Esto es para imprimir los mensajes de error. */
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información de los empleados(as) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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

                        /* Aqui lo que se hace es ocultar los botones de crear, actualizar, -
                         * eliminar y seleccionar un registro de FAQ. Y luego se coloca el -
                         * logo de contenido por defecto. Esto por temas de buenas prácticas. */
                        buscadorEmpleados.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);
                        botonPermisos.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoEmpleados.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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

            /* Aqui es para saber si hubo un fallo en dar la respuesta -
             * por parte del API. */
            @Override
            public void onFailure(Call<List<ExtensionEmpleadoUsuarioEntitie>> call, Throwable t) {
                /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                 * y también para ver en que fallo en el API.
                 *
                 * NOTA: Este comando es para ver que fallo, el usuario no lo debe -
                 * ver:
                 * Toast.makeText(getActivity(), t.getLocalizedMessage(),
                 * Toast.LENGTH_SHORT).show(); */

                /* Aqui lo que se hace es ocultar los botones de crear, actualizar, -
                 * eliminar y seleccionar un registro de FAQ. Y luego se coloca el -
                 * logo de contenido por defecto. Esto por temas de buenas prácticas. */
                buscadorEmpleados.setVisibility(View.GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);
                botonPermisos.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);

                logitoEmpleados.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
                txtMensaje.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
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

    private void VistaCrearEmpleados() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentCrearEmpleado = new Intent(getActivity(), EmpleadoCrearActivity.class);

        startActivity(intentCrearEmpleado);

        getActivity().finish();
    }

    private void VistaActualizarEmpleados() {
        try {
            Integer cantidadChecks = 0;
            ExtensionEmpleadoUsuarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaEmpleados.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaEmpleados.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionEmpleadoUsuarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Nombre = datoSeleccionado.getNombre_Empleado().trim();
                String primerApellido = datoSeleccionado.getApellido1_Empleado().trim();
                String segundoApellido = datoSeleccionado.getApellido2_Empleado().trim();

                String Edad = datoSeleccionado.getEdad_Empleado().toString();
                String Cedula = datoSeleccionado.getCedula_Empleado().trim();
                String Telefono = datoSeleccionado.getTelefono_Empleado().trim();

                String Correo_Electronico = datoSeleccionado.getCorreo_Electronico_Empleado().trim();
                String Contraseña = datoSeleccionado.getContraseña_Empleado().trim();
                String Nombre_Rol = datoSeleccionado.getNombre_Rol().trim();

                String Departamento = datoSeleccionado.getDepartamento().trim();
                Boolean Activo = datoSeleccionado.getActivo();


                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentActualizarEmpleado = new Intent(getActivity(), EmpleadoActualizarActivity.class);

                /* Aquí lo que se esta haciendo es mandar el correo del usuario, -
                 * esto porque más adelante se necesitara para el inicio de sesión -
                 * respectivamente.*/

                intentActualizarEmpleado.putExtra("Nombre", Nombre);
                intentActualizarEmpleado.putExtra("Apellido1", primerApellido);
                intentActualizarEmpleado.putExtra("Apellido2", segundoApellido);
                intentActualizarEmpleado.putExtra("Edad", Edad);
                intentActualizarEmpleado.putExtra("Cedula", Cedula);
                intentActualizarEmpleado.putExtra("Telefono", Telefono);
                intentActualizarEmpleado.putExtra("Correo", Correo_Electronico);
                intentActualizarEmpleado.putExtra("Contraseña", Contraseña);
                intentActualizarEmpleado.putExtra("Rol", Nombre_Rol);
                intentActualizarEmpleado.putExtra("Departamento", Departamento);
                intentActualizarEmpleado.putExtra("Activo", Activo.toString());

                startActivity(intentActualizarEmpleado);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar el empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorEmpleados.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoEmpleados.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void EliminarEmpleados(String tokenUsuario) {
        try {
            Integer cantidadChecks = 0;
            ExtensionEmpleadoUsuarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaEmpleados.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaEmpleados.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionEmpleadoUsuarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String correoGuardado = datoSeleccionado.getCorreo_Electronico_Empleado().trim();

                Activity nombreActividad = getActivity();
                empleadoInterface = ConexionAPI.Conexion_API_Empleado(nombreActividad);

                Call<Boolean> eliminarFAQ = empleadoInterface.eliminarEmpleado(correoGuardado, tokenUsuario);

                eliminarFAQ.enqueue(new Callback<Boolean>() {

                    /* Aqui es para saber si hubo una respuesta por parte del API. */
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(getActivity(),
                                    "¡El empleado(a) seleccionado(a) ha sido eliminado(a) exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarEmpleados(tokenUsuario,  null, false);
                        } else {
                            try {
                                /* Esto permite leer el error del Body, de modo -
                                 * que sirva en el debug. */
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = "Se finalizo la sesión de su cuenta.";
                                }

                                /* Esto es para imprimir los mensajes de error. */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible eliminar el registro de este empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                                construirAlerta.setMessage("Pero no es posible eliminar el registro de este empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                        construirAlerta.setMessage("Pero no es posible eliminar el registro de este empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el registro de este empleado(a) debido a que selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorEmpleados.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoEmpleados.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar el registro de este empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void VistaPermisosUsuariosEmpleados() {
        try {
            Integer cantidadChecks = 0;
            ExtensionEmpleadoUsuarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaEmpleados.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaEmpleados.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionEmpleadoUsuarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Correo_Electronico = datoSeleccionado.getCorreo_Electronico_Empleado().trim();

                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentPermisos = new Intent(getActivity(), PermisosUsuarioEmpleadoActivity.class);

                intentPermisos.putExtra("Correo", Correo_Electronico);

                startActivity(intentPermisos);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible visualizar los permisos del empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorEmpleados.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoEmpleados.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoEmpleados.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible asignar o visualizar los permisos del empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}