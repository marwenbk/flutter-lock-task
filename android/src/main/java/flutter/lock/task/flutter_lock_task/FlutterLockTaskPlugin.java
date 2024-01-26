package flutter.lock.task.flutter_lock_task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

public class FlutterLockTaskPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler, PluginRegistry.ActivityResultListener {

    private MethodChannel channel;
    private Context appContext;
    private Activity mActivity;
    private DevicePolicyManager dpm;
    private ComponentName cn;

    private static final List<String> WHITELISTED_PACKAGES = Arrays.asList(
    "com.android.settings" // For general settings
    );

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        appContext = flutterPluginBinding.getApplicationContext();
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_lock_task");
        channel.setMethodCallHandler(this);
        cn = new ComponentName(appContext, DeviceAdmin.class);
        dpm = (DevicePolicyManager) appContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        switch (call.method) {
            case "startLockTask":
                startLockTask(result);
                break;
            case "stopLockTask":
                stopLockTask(result);
                break;
            case "isInLockTaskMode":
                isInLockTaskMode(result);
                break;
            case "openHomeSettings":
                openHomeSettings(result);
                break;
            case "getPackageName":
                getPackageName(result);
                break;
            default:
                result.notImplemented();
        }
    }

    private void startLockTask(@NonNull Result result) {
        try {
            if (mActivity != null && dpm.isDeviceOwnerApp(mActivity.getPackageName())) {
                ArrayList<String> packages = new ArrayList<>(WHITELISTED_PACKAGES);
                packages.add(mActivity.getPackageName());
                dpm.setLockTaskPackages(cn, packages.toArray(new String[0]));
                mActivity.startLockTask();
                result.success(true);
            } else {
                result.success(false);
            }
        } catch (Exception e) {
            result.error("LOCK_TASK_ERROR", "Failed to start lock task: " + e.getMessage(), null);
        }
    }

    private void stopLockTask(@NonNull Result result) {
        try {
            if (mActivity != null) {
                mActivity.stopLockTask();
                result.success(true);
            } else {
                result.success(false);
            }
        } catch (Exception e) {
            result.error("LOCK_TASK_ERROR", "Failed to stop lock task: " + e.getMessage(), null);
        }
    }

    private void isInLockTaskMode(@NonNull Result result) {
        try {
            if (mActivity != null) {
                ActivityManager activityManager = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
                boolean inLockTaskMode;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    inLockTaskMode = activityManager.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
                } else {
                    inLockTaskMode = activityManager.isInLockTaskMode();
                }
                result.success(inLockTaskMode);
            } else {
                result.success(false);
            }
        } catch (Exception e) {
            result.error("LOCK_TASK_ERROR", "Failed to check lock task mode: " + e.getMessage(), null);
        }
    }

    private void openHomeSettings(@NonNull Result result) {
        try {
            if (mActivity != null) {
                final Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                mActivity.startActivity(intent);
                result.success(true);
            } else {
                result.success(false);
            }
        } catch (Exception e) {
            result.error("LOCK_TASK_ERROR", "Failed to open home settings: " + e.getMessage(), null);
        }
    }

    private void getPackageName(@NonNull Result result) {
        if (mActivity != null) {
            result.success(mActivity.getPackageName());
        } else {
            result.success(null);
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivity = activityPluginBinding.getActivity();
        activityPluginBinding.addActivityResultListener(this);
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        this.mActivity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        this.mActivity = null;
    }

    @Override
    public void onDetachedFromActivity() {
        this.mActivity = null;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }
}
