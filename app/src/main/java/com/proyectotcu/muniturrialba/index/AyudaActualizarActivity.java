package com.proyectotcu.muniturrialba.index;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityAyudaActualizarBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.FAQEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.FAQInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AyudaActualizarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityAyudaActualizarBinding ayudaActualizarBinding;

    //Variables globales:
    String preguntaActualizada, respuestaActualizada, tipoPrioridadActualizada, preguntaRecorrido,
            respuestaRecorrido, tipoPrioridadRecorrido;

    //Interfaz que contiene los métodos de la entidad FAQ.
    FAQInterface faqInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ayudaActualizarBinding = ActivityAyudaActualizarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(ayudaActualizarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_AyudaActualizar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ayudaActualizarBinding.imgFotoActualizarAyuda.setVisibility(GONE);
        ayudaActualizarBinding.txtMensajeActualizarAyuda.setVisibility(GONE);

        try {
            /* Aqui lo que se hace es obtener el correo -
             * que nosotros habiamos enviado anteriormente -
             * en la clase de: "VistaCodigo". De forma que -
             * ahora se guarda ese correo y se pueda enviar -
             * hacia a la siguiente vista, que es donde se va -
             * a ocupar (de ahi el getStringExtra("Correo")).*/
            Intent recuperarFAQ = getIntent();
            preguntaRecorrido = recuperarFAQ.getStringExtra("Pregunta");
            respuestaRecorrido = recuperarFAQ.getStringExtra("Respuesta");
            tipoPrioridadRecorrido = recuperarFAQ.getStringExtra("Tipo_Prioridad");

            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            ArrayAdapter<CharSequence> propiedadesSpinner = ArrayAdapter.createFromResource(this, R.array.listaTipo_Prioridades, R.layout.spinner_color);
            propiedadesSpinner.setDropDownViewResource(R.layout.spinner_opciones_color);
            ayudaActualizarBinding.spTipoPrioridadActualizar.setAdapter(propiedadesSpinner);

            ayudaActualizarBinding.edtxtPreguntaActualizar.setText(preguntaRecorrido);
            ayudaActualizarBinding.edtxtRespuestaActualizar.setText(respuestaRecorrido);
            ayudaActualizarBinding.spTipoPrioridadActualizar.setSelection(propiedadesSpinner.getPosition(tipoPrioridadRecorrido));


            ayudaActualizarBinding.btnActualizar.setOnClickListener(v -> {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de actualizar esta pregunta?")
                        .setTitle("Actualizar FAQ.");


                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarFAQ(tokenGuardado);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(AyudaActualizarActivity.this, "¡No se continuo con la actualización!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });

            ayudaActualizarBinding.spTipoPrioridadActualizar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tipoPrioridadActualizada = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    tipoPrioridadActualizada = null;
                }
            });

        } catch (Exception error) {
            ayudaActualizarBinding.txtTituloActualizarAyuda.setVisibility(GONE);
            ayudaActualizarBinding.txtInfoActualizarAyuda.setVisibility(GONE);
            ayudaActualizarBinding.edtxtPreguntaActualizar.setVisibility(GONE);
            ayudaActualizarBinding.edtxtRespuestaActualizar.setVisibility(GONE);
            ayudaActualizarBinding.spTipoPrioridadActualizar.setVisibility(GONE);
            ayudaActualizarBinding.btnActualizar.setVisibility(GONE);

            ayudaActualizarBinding.imgFotoActualizarAyuda.setVisibility(VISIBLE);
            ayudaActualizarBinding.txtMensajeActualizarAyuda.setVisibility(VISIBLE);
            ayudaActualizarBinding.imgFotoActualizarAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            ayudaActualizarBinding.txtMensajeActualizarAyuda.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void ActualizarFAQ(String tokenUsuario) {

        preguntaActualizada = ayudaActualizarBinding.edtxtPreguntaActualizar.getText().toString().trim();
        respuestaActualizada = ayudaActualizarBinding.edtxtRespuestaActualizar.getText().toString().trim();

        boolean respuestaValidacion = ValidarFAQs(preguntaActualizada, respuestaActualizada, tipoPrioridadActualizada);
        if(respuestaValidacion != false) {

            faqInterface = ConexionAPI.Conexion_API_FAQ(this);

            FAQEntitie faqEntitie = new FAQEntitie(preguntaActualizada, respuestaActualizada, tipoPrioridadActualizada);
            Call<Boolean> actualizarFAQ = faqInterface.actualizarFAQ(faqEntitie, preguntaRecorrido, tokenUsuario);

            actualizarFAQ.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el registro.
                        Toast.makeText(AyudaActualizarActivity.this,
                                "¡La pregunta y respuesta se actualizo exitosamente!", Toast.LENGTH_SHORT).show();

                        /* Se resetean las variables globales. Esto por temas de -
                         * buenas practicas. */
                        preguntaActualizada = null;
                        respuestaActualizada = null;
                        tipoPrioridadActualizada = null;

                        preguntaRecorrido = null;
                        respuestaRecorrido = null;
                        tipoPrioridadRecorrido = null;

                        /* Esto es para enviarlo al metodo principal -
                         * de la aplicación móvil. */
                        VistaRegreso();
                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Esto es para imprimir los mensajes de error. */
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible actualizar la pregunta debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible actualizar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible actualizar la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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


    private boolean ValidarFAQs(String pregunta, String respuesta, String tipoPrioridad) {
        if (pregunta.equals(preguntaRecorrido) && respuesta.equals(respuestaRecorrido) && tipoPrioridad.equals(tipoPrioridadRecorrido)) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar la pregunta debido a que están los mismos datos." + "\n\nPor favor, ingrese los nuevos cambios que desea para actualizar esta pregunta.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        if (pregunta.isEmpty() && respuesta.isEmpty() && tipoPrioridad.equals("Seleccione el tipo de prioridad:")) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar la pregunta debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        Pattern patronPreguntas = Pattern.compile("¿.*\\?");
        Matcher compararPregunta = patronPreguntas.matcher(pregunta);
        if (pregunta.isEmpty() || pregunta.equals(null) || !compararPregunta.find()) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar la pregunta debido a que esta se encuentra vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su pregunta e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        if (respuesta.isEmpty() || respuesta.equals(null)) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar la pregunta debido a que la respuesta se encuentra vacia." + "\n\nPor favor, digite otra vez su respuesta e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        if (tipoPrioridad.isEmpty() || tipoPrioridad.equals(null) || tipoPrioridad.equals("Seleccione el tipo de prioridad:")) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo actualizar la pregunta debido a que el tipo de prioridad se encuentra vacia." + "\n\nPor favor, digite otra vez la prioridad que desea e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        return true;
    }


    private void VistaRegreso() {
        //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
        Intent intentAyudaFAQ = new Intent(AyudaActualizarActivity.this, MenuPrincipalActivity.class);

        intentAyudaFAQ.putExtra("Seccion_A_Mostrar", "Ayuda");

        //Le indica que ejecute el hipervinculo.
        startActivity(intentAyudaFAQ);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }

}