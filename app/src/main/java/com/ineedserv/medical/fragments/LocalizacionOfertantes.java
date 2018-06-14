package com.ineedserv.medical.fragments;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.ineedserv.medical.Clases.Bandeja;
import com.ineedserv.medical.Clases.Base_datos;
import com.ineedserv.medical.Clases.Ofertantes;
import com.ineedserv.medical.Clases.Usuario;
import com.ineedserv.medical.MainActivity;
import com.ineedserv.medical.PermissionUtils;
import com.ineedserv.medical.R;
import com.ineedserv.medical.Clases.Solicitudes;
import static java.lang.Math.round;
import static com.ineedserv.medical.R.id.map;

/**
 * Created by andrade on 05-07-17.
 * maneja la pantalla de ofertantes aquellos que ofrecen sus servicios
 * esta es la pantalla del cliente
 */


public class LocalizacionOfertantes extends Fragment implements
        SeekBar.OnSeekBarChangeListener,
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    int MY_LOCATION_REQUEST_CODE;
    LocationManager locationManager1;

    public double lat, lon;
    LatLng direccion;

    /* variables para el manejo del autocompleteextView*/
    Button limpiar;
    Button solicitalo;  // boton de solicitud
    Button verSolicitudes;
    AutoCompleteTextView textView;
    ArrayAdapter<String> adaptadorServicios;

    Usuario userCon;
    String[] servicios;
    Ofertantes[] ofertantes;

    /* variables para el manejo de la barra de distancia*/
    SeekBar seek;
    TextView valor;
    int distanciaDeterminada;

    /* declaraciones para el manejo de base de datos*/
    public SQLiteDatabase db;
    public static final int VERSION = 1;
    public int idSolicitudes;
    public Solicitudes[] solicitud = new Solicitudes[150];

    /* declaracion para manejo de medicos seleccionados*/
    private Context context;
    //private GoogleMap googleMap;

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    String zipCode;
    String code;
    String nroCelular;
    String nombre;
    String fbUid;
    String correo;
    String nroOfertanteConfirmado;

    private ProgressDialog progress;

    /* variables para grabar en basse de datos remota*/
    //String direccion ="http://190.129.95.187/aineed/"; //URL del servicio WEB creado en e l servidor
    FirebaseDatabase dbFB, dbFBDr;
    DatabaseReference refFB, refFB2, refFBDr, refFBcancelado;
    public int cantidadOfertantes;
    ArrayAdapter<String> adapter1;

    String urlBdBandeja, urlBdSolicitud;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_localizacion_ofertantes, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        context = getActivity().getApplicationContext();

        MainActivity activity = (MainActivity) getActivity();
        zipCode = activity.getZipCode();
        nombre = activity.getNombre();
        nroCelular = activity.getNroCelular();
        fbUid = activity.getFbUid();
        correo = activity.getCorreo();
        // recupera los parametros enviados
        userCon = activity.getUserCon();
        servicios = activity.getServicios();

