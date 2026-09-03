package com.proyectotcu.muniturrialba.index;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityCambiarPasswordBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.JWTEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarPasswordActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityCambiarPasswordBinding cambiarPasswordBinding;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cambiarPasswordBinding = ActivityCambiarPasswordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(cambiarPasswordBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_CambiarPassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* Sirve para llamar el metodo de cambiar contraseña, siempre y cuando -
         * el usuario presione el boton respectivo. */
        cambiarPasswordBinding.btnCambiarPassword.setOnClickListener(v -> CambiarContraseña());
    }

    /* Metodo que sirve para cambiar la contraseña que coloca el usuario, -
     * esto para poder continuar con la recuperación de su cuenta respecti-
     * vamente. */
    private void CambiarContraseña() {
        /* Lo primero es guardar los datos que coloca el usuario -
         * en estas variables de texto. Luego con el metodo: trim(), -
         * nos ayudara a quitar los espacios que pueden estar al -
         * principio o al final del texto. */
        String contraseñaIngresada = cambiarPasswordBinding.edtxtCambiarNuevoPassword.getText().toString().trim();
        String contraseñaConfirmada = cambiarPasswordBinding.edtxtConfirmarNuevoPassword.getText().toString().trim();


        /* Luego, una vez hecho eso, lo segundo que se va a hacer -
         * es validar la contraseña que coloco el usuario.
         * Y, si luego de eso, la respuesta que trae es un true, -
         * entonces quiere decir que todos los campos estan bien. */
        boolean respuestaValidación = ValidarContraseña(contraseñaIngresada, contraseñaConfirmada);
        if(respuestaValidación != false) {
            /* Aqui lo que se hace es obtener el correo -
             * que nosotros habiamos enviado anteriormente -
             * en la clase de: "VistaCambiarContraseña". De-
             * forma que ahora se guarda ese correo y se -
             * pueda utilizar para realizar el inicio de -
             * sesión, de ahi el getStringExtra("Correo"). */
            Intent recuperarCorreo = getIntent();
            String correoRecorrido = recuperarCorreo.getStringExtra("Correo");

            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en esta clase se esta pidiendo esa conexión -
             * del API como tal, de ahí el "this". */
            usuarioInterface = ConexionAPI.Conexion_API(this);


            /* Luego de eso se procede a llamar la entidad: "ExtensionUsuarioEntitie" para poder -
             * registrar esos datos que el usuario esta colocando para cambiar la contraseña como-
             * tal.
             *
             * Ahora, como es un PUT, donde se va a cambiar la contraseña de ese usuario, es -
             * necesario pasarlo a un constructor parametrizado para que el sistema sepa en que -
             * variables tiene que almacenar esos datos. */
            ExtensionUsuarioEntitie extensionUsuarioEntitie = new ExtensionUsuarioEntitie(
                    correoRecorrido, contraseñaConfirmada);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder realizar la actualización de esos datos que están almacenados en el -
             * constructor: "extensionUsuarioEntitie" y que el API lo pueda recibir. Esto por-
             * medio de la interfaz respectiva. */
            Call<Boolean> actualizarContraseña = usuarioInterface.actualizarContraseña(extensionUsuarioEntitie);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo PUT.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            actualizarContraseña.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo cambiar la contraseña.
                        Toast.makeText(CambiarPasswordActivity.this,
                                "¡La contraseña se cambio exitosamente!", Toast.LENGTH_SHORT).show();

                        /* Esto es para enviarlo al metodo de iniciar -
                         * sesión de la aplicación móvil. */
                        IniciaSesion(correoRecorrido,contraseñaConfirmada);

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            //Imprime el error capturado.
                            Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, pero en este momento no se cambiar la contraseña!", Toast.LENGTH_LONG).show();
                            Toast.makeText(CambiarPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                            Toast.makeText(CambiarPasswordActivity.this, "Por favor, corriga los datos e intentelo de nuevo.", Toast.LENGTH_LONG).show();

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */
                            Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, " +
                                    "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                            Toast.makeText(CambiarPasswordActivity.this,
                                    "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                        }
                    }
                }


                /* Aqui es para saber si hubo un fallo en dar la respuesta -
                 * por parte del API. */
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el usuario no lo -
                     * debe ver:
                     * Toast.makeText(CambiarPasswordActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, " +
                            "pero en este momento no se pudo cambiar la contraseña!", Toast.LENGTH_LONG).show();

                    Toast.makeText(CambiarPasswordActivity.this,
                            "Por favor, intentelo de nuevo.", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    /* Este metodo sirve para validar la contraseña -
     * que esta colocando el usuario respectivamente. */
    private boolean ValidarContraseña(String contraseñaIngresada, String contraseñaConfirmada) {
        /* Aqui valida si la contraseña ingresada y confirmada están vacios. Y si -
         * entra, entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: CambiarContraseña. */
        if (contraseñaIngresada.isEmpty() && contraseñaConfirmada.isEmpty()) {
            Toast.makeText(CambiarPasswordActivity.this, "¡Los espacios para la nueva contraseña están vacios!", Toast.LENGTH_LONG).show();
            Toast.makeText(CambiarPasswordActivity.this, "Por favor, ingrese todos los campos requeridos.", Toast.LENGTH_LONG).show();
            return false;
        }

        /* Aqui valida si la contraseña ingresada esta vacia o si es menor a 12 (que -
         * es el minimo de digitos de la contraseña). Y si entra, entonces mandaria -
         * un mensaje de advertencia al usuario y un false al metodo: CambiarContraseña. */
        if (contraseñaIngresada.isEmpty() || contraseñaIngresada.length() < 12) {
            Toast.makeText(CambiarPasswordActivity.this,
                    "¡La nueva contraseña esta vacia o esta incorrecta!", Toast.LENGTH_LONG).show();

            Toast.makeText(CambiarPasswordActivity.this,
                    "Por favor, digite otra vez su contraseña.", Toast.LENGTH_LONG).show();
            return false;
        }

        /* Aqui valida si la contraseña confirmada esta vacia o si es menor a 12 (que -
         * es el minimo de digitos de la contraseña). Y si entra, entonces mandaria -
         * un mensaje de advertencia al usuario y un false al metodo: CambiarContraseña. */
        if (contraseñaConfirmada.isEmpty() || contraseñaConfirmada.length() < 12) {
            Toast.makeText(CambiarPasswordActivity.this,
                    "¡La nueva contraseña esta vacia o esta incorrecta!", Toast.LENGTH_LONG).show();

            Toast.makeText(CambiarPasswordActivity.this,
                    "Por favor, digite otra vez su contraseña.", Toast.LENGTH_LONG).show();
            return false;
        }

        /* Aqui valida si la contraseña confirmada es diferente a la contraseña ingresada.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: CambiarContraseña. */
        if (!contraseñaConfirmada.equals(contraseñaIngresada)) {
            Toast.makeText(CambiarPasswordActivity.this,
                    "¡Las contraseñas están incorrectas!", Toast.LENGTH_LONG).show();

            Toast.makeText(CambiarPasswordActivity.this,
                    "Por favor, asegurese que sea la misma contraseña en ambos lados.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /* Metodo que sirve para realizar el inicio de sesión por parte del -
     * usuario respectivamente. */
    private void IniciaSesion(String correo, String contraseña) {
        /* Aquí se llama la conexión del API. Además de indicarle -
         * también que en esta clase se esta pidiendo esa conexión -
         * del API como tal, de ahí el "this". */
        usuarioInterface = ConexionAPI.Conexion_API(this);

        /* Luego de eso se procede a llamar la entidad: "UsuarioEntitie" para poder registrar -
         * esos datos que el usuario esta colocando en el inicio de sesión como tal.
         *
         * Ahora, como es un POST, donde se va a hacer lo del inicio de sesión es necesario -
         * pasarlo a un constructor parametrizado para que el sistema sepa en que variables -
         * tiene que almacenar esos datos. */
        UsuarioEntitie usuarioEntitie = new UsuarioEntitie(null, null,
                null, 0, null, null, correo, contraseña,
                null, 0);

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
                        String campoNombre = json.optString("nombre");
                        String campoApellido_1 = json.optString("primer_Apellido");
                        String campoApellido_2 = json.optString("segundo_Apellido");
                        MainActivity.botonActivado = true;
                        VistaMenu(campoNombre, campoApellido_1, campoApellido_2);

                    } catch (JSONException error) {
                        /* Sirve para imprimir el mensaje que se recibio anteriormente.
                         *
                         * NOTA: Este comando es para ver que fallo, el usuario no lo debe ver:
                         * Toast.makeText(CambiarPasswordActivity.this, error.toString(), Toast.LENGTH_LONG).show(); */
                        Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, " +
                                "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this,
                                "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        /* Esto permite leer el error del Body, de modo -
                         * que sirva en el debug. */
                        String error = response.errorBody().string();

                        /* Esto es para imprimir los mensajes de error. */
                        Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, pero en este momento no se pudo iniciar sesión!", Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this, "Por favor, corriga los datos e intentelo de nuevo.", Toast.LENGTH_LONG).show();

                        /* Este sirve solo para el logcat:
                         * System.out.println(error); */
                    } catch (Exception error) {
                        /* El printStackTrace(), sirve para aspectos de depuración.
                         * Esto debido a que ayuda a entender donde y porque ocurrio -
                         * un error durante la ejecución del proyecto. En este caso -
                         * las excepciones respectivamente.
                         *
                         * error.printStackTrace(); */
                        Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, " +
                                "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this,
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
                 * Toast.makeText(CambiarPasswordActivity.this, t.getLocalizedMessage(),
                 * Toast.LENGTH_SHORT).show(); */
                Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos, " +
                        "pero en estos instantes no se pudo continuar con el proceso!", Toast.LENGTH_LONG).show();

                Toast.makeText(CambiarPasswordActivity.this,
                        "Por favor, intentelo de nuevo.", Toast.LENGTH_LONG).show();
            }

        });

    }


    /* Este metodo sirve para poder enviar el usuario hacia al -
     * menú principal de la aplicación movil. */
    private void VistaMenu(String nombre, String apellido1, String apellido2) {
        //Para imprimir el mensaje de bienvenida.
        Toast.makeText(CambiarPasswordActivity.this, "¡Bienvenido(a): " + nombre +
                " " + apellido1 + " " + apellido2 + "!", Toast.LENGTH_LONG).show();

        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentMenu = new Intent(CambiarPasswordActivity.this, MenuPrincipalActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentMenu);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas.
         *
         * NOTA: A diferencia del finish() que eliminaba una -
         * actividad, el Affinity sirve para eliminar todas -
         * las actividades que existen respectivamente. */
        finishAffinity();
    }




}