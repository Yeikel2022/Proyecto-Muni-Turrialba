package com.proyectotcu.muniturrialba.moduloEmpleados;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityControlEmpleadosBinding;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ControlEmpleadosActivity extends AppCompatActivity {

    private ActivityControlEmpleadosBinding controlEmpleadosBinding;

    public static Boolean mensajeControlEmpleados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        controlEmpleadosBinding = ActivityControlEmpleadosBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(controlEmpleadosBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ControlEmpleados), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = this.getSharedPreferences(
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

            Menu itemsMenu = controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.getMenu();
            itemsMenu.findItem(R.id.itm_Mensaje).setVisible(false);


            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador" -
             * o "Administrador". Y si no, entonces no lo dejaria acceder a las opciones del -
             * perfil. */
            if (campoRol == 1 || campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisos(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);

                if (respuestaPermisos == 5) {
                    /* Sirve para llamar los fragmentos del menu, perfil, carnet virtual -
                     * y ayuda, siempre y cuando el usuario presione el boton respectivo.
                     *
                     * Además de que antes se coloca el fragmento relacionado al menú de -
                     * forma predeterminada, de ahi el porque se puso el comando: -
                     * "Fragmentos(new MenuFragment())" respectivamente. */
                    Intent seccionRecorrida = getIntent();
                    String seccionMostrar = seccionRecorrida.getStringExtra("Seccion_A_Mostrar");

                    if ("Permiso_Tiempo".equals(seccionMostrar)) {
                        Fragmentos(new PermisoTiempoFragment());
                        controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_PermisosTiempo);

                    } else if ("Control_Salario".equals(seccionMostrar)) {
                        Fragmentos(new ControlSalarioFragment());
                        controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_ControlSalarios);

                    } else {
                        Fragmentos(new EmpleadoFragment());
                    }


                    controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setOnItemSelectedListener(menuItem -> {
                        int idItem = menuItem.getItemId();

                        if (idItem == R.id.itm_Empleados) {
                            Fragmentos(new EmpleadoFragment());

                        } else if (idItem == R.id.itm_PermisosTiempo) {
                            Fragmentos(new PermisoTiempoFragment());

                        } else if (idItem == R.id.itm_ControlSalarios) {
                            Fragmentos(new ControlSalarioFragment());
                        }

                        return true;
                    });
                }
            }


            if (campoRol == 3) {
                Integer respuestaPermisos = ValidarPermisos(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);

                if (respuestaPermisos == 5) {
                    itemsMenu.findItem(R.id.itm_Empleados).setVisible(false);
                    itemsMenu.findItem(R.id.itm_ControlSalarios).setVisible(false);

                    Fragmentos(new PermisoTiempoFragment());
                    controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_PermisosTiempo);

                    controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setOnItemSelectedListener(menuItem -> {
                        int idItem = menuItem.getItemId();

                        if (idItem == R.id.itm_PermisosTiempo) {
                            Fragmentos(new PermisoTiempoFragment());
                        }

                        return true;
                    });
                }
            }

        } catch (Exception error) {
            Menu itemsMenu = controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.getMenu();
            itemsMenu.findItem(R.id.itm_Empleados).setVisible(false);
            itemsMenu.findItem(R.id.itm_PermisosTiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_ControlSalarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Mensaje).setVisible(true);

            Bundle bundleMensaje = new Bundle();
            MensajeFragment mensajeFragment = new MensajeFragment();

            bundleMensaje.putString("Tipo_Mensaje", "Error");
            mensajeFragment.setArguments(bundleMensaje);

            Fragmentos(mensajeFragment);
            controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_Mensaje);

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlEmpleadosActivity.this);
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


    private Integer ValidarPermisos(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        /* Aqui valida si todos los permisos de ese usuario son falsos -
         * (dando a entender que no esta autorizado). Y si entra, entonces -
         * se ocultaria todos las opciones del perfil y se enviaria un mensaje -
         * mencionando que no tiene la autorización suficiente para poder continuar. */
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            Menu itemsMenu = controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.getMenu();
            itemsMenu.findItem(R.id.itm_Empleados).setVisible(false);
            itemsMenu.findItem(R.id.itm_PermisosTiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_ControlSalarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Mensaje).setVisible(true);

            Fragmentos(new MensajeFragment());
            controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_Mensaje);

            if (mensajeControlEmpleados != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlEmpleadosActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados de control de empleados.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeControlEmpleados = true;
                    }});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

            return 0;
        }


        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso los permisos de la aplicación). Y si entra, -
         * entonces se enviaria un mensaje mencionando que no esta autorizado -
         * y luego un 1 para que el metodo: "VistaPermisos" pueda saber que no -
         * esta autorizado. */
        if(Leer == false) {
            Menu itemsMenu = controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.getMenu();
            itemsMenu.findItem(R.id.itm_Empleados).setVisible(false);
            itemsMenu.findItem(R.id.itm_PermisosTiempo).setVisible(false);
            itemsMenu.findItem(R.id.itm_ControlSalarios).setVisible(false);
            itemsMenu.findItem(R.id.itm_Mensaje).setVisible(true);

            Fragmentos(new MensajeFragment());
            controlEmpleadosBinding.btnVBarraNavegacionControlEmpleados.setSelectedItemId(R.id.itm_Mensaje);

            if (mensajeControlEmpleados != true) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ControlEmpleadosActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no tienes la autorización necesaria para visualizar estos apartados de control de empleados.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mensajeControlEmpleados = true;
                    }});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

            return 0;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }


    /* Este metodo sirve para poder enviar el usuario hacia a las -
     * diferentes opciones de la barra de navegación de la aplicación -
     * móvil. Básicamente es lo equivalente a usar los intents en una -
     * actividad, solo que aqui son fragmentos. */
    private void Fragmentos(Fragment fragmentoSeleccionado){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framL_PlantillaControlEmpleados, fragmentoSeleccionado).commit();
    }
}