// Get the string array carga la lista borrar
        String[] especialidades = getResources().getStringArray(R.array.servicios_array);

        // carga la lista con los servicios ofrecidos
        textView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete_servicios);
        adapter1 = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_1, especialidades);
        // librar       android.R.layout.simple_list_item_1, servicios);
        textView.setThreshold(3);
        textView.setAdapter(adapter1);

        code = zipCode;

        /* estado inicial de la pantalla del solicitante...
           se muestran las solicitudes realizadas si existieran
         */

        String fechaSolicitud;
        fechaSolicitud = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
        // Read from the database
        dbFB = FirebaseDatabase.getInstance();
        refFB = dbFB.getReference("Solicitudes/" + fbUid + "/" + fechaSolicitud);
        refFB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String key;
                String fecha;
                Solicitudes solicitudes;
                if (snapshot.getValue() != null) {
                    fecha = snapshot.getKey();
                    GenericTypeIndicator<HashMap<String, Solicitudes>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Solicitudes>>() {
                    };
                    Map<String, Solicitudes> objectHashMap = snapshot.getValue(objectsGTypeInd);
                    ArrayList<Solicitudes> objectArrayList = new ArrayList<Solicitudes>(objectHashMap.values());
                    ArrayList<String> objectKey = new ArrayList<String>(objectHashMap.keySet());
                    int nroregistros = objectHashMap.size();
                    for (int x = 0; x < nroregistros; x = x + 1) {
                        key = objectKey.get(x);
                        objectArrayList.get(x);
                        urlBdBandeja = "Bandeja/"+ objectArrayList.get(x).getIdDr()+"/"+fecha+"/"+key;
                        urlBdSolicitud = "Solicitudes/"+objectArrayList.get(x).getIdPcte()+"/"+fecha+"/"+key;
                        cargaMarcasMapaFB(objectArrayList.get(x),urlBdBandeja,urlBdSolicitud);

                        /* muestra la notificacion*/
                        if (objectArrayList.get(x).getEstado().equals("ACEPTADA")) {
                            int notid = 001;
                            Intent intent = new Intent(getContext(), LocalizacionSolicitantes.class);
                            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                                    0,
                                    intent,
                                    PendingIntent.FLAG_ONE_SHOT);

                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.alert_icon)
                                    .setContentTitle(getString(R.string.app_name)) //"iNeed"
                                    .setContentText(getString(R.string.locoferDr) + objectArrayList.get(x).getNombreDr() + getString(R.string.locoferacepto))
                                    .setAutoCancel(true)
                                    .setSound(soundUri)
                                    .setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(notid, notificationBuilder.build());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Value is: ", Toast.LENGTH_LONG).show();
            }
        });


        /* pone invisible el boton limpiar y el boton solicitar*/
        limpiar = (Button) view.findViewById(R.id.limpiar);
        limpiar.setVisibility(View.INVISIBLE);
        solicitalo = (Button) view.findViewById(R.id.botonSolocitar);
        solicitalo.setVisibility(View.INVISIBLE);
        verSolicitudes = (Button) view.findViewById(R.id.botonVerSolicitudes);

        /* boton de limpiar*/
        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText("");
                mMap.clear();
            }
        });


        refFB2 = FirebaseDatabase.getInstance().getReference();
        refFB2 = refFB2.child("Ofertantes");
        refFB2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nOfertantes = (int) dataSnapshot.getChildrenCount();
                int x = 0;
                ofertantes = new Ofertantes[nOfertantes];
                for (DataSnapshot data3 : dataSnapshot.getChildren()) {
                    String key = data3.getKey();
                    Ofertantes ofer = data3.getValue(Ofertantes.class);
                    /* OAS 27/04/2018  se comenta este paedazo de codigo para que se muestren los doctores que se encuentran cerca del paciente.*/
                    if ( /*(ofer.getPais().equals(userCon.getPais()))&&*/
                            (ofer.getEstado().equals("AC")) ) {
                        cantidadOfertantes = x + 1;
                        ofertantes[x++] = ofer;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*prepara el listener para las acciones con la lista*/
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something when the user clicks
                InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                base(ofertantes);
                if (idSolicitudes > 0) {
                    limpiar.setVisibility(View.VISIBLE);
                    solicitalo.setVisibility(View.VISIBLE);
                } else {

                    limpiar.setVisibility(View.INVISIBLE);
                    solicitalo.setVisibility(View.INVISIBLE);
                }
            }
        });

        //cuando introduce letras en el campo de especialidades
        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    limpiar.setVisibility(View.VISIBLE);
                    solicitalo.setVisibility(View.VISIBLE);

                } else {
                    limpiar.setVisibility(View.INVISIBLE);
                    solicitalo.setVisibility(View.INVISIBLE);
                }
            }
        });
        /* muestra la lista al presionar en el campo*/
        textView.setThreshold(1);       //will start working from first character
        textView.setAdapter(adapter1);   //setting the adapter data into the AutoCompleteTextView
        //Shows drop down list on touch
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textView.showDropDown();
                return false;
            }
        });

        //close button visibility for autocomplete text view selection
        textView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                limpiar.setVisibility(View.VISIBLE);
                solicitalo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                limpiar.setVisibility(View.INVISIBLE);
                solicitalo.setVisibility(View.INVISIBLE);
            }

        });

        /* configuracion de la barra de busqueda que sirve para
        definir la distancia de busqueda de medicos*/
        seek = (SeekBar) view.findViewById(R.id.seek);
        valor = (TextView) view.findViewById(R.id.valor);
        seek.setOnSeekBarChangeListener(this);

        /* accion para la presion del boton solicita*/
        solicitalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strMinActual;
                int intMinActual,horaInicial;

                String horaactual = (DateFormat.format("HH:mm:ss", new java.util.Date()).toString());
                horaInicial = Integer.parseInt(horaactual.substring(0, 2));
                strMinActual = horaactual.substring(3, 5);
                intMinActual = Integer.parseInt(strMinActual);
                if ((horaInicial==20)&&(intMinActual>15)){
                    Toast.makeText(getActivity(), R.string.imposibleCitas,Toast.LENGTH_LONG).show();
                } else if (horaInicial>20) {
                    Toast.makeText(getActivity(), R.string.imposibleCitas,Toast.LENGTH_LONG).show();
                }else{
                    grabaSolicitudesFB(idSolicitudes);
                    solicitalo.setVisibility(View.INVISIBLE);
                    //verSolicitudes.callOnClick();
                }
            }
        });
        /* accion para la presion del boton ver solicitudes*/
        verSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verSolicitudes.getText().equals(getString(R.string.locoferVersolicitud))) {
                    limpiar.setVisibility(View.INVISIBLE);
                    solicitalo.setVisibility(View.INVISIBLE);
                    verSolicitudes.setText(R.string.locoferLimpiar);
                    textView.setText("");
                    textView.setVisibility(View.INVISIBLE);
                    seek.setVisibility(View.INVISIBLE);
                    valor.setVisibility(View.INVISIBLE);
                    recuperaSolicitudes();
                }else{
                    mMap.clear();
                    verSolicitudes.setText(R.string.locoferVersolicitud);
                    textView.setText("");
                    textView.setVisibility(View.VISIBLE);
                    seek.setVisibility(View.VISIBLE);
                    valor.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }
