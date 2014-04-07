package umich.edu.csev.rps;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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

        View rootView;

        public String urlString = "http://192.168.1.201:8888/mmorps";
        int serverHeight = -1;
        public String pairString = "";
        int pairHeight = -1;

        ImageButton rockButton;
        ImageButton paperButton;
        ImageButton scissorsButton;
        Button settingsButton;
        ProgressBar spinner;
        TextView textStatus;
        String statusStr = "You need pairing code to play";
        TextView textLeaders;
        String leadersStr;

        private Handler mHandler = new Handler();
        long mStartTime = 0L;

        String rpsGuid = null;
        long checkTime = -1;
        long leaderTime = 1;

        String EOL = System.getProperty("line.separator");

        private void handleStats(String input) {
            try {
                System.out.println("handleStats ="+input);
                JSONArray json = new JSONArray(input);
                System.out.println("array="+json);
                leadersStr = "Leaderboard:"+EOL;
                for(int i = 0 ; i < json.length(); i++){
                    String name = json.getJSONObject(i).getString("name");
                    Integer score = json.getJSONObject(i).getInt("score");
                    Integer games = json.getJSONObject(i).getInt("games");
                    leadersStr = leadersStr + " " + name + "(" + games + ")" + " score=" + score + EOL;
                    System.out.println(leadersStr);
                }
                leaderTime = 15L;
            } catch (Exception e) {
                System.out.println("Exception "+e.getMessage());
            }
        }

        private void handlePlay(String input) {
            try {
                System.out.println("handlePlay ="+input);
                JSONObject json = new JSONObject(input);
                System.out.println("object="+json);
                /*
                leadersStr = "Leaderboard:"+EOL;
                for(int i = 0 ; i < json.length(); i++){
                    String name = json.getJSONObject(i).getString("name");
                    Integer score = json.getJSONObject(i).getInt("score");
                    Integer games = json.getJSONObject(i).getInt("games");
                    leadersStr = leadersStr + " " + name + "(" + games + ")" + " score=" + score + EOL;
                    System.out.println(leadersStr);
                }
                leaderTime = 15L;
                */
            } catch (Exception e) {
                System.out.println("Exception "+e.getMessage());
            }
        }

        private class MyGETJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String script = null;
                for(String whatever : params){
                    System.out.println("P="+whatever);
                    script = whatever;
                }
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    String theUrl = urlString+"/"+script;
                    System.out.println("theUrl="+theUrl);
                    URI website = new URI(theUrl);
                    HttpGet get = new HttpGet();
                    get.setURI(website);
                    HttpResponse response = httpclient.execute(get);
                    StatusLine statusLine = response.getStatusLine();
                    System.out.println("SL="+statusLine);
                    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        String responseString = out.toString();
                        System.out.println("Response\n");
                        System.out.println(responseString);
                        if ( script.startsWith("stats.php")) handleStats(responseString);
                        if ( script.startsWith("play.php")) handlePlay(responseString);
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (Exception e) {
                    System.out.println("Exception "+e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                // System.out.println("onPostExecute\n"+leadersStr);
                textLeaders.setText(leadersStr);
                textStatus.setText(statusStr);
                if ( checkTime < 1 ) {
                    spinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onProgressUpdate(Void... values) {
            }
        }

        // http://android-developers.blogspot.kr/2007/11/stitch-in-time.html
        private Runnable mUpdateTimeTask = new Runnable() {
            public void run() {
                final long start = mStartTime;
                long current = SystemClock.uptimeMillis();
                // System.out.println("start="+start+" current="+current);
                long millis = current - start;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                /* if (seconds < 10) {
                    textStatus.setText("" + minutes + ":0" + seconds);
                } else {
                    textStatus.setText("" + minutes + ":" + seconds);
                } */

                if ( leaderTime > 0 ) leaderTime = leaderTime -1;
                if ( leaderTime == 0 ) {
                    spinner.setVisibility(View.VISIBLE);
                    leaderTime = -1;
                    System.out.println("I AM TRIGGERED");
                    new MyGETJSON().execute("stats.php");
                    System.out.println("Back");
                }


                mHandler.postAtTime(this,
                        start + (((minutes * 60) + seconds + 1) * 1000));
            }
        };

        public PlaceholderFragment() {
        }

        public void doPlay(int playValue, EditText textServer, EditText textPair) {
            if ( pairString.length()< 1 ) {
                statusStr = "Please enter a pairing code";
                textStatus.setText(statusStr);
                textServer.setVisibility(View.VISIBLE);
                textPair.setVisibility(View.VISIBLE);
                settingsButton.setText("^");
            } else {
                String url = "play.php?play="+playValue+"&pair="+pairString;
                System.out.println("url="+url);
                new MyGETJSON().execute(url);
                System.out.println("Back");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // http://stackoverflow.com/questions/12559461/how-to-show-progress-barcircle-in-an-activity-having-a-listview-before-loading
            spinner = (ProgressBar) rootView.findViewById(R.id.spinner);
            System.out.println("Spinner="+spinner);

            // http://stackoverflow.com/questions/4310525/android-on-edittext-changed-listener
            final EditText textServer = (EditText) rootView.findViewById(R.id.serverText);
            System.out.println("text="+textServer);
            textServer.setText(urlString);

            textServer.addTextChangedListener(new TextWatcher(){
                public void afterTextChanged(Editable s) {
                    // System.out.println("url onchange="+textServer.getText());
                    urlString = textServer.getText().toString();
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                public void onTextChanged(CharSequence s, int start, int before, int count){}
            });

            final EditText textPair = (EditText) rootView.findViewById(R.id.pairText);
            System.out.println("text=" + textPair);

            textPair.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    // System.out.println("pair onchange="+textPair.getText());
                    pairString = textPair.getText().toString();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            // http://stackoverflow.com/questions/4622517/hide-a-edittext-make-it-visible-by-clicking-a-menu
            settingsButton = (Button) rootView.findViewById(R.id.settingsButton);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int current = textServer.getVisibility();
                    if ( current == View.GONE ) {
                        textServer.setVisibility(View.VISIBLE);
                        textPair.setVisibility(View.VISIBLE);
                        settingsButton.setText("^");
                    } else {
                        textServer.setVisibility(View.GONE);
                        textPair.setVisibility(View.GONE);
                        settingsButton.setText("+");
                    }
                }

            });

            rockButton = (ImageButton) rootView.findViewById(R.id.rockButton);
            System.out.println("ImageButton = "+rockButton);
            rockButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    System.out.println("Rock CLicked");
                    doPlay(0, textServer, textPair);
                }
            });

            paperButton = (ImageButton) rootView.findViewById(R.id.paperButton);
            System.out.println("paperButton = "+paperButton);
            paperButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    System.out.println("paperClicked ");
                    doPlay(1, textServer, textPair);
                }

            });


            scissorsButton = (ImageButton) rootView.findViewById(R.id.scissorsButton);
            System.out.println("scissorsButton = "+scissorsButton);
            scissorsButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    System.out.println("scissorsClicked");
                    doPlay(2, textServer, textPair);
                }

            });

            textStatus = (TextView) rootView.findViewById(R.id.textStatus);
            textLeaders = (TextView) rootView.findViewById(R.id.textLeaders);

            // http://stackoverflow.com/questions/1748977/making-textview-scrollable-in-android
            // yourTextView.setMovementMethod(new ScrollingMovementMethod())

            // First up my timer...
            mStartTime = SystemClock.uptimeMillis();
            System.out.println("Click="+mStartTime);
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
            return rootView;
        }
    }

}
