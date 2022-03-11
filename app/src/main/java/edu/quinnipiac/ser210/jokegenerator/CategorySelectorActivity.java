package edu.quinnipiac.ser210.jokegenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class CategorySelectorActivity extends AppCompatActivity {

    String url1 = "https://papajoke.p.rapidapi.com/api/jokes";
    private String LOG_TAG = CategorySelectorActivity.class.getSimpleName();
    Random rand = new Random();
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selector);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main,menu);
        //get menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);
        //connect shareActionProvider and send message
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareActionIntent("This joke is so funny! Download this app so you can laugh too!");

        return super.onCreateOptionsMenu(menu);
    }

    private void setShareActionIntent(String text){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,text);
        shareActionProvider.setShareIntent(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_help:
                //switch to help activity displaying developer and API info
                Intent intent = new Intent(this,HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //TODO: Change background color
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void switchToJoke(View view) {
        //calls FetchJoke asnyctask
        new FetchJoke().execute();

    }

    class FetchJoke extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String[] jokeContents = new String[2];

            try{

                //making URL connection
                URL url = new URL(url1);
                Log.d("URL",url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key","66653155e6mshbed1e1c6c568f68p1f931ajsn686358e46681");
                Log.d("try/catch","Reached request property");
                urlConnection.connect();
                Log.d("try/catch","Reached connect");


                InputStream in = urlConnection.getInputStream();
                if(in == null) return null;

                //creating reader
                reader = new BufferedReader(new InputStreamReader(in));
                //joke contents contains the setup and punchline as separate String objects in an array
                jokeContents = getStringArrayFromReader(reader);
                //result creates a single String that contains setup and punchline separated by "@"
                String result = jokeContents[0] + "@" + jokeContents[1];
                Log.d("result",result);
                return result;


            }catch(Exception e){
                e.printStackTrace();
            }finally{
                {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(reader!=null){
                        try{
                            reader.close();
                        }catch(IOException e){
                            Log.e(LOG_TAG,"Error: " + e.getMessage());
                            return null;
                        }
                    }

                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                //finds index of "@" in result string, and parses through to separate setup and punchline
                //creates intent with two String extras for setup and punchline and starts JokeActivity
                int atIndex = result.indexOf('@');
                String setup = result.substring(0,atIndex);
                String punchline = result.substring(atIndex+1);
                Intent intent = new Intent(CategorySelectorActivity.this,JokeActivity.class);
                intent.putExtra("setup",setup);
                intent.putExtra("punchline",punchline);
                startActivity(intent);
            }
            else{
                Log.d("Results", "results were null");
            }
        }
    }

    private String[] getStringArrayFromReader(BufferedReader reader){
        StringBuffer buffer = new StringBuffer();
        String line;
        String[] results = new String[2];

        if(reader != null){
            try{
                while((line = reader.readLine()) != null){
                    buffer.append(line + '\n');
                }
                reader.close();


                //parsing through JSON data
                //API returns an array of jokes, so we create a JSONArray holding the jokes
                JSONObject jokeContentsJSONObj = new JSONObject(buffer.toString());
                Log.d("jokeContentsJSONObj",jokeContentsJSONObj.toString());
                JSONArray jokes = (JSONArray)jokeContentsJSONObj.get("items");

                int jokeNum = 0;
                JSONObject joke = null;

                //we only want two-parter jokes, so we keep pulling jokes from the JSONArray until
                //we get a non one-liner
                do{
                    jokeNum = rand.nextInt(26);
                    joke = jokes.getJSONObject(jokeNum);
                }while(((String)joke.get("type")).equals("oneliner"));

                //getting setup and punchline strings from JSONObject
                String setup = (String)joke.get("headline");
                String punchline = (String)joke.get("punchline");
                Log.d("setup", setup);
                Log.d("punchline",punchline);
                results[0] = setup;
                results[1] = punchline;
                return results;

            }catch (Exception e){
                Log.e("CategorySelectorActivity","Error: " + e.getMessage());
                return null;
            }
            finally{

            }
        }
        return null;
    }
}