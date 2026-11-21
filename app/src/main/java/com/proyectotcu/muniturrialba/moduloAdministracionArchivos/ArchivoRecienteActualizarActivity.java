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
import com.proyectotcu.muniturrialba.databinding.ActivityArchivoRecienteActualizarBinding;
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

public class ArchivoRecienteActualizarActivity extends AppCompatActivity {

    //Variable para usar el ViewBinding de esta clase.
    private ActivityArchivoRecienteActualizarBinding archivoRecienteActualizarBinding;

    //Variables globales:
    String archivoRecorrido, nombreArchivoRecorrido, formatoArchivoRecorrido, fechaSubidaRecorrido, nombreDestinatarioRecorrido, primerApellidoDestinatarioRecorrido,
            segundoApellidoDestinatarioRecorrido, correoDestinatarioRecorrido, departamentoDestinatarioRecorrido, correoDestinatario_RespaldoRecorrido,
            archivoActualizada, nombreArchivoActualizada, formatoArchivoActualizada, nombreDestinatarioActualizada, primerApellidoDestinatarioActualizada,
            segundoApellidoDestinatarioActualizada, correoDestinatarioActualizada, departamentoDestinatarioActualizada, archivoNuevo;
    Boolean cambioArchivo;
    ActivityResultLauncher<Intent> launcher;

    Gson gson = new Gson();
    Type datosParseado = new TypeToken<String>(){}.getType();

    static String nombreArchivoDestinatarioRespaldo, formatoArchivoDestinatarioRespaldo, fechaSubidaDestinatarioRespaldo,
            nombreDestinatarioRespaldo, primerApellidoDestinatarioRespaldo,segundoApellidoDestinatarioRespaldo,
            correoDestinatarioRespaldo, departamentoDestinatarioRespaldo;
    static String archivoAntiguo, nombreArchivoAntiguo, formatoArchivoAntiguo, nombreUsuarioAntiguo,
            primerApellidoAntiguo,segundoApellidoAntiguo, correoAntiguo, departamentoAntiguo;
    static String archivo, nombreArchivo, formatoArchivo;
    static int puntoTipoArchivo;

    public static Boolean autorizacionArchivoActualizar = false;
    public static Boolean segundaAutorizacion_ArchivoActualizar = false;


