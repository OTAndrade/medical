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
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineedserv.medical.Clases.Bandeja;
import com.ineedserv.medical.Clases.Base_datos;
import com.ineedserv.medical.Clases.Category;
import com.ineedserv.medical.Clases.Ofertantes;
import com.ineedserv.medical.MainActivity;
import com.ineedserv.medical.PermissionUtils;
import com.ineedserv.medical.R;
import com.ineedserv.medical.Clases.Solicitudes;

import org.json.JSONArray;

import static com.ineedserv.medical.R.id.center;
import static com.ineedserv.medical.R.id.mapSolicitante;

/**
 * Created by andrade on 05-07-17.
 * maneja la pantalla de solicitantes aquellos que solicitan los servicios
 * esta es la pantalla del Medico
 */

public class LocalizacionSolicitantes extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private GoogleMap mMap;
    int MY_LOCATION_REQUEST_CODE;
    LocationManager locationManager1;
    public double lat, lon;
    LatLng direccion;

    public int idSolicitudes;
    public Solicitudes[] solicitud = new Solicitudes[150];

    /* variables para el manejo del autocompleteextView*/
    Button historial;

    /* declaraciones para el manejo de base de datos*/
    public SQLiteDatabase db;
    public static final int VERSION = 1;

    /* declaracion para manejo de medicos seleccionados*/
    private Context context;

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
    String nroCelular;
    String domicilio;
    String uid;

    String nroSolicitanteAceptado;

    private ProgressDialog progress;

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    ListView miLista;
    String despliega;
    ArrayList<Category> category;

    Category cat;

    DatabaseReference refFBDr, refFB;

    String urlBdBandeja, urlBdSolicitud;

    Bandeja[] bandeja;

    int contador = 0;

    Ofertantes ofertante;

    String horaCita = "0";
    String fecha;
    String fechahora;
    RadioGroup rdg;
    String swDialogo;

    //private PopupWindow myPopup;
    // private RelativeLayout positionOfPopup;


    private static final int NOTIF_ALERTA_ID = 1;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_localizacion_solicitantes, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(mapSolicitante);
        mapFragment.getMapAsync(this);

        context = getActivity().getApplicationContext();
        /* se recuperan variables del Mainactivity*/
        MainActivity activity = (MainActivity) getActivity();
        zipCode = activity.getZipCode();
        nroCelular = activity.getNroCelular();
        domicilio = activity.getDireccion();
        uid = activity.getFbUid();
        ofertante = activity.getOfertante();

        String fechaSolicitud;
        fechaSolicitud = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
        /* definicion para la lista de historial*/
        category = new ArrayList<Category>();
        miLista = (ListView) view.findViewById(R.id.lista);
        //category.clear();

        /* listener para actualizar las solicitudes*/
        refFB = FirebaseDatabase.getInstance().getReference();
        refFB = refFB.child("Bandeja/" + uid + "/" + fechaSolicitud);
        refFB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nBandeja = (int) dataSnapshot.getChildrenCount();
                int x = 0;
                String mensaje = "";
                category.clear();
                boolean muestraNotificacion = false;
                bandeja = new Bandeja[nBandeja];
                for (DataSnapshot data3 : dataSnapshot.getChildren()) {
                    String key = data3.getKey();
                    Bandeja band = data3.getValue(Bandeja.class);
                    cat = new Category();
                    cat.setDescription(getString(R.string.locsolL1aceptada) + band.getNombrePcte());
                    cat.setCategoryId(contador);
                    if (band.getEstado().equals("ACEPTADA")) {
                        cat.setTittle(getString(R.string.locsolL2aceptada) + getString(R.string.ESTACEPTADA) + getString(R.string.locsolL21aceptada) + band.getFechaAceptacion());
                        //img = R.drawable.alfiler;
                        cat.setImagen(null);
                        muestraNotificacion = false;
                    } else if (band.getEstado().equals("CONFIRMADA")) {
                        cat.setTittle(getString(R.string.locsolL2aceptada) + getString(R.string.ESTCONFIRMADA) + getString(R.string.locsolL1confirmada) + band.getHoraCita());
                        cat.setImagen(null);
                        muestraNotificacion = true;
                        mensaje = getString(R.string.locsolL2confirmada);
                    } else if (band.getEstado().equals("CANCELADA")) {
                        cat.setTittle(getString(R.string.locsolL2aceptada) + getString(R.string.ESTCANCELADA));
                        cat.setImagen(null);
                        muestraNotificacion = false;
                    } else if (band.getEstado().equals("ELABORADA")) {
                        cat.setTittle(getString(R.string.locsolL2aceptada) + getString(R.string.ESTELABORADA) + getString(R.string.locsolL21aceptada) + band.getFechaSolicitud());
                        cat.setImagen(null);
                        muestraNotificacion = true;
                        mensaje = getString(R.string.locsolL1elaborada);
                    }
                    category.add(cat);
                    contador++;

                    if (muestraNotificacion) {
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
                                .setContentText(mensaje)
                                .setAutoCancel(true)
                                .setSound(soundUri)
                                .setContentIntent(pendingIntent);
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(notid, notificationBuilder.build());
                        muestraNotificacion = false;
                    }
                }
                AdapterItem adapter = new AdapterItem(getActivity(), category); //new AdapterItem(this, category);
                miLista.setAdapter(adapter);
                miLista.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        despliega = "0";
        historial = (Button) view.findViewById(R.id.historial);
        historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (despliega.equals("0")) {
                    if (miLista.getCount() > 0) {
                        miLista.setVisibility(view.VISIBLE);
                        historial.setText(R.string.locsolMapa);
                        despliega = "1";
                    } else {
                        Toast.makeText(getContext(), R.string.sinLista, Toast.LENGTH_LONG).show();
                    }
                } else {
                    miLista.setVisibility(view.INVISIBLE);
                    historial.setText(R.string.solicitanteshist);
                    despliega = "0";
                }
            }
        });
        return view;
    }
