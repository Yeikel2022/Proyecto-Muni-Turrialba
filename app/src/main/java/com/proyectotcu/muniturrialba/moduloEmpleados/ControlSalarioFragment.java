package com.proyectotcu.muniturrialba.moduloEmpleados;

import static android.view.View.GONE;
import static android.view.View.TEXT_ALIGNMENT_VIEW_START;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proyectotcu.muniturrialba.R;
import com.proyectotcu.muniturrialba.manejoAPI.ConexionAPI;
import com.proyectotcu.muniturrialba.manejoAPI.entidadesAPI.ExtensionSalarioEntitie;
import com.proyectotcu.muniturrialba.manejoAPI.interfacesAPI.SalarioInterface;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ControlSalarioFragment extends Fragment {

    //Variables globales para esta clase.
    TextView txtNombre, txtApellidos, txtEdad, txtCedula, txtDepartamento, txtFechaEntrega,
             txtSalario, txtDescripcion, campoNombre, campoApellidos, campoEdad, campoCedula,
             campoDepartamento, campoFechaEntrega, campoSalario, campoDescripcion, txtMensaje;

    Integer LargoContenido, AnchoContenido, LargoCheckBox, AnchoCheckBox, TamañoLetraContenido,
            margenContenido, margenCheckBox, margenTop, paddingTopContenido, paddingStartContenido,
            paddingEndContenido;

    Button botonCrear, botonActualizar, botonEliminar;
    TableRow tbrInfoFila, tbrPrimeraFila, nuevaFila;

    TableRow.LayoutParams parametrosContenido, parametrosCheckBox;
    HorizontalScrollView scrollHorizontal, scrollHorizontalBotones;
    CheckBox botonSeleccion, campoCheckBox;

    TableLayout tblTablaSalarios;
    ImageView logitoSalarios;
    SearchView buscadorSalarios;
    List<ExtensionSalarioEntitie> datosOrdenados;

    public static Boolean mensajeSalarios = false;

    //Interfaz que contiene los métodos de la entidad FAQ.
    SalarioInterface salarioInterface;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_control_salarios, container, false);
        logitoSalarios = view.findViewById(R.id.img_fotoSalarios);
        buscadorSalarios = view.findViewById(R.id.sv_buscarSalarios);

        botonCrear = view.findViewById(R.id.btn_AñadirSalario);
        botonActualizar = view.findViewById(R.id.btn_EditarSalario);
        botonEliminar = view.findViewById(R.id.btn_EliminarSalarios);
        botonSeleccion = view.findViewById(R.id.btn_SeleccionDatoSalarios);

        txtNombre = view.findViewById(R.id.txt_NombreSalario);
        txtApellidos = view.findViewById(R.id.txt_ApellidoSalario);
        txtEdad = view.findViewById(R.id.txt_EdadSalario);
        txtCedula = view.findViewById(R.id.txt_CedulaSalario);
        txtDepartamento = view.findViewById(R.id.txt_DepartamentoSalario);
        txtFechaEntrega = view.findViewById(R.id.txt_FechaEntregaSalario);
        txtSalario = view.findViewById(R.id.txt_Salario);
        txtDescripcion = view.findViewById(R.id.txt_DescripcionSalario);
        txtMensaje = view.findViewById(R.id.txt_MensajeSalarios);

        scrollHorizontal = view.findViewById(R.id.hsv_ScrollHorizontalSalarios);
        scrollHorizontalBotones = view.findViewById(R.id.hsv_ScrollHorizontalBotones_Salarios);

        tblTablaSalarios = view.findViewById(R.id.tbl_TablaSalarios);
        tbrInfoFila = view.findViewById(R.id.tbr_InfoFilaSalarios);
        tbrPrimeraFila = view.findViewById(R.id.tbr_PrimeraFilaSalarios);

        logitoSalarios.setVisibility(GONE);
        txtMensaje.setVisibility(GONE);

        try {
            SharedPreferences archivoXML = getActivity().getSharedPreferences(
                    "Archivo_Autenticacion", Context.MODE_PRIVATE);

            String tokenGuardado = archivoXML.getString("JWT_token", null);

            String[] partesToken = tokenGuardado.split("\\.");

            String cuerpoToken = new String(Base64.decode(partesToken[1],
                    Base64.URL_SAFE), StandardCharsets.UTF_8);

            JSONObject json = new JSONObject(cuerpoToken);

            Integer campoRol = Integer.parseInt(json.optString("rol"));
            Boolean campoPermisoLeer = Boolean.parseBoolean(json.optString("permiso_Leer"));
            Boolean campoPermisoCrear = Boolean.parseBoolean(json.optString("permiso_Crear"));
            Boolean campoPermisoActualizar = Boolean.parseBoolean(json.optString("permiso_Actualizar"));
            Boolean campoPermisoEliminar = Boolean.parseBoolean(json.optString("permiso_Eliminar"));


            buscadorSalarios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, query);
                    buscadorSalarios.clearFocus();
                    return true;
                }
            });

            buscadorSalarios.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    BuscarPrioridad(tokenGuardado, datosOrdenados, "true");
                    buscadorSalarios.clearFocus();
                    buscadorSalarios.setIconifiedByDefault(true);
                    return false;
                }
            });


            //Moderador:
            if (campoRol == 1) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearSalarios());
                    botonActualizar.setOnClickListener(v -> VistaActualizarSalarios());
                    botonEliminar.setOnClickListener(v -> { AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_eliminar);
                        construirAlerta.setMessage("¿Esta completamente seguro(a) de eliminar este salario de forma permanentemente?")
                                .setTitle("Eliminar Salario.");


                        construirAlerta.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EliminarSalarios(tokenGuardado);
                            }
                        });

                        construirAlerta.setNegativeButton("No.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "¡No se continuo con la eliminación!", Toast.LENGTH_LONG).show();
                            }
                        });

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    });

                    MostrarSalarios(tokenGuardado, null, false);
                }
            }


            //Administrador:
            if (campoRol == 2) {
                Integer respuestaPermisos = ValidarPermisosAdmin(campoPermisoLeer, campoPermisoCrear, campoPermisoActualizar, campoPermisoEliminar, campoRol);

                if (respuestaPermisos == 5) {
                    botonCrear.setOnClickListener(v -> VistaCrearSalarios());
                    botonActualizar.setVisibility(GONE);
                    botonEliminar.setVisibility(GONE);

                    MostrarSalarios(tokenGuardado, null, false);
                }
            }

        } catch (Exception error) {
            buscadorSalarios.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información de los salarios en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }

        return view;
    }



    private Integer ValidarPermisosAdmin(Boolean Leer, Boolean Crear, Boolean Actualizar, Boolean Eliminar, Integer Rol) {
        ArrayList<String> listaMensaje = new ArrayList<String>();
        String mensajeProveniente = "";

        switch (Rol) {
            //Moderador:
            case 1:
                if(Leer == false && Crear == false && Actualizar == false && Eliminar == false) {
                    buscadorSalarios.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoSalarios.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    if (mensajeSalarios != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeSalarios = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }


                if(Leer == false) {
                    buscadorSalarios.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoSalarios.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }


                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo registro de salario dentro de este apartado.\n\n");
                }


                if(Actualizar == false) {
                    botonActualizar.setVisibility(GONE);
                    listaMensaje.add("- Actualizar un registro de salario respectivamente.\n\n");
                }


                if(Eliminar == false) {
                    botonEliminar.setVisibility(GONE);
                    listaMensaje.add("- Eliminar un registro de salario respectivamente.");
                }


                if(Leer != true || Crear != true || Actualizar != true || Eliminar != true) {
                    if (listaMensaje.size() == 3) {
                        scrollHorizontalBotones.setVisibility(GONE);
                    }

                    if (mensajeSalarios != true) {
                        for (int i = 0; i < listaMensaje.size(); i++) {
                            mensajeProveniente += listaMensaje.get(i);
                        }

                        AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                        construirAlertaCrear.setIcon(R.drawable.icono_error);

                        construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente)
                                .setTitle("¡Lo sentimos!");

                        construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeSalarios = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;

            //Administrador:
            case 2:
                if(Leer == false && Crear == false) {
                    buscadorSalarios.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoSalarios.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    if (mensajeSalarios != true) {
                        AlertDialog.Builder construirAlertaAutorizacion = new AlertDialog.Builder(getActivity());
                        construirAlertaAutorizacion.setIcon(R.drawable.icono_error);
                        construirAlertaAutorizacion.setMessage("Pero no tienes la autorización necesaria para realizar alguna acción dentro de este apartado.")
                                .setTitle("¡Lo sentimos!");

                        construirAlertaAutorizacion.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeSalarios = true;
                            }});

                        AlertDialog ejecutarMensajeAutorizacion = construirAlertaAutorizacion.create();
                        ejecutarMensajeAutorizacion.show();
                    }

                    return 0;
                }


                if(Leer == false) {
                    buscadorSalarios.setVisibility(View.GONE);
                    botonCrear.setVisibility(View.GONE);
                    botonActualizar.setVisibility(View.GONE);
                    botonEliminar.setVisibility(View.GONE);

                    scrollHorizontalBotones.setVisibility(View.GONE);
                    scrollHorizontal.setVisibility(View.GONE);

                    logitoSalarios.setVisibility(VISIBLE);
                    txtMensaje.setVisibility(VISIBLE);

                    logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                    txtMensaje.setText(getString(R.string.AutorizacionDenegada));

                    listaMensaje.add("- Visualizar esta información.\n\n");
                }


                if(Crear == false) {
                    botonCrear.setVisibility(GONE);
                    listaMensaje.add("- Crear un nuevo registro de salario dentro de este apartado.");
                }


                if(Leer != true || Crear != true) {
                    scrollHorizontalBotones.setVisibility(GONE);

                    if (mensajeSalarios != true) {
                        for (int i = 0; i < listaMensaje.size(); i++) {
                            mensajeProveniente += listaMensaje.get(i);
                        }

                        AlertDialog.Builder construirAlertaCrear = new AlertDialog.Builder(getActivity());
                        construirAlertaCrear.setIcon(R.drawable.icono_error);

                        construirAlertaCrear.setMessage("Pero no tienes la autorización necesaria para: \n\n" + mensajeProveniente)
                                .setTitle("¡Lo sentimos!");

                        construirAlertaCrear.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mensajeSalarios = true;
                            }
                        });

                        AlertDialog ejecutarMensajeCrear = construirAlertaCrear.create();
                        ejecutarMensajeCrear.show();
                    }
                }

                break;
        }


        //El 5 se refiere a que el usuario que inicio sesión si esta autorizado.
        return 5;
    }

    private void BuscarPrioridad(String tokenUsuario, List<ExtensionSalarioEntitie> listaDatos, String textoIngresado) {
        try {
            List<ExtensionSalarioEntitie> datosFiltrados = new ArrayList<>();

            if (textoIngresado.isEmpty() || textoIngresado.equals("true")) {
                datosFiltrados.addAll(listaDatos);
                MostrarSalarios(tokenUsuario, datosFiltrados, false);

            } else {
                for (ExtensionSalarioEntitie salarioEntitie : listaDatos) {
                    String nombreEmpleado = salarioEntitie.getNombre().toLowerCase().trim();

                    if (nombreEmpleado.contains(textoIngresado.toLowerCase())) {
                        datosFiltrados.add(salarioEntitie);
                    }
                }

                if(datosFiltrados.isEmpty()) {
                    AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                    construirAlerta.setIcon(R.drawable.icono_advertencia);
                    construirAlerta.setMessage("Pero no se pudo encontrar el salario debido a que existen datos incorrectos o porque el registro no existe como tal. \n\nPor favor, corriga los errores e intentelo de nuevo.")
                            .setTitle("¡Lo sentimos!");

                    construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}});

                    AlertDialog ejecutarMensaje = construirAlerta.create();
                    ejecutarMensaje.show();

                    MostrarSalarios(tokenUsuario, datosFiltrados, false);

                } else {
                    MostrarSalarios(tokenUsuario, datosFiltrados, true);
                }
            }

        } catch (Exception error) {
            buscadorSalarios.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible visualizar la información del salario en estos momentos debido a un problema técnico. Por favor intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void MostrarSalarios(String tokenUsuario, List<ExtensionSalarioEntitie> listaActualizada, Boolean autorizacion) {

        Activity nombreActividad = getActivity();

        salarioInterface = ConexionAPI.Conexion_API_Salario(nombreActividad);

        Call<List<ExtensionSalarioEntitie>> mostrarSalarios = salarioInterface.obtenerSalarios(tokenUsuario);


        mostrarSalarios.enqueue(new Callback<List<ExtensionSalarioEntitie>>() {
            @Override
            public void onResponse(Call<List<ExtensionSalarioEntitie>> call, Response<List<ExtensionSalarioEntitie>> response) {
                if (response.isSuccessful()) {

                    if(response.body().size() == 1) {
                        tblTablaSalarios.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            ExtensionSalarioEntitie salarios = response.body().get(i);
                            String Nombre  = salarios.getNombre().trim();
                            String Apellidos = salarios.getApellido_1().trim() + " " + salarios.getApellido_2().trim();
                            String Edad = salarios.getEdad().toString().trim();
                            String Cedula = salarios.getCedula().trim();
                            String Departamento = salarios.getDepartamento().trim();
                            String FechaEntrega = salarios.getFechaEntrega().trim();
                            String Salario = salarios.getSalario().toString().trim();
                            String Descripcion = salarios.getDescripcion().trim();


                            txtNombre.setText(Nombre);
                            txtApellidos.setText(Apellidos);
                            txtEdad.setText(Edad);
                            txtCedula.setText(Cedula);
                            txtDepartamento.setText(Departamento);
                            txtFechaEntrega.setText(FechaEntrega);
                            txtSalario.setText(Salario);
                            txtDescripcion.setText(Descripcion);

                            botonSeleccion.setTag(salarios);
                            botonSeleccion.setVisibility(VISIBLE);
                        }
                    }

                    tblTablaSalarios.removeAllViews();
                    tbrPrimeraFila.setVisibility(GONE);
                    botonSeleccion.setVisibility(GONE);

                    datosOrdenados = response.body();
                    datosOrdenados.sort(new Comparator<ExtensionSalarioEntitie>() {
                        @Override
                        public int compare(ExtensionSalarioEntitie o1, ExtensionSalarioEntitie o2) {
                            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
                        }
                    });


                    if(autorizacion != false) {
                        tblTablaSalarios.removeAllViews();

                        for (int i = 0; i < listaActualizada.size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoEdad = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoFechaEntrega = new TextView(getActivity());
                            campoSalario = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            LargoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            AnchoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);


                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            parametrosCheckBox = new TableRow.LayoutParams(LargoCheckBox, AnchoCheckBox);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            margenCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            parametrosContenido.setMarginStart(margenContenido);
                            parametrosCheckBox.setMarginStart(margenCheckBox);
                            parametrosCheckBox.setMarginEnd(margenCheckBox);


                            ExtensionSalarioEntitie salarios = listaActualizada.get(i);
                            String Nombre  = salarios.getNombre().trim();
                            String Apellidos = salarios.getApellido_1().trim() + " " + salarios.getApellido_2().trim();
                            String Edad = salarios.getEdad().toString().trim();
                            String Cedula = salarios.getCedula().trim();
                            String Departamento = salarios.getDepartamento().trim();
                            String FechaEntrega = salarios.getFechaEntrega().trim().replace("T", " ");

                            //Se coloco un BigDecimal para eliminar la notación cientifica:
                            String Salario = new BigDecimal(salarios.getSalario().toString().trim()).toPlainString();
                            String Descripcion = salarios.getDescripcion().trim();

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(salarios);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoEdad.setText(Edad);
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            tblTablaSalarios.addView(nuevaFila);
                        }

                    } else {
                        tblTablaSalarios.removeAllViews();

                        for (int i = 0; i < response.body().size(); i++) {
                            nuevaFila = new TableRow(getActivity());
                            nuevaFila.setBackground(getActivity().getDrawable(R.drawable.border_table));
                            campoCheckBox = new CheckBox(getActivity());
                            campoNombre = new TextView(getActivity());
                            campoApellidos = new TextView(getActivity());
                            campoEdad = new TextView(getActivity());
                            campoCedula = new TextView(getActivity());
                            campoDepartamento = new TextView(getActivity());
                            campoFechaEntrega = new TextView(getActivity());
                            campoSalario = new TextView(getActivity());
                            campoDescripcion = new TextView(getActivity());


                            LargoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 350);
                            AnchoContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 205);
                            LargoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            AnchoCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 30);
                            TamañoLetraContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_SP, 10);


                            parametrosContenido = new TableRow.LayoutParams(LargoContenido, AnchoContenido);
                            parametrosCheckBox = new TableRow.LayoutParams(LargoCheckBox, AnchoCheckBox);
                            margenContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, -1);
                            margenCheckBox = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 7);
                            margenTop = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            paddingStartContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 11);
                            paddingEndContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);
                            paddingTopContenido = ConvertirPropiedades(TypedValue.COMPLEX_UNIT_DIP, 5);

                            parametrosContenido.setMarginStart(margenContenido);
                            parametrosCheckBox.setMarginStart(margenCheckBox);
                            parametrosCheckBox.setMarginEnd(margenCheckBox);


                            ExtensionSalarioEntitie salarios = datosOrdenados.get(i);
                            String Nombre  = salarios.getNombre().trim();
                            String Apellidos = salarios.getApellido_1().trim() + " " + salarios.getApellido_2().trim();
                            String Edad = salarios.getEdad().toString().trim();
                            String Cedula = salarios.getCedula().trim();
                            String Departamento = salarios.getDepartamento().trim();
                            String FechaEntrega = salarios.getFechaEntrega().trim().replace("T", " ");

                            //Se coloco un BigDecimal para eliminar la notación cientifica:
                            String Salario = new BigDecimal(salarios.getSalario().toString().trim()).toPlainString();
                            String Descripcion = salarios.getDescripcion().trim();

                            campoCheckBox.setWidth(LargoCheckBox);
                            campoCheckBox.setHeight(AnchoCheckBox);
                            campoCheckBox.setLayoutParams(parametrosCheckBox);
                            campoCheckBox.setTop(margenTop);
                            campoCheckBox.setPaddingRelative(0, paddingTopContenido, 0, 0);
                            campoCheckBox.setButtonTintList(ColorStateList.valueOf(Color.BLACK));
                            campoCheckBox.setTag(salarios);

                            campoNombre.setText(Nombre);
                            campoNombre.setWidth(LargoContenido);
                            campoNombre.setHeight(AnchoContenido);
                            campoNombre.setLayoutParams(parametrosContenido);
                            campoNombre.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoNombre.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoNombre.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoNombre.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoNombre.setTextColor(Color.BLACK);
                            campoNombre.setTextSize(TamañoLetraContenido);

                            campoApellidos.setText(Apellidos);
                            campoApellidos.setWidth(LargoContenido);
                            campoApellidos.setHeight(AnchoContenido);
                            campoApellidos.setLayoutParams(parametrosContenido);
                            campoApellidos.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoApellidos.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoApellidos.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoApellidos.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoApellidos.setTextColor(Color.BLACK);
                            campoApellidos.setTextSize(TamañoLetraContenido);

                            campoEdad.setText(Edad);
                            campoEdad.setWidth(LargoContenido);
                            campoEdad.setHeight(AnchoContenido);
                            campoEdad.setLayoutParams(parametrosContenido);
                            campoEdad.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoEdad.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoEdad.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoEdad.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoEdad.setTextColor(Color.BLACK);
                            campoEdad.setTextSize(TamañoLetraContenido);

                            campoCedula.setText(Cedula);
                            campoCedula.setWidth(LargoContenido);
                            campoCedula.setHeight(AnchoContenido);
                            campoCedula.setLayoutParams(parametrosContenido);
                            campoCedula.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoCedula.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoCedula.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoCedula.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoCedula.setTextColor(Color.BLACK);
                            campoCedula.setTextSize(TamañoLetraContenido);

                            campoDepartamento.setText(Departamento);
                            campoDepartamento.setWidth(LargoContenido);
                            campoDepartamento.setHeight(AnchoContenido);
                            campoDepartamento.setLayoutParams(parametrosContenido);
                            campoDepartamento.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDepartamento.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDepartamento.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDepartamento.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDepartamento.setTextColor(Color.BLACK);
                            campoDepartamento.setTextSize(TamañoLetraContenido);

                            campoFechaEntrega.setText(FechaEntrega);
                            campoFechaEntrega.setWidth(LargoContenido);
                            campoFechaEntrega.setHeight(AnchoContenido);
                            campoFechaEntrega.setLayoutParams(parametrosContenido);
                            campoFechaEntrega.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoFechaEntrega.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoFechaEntrega.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoFechaEntrega.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoFechaEntrega.setTextColor(Color.BLACK);
                            campoFechaEntrega.setTextSize(TamañoLetraContenido);

                            campoSalario.setText(Salario);
                            campoSalario.setWidth(LargoContenido);
                            campoSalario.setHeight(AnchoContenido);
                            campoSalario.setLayoutParams(parametrosContenido);
                            campoSalario.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoSalario.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoSalario.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoSalario.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoSalario.setTextColor(Color.BLACK);
                            campoSalario.setTextSize(TamañoLetraContenido);

                            campoDescripcion.setText(Descripcion);
                            campoDescripcion.setWidth(LargoContenido);
                            campoDescripcion.setHeight(AnchoContenido);
                            campoDescripcion.setLayoutParams(parametrosContenido);
                            campoDescripcion.setPaddingRelative(paddingStartContenido, paddingTopContenido, paddingEndContenido, 0);
                            campoDescripcion.setTextAlignment(TEXT_ALIGNMENT_VIEW_START);
                            campoDescripcion.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
                            campoDescripcion.setBackground(getActivity().getDrawable(R.drawable.border_table_row));
                            campoDescripcion.setTextColor(Color.BLACK);
                            campoDescripcion.setTextSize(TamañoLetraContenido);


                            nuevaFila.addView(campoCheckBox);
                            nuevaFila.addView(campoNombre);
                            nuevaFila.addView(campoApellidos);
                            nuevaFila.addView(campoEdad);
                            nuevaFila.addView(campoCedula);
                            nuevaFila.addView(campoDepartamento);
                            nuevaFila.addView(campoFechaEntrega);
                            nuevaFila.addView(campoSalario);
                            nuevaFila.addView(campoDescripcion);
                            tblTablaSalarios.addView(nuevaFila);
                        }
                    }

                } else {
                    try {
                        String error = response.errorBody().string();
                        int errorRaw = response.raw().code();

                        if(errorRaw == 401) {
                            error = "Se finalizo la sesión de su cuenta.";
                        }

                        buscadorSalarios.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoSalarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero en este momento no es posible ver la información de los salarios debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();

                    } catch (Exception error) {
                        buscadorSalarios.setVisibility(View.GONE);
                        botonCrear.setVisibility(View.GONE);
                        botonActualizar.setVisibility(View.GONE);
                        botonEliminar.setVisibility(View.GONE);

                        scrollHorizontalBotones.setVisibility(View.GONE);
                        scrollHorizontal.setVisibility(View.GONE);

                        logitoSalarios.setVisibility(VISIBLE);
                        txtMensaje.setVisibility(VISIBLE);

                        logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                        txtMensaje.setText(getString(R.string.ErrorFragment));

                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible visualizar la información de los salarios en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
            public void onFailure(Call<List<ExtensionSalarioEntitie>> call, Throwable t) {
                buscadorSalarios.setVisibility(View.GONE);
                botonCrear.setVisibility(View.GONE);
                botonActualizar.setVisibility(View.GONE);
                botonEliminar.setVisibility(View.GONE);

                scrollHorizontalBotones.setVisibility(View.GONE);
                scrollHorizontal.setVisibility(View.GONE);

                logitoSalarios.setVisibility(VISIBLE);
                txtMensaje.setVisibility(VISIBLE);

                logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
                txtMensaje.setText(getString(R.string.ErrorFragment));

                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero no es posible visualizar la información de los salarios en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }
        });
    }

    private Integer ConvertirPropiedades(int tipoPropiedad, int tamañoPropiedad) {
        int resultado = 0;

        if (tipoPropiedad == TypedValue.COMPLEX_UNIT_DIP) {
            resultado = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tamañoPropiedad,
                    getResources().getDisplayMetrics());
        }

        if(tipoPropiedad == TypedValue.COMPLEX_UNIT_SP) {
            resultado = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tamañoPropiedad,
                    getResources().getDisplayMetrics());
        }

        return resultado;
    }

    private void VistaCrearSalarios() {
        Intent intentCrearSalario = new Intent(getActivity(), ControlSalarioCrearActivity.class);

        startActivity(intentCrearSalario);

        getActivity().finish();
    }

    private void VistaActualizarSalarios() {
        try {
            Integer cantidadChecks = 0;
            ExtensionSalarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaSalarios.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaSalarios.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionSalarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String Nombre = datoSeleccionado.getNombre().trim();
                String Apellidos = datoSeleccionado.getApellido_1().trim() + " " + datoSeleccionado.getApellido_2().trim();

                String Cedula = datoSeleccionado.getCedula().trim();
                String FechaEntrega = datoSeleccionado.getFechaEntrega().trim().replace("T", " ");

                //Para quitar la notación cientifica en el salario (esto por si el dato es muy largo):
                String Salario = new BigDecimal(datoSeleccionado.getSalario().toString().trim()).toPlainString();
                String Descripcion = datoSeleccionado.getDescripcion().trim();


                Intent intentActualizarSalario = new Intent(getActivity(), ControlSalarioActualizarActivity.class);

                intentActualizarSalario.putExtra("Nombre", Nombre);
                intentActualizarSalario.putExtra("Apellidos", Apellidos);

                intentActualizarSalario.putExtra("Cedula", Cedula);
                intentActualizarSalario.putExtra("FechaEntrega", FechaEntrega);

                intentActualizarSalario.putExtra("Salario", Salario);
                intentActualizarSalario.putExtra("Descripcion", Descripcion);

                startActivity(intentActualizarSalario);

                getActivity().finish();

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible actualizar el salario del empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorSalarios.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible actualizar el salario en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }

    private void EliminarSalarios(String tokenUsuario) {
        try {
            Integer cantidadChecks = 0;
            ExtensionSalarioEntitie datoSeleccionado = null;

            for(int i = 0; i < tblTablaSalarios.getChildCount(); i++) {
                TableRow registroDatos = (TableRow) tblTablaSalarios.getChildAt(i);
                CheckBox seleccionDato = (CheckBox) registroDatos.getChildAt(0);


                if(seleccionDato.isChecked()) {
                    cantidadChecks++;
                    datoSeleccionado = (ExtensionSalarioEntitie) seleccionDato.getTag();
                }
            }


            if(cantidadChecks == 1 && datoSeleccionado != null) {
                String cedulaGuardado = datoSeleccionado.getCedula().trim();

                Activity nombreActividad = getActivity();
                salarioInterface = ConexionAPI.Conexion_API_Salario(nombreActividad);

                Call<Boolean> eliminarSalario = salarioInterface.eliminarSalarios(cedulaGuardado, tokenUsuario);

                eliminarSalario.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(),
                                    "¡El registro del salario seleccionado ha sido eliminado exitosamente!", Toast.LENGTH_SHORT).show();

                            MostrarSalarios(tokenUsuario,  null, false);
                        } else {
                            try {
                                String error = response.errorBody().string();
                                int errorRaw = response.raw().code();

                                if(errorRaw == 401) {
                                    error = "Se finalizo la sesión de su cuenta.";
                                }

                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero en este momento no es posible eliminar el salario del empleado(a) debido a que: " + error + "\n\nPor favor, intentelo de nuevo.")
                                        .setTitle("¡Lo sentimos!");

                                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}});

                                AlertDialog ejecutarMensaje = construirAlerta.create();
                                ejecutarMensaje.show();

                            } catch (Exception error) {
                                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                                construirAlerta.setIcon(R.drawable.icono_error);
                                construirAlerta.setMessage("Pero no es posible eliminar el salario del empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
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
                        AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                        construirAlerta.setIcon(R.drawable.icono_error);
                        construirAlerta.setMessage("Pero no es posible eliminar el salario del empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                                .setTitle("¡Lo sentimos!");

                        construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}});

                        AlertDialog ejecutarMensaje = construirAlerta.create();
                        ejecutarMensaje.show();
                    }
                });

            } else {
                AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
                construirAlerta.setIcon(R.drawable.icono_error);
                construirAlerta.setMessage("Pero en este momento no es posible eliminar el salario del empleado(a) debido a que se selecciono más de un dato o que incluso no se selecciono ninguno.")
                        .setTitle("¡Lo sentimos!");

                construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

                AlertDialog ejecutarMensaje = construirAlerta.create();
                ejecutarMensaje.show();
            }

        } catch (Exception error) {
            buscadorSalarios.setVisibility(View.GONE);
            botonCrear.setVisibility(View.GONE);
            botonActualizar.setVisibility(View.GONE);
            botonEliminar.setVisibility(View.GONE);

            scrollHorizontalBotones.setVisibility(View.GONE);
            scrollHorizontal.setVisibility(View.GONE);

            logitoSalarios.setVisibility(VISIBLE);
            txtMensaje.setVisibility(VISIBLE);

            logitoSalarios.setImageResource(R.drawable.icono_contenido_no_disponible);
            txtMensaje.setText(getString(R.string.ErrorFragment));

            AlertDialog.Builder construirAlerta = new AlertDialog.Builder(getActivity());
            construirAlerta.setIcon(R.drawable.icono_error);
            construirAlerta.setMessage("Pero no es posible eliminar el salario del empleado(a) en estos momentos debido a un problema técnico. Por favor, intentelo más tarde." + "\n\nSi el problema persiste, entonces contactese con el personal técnico.")
                    .setTitle("¡Lo sentimos!");

            construirAlerta.setNeutralButton("Ok.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}});

            AlertDialog ejecutarMensaje = construirAlerta.create();
            ejecutarMensaje.show();
        }
    }
}