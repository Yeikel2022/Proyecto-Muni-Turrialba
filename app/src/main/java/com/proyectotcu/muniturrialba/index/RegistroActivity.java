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
     * También se sustituyo aspectos como "find by id (y similares)" por el uso del ViewBinding,-
     * el cual es una nueva forma para llamar los elementos de la vista de una forma más optimizada.
     */
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

    /* Este metodo sirve para realizar el registro.*/
    private void RealizarRegistro() {
        /* Primero se llama el metodo: ValidarRegistro, -
        *  esto para validar los campos. */
        boolean respuestaValidacion = ValidarRegistro();


        /* Una vez hecho eso, si la respuesta que trae es un true, -
         * entonces quiere decir que todos los campos estan bien. */
        if (respuestaValidacion != false) {
            /* Se llama la conexión del API. Esto para ver -
             * si se puede hacer la conexión hacia al mismo API. */
            usuarioInterface = ConexionAPI.Conexion_API();

            /* Luego se procede a llamar la entidad para poder registrar los -
             * datos que el usuario esta colocando en el registro.
             *
             * Ahora, como es un POST, es necesario pasarlo a un constructor parametrizado -
             * para que el sistema sepa en que variables tiene que almacenar esos datos. */
            UsuarioEntitie usuarioEntitie = new UsuarioEntitie(textoNombre, textoApellido_1,
                    textoApellido_2, Integer.parseInt(textoEdad), textoCedula, textoTelefono,
                    textoCorreo, textoContraseña, null, 3);

            /* Después se llama el metodo para enviar los datos que están almacenados en el -
             * constructor, esto por medio de la interfaz respectiva.  */
            Call<UsuarioEntitie> enviarUsuario = usuarioInterface.enviarUsuario(usuarioEntitie);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo POST.*/
            enviarUsuario.enqueue(new Callback<UsuarioEntitie>() {

                /* Aqui es si la respuesta logra llegar a la aplicación. */
                @Override
                public void onResponse(
                        Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                        /* Si la respuesta que se recibio del proceso POST resulto -
                         * correcta, entonces siga con lo demás, caso contrario, -
                         * muestre el error respectivo. */
                        if (response.isSuccessful()) {
                            //Imprime un mensaje indicando que se pudo hacer el registro.
                            Toast.makeText(RegistroActivity.this, "¡Se registro exitosamente!",
                                    Toast.LENGTH_SHORT).show();

                            /* Se resetean las variables gloables. Esto por temas de -
                             * buenas practicas. */
                            textoNombre = null;
                            textoApellido_1 = null;
                            textoApellido_2 = null;

                            textoEdad = null;
                            textoCedula = null;
                            textoTelefono = null;

                            textoCorreo = null;
                            textoContraseña = null;

                            //Esto es para enviarlo al metodo del menu.
                            VistaMenu();

                        } else {
                            try {
                                //Permite leer el error del Body, de modo que sirva en el debug.
                                String error = response.errorBody().string();

                                //Imprime el error capturado.
                                Toast.makeText(RegistroActivity.this, error,
                                        Toast.LENGTH_LONG).show();

                                //Este sirve solo para el logcat.
                                //System.out.println(error);
                            } catch (Exception error) {
                                /* El printStackTrace(), sirve para aspectos de depuración.
                                 * Esto debido a que ayuda a entender donde y porque ocurrio -
                                 * un error durante la ejecución del proyecto. En este caso -
                                 * las Excepciones respectivamente.

                                 * error.printStackTrace();*/
                                Toast.makeText(RegistroActivity.this, "¡Lo sentimos!\n" +
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

                    Toast.makeText(RegistroActivity.this, "¡Lo sentimos! \n" +
                            "¡Pero no se pudo realizar el registro!", Toast.LENGTH_LONG).show();

                    Toast.makeText(RegistroActivity.this, "Por favor, intentelo de nuevo.",
                            Toast.LENGTH_LONG).show();
                }

                });
        }
    }


    /* Este metodo sirve para validar los campos del -
    *  registro.*/
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

        //En este caso el telefono si puede tener campos vacios (que seria permitir nulos).
        textoTelefono = registroBinding.edtxtTelefono.getText().toString().trim();

        textoCorreo = registroBinding.edtxtCorreoRegistro.getText().toString().trim();
        textoContraseña = registroBinding.edtxtPasswordRegistro.getText().toString().trim();

        //Luego, lo segundo seria validar esos campos respectivamente.
        /* Aqui valida si el nombre es nulo o esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y -
         * un false al metodo: RealizarRegistro. */
        if (textoNombre == null || textoNombre.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El nombre se encuentra vacio!\n"
                            + "Por favor, digite otra vez su nombre.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el primer apellido es nulo o esta vacio. Y si entra -
         * entonces mandaria un mensaje de advertencia al usuario y un false -
         * al metodo: RealizarRegistro. */
        if (textoApellido_1 == null || textoApellido_1.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El primer apellido se encuentra vacio! \n" +
                    "Por favor, digite otra vez su apellido.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el segundo apellido es nulo o esta vacio. Y si -
         * entra entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarRegistro. */
        if (textoApellido_2 == null || textoApellido_2.isEmpty()) {
            Toast.makeText(RegistroActivity.this, "¡El segundo apellido se encuentra vacio! \n"
                    + "Por favor, digite otra vez su apellido.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la edad esta vacia, o si dicha edad es mayor a 99 (lo que -
         * indicaria que tiene 3 digitos), o si es igual a cero. Y si entra entonces -
         * mandaria un mensaje de advertencia al usuario y un false al metodo: -
         * RealizarRegistro. */
        if (textoEdad.isEmpty() || Integer.parseInt(textoEdad) == 0 || Integer.parseInt(textoEdad) > 99) {
            Toast.makeText(RegistroActivity.this, "¡La edad esta vacia o esta incorrecta! \n" +
                    "Por favor, digite otra vez su edad.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si la cedula esta vacia o si es mayor a 12 (esto porque -
         * en Costa Rica la cedula nacional es de 9 digitos y el extranjero 12). -
         * Y si entra entonces mandaria un mensaje de advertencia al usuario y un -
         * false al metodo: RealizarRegistro. */
        if (textoCedula.isEmpty() || textoCedula.length() > 12) {
            Toast.makeText(RegistroActivity.this, "¡La cedula esta vacia o supera el limite! \n" +
                    "Por favor, digite otra vez su cédula.", Toast.LENGTH_LONG).show();
            return false;
        }


        /* Aqui valida si el correo esta vacio o si tiene errores en el formato.
         * Y si entra, entonces mandaria un mensaje de advertencia al usuario -
         * y un false al metodo: RealizarRegistro.
         *
         * NOTA: El comando: !Patterns.EMAIL_ADDRESS.matcher(textoCorreo).matches(), -
         * lo que ayuda es para saber si el correo esta bien o no, esto por medio de -
         * lo siguiente: "Patterns.EMAIL_ADDRESS" es un comando que brinda Android Studio -
         * para poder manejar diferentes patrones como si fuera una expresión regular, -
         * en este caso, correo. Ahora con: "matcher(textoCorreo)" sirve para comparar el -
         * correo ingresado con el patron que nosotros habiamos solicitado, osea el Patterns, -
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
     * menu principal de la aplicación movil. */
    private void VistaMenu() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentMenu = new Intent(RegistroActivity.this, MenuPrincipalActivity.class);

        //Le indica que ejecute el hipervinculo.
        startActivity(intentMenu);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }

}
