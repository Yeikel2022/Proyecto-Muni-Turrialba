package com.proyectotcu.muniturrialba.moduloAdministracionArchivos;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.databinding.ActivityArchivoRecienteCrearBinding;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.RegistroArchivoEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.RegistroArchivoInterface;
import com.proyectotcu.muniturrialba.moduloEmpleados.UsuarioEmpleadoActivity;

import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArchivoRecienteCrearActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityArchivoRecienteCrearBinding archivoRecienteCrearBinding;

    //Variables globales:
    String archivoIngresado, nombreArchivoIngresado, formatoArchivoIngresado, nombreOriginarioIngresado, primerApellidoOriginarioIngresado, segundoApellidoOriginarioIngresado,
            correoOriginarioIngresado, departamentoOriginarioIngresado, nombreDestinatarioIngresado, primerApellidoDestinatarioIngresado, segundoApellidoDestinatarioIngresado,
            correoDestinatarioIngresado, departamentoDestinatarioIngresado, nombreDestinatarioRecorrido, primerApellidoDestinatarioRecorrido, segundoApellidoDestinatarioRecorrido,
            correoDestinatarioRecorrido, departamentoDestinatarioRecorrido;
    ActivityResultLauncher<Intent> launcher;

    Gson gson = new Gson();
    Type datosParseado = new TypeToken<String>(){}.getType();

    static String nombreUsuarioTemporal, primerApellidoTemporal, segundoApellidoTemporal, correoTemporal, departamentoTemporal;
    static String archivo, nombreArchivo, formatoArchivo;
    static int puntoTipoArchivo;

    public static Boolean autorizacionArchivoCrear = false;

    //Interfaz que contiene los métodos de la entidad FAQ.
    RegistroArchivoInterface registroArchivoInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        archivoRecienteCrearBinding = ActivityArchivoRecienteCrearBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(archivoRecienteCrearBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ArchivoReciente_Crear), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setVisibility(GONE);
        archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            String[] partesToken = tokenGuardado.split("\\.");

            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(cuerpoToken);

            Intent recuperarUsuarioDestinatario = getIntent();

            String campoNombreOriginario = json.optString("nombre");
            String campoPrimerApellidoOriginario = json.optString("primer_Apellido");
            String campoSegundoApellidoOriginario = json.optString("segundo_Apellido");
            String campoCorreoOriginario = json.optString("correo");
            String campoDepartamentoOriginario = json.optString("departamento");

            /* Esta parte es donde se obtiene el usuario destinatario que selecciono el usuario originario. */
            nombreDestinatarioRecorrido = recuperarUsuarioDestinatario.getStringExtra("nombre_UsuarioRecorrido");
            primerApellidoDestinatarioRecorrido = recuperarUsuarioDestinatario.getStringExtra("primerApellido_UsuarioRecorrido");
            segundoApellidoDestinatarioRecorrido = recuperarUsuarioDestinatario.getStringExtra("segundoApellido_UsuarioRecorrido");
            correoDestinatarioRecorrido = recuperarUsuarioDestinatario.getStringExtra("correoElectronico_UsuarioRecorrido");
            departamentoDestinatarioRecorrido = recuperarUsuarioDestinatario.getStringExtra("departamento_UsuarioRecorrido");

            /* Aqui lo que hace es obtener los datos originales que están relacionados al usuario destinatario, lo cual, indicaria -
             * que el usuario originario no selecciono un nuevo destinatario respectivamente. */
            if(autorizacionArchivoCrear == true) {
                nombreDestinatarioRecorrido = gson.fromJson(nombreUsuarioTemporal, datosParseado);
                primerApellidoDestinatarioRecorrido = gson.fromJson(primerApellidoTemporal, datosParseado);
                segundoApellidoDestinatarioRecorrido = gson.fromJson(segundoApellidoTemporal, datosParseado);

                correoDestinatarioRecorrido = gson.fromJson(correoTemporal, datosParseado);
                departamentoDestinatarioRecorrido = gson.fromJson(departamentoTemporal, datosParseado);
            }

            /* Aqui lo que se hace es obtener el archivo que selecciono el usuario originario, esto para poder compartirlo al usuario -
             * destinario respectivamente. */
            launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Intent datos = result.getData();

                    /* Se resetean estas variables para evitar posibles enredos con la información que esta relacionada con el archivo -
                     * que se selecciono. Además de ser una buena práctica. */
                    archivo = null;
                    nombreArchivo = null;
                    formatoArchivo = null;
                    puntoTipoArchivo = 0;

                    if(datos != null) {
                        Uri uriArchivoSeleccionado = datos.getData();
                        Cursor cursor = getContentResolver().query(uriArchivoSeleccionado,null, null, null, null);

                        if (cursor != null && cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            nombreArchivo = cursor.getString(index);
                            cursor.close();

                            puntoTipoArchivo = nombreArchivo.lastIndexOf(".");
                            if (puntoTipoArchivo != -1) {
                                formatoArchivo = nombreArchivo.substring(puntoTipoArchivo + 1).toUpperCase();
                            }

                            getContentResolver().takePersistableUriPermission(uriArchivoSeleccionado, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            archivo = gson.toJson(uriArchivoSeleccionado.toString());

                            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setText(uriArchivoSeleccionado.toString());
                            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setText(nombreArchivo.substring(0, puntoTipoArchivo));
                            archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.setText(formatoArchivo);
                        }
                    }
                }
            });

            /* Aqui lo que se hace es obtener el archivo original que el usuario habia seleccionado previamente, tanto su nombre como su -
             * tipo de formato. Esto se debe a que cuando el usuario, indepedientemente seleccionaba o no un usuario destinatario, la -
             * información de ese archivo desaparecia. Por lo que se coloco esta validación para recuperar dicha información respectivamente. */
            if (archivo != null) {
                Uri uriArchivoSeleccionado = Uri.parse(gson.fromJson(archivo, datosParseado));

                archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setText(uriArchivoSeleccionado.toString());
                archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setText(nombreArchivo.substring(0, puntoTipoArchivo));
                archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.setText(formatoArchivo);
            }


            /* Aqui lo que se hace es colocar la información del usuario originario y destinatario en sus respectivos -
             * campos. */
            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setMovementMethod(new ScrollingMovementMethod());
            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setMovementMethod(new ScrollingMovementMethod());
            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setLongClickable(false);
            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setLongClickable(false);

            archivoRecienteCrearBinding.txtNombreUsuarioOriginarioCrearArchivoReciente.setText(campoNombreOriginario);
            archivoRecienteCrearBinding.txtApellido1UsuarioOriginarioCrearArchivoReciente.setText(campoPrimerApellidoOriginario);
            archivoRecienteCrearBinding.txtApellido2UsuarioOriginarioCrearArchivoReciente.setText(campoSegundoApellidoOriginario);
            archivoRecienteCrearBinding.txtCorreoUsuarioOriginarioCrearArchivoReciente.setText(campoCorreoOriginario);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioOriginarioCrearArchivoReciente.setText(campoDepartamentoOriginario);

            archivoRecienteCrearBinding.txtNombreUsuarioDestinatarioCrearArchivoReciente.setText(nombreDestinatarioRecorrido);
            archivoRecienteCrearBinding.txtApellido1UsuarioDestinatarioCrearArchivoReciente.setText(primerApellidoDestinatarioRecorrido);
            archivoRecienteCrearBinding.txtApellido2UsuarioDestinatarioCrearArchivoReciente.setText(segundoApellidoDestinatarioRecorrido);
            archivoRecienteCrearBinding.txtCorreoUsuarioDestinatarioCrearArchivoReciente.setText(correoDestinatarioRecorrido);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioDestinatarioCrearArchivoReciente.setText(departamentoDestinatarioRecorrido);

            archivoRecienteCrearBinding.btnSeleccionarUsuarioDestinatarioArchivoReciente.setOnClickListener(v -> VistaAñadirDestinario(nombreDestinatarioRecorrido,
                    primerApellidoDestinatarioRecorrido, segundoApellidoDestinatarioRecorrido, correoDestinatarioRecorrido, departamentoDestinatarioRecorrido));

            archivoRecienteCrearBinding.btnSeleccionarArchivoArchivoReciente.setOnClickListener(v -> VistaAñadirArchivo());
            archivoRecienteCrearBinding.btnCrearArchivoReciente.setOnClickListener(v -> CrearNuevoRegistroArchivo(tokenGuardado));

        } catch (Exception error) {
            archivoRecienteCrearBinding.txtTituloCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.btnSeleccionarUsuarioDestinatarioArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnSeleccionarArchivoArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no fue posible finalizar el proceso en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                /* Se resetean estas variables para evitar posibles enredos, y también porque es -
                 * una buena práctica. */
                archivo = null;
                nombreArchivo = null;
                formatoArchivo = null;

                puntoTipoArchivo = 0;
                autorizacionArchivoCrear = false;

                nombreUsuarioTemporal = null;
                primerApellidoTemporal = null;
                segundoApellidoTemporal = null;

                correoTemporal = null;
                departamentoTemporal = null;

                Intent intentRegreso = new Intent(ArchivoRecienteCrearActivity.this, AdministracionArchivosActivity.class);

                startActivity(intentRegreso);

                finish();
            }
        });
    }


    private void CrearNuevoRegistroArchivo(String tokenUsuario) {
        try {
            archivoIngresado = archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.getText().toString().trim();
            nombreArchivoIngresado = archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.getText().toString().trim();
            formatoArchivoIngresado = archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.getText().toString().trim();

            nombreOriginarioIngresado = archivoRecienteCrearBinding.txtNombreUsuarioOriginarioCrearArchivoReciente.getText().toString().trim();
            primerApellidoOriginarioIngresado = archivoRecienteCrearBinding.txtApellido1UsuarioOriginarioCrearArchivoReciente.getText().toString().trim();
            segundoApellidoOriginarioIngresado = archivoRecienteCrearBinding.txtApellido2UsuarioOriginarioCrearArchivoReciente.getText().toString().trim();
            correoOriginarioIngresado = archivoRecienteCrearBinding.txtCorreoUsuarioOriginarioCrearArchivoReciente.getText().toString().trim();
            departamentoOriginarioIngresado = archivoRecienteCrearBinding.txtDepartamentoUsuarioOriginarioCrearArchivoReciente.getText().toString().trim();

            nombreDestinatarioIngresado = archivoRecienteCrearBinding.txtNombreUsuarioDestinatarioCrearArchivoReciente.getText().toString().trim();
            primerApellidoDestinatarioIngresado = archivoRecienteCrearBinding.txtApellido1UsuarioDestinatarioCrearArchivoReciente.getText().toString().trim();
            segundoApellidoDestinatarioIngresado = archivoRecienteCrearBinding.txtApellido2UsuarioDestinatarioCrearArchivoReciente.getText().toString().trim();
            correoDestinatarioIngresado = archivoRecienteCrearBinding.txtCorreoUsuarioDestinatarioCrearArchivoReciente.getText().toString().trim();
            departamentoDestinatarioIngresado = archivoRecienteCrearBinding.txtDepartamentoUsuarioDestinatarioCrearArchivoReciente.getText().toString().trim();

            boolean respuestaValidacion = ValidarRegistroArchivo(archivoIngresado, nombreArchivoIngresado, formatoArchivoIngresado, nombreOriginarioIngresado, primerApellidoOriginarioIngresado,
                    segundoApellidoOriginarioIngresado, correoOriginarioIngresado, departamentoOriginarioIngresado, nombreDestinatarioIngresado, primerApellidoDestinatarioIngresado,
                    segundoApellidoDestinatarioIngresado, correoDestinatarioIngresado, departamentoDestinatarioIngresado);

            if(respuestaValidacion != false) {
                InputStream lectorDatos = getContentResolver().openInputStream(Uri.parse(archivoIngresado));
                ByteArrayOutputStream cajita = new ByteArrayOutputStream();

                int datosLeidos;
                byte[] memoriaTemporal = new byte[4096];
                while((datosLeidos = lectorDatos.read(memoriaTemporal, 0, memoriaTemporal.length)) != -1) {
                    cajita.write(memoriaTemporal, 0, datosLeidos);
                }

                byte[] datosArchivo = cajita.toByteArray();
                lectorDatos.close();

                String archivoBase64 = Base64.encodeToString(datosArchivo, Base64.DEFAULT);

                registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(this);

                RegistroArchivoEntitie registroArchivoEntitie = new RegistroArchivoEntitie(archivoBase64, nombreArchivoIngresado, formatoArchivoIngresado, null, nombreOriginarioIngresado,
                        primerApellidoOriginarioIngresado, segundoApellidoOriginarioIngresado, correoOriginarioIngresado, departamentoOriginarioIngresado, nombreDestinatarioIngresado,
                        primerApellidoDestinatarioIngresado, segundoApellidoDestinatarioIngresado, correoDestinatarioIngresado, departamentoDestinatarioIngresado, "Pendiente");

                Call<RegistroArchivoEntitie> crearNuevoRegistroArchivo = registroArchivoInterface.crearRegistroArchivos(registroArchivoEntitie, tokenUsuario);

                crearNuevoRegistroArchivo.enqueue(new Callback<RegistroArchivoEntitie>() {
                    @Override
                    public void onResponse(Call<RegistroArchivoEntitie> call, Response<RegistroArchivoEntitie> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ArchivoRecienteCrearActivity.this,
                                    "¡El nuevo registro de archivo se creo exitosamente!", Toast.LENGTH_LONG).show();

                            nombreDestinatarioRecorrido = null;
                            primerApellidoDestinatarioRecorrido = null;
                            segundoApellidoDestinatarioRecorrido = null;
                            correoDestinatarioRecorrido = null;
                            departamentoDestinatarioRecorrido = null;

                            archivoIngresado = null;
                            nombreArchivoIngresado = null;
                            formatoArchivoIngresado = null;

                            nombreOriginarioIngresado = null;
                            primerApellidoOriginarioIngresado = null;
                            segundoApellidoOriginarioIngresado = null;
                            correoOriginarioIngresado = null;
                            departamentoOriginarioIngresado = null;

                            nombreDestinatarioIngresado = null;
                            primerApellidoDestinatarioIngresado = null;
                            segundoApellidoDestinatarioIngresado = null;
                            correoDestinatarioIngresado = null;
                            departamentoDestinatarioIngresado = null;

                            archivo = null;
                            nombreArchivo = null;
                            formatoArchivo = null;

                            puntoTipoArchivo = 0;
                            autorizacionArchivoCrear = false;

                            nombreUsuarioTemporal = null;
                            primerApellidoTemporal = null;
                            segundoApellidoTemporal = null;
                            correoTemporal = null;
                            departamentoTemporal = null;

                            VistaRegreso();
                        } else {
                            try {
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = " Se finalizo la sesión de su cuenta.";
                                }

                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible crear el registro del archivo debido a que:" + error.replace('"', ' ') + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                            } catch (Exception error) {
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible crear el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();
                            }
                        }
                    }


                    @Override
                    public void onFailure(Call<RegistroArchivoEntitie> call, Throwable t) {
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible crear el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    }
                });
            }

        } catch (Exception error) {
            archivoRecienteCrearBinding.txtTituloCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.btnSeleccionarUsuarioDestinatarioArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnSeleccionarArchivoArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible crear el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private boolean ValidarRegistroArchivo(String archivo, String nombreArchivo, String formatoArchivo, String nombreOriginario, String primerApellidoOriginario, String segundoApellidoOriginario,
                                           String correoOriginario, String departamentoOriginario, String nombreDestinatario, String primerApellidoDestinatario,
                                           String segundoApellidoDestinatario, String correoDestinatario, String departamentoDestinatario) {
        try {
            if (archivo.isEmpty() && nombreArchivo.isEmpty() && formatoArchivo.isEmpty() && nombreOriginario.isEmpty() && primerApellidoOriginario.isEmpty() && segundoApellidoOriginario.isEmpty() && correoOriginario.isEmpty() &&
                    departamentoOriginario.isEmpty() && nombreDestinatario.isEmpty() && primerApellidoDestinatario.isEmpty() && segundoApellidoDestinatario.isEmpty() && correoDestinatario.isEmpty() &&
                    departamentoDestinatario.isEmpty()) {

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (Objects.equals(nombreOriginario, nombreDestinatario) && Objects.equals(primerApellidoOriginario, primerApellidoDestinatario) &&
                    Objects.equals(segundoApellidoOriginario, segundoApellidoDestinatario) && Objects.equals(correoOriginario, correoDestinatario) &&
                    Objects.equals(departamentoOriginario, departamentoDestinatario)) {

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que selecciono el mismo usuario originario(a)." + "\n\nPor favor, digite otra vez otro usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (archivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el archivo se encuentra vacio." + "\n\nPor favor, digite otra vez su archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (nombreArchivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el nombre del archivo se encuentra vacio." + "\n\nPor favor, digite otra vez el nombre del archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (formatoArchivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el formato del archivo se encuentra vacio." + "\n\nPor favor, digite otra vez el formato del archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (nombreOriginario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el nombre del usuario originario(a) que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el nombre del usuario originario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (primerApellidoOriginario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el primer apellido del usuario originario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el primer apellido del usuario originario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (segundoApellidoOriginario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el segundo apellido del usuario originario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el segundo apellido del usuario originario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (correoOriginario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el correo electrónico del usuario originario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el correo eletrónico del usuario originario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (departamentoOriginario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el departamento del usuario originario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el departamento del usuario originario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (nombreDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el nombre del usuario destinatario(a) que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el nombre del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (primerApellidoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el primer apellido del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el primer apellido del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (segundoApellidoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el segundo apellido del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el segundo apellido del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (correoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el correo electrónico del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el correo electrónico del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (departamentoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo crear el registro del archivo debido a que el departamento del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el departamento del usuario destinatario(a) e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible crear el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }
    }


    private void VistaAñadirDestinario(String nombreDestinarioActual, String primerApellidoDestinarioActual,
                                       String segundoApellidoDestinarioActual, String correoDestinarioActual,
                                       String departamentoDestinarioActual) {

        /* Se resetean estas variables para evitar posibles enredos, y también porque es -
         * una buena práctica. */
        nombreUsuarioTemporal = null;
        primerApellidoTemporal = null;
        segundoApellidoTemporal = null;

        correoTemporal = null;
        departamentoTemporal = null;
        autorizacionArchivoCrear = false;

        /* Aqui lo que se hace es validar si los datos del usuario destinatario son distintos a nulo, si es así, -
         * entonces se guardarian en unas variables globales para tenerlos ahi temporalmente, en dado caso que el -
         * usuario originario quiera añadir un usuario destinatario. */
        if(nombreDestinarioActual != null && primerApellidoDestinarioActual != null && segundoApellidoDestinarioActual != null &&
                correoDestinarioActual != null && departamentoDestinarioActual != null) {

            nombreUsuarioTemporal = gson.toJson(nombreDestinarioActual);
            primerApellidoTemporal = gson.toJson(primerApellidoDestinarioActual);
            segundoApellidoTemporal = gson.toJson(segundoApellidoDestinarioActual);

            correoTemporal = gson.toJson(correoDestinarioActual);
            departamentoTemporal = gson.toJson(departamentoDestinarioActual);
        }

        Intent intentUsuarioEmpleado = new Intent(ArchivoRecienteCrearActivity.this, UsuarioEmpleadoActivity.class);

        intentUsuarioEmpleado.putExtra("Tipo_De_Accion", "Crear_ArchivoReciente");
        intentUsuarioEmpleado.putExtra("Regresar", "ArchivoRecienteCrear");

        startActivity(intentUsuarioEmpleado);

        finish();
    }


    private void VistaAñadirArchivo() {
        try {
            Intent intentSeleccionarArchivo = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intentSeleccionarArchivo.setType("*/*");
            intentSeleccionarArchivo.addCategory(Intent.CATEGORY_OPENABLE);

            launcher.launch(intentSeleccionarArchivo);

        } catch (Exception error) {
            archivoRecienteCrearBinding.txtTituloCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreArchivoCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtFormatoArchivoCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioOriginarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.txtTituloUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtNombreUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido1UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtApellido2UsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtCorreoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.txtDepartamentoUsuarioDestinatarioCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.btnSeleccionarUsuarioDestinatarioArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnSeleccionarArchivoArchivoReciente.setVisibility(GONE);
            archivoRecienteCrearBinding.btnCrearArchivoReciente.setVisibility(GONE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteCrearBinding.imgFotoCrearArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteCrearBinding.txtMensajeCrearArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteCrearActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no es posible crear el registro del archivo debido a que existe un error al cargar el archivo que fue seleccionado o porque sobrepaso el tamaño máximo. Por favor, intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void VistaRegreso() {
        Intent intentAdministracionArchivos = new Intent(ArchivoRecienteCrearActivity.this, AdministracionArchivosActivity.class);

        startActivity(intentAdministracionArchivos);

        finish();
    }
}