    //Interfaz que contiene los métodos de la entidad FAQ.
    RegistroArchivoInterface registroArchivoInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        archivoRecienteActualizarBinding = ActivityArchivoRecienteActualizarBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(archivoRecienteActualizarBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_ArchivoReciente_Actualizar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setVisibility(GONE);
        archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setVisibility(GONE);

        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            SharedPreferences archivoXML = getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            String[] partesToken = tokenGuardado.split("\\.");

            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(cuerpoToken);

            Intent recuperarArchivoReciente = getIntent();

            String campoNombreOriginario = json.optString("nombre");
            String campoPrimerApellidoOriginario = json.optString("primer_Apellido");
            String campoSegundoApellidoOriginario = json.optString("segundo_Apellido");
            String campoCorreoOriginario = json.optString("correo");
            String campoDepartamentoOriginario = json.optString("departamento");

            /* Esta parte es donde se obtiene toda la información del registro de archivo que fue -
             * seleccionado previamente. */
            archivoRecorrido = recuperarArchivoReciente.getStringExtra("archivo_Destinatario");
            nombreArchivoRecorrido = recuperarArchivoReciente.getStringExtra("nombreArchivo_Destinatario");
            formatoArchivoRecorrido = recuperarArchivoReciente.getStringExtra("formatoArchivo_Destinatario");
            fechaSubidaRecorrido = recuperarArchivoReciente.getStringExtra("fechaSubida_Destinatario");

            nombreDestinatarioRecorrido = recuperarArchivoReciente.getStringExtra("nombre_Destinatario");
            primerApellidoDestinatarioRecorrido = recuperarArchivoReciente.getStringExtra("primerApellido_Destinatario");
            segundoApellidoDestinatarioRecorrido = recuperarArchivoReciente.getStringExtra("segundoApellido_Destinatario");

            correoDestinatarioRecorrido = recuperarArchivoReciente.getStringExtra("correoElectronico_Destinatario");
            departamentoDestinatarioRecorrido = recuperarArchivoReciente.getStringExtra("departamento_Destinatario");

            /* Aqui lo que se hace es obtener el correo original que el usuario habia traido previamente, esto para poderlo -
             * enviar al API y que este sepa cual registro es el que se esta actualizando, porque si no, entonces no se podria -
             * ejecutar bien la actualización de dicho registro que se selecciono anteriormente. */
            correoDestinatario_RespaldoRecorrido = recuperarArchivoReciente.getStringExtra("correoElectronico_Destinatario_Respaldo");

            if(correoDestinatario_RespaldoRecorrido != null) {
                nombreArchivoDestinatarioRespaldo = gson.toJson(nombreArchivoRecorrido);
                formatoArchivoDestinatarioRespaldo = gson.toJson(formatoArchivoRecorrido);
                fechaSubidaDestinatarioRespaldo = gson.toJson(fechaSubidaRecorrido);

                nombreDestinatarioRespaldo = gson.toJson(nombreDestinatarioRecorrido);
                primerApellidoDestinatarioRespaldo = gson.toJson(primerApellidoDestinatarioRecorrido);
                segundoApellidoDestinatarioRespaldo = gson.toJson(segundoApellidoDestinatarioRecorrido);

                departamentoDestinatarioRespaldo = gson.toJson(departamentoDestinatarioRecorrido);
                correoDestinatarioRespaldo = gson.toJson(correoDestinatario_RespaldoRecorrido);
            }

            /* Aqui lo que se hace es obtener los datos originales que están relacionados al archivo, lo cual, indicaria -
             * que el usuario originario no selecciono un nuevo archivo respectivamente. */
            if(segundaAutorizacion_ArchivoActualizar != false) {
                archivoRecorrido = gson.fromJson(archivoAntiguo, datosParseado);
                nombreArchivoRecorrido = gson.fromJson(nombreArchivoAntiguo, datosParseado);
                formatoArchivoRecorrido = gson.fromJson(formatoArchivoAntiguo, datosParseado);
            }

            /* Aqui lo que se hace es obtener los datos originales que están relacionados al usuario destinatario, lo cual, -
             * indicaria que el usuario originario no selecciono un nuevo destinatario respectivamente. */
            if(autorizacionArchivoActualizar != false) {
                nombreDestinatarioRecorrido = gson.fromJson(nombreUsuarioAntiguo, datosParseado);
                primerApellidoDestinatarioRecorrido = gson.fromJson(primerApellidoAntiguo, datosParseado);
                segundoApellidoDestinatarioRecorrido = gson.fromJson(segundoApellidoAntiguo, datosParseado);

                correoDestinatarioRecorrido = gson.fromJson(correoAntiguo, datosParseado);
                departamentoDestinatarioRecorrido = gson.fromJson(departamentoAntiguo, datosParseado);
            }

            /* Aqui lo que se hace es obtener el nuevo archivo que selecciono el usuario originario, esto para poder compartirlo -
             * al usuario destinario respectivamente. */
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
                        Cursor cursor = getContentResolver().query(uriArchivoSeleccionado, null, null, null, null);

                        if(cursor != null && cursor.moveToFirst()) {
                            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            nombreArchivo = cursor.getString(index);
                            cursor.close();

                            puntoTipoArchivo = nombreArchivo.lastIndexOf(".");
                            if(puntoTipoArchivo != -1) {
                                formatoArchivo = nombreArchivo.substring(puntoTipoArchivo + 1).toUpperCase();
                            }

                            getContentResolver().takePersistableUriPermission(uriArchivoSeleccionado, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            archivo = gson.toJson(uriArchivoSeleccionado.toString());
                            cambioArchivo = true;

                            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setText(uriArchivoSeleccionado.toString());
                            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setText(nombreArchivo.substring(0, puntoTipoArchivo));
                            archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setText(formatoArchivo);
                        }
                    }
                }
            });

            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setMovementMethod(new ScrollingMovementMethod());
            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setMovementMethod(new ScrollingMovementMethod());
            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setLongClickable(false);
            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setLongClickable(false);

            /* Aqui lo que se hace es obtener el archivo original que el usuario habia seleccionado previamente, tanto su nombre como su -
             * tipo de formato. Esto se debe a que cuando el usuario, indepedientemente seleccionaba o no un usuario destinatario, la -
             * información de ese archivo desaparecia. Por lo que se coloco esta validación para recuperar dicha información respectivamente.
             *
             * Ahora, si resulta que es nulo, quiere decir que el usuario no ha seleccionado todavia ningún archivo, lo que quiere decir que -
             * recien esta entrando en la clase de ArchivoRecienteActualizarActivity, por lo que se mostraria la información de ese registro -
             * que el usuario originario seleccciono para actualizarlo. */
            if (archivo != null) {
                Uri uriArchivoSeleccionado = Uri.parse(gson.fromJson(archivo, datosParseado));
                cambioArchivo = true;

                archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setText(uriArchivoSeleccionado.toString());
                archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setText(nombreArchivo.substring(0, puntoTipoArchivo));
                archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setText(formatoArchivo);

            } else {
                cambioArchivo = false;

                //Aqui lo que se hace es colocar la información del usuario destinatario en sus respectivos campos.
                archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setText(archivoRecorrido);
                archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setText(nombreArchivoRecorrido);
                archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setText(formatoArchivoRecorrido);
            }

            //Aqui lo que se hace es colocar la información del usuario destinatario en sus respectivos campos.
            archivoRecienteActualizarBinding.txtNombreUsuarioDestinatarioActualizarArchivoReciente.setText(nombreDestinatarioRecorrido);
            archivoRecienteActualizarBinding.txtApellido1UsuarioDestinatarioActualizarArchivoReciente.setText(primerApellidoDestinatarioRecorrido);
            archivoRecienteActualizarBinding.txtApellido2UsuarioDestinatarioActualizarArchivoReciente.setText(segundoApellidoDestinatarioRecorrido);
            archivoRecienteActualizarBinding.txtCorreoUsuarioDestinatarioActualizarArchivoReciente.setText(correoDestinatarioRecorrido);
            archivoRecienteActualizarBinding.txtDepartamentoUsuarioDestinatarioActualizarArchivoReciente.setText(departamentoDestinatarioRecorrido);

            archivoRecienteActualizarBinding.btnSeleccionarUsuarioDestinatarioActualizarArchivoReciente.setOnClickListener(v -> VistaAñadirDestinario(archivoRecorrido, nombreArchivoRecorrido, formatoArchivoRecorrido,
                    nombreDestinatarioRecorrido, primerApellidoDestinatarioRecorrido, segundoApellidoDestinatarioRecorrido, correoDestinatarioRecorrido, departamentoDestinatarioRecorrido));

            archivoRecienteActualizarBinding.btnSeleccionarArchivoActualizarArchivoReciente.setOnClickListener(v -> VistaActualizarArchivo());
            archivoRecienteActualizarBinding.btnActualizarArchivoReciente.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_actualizar);
                construirAlerta.setMessage("¿Esta completamente seguro(a) de actualizar este registro de archivo respectivamente?")
                        .setTitle("Actualizar Registro Archivo.");

                construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActualizarRegistroArchivo(tokenGuardado, campoNombreOriginario, campoPrimerApellidoOriginario,
                                campoSegundoApellidoOriginario, campoCorreoOriginario, campoDepartamentoOriginario);
                    }
                });

                construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ArchivoRecienteActualizarActivity.this, "¡No se continuo con la actualización!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            });

        } catch (Exception error) {
            archivoRecienteActualizarBinding.txtTituloActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtTituloUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido1UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido2UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtCorreoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtDepartamentoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.btnSeleccionarUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnSeleccionarArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
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
                autorizacionArchivoActualizar = false;
                segundaAutorizacion_ArchivoActualizar = false;

                archivoAntiguo = null;
                nombreArchivoAntiguo = null;
                formatoArchivoAntiguo = null;

                nombreUsuarioAntiguo = null;
                primerApellidoAntiguo = null;
                segundoApellidoAntiguo = null;

                correoAntiguo = null;
                departamentoAntiguo = null;

                nombreArchivoDestinatarioRespaldo = null;
                formatoArchivoDestinatarioRespaldo = null;
                fechaSubidaDestinatarioRespaldo = null;

                nombreDestinatarioRespaldo = null;
                primerApellidoDestinatarioRespaldo = null;
                segundoApellidoDestinatarioRespaldo = null;

                correoDestinatarioRespaldo = null;
                departamentoDestinatarioRespaldo = null;

                Intent intentRegreso = new Intent(ArchivoRecienteActualizarActivity.this, AdministracionArchivosActivity.class);

                startActivity(intentRegreso);

                finish();
            }
        });
    }


    private void ActualizarRegistroArchivo(String tokenUsuario, String nombreOriginario, String primerApellidoOriginario,
                                           String segundoApellidoOriginario, String correoOriginario, String departamentoOriginario) {
        try {
            archivoActualizada = archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.getText().toString().trim();
            nombreArchivoActualizada = archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.getText().toString().trim();
            formatoArchivoActualizada = archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.getText().toString().trim();

            nombreDestinatarioActualizada = archivoRecienteActualizarBinding.txtNombreUsuarioDestinatarioActualizarArchivoReciente.getText().toString().trim();
            primerApellidoDestinatarioActualizada = archivoRecienteActualizarBinding.txtApellido1UsuarioDestinatarioActualizarArchivoReciente.getText().toString().trim();
            segundoApellidoDestinatarioActualizada = archivoRecienteActualizarBinding.txtApellido2UsuarioDestinatarioActualizarArchivoReciente.getText().toString().trim();

            correoDestinatarioActualizada = archivoRecienteActualizarBinding.txtCorreoUsuarioDestinatarioActualizarArchivoReciente.getText().toString().trim();
            departamentoDestinatarioActualizada = archivoRecienteActualizarBinding.txtDepartamentoUsuarioDestinatarioActualizarArchivoReciente.getText().toString().trim();

            String fechaSubidaDestinatarioAntiguo = gson.fromJson(fechaSubidaDestinatarioRespaldo, datosParseado);
            String correoDestinatarioAntiguo = gson.fromJson(correoDestinatarioRespaldo, datosParseado);

            boolean respuestaValidacion = ValidarRegistroArchivo(cambioArchivo, archivoActualizada, nombreArchivoActualizada, formatoArchivoActualizada, nombreOriginario, primerApellidoOriginario,
                    segundoApellidoOriginario, correoOriginario, departamentoOriginario, nombreDestinatarioActualizada, primerApellidoDestinatarioActualizada, segundoApellidoDestinatarioActualizada,
                    correoDestinatarioActualizada, departamentoDestinatarioActualizada);

            if(respuestaValidacion != false) {
                if(cambioArchivo != false) {
                    InputStream lectorDatos = getContentResolver().openInputStream(Uri.parse(archivoActualizada));
                    ByteArrayOutputStream cajita = new ByteArrayOutputStream();

                    int datosLeidos;
                    byte[] memoriaTemporal = new byte[4096];
                    while((datosLeidos = lectorDatos.read(memoriaTemporal, 0, memoriaTemporal.length)) != -1) {
                        cajita.write(memoriaTemporal, 0, datosLeidos);
                    }

                    byte[] datosArchivo = cajita.toByteArray();
                    lectorDatos.close();

                    archivoNuevo = Base64.encodeToString(datosArchivo, Base64.DEFAULT);

                } else {
                    archivoNuevo = archivoActualizada;
                }

                registroArchivoInterface = ConexionAPI.Conexion_API_Archivo(this);

                RegistroArchivoEntitie registroArchivoEntitie = new RegistroArchivoEntitie(archivoNuevo, nombreArchivoActualizada, formatoArchivoActualizada, fechaSubidaDestinatarioAntiguo,
                        nombreOriginario, primerApellidoOriginario, segundoApellidoOriginario, correoOriginario, departamentoOriginario, nombreDestinatarioActualizada,
                        primerApellidoDestinatarioActualizada, segundoApellidoDestinatarioActualizada, correoDestinatarioActualizada, departamentoDestinatarioActualizada,
                        null);

                Call<Boolean> actualizarRegistroArchivo = registroArchivoInterface.actualizarRegistroArchivo(registroArchivoEntitie, correoDestinatarioAntiguo, tokenUsuario);

                actualizarRegistroArchivo.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ArchivoRecienteActualizarActivity.this,
                                    "¡El registro del archivo se actualizo exitosamente!", Toast.LENGTH_SHORT).show();

                            archivoRecorrido = null;
                            nombreArchivoRecorrido = null;
                            formatoArchivoRecorrido = null;
                            fechaSubidaRecorrido = null;

                            nombreDestinatarioRecorrido = null;
                            primerApellidoDestinatarioRecorrido = null;
                            segundoApellidoDestinatarioRecorrido = null;
                            correoDestinatarioRecorrido = null;
                            departamentoDestinatarioRecorrido = null;

                            archivoActualizada = null;
                            nombreArchivoActualizada = null;
                            formatoArchivoActualizada = null;

                            nombreDestinatarioActualizada = null;
                            primerApellidoDestinatarioActualizada = null;
                            segundoApellidoDestinatarioActualizada = null;
                            correoDestinatarioActualizada = null;
                            departamentoDestinatarioActualizada = null;

                            archivo = null;
                            nombreArchivo = null;
                            formatoArchivo = null;

                            puntoTipoArchivo = 0;
                            autorizacionArchivoActualizar = false;
                            segundaAutorizacion_ArchivoActualizar = false;

                            archivoAntiguo = null;
                            nombreArchivoAntiguo = null;
                            formatoArchivoAntiguo = null;

                            nombreUsuarioAntiguo = null;
                            primerApellidoAntiguo = null;
                            segundoApellidoAntiguo = null;

                            correoAntiguo = null;
                            departamentoAntiguo = null;

                            nombreArchivoDestinatarioRespaldo = null;
                            formatoArchivoDestinatarioRespaldo = null;
                            fechaSubidaDestinatarioRespaldo = null;

                            nombreDestinatarioRespaldo = null;
                            primerApellidoDestinatarioRespaldo = null;
                            segundoApellidoDestinatarioRespaldo = null;

                            correoDestinatarioRespaldo = null;
                            departamentoDestinatarioRespaldo = null;

                            VistaRegreso();
                        } else {
                            try {
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = " Se finalizo la sesión de su cuenta.";
                                }

                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible actualizar el registro del archivo debido a que:" + error.replace('"', ' ') + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                            } catch (Exception error) {
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
            archivoRecienteActualizarBinding.txtTituloActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtTituloUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido1UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido2UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtCorreoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtDepartamentoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.btnSeleccionarUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnSeleccionarArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private boolean ValidarRegistroArchivo(boolean archivoCambio, String archivo, String nombreArchivo, String formatoArchivo, String nombreOriginario, String primerApellidoOriginario,
                                           String segundoApellidoOriginario, String correoOriginario, String departamentoOriginario, String nombreDestinatario,
                                           String primerApellidoDestinatario, String segundoApellidoDestinatario, String correoDestinatario, String departamentoDestinatario) {
        try {
            String nombreArchivo_DestinatarioAntiguo = gson.fromJson(nombreArchivoDestinatarioRespaldo, datosParseado);
            String formatoArchivo_DestinatarioAntiguo = gson.fromJson(formatoArchivoDestinatarioRespaldo, datosParseado);

            String nombre_DestinatarioAntiguo = gson.fromJson(nombreDestinatarioRespaldo, datosParseado);
            String primerApellido_DestinatarioAntiguo = gson.fromJson(primerApellidoDestinatarioRespaldo, datosParseado);
            String segundoApellido_DestinatarioAntiguo = gson.fromJson(segundoApellidoDestinatarioRespaldo, datosParseado);

            String departamento_DestinatarioAntiguo = gson.fromJson(departamentoDestinatarioRespaldo, datosParseado);
            String correo_DestinatarioAntiguo = gson.fromJson(correoDestinatarioRespaldo, datosParseado);

            if (archivoCambio != true && Objects.equals(nombreArchivo, nombreArchivo_DestinatarioAntiguo) && Objects.equals(formatoArchivo, formatoArchivo_DestinatarioAntiguo) &&
                    Objects.equals(nombreDestinatario, nombre_DestinatarioAntiguo) && Objects.equals(primerApellidoDestinatario, primerApellido_DestinatarioAntiguo) && Objects.equals(segundoApellidoDestinatario, segundoApellido_DestinatarioAntiguo) &&
                    Objects.equals(correoDestinatario, correo_DestinatarioAntiguo) && Objects.equals(departamentoDestinatario, departamento_DestinatarioAntiguo)) {

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que están los mismos datos." + "\n\nPor favor, ingrese los nuevos cambios que desea e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (archivo.isEmpty() && nombreArchivo.isEmpty() && formatoArchivo.isEmpty() && nombreDestinatario.isEmpty() && primerApellidoDestinatario.isEmpty() &&
                    segundoApellidoDestinatario.isEmpty() && correoDestinatario.isEmpty() && departamentoDestinatario.isEmpty()) {

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que todos los espacios están vacios." + "\n\nPor favor, ingrese todos los campos requeridos e intentelo de nuevo.")
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

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que selecciono el mismo usuario originario(a)." + "\n\nPor favor, digite otra vez otro usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (archivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el archivo que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (nombreArchivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el nombre del archivo que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el nombre del archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (formatoArchivo.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el formato del archivo que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el formato del archivo e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (nombreDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el nombre del usuario destinatario(a) que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el nombre del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (primerApellidoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el primer apellido del usuario destinatario(a) que se necesita se encuentra vacio." + "\n\nPor favor, digite otra vez el primer apellido del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (segundoApellidoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el segundo apellido del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el segundo apellido del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (correoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el correo electrónico del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el correo electrónico del usuario destinatario(a) e intentelo de nuevo.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});


                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
                return false;
            }


            if (departamentoDestinatario.isEmpty()) {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
                construirAlerta.setIcon(R.drawable.icono_advertencia);
                construirAlerta.setMessage("Pero no se pudo actualizar el registro del archivo debido a que el departamento del usuario destinatario(a) se encuentra vacio." + "\n\nPor favor, digite otra vez el departamento del usuario destinatario(a) e intentelo de nuevo.")
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
            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});


            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
            return false;
        }
    }


    private void VistaAñadirDestinario(String archivoActual, String nombreArchivoActual, String formatoArchivoActual, String nombreActual,
                                       String primerApellidoActual, String segundoApellidoActual, String correoActual, String departamentoActual) {

        /* Se resetean estas variables para evitar posibles enredos, y también porque es -
         * una buena práctica. */
        archivoAntiguo = null;
        nombreArchivoAntiguo = null;
        formatoArchivoAntiguo = null;

        nombreUsuarioAntiguo = null;
        primerApellidoAntiguo = null;
        segundoApellidoAntiguo = null;

        correoAntiguo = null;
        departamentoAntiguo = null;

        autorizacionArchivoActualizar = false;
        segundaAutorizacion_ArchivoActualizar = false;

        /* Aqui lo que se hace con estas validaciones es ver si los datos del usuario destinatario y su archivo son -
         * distintos a nulo, si es así, entonces se guardarian en unas variables globales para tenerlos ahi temporalmente, -
         * en dado caso que el usuario originario quiera añadir un usuario destinatario. */
        if(archivoActual != null && nombreArchivoActual != null && formatoArchivoActual != null) {
            archivoAntiguo = gson.toJson(archivoActual);
            nombreArchivoAntiguo = gson.toJson(nombreArchivoActual);
            formatoArchivoAntiguo = gson.toJson(formatoArchivoActual);
        }

        if(nombreActual != null && primerApellidoActual != null && segundoApellidoActual != null &&
                correoActual != null && departamentoActual != null) {

            nombreUsuarioAntiguo = gson.toJson(nombreActual);
            primerApellidoAntiguo = gson.toJson(primerApellidoActual);
            segundoApellidoAntiguo = gson.toJson(segundoApellidoActual);

            correoAntiguo = gson.toJson(correoActual);
            departamentoAntiguo = gson.toJson(departamentoActual);
        }

        Intent intentUsuarioEmpleado = new Intent(ArchivoRecienteActualizarActivity.this, UsuarioEmpleadoActivity.class);

        intentUsuarioEmpleado.putExtra("Tipo_De_Accion", "Actualizar_ArchivoReciente");
        intentUsuarioEmpleado.putExtra("Regresar", "ArchivoRecienteActualizar");

        startActivity(intentUsuarioEmpleado);

        finish();
    }


    private void VistaActualizarArchivo() {
        try {
            Intent intentSeleccionarArchivo = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intentSeleccionarArchivo.setType("*/*");
            intentSeleccionarArchivo.addCategory(Intent.CATEGORY_OPENABLE);

            launcher.launch(intentSeleccionarArchivo);

        } catch (Exception error) {
            archivoRecienteActualizarBinding.txtTituloActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtFormatoArchivoActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtTituloUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtNombreUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido1UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtApellido2UsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.txtCorreoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.txtDepartamentoUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.btnSeleccionarUsuarioDestinatarioActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnSeleccionarArchivoActualizarArchivoReciente.setVisibility(GONE);
            archivoRecienteActualizarBinding.btnActualizarArchivoReciente.setVisibility(GONE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setVisibility(VISIBLE);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setVisibility(VISIBLE);

            archivoRecienteActualizarBinding.imgFotoActualizarArchivoReciente.setImageResource(R.drawable.icono_contenido_no_disponible);
            archivoRecienteActualizarBinding.txtMensajeActualizarArchivoReciente.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(ArchivoRecienteActualizarActivity.this);
            construirAlerta.setIcon(R.drawable.icono_advertencia);
            construirAlerta.setMessage("Pero no es posible actualizar el registro del archivo debido a que existe un error al cargar el archivo que fue seleccionado o porque sobrepaso el tamaño máximo. Por favor, intentelo de nuevo.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }


    private void VistaRegreso() {
        Intent intentArchivoReciente = new Intent(ArchivoRecienteActualizarActivity.this, AdministracionArchivosActivity.class);

        startActivity(intentArchivoReciente);

        finish();
    }
}
