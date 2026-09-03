package com.proyectotcu.muniturrialba.index;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.moduloAdministracionArchivos.AdministracionArchivosActivity;
import com.proyectotcu.muniturrialba.moduloEmpleados.ControlEmpleadosActivity;
import com.proyectotcu.muniturrialba.moduloReporteria.ReporteriaActivity;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


public class MenuFragment extends Fragment {

    //Variables globales para esta clase.
    Button botonEmpleado, botonReporteria, botonAdministracionArchivos;
    TextView txtMensaje;
    ImageView logitoMenu;
    ScrollView scrollVerticalMenu;
    static Boolean mensajeMenuPrincipal = false;


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

            txtMensaje.setVisibility(View.VISIBLE);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_menu.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            Toast.makeText(getActivity(), "¡Lo sentimos, pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" +
                    "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
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
        try {
            //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
            Intent intentEmpleado = new Intent(getActivity(), ControlEmpleadosActivity.class);

            //Le indica que ejecute el hipervinculo.
            startActivity(intentEmpleado);

        } catch (Exception error) {
            botonEmpleado.setVisibility(View.GONE);

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no es posible visualizar la información en estos momentos debido a un problema técnico. Por favor, intentelo más tarde.\n" + "\\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

        }
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de reporteria respectivamente (básicamente es la pantallita). */
    private void VistaReporteria() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentReporteria = new Intent(getActivity(), ReporteriaActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentReporteria);
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de administración de archivos respectivamente (básicamente -
     * es la pantallita). */
    private void VistaAdministracionArchivos() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentArchivos = new Intent(getActivity(), AdministracionArchivosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentArchivos);
    }

}