package cordova.plugin.zendrive;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;

import br.com.pulluptecnologia.tracker.MainActivity;

public class SplashActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isUserLoggedIn(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String savedDriverId = sharedPreferences.getString(SharedPreferenceManager.DRIVER_ID_KEY, null);
        return savedDriverId!=null && !savedDriverId.equalsIgnoreCase("");
    }
}