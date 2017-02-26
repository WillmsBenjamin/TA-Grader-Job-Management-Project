package ecse321.group12.tamas.androidtamas;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ecse321.group12.tamas.controller.InvalidInputException;
import ecse321.group12.tamas.controller.TamasController;
import ecse321.group12.tamas.model.Course;
import ecse321.group12.tamas.model.Instructor;
import ecse321.group12.tamas.model.Job;
import ecse321.group12.tamas.model.ResourceManager;
import ecse321.group12.tamas.persistence.PersistenceXStream;

public class ViewJobsActivity extends AppCompatActivity {

    private ResourceManager rm;
    private String fileName;
    String error = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener
                (
                        new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "DONE?", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("LOGOUT", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                fileName = getFilesDir().getAbsolutePath() + "/tamas_data.xml";
                                                rm = PersistenceXStream.initializeModelManager(fileName);
                                                refreshData();
                                                logout();
                                            }
                                        }).show();
                            }
                        }
                );

        fileName = getFilesDir().getAbsolutePath() + "/eventregistration.xml";
        rm = PersistenceXStream.initializeModelManager(fileName);

        refreshData();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void createDummyJobs()
    {
        TamasController tc = new TamasController(rm);
        try
        {
            tc.createCourse("Being a Duck: DCK-101",2,2,100);
            tc.registerInstructor("Bugs Bunny, the Elder","129384576");

            Instructor I = rm.getInstructor(0);
            Course C = rm.getCourse(0);
            tc.addInstructorToCourse(I,C);

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            int year=2050;
            int month=11;
            int day = 12;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month - 1);
            cal.set(Calendar.DAY_OF_MONTH, day);

            java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());
            tc.postGraderJob(100,10,date,"Being a Duck","3.90","3.90","Being a Duck",C);
            tc.postTAJob(100,10,date,"Being a Duck","3.90","3.90","Being a Duck",C, 45, true);
            Toast.makeText(getApplicationContext(),"Dummy Jobs Created",Toast.LENGTH_SHORT).show();
        }
        catch (InvalidInputException e)
        {
            error=e.getMessage();
            Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();

        }

    }

    private void logout()
    {
        TamasController tc = new TamasController(rm);
        tc.logOut();
        forceToLoginPage();

    }

    private void refreshData()
    {
        Spinner spinnerj = (Spinner) findViewById(R.id.job_spinner);

        ArrayAdapter<CharSequence> jAdapter = new
                ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);

        jAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (Job j: rm.getJobs() )
        {
            jAdapter.add(j.getCourse().getName());
        }
        spinnerj.setAdapter(jAdapter);

        if (jAdapter.isEmpty())
        {
            createDummyJobs();
        }
        else
        {
           Job J = rm.getJob(spinnerj.getSelectedItemPosition());
           drawField(J.getRequiredCourseGPA(),J.getRequiredCGPA(),"Experience Required\n\n" + J.getRequiredExperience(), "Skills Required\n\n" + J.getRequiredSkills());
        }



    }
    private void drawField(String gpa, String cgpa, String reqSkills, String jobSkills)
    {
        TextView tv = (TextView) findViewById(R.id.required_gpa);
        tv.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(gpa), null, null, null);
        tv.setCompoundDrawablePadding(gpa.length()*10);

        tv = (TextView) findViewById(R.id.required_cgpa);
        tv.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(cgpa), null, null, null);
        tv.setCompoundDrawablePadding(cgpa.length()*10);

        tv = (TextView) findViewById(R.id.required_skills);
        tv.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(reqSkills), null, null, null);
        tv.setCompoundDrawablePadding(reqSkills.length()*10);

        tv = (TextView) findViewById(R.id.job_skills);
        tv.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(jobSkills), null, null, null);
        tv.setCompoundDrawablePadding(jobSkills.length()*10);;
    }
    public void moveToMainPage(View v)
    {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);

        startActivity(i);
        refreshData();
    }
    public void forceToLoginPage()
    {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }
    public void moveToApplicationPage(View v)
    {
        try
        {
            Spinner spinnerj= (Spinner) findViewById(R.id.job_spinner);
            int jindex = spinnerj.getSelectedItemPosition();

            Job j = rm.getJob(jindex);

            String name = j.getCourse().getName();

            Intent i = new Intent(getApplicationContext(), ApplicationActivity.class);
            i.putExtra("jindex", jindex);
            i.putExtra("name", name);
            startActivity(i);
            refreshData();
        }
        catch(IllegalStateException e)
        {
            error=e.getMessage();
            Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(),"Please Select a Job",Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction()
    {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


