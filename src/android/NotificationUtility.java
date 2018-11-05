package cordova.plugin.zendrive;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.LocationSettingsResult;

import br.com.pulluptecnologia.tracker.MainActivity;
import br.com.pulluptecnologia.tracker.R;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;


/**
 * Utility to create notifications to show to the user when the Zendrive SDK has something
 * interesting to report.
 */
public class NotificationUtility {
    // Notification related constants
    public static final int FOREGROUND_MODE_NOTIFICATION_ID = 98;
    public static final int LOCATION_DISABLED_NOTIFICATION_ID = 99;
    public static final int LOCATION_PERMISSION_DENIED_NOTIFICATION_ID = 100;

    public static final int PSM_ENABLED_NOTIFICATION_ID = 101;
    public static final int BACKGROUND_RESTRICTION_NOTIFICATION_ID = 102;
    public static final int WIFI_SCANNING_NOTIFICATION_ID = 104;
    public static final int GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID = 105;

    private static final int psmEnabledRequestCode = 200;
    private static final int locationDisabledRequestCode = 201;
    private static final int backgroundRestrictedRequestCode = 202;
    private static final int wifiScanningRequestCode = 203;
    private static final int googlePlaySettingsRequestCode = 204;
    private static final int locationPermissionRequestCode = 205;

    // channel keys (id) are used to sort the channels in the notification
    // settings page. Meaningful ids and descriptions tell the user
    // about which notifications are safe to toggle on/off for the application.
    private static final String FOREGROUND_CHANNEL_KEY = "Foreground";
    private static final String SETTINGS_CHANNEL_KEY = "Settings";

