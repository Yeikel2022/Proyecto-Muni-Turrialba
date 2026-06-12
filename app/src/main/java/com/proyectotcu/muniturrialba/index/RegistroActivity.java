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

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityRegistroBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistroActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityRegistroBinding registroBinding;

    //Variables globales:
    String textoNombre, textoApellido_1, textoApellido_2, textoEdad,
           textoCedula, textoTelefono, textoCorreo, textoContraseña;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;

    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registroBinding = ActivityRegistroBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(registroBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_Registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /* Sirve para llamar el metodo de registro, siempre y cuando el usuario presione -
         * el boton respectivo. */
        registroBinding.btnRegistraCuenta.setOnClickListener(v -> RealizarRegistro());
    }

    /* Este metodo sirve para realizar el registro. */
    private void RealizarRegistro() {
        /* Primero se llama el metodo: ValidarRegistro, -
         * esto para validar los campos. */
        boolean respuestaValidacion = ValidarRegistro();


        /* Una vez hecho eso, si la respuesta que trae es un true, -
         * entonces quiere decir que todos los campos estan bien. */
        if (respuestaValidacion != false) {
            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en esta clase se esta pidiendo esa conexión -
             * del API como tal, de ahí el "this". */
            usuarioInterface = ConexionAPI.Conexion_API(this);

            /* Luego de eso se procede a llamar la entidad: "UsuarioEntitie" para poder registrar -
             * esos datos que el usuario esta colocando en el registro como tal.
             *
             * Ahora, como es un POST, donde se va a registrar (o crear) ese usuario, es necesario-
             * pasarlo a un constructor parametrizado para que el sistema sepa en que variables -
             * tiene que almacenar esos datos. */
            UsuarioEntitie usuarioEntitie = new UsuarioEntitie(textoNombre, textoApellido_1,
                    textoApellido_2, Integer.parseInt(textoEdad), textoCedula, textoTelefono,
                    textoCorreo, textoContraseña, null, 3);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder realizar la creación (o registro) de esos datos que están almacenados -
             * en el constructor: "usuarioEntitie" y que el API lo pueda recibir. Esto por medio -
             * de la interfaz respectiva. */
            Call<UsuarioEntitie> registrarUsuario = usuarioInterface.registrarUsuario(usuarioEntitie, true);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            registrarUsuario.enqueue(new Callback<UsuarioEntitie>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                        /* Si la respuesta que se recibio resulto ser correcta, entonces -
                         * siga con lo demás, caso contrario, pues muestre el error respectivo. */
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(RegistroActivity.this,
                                    "¡La cuenta se registro exitosamente!", Toast.LENGTH_SHORT).show();

                            /* Se resetean las variables globales. Esto por temas de -
                             * buenas practicas. */
                            textoNombre = null;
                            textoApellido_1 = null;
                            textoApellido_2 = null;

                            textoEdad = null;
                            textoCedula = null;
                            textoTelefono = null;

                            textoCorreo = null;
                            textoContraseña = null;

                            /* Esto es para enviarlo al metodo principal -
                             * de la aplicación móvil. */
                            VistaPrincipal();

                        } else {
                            try {
                                /* Esto permite leer el error del Body, de modo -
                                 * que sirva en el debug. */
                                String error = response.errorBody().string();

                                //Imprime el error capturado.
                                Toast.makeText(RegistroActivity.this, "¡Lo sentimos, pero en este momento no se pudo crear la cuenta!", Toast.LENGTH_LONG).show();
                                Toast.makeText(RegistroActivity.this, error, Toast.LENGTH_LONG).show();
                                Toast.makeText(RegistroActivity.this, "Por favor, corriga los datos e intentelo de nuevo.", Toast.LENGTH_LONG).show();

                                /* Este sirve solo para el logcat:
                                 * System.out.println(error); */
                            } catch (Exception error) {
                                /* El printStackTrace(), sirve para aspectos de depuración.
                                 * Esto debido a que ayuda a entender donde y porque ocurrio -
                                 * un error durante la ejecución del proyecto. En este caso -
                                 * las excepciones respectivamente.
                                 *
                                 * error.printStackTrace(); */
                                Toast.makeText(RegistroActivity.this, "¡Lo sentimos, " +
                                        "pero parece que hubo un problema!", Toast.LENGTH_LONG).show();
                                Toast.makeText(RegistroActivity.this,
                                        "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
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

                    Toast.makeText(RegistroActivity.this, "¡Lo sentimos, " +
                            "pero en este momento no se pudo realizar el registro!", Toast.LENGTH_LONG).show();

                    Toast.makeText(RegistroActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }

                });
        }
    }


    /* Este metodo sirve para validar los campos del -
     * registro. */
    private boolean ValidarRegistro() {
        /* Lo primero es guardar los datos que coloca el usuario -
         * en estas variables globales. Luego con el metodo: trim(), -
         * nos ayudara a quitar los espacios que pueden estar al -
         * principio o al final del texto. */
        textoNombre = registroBinding.edtxtNombre.getText().toString().trim();
        textoApellido_1 = registroBinding.edtxtApellido1.getText().toString().trim();
        textoApellido_2 = registroBinding.edtxtApellido2.getText().toString().trim();

        textoEdad =  registroBinding.edtxtEdad.getText().toString().trim();
        textoCedula = registroBinding.edtxtCedula.getText().toString().trim();
        textoTelefono = registroBinding.edtxtTelefono.getText().toString().trim();
        //En este caso el telefono si puede tener campos vacios (que seria permitir nulos).

        textoCorreo = registroBinding.edtxtCorreoRegistro.getText().toString().trim();
        textoContraseña = registroBinding.edtxtPasswordRegistro.getText().toString().trim();


        /* Aqui valida si el nombre, los apellidos, la edad, la cédula, el correo electronico -
         * y la contraseña están vacios. Y si entra, entonces mandaria un mensaje de advertencia -
         * al usuario y un false al metodo: RealizarRegistro. */
        if (textoNombre.isEmpty() && textoApellido_1.isEmpty() && textoApellido_2.isEmpty() &&
                textoEdad.isEmpty() && textoCedula.isEmpty() && textoCorreo.isEmpty() &&
                textoContraseña.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡Todos los espacios están vacios!", Toast.LENGTH_LONG).show();
            Toast.makeText(RegistroActivity.this, "Por favor, ingrese todos los campos requeridos (excepto el teléfono).", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Luego, lo segundo seria validar esos campos respectivamente.
         * Aqui valida si el nombre es nulo o si esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y -
         * un false al metodo: RealizarRegistro. */
        if (textoNombre == null || textoNombre.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El nombre se encuentra vacio!\n"
                            + "Por favor, digite otra vez su nombre.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el primer apellido es nulo o si esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: RealizarRegistro. */
        if (textoApellido_1 == null || textoApellido_1.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El primer apellido se encuentra vacio! \n" +
                    "Por favor, digite otra vez su apellido.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el segundo apellido es nulo o si esta vacio. Y si -
         * entra entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarRegistro. */
        if (textoApellido_2 == null || textoApellido_2.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El segundo apellido se encuentra vacio! \n"
                    + "Por favor, digite otra vez su apellido.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la edad se encuentra vacia, si es igual a cero, o si -
         * dicha edad es mayor a 99 (lo que indicaria que tiene 3 digitos). Y -
         * si entra entonces mandaria un mensaje de advertencia al usuario y un -
         * false al metodo: RealizarRegistro. */
        if (textoEdad.isEmpty() || Integer.parseInt(textoEdad) == 0 || Integer.parseInt(textoEdad) > 99) {
            Toast.makeText(RegistroActivity.this, "¡La edad esta vacia o esta incorrecta! \n" +
                    "Por favor, digite otra vez su edad.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la cédula esta vacia o si es mayor a 12 (esto porque -
         * en Costa Rica la cédula nacional es de 9 digitos y el extranjero 12). -
         * Y si entra entonces mandaria un mensaje de advertencia al usuario y un -
         * false al metodo: RealizarRegistro. */
        if (textoCedula.isEmpty() || textoCedula.length() > 12) {
            Toast.makeText(RegistroActivity.this, "¡La cédula esta vacia o supera el limite! \n" +
                    "Por favor, digite otra vez su cédula.", Toast.LENGTH_LONG).show();
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
        if (textoCorreo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches()) {
            Toast.makeText(RegistroActivity.this, "¡El correo esta vacio o esta incorrecto! \n" +
                    "Por favor, digite otra vez su correo.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la contraseña esta vacia o si es menor a 12 (que es el minimo -
         * de digitos de la contraseña). Y si entra, entonces mandaria un mensaje de -
         * advertencia al usuario y un false al metodo: RealizarRegistro. */
        if (textoContraseña.isEmpty() || textoContraseña.length() < 12) {
            Toast.makeText(RegistroActivity.this, "¡La contraseña esta vacia o esta incorrecta!",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(RegistroActivity.this,"Por favor, digite otra vez su contraseña.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    /* Este metodo sirve para poder enviar el usuario hacia al -
     * al principio de la aplicación móvil. Esto para que pueda -
     * iniciar sesión respectivamente. */
    private void VistaPrincipal() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentPrincipal = new Intent(RegistroActivity.this, MainActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentPrincipal);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }

}
