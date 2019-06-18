package br.com.recyclerviewaula;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Actions {

    private List<Pudim> listaPudims;
    private PudimAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;

    private static final int REQUEST_EDIT = 1;
    private static final int REQUEST_INSERT = 2;

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new GetPudimJson().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, adapter.getListaPudims().get(0).getSabor() + " " + adapter.getListaPudims().get(1).getSabor() + " " + adapter.getListaPudims().get(2).getSabor(), Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Bitmap changeSize (Bitmap photo){

        final float newSize = MainActivity.this.getResources().getDisplayMetrics().density;
        int largura;
        int altura;

        altura = (int) (120*newSize);
        largura = (int) (altura*photo.getWidth()/((double)photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, largura, altura, true);

        return photo;
    }

    private void setRecyclerView() {
        setListaPudimsJson();

        adapter = new PudimAdapter(listaPudims, this);

        recyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelp(adapter));
        touchHelper.attachToRecyclerView(recyclerView);

    }

    private void setListaPudimsXML() {
        XmlPullParserFactory pullParserFactory;
        try {
            pullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = pullParserFactory.newPullParser();

            InputStream in_s = getApplicationContext().getAssets().open("pudim.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);

            parseXML(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        listaPudims = null;
        int eventType = parser.getEventType();
        Pudim pudim = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    listaPudims = new ArrayList<Pudim>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("pudim")) {
                        pudim = new Pudim();
                    } else if (pudim != null) {
                        if (name.equals("sabor")) {
                            pudim.setSabor(parser.nextText());
                        } else if (name.equals("cobertura")) {
                            pudim.setCobertura(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("pudim") && pudim != null) {
                        listaPudims.add(pudim);
                    }
            }
            eventType = parser.next();
        }
    }

    private void setListaPudimsJson() {

    }


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private void setFloatActionButton() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirPudim();
            }
        });
    }

    private void inserirPudim() {
        Intent intent = new Intent(this, EditPudimActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("request_code", REQUEST_INSERT);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_INSERT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Pudim f = (Pudim) bundle.getParcelable("pudim");
                int position = bundle.getInt("position");
                //adapter.updateTitle(f.getTitulo(),position);
                //adapter.updateAno(f.getAno(),position);
                //adapter.updateGenero(f.getGenero  (),position);
                adapter.update(f, position);
            }
        }
        if (requestCode == REQUEST_INSERT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Pudim f = (Pudim) bundle.getParcelable("pudim");
                adapter.inserir(f);
            }
        }
    }

    @Override
    public void undo() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout), "Item removido.", Snackbar.LENGTH_LONG);

        snackbar.setAction("Desfazer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restaurar();
            }
        });
        snackbar.show();
    }

    @Override
    public void toast(Pudim pudim) {
        Toast.makeText(this, pudim.getSabor() + " " + pudim.getCobertura() + " " , Toast.LENGTH_LONG).show();
    }

    @Override
    public void edit(int position) {
        Intent intent = new Intent(this, EditPudimActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("request_code", REQUEST_EDIT);
        bundle.putParcelable("pudim", adapter.getListaPudims().get(position));
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private class GetPudimJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            listaPudims = new ArrayList<Pudim>();

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("https://my-json-server.typicode.com/Karpavicius/jsonServer/db");
            if (jsonStr != null) {
                try {

                    JSONObject object = new JSONObject(jsonStr);

                    JSONArray jsonArray = object.getJSONArray("pudim");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Pudim f = new Pudim();

                        f.setSabor(jsonArray.getJSONObject(i).getString("sabor"));
                        f.setCobertura(jsonArray.getJSONObject(i).getString("cobertura"));
                        f.setFoto(changeSize(new HttpHandler().getBitmap(jsonArray.getJSONObject(i).getString("foto"))));
                        listaPudims.add(f);
                    }
                }  catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setRecyclerView();
            setFloatActionButton();
        }
    }

}