    /**
     * Create a notification when location permission is denied to the application.
     *
     * @param context App context
     * @return the created notification.
     */
    public static Notification createLocationPermissionDeniedNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(context, MainActivity.class);
        actionIntent.setAction(Constants.EVENT_LOCATION_PERMISSION_ERROR);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, locationPermissionRequestCode,
                actionIntent, FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context, SETTINGS_CHANNEL_KEY)
                .setContentTitle("Location Permission Denied")
                .setTicker("Location Permission Denied")
                .setContentText("Grant location permission to Zendrive app.")
                .setSmallIcon(R.mipmap.icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    /**
     * Create a notification when high accuracy location is disabled on the device.
     *
     * @param context App context
     * @return the created notification.
     */
    public static Notification createLocationSettingDisabledNotification(Context context) {
        createNotificationChannels(context);
        // TODO: use the result from the callback and show appropriate message and intent
        Intent callGPSSettingIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        callGPSSettingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), locationDisabledRequestCode,
                callGPSSettingIntent, 0);

        return new NotificationCompat.Builder(context.getApplicationContext(), SETTINGS_CHANNEL_KEY)
                .setContentTitle(context.getResources().getString(R.string.location_disabled))
                .setTicker(context.getResources().getString(R.string.location_disabled))
                .setContentText(context.getResources().getString(R.string.enable_location))
                .setSmallIcon(R.mipmap.icon)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .build();
    }

    /**
     * Create a notification when there was a settings error reported by Google Play Services.
     * @param context App context
     * @param result The {@link LocationSettingsResult} object from Play Services
     * @return created notification
     */
    public static Notification createGooglePlaySettingsNotification(Context context,
                                                                    LocationSettingsResult result) {
        if (result.getStatus().isSuccess()) {
            return null;
        }
        createNotificationChannels(context);
        Intent actionIntent = new Intent(context, MainActivity.class);
        actionIntent.setAction(Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR);
        actionIntent.putExtra(Constants.EVENT_GOOGLE_PLAY_SETTING_ERROR, result);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, googlePlaySettingsRequestCode,
                actionIntent, FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context, SETTINGS_CHANNEL_KEY)
                .setContentTitle("Location Settings Error")
                .setTicker("Location Settings Error")
                .setContentText("Tap here to resolve.")
                .setSmallIcon(R.mipmap.icon)
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    /**
     * Create a notification when Power Saver Mode is enabled on the device.
     * @param context App context
     * @return created notification
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static Notification createPSMEnabledNotification(Context context, boolean isError) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, psmEnabledRequestCode,
                actionIntent, FLAG_CANCEL_CURRENT);

        String errorWarningPrefix = isError ? "Error: " : "Warning: ";

        return new NotificationCompat.Builder(context, SETTINGS_CHANNEL_KEY)
                .setContentTitle(errorWarningPrefix + "Power Saver Mode Enabled")
                .setTicker(errorWarningPrefix + "power Saver Mode Enabled")
                .setContentText("Disable power saver mode.")
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.icon)
                .build();
    }


    /**
     * Create a notification when Wifi scanning is disabled on device.
     *
     * @param context App Context
     * @return created notification
     */
    public static Notification createWifiScanningDisabledNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, wifiScanningRequestCode,
                actionIntent, FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Builder(context, SETTINGS_CHANNEL_KEY)
                .setContentTitle("Wifi Scanning Disabled")
                .setTicker("Wifi Scanning Disabled")
                .setContentText("Tap to enable wifi radio.")
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.icon)
                .build();
    }

    /**
     * Create a notification when background restriction is enabled on the device.
     *
     * @param context App context
     * @return created notification
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification createBackgroundRestrictedNotification(Context context) {
        createNotificationChannels(context);
        Intent actionIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        actionIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, backgroundRestrictedRequestCode,
                actionIntent, FLAG_CANCEL_CURRENT);

        return new Notification.Builder(context, SETTINGS_CHANNEL_KEY)
                .setContentTitle("Background Restricted")
                .setTicker("Background Restricted")
                .setContentText("Disable Background Restriction")
                .setOnlyAlertOnce(true)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.icon)
                .build();
    }

    /**
     * Create a notification that is displayed when the Zendrive SDK
     * detects a possible drive.
     *
     * @param context App context
     * @return the created notification.
     */
    public static Notification createMaybeInDriveNotification(Context context) {
        createNotificationChannels(context);

        // suppresses deprecated warning for setPriority(PRIORITY_MIN)
        //noinspection deprecation
        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Zendrive")
                .setDefaults(0)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Detecting possible drive.")
                .setContentIntent(getNotificationClickIntent(context))
                .build();
    }

    /**
     * Create a notification that is displayed when the Zendrive SDK
     * determines that the user is driving.
     *
     * @param context App context
     * @return the created notification.
     */
    public static Notification createInDriveNotification(Context context) {
        createNotificationChannels(context);
        return new NotificationCompat.Builder(context, FOREGROUND_CHANNEL_KEY)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Zendrive")
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentText("Drive started.")
                .setContentIntent(getNotificationClickIntent(context))
                .build();
    }

    public static void cancelErrorAndWarningNotifications(Context context) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(LOCATION_DISABLED_NOTIFICATION_ID);
        manager.cancel(LOCATION_PERMISSION_DENIED_NOTIFICATION_ID);
        manager.cancel(PSM_ENABLED_NOTIFICATION_ID);
        manager.cancel(BACKGROUND_RESTRICTION_NOTIFICATION_ID);
        manager.cancel(WIFI_SCANNING_NOTIFICATION_ID);
        manager.cancel(GOOGLE_PLAY_SETTINGS_NOTIFICATION_ID);
    }

    private static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            NotificationChannel lowPriorityNotificationChannel = new NotificationChannel(FOREGROUND_CHANNEL_KEY,
                    "Zendrive trip tracking",
                    NotificationManager.IMPORTANCE_MIN);
            lowPriorityNotificationChannel.setShowBadge(false);
            manager.createNotificationChannel(lowPriorityNotificationChannel);

            NotificationChannel defaultNotificationChannel = new NotificationChannel
                    (SETTINGS_CHANNEL_KEY, "Problems",
                            NotificationManager.IMPORTANCE_HIGH);
            defaultNotificationChannel.setShowBadge(true);
            manager.createNotificationChannel(defaultNotificationChannel);
        }
    }

    private static PendingIntent getNotificationClickIntent(Context context) {
        Intent notificationIntent = new Intent(context.getApplicationContext(), SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(context.getApplicationContext(), 0,
                notificationIntent, 0);
    }
}