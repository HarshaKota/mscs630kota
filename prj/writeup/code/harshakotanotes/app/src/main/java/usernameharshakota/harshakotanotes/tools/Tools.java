package usernameharshakota.harshakotanotes.tools;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Tools {

    public static String getCurrentTimeFormatted() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getFormattedDateTime(Context context, Long date) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"
                , context.getResources().getConfiguration().getLocales().get(0));
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(new Date(date));
    }
}
