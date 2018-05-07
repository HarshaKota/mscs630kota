package usernameharshakota.harshakotanotes.note;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import usernameharshakota.harshakotanotes.R;
import usernameharshakota.harshakotanotes.activities.OpenNoteActivity;
import usernameharshakota.harshakotanotes.database.Notes;
import usernameharshakota.harshakotanotes.encryption.AesEncryption;
import usernameharshakota.harshakotanotes.tools.Tools;

/*
This classs implements the adapter for thee Recycler view and sets up the main list view of notes
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    private boolean mEncryptionState;

    public NotesAdapter(Context context, Cursor cursor, boolean encryptedState) {
        mContext = context;
        mCursor = cursor;
        mEncryptionState = encryptedState;
    }

    public class NotesViewHolder extends RecyclerView.ViewHolder {

        private TextView dateText;
        private TextView dataText;
        private LinearLayout recycler_view_layout;

        public NotesViewHolder(View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.activity_main_item_note_date);
            dataText = itemView.findViewById(R.id.activity_main_item_note_data);
            recycler_view_layout = itemView.findViewById(R.id.activity_main_item_card_linear_layout);

        }
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.activity_main_item, parent, false);
        return new NotesViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        Long date = mCursor.getLong(mCursor.getColumnIndex(Notes.NotesEntry.COLUMN_DATE));
        String data = mCursor.getString(mCursor.getColumnIndex(Notes.NotesEntry.COLUMN_DATA));

        holder.dateText.setText(Tools.getFormattedDateTime(mContext, date));
        if (mEncryptionState) {
            holder.dataText.setText(data.substring(0, (data.length() > 35) ? 35 : data.length()));
        } else {
            try {
                String decryptedData = AesEncryption.aesDecrypt(mContext,data);
                holder.dataText.setText(
                        decryptedData.substring(0, (decryptedData.length() > 35) ? 35 : decryptedData.length()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        holder.recycler_view_layout.setOnClickListener(view -> {
            if (mEncryptionState) {
                Toast.makeText(mContext, "Decryption Needed", Toast.LENGTH_SHORT).show();
            } else {
                mCursor.moveToPosition(position);
                Long record = mCursor.getLong(mCursor.getColumnIndex(Notes.NotesEntry.COLUMN_DATE));
                Intent newIntent = new Intent(mContext, OpenNoteActivity.class);
                newIntent.putExtra("filename", record.toString());
                mContext.startActivity(newIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
}
