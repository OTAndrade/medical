package com.ineedserv.medical;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.maps.MapFragment;
import com.google.firebase.database.DatabaseReference;
import com.ineedserv.medical.Clases.Ofertantes;
import com.ineedserv.medical.Clases.Usuario;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.ineedserv.medical.fragments.LocalizacionOfertantes;
import com.ineedserv.medical.fragments.LocalizacionSolicitantes;
import com.ineedserv.medical.fragments.Pagina1;
import com.ineedserv.medical.fragments.Pagina2;
import com.ineedserv.medical.fragments.Pagina3;

//import android.support.v4.app.FragmentActivity;

public class MainActivity extends AppCompatActivity {

    public static final int DRAWER_ITEM_MAPA_OFERTANTE = 0;
    public static final int DRAWER_ITEM_MAPA_SOLICITANTE = 1;
    public static final int DRAWER_ITEM_AYUDA = 2;
    public static final int DRAWER_ITEM_CONFIGURACION = 3;
    public static final int DRAWER_ITEM_ADMUSER = 4;
    private Drawer drawer;
    public int DRAWER_SELECCIONADO=0;
    private static final String LOG = "MainActivity";
    private Context context;
    private Toolbar toolbar;

    String nombre="";
    private String email="";
    String zipCode;
    String nroCelular;
    public String tipoUsuario;
    String correo;
    String clave;
    String fbUid;
    String direccion;
    DatabaseReference refFB;
    Usuario userCon;
    String[] servicios;
    Ofertantes[] ofertantes;
    Ofertantes ofertante;
    String ayuda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Intent intent = getIntent();
        userCon = new Usuario();
        // parametros que se reciben
        userCon = (Usuario) getIntent().getSerializableExtra("usuario");
        servicios = (String[]) getIntent().getSerializableExtra("servicio");
        ofertante = (Ofertantes) getIntent().getSerializableExtra("ofertante");

        if (userCon.getTipoUsuario().equals("2")){
            direccion = ofertante.getDireccion();
            setDireccion(direccion);
            nombre = "Dr(a). "+userCon.getNombre();
        }else{
            nombre = userCon.getNombre();
        }
        zipCode = userCon.getPais();
        nroCelular = userCon.getInstancia();
        tipoUsuario = userCon.getTipoUsuario();
        clave = userCon.getContrasenia();
        correo = userCon.getCorreo();
        fbUid = userCon.getFbUid();

        setZipCode(zipCode);
        setNroCelular(nroCelular);
        setFbUid(fbUid);
        setNombre(nombre);
        setCorreo(correo);
        setServicios(servicios);
        setOfertante(ofertante);

