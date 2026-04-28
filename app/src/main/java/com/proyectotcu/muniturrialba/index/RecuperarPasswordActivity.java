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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarPasswordActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityRecuperarPasswordBinding recuperarPasswordBinding;

    UsuarioInterface usuarioInterface;

    String textoCorreo;

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

        recuperarPasswordBinding.btnRecuperarCuenta.setOnClickListener(v ->
                EnviarCorreo());
    }

    private void EnviarCorreo() {
        textoCorreo = recuperarPasswordBinding.edtxtCorreoRecuperacion.getText().toString().trim();
        boolean respuestaValidacion = ValidarCorreo(textoCorreo);

        if(respuestaValidacion == true) {
            usuarioInterface = ConexionAPI.Conexion_API();


            Call<UsuarioEntitie> enviarCorreo = usuarioInterface.enviarCorreo(textoCorreo);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.*/
            enviarCorreo.enqueue(new Callback<UsuarioEntitie>() {

                /* Aqui es si la respuesta logra llegar a la aplicación. */
                @Override
                public void onResponse(
                        Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                    /* Si la respuesta que se recibio del proceso POST resulto -
                     * correcta, entonces siga con lo demás, caso contrario, -
                     * muestre el error respectivo. */
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el envio del correo.
                        Toast.makeText(RecuperarPasswordActivity.this, "¡Se envio el correo exitosamente!",
                                Toast.LENGTH_SHORT).show();

                        //Esto es para enviarlo al metodo del VistaCodigo.
                        VistaCodigo(textoCorreo);

                    } else {
                        try {
                            //Permite leer el error del Body, de modo que sirva en el debug.
                            String error = response.errorBody().string();

                            //Imprime el error capturado.
                            Toast.makeText(RecuperarPasswordActivity.this, error,
                                    Toast.LENGTH_LONG).show();

                            //Este sirve solo para el logcat.
                            //System.out.println(error);
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las Excepciones respectivamente.

                             * error.printStackTrace();*/
                            Toast.makeText(RecuperarPasswordActivity.this, "¡Lo sentimos!\n" +
                                    "¡Parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        }
                    }
                }


                /* Aqui es si la respuesta fallo en llegar. */
                @Override
                public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente. NOTA: Este comando es para ver -
                     * que fallo, el usuario no lo debe ver:
                     * Toast.makeText(RegistroActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show();*/

                    Toast.makeText(RecuperarPasswordActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se pudo enviar el correo!", Toast.LENGTH_LONG).show();

                    Toast.makeText(RecuperarPasswordActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }

            });

        }

    }

    private boolean ValidarCorreo(String correo) {
        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: EnviarCorreo.
         *
         * NOTA: El comando: !Patterns.EMAIL_ADDRESS.matcher(correo).matches(), -
         * lo que ayuda es para saber si el correo esta bien o no, esto por medio de -
         * lo siguiente: "Patterns.EMAIL_ADDRESS" es un comando que brinda Android Studio -
         * para poder manejar diferentes patrones como si fuera una expresión regular, -
         * en este caso, correo. Ahora con: "matcher(correo)" sirve para comparar el -
         * correo ingresado con el patron que nosotros habiamos solicitado, osea el Patterns, -
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
        intentCodigo.putExtra("Correo", correoDigitado);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentCodigo);
    }



}