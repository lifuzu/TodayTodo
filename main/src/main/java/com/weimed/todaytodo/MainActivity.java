package com.weimed.todaytodo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.weimed.lib.ConnectionDetector;
import com.weimed.lib.CustomAlertDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    public final static String EXTRA_TODO_TITLE = "com.weimed.todaytodo.TITLE";

    // Internet connection status
    Boolean isInternetConnected = false;
    // Connection detector class
    ConnectionDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Create connection detector instance
        detector = new ConnectionDetector(getApplicationContext());
        // Get the Internet connection status
        isInternetConnected = detector.isConnectingToInternet();
        // Check the connection status
        if (!isInternetConnected) {
            CustomAlertDialog.show(MainActivity.this, "No Internet Connection", "You don't have internet connection.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_create) {
            Intent intent = new Intent(this, CreateActivity.class);
            startActivityForResult(intent, id);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(R.id.action_create): {
                if (resultCode == Activity.RESULT_OK ) {
                    // String todoTitle = data.getIntExtra(todo_title);
                    PlaceholderFragment fr = (PlaceholderFragment)getFragmentManager().findFragmentById(R.id.container);
                    fr.update();
                }
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        /**
         * Used to display items in adapter with ListView
         */
        private SimpleAdapter adapter;
        private ListView listView;

        public PlaceholderFragment() {
        }

        public void update() {
            //Toast.makeText(getActivity(),"Clicked: " + ((HashMap<String, String>)listView.getItemAtPosition(3)).get("title"), Toast.LENGTH_SHORT).show();
            // Exec async load task
            (new AsyncListViewLoader()).execute("http://weimed.com/todo");
            //adapter.notifyDataSetChanged();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            listView = (ListView) rootView.findViewById(R.id.list_container);

            // Exec async load task
            (new AsyncListViewLoader()).execute("http://weimed.com/todo");

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(getActivity(),"Clicked: " + ((HashMap<String, String>)listView.getItemAtPosition(i)).get("title"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), DisplayActivity.class);
                    String message = ((HashMap<String, String>)listView.getItemAtPosition(i)).get("title");
                    intent.putExtra(EXTRA_TODO_TITLE, message);
                    startActivity(intent);
                }
            });

            return rootView;
        }

        private class AsyncListViewLoader extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
            @Override
            protected ArrayList<HashMap<String, String>> doInBackground(String... urls) {
                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                String response = "";
                for (String url: urls) {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url);
                    try {
                        HttpResponse httpResponse = client.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        response = EntityUtils.toString(httpEntity);
                    } catch (Exception e) {
                        Log.e("log_tag", "Error in http connection" + e.toString());
                    }

                    try {
                        JSONArray todos = new JSONArray(response);
                        for (int i = 0; i < todos.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            JSONObject e = todos.getJSONObject(i);
                            map.put("id", String.valueOf(i));
                            map.put("title", e.getString("title"));
                            map.put("content", "content_" + String.valueOf(i));
                            list.add(map);
                        }
                    } catch (Exception e) {
                        Log.e("log_tag", "Error parsing data " + e.toString());
                    }
                }
                return list;
            }

            @Override
            protected void onPostExecute( ArrayList<HashMap<String, String>> result) {
                super.onPostExecute(result);
                adapter = new SimpleAdapter(getActivity(), result, R.layout.content_main, new String[] { "title" },
                        new int[] {R.id.text_name});
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
