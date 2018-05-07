package usernameharshakota.harshakotanotes.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import usernameharshakota.harshakotanotes.database.Notes;
import usernameharshakota.harshakotanotes.database.NotesDbHelper;
import usernameharshakota.harshakotanotes.encryption.AesEncryption;
import usernameharshakota.harshakotanotes.R;
import usernameharshakota.harshakotanotes.note.*;
import usernameharshakota.harshakotanotes.speechrecognizer.*;
import usernameharshakota.harshakotanotes.tools.*;

public class OpenNoteActivity extends AppCompatActivity{

    private String fileName;
    private Note loadNote;

    private EditText note_date;
    private EditText note_data;

    private Switch sb;

    private SQLiteDatabase mDatabase;

    private static final int RECORD_REQUEST_CODE = 1;

    private SpeechAPI speechAPI;
    private VoiceRecorder mVoiceRecorder;
    private String dateHolderForVoice;

    private final VoiceRecorder.Callback mVoiceCallback = new VoiceRecorder.Callback() {

        /*
        The following methods OnVoiceStart, onVoice, onVoiceEnd provided by the implementation
        of the Speech API, starts the listener when a voice is heard and starts the recognize
        method which sends voice and receives text results back. On voice end, it stops the
        recognize method
         */
        @Override
        public void onVoiceStart() {
            if (speechAPI != null) {
                speechAPI.startRecognizing(mVoiceRecorder.getSampleRate());
            }
        }

        @Override
        public void onVoice(byte[] data, int size) {
            if (speechAPI != null) {
                speechAPI.recognize(data, size);
            }
        }

        @Override
        public void onVoiceEnd() {
            if (speechAPI != null) {
                speechAPI.finishRecognizing();
            }
        }

    };

    /*
    This method allows us to use the recognized results that was sent from the Speech API, to check
    of they are final results or intermediate results and allows it to be set on the date & time
    field if they intermediate and on the text area if they are final. After which the date & time
    are set back to their original state
     */
    private final SpeechAPI.Listener mSpeechServiceListener =
            new SpeechAPI.Listener() {
                @Override
                public void onSpeechRecognized(final String text, final boolean isFinal) {
                    if (isFinal) {
                        mVoiceRecorder.dismiss();
                    }
                    if (note_data != null && !TextUtils.isEmpty(text)) {
                        runOnUiThread(() -> {
                            if (isFinal) {
                                note_data.append(" "+ text);
                                sb.setChecked(false);
                            } else {
                                note_date.setText(text);
                            }
                        });
                    }
                }
            };


