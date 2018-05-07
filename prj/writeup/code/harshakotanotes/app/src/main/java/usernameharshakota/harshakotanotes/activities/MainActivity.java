package usernameharshakota.harshakotanotes.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import usernameharshakota.harshakotanotes.R;
import usernameharshakota.harshakotanotes.database.Notes;
import usernameharshakota.harshakotanotes.database.NotesDbHelper;
import usernameharshakota.harshakotanotes.shakeDetection.ShakeDetector;
import usernameharshakota.harshakotanotes.note.*;

public class MainActivity extends AppCompatActivity {

    private static boolean EncryptionState = true;

    private RecyclerView recyclerView;
    private SQLiteDatabase mDatabase;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;

    private static final int TOAST_TIME_LIMIT = 3000;
    private static long mToastCreatedTime = System.currentTimeMillis();

    private static final int FingerprintRequestCode = 0;


    /*
    A SQLite database helper object is created that allows us to access the database and its entries

    The floating button is set to open up OpenNoteActivity which will allow the user to enter a new
    note, and also a check to see if the state of the application is in Encrypted or Decrypted state

    Accelerometer sensor object is initialised and set to listen for shakes
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotesDbHelper dbHelper = new NotesDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.activity_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (!EncryptionState) {
                startActivity(new Intent(this, OpenNoteActivity.class));
            } else {
                if (mToastCreatedTime + TOAST_TIME_LIMIT < System.currentTimeMillis()) {
                    Toast.makeText(this, "Decrypt Before Making New Notes", Toast.LENGTH_SHORT).show();
                    mToastCreatedTime = System.currentTimeMillis();
                }
            }

        });

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(() -> {
                if (!EncryptionState) {
                    EncryptionState = true;
                    loadAllItems();
                } else {
                    if (mToastCreatedTime + TOAST_TIME_LIMIT < System.currentTimeMillis()) {
                        Toast.makeText(this, "Already In Encrypted Mode", Toast.LENGTH_SHORT).show();
                        mToastCreatedTime = System.currentTimeMillis();
                    }
                }
            });
        }

    }

    /*
    Menu options are populated on the top bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    /*
    On buttons selected on the menu bar, an action to encrypt or decrypt is performed depending on the
    state of the application. If the decrypt button is clicked the FingerprintChecking Activity is
    loaded to first authenticate.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.button_encrypt:
                if (!EncryptionState) {
                    EncryptionState = true;
                    loadAllItems();
                } else {
                    if (mToastCreatedTime + TOAST_TIME_LIMIT < System.currentTimeMillis()) {
                        Toast.makeText(this, "Already In Encrypted Mode", Toast.LENGTH_SHORT).show();
                        mToastCreatedTime = System.currentTimeMillis();
                    }
                }
                break;
            case R.id.button_decrypt:
                if (EncryptionState) {
                    startActivityForResult(new Intent(this,FingerprintChecking.class),
                            FingerprintRequestCode);
                } else {
                    if (mToastCreatedTime + TOAST_TIME_LIMIT < System.currentTimeMillis()) {
                        Toast.makeText(this, "Already In Decrypted Mode", Toast.LENGTH_SHORT).show();
                        mToastCreatedTime = System.currentTimeMillis();
                    }
                }
                break;
        }
        System.nanoTime();
        return super.onOptionsItemSelected(item);
    }

    /*
    This method listens to the result returned from the FingerprintChecking activity for a successful
    or unsuccessful authentication and loads the list view of notes.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FingerprintRequestCode) {
            if (resultCode == RESULT_OK) {
                int fingerprintResult = data.getIntExtra("fingerprintCheckingResult",9);

                switch (fingerprintResult) {
                    // Fingerprint passed
                    case 1:
                        EncryptionState = false;
                        loadAllItems();
                        break;
                    // Fingerprint failed
                    case 0:
                        Toast.makeText(this, "Unknown User", Toast.LENGTH_SHORT).show();
                        break;
                    // If not both then it timed-out
                    default:
                        Toast.makeText(this, "Fingerprint Auth Timed Out", Toast.LENGTH_SHORT).show();
                        break;
                }
            } else{
                // If Activity did'nt return with RESULT_OK
                Toast.makeText(this, "Fingerprint Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    All the notes from the database are loaded onto the main list view on the homescreen of the
    application
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadAllItems();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    /*
    When the application is not in the foreground, the Shake detection, accelerometer is removed to
    stop listening for shakes.
     */
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    /*
    Ensuring the list view is populated with encrypted notes such that no decrypted notes are viewed
    from the multitasking screen when the application is in the background.
     */
    @Override
    protected void onStop() {
        if (!EncryptionState) {
            loadAllItems();
        }
        super.onStop();
    }

    /*
    This method loads all the items onto the Recycler list view by fetching all the notes from the
    database

    It checks if there are any notes, if there are none a toast notification is displayed suggesting
    the same
     */
    private void loadAllItems() {
        recyclerView.setAdapter(null);
        NotesAdapter mAdapter = new NotesAdapter(this, getAllItems(), EncryptionState);

        if(mAdapter.getItemCount() > 0) {
            recyclerView.setAdapter(mAdapter);
        } else {
            EncryptionState = false;
            if (mToastCreatedTime + TOAST_TIME_LIMIT < System.currentTimeMillis()) {
                Toast.makeText(this, "You Don't Have Any Saved Notes", Toast.LENGTH_SHORT).show();
                mToastCreatedTime = System.currentTimeMillis();
            }
        }
    }

    /*
    Queries the database to get all notes in their descending order of creation time (date column)
     */
    private Cursor getAllItems() {
        return mDatabase.query(
                Notes.NotesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Notes.NotesEntry.COLUMN_DATE + " DESC"
        );
    }
}
