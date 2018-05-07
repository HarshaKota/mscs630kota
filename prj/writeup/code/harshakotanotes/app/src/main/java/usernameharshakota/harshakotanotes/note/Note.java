package usernameharshakota.harshakotanotes.note;

import android.content.Context;
import android.os.Build;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
This class show all the fields a note will have, such as the data and date field.
Also provides a function to turn the dateInMillis to a formatted date
 */
public class Note implements Serializable {

    private String data;
    private long date;

    public Note(long dateInMillis, String data) {
        this.date = dateInMillis;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getFormattedDateTime(Context context) {
        SimpleDateFormat formatter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"
                    , context.getResources().getConfiguration().getLocales().get(0));
        } else {
            formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"
                    , context.getResources().getConfiguration().locale);
        }
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(date));
    }
}
