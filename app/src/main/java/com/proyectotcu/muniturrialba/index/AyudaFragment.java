package com.proyectotcu.muniturrialba.index;

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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.FAQEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.FAQInterface;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AyudaFragment extends Fragment {

    //Variables globales para esta clase.
    Integer LargoContenido, AnchoContenido, Largo_CheckBox, Ancho_CheckBox,
            alturaScrollHorizontal, baseScrollHorizontal, TamañoLetraContenido,
            margen;

    TextView txtPregunta, txtRespuesta, txtTipoPrioridad, campoPregunta, campoRespuesta,
             campoTipoPrioridad, txtMensaje;

    TableRow.LayoutParams margenScrollHorizontal, margenContenido, margen_CheckBox;

    Button botonCrear, botonActualizar, botonEliminar;
    CheckBox botonSeleccion, campoCheckBox;
    TableRow tbrPrimeraFila, nuevaFila;

    TableLayout tblTablaAyuda;
    ImageView logitoAyuda;
    HorizontalScrollView scrollHorizontal, scrollHorizontalAyuda;
    SearchView buscadorAyuda;
    static Boolean mensajePermisosAyuda = false;
    List<FAQEntitie> datosOrdenados;


    //Interfaz que contiene los métodos de la entidad FAQ.
    FAQInterface faqInterface;


    /* A diferencia de las actividades, al ser esto un fragmento, no se -
     * puede usar el ViewBinding para acceder a los recursos de una forma -
     * más sencilla, por lo que se hizo de la manera manual respectivamente. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Lo primero seria obtener el fragmento que esta relacionado -
         * al perfil y también los botones que estan relacionados a los -
         * permisos, la foto de perfil y la capacidad de cerrar sesión -
         * dentro de la aplicación móvil respectivamente.
         *
         * Además de los textos que contienen el nombre, los apellidos, el correo -
         * y el mensaje de error o de negación (se refiere a la autorización de -
         * permisos). Asimismo, también se ocultarian todos los botones y textos -
         * para evitar cualquier situación de que alguien logre saltarse la validación -
         * de los roles. */
        View view = inflater.inflate(R.layout.fragment_ayuda, container, false);
        logitoAyuda = view.findViewById(R.id.img_fotoAyuda);
        buscadorAyuda = view.findViewById(R.id.sv_buscarTipoPrioridad);

        botonCrear = view.findViewById(R.id.btn_CrearAyuda);
        botonActualizar = view.findViewById(R.id.btn_ActualizarAyuda);
        botonEliminar = view.findViewById(R.id.btn_EliminarAyuda);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDato);

        txtPregunta = view.findViewById(R.id.txt_Pregunta);
        txtRespuesta = view.findViewById(R.id.txt_Respuesta);
        txtTipoPrioridad = view.findViewById(R.id.txt_TipoPrioridad);
        txtMensaje = view.findViewById(R.id.txt_MensajeAyuda);

        scrollHorizontalAyuda = view.findViewById(R.id.hsv_ScrollHorizontalBotones_Ayuda);
        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontal);
        tblTablaAyuda = view.findViewById(R.id.tbl_TablaAyuda);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFila);

        logitoAyuda.setVisibility(GONE);
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

            buscadorAyuda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    buscadorAyuda.clearFocus();
                    return true;
                }
            });

            buscadorAyuda.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    buscadorAyuda.clearFocus();
                    buscadorAyuda.setIconifiedByDefault(true);
                    return false;
                }
            });


            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador" -
             * o "Administrador". Y si no, entonces no lo dejaria acceder a las opciones del -
             * perfil. */
            if (campoRol == 1 || campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearAyudaFAQ());
                    botonActualizar.setOnClickListener(v -> VistaActualizarAyudaFAQ());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_advertencia);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar esta pregunta de forma permanentemente?")
                                .setTitle("Eliminar FAQ.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarAyudaFAQ(tokenGuardado);
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

                    MostrarAyudaFAQ(tokenGuardado,  null, false);
                }

                /* Asimismo, también se hace otra validación, ya que, si la respuesta que trae -
                 * es un 3, quiere decir que ese usuario que inicio sesión no contiene el permiso -
                 * de actualizar, por lo que no esta autorizado para hacer algun cambio en la foto -
                 * de perfil respectivamente. */
                if (respuestaPermisos == 2) {
                    botonActualizar.setOnClickListener(v -> VistaActualizarAyudaFAQ());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_advertencia);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar esta pregunta de forma permanentemente?")
                                .setTitle("Eliminar FAQ.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarAyudaFAQ(tokenGuardado);
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

                    MostrarAyudaFAQ(tokenGuardado,  null, false);
                }

                /* Asimismo, también se hace otra validación, ya que, si la respuesta que trae -
                 * es un 3, quiere decir que ese usuario que inicio sesión no contiene el permiso -
                 * de actualizar, por lo que no esta autorizado para hacer algun cambio en la foto -
                 * de perfil respectivamente. */
                if (respuestaPermisos == 3) {
                    botonCrear.setOnClickListener(v -> VistaCrearAyudaFAQ());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_advertencia);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar esta pregunta de forma permanentemente?")
                                .setTitle("Eliminar FAQ.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarAyudaFAQ(tokenGuardado);
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

                    MostrarAyudaFAQ(tokenGuardado,  null, false);
                }

                /* Asimismo, también se hace otra validación, ya que, si la respuesta que trae -
                 * es un 3, quiere decir que ese usuario que inicio sesión no contiene el permiso -
                 * de actualizar, por lo que no esta autorizado para hacer algun cambio en la foto -
                 * de perfil respectivamente. */
                if (respuestaPermisos == 4) {
                    botonCrear.setOnClickListener(v -> VistaCrearAyudaFAQ());
                    botonActualizar.setOnClickListener(v -> VistaActualizarAyudaFAQ());

                    MostrarAyudaFAQ(tokenGuardado,  null, false);
                }
            }

            /* Aqui lo que se esta haciendo es validar si el usuario tiene el "Empleado".
             * Y si no, entonces no lo dejaria acceder a las opciones del perfil. */
            if (campoRol == 3) {
                botonCrear.setVisibility(GONE);
                botonActualizar.setVisibility(GONE);
                botonEliminar.setVisibility(GONE);

                alturaScrollHorizontal = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 380);
                baseScrollHorizontal = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 410);
                margenScrollHorizontal = new TableRow.LayoutParams(baseScrollHorizontal, alturaScrollHorizontal);

                int margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 50);
                margenScrollHorizontal.setMargins(0, margenTop, 0, 0);
                scrollHorizontal.setLayoutParams(margenScrollHorizontal);

                MostrarAyudaFAQ(tokenGuardado,  null, false);
            }

        } catch (Exception error) {
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);
            scrollHorizontalAyuda.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoAyuda.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

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
        /* Aqui valida si todos los permisos de ese usuario son falsos -
         * (dando a entender que no esta autorizado). Y si entra, entonces -
         * se ocultaria todos las opciones del perfil y se enviaria un mensaje -
         * mencionando que no tiene la autorización suficiente para poder continuar. */
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalAyuda.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(GONE);

            logitoAyuda.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no tienes la autorización necesaria para realizar las acciones dentro del foro.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return 0;
        }

        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso los permisos de la aplicación). Y si entra, -
         * entonces se enviaria un mensaje mencionando que no esta autorizado -
         * y luego un 1 para que el metodo: "VistaPermisos" pueda saber que no -
         * esta autorizado. */
        if(Crear == false) {
            botonCrear.setVisibility(GONE);

            if(mensajePermisosAyuda != true) {
                AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                construirAlertaCrear.setIcon(R.drawable.icono_error);
                construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para crear una nueva pregunta dentro del foro.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajePermisosAyuda = true;
                    }
                });

                AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                ejecutarMensajeCrear.show();
            }

            return 2;
        }

        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso los permisos de la aplicación). Y si entra, -
         * entonces se enviaria un mensaje mencionando que no esta autorizado -
         * y luego un 1 para que el metodo: "VistaPermisos" pueda saber que no -
         * esta autorizado. */
        if(Actualizar == false) {
            botonActualizar.setVisibility(GONE);

            if(mensajePermisosAyuda != true) {
                AlertDialog.Builder construirAlertaActualizacion = new AlertDialog.Builder(getActivity());
                construirAlertaActualizacion.setIcon(R.drawable.icono_error);
                construirAlertaActualizacion.setMessage("Pero no tienes la autorización necesaria para actualizar una pregunta que está situada en el foro.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaActualizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajePermisosAyuda = true;
                    }
                });

                AlertDialog ejecutarMensajeActualizacion = construirAlertaActualizacion.create();
                ejecutarMensajeActualizacion.show();
            }

            return 3;
        }

        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso los permisos de la aplicación). Y si entra, -
         * entonces se enviaria un mensaje mencionando que no esta autorizado -
         * y luego un 1 para que el metodo: "VistaPermisos" pueda saber que no -
         * esta autorizado. */
        if(Eliminar == false) {
            botonEliminar.setVisibility(GONE);

            if(mensajePermisosAyuda != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no tienes la autorización necesaria para eliminar una pregunta que esta situada en el foro.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajePermisosAyuda = true;
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

            return 4;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }

    private void BuscarPrioridad(String tokenUsuario, List<FAQEntitie> listaDatos, String textoIngresado) {
        try {
            List<FAQEntitie> datosFiltrados = new ArrayList<>();

            if(textoIngresado.isEmpty() || textoIngresado.equals("true")){
                datosFiltrados.addAll(listaDatos);
                MostrarAyudaFAQ(tokenUsuario, datosFiltrados, false);

            } else {
                for(FAQEntitie faqEntitie : listaDatos) {
                    if(faqEntitie.getTipoPrioridad().toLowerCase().contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(faqEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar la pregunta debido a que existen datos incorrectos o porque el registro no existe como tal." + "\n\nPor favor corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                }

            }

            MostrarAyudaFAQ(tokenUsuario, datosFiltrados, true);

        } catch (Exception error) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible posible visualizar la información en estos momentos debido a un problema técnico. Por favor intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");


            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

    }

    private void MostrarAyudaFAQ(String tokenUsuario, List<FAQEntitie> listaActualizada, Boolean autorizacion) {
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
        faqInterface = ConexionAPI.Conexion_API_FAQ(nombreActividad);

        /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
         * así poder enviar el correo y el token para que el API lo pueda recibir y obtener -
         * los datos del usuario. Esto por medio de la interfaz respectiva. */
        Call<List<FAQEntitie>> mostrarFAQs = faqInterface.obtenerFAQs(tokenUsuario);

        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
         * el metodo GET.
         *
         * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
         * podemos saber la posible respuesta que pudo brindar el API como tal. */
        mostrarFAQs.enqueue(new Callback<List<FAQEntitie>>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<List<FAQEntitie>> call, Response<List<FAQEntitie>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size() == 1) {
                        tblTablaAyuda.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            FAQEntitie faqEntitie = response.body().get(i);
                            String Pregunta = faqEntitie.getPregunta().trim();
                            String Respuesta = faqEntitie.getRespuesta().trim();
                            String Tipo_Prioridad = faqEntitie.getTipoPrioridad().trim();

                            txtPregunta.setText(Pregunta);
                            txtRespuesta.setText(Respuesta);
                            txtTipoPrioridad.setText(Tipo_Prioridad);
                            botonSeleccion.setTag(faqEntitie);
                            botonSeleccion.setVisibility(VISIBLE);
                        }
                    }

                    tblTablaAyuda.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<FAQEntitie>() {
                        @Override
                        public int compare(FAQEntitie o1, FAQEntitie o2) {
                            return o1.getPregunta().compareToIgnoreCase(o2.getPregunta());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaAyuda.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoPregunta = new TextView(getActivity());
                            campoRespuesta = new TextView(getActivity());
                            campoTipoPrioridad = new TextView(getActivity());


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            Largo_CheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 35);
                            Ancho_CheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 8);


                            margenContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            margen_CheckBox = new TableRow.LayoutParams(Largo_CheckBox, Ancho_CheckBox);
                            margen = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenContenido.setMarginStart(margen);
                            margen_CheckBox.setMarginStart(margen);
                            margen_CheckBox.setMarginEnd(margen);


                            FAQEntitie faqEntitie = listaActualizada.get(i);
                            String Pregunta = faqEntitie.getPregunta().trim();
                            String Respuesta = faqEntitie.getRespuesta().trim();
                            String Tipo_Prioridad = faqEntitie.getTipoPrioridad().trim();

                            campoCheckBox.setWidth(Largo_CheckBox);
                            campoCheckBox.setHeight(Ancho_CheckBox);
                            campoCheckBox.setLayoutParams(margen_CheckBox);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            campoCheckBox.setTag(faqEntitie);

                            campoPregunta.setText(Pregunta);
                            campoPregunta.setWidth(LargoContenido);
                            campoPregunta.setHeight(AnchoContenido);
                            campoPregunta.setLayoutParams(margenContenido);
                            campoPregunta.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoPregunta.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoPregunta.setTextColor(Color.BLACK);
                            campoPregunta.setTextSize(TamañoLetraContenido);

                            campoRespuesta.setText(Respuesta);
                            campoRespuesta.setWidth(LargoContenido);
                            campoRespuesta.setHeight(AnchoContenido);
                            campoRespuesta.setLayoutParams(margenContenido);
                            campoRespuesta.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRespuesta.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRespuesta.setTextColor(Color.BLACK);
                            campoRespuesta.setTextSize(TamañoLetraContenido);

                            campoTipoPrioridad.setText(Tipo_Prioridad);
                            campoTipoPrioridad.setWidth(LargoContenido);
                            campoTipoPrioridad.setHeight(AnchoContenido);
                            campoTipoPrioridad.setLayoutParams(margenContenido);
                            campoTipoPrioridad.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            campoTipoPrioridad.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                            campoTipoPrioridad.setTextColor(Color.BLACK);
                            campoTipoPrioridad.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoPregunta);
                            nuevaFila.addView(campoRespuesta);
                            nuevaFila.addView(campoTipoPrioridad);
                            tblTablaAyuda.addView(nuevaFila);
                        }

                    } else {
                        tblTablaAyuda.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoPregunta = new TextView(getActivity());
                            campoRespuesta = new TextView(getActivity());
                            campoTipoPrioridad = new TextView(getActivity());


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            Largo_CheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 35);
                            Ancho_CheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 8);


                            margenContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            margen_CheckBox = new TableRow.LayoutParams(Largo_CheckBox, Ancho_CheckBox);
                            margen = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenContenido.setMarginStart(margen);
                            margen_CheckBox.setMarginStart(margen);
                            margen_CheckBox.setMarginEnd(margen);


                            FAQEntitie faqEntitie = datosOrdenados.get(i);
                            String Pregunta = faqEntitie.getPregunta().trim();
                            String Respuesta = faqEntitie.getRespuesta().trim();
                            String Tipo_Prioridad = faqEntitie.getTipoPrioridad().trim();

                            campoCheckBox.setWidth(Largo_CheckBox);
                            campoCheckBox.setHeight(Ancho_CheckBox);
                            campoCheckBox.setLayoutParams(margen_CheckBox);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.WHITE));
                            campoCheckBox.setTag(faqEntitie);

                            campoPregunta.setText(Pregunta);
                            campoPregunta.setWidth(LargoContenido);
                            campoPregunta.setHeight(AnchoContenido);
                            campoPregunta.setLayoutParams(margenContenido);
                            campoPregunta.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoPregunta.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoPregunta.setTextColor(Color.BLACK);
                            campoPregunta.setTextSize(TamañoLetraContenido);

                            campoRespuesta.setText(Respuesta);
                            campoRespuesta.setWidth(LargoContenido);
                            campoRespuesta.setHeight(AnchoContenido);
                            campoRespuesta.setLayoutParams(margenContenido);
                            campoRespuesta.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoRespuesta.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoRespuesta.setTextColor(Color.BLACK);
                            campoRespuesta.setTextSize(TamañoLetraContenido);

                            campoTipoPrioridad.setText(Tipo_Prioridad);
                            campoTipoPrioridad.setWidth(LargoContenido);
                            campoTipoPrioridad.setHeight(AnchoContenido);
                            campoTipoPrioridad.setLayoutParams(margenContenido);
                            campoTipoPrioridad.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            campoTipoPrioridad.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC);
                            campoTipoPrioridad.setTextColor(Color.BLACK);
                            campoTipoPrioridad.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoPregunta);
                            nuevaFila.addView(campoRespuesta);
                            nuevaFila.addView(campoTipoPrioridad);
                            tblTablaAyuda.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();

                        /* Aqui lo que se hace es ocultar los botones de crear, actualizar, -
                         * eliminar y seleccionar un registro de FAQ. Y luego se coloca el -
                         * logo de contenido por defecto. Esto por temas de buenas prácticas. */
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalAyuda.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoAyuda.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        /* Esto es para imprimir los mensajes de error. */
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalAyuda.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoAyuda.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
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

            /* Aqui es para saber si hubo un fallo en dar la respuesta -
             * por parte del API. */
            @Override
            public void onFailure(Call<List<FAQEntitie>> call, Throwable t) {
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
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);

                scrollHorizontalAyuda.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);

                logitoAyuda.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
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

    private void VistaCrearAyudaFAQ() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentCrearAyuda = new Intent(getActivity(), AyudaCrearActivity.class);
        startActivity(intentCrearAyuda);
    }

    private void VistaActualizarAyudaFAQ() {
        try {
            Integer cantidadChecks = 0;
            FAQEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaAyuda.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaAyuda.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (FAQEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String preguntaGuardado = datoSeleccionado.getPregunta().trim();
                String respuestaGuardado = datoSeleccionado.getRespuesta().trim();
                String tipoPrioridadGuardado = datoSeleccionado.getTipoPrioridad().trim();

                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentActualizarAyuda = new Intent(getActivity(), AyudaActualizarActivity.class);

                /* Aquí lo que se esta haciendo es mandar el correo del usuario, -
                 * esto porque más adelante se necesitara para el inicio de sesión -
                 * respectivamente. */
                intentActualizarAyuda.putExtra("Pregunta", preguntaGuardado);
                intentActualizarAyuda.putExtra("Respuesta", respuestaGuardado);
                intentActualizarAyuda.putExtra("Tipo_Prioridad", tipoPrioridadGuardado);


                startActivity(intentActualizarAyuda);

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar la pregunta debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalAyuda.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoAyuda.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");


            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});
            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void EliminarAyudaFAQ(String tokenUsuario) {
        try {
            Integer cantidadChecks = 0;
            FAQEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaAyuda.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaAyuda.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (FAQEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String preguntaGuardado = datoSeleccionado.getPregunta().trim();

                Activity nombreActividad = getActivity();
                faqInterface = ConexionAPI.Conexion_API_FAQ(nombreActividad);

                Call<Boolean> eliminarFAQ = faqInterface.eliminarFAQ(preguntaGuardado, tokenUsuario);

                eliminarFAQ.enqueue(new Callback<Boolean>() {

                    /* Aqui es para saber si hubo una respuesta por parte del API. */
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(getActivity(),
                                    "¡La pregunta seleccionada ha sido eliminada exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarAyudaFAQ(tokenUsuario,  null, false);
                        } else {
                            try {
                                /* Esto permite leer el error del Body, de modo -
                                 * que sirva en el debug. */
                                String error = response.errorBody().string();

                                /* Esto es para imprimir los mensajes de error. */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                    construirAlerta.setIcon(R.drawable.icono_error);
                                    construirAlerta.setMessage("Pero en este momento no es posible eliminar la pregunta debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                                construirAlerta.setMessage("Pero no es posible eliminar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                        construirAlerta.setMessage("Pero no es posible eliminar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                construirAlerta.setMessage("Pero en este momento no es posible eliminar la pregunta debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();

            }

        } catch (Exception error) {
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalAyuda.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoAyuda.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }




}