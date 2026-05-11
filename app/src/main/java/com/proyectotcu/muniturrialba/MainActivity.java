package com.proyectotcu.muniturrialba;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.databinding.ActivityMainBinding;
import com.proyectotcu.muniturrialba.index.MenuPrincipalActivity;
import com.proyectotcu.muniturrialba.index.RecuperarPasswordActivity;
import com.proyectotcu.muniturrialba.index.RegistroActivity;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.JWTEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityMainBinding mainBinding;

    //Variables globales para esta clase.
    String textoCorreo, textoContraseña,
           campoNombre, campoApellido_1,
           campoApellido_2;

    //Variable global de tipo estatica:
    public static boolean botonActivado;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(mainBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Sirve para llamar los metodos de registro, inicio de sesion, -
         * recuperación de contraseña y de mantener la sesión, siempre -
         * y cuando el usuario presione el boton respectivo. */
        mainBinding.btnRegistro.setOnClickListener(v -> VistaRegistro());
        mainBinding.btnInicioSesion.setOnClickListener(v -> RealizarInicioSesion());
        mainBinding.btnPassword.setOnClickListener(v -> VistaRecuperarPassword());
        VistaSesionMantenida(botonActivado);
    }

    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de registro respectivamente (básicamente es la pantallita). */
    private void VistaRegistro() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentRegistro = new Intent(MainActivity.this, RegistroActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentRegistro);
    }

    /* Metodo que sirve para realizar el inicio de sesión por parte del -
     * usuario respectivamente. */
    private void RealizarInicioSesion() {
        //Lo primero es resetear las variables globales.
        textoCorreo = null;
        textoContraseña = null;
        campoNombre = null;
        campoApellido_1 = null;
        campoApellido_2 = null;


        /* Luego, lo segundo seria validar si el usuario -
         * desea mantener la sesión en la aplicación móvil. */
        boolean mantenerSesion = mainBinding.btnAprobacion.isChecked();
        if(mantenerSesion != false) {
            botonActivado = true;
        }

        /* Una vez hecho eso, lo tercero que se va a hacer es validar -
         * los datos relacionados al inicio de sesión. Y, si luego de -
         * eso, la respuesta que trae es un true, entonces quiere decir -
         * que todos los campos estan bien. */
        boolean respuestaValidacion = ValidarInicioSesion();
        if (respuestaValidacion != false) {
            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en esta clase se esta pidiendo esa conexión -
             * del API como tal, de ahí el "this".
             *
             * NOTA: Se pone: "this" porque es la manera en la cual la -
             * aplicacion puede saber donde se esta haciendo el llamado -
             * en este caso la clase de MainActivity que tiene como el -
             * activity main respectivamente. Osea una instancia de la -
             * clase. */
            usuarioInterface = ConexionAPI.Conexion_API(this);

            /* Luego de eso se procede a llamar la entidad: "UsuarioEntitie" para poder registrar -
             * esos datos que el usuario esta colocando en el inicio de sesión como tal.
             *
             * Ahora, como es un POST, donde se va a hacer lo del inicio de sesión es necesario -
             * pasarlo a un constructor parametrizado para que el sistema sepa en que variables -
             * tiene que almacenar esos datos. */
            UsuarioEntitie usuarioEntitie = new UsuarioEntitie(null, null,
                    null, 0, null, null, textoCorreo,
                    textoContraseña, null, 0);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder enviar esos datos que están almacenados en el constructor: -
             * "usuarioEntitie" y que el API lo pueda recibir. Esto por medio de la interfaz -
             * respectiva. */
            Call<JWTEntitie> iniciarSesion = usuarioInterface.iniciarSesion(usuarioEntitie);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            iniciarSesion.enqueue(new Callback<JWTEntitie>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<JWTEntitie> call, Response<JWTEntitie> response) {
                    if (response.isSuccessful()) {
                        /* Aquí lo que se hace es guardar el token que nos brindo el API, -
                         * de modo que el usuario pueda seguir usando la aplicación sin -
                         * ningún tipo de problema. */
                        String tokenRecibido = response.body().getTokenAcceso().toString().trim();

                        /* Aqui lo que hacemos es acceder al archivo XML que tiene como nombre: -
                         * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
                         * esta creado, entonces el sistema lo crearia automaticamente. */
                        SharedPreferences archivoXML = getSharedPreferences("Archivo_Autenticacion",
                                Context.MODE_PRIVATE);

                        /* Aqui lo que se esta haciendo es agregar un texto llamado: "JWT_token", -
                         * que contendra el token que se recibio por parte del API. Y luego de eso, -
                         * se aplica dicho cambio y se guardaria en el archivo respectivamente. */
                        archivoXML.edit().putString("JWT_token", tokenRecibido).apply();

                        try {
                            /* Aqui lo que se esta haciendo es crear una lista con todas las partes -
                             * separadas que contiene el token recibido por parte del API. Esto por -
                             * medio del comando: "split("\\.")", el cual nos ayuda a separar dicho -
                             * token por los puntos que contiene, de ahi el \\., ya que es una expre-
                             * sión regular que nos permite realizar esa acción respectivamente. */
                            String[] partesToken = tokenRecibido.split("\\.");

                            /* Aqui lo que se esta haciendo es decodificar la parte que contiene los -
                             * claims del token, esto porque se necesita pasar el nombre y los apelli-
                             * dos de dicho usuario. */
                            String cuerpoToken = new String(Base64.decode(partesToken[1],
                                    Base64.URL_SAFE), StandardCharsets.UTF_8);

                            /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
                             * para poder almacenar correctamente los datos que fueron gu -
                             * ardados en la variable: "cuerpoToken". Esto porque el token -
                             * en si, esta en un formato JSON respectivamente. */
                            JSONObject json = new JSONObject(cuerpoToken);

                            /* Después de crear ese objeto, entonces lo que se haria es -
                             * guardar el nombre y los apellidos del usuario que esta -
                             * iniciando sesión en los campos respectivos, de modo que -
                             * así se puedan utilizar para el mensaje de bienvenida en el -
                             * metodo respectivo, el cual seria: VistaMenu. */
                            campoNombre = json.optString("nombre");
                            campoApellido_1 = json.optString("primer_Apellido");
                            campoApellido_2 = json.optString("segundo_Apellido");
                            VistaMenu();

                        } catch (JSONException error) {
                            /* Sirve para imprimir el mensaje que se recibio anteriormente.
                             *
                             * NOTA: Este comando es para ver que fallo, el usuario no lo debe ver:
                             * Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show(); */
                            Toast.makeText(MainActivity.this, "¡Lo sentimos, " +
                                    "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,
                                    "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Esto es para imprimir los mensajes de error. */
                            Toast.makeText(MainActivity.this, "¡Lo sentimos, pero en este momento no se pudo iniciar sesión!", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, "Por favor, corriga los datos e intentelo de nuevo.", Toast.LENGTH_LONG).show();

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */
                            Toast.makeText(MainActivity.this, "¡Lo sentimos, " +
                                    "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this,
                                    "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /* Aqui es para saber si hubo un fallo en dar la respuesta -
                 * por parte del API. */
                @Override
                public void onFailure(Call<JWTEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(MainActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    Toast.makeText(MainActivity.this, "¡Lo sentimos, " +
                            "pero en estos instantes no se pudo continuar con el proceso!", Toast.LENGTH_LONG).show();

                    Toast.makeText(MainActivity.this,
                            "Por favor, intentelo de nuevo.", Toast.LENGTH_LONG).show();
                }

            });

            /*Se trae el metodo correspondiente que tiene dicha interfaz.
            Call<UsuarioEntitie> usuario = usuarioInterface.obtenerUsuario(textoCorreo);
            Se ejecuta el metodo y se espera la respuesta.
            usuario.enqueue(new Callback<UsuarioEntitie>() {
                //Este metodo es para saber si hubo respuesta por parte del API.
                @Override
                public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                    /* Si la respuesta que trajo se pudo realizar correctamente, entonces -
                     * siga con lo demás, caso contrario, muestre el error respectivo.
                    if (response.isSuccessful()) {
                        /* Se almacenan los datos y se quitan los espacios que pueden tener -
                         * adelante y hacia atras.
                        String CorreoObtenido = response.body().getCorreo_Electronico().trim();
                        String ContraseñaObtenida = response.body().getContraseña().trim();

                        /* Si el correo y la contraseña que puso el usuario, es igual al que -
                         * esta en el sistema, entonces quiere decir que es el mismo usuario. -
                         * Por lo que le dejaria iniciar sesión, si no fuera así, entonces -
                         * daria error respectivamente.
                        if (Objects.equals(textoCorreo, CorreoObtenido) && Objects.equals(textoContraseña, ContraseñaObtenida)) {
                            campoNombre = response.body().getNombre().trim();
                            campoApellido_1 = response.body().getApellido_1().trim();
                            campoApellido_2 = response.body().getApellido_2().trim();
                            VistaMenu();
                        } else {
                            Toast.makeText(MainActivity.this, "¡No se pudo iniciar sesión! \n" +
                                    "La contraseña esta incorrecta.", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, "Por favor, intentelo de nuevo.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        try {
                            //Permite leer el error del Body, de modo que sirva en el debug.
                            String error = response.errorBody().string();

                            /* Esto es para imprimir los mensajes de error.
                            Toast.makeText(MainActivity.this, "¡Lo sentimos! \n" +
                                    "¡No se pudo iniciar sesión!", Toast.LENGTH_LONG).show();
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();

                            //Este sirve solo para el logcat.
                            //System.out.println(error);
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las Excepciones respectivamente.

                             * error.printStackTrace();
                            Toast.makeText(MainActivity.this, "¡Lo sentimos!\n" +
                                    "¡Parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /* Este metodo es para saber si se fallo en dar una respuesta por -
                 * parte del API.
                @Override
                public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente.
                    /* Este es para ver que fallo, el usuario no lo debe ver:
                     * Toast.makeText(MainActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show();

                    Toast.makeText(MainActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se pudo continuar!", Toast.LENGTH_LONG).show();

                    Toast.makeText(MainActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }
            });*/
        }
    }


    /* Metodo que sirve para validar los datos que esta colocando el usuario -
     * para poder iniciar sesión respectivamente. */
    private boolean ValidarInicioSesion(){
        /* Lo primero es guardar los datos que coloca el usuario -
         * en estas variables globales. Luego con el metodo: trim(), -
         * nos ayudara a quitar los espacios que pueden estar al -
         * principio o al final del texto. */
        textoCorreo = mainBinding.edtxtCampoEmail.getText().toString().trim();
        textoContraseña = mainBinding.edtxtCampoPassword.getText().toString().trim();


        /* Aqui valida si la contraseña y el correo electronico están vacios. Y si -
         * entra, entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: RealizarInicioSesion. */
        if (textoCorreo.isEmpty() && textoContraseña.isEmpty()) {
            Toast.makeText(MainActivity.this, "¡El correo y la contraseña están vacios!", Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this, "Por favor, ingrese todos los campos requeridos.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarInicioSesion.
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
        if (textoCorreo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches()) {
            Toast.makeText(MainActivity.this, "¡El correo esta vacio o esta incorrecto! \n" +
                    "Por favor, digite otra vez su correo.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: RealizarInicioSesion. */
        if (textoContraseña.isEmpty() || textoContraseña.length() < 12) {
            Toast.makeText(MainActivity.this, "¡La contraseña esta vacia o esta incorrecta!",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this,"Por favor, digite otra vez su contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de recuperar contraseña respectivamente (básicamente es -
     * la pantallita). */
    private void VistaRecuperarPassword() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPassword = new Intent(MainActivity.this, RecuperarPasswordActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentPassword);
    }


    /* Este metodo sirve para poder enviar el usuario hacia al -
     * menú principal de la aplicación movil. */
    private void VistaMenu() {
        //Esto es para poder imprimir el mensaje de bienvenida.
        Toast.makeText(MainActivity.this, "¡Bienvenido(a): " + campoNombre +
                " " + campoApellido_1 + " " + campoApellido_2 + "!", Toast.LENGTH_LONG).show();

        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentMenu = new Intent(MainActivity.this, MenuPrincipalActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentMenu);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }


    /* Este metodo sirve para poder mantener la sesión dentro -
     * de la aplicación movil. */
    private void VistaSesionMantenida(boolean respuestaSesion) {
        /* Si la respuesta por parte del usuario es un true -
         * quiere decir que si quiere mantener la sesión, caso -
         * contrario, no haga nada. */
        if (respuestaSesion != false) {

            /* Lo primero seria resetear las variables globales.
             * Esto por temas de buenas prácticas. */
            textoCorreo = null;
            textoContraseña = null;
            campoNombre = null;
            campoApellido_1 = null;
            campoApellido_2 = null;

            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getSharedPreferences("Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            /* Luego, lo cuarto que hay que hacer seria validar si en la variable: "tokenGuardado" -
             * contiene algún dato, y si resulta que es nulo, entonces no lo dejaria pasar. */
            if (tokenGuardado != null) {
                try {
                    /* Aqui lo que se esta haciendo es crear una lista con todas las partes -
                     * separadas que contiene el token que fue guardado. Esto por medio del -
                     * comando: "split("\\.")", el cual nos ayuda a separar dicho token por -
                     * los puntos que contiene, de ahi el \\., ya que es una expresión regular -
                     * que nos permite realizar esa acción respectivamente. */
                    String[] partesToken = tokenGuardado.split("\\.");

                    /* Aqui lo que se esta haciendo es decodificar la parte que contiene los -
                     * claims del token, esto porque se necesita pasar el nombre y los apelli-
                     * dos de dicho usuario. */
                    String cuerpoToken_Actual = new String(Base64.decode(partesToken[1],
                            Base64.URL_SAFE), StandardCharsets.UTF_8);

                    /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
                     * para poder almacenar correctamente los datos que fueron gu -
                     * ardados en la variable: "cuerpoToken_Actual". Esto porque el -
                     * token en si, esta en un formato JSON respectivamente. */
                    JSONObject json = new JSONObject(cuerpoToken_Actual);

                    /* Después de crear ese objeto, entonces lo que se haria es -
                     * guardar el nombre y los apellidos del usuario que esta -
                     * iniciando sesión en los campos respectivos, de modo que -
                     * así se puedan utilizar para el mensaje de bienvenida en el -
                     * metodo respectivo, el cual seria: VistaMenu. */
                    campoNombre = json.optString("nombre");
                    campoApellido_1 = json.optString("primer_Apellido");
                    campoApellido_2 = json.optString("segundo_Apellido");
                    VistaMenu();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}