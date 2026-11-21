package com.proyectotcu.muniturrialba;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* Aqui lo que se hace es pedir al usuario, el permiso relacionado a las -
         * notificaciones. */
        SolicitarPermisoNotificaciones();

        /* Sirve para llamar los metodos de registro, inicio de sesion, -
         * recuperación de contraseña y de mantener la sesión, siempre -
         * y cuando el usuario presione el boton respectivo. */
        mainBinding.btnRegistro.setOnClickListener(v -> VistaRegistro());
        mainBinding.btnInicioSesion.setOnClickListener(v -> RealizarInicioSesion());
        mainBinding.btnPassword.setOnClickListener(v -> VistaRecuperarPassword());
        VistaSesionMantenida(botonActivado);
    }


    /* Este metodo sirve para poder pedir el permiso relacionado a las -
     * notificaciones de la aplicación móvil respectivamente. */
    private void SolicitarPermisoNotificaciones() {
        /* Aqui lo que se esta haciendo es simplemente verificar si -
         * el usuario(a) tiene una versión de Android 13 (que seria -
         * un API nivel 33), esto para poder solicitar los permisos -
         * relacionados a las notificaciones. Básicamente es para -
         * mostrar un cuadrito con la pregunta de las notificaciones. */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            /* Aquí lo que se esta haciendo es verificar si el usuario(a) acepto el permiso relacionado -
             * a las notificaciones, si resulta que no las acepto todavia, entonces se mandaria un mensaje, -
             * pidiendole dichos permisos respectivamente. Ya en caso contrario que haya aceptado el permiso -
             * entonces se podria proseguir con la creación del canal de notificación para poder usar las -
             * notificaciones respectivamente. */
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[] { Manifest.permission.POST_NOTIFICATIONS }, 200);

            } else {
                canalNotificacion(this);
            }
        }
    }


    /* Este metodo sirve para poder capturar la respuesta -
     * del usuario(a) ante la solicitud de los permisos, en -
     * caso, el de las notificaciones. Sin este metodo, -
     * prácticamente no se podria saber que respuesta -
     * dio el usuario(a), además de dar un posible error. */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

        /* Aqui lo que se esta validando es ver si la variable: -
         * requestCode es igual a 200, dando a entender que es -
         * el permiso relacionado a las notificaciones. */
        if(requestCode == 200) {

            /* Aqui lo que se esta validando es ver si el usuario(a) -
             * acepto el permiso de las notificaciones, además de ver -
             * si es mayor a 0, dando entender que hay un permiso en -
             * dicha lista. */
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                canalNotificacion(this);
                Toast.makeText(MainActivity.this, "¡Permiso otorgado!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MainActivity.this, "¡Permiso denegado!", Toast.LENGTH_LONG).show();
            }
        }
    }


    /* Metodo que sirve para crear el canal relacionado a las -
     * notificaciones para la aplicación móvil respectivamente. */
    private void canalNotificacion(Context context) {
        /* Aqui lo que se esta haciendo es simplemente verificar si -
         * el usuario(a) tiene una versión de Android 8.0 o superior, -
         * esto debido a que en estas versiones se implemento el concepto -
         * de los canales, los cuales ayudan al usuario (como al desarrollador) -
         * a definir que notificaciones son importantes y cuales no.
         *
         * Básicamente es una agrupación de tipos de notificaciones, por ejemplo:
         * un canal enfocado exclusivamente para publicidad, otro enfocado a advertencias -
         * y asi sucesivamente, por lo que esto es una buena manera para que el usuario no -
         * sienta que la aplicación lo este bombardeando de notificaciones constantemente. -
         * Además, esto el usuario lo puede modificar y nosotros no a nivel de ejecución.
         *
         * NOTA: Aquí tanto el nivel de importancia como el tipo de prioridad son muy -
         * importantes, ya que ambos están correlacionados sobre que tan intrusivo o -
         * no debe ser una notificación, en este caso, el nivel de importancia es para -
         * las versiones de Android 8.0 o posteriores, y el nivel de prioridad es para -
         * las versiones de Android 7.1 o anteriores.
         *
         * Además, con lo que mencionan en la documentación oficial, nos recomiendan crear -
         * este canal cuando se inicie la aplicación móvil, de ahi que se coloco en la clase -
         * principal respectivamente. */
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String nombreCanal = "Archivos compartidos.";
            String descripcionCanal = "Es un canal enfocado exclusivamente para notificar al usuario(a) si le compartieron un archivo respectivamente.";
            int tipoImportancia = NotificationManager.IMPORTANCE_DEFAULT;

            /* Aqui es donde se crea el canal de notificación que se va a utilizar, en este caso, este -
             * canal va a estar enfocado en si el usuario(a) recibe un archivo compartido. También, en -
             * dicho canal se le esta colocando su identificador unico, el nombre de dicho canal, el -
             * tipo de importancia que va a tener, la descripción que llevaria y el tipo de color, luz -
             * y de vibración respectivamente. */
            NotificationChannel canalNotificacion =
                    new NotificationChannel("Canal_RecepcionArchivo", nombreCanal, tipoImportancia);

            canalNotificacion.setDescription(descripcionCanal);
            canalNotificacion.setLightColor(R.color.green);
            canalNotificacion.enableVibration(true);
            canalNotificacion.enableLights(true);


            /* Aqui ya se terminaría de crear el canal de notificaciones, básicamente, con el -
             * NotificationManager, lo que se esta haciendo es pedir el servicio de la clase -
             * que controla las notificaciones, de forma que finalmente nos pueda permitir -
             * crear por completo el canal de notificaciones respectivamente.
             *
             * Además, se agrego una validación por si da nulo el NotificationManager y así -
             * evitar posibles errores. */
            NotificationManager manejadorNotificaciones = context.getSystemService(NotificationManager.class);
            if(manejadorNotificaciones != null) {
                manejadorNotificaciones.createNotificationChannel(canalNotificacion);
            }
        }
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de registro respectivamente (básicamente es la pantallita). */
    private void VistaRegistro() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentRegistro = new Intent(MainActivity.this, RegistroActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentRegistro);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible iniciar sesión en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                    .setTitle("¡Lo sentimos!");

                            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}});

                            AlertDialog ejecutarMensaje = construirAlerta.create();
                            ejecutarMensaje.show();
                        }
                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Esto es para imprimir los mensajes de error. */
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible iniciar sesión debido a que:" + error.replace('"', ' ') + "\n\nPor favor, corriga los datos e intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible iniciar sesión en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<JWTEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(MainActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible iniciar sesión en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    /* Metodo que sirve para validar los datos que esta colocando el usuario -
     * para poder iniciar sesión respectivamente. */
    private boolean ValidarInicioSesion() {
        try {
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
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo iniciar sesión debido a que el correo y la contraseña están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
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
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo iniciar sesión debido a que el correo electrónico se encuentra vacio o esta incorrecto." + "\n\nPor favor, digite otra vez el correo eletrónico e intentelo de nuevo.")
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
             * advertencia al usuario y un false al metodo: RealizarInicioSesion. */
            if (textoContraseña.isEmpty() || textoContraseña.length() < 12) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo iniciar sesión debido a que la contraseña se encuentra vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su contraseña e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }

            return true;

        } catch (Exception error) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible iniciar sesión en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }
    }


    /* Metodo que sirve para llevar el usuario hacia la vista -
     * de recuperar contraseña respectivamente (básicamente es -
     * la pantallita). */
    private void VistaRecuperarPassword() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPassword = new Intent(MainActivity.this, RecuperarPasswordActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentPassword);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
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
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(MainActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible iniciar sesión en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();
                }
            }
        }
    }
}