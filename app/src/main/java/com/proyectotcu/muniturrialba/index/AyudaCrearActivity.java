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
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityAyudaCrearBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.FAQEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.FAQInterface;
import com.proyectotcu.muniturrialba.moduloEmpleados.ControlEmpleadosActivity;
import com.proyectotcu.muniturrialba.moduloEmpleados.PermisoTiempoCrearActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AyudaCrearActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityAyudaCrearBinding ayudaCrearBinding;

    //Variables globales:
    String preguntaIngresada, respuestaIngresada, tipoPrioridadIngresada;

    //Interfaz que contiene los métodos de la entidad FAQ.
    FAQInterface faqInterface;


    /* Este metodo sirve para poder crear y enlazar la clase hacia la vista respectiva.
     * También, se sustituyo aspectos similares (como por ejemplo el "find by id"), por -
     * el uso del ViewBinding. El cual es una nueva forma para llamar los elementos de -
     * la vista de una forma más optimizada. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ayudaCrearBinding = ActivityAyudaCrearBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(ayudaCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_AyudaCrear), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ayudaCrearBinding.imgFotoCrearAyuda.setVisibility(GONE);
        ayudaCrearBinding.txtMensajeCrearAyuda.setVisibility(GONE);

        try {
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
            ayudaCrearBinding.spTipoPrioridad.setAdapter(propiedadesSpinner);

            ayudaCrearBinding.btnCrear.setOnClickListener(v -> CrearNuevoFAQ(tokenGuardado));
            ayudaCrearBinding.spTipoPrioridad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tipoPrioridadIngresada = parent.getItemAtPosition(position).toString();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    tipoPrioridadIngresada = null;
                }
            });

        } catch (Exception error) {
            ayudaCrearBinding.txtTituloCrearAyuda.setVisibility(GONE);
            ayudaCrearBinding.txtInfoCrearAyuda.setVisibility(GONE);
            ayudaCrearBinding.edtxtPregunta.setVisibility(GONE);
            ayudaCrearBinding.edtxtRespuesta.setVisibility(GONE);
            ayudaCrearBinding.spTipoPrioridad.setVisibility(GONE);
            ayudaCrearBinding.btnCrear.setVisibility(GONE);

            ayudaCrearBinding.imgFotoCrearAyuda.setVisibility(VISIBLE);
            ayudaCrearBinding.txtMensajeCrearAyuda.setVisibility(VISIBLE);
            ayudaCrearBinding.imgFotoCrearAyuda.setImageResource(R.drawable.icono_contenido_no_disponible);
            ayudaCrearBinding.txtMensajeCrearAyuda.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */


            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible crear la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        //METODO PARA DETECTAR SI EL USUARIO POR X O Y RAZÓN, DESEA REGRESARSE.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
                Intent intentAyudaFAQ = new Intent(AyudaCrearActivity.this, MenuPrincipalActivity.class);

                intentAyudaFAQ.putExtra("Seccion_A_Mostrar", "Ayuda");

                //Le indica que ejecute el hipervinculo.
                startActivity(intentAyudaFAQ);

                /* Sirve para evitar que el usuario se regrese después.
                 * Esto por temas de buenas prácticas. */
                finish();
            }
        });
}


    private void CrearNuevoFAQ(String tokenUsuario) {

        preguntaIngresada = ayudaCrearBinding.edtxtPregunta.getText().toString().trim();
        respuestaIngresada = ayudaCrearBinding.edtxtRespuesta.getText().toString().trim();

        boolean respuestaValidacion = ValidarFAQs(preguntaIngresada, respuestaIngresada, tipoPrioridadIngresada);
        if(respuestaValidacion != false) {

            faqInterface = ConexionAPI.Conexion_API_FAQ(this);

            FAQEntitie faqEntitie = new FAQEntitie(preguntaIngresada, respuestaIngresada, tipoPrioridadIngresada);
            Call<FAQEntitie> crearNuevoFAQ = faqInterface.crearFAQ(faqEntitie, tokenUsuario);

            crearNuevoFAQ.enqueue(new Callback<FAQEntitie>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<FAQEntitie> call, Response<FAQEntitie> response) {
                    if (response.isSuccessful()) {
                        //Imprime un mensaje indicando que se pudo hacer el registro.
                        Toast.makeText(AyudaCrearActivity.this,
                                "¡La nueva pregunta y respuesta se creo exitosamente!", Toast.LENGTH_SHORT).show();

                        /* Se resetean las variables globales. Esto por temas de -
                         * buenas practicas. */
                        preguntaIngresada = null;
                        respuestaIngresada = null;
                        tipoPrioridadIngresada = null;

                        /* Esto es para enviarlo al metodo principal -
                         * de la aplicación móvil. */
                        VistaRegreso();
                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();
                            int errorRaw = response.raw().code();

                            if(errorRaw == 401) {
                                error = "Se finalizo la sesión de su cuenta.";
                            }

                            /* Esto es para imprimir los mensajes de error. */
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero en este momento no es posible crear la pregunta debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
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
                            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
                            construirAlerta.setIcon(R.drawable.icono_error);
                            construirAlerta.setMessage("Pero no es posible crear la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                public void onFailure(Call<FAQEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio -
                     * anteriormente.
                     *
                     * NOTA: Este comando es para ver que fallo, el -
                     * usuario no lo debe ver:
                     * Toast.makeText(AyudaCrearActivity.this, t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
                    construirAlerta.setIcon(R.drawable.icono_error);
                    construirAlerta.setMessage("Pero no es posible crear la pregunta en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
        if (pregunta.isEmpty() && respuesta.isEmpty() && tipoPrioridad.equals("Seleccione el tipo de prioridad:")) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear la pregunta debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear la pregunta debido a que esta se encuentra vacia o esta incorrecta." + "\n\nPor favor, digite otra vez su pregunta e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        if (respuesta.isEmpty() || respuesta.equals(null)) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear la pregunta debido a que la respuesta se encuentra vacia." + "\n\nPor favor, digite otra vez su respuesta e intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();

            return false;
        }

        if (tipoPrioridad.isEmpty() || tipoPrioridad.equals(null) || tipoPrioridad.equals("Seleccione el tipo de prioridad:")) {
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(AyudaCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no se pudo crear la pregunta debido a que el tipo de prioridad se encuentra vacia." + "\n\nPor favor, digite otra vez la prioridad que desea e intentelo de nuevo.")
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
        Intent intentAyudaFAQ = new Intent(AyudaCrearActivity.this, MenuPrincipalActivity.class);

        intentAyudaFAQ.putExtra("Seccion_A_Mostrar", "Ayuda");

        //Le indica que ejecute el hipervinculo.
        startActivity(intentAyudaFAQ);

        /* Sirve para evitar que el usuario se regrese después.
         * Esto por temas de buenas prácticas. */
        finish();
    }
}