        email=correo;

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(nombre)
                                .withEmail(email)
                                .withIcon(new IconicsDrawable(this)
                                        .icon(FontAwesome.Icon.faw_user)
                                        .color(getResources().getColor(R.color.blanco))
                                        .backgroundColor(getResources().getColor(R.color.colorAccent))
                                        .paddingDp(2)
                                        .sizeDp(20))
                )
                .withHeaderBackground(R.color.colorAccent)
                .build();

        if (tipoUsuario.equals("1")) { // si es solicitantemuestra los ofertantes
            drawer = new DrawerBuilder(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .withActionBarDrawerToggle(true)
                    .addDrawerItems(
                            new DividerDrawerItem(),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_MAPA_OFERTANTE).// MAPA DE OFERTANTES PARA LOS SOLICITANTES
                                    withName(R.string.opcion2).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_home),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_AYUDA).
                                    withName(R.string.opcion3).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_plus_square),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_ADMUSER).
                                    withName(R.string.opcion4).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_plus_square),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_CONFIGURACION).
                                    withName(R.string.opcion5).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent))//.withIcon(FontAwesome.Icon.faw_file_text)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
                            selectItem(drawerItem.getIdentifier());
                            return false;
                        }
                    })
                    .withSelectedItem(DRAWER_ITEM_MAPA_OFERTANTE)
                    .withSavedInstance(savedInstanceState)
                    .build();
            selectItem(DRAWER_ITEM_MAPA_OFERTANTE);
        } else if (tipoUsuario.equals("2")) {  // si es ofertante; muestra las solicitudes
            drawer = new DrawerBuilder(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .withActionBarDrawerToggle(true)
                    .addDrawerItems(
                            new DividerDrawerItem(),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_MAPA_SOLICITANTE). // MAPA DE SOLICITUDES PARA LOS OFERTANTES
                                    withName(R.string.opcion1).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_home),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_MAPA_OFERTANTE).
                                    withName(R.string.opcion2).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_plus_square),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_AYUDA).
                                    withName(R.string.opcion3).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_plus_square),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_ADMUSER).
                                    withName(R.string.opcion4).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent)),//.withIcon(FontAwesome.Icon.faw_plus_square),
                            new PrimaryDrawerItem().
                                    withIdentifier(DRAWER_ITEM_CONFIGURACION).
                                    withName(R.string.opcion5).
                                    withSelectedIconColor(getResources().getColor(R.color.colorAccent)).
                                    withSelectedTextColor(getResources().getColor(R.color.colorAccent))//.withIcon(FontAwesome.Icon.faw_file_text)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem drawerItem) {
                            selectItem(drawerItem.getIdentifier());
                            return false;
                        }
                    })
                    .withSelectedItem(DRAWER_ITEM_MAPA_SOLICITANTE)
                    .withSavedInstance(savedInstanceState)
                    .build();
            selectItem(DRAWER_ITEM_MAPA_SOLICITANTE);
        }
        toolbar.setTitle(R.string.title_activity_maps);
    }

    public void selectItem(int idMenu)
    {
        String titulo = "";
        Fragment f=new Pagina1();
        MapFragment mf=new MapFragment();

        Bundle args=new Bundle();

        switch (idMenu)
        {
            case DRAWER_ITEM_MAPA_OFERTANTE:
                titulo= getString(R.string.title_activity_maps);//"iNeed";
                ayuda = "ayudaSolicitante";
                f=new LocalizacionOfertantes();
                break;
            case DRAWER_ITEM_MAPA_SOLICITANTE:
                titulo=getString(R.string.title_activity_maps);
                ayuda = "ayudaOfertante";
                f=new LocalizacionSolicitantes();
                break;
            case DRAWER_ITEM_AYUDA:
                titulo=getString(R.string.opcion3);
                f=new Pagina1();
                break;
            case DRAWER_ITEM_CONFIGURACION:
                titulo=getString(R.string.opcion5);
                f=new Pagina2();
                break;
            case DRAWER_ITEM_ADMUSER:
                titulo=getString(R.string.opcion4);
                f=new Pagina3();
                break;
        }
        toolbar.setTitle(titulo);
        args.putString("param1", titulo);
        f.setArguments(args);

        FragmentManager fm = getSupportFragmentManager();
        Fragment oldFragment = getSupportFragmentManager().findFragmentById(R.id.contenedor);
        if (oldFragment != null)
        {
                fm.beginTransaction()
                        .remove(oldFragment)
                        .addToBackStack("tag")
                        .replace(R.id.contenedor, f)
                        .commit();
        }
        else
            fm.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.contenedor, f)
                    .commit();

        DRAWER_SELECCIONADO=idMenu;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(DRAWER_SELECCIONADO== DRAWER_ITEM_CONFIGURACION)
        {
            selectItem(DRAWER_ITEM_CONFIGURACION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            if (drawer.isDrawerOpen())
                drawer.closeDrawer();
            else
                drawer.openDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DRAWER_SELECCIONADO=0;
        //si no queda ningún fragment sale de este activity

        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else
        {
           // drawer.closeDrawer();
            //super.onBackPressed();
            moveTaskToBack(true);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNroCelular() {
        return nroCelular;
    }

    public void setNroCelular(String nroCelular) {
        this.nroCelular = nroCelular;
    }

    public String getFbUid() { return fbUid; }

    public void setFbUid(String fbUid) {this.fbUid = fbUid; }

    public void setCorreo(String correo) { this.correo = correo; }

    public String getCorreo() {return correo;}

    public String[] getServicios() { return servicios; }

    public void setServicios(String[] servicios) { this.servicios = servicios; }

    public Ofertantes[] getOfertantes() { return ofertantes; }

    public void setOfertantes(Ofertantes[] ofertantes) { this.ofertantes = ofertantes;}

    public Usuario getUserCon() {return userCon; }

    public void setUserCon(Usuario userCon) {this.userCon = userCon; }

    public String getDireccion() {return direccion;}

    public void setDireccion(String direccion) {this.direccion = direccion;}

    public Ofertantes getOfertante() {return ofertante; }

    public void setOfertante(Ofertantes ofertante) {this.ofertante = ofertante;}

    public String getAyuda() {return ayuda;}

    public void setAyuda(String ayuda) {this.ayuda = ayuda;}
}
