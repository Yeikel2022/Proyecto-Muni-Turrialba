package com.proyectotcu.muniturrialba.index;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityCodigoRecuperacionBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodigoRecuperacionActivity extends AppCompatActivity {

    private ActivityCodigoRecuperacionBinding codigoRecuperacionBinding;

    UsuarioInterface usuarioInterface;

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
        codigoRecuperacionBinding.btnCodigoRecuperacion.setOnClickListener(v -> EnviarCodigo());
    }

    private void EnviarCodigo() {
        String codigoIngresado = codigoRecuperacionBinding.edtxtCodigo.getText().toString().trim();
        boolean respuestaValidación = ValidarCodigo(codigoIngresado);

        if(respuestaValidación != false) {
            usuarioInterface = ConexionAPI.Conexion_API();
            Call<Boolean> enviarCodigo = usuarioInterface.enviarCodigo(codigoIngresado);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.*/
            enviarCodigo.enqueue(new Callback<Boolean>() {

                /* Aqui es si la respuesta logra llegar a la aplicación. */
                @Override
                public void onResponse(
                        Call<Boolean> call, Response<Boolean> response) {
                    /* Si la respuesta que se recibio del proceso POST resulto -
                     * correcta, entonces siga con lo demás, caso contrario, -
                     * muestre el error respectivo. */
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el envio del correo.
                        boolean respuestaCodigo = Boolean.parseBoolean(response.body().toString());

                        if (respuestaCodigo != false) {
                            Toast.makeText(CodigoRecuperacionActivity.this, "¡Codigo correcto!",
                                    Toast.LENGTH_SHORT).show();

                            Intent recuperarCorreo = getIntent();
                            String correoRecorrido = recuperarCorreo.getStringExtra("Correo");

                            //Esto es para enviarlo al metodo del VistaCodigo.
                            VistaCambiarContraseña(correoRecorrido);
                        }

                    } else {
                        try {
                            //Permite leer el error del Body, de modo que sirva en el debug.
                            String error = response.errorBody().string();

                            //Imprime el error capturado.
                            Toast.makeText(CodigoRecuperacionActivity.this, error,
                                    Toast.LENGTH_LONG).show();

                            //Este sirve solo para el logcat.
                            //System.out.println(error);
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las Excepciones respectivamente.

                             * error.printStackTrace();*/
                            error.printStackTrace();
                            Toast.makeText(CodigoRecuperacionActivity.this, "¡Lo sentimos!\n" +
                                    "¡Parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        }
                    }
                }


                /* Aqui es si la respuesta fallo en llegar. */
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente. NOTA: Este comando es para ver -
                     * que fallo, el usuario no lo debe ver:
                     * Toast.makeText(RegistroActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show();*/
                    Toast.makeText(CodigoRecuperacionActivity.this, t.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(CodigoRecuperacionActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se pudo validar el codigo!", Toast.LENGTH_LONG).show();

                    Toast.makeText(CodigoRecuperacionActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }

            });
        }
    }

    private boolean ValidarCodigo(String codigo) {
        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: EnviarCodigo. */
        if (codigo == null || codigo.isEmpty()) {
            Toast.makeText(CodigoRecuperacionActivity.this, "¡El código esta vacio! \n" +
                    "Por favor, digite otra vez su código.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    /* Este metodo sirve para poder enviar el usuario hacia la -
     * siguiente vista del proceso de recuperar la contraseña y -
     * su cuenta en la aplicación movil. */
    private void VistaCambiarContraseña(String correoDigitado) {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentCodigo = new Intent(CodigoRecuperacionActivity.this,
                CambiarPasswordActivity.class);

        intentCodigo.putExtra("Correo", correoDigitado);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentCodigo);

        finish();
    }

}