    /*
    This method checks if the note is already present, i.e when the user clicks on on of the notes
    in the list view of the home page. If its an old note, its retrieved from the database and the
    date and data are set in their respective areas. If its a new note , only the current date and
    time are set.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_note);

        NotesDbHelper dbHelper = new NotesDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        note_data = findViewById(R.id.activity_open_note_note_data);
        note_date = findViewById(R.id.activity_open_note_note_date);

        speechAPI = new SpeechAPI(this);

        fileName = getIntent().getStringExtra("filename");

        if (fileName != null && !fileName.isEmpty()) {
            Cursor mCursor = getRecord(fileName);
            if (mCursor.moveToFirst()) {
                loadNote = new Note(
                        mCursor.getLong(mCursor.getColumnIndex(Notes.NotesEntry.COLUMN_DATE)),
                        mCursor.getString(mCursor.getColumnIndex(Notes.NotesEntry.COLUMN_DATA))
                );
            }
            note_date.setText(loadNote.getFormattedDateTime(this));
            note_date.setLongClickable(false);
                try {
                    note_data.setText(AesEncryption.aesDecrypt(this,loadNote.getData()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } else {
            note_date.setText(Tools.getCurrentTimeFormatted());
        }


    }

    /*
    This method loads up the options in the menu bar including the Voice button for which a onclick
    listener is attached to check for its state. When clicked first the permission is checked and
    then the Speech API recorder and listener are started. The toggle is set back to original state
    when the voice ends and the final result is received.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_open_note_menu, menu);

        sb = menu.findItem(R.id.activity_open_note_menu_button_voice).
                getActionView().findViewById(R.id.switch_button);

        sb.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                if (checkIfUserGrantedMicPermission()) {
                    Toast.makeText(getBaseContext(), "Listening", Toast.LENGTH_SHORT).show();
                    dateHolderForVoice = note_date.getText().toString();
                    startVoiceRecorder();
                    speechAPI.addListener(mSpeechServiceListener);
                } else {
                    checkIfUserGrantedMicPermission();
                    sb.setChecked(false);
                }
            } else {
                stopVoiceRecorder();
                note_date.setText(dateHolderForVoice);
                if (!speechAPI.isEmptyListener())
                    speechAPI.removeListener(mSpeechServiceListener);
            }
        }));

        return super.onCreateOptionsMenu(menu);
    }

    /*
    These menu options are provided a onclick listener for actions of save, delete and cancel.
    Appropriate methods are called to perform these actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_open_note_menu_button_save:
                saveNote();
                break;
            case R.id.activity_open_note_menu_button_cancel:
                actionCancel();
                break;
            case R.id.activity_open_note_menu_button_delete:
                actionDelete();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    The data entered into the note is extracted and trimmed to remove leading white spaces.

    Depending on if the note is a new note or an old note, the database is updated or inserted with
    the new data
     */
    private void saveNote() {
        String data = note_data.getText().toString().trim();
        long newNoteCreationTime;

        if (loadNote == null) {
            if (data.isEmpty()) {
                Toast.makeText(this, "Cant Save Empty Note!", Toast.LENGTH_SHORT).show();
                return;
            }

            newNoteCreationTime = System.currentTimeMillis();
            try {
                String encryptedData = AesEncryption.aesEncrypt(this,data);
                ContentValues cv = new ContentValues();
                cv.put(Notes.NotesEntry.COLUMN_DATE, newNoteCreationTime);
                cv.put(Notes.NotesEntry.COLUMN_DATA, encryptedData);
                if (mDatabase.insert(Notes.NotesEntry.TABLE_NAME, null, cv) > 0) {
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Save Error!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
        } else {
            if (isAltered(data)) {
                newNoteCreationTime = System.currentTimeMillis();

                try {
                    String encryptedData = AesEncryption.aesEncrypt(this,data);
                    ContentValues cv = new ContentValues();
                    cv.put(Notes.NotesEntry.COLUMN_DATE, newNoteCreationTime);
                    cv.put(Notes.NotesEntry.COLUMN_DATA, encryptedData);
                    if (mDatabase.update(Notes.NotesEntry.TABLE_NAME, cv,
                            Notes.NotesEntry.COLUMN_DATE + " = ?",
                            new String[] { fileName}) > 0) {
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Save Error!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            } else {
                finish();
            }
        }
    }

    /*
    This method checks if there are any changes to the data from the loaded old note.
     */
    private boolean isAltered(String data) {
        String loadText = null;
        try {
            loadText = AesEncryption.aesDecrypt(this, loadNote.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadNote != null && !data.equals(loadText);
    }


    @Override
    public void onBackPressed() { actionCancel();}

    /*
    This method pops up a dialog box when the user tries to exit the screen when with data entered,
    suggesting to choose to discard or save unsaved changes to the note.
     */
    private void actionCancel() {
        String data = note_data.getText().toString().trim();
        if (loadNote == null) {
            if (data.isEmpty()) {
                finish();
            } else {
                AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this)
                        .setTitle("Discard Changes?")
                        .setMessage("There are unsaved changes in your notes.\n" +
                                "Are you sure you don't want to save them?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", null);
                cancelDialog.show();

            }
        } else {
            if (isAltered(data)) {
                AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this)
                        .setTitle("Discard Changes?")
                        .setMessage("There are unsaved changes in your notes.\n" +
                                "Are you sure you don't want to save them?")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", null);
                cancelDialog.show();
            } else {
                finish();
            }
        }
    }

    /*
    This method pops up a dialog box when the user tries to delete an existing note, it provides
    options to delete or save unsaved changes
     */
    private void actionDelete() {
        String data = note_data.getText().toString().trim();

        if (data.isEmpty()) {
            finish();
        } else {
            if (loadNote == null) {
                actionCancel();
            } else {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this)
                        .setTitle("Are You Sure You Want To Delete This Note")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (mDatabase.delete(Notes.NotesEntry.TABLE_NAME,
                                    Notes.NotesEntry.COLUMN_DATE + " = ?",
                                    new String[] { fileName}) == 1) {
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Delete Error!", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        })
                        .setNegativeButton("No", null);
                deleteDialog.show();
            }
        }
    }

    /*
    When the activity goes in the background or when its closed, the Speech API listener is removed
     */
    @Override
    protected void onStop() {
        stopVoiceRecorder();

        // Stop Cloud Speech API
        if (!speechAPI.isEmptyListener()) {
            speechAPI.removeListener(mSpeechServiceListener);
        }
        speechAPI.destroy();
        speechAPI = null;

        super.onStop();
    }

    /*
    When the activity is back in foreground a new Speech API object is created.
     */
    @Override
    protected void onResume() {
        speechAPI = new SpeechAPI(this);
        super.onResume();
    }

    /*
    These following methods provide checks to see if the microphone access is provided so as to use
    the Voice button with the Speech API
     */
    private boolean checkIfUserGrantedMicPermission() {
        if (isGrantedPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            makeRequest(Manifest.permission.RECORD_AUDIO);
            return false;
        }
        return true;
    }

    private int isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0) {
                Toast.makeText(this, "Did Not Receive Any", Toast.LENGTH_LONG).show();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Rejected", Toast.LENGTH_LONG).show();
            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*
    A new voice recorder object is created that passes voice data to the Speech API to translate

    stopVoiceRecorder stops the recorder.
     */
    private void startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
        }
        mVoiceRecorder = new VoiceRecorder(mVoiceCallback);
        mVoiceRecorder.start();
    }

    private void stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder.stop();
            mVoiceRecorder = null;
        }
    }

    /*
    Queries the database to get specific notes using the date and time as a key.
     */
    private Cursor getRecord(String date) {
        return mDatabase.query(
                Notes.NotesEntry.TABLE_NAME,
                new String[] {Notes.NotesEntry.COLUMN_DATE, Notes.NotesEntry.COLUMN_DATA},
                Notes.NotesEntry.COLUMN_DATE + " = ?",
                new String[] { date.trim() },
                null,
                null,
                null
        );
    }
}
