package com.weimed.todaytodo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

    /**
     * Used for ListView
     */
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Exec async load task
        (new AsyncListViewLoader()).execute("http://weimed.com/todo");
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
            adapter = new SimpleAdapter(getApplicationContext(), result, R.layout.content_main, new String[] { "title" },
                    new int[] {R.id.text_name});
            ListView listView = (ListView)findViewById(R.id.list_container);
            // new PlaceholderFragment()..findViewById(R.id.list_container);
            //ListView listView = (ListView) getFragmentManager().findFragmentById(R.id.list_container);
            listView.setAdapter(adapter);
            //(getFragmentManager().findFragmentById(R.id.list_container)).setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