/* fin del programa principal*/


    private void grabaSolicitudesFB(int idSolicitudes) {

        // obtiene la instancia para la base de datos
        dbFB = FirebaseDatabase.getInstance();
        refFB = dbFB.getReference("Solicitudes");
        dbFBDr = FirebaseDatabase.getInstance();
        refFBDr = dbFBDr.getReference("Bandeja");
        int x;
        for (x = 0; x < idSolicitudes; x++) {
            Solicitudes solicitudes = new Solicitudes(
                    solicitud[x].getNombreDr(),
                    solicitud[x].getNombrePcte(),
                    solicitud[x].getDistancia(),
                    solicitud[x].getServicio(),
                    solicitud[x].getLatOfertante(),
                    solicitud[x].getLonOfertante(),
                    solicitud[x].getTelefonoDr(),
                    solicitud[x].getIdDr(),
                    solicitud[x].getIdPcte(),
                    solicitud[x].getFechaSolicitud(),
                    solicitud[x].getFechaAceptacion(),
                    solicitud[x].getFechaConfirmacion(),
                    solicitud[x].getHoraCita(),
                    solicitud[x].getDireccion(),
                    solicitud[x].getEstado(),
                    solicitud[x].getCosto(),
                    solicitud[x].getExperiencia());
            String key = refFB.push().getKey();
            refFB.child(solicitud[x].getIdPcte()).
                    child(solicitud[x].getFechaSolicitud().substring(0, 10)).
                    child(key).setValue(solicitudes);

            Bandeja bandeja = new Bandeja(
                    solicitud[x].getNombreDr(),
                    solicitud[x].getNombrePcte(),
                    solicitud[x].getDistancia(),
                    solicitud[x].getServicio(),
                    lat,
                    lon,
                    nroCelular,
                    solicitud[x].getIdDr(),
                    solicitud[x].getIdPcte(),
                    solicitud[x].getFechaSolicitud(),
                    solicitud[x].getFechaAceptacion(),
                    solicitud[x].getFechaConfirmacion(),
                    solicitud[x].getHoraCita(),
                    solicitud[x].getEstado());
            String key2 = refFB.push().getKey();
            refFBDr.child(solicitud[x].getIdDr()).
                    child(solicitud[x].getFechaSolicitud().substring(0, 10)).
                    child(key).setValue(bandeja);

        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation(mMap);

        /* configuracion para trabajar con la base de datos
           esto debe ir en esta parte por que sino no funciona*/
        Base_datos crearBD = new Base_datos(context, VERSION);
        db = crearBD.getWritableDatabase();

        locationManager1 = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                UbicacionActual(lat, lon, mMap);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {   }

            public void onProviderEnabled(String provider) {    }

            public void onProviderDisabled(String provider) {   }
        };

        if (!checkLocation()) return;

        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager1.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager1.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListener);
        }/*else
        {
            locationManager1.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListener);
        }*/
        Location location = locationManager1.getLastKnownLocation(locationManager1.getBestProvider(criteria, false));
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            float zoom = 10;//13 es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } /*else {
           // se entra aqui cuando no se actualizo la ubicacion del dispositivo
            lat = 0.0; //-16.510886515734978;  // OAS eliminar estas 5 lineas
            lon = 0.0; //-68.137675524898430;
            LatLng latLng = new LatLng(lat, lon);
            float zoom = 10;//13 es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }*/

        /* cambia el estado de la solictud a CONFIRMADA*/
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final String especialidad;
                String fecha;
                String fechahora;
                int vMarca;
                String id;
                code = GetCountryZipCode();
                if (marker.getTitle().indexOf(getString(R.string.ACEPTADA)) != -1) {
                    vMarca = ((int) marker.getZIndex());
                    id = marker.getId();
                    /* Se verifica que ya se tenga cargada la tabla de ofertantes si no se traen de la base remota para cargarse de manera local */
                    Cursor relacion = db.rawQuery("SELECT urlDr, urlPct FROM relacion where id="+vMarca, null);
                    if (relacion.moveToFirst()) {
                        urlBdBandeja=relacion.getString(0);
                        urlBdSolicitud=relacion.getString(1);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.locoferNoexisteReg, Toast.LENGTH_LONG).show();
                    }
                    nroOfertanteConfirmado = marker.getTitle();
                    especialidad = nroOfertanteConfirmado.substring(0,nroOfertanteConfirmado.indexOf("\n"));
                    direccion = marker.getPosition();
                    fecha = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                    //fechahora = (DateFormat.format("hh:mm:ss", sumarRestarHorasFecha(new java.util.Date(), 1)).toString());

                    refFB = FirebaseDatabase.getInstance().getReference();
                    // Bandeja/UsrId/fecha/key/fechaConfirmacion
                    refFB.child(urlBdBandeja).child("fechaConfirmacion").setValue(fecha);
                    //refFB.child(urlBdBandeja).child("horaCita").setValue(fechahora);
                    refFB.child(urlBdBandeja).child("estado").setValue("CONFIRMADA");
                    refFB.child(urlBdSolicitud).child("fechaConfirmacion").setValue(fecha);
                    //refFB.child(urlBdSolicitud).child("horaCita").setValue(fechahora);
                    refFB.child(urlBdSolicitud).child("estado").setValue("CONFIRMADA");


                    /* pone como canceladas todas las demas solicitudes*/
                    urlBdSolicitud = urlBdSolicitud.substring(0,urlBdSolicitud.lastIndexOf('/'));
                    urlBdBandeja="";
                    // Read from the database
                    dbFB = FirebaseDatabase.getInstance();
                    refFB = dbFB.getReference(urlBdSolicitud + "/");
                    refFB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            String key;
                            String fecha;
                            if (snapshot.getValue() != null) {
                                fecha = snapshot.getKey();
                                GenericTypeIndicator<HashMap<String, Solicitudes>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Solicitudes>>() {
                                };
                                Map<String, Solicitudes> objectHashMap = snapshot.getValue(objectsGTypeInd);
                                ArrayList<Solicitudes> objectArrayList = new ArrayList<Solicitudes>(objectHashMap.values());
                                ArrayList<String> objectKey = new ArrayList<String>(objectHashMap.keySet());
                                int nroregistros = objectHashMap.size();
                                refFBcancelado = FirebaseDatabase.getInstance().getReference();
                                for (int x = 0; x < nroregistros; x = x + 1) {
                                    key = objectKey.get(x);
                                    objectArrayList.get(x);
                                    urlBdBandeja = "Bandeja/"+ objectArrayList.get(x).getIdDr()+"/"+fecha+"/"+key;
                                    urlBdSolicitud = "Solicitudes/"+objectArrayList.get(x).getIdPcte()+"/"+fecha+"/"+key;
                                    if ((!objectArrayList.get(x).getEstado().equals("CONFIRMADA"))&&
                                        (objectArrayList.get(x).getServicio().equals(especialidad))) {
                                        refFBcancelado.child(urlBdBandeja).child("estado").setValue("CANCELADA");
                                        refFBcancelado.child(urlBdSolicitud).child("estado").setValue("CANCELADA");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Value is: ", Toast.LENGTH_LONG).show();
                            // ...
                        }
                    });

                    Toast.makeText(getActivity().getApplicationContext(), R.string.ofertanteconf , Toast.LENGTH_LONG).show();
                    marker.setSnippet(getString(R.string.locofersolconfirmada));
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker
                            (BitmapDescriptorFactory.HUE_GREEN));
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());
                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
    }


    // Suma o resta las horas recibidos a la fecha
    public static Date sumarRestarHorasFecha(Date fecha, int horas) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0
        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas
    }

    /* si el solicitante tiene solicitudes elaboradas en el dia se muestran primero*/
    public void recuperaSolicitudes() {
        mMap.clear();
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.locoferrecupera));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String fechaSolicitud;
                fechaSolicitud = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
                // Read from the database
                dbFB = FirebaseDatabase.getInstance();
                refFB = dbFB.getReference("Solicitudes/" + fbUid + "/" + fechaSolicitud);
                refFB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String key;
                        String fecha;
                        Solicitudes solicitudes;
                        if (snapshot.getValue() != null) {
                            fecha = snapshot.getKey();
                            GenericTypeIndicator<HashMap<String, Solicitudes>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, Solicitudes>>() {
                            };
                            Map<String, Solicitudes> objectHashMap = snapshot.getValue(objectsGTypeInd);
                            ArrayList<Solicitudes> objectArrayList = new ArrayList<Solicitudes>(objectHashMap.values());
                            ArrayList<String> objectKey = new ArrayList<String>(objectHashMap.keySet());
                            int nroregistros = objectHashMap.size();
                            for (int x = 0; x < nroregistros; x = x + 1) {
                                key = objectKey.get(x);
                                objectArrayList.get(x);
                                urlBdBandeja = "Bandeja/"+ objectArrayList.get(x).getIdDr()+"/"+fecha+"/"+key;
                                urlBdSolicitud = "Solicitudes/"+objectArrayList.get(x).getIdPcte()+"/"+fecha+"/"+key;
                                cargaMarcasMapaFB(objectArrayList.get(x),urlBdBandeja,urlBdSolicitud);

                                /* muestra la notificacion*/
                                if (objectArrayList.get(x).getEstado().equals("ACEPTADA")) {
                                    int notid = 001;
                                    Intent intent = new Intent(getContext(), LocalizacionSolicitantes.class);
                                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                                            0,
                                            intent,
                                            PendingIntent.FLAG_ONE_SHOT);

                                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getContext())
                                            .setSmallIcon(R.drawable.alert_icon)
                                            .setContentTitle(getString(R.string.app_name))
                                            .setContentText(getString(R.string.locoferUdtieneunaSol) + objectArrayList.get(x).getEstado())
                                            .setAutoCancel(true)
                                            .setSound(soundUri)
                                            .setContentIntent(pendingIntent);
                                    NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(notid, notificationBuilder.build());
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Value is: ", Toast.LENGTH_LONG).show();
                    }
                });
                progress.cancel();
            }
        }, 500);
    }

    public void cargaMarcasMapaFB(Solicitudes solicitudes,String urlBand, String urlBdSolicitud ) {
        //mMap.clear();
        LatLng destino;
        String fecha1;
        int indice=0;

        Location location2 = new Location("localizacion 2");
        destino = new LatLng(solicitudes.getLatOfertante(), solicitudes.getLonOfertante());
        /* inserta en base de datos la referencia a bd firebase */
        ContentValues values = new ContentValues();
        values = new ContentValues();
            /* Se verifica que ya se tenga cargada la tabla de ofertantes si no se traen de la base remota para cargarse de manera local */
        Cursor nroRelacion = db.rawQuery("SELECT MAX(id) FROM relacion", null);
        if (nroRelacion.moveToFirst()) {
            indice=nroRelacion.getInt(0)+1;

        } else {
            indice =1;
        }
        values.put("id",indice);
        values.put("urlDr", urlBand);
        values.put("urlPct", urlBdSolicitud);
        db.insert("relacion", null, values);

        if (solicitudes.getEstado().equals("ACEPTADA")) {
            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title(solicitudes.getServicio()+"\n"+getString(R.string.locoferL1aceptada))
                    .snippet(getString(R.string.locofeL2aceptada)+ solicitudes.getHoraCita()
                            + getString(R.string.locoferL3aceptada)+solicitudes.getCosto()
                            + getString(R.string.locoferL4aceptada)+solicitudes.getExperiencia()
                            + getString(R.string.locoferL5aceptada)+solicitudes.getTelefonoDr()
                            + getString(R.string.locoferL6aceptada) + solicitudes.getNombreDr()
                            + getString(R.string.locoferL7aceptada)+solicitudes.getDireccion())
                    .zIndex(indice)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            float zoom = 14;//13locoferL3confirmada es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, zoom));
        } else if (solicitudes.getEstado().equals("CONFIRMADA")) {
            fecha1 = (DateFormat.format("hh:mm:ss", new java.util.Date().getTime()).toString());

            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title(getString(R.string.locoferL1confirmada)+ solicitudes.getHoraCita())
                    .zIndex(indice)
                    .snippet(getString(R.string.locoferL2confirmada) +solicitudes.getNombreDr()+
                             getString(R.string.locoferL3confirmada) +solicitudes.getTelefonoDr()+
                             getString(R.string.locoferL4confirmada)+solicitudes.getDireccion()+
                             getString(R.string.locoferL5confirmada)+solicitudes.getCosto()+
                             getString(R.string.locoferL6confirmada)+solicitudes.getDistancia()+
                             getString(R.string.locoferL7confirmada)+solicitudes.getExperiencia())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            float zoom = 15;//13locoferL3confirmada es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, zoom));
        } else if (solicitudes.getEstado().equals("CANCELADA")) {
            fecha1 = (DateFormat.format("hh:mm:ss", new java.util.Date().getTime()).toString());
/*
            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title(solicitudes.getNombreDr())
                    .zIndex(indice)
                    .snippet("Solicitud CANCELADA.")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
*/
        } else if (solicitudes.getEstado().equals("ELABORADA")) {
            mMap.addMarker(new MarkerOptions()
                    .position(destino)
                    .title(getString(R.string.locoferL1elaborada))
                    .zIndex(indice)
                    .snippet(getString(R.string.locoferL2elaborada) + solicitudes.getNombreDr() +
                             getString(R.string.locoferL3elaborada)+solicitudes.getServicio()+
                             getString(R.string.locoferL41elaborada) + solicitudes.getDistancia() + getString(R.string.locoferL42elaborada)+
                             getString(R.string.locoferL5elaborada) + solicitudes.getDireccion())); // cambiar a direccion
        }

    }


    /* coloca el foco del mapa en la ubicacion actual*/
    public void UbicacionActual(double lat, double lon, GoogleMap mMap) {
        LatLng latLng = new LatLng(lat, lon);
        float zoom = 10;//13 es optimo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }


    /* obtiene el zip code del pais*/
    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) getActivity().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    /*reacciona al movimiento del seekbar*/
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        valor.setText(getString(R.string.locoferBuscar)+ new Integer(progress).toString() + " km");
        distanciaDeterminada = progress;
        if (!textView.getText().toString().equals("")) {
            base(ofertantes);
            if (idSolicitudes > 0) {
                limpiar.setVisibility(View.VISIBLE);
                solicitalo.setVisibility(View.VISIBLE);
            } else {
                limpiar.setVisibility(View.INVISIBLE);
                solicitalo.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        // Hacer algo
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        // Hacer algo
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getApplicationContext());
        dialog.setTitle(R.string.locoferUbicacion)
                .setMessage(getString(R.string.locoferUbicL1))
                .setPositiveButton(getString(R.string.locoferubicL2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(getString(R.string.locoferUbicL3), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager1.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void clear(View view) {
        textView.setText("");
        limpiar.setVisibility(View.INVISIBLE);
    }


    /* carga los puntos de los doctores desde la base de datos despues de que se selecciona una especialidad */
    public void base(Ofertantes[] ofer) {
        String aux, aux1;
        double distance;
        long tiempo;
        mMap.clear();
        idSolicitudes = 0;
        LatLng origen;
        LatLng destino;
        String fecha;

        //Cursor ubicaciones_existentes = db.rawQuery("SELECT id,pais,instancia,correo,nombre,especialidad,latitud,longitud,direccion,datoServicio,estado FROM ofertantes", null);//where servicio='" + textView.getText().toString() + "'", null);

        int cant = cantidadOfertantes;//ofer.length;
        aux = textView.getText().toString();
        for (int x = 0; x < cant; x++) {
                /* compara con la especialidad buscada en aux1*/
            aux1 = ofer[x].getEspecialidad();
            if (aux.equals(aux1)) {
                Location location = new Location("localizacion 1");
                location.setLatitude(lat);  //latitud
                location.setLongitude(lon); //longitud
                origen = new LatLng(lat, lon);
                Location location2 = new Location("localizacion 2");
                location2.setLatitude(Double.parseDouble(ofer[x].getLatitud()));  //latitud
                location2.setLongitude(Double.parseDouble(ofer[x].getLongitud())); //longitud
                destino = new LatLng(Double.parseDouble(ofer[x].getLatitud()), Double.parseDouble(ofer[x].getLongitud()));
                distance = location.distanceTo(location2);
               // tiempo = location.getElapsedRealtimeNanos();
                distance = distance / 1000;
                distance = round(distance);
                //distance2=CalculationByDistance(origen,destino);
                if (distance <= distanciaDeterminada) {
                    //Toast.makeText(this, "distancia " + distance, Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(ofer[x].getLatitud()), Double.parseDouble(ofer[x].getLongitud())))
                            .title(getString(R.string.locoferL2elaborada) + ofer[x].getNombre() +getString(R.string.locoferA) + distance + getString(R.string.locoferKm) + ofer[x].getDireccion()));
                    solicitud[idSolicitudes] = new Solicitudes();
                    solicitud[idSolicitudes].setNombreDr(ofer[x].getNombre());
                    solicitud[idSolicitudes].setNombrePcte(nombre);
                    String dist = Double.toString(distance);
                   // dist = dist +" "+ Long.toString(tiempo);
                    solicitud[idSolicitudes].setDistancia(dist);
                    solicitud[idSolicitudes].setCosto(ofer[x].getCosto());
                    solicitud[idSolicitudes].setExperiencia(ofer[x].getExperiencia());
                    solicitud[idSolicitudes].setServicio(textView.getText().toString());
                    solicitud[idSolicitudes].setLatOfertante(Double.parseDouble(ofer[x].getLatitud()));
                    solicitud[idSolicitudes].setLonOfertante(Double.parseDouble(ofer[x].getLongitud()));
                    solicitud[idSolicitudes].setTelefonoDr(ofer[x].getInstancia());
                    solicitud[idSolicitudes].setIdDr(ofer[x].getUsuario());
                    solicitud[idSolicitudes].setIdPcte(fbUid);
                    fecha = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                    solicitud[idSolicitudes].setFechaSolicitud(fecha); // sumar  la fecha un tiempo estimado
                    solicitud[idSolicitudes].setFechaAceptacion(" ");
                    solicitud[idSolicitudes].setFechaConfirmacion(" ");
                    solicitud[idSolicitudes].setHoraCita(" ");
                    solicitud[idSolicitudes].setDireccion(ofer[x].getDireccion());
                    solicitud[idSolicitudes].setEstado("ELABORADA");
                    idSolicitudes++;
                }
            }
        }
        // Posicionar el mapa en una localización y con un nivel de zoom
        LatLng latLng = new LatLng(lat, lon);
        // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        float zoom = 10;//13 es optimo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
            //map.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation(mMap);
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


}