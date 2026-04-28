package com.proyectotcu.muniturrialba.index;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityCambiarPasswordBinding;
import com.proyectotcu.muniturrialba.databinding.ActivityCodigoRecuperacionBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionUsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CambiarPasswordActivity extends AppCompatActivity {

    private ActivityCambiarPasswordBinding cambiarPasswordBinding;
    UsuarioInterface usuarioInterface;

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
        cambiarPasswordBinding.btnCambiarContraseA.setOnClickListener(v -> CambiarContraseña());
    }

    private void CambiarContraseña() {
        String contraseñaIngresada = cambiarPasswordBinding.edtxtCambiarNuevoPassword.getText().toString().trim();
        String contraseñaConfirmada = cambiarPasswordBinding.edtxtConfirmarNuevoPassword.getText().toString().trim();

        boolean respuestaValidación = ValidarContraseña(contraseñaIngresada, contraseñaConfirmada);

        if(respuestaValidación != false) {
            Intent recuperarCorreo = getIntent();
            String correoRecorrido = recuperarCorreo.getStringExtra("Correo");

            usuarioInterface = ConexionAPI.Conexion_API();

            /* Luego se procede a llamar la entidad para poder registrar los -
             * datos que el usuario esta colocando en el registro.
             *
             * Ahora, como es un POST, es necesario pasarlo a un constructor parametrizado -
             * para que el sistema sepa en que variables tiene que almacenar esos datos. */
            ExtensionUsuarioEntitie extensionUsuarioEntitie = new ExtensionUsuarioEntitie(
                    correoRecorrido, contraseñaConfirmada);

            Call<Boolean> actualizarContraseña =
                    usuarioInterface.actualizarContraseña(extensionUsuarioEntitie);

            /*Call<UsuarioEntitie> actualizarContraseña =
                    usuarioInterface.actualizarContraseña(contraseñaConfirmada, correoRecorrido);*/

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.*/
            actualizarContraseña.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    /* Si la respuesta que se recibio del proceso POST resulto -
                     * correcta, entonces siga con lo demás, caso contrario, -
                     * muestre el error respectivo. */
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el envio del correo.
                        Toast.makeText(CambiarPasswordActivity.this, "¡Se cambio la contraseña exitosamente!",
                                Toast.LENGTH_SHORT).show();

                        //Esto es para enviarlo al metodo del VistaCodigo.
                        IniciaSesion(correoRecorrido,contraseñaConfirmada);

                    } else {
                        try {
                            //Permite leer el error del Body, de modo que sirva en el debug.
                            String error = response.errorBody().string();

                            //Imprime el error capturado.
                            Toast.makeText(CambiarPasswordActivity.this, error,
                                    Toast.LENGTH_LONG).show();

                            //Este sirve solo para el logcat.
                            //System.out.println(error);
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las Excepciones respectivamente.

                             * error.printStackTrace();*/
                            Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos!\n" +
                                    "¡Parece que hubo un problema!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente. NOTA: Este comando es para ver -
                     * que fallo, el usuario no lo debe ver:
                     * Toast.makeText(RegistroActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show();*/

                    Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se cambiar la contraseña!", Toast.LENGTH_LONG).show();

                    Toast.makeText(CambiarPasswordActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private boolean ValidarContraseña(String contraseñaIngresada, String contraseñaConfirmada) {
        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: VistaInicioSesion. */
        if (contraseñaIngresada.isEmpty() || contraseñaIngresada.length() < 12) {
            Toast.makeText(CambiarPasswordActivity.this, "¡La nueva contraseña esta vacia o esta incorrecta!",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(CambiarPasswordActivity.this,"Por favor, digite otra vez su contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (contraseñaConfirmada.isEmpty() || contraseñaConfirmada.length() < 12) {
            Toast.makeText(CambiarPasswordActivity.this, "¡La nueva contraseña esta vacia o esta incorrecta!",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(CambiarPasswordActivity.this,"Por favor, digite otra vez su contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (!contraseñaConfirmada.equals(contraseñaIngresada)) {
            Toast.makeText(CambiarPasswordActivity.this, "¡La contraseña esta incorrecta, deber ser la misma!",
                    Toast.LENGTH_LONG).show();

            Toast.makeText(CambiarPasswordActivity.this,"Por favor, asegurese que sea la misma contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void IniciaSesion(String correo, String contraseña) {
        String Correo_recibido = correo;

        usuarioInterface = ConexionAPI.Conexion_API();

        //Se trae el metodo correspondiente que tiene dicha interfaz.
        Call<UsuarioEntitie> usuario = usuarioInterface.obtenerUsuario(correo);

        //Se ejecuta el metodo y se espera la respuesta.
        usuario.enqueue(new Callback<UsuarioEntitie>() {
            //Este metodo es para saber si hubo respuesta por parte del API.
            @Override
            public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                /* Si la respuesta que trajo se pudo realizar correctamente, entonces -
                 * siga con lo demás, caso contrario, muestre el error respectivo. */
                if (response.isSuccessful()) {
                    /* Se almacenan los datos y se quitan los espacios que pueden tener -
                     * adelante y hacia atras. */
                    String CorreoObtenido = response.body().getCorreo_Electronico().trim();
                    String ContraseñaObtenida = response.body().getContraseña().trim();

                    /* Si el correo y la contraseña que puso el usuario, es igual al que -
                     * esta en el sistema, entonces quiere decir que es el mismo usuario. -
                     * Por lo que le dejaria iniciar sesión, si no fuera así, entonces -
                     * daria error respectivamente. */
                    if (Objects.equals(correo, CorreoObtenido) &&
                            Objects.equals(contraseña, ContraseñaObtenida)) {
                        String campoNombre = response.body().getNombre().trim();
                        String campoApellido_1 = response.body().getApellido_1().trim();
                        String campoApellido_2 = response.body().getApellido_2().trim();
                        VistaMenu(campoNombre, campoApellido_1, campoApellido_2);
                    } else {
                        Toast.makeText(CambiarPasswordActivity.this, "¡No se pudo iniciar sesión! \n" +
                                "La contraseña esta incorrecta.", Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this, "Por favor, intentelo de nuevo.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        //Permite leer el error del Body, de modo que sirva en el debug.
                        String error = response.errorBody().string();

                        /* Esto es para imprimir los mensajes de error. */
                        Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos! \n" +
                                "¡No se pudo iniciar sesión!", Toast.LENGTH_LONG).show();
                        Toast.makeText(CambiarPasswordActivity.this, error, Toast.LENGTH_LONG).show();

                        //Este sirve solo para el logcat.
                        //System.out.println(error);
                    } catch (Exception error) {
                        /* El printStackTrace(), sirve para aspectos de depuración.
                         * Esto debido a que ayuda a entender donde y porque ocurrio -
                         * un error durante la ejecución del proyecto. En este caso -
                         * las Excepciones respectivamente.

                         * error.printStackTrace();*/
                        Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos!\n" +
                                "¡Parece que hubo un problema!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            /* Este metodo es para saber si se fallo en dar una respuesta por -
             * parte del API.*/
            @Override
            public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                /* Sirve para imprimir el mensaje que se recibio -
                 * anteriormente. */
                /* Este es para ver que fallo, el usuario no lo debe ver:
                 * Toast.makeText(MainActivity.this, t.getLocalizedMessage(),
                 * Toast.LENGTH_SHORT).show(); */

                Toast.makeText(CambiarPasswordActivity.this, "¡Lo sentimos! \n" +
                        "¡Pero no se pudo continuar!", Toast.LENGTH_LONG).show();

                Toast.makeText(CambiarPasswordActivity.this, "Por favor, intentelo de nuevo.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    /* Este metodo sirve para poder enviar el usuario hacia al -
     * menu principal de la aplicación movil. */
    private void VistaMenu(String nombre, String apellido1, String apellido2) {
        //Para imprimir el mensaje de bienvenida.
        Toast.makeText(CambiarPasswordActivity.this, "¡Bienvenido(a): " + nombre +
                " " + apellido1 + " " + apellido2 + "!", Toast.LENGTH_LONG).show();

        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentMenu = new Intent(CambiarPasswordActivity.this,
                MenuPrincipalActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentMenu);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finishAffinity();
    }




}