package com.proyectotcu.muniturrialba.index;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityCodigoRecuperacionBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodigoRecuperacionActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityCodigoRecuperacionBinding codigoRecuperacionBinding;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        codigoRecuperacionBinding = ActivityCodigoRecuperacionBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(codigoRecuperacionBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_CodigoRecuperarPassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /* Sirve para llamar el metodo de enviar un codigo de recuperación, siempre -
         * y cuando el usuario presione el boton respectivo. */
        codigoRecuperacionBinding.btnCodigoRecuperacion.setOnClickListener(v -> EnviarCodigo());

        //METODO PARA DETECTAR SI EL USUARIO POR X O Y RAZÓN, DESEA REGRESARSE.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentRecuperarContra = new Intent(CodigoRecuperacionActivity.this, RecuperarPasswordActivity.class);

                //Le indica que ejecute el hipervinculo.
                startActivity(intentRecuperarContra);

                /* Sirve para evitar que el usuario se regrese después.
                 * Esto por temas de buenas prácticas. */
                finish();
            }
        });
    }

    /* Metodo que sirve para enviar el codigo de recuperación que coloca el -
     * usuario para poder continuar con la recuperación de su cuenta y de su -
     * contraseña respectivamente. */
    private void EnviarCodigo() {
        /* Lo primero es guardar los datos que coloca el usuario -
         * en esta variable de texto. Luego con el metodo: trim(), -
         * nos ayudara a quitar los espacios que pueden estar al -
         * principio o al final del texto. */
        String codigoIngresado = codigoRecuperacionBinding.edtxtCodigo.getText().toString().trim();


        /* Luego, una vez hecho eso, lo segundo que se va a hacer -
         * es validar el codigo de recuperación que coloco el usuario.
         * Y, si luego de eso, la respuesta que trae es un true, -
         * entonces quiere decir que todos los campos estan bien. */
        boolean respuestaValidación = ValidarCodigo(codigoIngresado);
        if(respuestaValidación != false) {
            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en esta clase se esta pidiendo esa conexión -
             * del API como tal, de ahí el "this". */
            usuarioInterface = ConexionAPI.Conexion_API(this);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder enviar ese dato que está almacenado en la variable: codigoIngresado -
             * y que el API lo pueda recibir. Esto por medio de la interfaz respectiva. */
            Call<Boolean> enviarCodigo = usuarioInterface.enviarCodigo(codigoIngresado);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo GET.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            enviarCodigo.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        /* Aqui lo que se esta haciendo es obtener la respuesta que nos -
                         * dio el API (de ahí el body), y se convierte a un booleano.
                         *
                         * Luego, después se valida la respuesta, de modo que si es un -
                         * true, entonces quiere decir que el código esta bien. */
                        boolean respuestaCodigo = Boolean.parseBoolean(response.body().toString());
                        if (respuestaCodigo != false) {
                            //Imprime un mensaje indicando que el codigo es esta correcto.
                            Toast.makeText(CodigoRecuperacionActivity.this,
                                    "¡El código proporcionado es correcto!", Toast.LENGTH_SHORT).show();

                            /* Aqui lo que se hace es obtener el correo -
                             * que nosotros habiamos enviado anteriormente -
                             * en la clase de: "VistaCodigo". De forma que -
                             * ahora se guarda ese correo y se pueda enviar -
                             * hacia a la siguiente vista, que es donde se va -
                             * a ocupar (de ahi el getStringExtra("Correo")). */
                            Intent recuperarCorreo = getIntent();
                            String correoRecorrido = recuperarCorreo.getStringExtra("Correo");

                            /* Luego de eso, se enviaria el correo hacia al metodo: -
                             * VistaCambiarContraseña, de modo que así se pueda seguir-
                             * con la recuperación de la cuenta y contraseña como tal. */
                            VistaCambiarContraseña(correoRecorrido);
                        }

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 404) {
                                error = " El código es necesario.";
                            }

                            //Imprime el error capturado.
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(CodigoRecuperacionActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no se pudo validar el código debido a que:" + error.replace('"', ' ') + "\n\nPor favor, corriga los datos e intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(CodigoRecuperacionActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible validar el código en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el usuario no lo -
                     * debe ver:
                     * Toast.makeText(CodigoRecuperacionActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(CodigoRecuperacionActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible validar el código en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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

    /* Este metodo sirve para validar el codigo de recuperación -
     * que esta colocando el usuario respectivamente. */
    private boolean ValidarCodigo(String codigo) {
        try {
            /* Aqui valida si el código esta vacio o si es nulo. Y si entra, -
             * entonces mandaria un mensaje de advertencia al usuario y un -
             * false al metodo: EnviarCodigo. */
            if (codigo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(CodigoRecuperacionActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo validar el código debido a que este mismo se encuentra vacio." + "\n\nPor favor, digite otra vez el código e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(CodigoRecuperacionActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible validar el código en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }
    }

    /* Este metodo sirve para poder enviar el usuario hacia la -
     * siguiente vista del proceso de recuperar la contraseña y -
     * su cuenta en la aplicación movil. */
    private void VistaCambiarContraseña(String correoDigitado) {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentCodigo = new Intent(CodigoRecuperacionActivity.this, CambiarPasswordActivity.class);

        /* Aquí lo que se esta haciendo es mandar el correo del usuario, -
         * esto porque más adelante se necesitara para el inicio de sesión -
         * respectivamente. */
        intentCodigo.putExtra("Correo", correoDigitado);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentCodigo);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }
}