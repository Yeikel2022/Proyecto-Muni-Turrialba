package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityEmpleadosActualizarBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.EmpleadoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpleadoActualizarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityEmpleadosActualizarBinding empleadosActualizarBinding;

    //Variables globales:
    String nombreRecorrido, primerApellidoRecorrido, segundoApellidoRecorrido, cedulaRecorrido, edadRecorrido,
            telefonoRecorrido, correoRecorrido, contraseñaRecorrido, rolRecorrido, departamentoRecorrido,
            nombreActualizada, primerApellidoActualizada, segundoApellidoActualizada, cedulaActualizada,
            edadActualizada, telefonoActualizada, correoActualizada, contraseñaActualizada, rolActualizada,
            departamentoActualizada;

    Boolean activoActualizado, activoRecorrido;


    //Interfaz que contiene los métodos de la entidad FAQ.
    EmpleadoInterface empleadoInterface;
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        empleadosActualizarBinding = ActivityEmpleadosActualizarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(empleadosActualizarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ActualizarEmpleado), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        empleadosActualizarBinding.imgFotoActualizarEmpleado.setVisibility(GONE);
        empleadosActualizarBinding.txtMensajeActualizarEmpleado.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            /* Aqui lo que se hace es obtener el correo -
             * que nosotros habiamos enviado anteriormente -
             * en la clase de: "VistaCodigo". De forma que -
             * ahora se guarda ese correo y se pueda enviar -
             * hacia a la siguiente vista, que es donde se va -
             * a ocupar (de ahi el getStringExtra("Correo")).*/
            Intent recuperarEmpleado = getIntent();

            nombreRecorrido = recuperarEmpleado.getStringExtra("Nombre");
            primerApellidoRecorrido = recuperarEmpleado.getStringExtra("Apellido1");
            segundoApellidoRecorrido = recuperarEmpleado.getStringExtra("Apellido2");

            cedulaRecorrido = recuperarEmpleado.getStringExtra("Cedula");
            edadRecorrido = recuperarEmpleado.getStringExtra("Edad");
            telefonoRecorrido = recuperarEmpleado.getStringExtra("Telefono");

            correoRecorrido = recuperarEmpleado.getStringExtra("Correo");
            contraseñaRecorrido = recuperarEmpleado.getStringExtra("Contraseña");
            rolRecorrido = recuperarEmpleado.getStringExtra("Rol");

            departamentoRecorrido = recuperarEmpleado.getStringExtra("Departamento");
            activoRecorrido = Boolean.parseBoolean(recuperarEmpleado.getStringExtra("Activo"));


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

            ArrayAdapter<CharSequence> propiedadesSpinner = ArrayAdapter.createFromResource(this, R.array.listaTipo_Roles, R.layout.spinner_roles_color);
            propiedadesSpinner.setDropDownViewResource(R.layout.spinner_opciones_color);
            empleadosActualizarBinding.spRolEmpleadoActualizado.setAdapter(propiedadesSpinner);

            empleadosActualizarBinding.edtxtNombreEmpleadoActualizado.setText(nombreRecorrido);
            empleadosActualizarBinding.edtxtPrimerApellidoEmpleadoActualizado.setText(primerApellidoRecorrido);
            empleadosActualizarBinding.edtxtSegundoApellidoEmpleadoActualizado.setText(segundoApellidoRecorrido);

            empleadosActualizarBinding.edtxtCedulaEmpleadoActualizada.setText(cedulaRecorrido);
            empleadosActualizarBinding.edtxtEdadEmpleadoActualizada.setText(edadRecorrido);
            empleadosActualizarBinding.edtxtTelefonoEmpleadoActualizado.setText(telefonoRecorrido);

            empleadosActualizarBinding.edtxtCorreoEmpleadoActualizado.setText(correoRecorrido);
            empleadosActualizarBinding.edtxtPasswordEmpleadoActualizado.setText(contraseñaRecorrido);
            empleadosActualizarBinding.spRolEmpleadoActualizado.setSelection(propiedadesSpinner.getPosition(rolRecorrido));

            empleadosActualizarBinding.edtxtDepartamentoEmpleadoActualizado.setText(departamentoRecorrido);
            empleadosActualizarBinding.btnActivoEmpleado.setChecked(activoRecorrido);


            empleadosActualizarBinding.btnActualizarEmpleado.setOnClickListener(v -> {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_actualizar);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de actualizar este empleado(a)?")
                        .setTitle("Actualizar Empleado(a).");


                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarEmpleado(tokenGuardado);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EmpleadoActualizarActivity.this, "¡No se continuo con la actualización!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });

            empleadosActualizarBinding.spRolEmpleadoActualizado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rolActualizada = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    rolActualizada = null;
                }
            });

        } catch (Exception error) {
            empleadosActualizarBinding.txtTituloActualizarEmpleado.setVisibility(GONE);
            empleadosActualizarBinding.edtxtNombreEmpleadoActualizado.setVisibility(GONE);
            empleadosActualizarBinding.edtxtPrimerApellidoEmpleadoActualizado.setVisibility(GONE);
            empleadosActualizarBinding.edtxtSegundoApellidoEmpleadoActualizado.setVisibility(GONE);

            empleadosActualizarBinding.edtxtCedulaEmpleadoActualizada.setVisibility(GONE);
            empleadosActualizarBinding.edtxtEdadEmpleadoActualizada.setVisibility(GONE);
            empleadosActualizarBinding.edtxtTelefonoEmpleadoActualizado.setVisibility(GONE);

            empleadosActualizarBinding.edtxtCorreoEmpleadoActualizado.setVisibility(GONE);
            empleadosActualizarBinding.edtxtPasswordEmpleadoActualizado.setVisibility(GONE);
            empleadosActualizarBinding.spRolEmpleadoActualizado.setVisibility(GONE);

            empleadosActualizarBinding.edtxtDepartamentoEmpleadoActualizado.setVisibility(GONE);
            empleadosActualizarBinding.btnActivoEmpleado.setVisibility(GONE);
            empleadosActualizarBinding.btnActualizarEmpleado.setVisibility(GONE);

            empleadosActualizarBinding.imgFotoActualizarEmpleado.setVisibility(VISIBLE);
            empleadosActualizarBinding.txtMensajeActualizarEmpleado.setVisibility(VISIBLE);

            empleadosActualizarBinding.imgFotoActualizarEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            empleadosActualizarBinding.txtMensajeActualizarEmpleado.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */


            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible finalizar el proceso en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        //METODO PARA DETECTAR SI EL USUARIO POR X O Y RAZÓN, DESEA REGRESARSE.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentEmpleado = new Intent(EmpleadoActualizarActivity.this, ControlEmpleadosActivity.class);

                //Le indica que ejecute el hipervinculo.
                startActivity(intentEmpleado);

                /* Sirve para evitar que el usuario se regrese después.
                 * Esto por temas de buenas prácticas. */
                finish();
            }
        });
    }


    private void ActualizarEmpleado(String tokenUsuario) {
        int rol = 0;

        nombreActualizada = empleadosActualizarBinding.edtxtNombreEmpleadoActualizado.getText().toString().trim();
        primerApellidoActualizada = empleadosActualizarBinding.edtxtPrimerApellidoEmpleadoActualizado.getText().toString().trim();
        segundoApellidoActualizada = empleadosActualizarBinding.edtxtSegundoApellidoEmpleadoActualizado.getText().toString().trim();

        cedulaActualizada = empleadosActualizarBinding.edtxtCedulaEmpleadoActualizada.getText().toString().trim();
        edadActualizada = empleadosActualizarBinding.edtxtEdadEmpleadoActualizada.getText().toString().trim();
        telefonoActualizada = empleadosActualizarBinding.edtxtTelefonoEmpleadoActualizado.getText().toString().trim();

        correoActualizada = empleadosActualizarBinding.edtxtCorreoEmpleadoActualizado.getText().toString().trim();
        contraseñaActualizada = empleadosActualizarBinding.edtxtPasswordEmpleadoActualizado.getText().toString().trim();
        departamentoActualizada = empleadosActualizarBinding.edtxtDepartamentoEmpleadoActualizado.getText().toString().trim();
        activoActualizado = empleadosActualizarBinding.btnActivoEmpleado.isChecked();


        boolean respuestaValidacion = ValidarEmpleado(nombreActualizada, primerApellidoActualizada, segundoApellidoActualizada, cedulaActualizada,
                edadActualizada, correoActualizada, contraseñaActualizada, rolActualizada, departamentoActualizada, activoActualizado);

        if(respuestaValidacion != false) {
            usuarioInterface = ConexionAPI.Conexion_API(this);
            empleadoInterface = ConexionAPI.Conexion_API_Empleado(this);

            if(rolActualizada.toLowerCase().equals("Moderador".toLowerCase())) {
                rol = 1;
            }

            if(rolActualizada.toLowerCase().equals("Administrador".toLowerCase())) {
                rol = 2;
            }

            if(rolActualizada.toLowerCase().equals("Empleado".toLowerCase())) {
                rol = 3;
            }

            UsuarioEntitie usuarioEntitie = new
                    UsuarioEntitie(nombreActualizada, primerApellidoActualizada, segundoApellidoActualizada,
                    Integer.parseInt(edadActualizada), cedulaActualizada, telefonoActualizada, correoActualizada,
                    contraseñaActualizada, null, rol);

            EmpleadoEntitie empleadoEntitie = new EmpleadoEntitie(activoActualizado, departamentoActualizada);

            Call<Boolean> actualizarUsuario = usuarioInterface.actualizarUsuario(usuarioEntitie, cedulaRecorrido, tokenUsuario);
            Call<Boolean> actualizarEmpleado = empleadoInterface.actualizarEmpleado(empleadoEntitie, correoActualizada, tokenUsuario);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            actualizarUsuario.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    /* Si la respuesta que se recibio resulto ser correcta, entonces -
                     * siga con lo demás, caso contrario, pues muestre el error respectivo. */
                    if (response.isSuccessful()) {
                        actualizarEmpleado.enqueue(new Callback<Boolean>() {

                            /* Aqui es para saber si hubo una respuesta por parte del API. */
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.isSuccessful()) {
                                    //Imprime un mensaje indicando que se pudo hacer el registro.
                                    Toast.makeText(EmpleadoActualizarActivity.this,
                                            "¡El nuevo empleado(a) se actualizo exitosamente!", Toast.LENGTH_SHORT).show();

                                    /* Se resetean las variables globales. Esto por temas de -
                                     * buenas practicas. */
                                    nombreActualizada = null;
                                    primerApellidoActualizada = null;
                                    segundoApellidoActualizada = null;

                                    cedulaActualizada = null;
                                    edadActualizada = null;
                                    telefonoActualizada = null;

                                    correoActualizada = null;
                                    contraseñaActualizada = null;
                                    rolActualizada = null;
                                    departamentoActualizada = null;


                                    /* Esto es para enviarlo al metodo principal -
                                     * de la aplicación móvil. */
                                    VistaRegreso();
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
                                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
                                        construirAlerta.setIcon(R.drawable.icono_error);
                                        construirAlerta.setMessage("Pero en este momento no es posible actualizar el empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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

                                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
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
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible actualizar el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();
                            }
                        });



                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible actualizar el empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
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
                     * Toast.makeText(RegistroActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible actualizar el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                }

            });

        }
    }


    private boolean ValidarEmpleado(String nombre, String primerApellido, String segundoApellido, String cedula,
                                    String edad, String correo, String contraseña, String rol, String departamento, boolean activo) {

        /* Aqui valida si el nombre, los apellidos, la edad, la cédula, el correo electronico -
         * y la contraseña están vacios. Y si entra, entonces mandaria un mensaje de advertencia -
         * al usuario y un false al metodo: RealizarRegistro. */
        if (nombre.equals(nombreRecorrido) && primerApellido.equals(primerApellidoRecorrido) && segundoApellido.equals(segundoApellidoRecorrido) && cedula.equals(cedulaRecorrido) &&
                edad.equals(edadRecorrido) && correo.equals(correoRecorrido) && contraseña.equals(contraseñaRecorrido) && rol.equals(rolRecorrido) && departamento.equals(departamentoRecorrido) && activo == activoRecorrido) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que están los mismos datos." + "\n\nPor favor, ingrese los nuevos cambios que desea para actualizar el empleado(a) que fue seleccionado(a) respectivamente.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si el nombre, los apellidos, la edad, la cédula, el correo electronico -
         * y la contraseña están vacios. Y si entra, entonces mandaria un mensaje de advertencia -
         * al usuario y un false al metodo: RealizarRegistro. */
        if (nombre.isEmpty() && primerApellido.isEmpty() && segundoApellido.isEmpty() && cedula.isEmpty() && edad.isEmpty()
                && correo.isEmpty() && contraseña.isEmpty() && rol.equals("Seleccione el rol que quiere asignar:") && departamento.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos (excepto el teléfono) e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }


        /* Luego, lo segundo seria validar esos campos respectivamente.
         * Aqui valida si el nombre es nulo o si esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y -
         * un false al metodo: RealizarRegistro. */
        if (nombre == null || nombre.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el nombre se encuentra vacio." + "\n\nPor favor, digite otra vez su nombre e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si el primer apellido es nulo o si esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: RealizarRegistro. */
        if (primerApellido == null || primerApellido.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el primer apellido se encuentra vacio." + "\n\nPor favor, digite otra vez su apellido e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si el segundo apellido es nulo o si esta vacio. Y si -
         * entra entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarRegistro. */
        if (segundoApellido == null || segundoApellido.isEmpty()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el segundo apellido se encuentra vacio." + "\n\nPor favor, digite otra vez su apellido e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        /* Aqui valida si la cédula esta vacia o si es mayor a 12 (esto porque -
         * en Costa Rica la cédula nacional es de 9 digitos y el extranjero 12). -
         * Y si entra entonces mandaria un mensaje de advertencia al usuario y un -
         * false al metodo: RealizarRegistro. */
        if (cedula.isEmpty() || cedula.length() > 12) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que la cédula esta vacia o supera el limite." + "\n\nPor favor, digite otra vez su cédula e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si la edad se encuentra vacia, si es igual a cero, o si -
         * dicha edad es mayor a 99 (lo que indicaria que tiene 3 digitos). Y -
         * si entra entonces mandaria un mensaje de advertencia al usuario y un -
         * false al metodo: RealizarRegistro. */
        if (edad.isEmpty() || Integer.parseInt(edad) == 0 || Integer.parseInt(edad) > 99) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que la edad se encuentra vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su edad e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarRegistro.
         *
         * NOTA: El comando: !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches(), -
         * lo que ayuda es para saber si el correo esta bien o no, esto por medio de -
         * lo siguiente: "Patterns.EMAIL_ADDRESS", el cual es un comando que brinda -
         * Android Studio para poder manejar diferentes patrones como si fuera una -
         * expresión regular, en este caso, un correo electronico.
         *
         * Ahora con: "matcher(textoCorreo)", esto sirve para poder comparar el correo -
         * ingresado con ese patron que nosotros habiamos solicitado, osea el Patterns, -
         * luego de eso el: "matches()" nos ayudara a dar el resultado de esa comparación. */
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el correo electrónico esta vacio o esta incorrecto." + "\n\nPor favor, digite otra vez su correo electrónico e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }


        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: RealizarRegistro. */
        if (contraseña.isEmpty() || contraseña.length() < 12) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que la contraseña esta vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su contraseña e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }

        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: RealizarRegistro. */
        if (rol.isEmpty() || rol.equals("Seleccione el rol que quiere asignar:")) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el rol se encuentra vacio." + "\n\nPor favor, digite otra vez su rol e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();


            return false;
        }

        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: RealizarRegistro. */
        if (departamento.isEmpty() || departamento == null) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar el empleado(a) debido a que el departamento se encuentra vacio." + "\n\nPor favor, digite otra vez su departamento e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }


        return true;
    }


    private void VistaRegreso() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentEmpleados = new Intent(EmpleadoActualizarActivity.this, ControlEmpleadosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentEmpleados);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }
}

