package br.ufrpe.ikaro.openmap;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.inlocomedia.android.InLocoMedia;
import com.inlocomedia.android.InLocoMediaOptions;
import com.inlocomedia.android.ads.AdError;
import com.inlocomedia.android.ads.AdRequest;
import com.inlocomedia.android.ads.interstitial.InterstitialAd;
import com.inlocomedia.android.ads.interstitial.InterstitialAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;

public class ActPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, Button.OnClickListener,
        Response.Listener<JSONObject>, Response.ErrorListener, StringFragment.OnListFragmentInteractionListener{

    private FragmentManager fragmentManager;
    private Requests r;
    private static LatLng latLng;
    private Button btnBuscar;
    private JSONObject response;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        r = Requests.getInstance(this); //classe de requisições JSON
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, new MapsFragment(), "MapsFragment");
        transaction.commitAllowingStateLoss();

        //configuração de anúncios da InLoco Media
        InLocoMediaOptions options = InLocoMediaOptions.getInstance(this);
        options.setAdsKey("584ba5d2b311d256f0d05ce9cd38201822f5888b7c6cc8bbc6d92232bc67a305");
        options.setLogEnabled(true);
        options.setDevelopmentDevices("20B4A55276DFC75F9BBCBF42396745"); // 96CE8466EF87BD45A85D703D8F49EE7
        InLocoMedia.init(this, options);
    }

    //método para exibir anúncio Interstitial da InLoco Media
    public void mostrarAd(){

        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setInterstitialAdListener(new InterstitialAdListener() {

            @Override
            public void onAdReady(final InterstitialAd ad) {
                ad.show();
            }

            @Override
            public void onAdError(InterstitialAd ad, AdError error) {
                Log.w("InLocoMedia", "Your interstitial has failed with error: " + error);
            }
        });

        AdRequest adRequest = new AdRequest();
        interstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        //toque duplo no botão Back para sair do app
        if(fragmentManager.getBackStackEntryCount() == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Por favor, pressione Voltar novamente para sair.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        }else{
            super.onBackPressed();
            btnBuscar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void setLatLng(LatLng latLng1){
        latLng = latLng1;
    }

    private void showFragment(Fragment frag, String name){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, frag, name);
        transaction.addToBackStack(name);
        transaction.commit();
    }

    //mostrar informaçoes da cidade selecionada por meio de uma Pop-Up
    private void mostrarInfo(String cidade) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        JSONArray lista = response.getJSONArray("list");
        double temp_max = 0;
        double temp_min = 0;
        String descr = null;

        builder.setTitle(cidade); //define o título do pop-up

        for (int i=0; i<15; i++)
            if(lista.getJSONObject(i).getString("name").equals(cidade)){
                JSONObject main = lista.getJSONObject(i).getJSONObject("main");
                temp_max= Double.parseDouble(main.getString("temp_max")) -273.15;  //-273.15 por causa da conversão de Kelvin para Celsius
                temp_min= Double.parseDouble(main.getString("temp_min")) -273.15;
                descr=lista.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description");
                break;
            }


        builder.setMessage("Temperatura Máxima: "+String.format("%.2f",temp_max)+"ºC \n" +
                "Temperatura Mínima: "+ String.format("%.2f",temp_min)+"ºC \n" +
                "Descrição do Tempo: "+ descr+".");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mostrarAd();
            }
        });
        AlertDialog alerta = builder.create();
        alerta.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.nav_mapa:
                showFragment(new MapsFragment(), "MapsFragment");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Evento onClick para o botão btnBuscar
    @Override
    public void onClick(View v) {

       if (latLng == null){
            Toast.makeText(this, "Por favor, posicione um Marcador com um toque longo no mapa." , Toast.LENGTH_SHORT).show();
        }else {
           String url = "http://api.openweathermap.org/data/2.5/find?lat=" + latLng.latitude + "&lon=" + latLng.longitude + "&cnt=15&APPID=" + getString(R.string.weatherKey);
           r.getObject(url, this, this);
           Toast.makeText(this,"Pesquisando, por favor aguarde.", LENGTH_LONG).show();
        }
    }


    //evento resposta com erro da requisição JSON
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this,"Erro de conexão, verifique se está conectado à internet.", LENGTH_LONG).show();
    }

    //evento resposta com sucesso da requisição JSON
    @Override
    public void onResponse(JSONObject response) {
        showFragment(new StringFragment(), "Lista de Cidades Próximas");
        StringFragment.setResponse(response);
        this.response = response;
        btnBuscar.setVisibility(View.GONE);
    }

    @Override
    public void onResume(){
        super.onResume();
        //btnBuscar.setVisibility(View.VISIBLE);
    }


    //evento de seleção na lista de cidades
    @Override
    public void onListFragmentInteraction(String item) throws JSONException {
        mostrarInfo(item);
    }
}
