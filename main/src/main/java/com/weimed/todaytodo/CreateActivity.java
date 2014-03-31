package com.weimed.todaytodo;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class CreateActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create, menu);
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
        if (id == R.id.action_create_todo_done) {
            EditText editText = (EditText) findViewById(R.id.create_todo);
            Toast.makeText(this, "Clicked: " + editText.getText(), Toast.LENGTH_SHORT).show();
            sendPostRequest("http://weimed.com/todo", editText.getText().toString());
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendPostRequest(String url, String todoTitle) {
        class sendPostRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {
            @Override
            protected JSONObject doInBackground(String... params) {
                String url = params[0];
                String todoTitle = params[1];

                DefaultHttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                BasicNameValuePair todoTitleBasicNameValuePair = new BasicNameValuePair("title", todoTitle);
                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(todoTitleBasicNameValuePair);

                try {
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
                    httpPost.setEntity(urlEncodedFormEntity);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    HttpResponse response = client.execute(httpPost);
                    int status=response.getStatusLine().getStatusCode();
                    if (status == 201) {
                        /*HttpEntity httpEntity = response.getEntity();
                        String data = EntityUtils.toString(httpEntity);
                        JSONObject json = new JSONObject(data);
                        return json;*/
                        // Intent intent = new Intent();
                        // intent.putExtra();
                        setResult(RESULT_OK, null);
                        finish();
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPreExecute() {
                Log.i("Post Request in background", "Starting ...");
            }
            @Override
            protected void onPostExecute(JSONObject result) {
                super.onPostExecute(result);
                /*try {
                    String status = result.getString("status");
                    if(status.equalsIgnoreCase("yes")) {
                        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        }
        sendPostRequestAsyncTask sendPostReqAsyncTask = new sendPostRequestAsyncTask();
        sendPostReqAsyncTask.execute(url, todoTitle);
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
            View rootView = inflater.inflate(R.layout.fragment_create, container, false);
            return rootView;
        }
    }
}
