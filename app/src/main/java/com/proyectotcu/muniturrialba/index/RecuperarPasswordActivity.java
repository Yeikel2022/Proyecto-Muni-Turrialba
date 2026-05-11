package com.proyectotcu.muniturrialba.index;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityRecuperarPasswordBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarPasswordActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityRecuperarPasswordBinding recuperarPasswordBinding;

    //Variable global para esta clase.
    String textoCorreo;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        recuperarPasswordBinding = ActivityRecuperarPasswordBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(recuperarPasswordBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_RecuperarPassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /* Sirve para llamar el metodo de enviar un correo electrónico, siempre -
         * y cuando el usuario presione el boton respectivo. */
        recuperarPasswordBinding.btnRecuperarCuenta.setOnClickListener(v -> EnviarCorreo());
    }

    /* Metodo que sirve para enviar el correo electrónico que coloca el usuario -
     * para poder recuperar su cuenta y contraseña respectivamente. */
    private void EnviarCorreo() {
        /* Lo primero es guardar los datos que coloca el usuario -
         * en esta variable global. Luego con el metodo: trim(), -
         * nos ayudara a quitar los espacios que pueden estar al -
         * principio o al final del texto. */
        textoCorreo = recuperarPasswordBinding.edtxtCorreoRecuperacion.getText().toString().trim();


        /* Luego, una vez hecho eso, lo segundo que se va a hacer -
         * es validar el correo electrónico que coloco el usuario.
         * Y, si luego de eso, la respuesta que trae es un true, -
         * entonces quiere decir que todos los campos estan bien. */
        boolean respuestaValidacion = ValidarCorreo(textoCorreo);
        if(respuestaValidacion == true) {
            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en esta clase se esta pidiendo esa conexión -
             * del API como tal, de ahí el "this". */
            usuarioInterface = ConexionAPI.Conexion_API(this);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder enviar ese dato que está almacenado en la variable: textoCorreo y que -
             * el API lo pueda recibir. Esto por medio de la interfaz respectiva. */
            Call<Boolean> enviarCorreo = usuarioInterface.enviarCorreo(textoCorreo);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            enviarCorreo.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el envio del correo.
                        Toast.makeText(RecuperarPasswordActivity.this,
                                "¡El correo se envio exitosamente!", Toast.LENGTH_SHORT).show();

                        /* Luego de eso, se enviaria el correo hacia al metodo: -
                         * VistaCodigo, de modo que así se pueda seguir con la r-
                         * ecuperación de la cuenta y contraseña como tal. */
                        VistaCodigo(textoCorreo);

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            //Imprime el error capturado.
                            Toast.makeText(RecuperarPasswordActivity.this, "¡Lo sentimos, pero en este momento no se pudo enviar el correo!", Toast.LENGTH_LONG).show();
                            Toast.makeText(RecuperarPasswordActivity.this, error, Toast.LENGTH_LONG).show();
                            Toast.makeText(RecuperarPasswordActivity.this, "Por favor, corriga los datos e intentelo de nuevo.", Toast.LENGTH_LONG).show();

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */
                            Toast.makeText(RecuperarPasswordActivity.this, "¡Lo sentimos, " +
                                    "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                            Toast.makeText(RecuperarPasswordActivity.this,
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
                     * Toast.makeText(RecuperarPasswordActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    Toast.makeText(RecuperarPasswordActivity.this, "¡Lo sentimos, " +
                            "pero en este momento no se pudo enviar el correo!", Toast.LENGTH_LONG).show();

                    Toast.makeText(RecuperarPasswordActivity.this,
                            "Por favor, intentelo de nuevo.", Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    /* Este metodo sirve para validar el correo electronico -
     * que esta colocando el usuario respectivamente. */
    private boolean ValidarCorreo(String correo) {
        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: EnviarCorreo.
         *
         * NOTA: El comando: !Patterns.EMAIL_ADDRESS.matcher(correo).matches(), -
         * lo que ayuda es para saber si el correo esta bien o no, esto por medio de -
         * lo siguiente: "Patterns.EMAIL_ADDRESS", el cual es un comando que brinda -
         * Android Studio para poder manejar diferentes patrones como si fuera una -
         * expresión regular, en este caso, un correo electronico.
         *
         * Ahora con: "matcher(correo)", esto sirve para poder comparar el correo -
         * ingresado con ese patron que nosotros habiamos solicitado, osea el Patterns, -
         * luego de eso el: "matches()" nos ayudara a dar el resultado de esa comparación. */
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(RecuperarPasswordActivity.this, "¡El correo esta vacio o esta incorrecto! \n" +
                    "Por favor, digite otra vez su correo.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /* Este metodo sirve para poder enviar el usuario hacia la -
     * siguiente vista del proceso de recuperar la contraseña y -
     * su cuenta en la aplicación movil. */
    private void VistaCodigo(String correoDigitado) {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentCodigo = new Intent(RecuperarPasswordActivity.this, CodigoRecuperacionActivity.class);

        /* Aquí lo que se esta haciendo es mandar el correo del usuario, -
         * esto porque más adelante se necesitara para el inicio de sesión -
         * respectivamente. */
        intentCodigo.putExtra("Correo", correoDigitado);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentCodigo);
    }



}