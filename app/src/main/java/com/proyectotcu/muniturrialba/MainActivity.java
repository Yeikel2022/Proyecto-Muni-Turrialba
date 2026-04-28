package com.proyectotcu.muniturrialba;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

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
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityMainBinding mainBinding;

    //Variables globales para esta clase.
    String textoCorreo, textoContraseña, campoNombre,
            campoApellido_1, campoApellido_2;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
    * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
    * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
    */
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

        /* Sirve para llamar el metodo de registro y de inicio de sesion, siempre y cuando -
        *  el usuario presione el boton respectivo. */
        mainBinding.btnRegistro.setOnClickListener(v -> VistaRegistro());
        mainBinding.btnInicioSesion.setOnClickListener(v -> VistaInicioSesion());
        mainBinding.btnPassword.setOnClickListener(v -> VistaRecuperarPassword());
    }

    /* Metodo que sirve para llevar el usuario hacia la vista (que es la pantallita) -
    *  de registro.   */
    private void VistaRegistro() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentRegistro = new Intent(MainActivity.this, RegistroActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentRegistro);
    }

    /* Metodo que sirve para llevar el usuario hacia la vista (que es la pantallita) -
     * de inicio de sesion.   */
    private void VistaInicioSesion() {
        //Lo primero es resetear las variables globales.
        textoCorreo = null;
        textoContraseña = null;
        campoNombre = null;
        campoApellido_1 = null;
        campoApellido_2 = null;

        //Luego lo segundo seria realizar una validación de los datos.
        boolean respuestaValidacion = ValidarInicioSesion();

        /* Si la respuesta que trajo el metodo es diferente a falso (osea que es un true), -
         * entonces puede seguir con el proceso. */
        if (respuestaValidacion != false) {
            //Aquí se manda a llamar la conexión hacia al API.
            usuarioInterface = ConexionAPI.Conexion_API();

            //Se trae el metodo correspondiente que tiene dicha interfaz.
            Call<UsuarioEntitie> usuario = usuarioInterface.obtenerUsuario(textoCorreo);

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

                            /* Esto es para imprimir los mensajes de error. */
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

                             * error.printStackTrace();*/
                            Toast.makeText(MainActivity.this, "¡Lo sentimos!\n" +
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

                    Toast.makeText(MainActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se pudo continuar!", Toast.LENGTH_LONG).show();

                    Toast.makeText(MainActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /* Metodo que sirve para validar los datos que esta colocando el usuario -
     * respectivamente. */
    private boolean ValidarInicioSesion(){
        /* Se registran los datos que coloca el usuario. Eso si, también quitandole los -
         * posibles espacios que pueden tener adelante o hacia atras. */
        textoCorreo = mainBinding.edtxtCampoEmail.getText().toString().trim();
        textoContraseña = mainBinding.edtxtCampoPassword.getText().toString().trim();


        /* Aqui valida si el correo esta vacio o si tiene errores en cuanto al formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario, y un false -
         * al metodo: VistaInicioSesion.
         *
         * NOTA: El comando: !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches(), -
         * lo que ayuda es para saber si el correo esta bien o no, esto por medio de -
         * lo siguiente: "Patterns.EMAIL_ADDRESS" es un comando que brinda Android Studio -
         * para poder manejar diferentes patrones como si fuera una expresión regular, -
         * en este caso, correo. Ahora con: "matcher(textoCorreo)" sirve para comparar el -
         * correo ingresado con el patron que nosotros habiamos solicitado, osea el Patterns, -
         * luego de eso el: "matches()" nos ayudara a dar el resultado de esa comparación. */
        if (textoCorreo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches()) {
            Toast.makeText(MainActivity.this, "¡El correo esta vacio o esta incorrecto! \n" +
                    "Por favor, digite otra vez su correo.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: VistaInicioSesion. */
        if (textoContraseña.isEmpty() || textoContraseña.length() < 12) {
            Toast.makeText(MainActivity.this, "¡La contraseña esta vacia o esta incorrecta!",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(MainActivity.this,"Por favor, digite otra vez su contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    /* Metodo que sirve para llevar el usuario hacia la vista (que es la pantallita) -
     *  de recuperar contraseña.   */
    private void VistaRecuperarPassword() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPassword = new Intent(MainActivity.this, RecuperarPasswordActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentPassword);
    }

    /* Este metodo sirve para poder enviar el usuario hacia al -
     * menu principal de la aplicación movil. */
    private void VistaMenu() {
        //Para imprimir el mensaje de bienvenida.
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



}