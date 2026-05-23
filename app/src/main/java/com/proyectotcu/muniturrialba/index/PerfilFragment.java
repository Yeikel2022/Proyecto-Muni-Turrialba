package com.proyectotcu.muniturrialba.index;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts.*;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.proyectotcu.muniturrialba.MainActivity;
import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.UsuarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.UsuarioInterface;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    //Variables globales para esta clase.
    ImageButton botonEditarPerfil;
    ImageView fotoPerfil;
    Button botonPermisos, botonCerrar;
    TextView txtMensaje, txtNombre, txtApellidos, txtCorreo;
    String imagenBase64;

    //Variable global para detectar imagenes desde la galeria:
    ActivityResultLauncher<PickVisualMediaRequest> SelectorImagen;

    //Interfaz que contiene los métodos de la entidad usuario.
    UsuarioInterface usuarioInterface;


    /* A diferencia de las actividades, al ser esto un fragmento, no se -
     * puede usar el ViewBinding para acceder a los recursos de una forma -
     * más sencilla, por lo que se hizo de la manera manual respectivamente. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /* Lo primero seria obtener el fragmento que esta relacionado -
         * al perfil y también los botones que estan relacionados a los -
         * permisos, la foto de perfil y la capacidad de cerrar sesión -
         * dentro de la aplicación móvil respectivamente.
         *
         * Además de los textos que contienen el nombre, los apellidos, el correo -
         * y el mensaje de error o de negación (se refiere a la autorización de -
         * permisos). Asimismo, también se ocultarian todos los botones y textos -
         * para evitar cualquier situación de que alguien logre saltarse la validación -
         * de los roles. */
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        fotoPerfil = view.findViewById(R.id.img_fotoPerfil);
        botonEditarPerfil = view.findViewById(R.id.btn_editarPerfil);
        botonCerrar = view.findViewById(R.id.btn_CerrarSesion);
        botonPermisos = view.findViewById(R.id.btn_Permisos);

        txtNombre = view.findViewById(R.id.txt_NombreUsuario);
        txtApellidos = view.findViewById(R.id.txt_ApellidosUsuario);
        txtCorreo = view.findViewById(R.id.txt_CorreoUsuario);
        txtMensaje = view.findViewById(R.id.txt_MensajePerfil);

        txtNombre.setVisibility(View.INVISIBLE);
        txtApellidos.setVisibility(View.INVISIBLE);
        txtCorreo.setVisibility(View.INVISIBLE);
        txtMensaje.setVisibility(View.GONE);

        fotoPerfil.setVisibility(View.INVISIBLE);
        botonEditarPerfil.setVisibility(View.INVISIBLE);
        botonCerrar.setVisibility(View.INVISIBLE);
        botonPermisos.setVisibility(View.INVISIBLE);


        try {
            /* Luego, lo segundo seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getActivity().getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Después, lo tercero seria obtener un texto llamado: "JWT_token", el cual esta dentro -
             * del archivo que tiene como nombre: "Archivo_Autenticacion". Esto porque en dicho texto -
             * esta guardado el token que el usuario recibio por parte del API.
             *
             * Ahora, si resulta que en dicho texto no hay nada, entonces mandaria como respuesta un -
             * nulo respectivamente. */
            String tokenGuardado = archivoXML.getString("JWT_token", null);

            /* Aqui lo que se esta haciendo es crear una lista con todas las partes -
             * separadas que contiene el token recibido por parte del API. Esto por -
             * medio del comando: "split("\\.")", el cual nos ayuda a separar dicho -
             * token por los puntos que contiene, de ahi el \\., ya que es una expre-
             * sión regular que nos permite realizar esa acción respectivamente. */
            String[] partesToken = tokenGuardado.split("\\.");

            /* Aqui lo que se esta haciendo es decodificar la parte que contiene los -
             * claims del token, esto porque se necesita saber principalmente el rol -
             * y los permisos de dicho usuario. */
            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            /* Aqui lo que se esta haciendo crear un objeto de tipo JSON -
             * para poder almacenar correctamente los datos que fueron gu -
             * ardados en la variable: "cuerpoToken". Esto porque el token -
             * en si, esta en un formato JSON respectivamente. */
            JSONObject json = new JSONObject(cuerpoToken);

            /* Después de crear ese objeto, entonces lo que se haria es -
             * guardar el correo, el rol y los permisos del usuario que -
             * inicio sesión en el campo respectivo, de modo que así se -
             * puedan utilizar para validar cuales opciones puede acceder -
             * y también para verificar si dicho usuario tiene la autorización -
             * necesaria respectivamente. */
            String campoCorreo = json.optString("correo");
            Integer campoRol = Integer.parseInt(json.optString("rol"));
            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));


            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador", -
             * "Administrador" o "Empleado". Y si no, entonces no lo dejaria acceder a las -
             * opciones del perfil. */
            if (campoRol == 1 || campoRol == 2 || campoRol == 3) {
                /* Una vez hecho eso, lo que seguiria seria validar los permisos del usuario -
                 * que ha iniciado sesión. Y, si luego de eso, la respuesta que trae es un 4, -
                 * entonces quiere decir que si esta autorizado para acceder a las opciones del -
                 * perfil. */
                Integer respuestaPermisos = ValidarPermisosUsuario(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar);
                if (respuestaPermisos == 4) {
                    txtNombre.setVisibility(View.VISIBLE);
                    txtApellidos.setVisibility(View.VISIBLE);
                    txtCorreo.setVisibility(View.VISIBLE);

                    fotoPerfil.setVisibility(View.VISIBLE);
                    botonEditarPerfil.setVisibility(View.VISIBLE);
                    botonCerrar.setVisibility(View.VISIBLE);
                    botonPermisos.setVisibility(View.VISIBLE);

                    botonCerrar.setOnClickListener(v -> {
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());

                        construirAlerta.setIcon(R.drawable.rounded_cancel_presentation_24);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de cerrar sesión?")
                                .setTitle("Cerrar Sesión.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VistaCerrarSesion();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se cerro la sesión!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });

                    botonPermisos.setOnClickListener(v -> VistaPermisos(campoRol, campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar));
                    botonEditarPerfil.setOnClickListener(v -> SelectorImagen.launch(new PickVisualMediaRequest.
                            Builder().setMediaType(PickVisualMedia.ImageOnly.INSTANCE).build()));
                    /* NOTA: Aqui lo que se hace en el botonFotoPerfil, es usar el photo picker -
                     * el cual es una forma más optimizada y nueva que ofrece Google para que el -
                     * usuario pueda seleccionar una imagen en su galeria respectivamente.
                     *
                     * Además de que se puede asegurar que no solo se acceda a las imagenes (de ahi -
                     * el porque se puso: "PickVisualMedia.ImageOnly.INSTANCE"), si no que también -
                     * sirve para evitar solicitar permisos que pueden ser innecesarios. */


                    /* Ahora, esto también se relaciona con el botonFotoPerfil, esto debido a que cuando -
                     * se abre la galeria para seleccionar la imagen, este comando ayuda a registrar la -
                     * acción que va a hacer el usuario, lo cual, si es que pasa el escenario de que -
                     * selecciono una imagen, entonces se realizaria el proceso necesario para hacer -
                     * el cambio de su foto perfil.
                     *
                     * NOTA: El uri, es básicamente la ruta que indica donde esta la imagen, de modo -
                     * que con ese dato, se podria acceder a ella. */
                     SelectorImagen = registerForActivityResult(new PickVisualMedia(), uri -> {
                         if (uri != null) {
                             /* Este comando seria para ver si se pudo seleccionar la imagen, entonces -
                              * sirve de mucho para cuestiones de depuración:
                              * Log.d("Imagen", "Si se pudo seleccionar la imagen, la cual es la siguiente: " + uri); */
                             CambiarFotoPerfil(campoCorreo, tokenGuardado, uri);
                         }
                     });

                     MostrarDatosPerfil(campoCorreo, tokenGuardado);
                }

                /* Asimismo, también se hace otra validación, ya que, si la respuesta que trae -
                 * es un 3, quiere decir que ese usuario que inicio sesión no contiene el permiso -
                 * de actualizar, por lo que no esta autorizado para hacer algun cambio en la foto -
                 * de perfil respectivamente. */
                if (respuestaPermisos == 3) {
                    Toast.makeText(getActivity(), "¡No tienes la autorización necesaria para actualizar " + "la información de la cuenta!", Toast.LENGTH_LONG).show();

                    txtNombre.setVisibility(View.VISIBLE);
                    txtApellidos.setVisibility(View.VISIBLE);
                    txtCorreo.setVisibility(View.VISIBLE);

                    fotoPerfil.setVisibility(View.VISIBLE);
                    botonCerrar.setVisibility(View.VISIBLE);
                    botonPermisos.setVisibility(View.VISIBLE);

                    botonCerrar.setOnClickListener(v -> {
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());

                        construirAlerta.setIcon(R.drawable.rounded_cancel_presentation_24);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de cerrar sesión?")
                                .setTitle("Cerrar Sesión.");

                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                VistaCerrarSesion();
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se cerro la sesión!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });
                    botonPermisos.setOnClickListener(v -> VistaPermisos(campoRol, campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar));

                    MostrarDatosPerfil(campoCorreo, tokenGuardado);
                }

            }
        } catch (Exception error) {
            /* Sirve para imprimir el mensaje que se recibio anteriormente.
             *
             * NOTA: Este comando es para ver que fallo, el usuario no lo debe ver:
             * Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show(); */

            /* Aqui lo que se hace es ocultar todos las opciones del perfil, y luego se enviaria -
             * un mensaje de error, esto por si llegara a suceder algún imprevisto o algún error -
             * en el API (o la base de datos) respectivamente. */
            txtNombre.setVisibility(View.GONE);
            txtApellidos.setVisibility(View.GONE);
            txtCorreo.setVisibility(View.GONE);

            /* Esto es por si da un nulo el boton llamado: botonEditarPerfil, -
             * ya que es el que va a ejecutar lo de la selección de imagen.
             * Si no pasa eso, entonces simplemente seguira como los demás. */
            Boolean validacion = botonEditarPerfil == null;
            if (validacion != true) {
                botonEditarPerfil.setVisibility(View.GONE);
            }

            fotoPerfil.setVisibility(View.GONE);
            botonCerrar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            txtMensaje.setVisibility(View.VISIBLE);
            txtMensaje.setText(getString(R.string.ErrorFragment));
            /* NOTA: El "getString(R.string.ErrorFragment)", lo que hace es traer un -
             * mensaje que se coloco en: "strings.xml" para que el textview: "txtMensaje"-
             * pueda colocarlo en la pantalla del fragmento (osea en el fragment_perfil.xml),-
             * esto porque es una forma dinamica de hacerlo. */

            Toast.makeText(getActivity(), "¡Lo sentimos, pero parece que hubo un problema técnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" + "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
        }

        return view;
    }


    /* Metodo que sirve para validar los permisos del usuario -
     * que ha iniciado sesión, esto para poder continuar con -
     * la navegación dentro de la aplicación móvil respectivamente. */
    private Integer ValidarPermisosUsuario(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        /* Aqui valida si todos los permisos de ese usuario son falsos -
         * (dando a entender que no esta autorizado). Y si entra, entonces -
         * se ocultaria todos las opciones del perfil y se enviaria un mensaje -
         * mencionando que no tiene la autorización suficiente para poder continuar. */
        if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
            txtNombre.setVisibility(View.GONE);
            txtApellidos.setVisibility(View.GONE);
            txtCorreo.setVisibility(View.GONE);

            fotoPerfil.setVisibility(View.GONE);
            botonEditarPerfil.setVisibility(View.GONE);
            botonCerrar.setVisibility(View.GONE);
            botonPermisos.setVisibility(View.GONE);

            txtMensaje.setVisibility(View.VISIBLE);
            txtMensaje.setText(getString(R.string.AutorizacionDenegada));
            /* NOTA: El "getString(R.string.AutorizacionDenegada)", lo que hace es -
             * traer un mensaje que se coloco en: "strings.xml" para que el textview: -
             * "txtMensaje" pueda colocarlo en la pantalla del fragmento (osea en el -
             * fragment_perfil.xml), esto porque es una forma dinamica de hacerlo. */

            Toast.makeText(getActivity(), "¡No tienes la autorización necesaria para visualizar la información de la cuenta!", Toast.LENGTH_LONG).show();
            return 0;
        }

        /* Aqui valida si el permiso de leer de ese usuario es falso -
         * (dando a entender que no esta autorizado para visualizar los -
         * datos (en este caso los permisos de la aplicación). Y si entra, -
         * entonces se enviaria un mensaje mencionando que no esta autorizado -
         * y luego un 1 para que el metodo: "VistaPermisos" pueda saber que no -
         * esta autorizado. */
        if(Leer == false) {
            Toast.makeText(getActivity(), "¡No tienes la autorización necesaria para visualizar los permisos de la aplicación!", Toast.LENGTH_LONG).show();
            return 1;
        }

        /* Aqui valida si el permiso de actualizar de ese usuario es falso -
         * (dando a entender que no esta autorizado para actualizar los datos, -
         * en este caso la foto de perfil). Y si entra, entonces se ocultaria -
         * la opcion para editar la foto de perfil respectivamente. */
        if(Actualizar == false) {
            botonEditarPerfil.setVisibility(View.GONE);
            return 3;
        }

        //El 4 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 4;
    }


    /* Metodo que sirve para mostrar los datos del usuario, esto dentro -
     * de la aplicación móvil respectivamente. */
    private void MostrarDatosPerfil(String correoUsuario, String tokenUsuario) {
        /* Aquí se llama la conexión del API. Además de indicarle -
         * también que en este fragmento (y clase) se esta pidiendo -
         * esa conexión del API como tal, de ahí el porque se esta -
         * pasando lo que tiene la variable: "nombreActividad".
         *
         * NOTA: Se uso un getActivity() en la variable: "nombreActividad" -
         * porque es la manera en la cual se puede saber cual es la actividad -
         * que esta haciendo uso de los fragmentos (que este caso es el menu -
         * principal), haciendo que la aplicacion puede saber donde se esta -
         * haciendo el llamado. Osea una instancia de la clase. */
        Activity nombreActividad = getActivity();
        usuarioInterface = ConexionAPI.Conexion_API(nombreActividad);

        /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
         * así poder enviar el correo y el token para que el API lo pueda recibir y obtener -
         * los datos del usuario. Esto por medio de la interfaz respectiva. */
        Call<UsuarioEntitie> usuario = usuarioInterface.obtenerUsuario(correoUsuario, tokenUsuario);

        /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
         * el metodo GET.
         *
         * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
         * podemos saber la posible respuesta que pudo brindar el API como tal. */
        usuario.enqueue(new Callback<UsuarioEntitie>() {

            /* Aqui es para saber si hubo una respuesta por parte del API. */
            @Override
            public void onResponse(Call<UsuarioEntitie> call, Response<UsuarioEntitie> response) {
                    if (response.isSuccessful()) {
                        /* Aqui lo que se está haciendo es colocar el nombre, los -
                         * apellidos y el correo electrónico del usuario en la pantalla -
                         * del perfil respectivamente. */
                        String Nombre = response.body().getNombre().toString().trim();
                        String Apellidos = response.body().getApellido_1().toString().trim() + " " + response.body().getApellido_2().toString().trim();
                        String Correo = response.body().getCorreo_Electronico().toString().trim();

                        txtNombre.setText(Nombre);
                        txtApellidos.setText(Apellidos);
                        txtCorreo.setText(Correo);

                        /* Aqui lo que se está haciendo es colocar la imagen del perfil, pero -
                         * siempre y cuando el usuario haya colocado una imagen, si no entonces -
                         * en su lugar se pondra un logo de perfil por defecto.
                         *
                         * Esto es asi porque en el registro no se pide una imagen como tal, de -
                         * ahi que se pone aqui un try catch para detectar si esta nulo la imagen -
                         * para luego hacer esa acción. */
                        try {
                            String FotoPerfil = response.body().getImagen_Perfil().toString().trim();
                            byte[] lista = Base64.decode(FotoPerfil, Base64.DEFAULT);
                            Bitmap mapeo = BitmapFactory.decodeByteArray(lista, 0, lista.length);

                            fotoPerfil.setImageBitmap(mapeo);
                        } catch (Exception error) {
                            fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);
                        }
                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Aqui lo que se hace es colocar el logo de imagen por defecto y ocultar -
                             * la opcion de editar el perfil. Esto por temas de buenas prácticas. */
                            fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);
                            botonEditarPerfil.setVisibility(View.GONE);


                            /* Esto es para imprimir los mensajes de error. */
                            Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "Pero en este momento no es posible ver la información debido a que " + error, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */

                            /* Aqui lo que se hace es ocultar la opcion para editar el perfil, -
                             * y en la foto de perfil se coloca el logo de imagen por defecto. -
                             * Esto por temas de buenas prácticas. */
                            fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);
                            botonEditarPerfil.setVisibility(View.GONE);

                            Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "Pero no es posible visualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /* Aqui es para saber si hubo un fallo en dar la respuesta -
                 * por parte del API. */
                @Override
                public void onFailure(Call<UsuarioEntitie> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el usuario no lo debe -
                     * ver:
                     * Toast.makeText(getActivity(), t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */

                    /* Aqui lo que se hace es ocultar la opcion para editar el perfil, -
                     * y en la foto de perfil se coloca el logo de imagen por defecto. -
                     * Esto por temas de buenas prácticas. */
                    fotoPerfil.setImageResource(R.drawable.rounded_account_circle_24);
                    botonEditarPerfil.setVisibility(View.GONE);

                    Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "Pero no es posible visualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                }
            });
    }


    /* Metodo que sirve para cambiar la foto de perfil del usuario, -
     * dentro de la aplicación móvil respectivamente. */
    private void CambiarFotoPerfil(String correoUsuario, String token, Uri uri) {
        try {
            /* Lo primero que hay que hacer es crear un InputStream y un ByteArrayOutputStream, -
             * esto debido a que con el InputStream nos va a permitir leer los bytes (esto porque -
             * el InputStream necesita un flujo de entrada de bytes), de ahí con ayuda del -
             * getContentResolver nos va a permitir acceder al contenido del uri (que seria la ruta -
             * de la imagen).
             *
             * Ahora, con el ByteArrayOutputStream lo que nos servira es guardar los datos de esa uri -
             * de una forma automatica (o dinamica), y luego de eso, se podria obtener dichos datos por -
             * medio de un arreglo de bytes. Básicamente, esto es una cajita que nos guardara esos datos. */
            InputStream lectorDatos = getActivity().getContentResolver().openInputStream(uri); //Genera error para el catch.
            ByteArrayOutputStream cajita = new ByteArrayOutputStream();

            /* Luego, lo segundo seria ahora obtener esos datos del uri por medio del while, -
             * el cual con ayuda de la variable: "lectorDatos", lo que haria es que desde la -
             * posición 0, se lean los datos del uri entre 16 KB, de modo que una vez leidos -
             * se guarde en la variable: datosLeidos. Y cuando ya no pueda leer más datos, se -
             * detenga, de ahi el porque se coloco que debe ser distinto de -1.
             *
             * Ahora, otro aspecto a mencionar es que durante el transcurso del ciclo, se -
             * estaria guardando los datos del uri dentro de la variable: cajita, esto por -
             * lo comentado anteriormente. Además de que esos datos se guardaran desde la -
             * primera posición, osea 0. */
            int datosLeidos;
            byte[] memoriaTemporal = new byte[16384];
            while((datosLeidos = lectorDatos.read(memoriaTemporal, 0, memoriaTemporal.length)) != -1) {
                cajita.write(memoriaTemporal, 0, datosLeidos);
            }

            /* Luego, lo tercero seria ahora obtener esos datos como un arreglo de bytes, esto -
             * por medio de la variable: cajita, ya que en la documentación del ByteArrayOutputStream, -
             * dicha clase contiene el comando: toByteArray(), el cual nos permite obtener esos datos -
             * por medio de un arreglo (o lista) de tipo byte. Y una vez obtenido, se cierra la lectura -
             * del InputStream, por temas de buenas prácticas.
             *
             * Ahora, esto se hace porque en las imagenes se deben de manejar con un arreglo de bytes -
             * (osea byte[] en código) para poder manejar de mejor forma dicha imagen respectivamente. */
            byte[] datosImagen = cajita.toByteArray();
            lectorDatos.close();

            /* Después de eso, lo que se haria ahora es tomar la variable: "datosImagen", y codificarlos -
             * a una base de 64 caracteres, esto porque se evita que los datos binarios se corrompan -
             * durante la red. Asi que por eso se coloca este aspecto.
             *
             * Ahora, luego de eso, se emplea un RequestBody y un MultipartBody.part, esto debido a que -
             * con ayuda del RequestBody, nosotros podemos decirle que la variable: "imagenBase64", lo -
             * guarde como un texto plano, esto porque con el RequestBody podemos decirle cual es el -
             * contenido de la solicitud que vamos a enviar, que en este caso es la imagen.
             *
             * Ya con eso, con la ayuda del MultipartBody.part, nos permitira a decirle al endpoint (el cual -
             * fue el que se definio para enviar la imagen) que esto es parte del formulario, ya que en dicho -
             * endpoint se utiliza el Multipart que ofrece Retrofit.
             *
             * Por lo que se crea esa parte colocando dicho RequestBody, y agregandole también un nombre de campo -
             * que se llama: Imagen_Perfil. Básicamente, esto es similar a cuando se crea una nueva instancia de un -
             * SharedPreferences, por lo que fungiria como un identificador respectivamente. */
            imagenBase64 = Base64.encodeToString(datosImagen, Base64.DEFAULT);
            RequestBody archivoImagen = RequestBody.create(imagenBase64, MediaType.parse("text/plain"));
            MultipartBody.Part fotoPerfil = MultipartBody.Part.createFormData("Imagen_Perfil",null, archivoImagen);

            /* Aquí se llama la conexión del API. Además de indicarle -
             * también que en este fragmento (y clase) se esta pidiendo -
             * esa conexión del API como tal, de ahí el porque se esta -
             * pasando lo que tiene la variable: "nombreActividad".
             *
             * NOTA: Se uso un getActivity() en la variable: "nombreActividad" -
             * porque es la manera en la cual se puede saber cual es la actividad -
             * que esta haciendo uso de los fragmentos (que este caso es el menu -
             * principal), haciendo que la aplicacion puede saber donde se esta -
             * haciendo el llamado. Osea una instancia de la clase. */
            Activity nombreActividad = getActivity();
            usuarioInterface = ConexionAPI.Conexion_API(nombreActividad);

            /* Después de eso simplemente se hace la petición con el metodo respectivo, para -
             * así poder enviar la nueva imagen y el token para que el API lo pueda recibir. -
             * Esto por medio de la interfaz respectiva. */
            Call<Boolean> fotoUsuario = usuarioInterface.cambiarFotoPerfil(fotoPerfil, token);

            /* Aqui es cuando notamos si se pudo realizar o no el proceso, en este caso -
             * el metodo PUT.
             *
             * Básicamente, con esto podemos ejecutar la petición anterior y además, también -
             * podemos saber la posible respuesta que pudo brindar el API como tal. */
            fotoUsuario.enqueue(new Callback<Boolean>() {

                /* Aqui es para saber si hubo una respuesta por parte del API. */
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        /* Aqui lo que se está haciendo es mostrar un mensaje mencionando -
                         * que se pudo cambiar la imagen, y después de eso simplemente se -
                         * envia al metodo: MostrarDatosPerfil para que muestre todos los -
                         * datos actualizados (en este caso la imagen de perfil). */
                        Toast.makeText(getActivity(), "¡La foto de perfil se cambio exitosamente!", Toast.LENGTH_LONG).show();
                        MostrarDatosPerfil(correoUsuario, token);

                    } else {
                        try {
                            /* Esto permite leer el error del Body, de modo -
                             * que sirva en el debug. */
                            String error = response.errorBody().string();

                            /* Esto es para imprimir los mensajes de error. */
                            Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Pero en este momento no es posible actualizar la información debido a que " + error, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();

                            /* Este sirve solo para el logcat:
                             * System.out.println(error); */
                        } catch (Exception error) {
                            /* El printStackTrace(), sirve para aspectos de depuración.
                             * Esto debido a que ayuda a entender donde y porque ocurrio -
                             * un error durante la ejecución del proyecto. En este caso -
                             * las excepciones respectivamente.
                             *
                             * error.printStackTrace(); */
                            Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Pero no es posible actualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /* Aqui es para saber si hubo un fallo en dar la respuesta -
                 * por parte del API. */
                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    /* Sirve para imprimir el mensaje que se recibio anteriormente, -
                     * y también para ver en que fallo en el API.
                     *
                     * NOTA: Este comando es para ver que fallo, el usuario no lo debe -
                     * ver:
                     * Toast.makeText(getActivity(), t.getLocalizedMessage(),
                     * Toast.LENGTH_SHORT).show(); */
                    Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Pero no es posible actualizar la información en estos momentos debido problema técnico.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
                    Toast.makeText(getActivity(),"Si el problema persiste, entonces \ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            /* Aqui lo que se hace es enviar un mensaje de error, esto por si llegara a suceder -
             * algún imprevisto o algún error durante el cambio del perfil respectivamente. */
            Toast.makeText(getActivity(), "¡Lo sentimos, pero parece que hubo un problema técnico!" + "\nPor favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" + "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
        }
    }


    /* Metodo que sirve para ver los permisos que la aplicación -
     * móvil le ha solicitado al usuario para poder funcionar -
     * respectivamente. */
    private void VistaPermisos(int Rol, Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar) {
        try {
            /* Aqui lo que se esta haciendo es validar si el usuario tiene el rol: "Moderador", -
             * "Administrador" o "Empleado". Y si no, entonces no lo dejaria acceder a los permisos -
             * de la aplicación móvil. */
            if (Rol == 1 || Rol == 2 || Rol == 3) {
                /* Una vez hecho eso, lo que seguiria seria validar los permisos del usuario -
                 * que ha iniciado sesión. Y, si luego de eso, la respuesta que trae es un 4, -
                 * entonces quiere decir que si esta autorizado para acceder a los permisos de -
                 * la aplicacion móvil respectivamente. */
                Integer respuestaPermisos = ValidarPermisosUsuario(Leer, Crear, Actualizar, Eliminar);
                if (respuestaPermisos == 4) {
                    /* Aqui lo que se esta haciendo es primero realizar un intent, el cual, se le esta indicando -
                     * que lo lleve hacia los detalles de una aplicación dentro de los ajustes del sistema, esto -
                     * básicamente funge como un hipervinculo por decirlo de una manera.
                     *
                     * Ahora, con el Uri, lo que se esta haciendo es indicar la ruta donde se encuentra la aplicación -
                     * móvil, de modo que una vez hecho eso, simplemente se le coloca esa ruta al intentConfiguracionApp -
                     * y se ejecuta para que se lleve a los detalles de la aplicación. De forma que así el usuario pueda -
                     * revisar todos los permisos que solicita la aplicación móvil respectivamente. */
                    Intent intentConfiguracionApp = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri rutaAplicacion = Uri.fromParts("package", getActivity().getPackageName(), null);

                    intentConfiguracionApp.setData(rutaAplicacion);
                    startActivity(intentConfiguracionApp);
                }
            }
        } catch (Exception error) {
            Toast.makeText(getActivity(), "¡Lo sentimos, pero parece que hubo un problema técnico!" + "\nPor favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" + "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
        }
    }


    /* Metodo que sirve para que el usuario pueda cerrar sesión en -
     * la aplicación móvil respectivamente. */
    private void VistaCerrarSesion() {
        try {
            /* Lo primero seria acceder al archivo XML que tiene como nombre: -
             * "Archivo_Autenticacion", esto de forma privada. Y, si sucede que no -
             * esta creado, entonces el sistema lo crearia automaticamente. */
            SharedPreferences archivoXML = getContext().getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            /* Luego, lo segundo seria que en el archivo XML llamado: "Archivo_Autenticacion", -
             * se elimine absolutamente todos los datos que ha guardado y que aplique dicha -
             * eliminación.
             *
             * Después, simplemente se indica al MainActivity que se desactivo la sesión -
             * de forma permanente, ya que asi se puede evitar que algún token no valido -
             * se quede guardado en los SharedPreferences, de modo que así se mantenga -
             * siempre limpio. */
            archivoXML.edit().clear().apply();
            MainActivity.botonActivado = false;

            /* Esto solo seria si quisiera ver que pantalla es en el Debug.
            Activity nombreActividad = getActivity();

            //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
            Intent intentPrincipal = new Intent(nombreActividad, MainActivity.class);
            startActivity(intentPrincipal);
            nombreActividad.finish(); */

            //Aqui le dice a que vista tiene que ir, como un hipervinculo basicamente.
            Intent intentPrincipal = new Intent(getActivity(), MainActivity.class);
            startActivity(intentPrincipal);
            getActivity().finish();

        } catch (Exception error) {
            Toast.makeText(getActivity(), "¡Lo sentimos!", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Pero en este momento no fue posible cerrar la sesión de su cuenta debido a un problema técnico.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(), "Por favor, intentelo más tarde.", Toast.LENGTH_LONG).show();
            Toast.makeText(getActivity(),"Si el problema persiste, entonces" + "\ncontactese con el personal técnico.", Toast.LENGTH_LONG).show();
        }
    }

}