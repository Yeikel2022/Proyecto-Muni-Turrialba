package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityPermisosUsuarioEmpleadoBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.PermisoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.PermisoInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermisosUsuarioEmpleadoActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityPermisosUsuarioEmpleadoBinding permisosUsuario_EmpleadosCrearBinding;

    //Variables globales:
    Boolean leerActualizado, crearActualizado, actualizarActualizado, eliminarActualizado;

    String correoRecorrido;


    //Interfaz que contiene los métodos de la entidad FAQ.
    PermisoInterface permisoInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        permisosUsuario_EmpleadosCrearBinding = ActivityPermisosUsuarioEmpleadoBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(permisosUsuario_EmpleadosCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Permisos_UsuarioEmpleado), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setVisibility(GONE);
        permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setVisibility(GONE);

        try {
            /* Aqui lo que se hace es obtener el correo -
             * que nosotros habiamos enviado anteriormente -
             * en la clase de: "VistaCodigo". De forma que -
             * ahora se guarda ese correo y se pueda enviar -
             * hacia a la siguiente vista, que es donde se va -
             * a ocupar (de ahi el getStringExtra("Correo")).*/
            Intent recuperarEmpleado = getIntent();
            correoRecorrido = recuperarEmpleado.getStringExtra("Correo");

            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            permisoInterface = ConexionAPI.Conexion_API_Permiso_UsuarioEmpleado(this);


            Call<PermisoEntitie> obtenerPermisos = permisoInterface.obtenerPermisos(correoRecorrido, tokenGuardado);

            obtenerPermisos.enqueue(new Callback<PermisoEntitie>() {
                @Override
                public void onResponse(Call<PermisoEntitie> call, Response<PermisoEntitie> response) {
                    if (response.isSuccessful()) {

                        Boolean permisoLeer = response.body().getLeer().booleanValue();
                        Boolean permisoCrear = response.body().getCrear().booleanValue();
                        Boolean permisoActualizar = response.body().getActualizar().booleanValue();
                        Boolean permisoEliminar = response.body().getEliminar().booleanValue();


                        permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.setChecked(permisoLeer);
                        permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.setChecked(permisoCrear);
                        permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.setChecked(permisoActualizar);
                        permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.setChecked(permisoEliminar);

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Aqui lo que se hace es ocultar los botones de crear, actualizar, -
                             * eliminar y seleccionar un registro de FAQ. Y luego se coloca el -
                             * logo de contenido por defecto. Esto por temas de buenas prácticas. */
                            permisosUsuario_EmpleadosCrearBinding.txtTituloPermisosUsuarioEmpleado.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.txtInfoPermisosUsuarioEmpleado.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnConfirmarPermisos.setVisibility(GONE);

                            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setVisibility(VISIBLE);
                            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setVisibility(VISIBLE);
                            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                            /* Esto es para imprimir los mensajes de error. */
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no fue posible asignar los permisos hacia al usuario que fue seleccionado debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                            permisosUsuario_EmpleadosCrearBinding.txtTituloPermisosUsuarioEmpleado.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.txtInfoPermisosUsuarioEmpleado.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.setVisibility(GONE);
                            permisosUsuario_EmpleadosCrearBinding.btnConfirmarPermisos.setVisibility(GONE);

                            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setVisibility(VISIBLE);
                            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setVisibility(VISIBLE);
                            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no fue posible asignar los permisos hacia al usuario que fue seleccionado en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<PermisoEntitie> call, Throwable t) {
                    permisosUsuario_EmpleadosCrearBinding.txtTituloPermisosUsuarioEmpleado.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.txtInfoPermisosUsuarioEmpleado.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.setVisibility(GONE);
                    permisosUsuario_EmpleadosCrearBinding.btnConfirmarPermisos.setVisibility(GONE);

                    permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setVisibility(VISIBLE);
                    permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setVisibility(VISIBLE);
                    permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
                    permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setText(getString(R.string.ErrorFragment));

                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no fue posible asignar los permisos hacia al usuario que fue seleccionado en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                }
            });


            permisosUsuario_EmpleadosCrearBinding.btnConfirmarPermisos.setOnClickListener(v -> {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_actualizar);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de asignar y confirmar estos permisos para este empleado(a)?")
                        .setTitle("Asignar permisos.");


                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ConfirmarPermisos(tokenGuardado);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PermisosUsuarioEmpleadoActivity.this, "¡No se asginaron los permisos!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });


        } catch (Exception error) {
            permisosUsuario_EmpleadosCrearBinding.txtTituloPermisosUsuarioEmpleado.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.txtInfoPermisosUsuarioEmpleado.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.setVisibility(GONE);
            permisosUsuario_EmpleadosCrearBinding.btnConfirmarPermisos.setVisibility(GONE);


            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setVisibility(VISIBLE);
            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setVisibility(VISIBLE);
            permisosUsuario_EmpleadosCrearBinding.imgFotoPermisosUsuarioEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            permisosUsuario_EmpleadosCrearBinding.txtMensajePermisosUsuarioEmpleado.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible asignar los permisos hacia al usuario que fue seleccionado en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void ConfirmarPermisos(String tokenUsuario) {

        leerActualizado = permisosUsuario_EmpleadosCrearBinding.btnPermisoLeer.isChecked();
        crearActualizado = permisosUsuario_EmpleadosCrearBinding.btnPermisoCrear.isChecked();
        actualizarActualizado = permisosUsuario_EmpleadosCrearBinding.btnPermisoActualizar.isChecked();
        eliminarActualizado = permisosUsuario_EmpleadosCrearBinding.btnPermisoEliminar.isChecked();


        permisoInterface = ConexionAPI.Conexion_API_Permiso_UsuarioEmpleado(this);

        PermisoEntitie permisoEntitie = new PermisoEntitie(leerActualizado, crearActualizado, actualizarActualizado, eliminarActualizado);

        Call<Boolean> actualizarPermisos = permisoInterface.actualizarPermisos(permisoEntitie, correoRecorrido, tokenUsuario);

        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
        actualizarPermisos.enqueue(new Callback<Boolean>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    //Imprime un mensaje indicando que se pudo hacer el registro.
                    Toast.makeText(PermisosUsuarioEmpleadoActivity.this,
                            "¡Los permisos del empleado(a) se actualizaron exitosamente!", Toast.LENGTH_SHORT).show();

                    /* Se resetean las variables globales. Esto por temas de -
                     * buenas practicas. */
                    leerActualizado = false;
                    crearActualizado = false;
                    actualizarActualizado = false;
                    eliminarActualizado = false;


                    /* Esto es para enviarlo al metodo principal -
                     * de la aplicación móvil. */
                    VistaRegreso();
                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();

                        /* Esto es para imprimir los mensajes de error. */
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible actualizar los permisos del usuario debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible actualizar los permisos del usuario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(PermisosUsuarioEmpleadoActivity.this);
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible actualizar los permisos del usuario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }
        });
    }


    private void VistaRegreso() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPermisos_UsuarioEmpleado = new Intent(PermisosUsuarioEmpleadoActivity.this, ControlEmpleadosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentPermisos_UsuarioEmpleado);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }



}