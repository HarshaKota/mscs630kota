package usernameharshakota.harshakotanotes.encryption;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.Toast;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    // 9 - Timeout
    public volatile int fingerprintHandlerResult = 9;
    public CancellationSignal cancellationSignal;

    public FingerprintHandler(Context context) {
        this.mContext = context;
    }

    public void startAuth(FingerprintManager fingerprintManager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }

    public void stopListeningAuthentication() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        // This is triggered when cancellationSignal is called with
        // FingerprintManager.FINGERPRINT_ERROR_CANCELED (int = 5) errorCode
        // This occurs when cancellationSignal is sent out to cancel any ongoing listeners
        // Nothing to handle
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, "Authentication Failed ", Toast.LENGTH_SHORT).show();
        fingerprintHandlerResult = 0;
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(mContext, "Error " + helpString, Toast.LENGTH_SHORT).show();
        fingerprintHandlerResult = 0;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(mContext, "You have Fingerprint Access!", Toast.LENGTH_SHORT).show();
        fingerprintHandlerResult = 1;
    }
}
