package com.ineedserv.medical.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.ineedserv.medical.MainActivity;
import com.ineedserv.medical.ManejadorCuentas.LoginActivity;
import com.ineedserv.medical.ManejadorCuentas.SingUpActivity;
import com.ineedserv.medical.PrincipalActivity;
import com.ineedserv.medical.R;

public class Pagina3 extends Fragment {

    private Button btnChangePassword,
            btnRemoveUser,
            changePassword,
            remove,
            signOut,
            volver;

    private EditText oldEmail, password, newPassword;
    private ProgressBar progressBar;

    private ProgressDialog progress;
    private FirebaseAuth auth;
    FirebaseUser user;

    private Context context;

    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        //return inflater.inflate(R.layout.pagina3, container, false);
        View view = inflater.inflate(R.layout.pagina3, container, false);


        context = getActivity().getApplicationContext();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnChangePassword = (Button) view.findViewById(R.id.change_password_button);

        btnRemoveUser = (Button) view.findViewById(R.id.remove_user_button);

        changePassword = (Button) view.findViewById(R.id.changePass);

        signOut = (Button) view.findViewById(R.id.sign_out);

        volver = (Button) view.findViewById(R.id.volver);

        oldEmail = (EditText) view.findViewById(R.id.old_email);
        oldEmail.setText(user.getEmail());

        password = (EditText) view.findViewById(R.id.password);
        newPassword = (EditText) view.findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);

        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);

        changePassword.setVisibility(View.GONE);
        volver.setVisibility(View.INVISIBLE);

        //remove.setVisibility(View.GONE);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setText(user.getEmail());
                oldEmail.setVisibility(View.VISIBLE);

                password.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);

                changePassword.setVisibility(View.VISIBLE);
                volver.setVisibility(View.VISIBLE);

                btnChangePassword.setVisibility(View.INVISIBLE);
                signOut.setVisibility(View.INVISIBLE);
                // remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = auth.getCurrentUser();
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError(getString(R.string.msgErr));
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), R.string.resetPass, Toast.LENGTH_SHORT).show();
                                            signOut();
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    System.exit(0);
                                                }
                                            }, 3000);
                                        } else {
                                            Toast.makeText(getContext(), R.string.errorpass, Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError(getString(R.string.singuppass));
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Toast.makeText(getContext(), R.string.desconectar, Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 3000);
            }
        });

        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.INVISIBLE);

                password.setVisibility(View.INVISIBLE);
                newPassword.setVisibility(View.INVISIBLE);

                changePassword.setVisibility(View.INVISIBLE);
                volver.setVisibility(View.INVISIBLE);
                btnChangePassword.setVisibility(View.VISIBLE);
                signOut.setVisibility(View.VISIBLE);

            }
        });


        btnChangePassword.setFocusable(true);

        return view;
    }


    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(getContext(), PrincipalActivity.class));
                //finish();
            }
        }
    };

    //sign out method
    public void signOut() {
        auth.signOut();
    }


}

