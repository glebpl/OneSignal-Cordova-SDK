package com.plugin.gcm;

// Fork
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
// END Fork

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;


import com.onesignal.OneSignal;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSInAppMessageAction;

import com.onesignal.OneSignal.NotificationOpenedHandler;
import com.onesignal.OneSignal.NotificationReceivedHandler;
import com.onesignal.OneSignal.GetTagsHandler;
import com.onesignal.OneSignal.IdsAvailableHandler;
import com.onesignal.OneSignal.PostNotificationResponseHandler;

public class OneSignalController {
  private static CallbackContext notifReceivedCallbackContext;
  private static CallbackContext notifOpenedCallbackContext;
  private static CallbackContext inAppMessageClickedCallbackContext;

  private static final String TAG = "OneSignalPush";

  /**
   * Tags
   */
  public static boolean getTags(CallbackContext callbackContext) {
    final CallbackContext jsTagsAvailableCallBack = callbackContext;
    OneSignal.getTags(new GetTagsHandler() {
      @Override
      public void tagsAvailable(JSONObject tags) {
        CallbackHelper.callbackSuccess(jsTagsAvailableCallBack, tags);
      }
    });
    return true;
  }

  public static boolean sendTags(JSONArray data) {
    try {
      OneSignal.sendTags(data.getJSONObject(0));
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
    return true;
  }

  public static boolean deleteTags(JSONArray data) {
    try {
      Collection<String> list = new ArrayList<String>();
      for (int i = 0; i < data.length(); i++)
        list.add(data.get(i).toString());
      OneSignal.deleteTags(list);
      return true;
    } catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  /**
   * Subscriptions
   */
  public static boolean getPermissionSubscriptionState(CallbackContext callbackContext) {
    CallbackHelper.callbackSuccess(callbackContext, OneSignal.getPermissionSubscriptionState().toJSONObject());
    return true;
  }

  public static boolean setSubscription(JSONArray data) {
    try {
      OneSignal.setSubscription(data.getBoolean(0));
      return true;
    }
    catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  /**
   * Notifications
   */
  public static boolean postNotification(CallbackContext callbackContext, JSONArray data) {
    try {
      JSONObject jo = data.getJSONObject(0);
      final CallbackContext jsPostNotificationCallBack = callbackContext;
      OneSignal.postNotification(jo,
        new PostNotificationResponseHandler() {
          @Override
          public void onSuccess(JSONObject response) {
            CallbackHelper.callbackSuccess(jsPostNotificationCallBack, response);
          }
          
          @Override
          public void onFailure(JSONObject response) {
            CallbackHelper.callbackError(jsPostNotificationCallBack, response);
          }
        });
      
      return true;
    }
    catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  public static boolean clearOneSignalNotifications() {
    try {
      OneSignal.clearOneSignalNotifications();
      return true;
    }
    catch(Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  /**
   * Location
   */

  public static void promptLocation() {
    OneSignal.promptLocation();
  }

  public static void setLocationShared(JSONArray data) {
    try {
      OneSignal.setLocationShared(data.getBoolean(0));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Misc
   */
  public static boolean registerForPushNotifications() {
    // doesn't apply to Android
    return true;
  }

  public static boolean getIds(CallbackContext callbackContext) {
    final CallbackContext jsIdsAvailableCallBack = callbackContext;
    OneSignal.idsAvailable(new IdsAvailableHandler() {
      @Override
      public void idsAvailable(String userId, String registrationId) {
        JSONObject jsonIds = new JSONObject();
        try {
          jsonIds.put("userId", userId);
          if (registrationId != null)
            jsonIds.put("pushToken", registrationId);
          else
            jsonIds.put("pushToken", "");
          
          CallbackHelper.callbackSuccess(jsIdsAvailableCallBack, jsonIds);
        }
        catch (Throwable t) {
          t.printStackTrace();
        }
      }
    });
    return true;
  }

  public static boolean enableVibrate(JSONArray data) {
    try {
      OneSignal.enableVibrate(data.getBoolean(0));
      return true;
    }
    catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  public static boolean enableSound(JSONArray data) {
    try {
      OneSignal.enableSound(data.getBoolean(0));
      return true;
    }
    catch (Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  public static boolean setInFocusDisplaying(CallbackContext callbackContext, JSONArray data) {
    try {
      OneSignal.setInFocusDisplaying(data.getInt(0));
      return true;
    }
    catch (JSONException e) {
       Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
       return false;
    }
  }

  public static void setLogLevel(JSONArray data) {
    try {
      JSONObject jo = data.getJSONObject(0);
      OneSignal.setLogLevel(jo.optInt("logLevel", 0), jo.optInt("visualLevel", 0));
    }
    catch(Throwable t) {
      t.printStackTrace();
    }
  }

  public static boolean userProvidedConsent(CallbackContext callbackContext) {
    boolean providedConsent = OneSignal.userProvidedPrivacyConsent();
    final CallbackContext jsUserProvidedConsentContext = callbackContext;
    CallbackHelper.callbackSuccessBoolean(callbackContext, providedConsent);
    return true;
  }

  public static boolean setRequiresConsent(CallbackContext callbackContext, JSONArray data) {
    try {
      OneSignal.setRequiresUserPrivacyConsent(data.getBoolean(0));
      return true;
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
   }
  }

  public static boolean grantConsent(JSONArray data) {
    try {
      OneSignal.provideUserConsent(data.getBoolean(0));
      return true;
   } catch (JSONException e) {
      e.printStackTrace();
      return false;
   }
  }

  public static boolean setExternalUserId(JSONArray data) {
    try {
      OneSignal.setExternalUserId(data.getString(0));
      return true;
    } catch (JSONException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static boolean removeExternalUserId() {
    try {
      OneSignal.removeExternalUserId();
      return true;
    }
    catch(Throwable t) {
      t.printStackTrace();
      return false;
    }
  }

  // Fork: channels creation
  private static boolean isValidResourceName(String name) {
    return (name != null && !name.matches("^[0-9]"));
  }

  // Fork: channels creation
  private static Uri getSoundUri(Context context, String sound) {
    Resources resources = context.getResources();
    String packageName = context.getPackageName();
    int soundId;

    if (isValidResourceName(sound)) {
      soundId = resources.getIdentifier(sound, "raw", packageName);
      if (soundId != 0)
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);
    }

    soundId = resources.getIdentifier("onesignal_default_sound", "raw", packageName);

    if (soundId != 0)
      return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);

    return null;
  }

  /**
   * Fork: method added
   * Creates notification channel
   * @param appContext
   * @param data
   */
  public static boolean createNotificationChannel(Context appContext, JSONArray data) {
    // String channelId, String channelName, JSONObject jo

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      try {
        String channelId = data.getString(0);
        String channelName = data.getString(1);
        JSONObject jo = data.getJSONObject(2);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);

        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{0, 800});

        Uri soundUri = getSoundUri(appContext, jo.optString("sound", null));

        if (soundUri != null) {
          // Initial channel sound
          AudioAttributes audioAttributes = new AudioAttributes.Builder()
                  .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                  .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                  .build();

          notificationChannel.setSound(soundUri, audioAttributes);
        }

        NotificationManager notificationManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);

        return true;
      } catch (Throwable e) {
        e.printStackTrace();
        return false;
      }
    }

    return true;
  }

  /**
   * Fork
   * Method to delete channel
   * Used to change sound
   * @param appContext
   * @param data
   */
  public static boolean deleteNotificationChannel(Context appContext, JSONArray data) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      try {
        String channelId = data.getString(0);
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.deleteNotificationChannel(channelId);
        return true;
      } catch (Throwable e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  /**
   * Fork
   * Method added to use Proxy for REST requests
   * @param data
   */
  public static boolean useProxy(JSONArray data) {
    try {
      JSONObject jo = data.getJSONObject(0);

      if(jo.has("baseUrl")) {
        OneSignal.useBaseUrl(jo.getString("baseUrl"));
      } else {
        String host = jo.getString("host");
        int port = jo.getInt("port");
        if(jo.has("user") && jo.has("pass")) {
          OneSignal.useProxy(host, port, jo.getString("user"), jo.getString("pass"));
        } else {
          OneSignal.useProxy(host, port, null, null);
        }
      }
      return true;
    } catch(Throwable t) {
      t.printStackTrace();
      return false;
    }
  }
}