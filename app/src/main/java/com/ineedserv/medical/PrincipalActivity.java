package com.ineedserv.medical;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineedserv.medical.Clases.Ofertantes;
import com.ineedserv.medical.Clases.Usuario;
import com.ineedserv.medical.ManejadorCuentas.LoginActivity;
import com.ineedserv.medical.MensajesError.SinConexion;
import com.ineedserv.medical.MensajesError.SinTarjetaSim;
import com.ineedserv.medical.MensajesError.SinUbicacion;



public class PrincipalActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    DatabaseReference refFB, refFB1, refFB2;
    private FirebaseAuth auth;
    Usuario userCon;
    String[] servicios;
    Ofertantes[] ofertantes;
    Ofertantes ofertante;

    LocationManager locationManager1;

    private GoogleMap mMap;
    int MY_LOCATION_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
        //        MY_LOCATION_REQUEST_CODE);

       if (!isOnlineNet()  ) {
            Intent sinConexion = new Intent(PrincipalActivity.this, SinConexion.class);
            startActivityForResult(sinConexion, 0);
            finish();
        } else
        if (!checkLocation()) {
            Intent sinUbicacion = new Intent(PrincipalActivity.this, SinUbicacion.class);
            startActivityForResult(sinUbicacion, 0);
            finish();
        } else if (getPhoneNumber() != TelephonyManager.SIM_STATE_READY ) {
            Intent sinTarjetaSim = new Intent(PrincipalActivity.this, SinTarjetaSim.class);
            startActivityForResult(sinTarjetaSim, 0);
            finish();
        } else {

            auth = FirebaseAuth.getInstance();
            final FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                // obtiene el usuario
                refFB = FirebaseDatabase.getInstance().getReference();
                refFB.child("Usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                //refFB.child("Usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userCon = new Usuario();
                        userCon = dataSnapshot.getValue(Usuario.class);
                        if (userCon.getTipoUsuario().equals("1")) {
                            // obtiene las especialidades
                            refFB1 = FirebaseDatabase.getInstance().getReference();
                            refFB1 = refFB1.child("Especialidades");
                            refFB1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                    int nServicios = (int) dataSnapshot1.getChildrenCount();
                                    int x = 0;
                                    servicios = new String[nServicios];
                                    for (DataSnapshot data : dataSnapshot1.getChildren()) {
                                        String key = data.getKey();
                                        for (DataSnapshot data2 : data.getChildren()) {
                                            String valor = data2.getValue(String.class);
                                            servicios[x++] = valor;
                                        }
                                    }
                                    Intent actividadLogin;
                                    actividadLogin = new Intent(PrincipalActivity.this, MainActivity.class);
                                    actividadLogin.putExtra("usuario", userCon);
                                    actividadLogin.putExtra("servicio", servicios);
                                    startActivity(actividadLogin);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // obtiene al ofertante
                            refFB1 = FirebaseDatabase.getInstance().getReference();
                            refFB1 = refFB1.child("Ofertantes/" + user.getUid());
                            refFB1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    ofertante = dataSnapshot2.getValue(Ofertantes.class);

                                    refFB2 = FirebaseDatabase.getInstance().getReference();
                                    refFB2 = refFB2.child("Especialidades");
                                    refFB2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot1) {
                                            int nServicios = (int) dataSnapshot1.getChildrenCount();
                                            int x = 0;
                                            servicios = new String[nServicios];
                                            for (DataSnapshot data : dataSnapshot1.getChildren()) {
                                                String key = data.getKey();
                                                for (DataSnapshot data2 : data.getChildren()) {
                                                    String valor = data2.getValue(String.class);
                                                    servicios[x++] = valor;
                                                }
                                            }
                                            Intent actividadLogin;
                                            actividadLogin = new Intent(PrincipalActivity.this, MainActivity.class);
                                            actividadLogin.putExtra("usuario", userCon);
                                            actividadLogin.putExtra("servicio", servicios);
                                            actividadLogin.putExtra("ofertante", ofertante);
                                            startActivity(actividadLogin);
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),"0",Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // obtiene las especialidades
                refFB1 = FirebaseDatabase.getInstance().getReference();
                refFB1 = refFB1.child("Especialidades");
                refFB1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        int nServicios = (int) dataSnapshot1.getChildrenCount();
                        int x = 0;
                        servicios = new String[nServicios];
                        for (DataSnapshot data : dataSnapshot1.getChildren()) {
                            String key = data.getKey();
                            for (DataSnapshot data2 : data.getChildren()) {
                                String valor = data2.getValue(String.class);
                                servicios[x++] = valor;
                            }
                        }
                        Intent actividadLogin;
                        actividadLogin = new Intent(PrincipalActivity.this, LoginActivity.class);
                        actividadLogin.putExtra("servicio", servicios);
                        startActivity(actividadLogin);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    /* para verificar si existe conexion a internet*/
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkLocation() {
       locationManager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager1.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private int getPhoneNumber() {
        TelephonyManager mTelephonyManager;
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyManager.getSimState();
    }

    /* para que se salga de la aplicacion estando en otra actividad*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            System.exit(0);
            finish();
        }
    }


}