/* fin del programa principal*/


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation(mMap);
        /* configuracion para trabajar con la base de datos
           esto debe ir en esta parte por que sino no funciona*/
        Base_datos crearBD = new Base_datos(context, VERSION);
        db = crearBD.getWritableDatabase();

        String fechaSolicitud;
        fechaSolicitud = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
        refFBDr = FirebaseDatabase.getInstance().getReference();
        refFBDr = refFBDr.child("Bandeja/" + uid + "/" + fechaSolicitud);
        //urlBdBandeja="";
        refFBDr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int nBandeja = (int) dataSnapshot.getChildrenCount();
                int x = 0;
                String key = dataSnapshot.getKey();
                Bandeja[] bandejas = new Bandeja[nBandeja];
                for (DataSnapshot data3 : dataSnapshot.getChildren()) {
                    //for (DataSnapshot data4 : data3.getChildren()) {
                    Bandeja band = data3.getValue(Bandeja.class);
                    String key2 = data3.getKey();
                    urlBdBandeja = "Bandeja/" + band.getIdDr() + "/" + key + "/" + key2;
                    urlBdSolicitud = "Solicitudes/" + band.getIdPcte() + "/" + key + "/" + key2;
                    base(band, urlBdBandeja, urlBdSolicitud); /* carga los puntos de los solicitantes */
                    //UbicacionActual(mMap); /* carga la ubicacion del ofertante */
                    //}
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // determina el punto por defecto o la ubicacion actual del solicitante
        // Acquire a reference to the system Location Manager
        locationManager1 = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (!checkLocation())
            return;
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        locationManager1.removeUpdates(locationListener);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager1.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager1.requestLocationUpdates(provider, 2 * 20 * 1000, 10, locationListener);
        }

        Location location = locationManager1.getLastKnownLocation(locationManager1.getBestProvider(criteria, false));
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            float zoom = 10;//13 es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        } else {
            lat = 0.0; //-16.510886515734978;  // OAS eliminar estas 5 lineas
            lon = 0.0; //-68.137675524898430;
            //Toast.makeText(getContext(), "latitudes cero", Toast.LENGTH_LONG).show();
            LatLng latLng = new LatLng(lat, lon);
            float zoom = 10;//13 es optimo
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                int vMarca;
                String id;
                if (marker.getTitle().substring(0, marker.getTitle().indexOf("\n")).equals(getString(R.string.SOLICITUDELABORADA))) {
                    vMarca = ((int) marker.getZIndex());
                    id = marker.getId();
                    swDialogo="0";
                     /* Se verifica que ya se tenga cargada la tabla de ofertantes si no se traen de la base remota para cargarse de manera local */
                    Cursor relacion = db.rawQuery("SELECT urlDr, urlPct FROM relacion where id=" + vMarca, null);
                    if (relacion.moveToFirst()) {
                        urlBdBandeja = relacion.getString(0);
                        urlBdSolicitud = relacion.getString(1);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.locsolErrnoexisteReg, Toast.LENGTH_LONG).show();
                    }

                    direccion = marker.getPosition();
                    fecha = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                    /* verificar que la hora sea en formato de 24 horas*/
                    fechahora = (DateFormat.format("HH:mm:ss", new java.util.Date()).toString());
                    //fechahora = "19:55:30";
                    /* calculamos la hora*/
                    String strMinActual;
                    String fechaInicial = "";
                    strMinActual = fechahora.substring(3, 5);

                    int intMinActual, horaInicial;
                    intMinActual = Integer.parseInt(strMinActual);

                    if ((intMinActual >= 0) && (intMinActual <= 15)) {
                        fechaInicial = fechahora.substring(0, 2) + ":30:00";
                    } else if ((intMinActual > 15) && (intMinActual <= 45)) {
                        horaInicial = Integer.parseInt(fechahora.substring(0, 2));
                        if (horaInicial < 9) {
                            horaInicial = horaInicial + 1;
                            fechaInicial = "0" + Integer.toString(horaInicial) + ":00:00";
                        } else {
                            horaInicial = horaInicial + 1;
                            fechaInicial = Integer.toString(horaInicial) + ":00:00";
                        }
                    } else if ((intMinActual >= 45) && (intMinActual <= 59)) {
                        horaInicial = Integer.parseInt(fechahora.substring(0, 2));
                        if (horaInicial < 9) {
                            horaInicial = horaInicial + 1;
                            fechaInicial = "0" + Integer.toString(horaInicial) + ":30:00";
                        } else {
                            horaInicial = horaInicial + 1;
                            fechaInicial = Integer.toString(horaInicial) + ":30:00";
                        }
                    }

                    AlertDialog.Builder mbuilder = new AlertDialog.Builder(getContext());
                    View mView = getActivity().getLayoutInflater().inflate(R.layout.popuphora, null);
                    rdg = (RadioGroup) mView.findViewById(R.id.rdg);

                    RadioGroup rgp = (RadioGroup) mView.findViewById(R.id.rdg);
                    int buttons = 4;
                    for (int i = 1; i <= buttons; i++) {
                        RadioButton rbn = new RadioButton(getContext());
                        rbn.setId(i + 1000);
                        rbn.setText(fechaInicial);
                        rgp.addView(rbn);
                        fechaInicial = sumaHoras(fechaInicial);
                        if (fechaInicial.equals("21:00:00")) {
                            i = 5;
                        } else if (fechaInicial.equals("13:00:00")) {
                            fechaInicial = "14:30:00";
                        }
                    }
                    mbuilder.setView(mView);

                    rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (group.findViewById(checkedId) != null) {
                                RadioButton rb = ((RadioButton) group.findViewById(checkedId));
                                horaCita = rb.getText().toString();
                            }
                        }
                    });

                    mbuilder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (!horaCita.equals("0")) {
                                String fechaAceptacion = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());
                                refFB = FirebaseDatabase.getInstance().getReference();
                                // Bandeja/UsrId/fecha/key/fechaAceptacion
                                refFB.child(urlBdBandeja).child("fechaAceptacion").setValue(fechaAceptacion);
                                refFB.child(urlBdBandeja).child("horaCita").setValue(horaCita);
                                refFB.child(urlBdBandeja).child("estado").setValue("ACEPTADA");
                                refFB.child(urlBdSolicitud).child("fechaAceptacion").setValue(fechaAceptacion);
                                refFB.child(urlBdSolicitud).child("horaCita").setValue(horaCita);
                                refFB.child(urlBdSolicitud).child("estado").setValue("ACEPTADA");
                                swDialogo="1";
                                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.popup_acep_ok)+horaCita, Toast.LENGTH_LONG).show();
                                dialog.cancel(); //Cierra dialogo.
                            } else {
                                Toast.makeText(getContext(), R.string.popup_aceptar,Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    mbuilder.setNegativeButton(R.string.locoferUbicL3, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            swDialogo="2";
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = mbuilder.create();
                    dialog.show();
                    Button nbutton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                    Button pbutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) nbutton.getLayoutParams();

                    layoutParams.weight =10;
                    nbutton.setLayoutParams(layoutParams);
                    pbutton.setLayoutParams(layoutParams);

                    if (swDialogo.equals("1")) {
                        direccion = marker.getPosition();
                        marker.setSnippet(getString(R.string.locsolAceptada));
                        marker.setSnippet(getString(R.string.locsolconfirma));
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    }
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
        UbicacionActual(mMap); /* carga la ubicacion del ofertante */
    }

    /**
     * fin onmapready
     */


    public String sumaHoras(String hora) {
        String min = "";
        min = hora.substring(3, 5);
        if (min.equals("00")) {
            return hora.substring(0, 3) + "30:00";
        } else {
            int hh = Integer.parseInt(hora.substring(0, 2));
            if (hh < 9) {
                hh = hh + 1;
                return "0" + Integer.toString(hh) + ":00:00";
            } else {
                /*if (hh>=20) {
                    hh = 9;
                    return  "0" + Integer.toString(hh) + ":00:00";
                } else {*/
                hh = hh + 1;
                return Integer.toString(hh) + ":00:00";
                //}
            }
        }
    }

    /* carga los puntos de las solictudes relacionadas con la especialidad del ofertante */
    public void base(Bandeja band, String urlBand, String urlBdSolicitud) {
        mMap.clear();
        idSolicitudes = 0;
        LatLng destino;
        String fechaActual;
        int indice = 0;

        /* obtienes la fecha para poder calcular las atenciones*/
        Calendar calendar = Calendar.getInstance();
        fechaActual = simpleDateFormat.format(calendar.getTime());

        if (!band.getEstado().equals("CANCELADA")) {
            //aux=quantityTextView.getText().toString();
            //          do {
            Location location2 = new Location("localizacion 2");
            location2.setLatitude(band.getLatSolicitante()); //latitud
            location2.setLongitude(band.getLonSolicitante()); //longitud
            destino = new LatLng(band.getLatSolicitante(), band.getLonSolicitante());
            /* inserta en base de datos la referencia a bd firebase */
            ContentValues values = new ContentValues();
            values = new ContentValues();
            /* Se verifica que ya se tenga cargada la tabla de ofertantes si no se traen de la base remota para cargarse de manera local */
            Cursor nroRelacion = db.rawQuery("SELECT MAX(id) FROM relacion", null);
            if (nroRelacion.moveToFirst()) {
                indice = nroRelacion.getInt(0) + 1;

            } else {
                indice = 1;
            }
            values.put("id", indice);
            values.put("urlDr", urlBand);
            values.put("urlPct", urlBdSolicitud);
            db.insert("relacion", null, values);


            if (band.getEstado().equals("ACEPTADA")) {
                mMap.addMarker(new MarkerOptions()
                        .position(destino)
                        .title(getString(R.string.locsolAceptada))
                        .snippet(getString(R.string.locsolL1acepta) + band.getNombrePcte())
                        .zIndex(indice)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_YELLOW)));
                float zoom = 14;//13locoferL3confirmada es optimo
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, zoom));
            } else if (band.getEstado().equals("CONFIRMADA")) {
                mMap.addMarker(new MarkerOptions()
                        .position(destino)
                        .title(getString(R.string.locsolL1confirma)+ band.getHoraCita())
                        .snippet(getString(R.string.locsolL2confirma) + band.getNombrePcte()
                                + getString(R.string.locsolL3confirma) + band.getTelefonoPcte())
                        .zIndex(indice)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_GREEN)));
                float zoom = 15;//13locoferL3confirmada es optimo
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, zoom));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(destino)
                        .title(getString(R.string.locsolL1else))
                        .snippet(getString(R.string.locsolL2else) + band.getNombrePcte())
                        .zIndex(indice)
                        .visible(true)
                );
                float zoom = 10;//13locoferL3confirmada es optimo
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, zoom));
            }

        }
    }


    /* carga la ubicacion del ofertante */
    public void UbicacionActual(GoogleMap mMap) {

        String latitud, longitud;
        latitud = ofertante.getLatitud();
        longitud = ofertante.getLongitud();

        lat = Double.parseDouble(latitud);
        lon = Double.parseDouble(longitud);

        LatLng latLng = new LatLng(lat, lon); // Un zoom mayor que 13 hace que el emulador falle, pero un valor deseado para
        // callejero es 17 aprox.
        float zoom = 10;//13 es optimo
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.locsolMiconsultorio))
                .snippet("")
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_AZURE)));
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

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity().getApplicationContext());
        dialog.setTitle(R.string.locoferUbicacion)
                .setMessage(R.string.locoferUbicL1)
                .setPositiveButton(getString(R.string.locsolConfubic), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(R.string.locoferUbicL3, new DialogInterface.OnClickListener() {
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