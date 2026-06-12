package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityEmpleadosCrearBinding;
import com.proyectotcu.muniturrialba.index.AyudaCrearActivity;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.EmpleadoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.EmpleadoInterface;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpleadoCrearActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityEmpleadosCrearBinding empleadosCrearBinding;

    //Variables globales:
    String nombreIngresado, primerApellidoIngresado, segundoApellidoIngresado, cedulaIngresada,
            edadIngresada, telefonoIngresada, correoIngresada, contraseñaIngresada, rolIngresado,
            departamentoIngresado;


    //Interfaz que contiene los métodos de la entidad FAQ.
    EmpleadoInterface empleadoInterface;
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        empleadosCrearBinding = ActivityEmpleadosCrearBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(empleadosCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_CrearEmpleado), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        empleadosCrearBinding.imgFotoCrearEmpleado.setVisibility(GONE);
        empleadosCrearBinding.txtMensajeCrearEmpleado.setVisibility(GONE);

        try {
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
            empleadosCrearBinding.spRolEmpleado.setAdapter(propiedadesSpinner);


            empleadosCrearBinding.btnCrearEmpleado.setOnClickListener(v -> CrearNuevoEmpleado(tokenGuardado));

            empleadosCrearBinding.spRolEmpleado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    rolIngresado = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    rolIngresado = null;
                }
            });


        } catch (Exception error) {
            empleadosCrearBinding.txtTituloCrearEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtNombreEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtPrimerApellidoEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtSegundoApellidoEmpleado.setVisibility(GONE);

            empleadosCrearBinding.edtxtCedulaEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtEdadEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtTelefonoEmpleado.setVisibility(GONE);

            empleadosCrearBinding.edtxtCorreoEmpleado.setVisibility(GONE);
            empleadosCrearBinding.edtxtPasswordEmpleado.setVisibility(GONE);
            empleadosCrearBinding.spRolEmpleado.setVisibility(GONE);

            empleadosCrearBinding.edtxtDepartamentoEmpleado.setVisibility(GONE);
            empleadosCrearBinding.btnCrearEmpleado.setVisibility(GONE);

            empleadosCrearBinding.imgFotoCrearEmpleado.setVisibility(VISIBLE);
            empleadosCrearBinding.txtMensajeCrearEmpleado.setVisibility(VISIBLE);
            empleadosCrearBinding.imgFotoCrearEmpleado.setImageResource(R.drawable.icono_contenido_no_disponible);
            empleadosCrearBinding.txtMensajeCrearEmpleado.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible finalizar el proceso en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void CrearNuevoEmpleado(String tokenUsuario) {
        nombreIngresado = empleadosCrearBinding.edtxtNombreEmpleado.getText().toString().trim();
        primerApellidoIngresado = empleadosCrearBinding.edtxtPrimerApellidoEmpleado.getText().toString().trim();
        segundoApellidoIngresado = empleadosCrearBinding.edtxtSegundoApellidoEmpleado.getText().toString().trim();

        cedulaIngresada = empleadosCrearBinding.edtxtCedulaEmpleado.getText().toString().trim();
        edadIngresada = empleadosCrearBinding.edtxtEdadEmpleado.getText().toString().trim();
        telefonoIngresada = empleadosCrearBinding.edtxtTelefonoEmpleado.getText().toString().trim();

        correoIngresada = empleadosCrearBinding.edtxtCorreoEmpleado.getText().toString().trim();
        contraseñaIngresada = empleadosCrearBinding.edtxtPasswordEmpleado.getText().toString().trim();
        departamentoIngresado = empleadosCrearBinding.edtxtDepartamentoEmpleado.getText().toString().trim();



        boolean respuestaValidacion = ValidarEmpleado(nombreIngresado, primerApellidoIngresado, segundoApellidoIngresado,
                cedulaIngresada, edadIngresada, correoIngresada, contraseñaIngresada, rolIngresado, departamentoIngresado);

        if(respuestaValidacion != false) {
            int rol = 0;

            usuarioInterface = ConexionAPI.Conexion_API(this);
            empleadoInterface = ConexionAPI.Conexion_API_Empleado(this);

            if(rolIngresado.toLowerCase().equals("Moderador".toLowerCase())) {
                rol = 1;
            }

            if(rolIngresado.toLowerCase().equals("Administrador".toLowerCase())) {
                rol = 2;
            }

            if(rolIngresado.toLowerCase().equals("Empleado".toLowerCase())) {
                rol = 3;
            }

            UsuarioEntitie usuarioEntitie = new UsuarioEntitie(nombreIngresado, primerApellidoIngresado, segundoApellidoIngresado,
                    Integer.parseInt(edadIngresada), cedulaIngresada, telefonoIngresada, correoIngresada, contraseñaIngresada, null, rol);

            EmpleadoEntitie empleadoEntitie = new EmpleadoEntitie(true, departamentoIngresado);

            Call<UsuarioEntitie> crearNuevoUsuario = usuarioInterface.registrarUsuario(usuarioEntitie, false);
            Call<EmpleadoEntitie> crearNuevoEmpleado = empleadoInterface.crearEmpleado(empleadoEntitie, correoIngresada, tokenUsuario);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            crearNuevoUsuario.enqueue(new Callback<UsuarioEntitie>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                    /* Si la respuesta que se recibio resulto ser correcta, entonces -
                     * siga con lo demás, caso contrario, pues muestre el error respectivo. */
                    if (response.isSuccessful()) {


                        crearNuevoEmpleado.enqueue(new Callback<EmpleadoEntitie>() {

                            /* Aqui es para saber si hubo una respuesta por parte del API. */
                            @Override
                            public void onResponse(Call<EmpleadoEntitie> call, Response<EmpleadoEntitie> response) {
                                if (response.isSuccessful()) {
                                    //Imprime un mensaje indicando que se pudo hacer el registro.
                                    Toast.makeText(EmpleadoCrearActivity.this,
                                            "¡El nuevo empleado(a) se creo exitosamente!", Toast.LENGTH_SHORT).show();

                                    /* Se resetean las variables globales. Esto por temas de -
                                     * buenas practicas. */
                                    nombreIngresado = null;
                                    primerApellidoIngresado = null;
                                    segundoApellidoIngresado = null;

                                    cedulaIngresada = null;
                                    edadIngresada = null;
                                    telefonoIngresada = null;

                                    correoIngresada = null;
                                    contraseñaIngresada = null;
                                    rolIngresado = null;
                                    departamentoIngresado = null;


                                    /* Esto es para enviarlo al metodo principal -
                                     * de la aplicación móvil. */
                                    VistaRegreso();
                                } else {
                                    try {
                                        /* Esto permite leer el error del Body, de modo -
                                         * que sirva en el debug. */
                                        String error = response.errorBody().string();

                                        /* Esto es para imprimir los mensajes de error. */
                                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                                        construirAlerta.setIcon(R.drawable.icono_error);
                                        construirAlerta.setMessage("Pero en este momento no es posible crear el empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                                        construirAlerta.setIcon(R.drawable.icono_error);
                                        construirAlerta.setMessage("Pero no es posible crear el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                            public void onFailure(Call<EmpleadoEntitie> call, Throwable t) {
                                /* Sirve para imprimir el mensaje que se recibio -
                                 * anteriormente.
                                 *
                                 * NOTA: Este comando es para ver que fallo, el -
                                 * usuario no lo debe ver:
                                 * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                                 * Toast.LENGTH_SHORT).show(); */
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible crear el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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

                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible crear el empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible crear el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(RegistroActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible crear el empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                                    String edad, String correo, String contraseña, String rol, String departamento) {

        /* Aqui valida si el nombre, los apellidos, la edad, la cédula, el correo electronico -
         * y la contraseña están vacios. Y si entra, entonces mandaria un mensaje de advertencia -
         * al usuario y un false al metodo: RealizarRegistro. */
        if (nombre.isEmpty() && primerApellido.isEmpty() && segundoApellido.isEmpty() && cedula.isEmpty() && edad.isEmpty()
                && correo.isEmpty() && contraseña.isEmpty() && rol.equals("Seleccione el rol que quiere asignar:") && departamento.isEmpty()) {

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos (excepto el teléfono) e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el nombre se encuentra vacio." + "\n\nPor favor, digite otra vez su nombre e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el primer apellido se encuentra vacio." + "\n\nPor favor, digite otra vez su apellido e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el segundo apellido se encuentra vacio." + "\n\nPor favor, digite otra vez su apellido e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que la cédula esta vacia o supera el limite." + "\n\nPor favor, digite otra vez su cédula e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que la edad se encuentra vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su edad e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el correo electrónico esta vacio o esta incorrecto." + "\n\nPor favor, digite otra vez su correo electrónico e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que la contraseña esta vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su contraseña e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el rol se encuentra vacio." + "\n\nPor favor, digite otra vez su rol e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(EmpleadoCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear el empleado(a) debido a que el departamento se encuentra vacio." + "\n\nPor favor, digite otra vez su departamento e intentelo de nuevo.")
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
        Intent intentEmpleados = new Intent(EmpleadoCrearActivity.this, ControlEmpleadosActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentEmpleados);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }

}
