package com.proyectotcu.muniturrialba.index;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.RegistroArchivoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.RegistroArchivoInterface;
import com.proyectotcu.muniturrialba.moduloAdministracionArchivos.AdministracionArchivosActivity;
import com.proyectotcu.muniturrialba.moduloEmpleados.ControlEmpleadosActivity;
import com.proyectotcu.muniturrialba.moduloReporteria.ReporteriaActivity;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MenuFragment extends Fragment {

    //Variables globales para esta clase.
    Button botonEmpleado, botonReporteria, botonAdministracionArchivos;
    TextView txtMensaje;
    ImageView logitoMenu;
    ScrollView scrollVerticalMenu;
    List<RegistroArchivoEntitie> datosOrdenados = new ArrayList<>();

    static Boolean mensajeMenuPrincipal = false;
    static Boolean mensajeNotificaciones = false;

    public static ArrayList<RegistroArchivoEntitie> listaArchivoCompartido = new ArrayList<>();

    //Interfaz que contiene los métodos de la entidad FAQ.
    RegistroArchivoInterface registroArchivoInterface;

    /* A diferencia de las actividades, al ser esto un fragmento, no se -
     * puede usar el ViewBinding para acceder a los recursos de una forma -
     * más sencilla, por lo que se hizo de la manera manual respectivamente. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Lo primero seria obtener el fragmento que esta relacionado -
         * al menu y también los botones que estan relacionados a los -
         * modulos de la aplicación móvil respectivamente.
         *
         * Además del texto que contiene el mensaje de error o de la -
         * negación relacionada a la autorización de permisos. Y también de -
         * ocultar los botones para evitar cualquier situación de que alguien -
         * logre saltarse la validación de los roles. */
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        botonEmpleado = view.findViewById(R.id.btn_ControlEmpleado);
        botonReporteria = view.findViewById(R.id.btn_Reporteria);
        botonAdministracionArchivos = view.findViewById(R.id.btn_AdministracionArchivos);
        scrollVerticalMenu = view.findViewById(R.id.sv_ScrollVertical_Modulos);

        botonEmpleado.setVisibility(View.INVISIBLE);
        botonReporteria.setVisibility(View.INVISIBLE);
        botonAdministracionArchivos.setVisibility(View.INVISIBLE);

        logitoMenu = view.findViewById(R.id.img_fotoMenu);
        txtMensaje = view.findViewById(R.id.txt_MensajeMenu);

        logitoMenu.setVisibility(View.GONE);
        txtMensaje.setVisibility(View.GONE);

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
             * claims del token, esto porque se necesita saber el rol y los permisos -
             * de dicho usuario. */
            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
             * para poder almacenar correctamente los datos que fueron gu -
             * ardados en la variable: "cuerpoToken". Esto porque el token -
             * en si, esta en un formato JSON respectivamente. */
            JSONObject json = new JSONObject(cuerpoToken);

            /* Después de crear ese objeto, entonces lo que se haria es -
             * guardar el rol y los permisos del usuario que inicio sesión -
             * en el campo respectivo, de modo que así se puedan utilizar -
             * para validar cuales modulos puede acceder y también si tiene -
             * la autorización necesaria para acceder respectivamente. */
            Integer campoRol = Integer.parseInt(json.optString("rol"));
            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));

            String campoNombre = json.optString("nombre");
            String campoPrimerApellido = json.optString("primer_Apellido");
            String campoSegundoApellido = json.optString("segundo_Apellido");
            String campoCorreo = json.optString("correo");

            /* Una vez hecho eso, lo que seguiria seria validar los permisos del usuario -
             * que ha iniciado sesión. Y, si luego de eso, la respuesta que trae es un 5,-
             * entonces quiere decir que si esta autorizado para acceder a los módulos corres-
             * pondientes. */
            Integer respuestaPermisos = ValidarPermisos(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
            if (respuestaPermisos == 5) {
                /* Aqui lo que se esta haciendo es validar si el usuario tiene -
                 * el rol: "Moderador". Y si no, entonces no lo dejaria acceder -
                 * a los modulos respectivos. */
                if (campoRol == 1) {
                    botonEmpleado.setVisibility(View.VISIBLE);
                    botonReporteria.setVisibility(View.VISIBLE);
                    botonAdministracionArchivos.setVisibility(View.VISIBLE);

                    botonEmpleado.setOnClickListener(v -> VistaEmpleados());
                    botonReporteria.setOnClickListener(v -> VistaReporteria());
                    botonAdministracionArchivos.setOnClickListener(v -> VistaAdministracionArchivos());

                    NotificacionArchivo(tokenGuardado, campoNombre, campoPrimerApellido, campoSegundoApellido, campoCorreo);
                }

                /* Aqui lo que se esta haciendo es validar si el usuario tiene -
                 * el rol: "Administrador". Y si no, entonces no lo dejaria acceder -
                 * a los modulos respectivos. */
                if (campoRol == 2) {
                    botonEmpleado.setVisibility(View.VISIBLE);
                    botonReporteria.setVisibility(View.VISIBLE);
                    botonAdministracionArchivos.setVisibility(View.VISIBLE);

                    botonEmpleado.setOnClickListener(v -> VistaEmpleados());
                    botonReporteria.setOnClickListener(v -> VistaReporteria());
                    botonAdministracionArchivos.setOnClickListener(v -> VistaAdministracionArchivos());

                    NotificacionArchivo(tokenGuardado, campoNombre, campoPrimerApellido, campoSegundoApellido, campoCorreo);
                }

                /* Aqui lo que se esta haciendo es validar si el usuario tiene -
                 * el rol: "Empleado". Y si no, entonces no lo dejaria acceder -
                 * a los modulos respectivos. */
                if (campoRol == 3) {
                    botonEmpleado.setVisibility(View.VISIBLE);
                    botonReporteria.setVisibility(View.GONE);
                    botonAdministracionArchivos.setVisibility(View.VISIBLE);

                    botonEmpleado.setOnClickListener(v -> VistaEmpleados());
                    botonAdministracionArchivos.setOnClickListener(v -> VistaAdministracionArchivos());

                    NotificacionArchivo(tokenGuardado, campoNombre, campoPrimerApellido, campoSegundoApellido, campoCorreo);
                }
            }

        } catch (Exception error) {
            /* Sirve para imprimir el mensaje que se recibio anteriormente.
             *
             * NOTA: Este comando es para ver que fallo, el usuario no lo debe ver:
             * Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show(); */

            /* Aqui lo que se hace es ocultar todos los modulos y se enviaria un mensaje -
             * de error, esto por si llegara a suceder algún imprevisto o algún error en -
             * el API (o la base de datos) respectivamente. */
            botonEmpleado.setVisibility(View.GONE);
            botonReporteria.setVisibility(View.GONE);
            botonAdministracionArchivos.setVisibility(View.GONE);
            scrollVerticalMenu.setVisibility(View.GONE);

            logitoMenu.setVisibility(View.VISIBLE);
            txtMensaje.setVisibility(View.VISIBLE);

            logitoMenu.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_menu.xml),-
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


    /* Metodo que sirve para validar los permisos del usuario -
     * que ha iniciado sesión, esto para poder continuar con -
     * la navegación dentro de la aplicación móvil respectivamente. */
    private Integer ValidarPermisos(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        /* Aqui valida si todos los permisos de ese usuario son falsos -
         * (dando a entender que no esta autorizado). Y si entra, entonces -
         * se ocultaria todos los modulos y se enviaria un mensaje mencionando -
         * que no tiene la autorización suficiente para poder continuar. */
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            botonEmpleado.setVisibility(View.GONE);
            botonReporteria.setVisibility(View.GONE);
            botonAdministracionArchivos.setVisibility(View.GONE);
            scrollVerticalMenu.setVisibility(View.GONE);

            logitoMenu.setVisibility(View.VISIBLE);
            txtMensaje.setVisibility(View.VISIBLE);

            logitoMenu.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_menu.xml), esto porque es una forma dinamica de hacerlo. */

            if(mensajeMenuPrincipal != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados en el menú principal.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeMenuPrincipal = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }

        /* Aqui valida si ese usuario no esta autorizado para visualizar el menu. -
         * Y si entra, entonces se ocultaria todos los modulos y se enviaria un -
         * mensaje mencionando que no tiene la autorización para visualizar los -
         * apartados del menú respectivamente. */
        if(Leer == false) {
            botonEmpleado.setVisibility(View.GONE);
            botonReporteria.setVisibility(View.GONE);
            botonAdministracionArchivos.setVisibility(View.GONE);
            scrollVerticalMenu.setVisibility(View.GONE);

            logitoMenu.setVisibility(View.VISIBLE);
            txtMensaje.setVisibility(View.VISIBLE);

            logitoMenu.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_menu.xml), esto porque es una forma dinamica de hacerlo. */

            /* Esto es para que muestre el mensaje una sola vez. De forma que, a la -
             * de volver, el usuario no tenga que estar quitando el mensaje a cada -
             * rato. En resumen, es por temas de experiencia de usuario. */
            if(mensajeMenuPrincipal != true) {
                AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados en el menú principal.")
                        .setTitle("¡Lo sentimos!");

                construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeMenuPrincipal = true;
                    }});

                AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                ejecutarMensajeAutorizacion.show();
            }

            return 0;
        }

        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de control de empleados respectivamente (básicamente es -
     * la pantallita). */
    private void VistaEmpleados() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentEmpleado = new Intent(getActivity(), ControlEmpleadosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentEmpleado);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        getActivity().finish();
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de reporteria respectivamente (básicamente es la pantallita). */
    private void VistaReporteria() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentReporteria = new Intent(getActivity(), ReporteriaActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentReporteria);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        getActivity().finish();
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de administración de archivos respectivamente (básicamente -
     * es la pantallita). */
    private void VistaAdministracionArchivos() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentArchivos = new Intent(getActivity(), AdministracionArchivosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentArchivos);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        getActivity().finish();
    }


    /* Metodo que sirve para notificar al usuario(a) si recibio un -
     * registro de archivo que le fue compartido. */
    public void NotificacionArchivo(String tokenUsuario, String nombre, String primerApellido,
                                    String segundoApellido, String correo) {
        try {
            registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(getActivity());

            ArrayList<String> listaMensaje = new ArrayList<String>();

            Call<List<RegistroArchivoEntitie>> obtenerArchivosCompartidos = registroArchivoInterface.obtenerArchivosCompartidos(correo, tokenUsuario);

            obtenerArchivosCompartidos.enqueue(new Callback<List<RegistroArchivoEntitie>>() {
                @Override
                public void onResponse(Call<List<RegistroArchivoEntitie>> call, Response<List<RegistroArchivoEntitie>> response) {
                    if (response.isSuccessful()) {
                        try {
                            listaArchivoCompartido.clear();
                            datosOrdenados = response.body();

                            datosOrdenados.sort(new Comparator<RegistroArchivoEntitie>() {
                                @Override
                                public int compare(RegistroArchivoEntitie o1, RegistroArchivoEntitie o2) {
                                    return o1.getFechaSubida().compareToIgnoreCase(o2.getFechaSubida());
                                }
                            });

                            for(int i = 0; i < datosOrdenados.size(); i++) {
                                listaArchivoCompartido.add(datosOrdenados.get(i));
                            }

                            for(int i = 0; i < listaArchivoCompartido.size(); i++) {
                                RegistroArchivoEntitie registroArchivo = listaArchivoCompartido.get(i);
                                String fechaSubida = registroArchivo.getFechaSubida().trim();
                                String estadoArchivo = registroArchivo.getEstadoArchivo().trim();

                                if("Compartido".equals(estadoArchivo)) {
                                    listaMensaje.add("- " + registroArchivo.getNombreArchivo() + "." + registroArchivo.getFormatoArchivo().toLowerCase() + ". \n\n");

                                    Call<Boolean> confirmarArchivo = registroArchivoInterface.actualizarEstadoRegistroArchivo("Recibido", fechaSubida, correo, tokenUsuario);

                                    confirmarArchivo.enqueue(new Callback<Boolean>() {
                                        @Override
                                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                            if (response.isSuccessful()) {
                                            } else {
                                                try {
                                                    /* Esto permite leer el error del Body, de modo -
                                                     * que sirva en el debug. */
                                                    String error = response.errorBody().string();
                                                    int errorRaw = response.raw().code();

                                                    if (errorRaw == 401) {
                                                        error = " Se finalizo la sesión de su cuenta.";
                                                    }

                                                    if(errorRaw == 404) {
                                                        error = " No se pudo actualizar el registro del archivo.";
                                                    }

                                                    /* Este sirve solo para el logcat:
                                                     * System.out.println(error); */
                                                } catch (Exception error) {
                                                    /* El printStackTrace(), sirve para aspectos de depuración.
                                                     * Esto debido a que ayuda a entender donde y porque ocurrio -
                                                     * un error durante la ejecución del proyecto. En este caso -
                                                     * las excepciones respectivamente.
                                                     *
                                                     * error.printStackTrace(); */
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
                                        }
                                    });
                                }
                            }

                            if(listaMensaje.size() != 0) {
                                String mensajeProveniente = "";

                                for(int j = 0; j < listaMensaje.size(); j++) {
                                    mensajeProveniente += listaMensaje.get(j);
                                }

                                if(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                                        == PackageManager.PERMISSION_GRANTED) {

                                    /* Aqui lo que hace es crear un hipervinculo para que se pueda usar en la -
                                     * notificación que se llega a mostrar al usuario(a) respectivamente. */
                                    Intent intentHistorialArchivos = new Intent(getActivity(), AdministracionArchivosActivity.class);
                                    intentHistorialArchivos.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intentHistorialArchivos.putExtra("Seccion_A_Mostrar", "Historial_Archivos");

                                    PendingIntent permisoNotificacion = PendingIntent.getActivity(
                                            getActivity(), 0, intentHistorialArchivos, PendingIntent.FLAG_IMMUTABLE);


                                    /* Aqui lo que hace es instanciar un nuevo constructor para las notificaciones.
                                     * Esto es necesario porque con el nos permitira diseñar la notificación, como -
                                     * son el icono, el titulo, la descripción que llevaria, el tipo de prioridad, -
                                     * el hipervinculo (que seria un intent) que utilizaria y el cierre del mismo.
                                     *
                                     * NOTA: Como tal, en base a la documentación oficial, nos permite definir que -
                                     * tan intrusivo puede ser la notificación, ya en versiones diferentes de Android, -
                                     * esto suele tener un rol distinto, pero en escencia es importante definirlo.
                                     *
                                     * En este caso, como solo se quiere avisar sobre si le compartieron un archivo, -
                                     * entonces se deja con una prioridad normal (osea por defecto) respectivamente.
                                     *
                                     * También, otro aspecto a mencionar es que en este constructor es necesario pasar -
                                     * el canal de notificaciones, esto para que pueda saber en que canal debe usar para -
                                     * mostrar la información respectivamente. */
                                    Bitmap mapearIcono = BitmapFactory.decodeResource(getResources(), R.drawable.turri15);

                                    NotificationCompat.Builder constructor = new NotificationCompat.Builder(getActivity(), "Canal_RecepcionArchivo")
                                            .setSmallIcon(R.drawable.icono_notificacion)
                                            .setLargeIcon(mapearIcono)
                                            .setContentTitle("¡Hola " + nombre + " " + primerApellido + " " + segundoApellido + "!")
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText("Te compartieron un documento llamado: \n\n" + mensajeProveniente + "\n\nAhora esta disponible en la sección: 'Historial Archivos' de administración de archivos."))
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setContentIntent(permisoNotificacion)
                                            .setAutoCancel(true);

                                    /* Aqui lo que se esta haciendo es crear un manejador compacto de notificacion, para -
                                     * poder colocar el contructor que se creo para la notificación, y una vez creada, -
                                     * mostrarlo al usuario. Además, antes de hacer esto se verifica si el usuario acepto -
                                     * el permiso de las notificaciones, y si no la acepto, entonces no notificaria al usuario(a) -
                                     * respectivamente. */
                                    NotificationManagerCompat manejadorNotificacion = NotificationManagerCompat.from(getActivity());
                                    manejadorNotificacion.notify(1, constructor.build());

                                } else {
                                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                                    construirAlerta.setMessage("Pero no es posible visualizar la notificación en estos momentos debido a la falta de permisos. Por favor, acepte los permisos respectivos para mostrar la información.")
                                            .setTitle("¡Lo sentimos!");

                                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}});

                                    AlertDialog ejecutarMensaje = construirAlerta.create();
                                    ejecutarMensaje.show();
                                }
                            }

                        } catch (Exception error) {
                            if(mensajeNotificaciones != true) {
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible visualizar la notificación en estos momentos debido a un problema técnico. Por favor, intentelo más tarde.\n" + "\nSi el problema persiste, entonces contactese con el personal técnico.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mensajeNotificaciones = true;
                                    }});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();
                            }
                        }

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = " Se finalizo la sesión de su cuenta.";
                            }

                            if(errorRaw == 404) {
                                error = " No se pudieron obtener los registros de los archivos.";
                            }

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */
                        }
                    }
                }

                /* Aqui es para saber si hubo un fallo en dar la respuesta -
                 * por parte del API. */
                @Override
                public void onFailure(Call<List<RegistroArchivoEntitie>> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                }
            });

        } catch (Exception error) {
            if(mensajeNotificaciones != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible visualizar la notificación en estos momentos debido a un problema técnico. Por favor, intentelo más tarde.\n" + "\nSi el problema persiste, entonces contactese con el personal técnico.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeNotificaciones = true;
                    }});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }
        }
    }
}