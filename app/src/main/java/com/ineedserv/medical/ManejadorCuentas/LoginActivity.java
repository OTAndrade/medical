package com.ineedserv.medical.ManejadorCuentas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ineedserv.medical.Clases.Ofertantes;
import com.ineedserv.medical.MainActivity;
import com.ineedserv.medical.R;
import com.ineedserv.medical.Clases.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private DatabaseReference refFB, refFB1;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    private FirebaseAuth.AuthStateListener authstateListener;
    Usuario userCon;

    private ProgressDialog progress;

    String[] servicios;
    Ofertantes ofertante;

    Button enlace;
    int MY_LOCATION_REQUEST_CODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        servicios = (String[]) getIntent().getSerializableExtra("servicio");

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                MY_LOCATION_REQUEST_CODE);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            refFB = FirebaseDatabase.getInstance().getReference();
            refFB.child("Usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userCon = new Usuario();
                    userCon = dataSnapshot.getValue(Usuario.class);
                    Intent actividadLogin;
                    actividadLogin = new Intent(LoginActivity.this, MainActivity.class);
                    actividadLogin.putExtra("usuario", userCon);
                    actividadLogin.putExtra("servicio", servicios);
                    startActivity(actividadLogin);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        authstateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(getApplicationContext(), R.string.usrNoconectado, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    refFB = FirebaseDatabase.getInstance().getReference("Usuarios");
                    refFB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userCon = new Usuario();
                            userCon = dataSnapshot.getValue(Usuario.class);
                            Intent actividadLogin;
                            actividadLogin = new Intent(LoginActivity.this, MainActivity.class);
                            actividadLogin.putExtra("usuario", userCon);
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
        };

        setContentView(R.layout.activity_login);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        enlace = (Button) findViewById(R.id.btn_enlace);

        enlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.ineedserv.com/ineed/web/index.php?r=site%2Fmedico");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // boton para registrarse
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadSingUp;
                actividadSingUp = new Intent(LoginActivity.this, SingUpActivity.class);
                actividadSingUp.putExtra("servicio", servicios);
                startActivity(actividadSingUp);
                finish();
            }
        });
        //boton para resetear el
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadReset;
                actividadReset = new Intent(LoginActivity.this, ResetpasswordActivity.class);
                actividadReset.putExtra("servicio", servicios);
                startActivity(actividadReset);
                finish();
            }
        });
        // boton de logeo
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.direccionEmail, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.introPass, Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = auth.getCurrentUser();
                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    FirebaseUser user = auth.getCurrentUser();
                                    refFB = FirebaseDatabase.getInstance().getReference("Usuarios");
                                    refFB.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            userCon = new Usuario();
                                            userCon = dataSnapshot.getValue(Usuario.class);

                                            refFB1 = FirebaseDatabase.getInstance().getReference();
                                            refFB1 = refFB1.child("Ofertantes/" + userCon.getFbUid());
                                            refFB1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                                    ofertante = dataSnapshot2.getValue(Ofertantes.class);
                                                    Intent actividadLogin;
                                                    actividadLogin = new Intent(LoginActivity.this, MainActivity.class);
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
                        });
            }
        });
    }
}
