package com.ineedserv.medical.ManejadorCuentas;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.firebaseloginapp.R;
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
import com.ineedserv.medical.PrincipalActivity;
import com.ineedserv.medical.R;
import com.ineedserv.medical.Clases.Usuario;

public class SingUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, celular, nombre;     //hit option + enter if you on mac , for windows hit ctrl + enter
    private TextView textzipCode;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private String fbUid,idOfertante;

    DatabaseReference refFB,refFB1;
    String[] servicios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        servicios = (String[]) getIntent().getSerializableExtra("servicio");

        textzipCode = (TextView) findViewById(R.id.zipCode);
        celular = (EditText) findViewById(R.id.celular);
        nombre = (EditText) findViewById(R.id.nombre);
        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        textzipCode.setText("+"+GetCountryZipCode());

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SingUpActivity.this, ResetpasswordActivity.class));
                Intent actividadLogin;
                actividadLogin = new Intent(SingUpActivity.this, ResetpasswordActivity.class);
                actividadLogin.putExtra("servicio", servicios);
                startActivity(actividadLogin);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actividadLogin;
                actividadLogin = new Intent(SingUpActivity.this, LoginActivity.class);
                actividadLogin.putExtra("servicio", servicios);
                startActivity(actividadLogin);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();


                if (TextUtils.isEmpty(celular.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), R.string.singupingresa, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nombre.getText().toString().trim())) {
                    Toast.makeText(getApplicationContext(), R.string.singupNombre, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), R.string.singupDireccion, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.singuppass, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.singuppasscorta, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SingUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SingUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SingUpActivity.this, getString(R.string.singupAutfail) + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseUser user = auth.getCurrentUser();
                                    fbUid = user.getUid();
                                    refFB = FirebaseDatabase.getInstance().getReference();
                                    idOfertante = " ";
                                    final Usuario usuario = new Usuario(
                                            idOfertante,
                                            GetCountryZipCode(),//zipCode,
                                            celular.getText().toString(),//nroCelular,
                                            inputEmail.getText().toString(),//email,
                                            "1",  //solicitante
                                            "AC",
                                            nombre.getText().toString(),//nombre,
                                            inputPassword.getText().toString(),//password,
                                            fbUid);

                                    refFB.child("Usuarios").child(fbUid).setValue(usuario);

                                    Intent actividadLogin;
                                    actividadLogin = new Intent(SingUpActivity.this, MainActivity.class);
                                    actividadLogin.putExtra( "usuario", usuario);
                                    actividadLogin.putExtra("servicio", servicios);
                                    startActivity(actividadLogin);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    //obtiene el zip code del pais
    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
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


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
