package com.rahnamaeman.app;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

import com.rahnamaeman.app.R;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks {
    private static Context context;

    public static String deviceFirebaseToken = "NOTHING";
    private WebView mWebView;

    private PermissionRequest mPermissionRequest;

    private String TAG = "me";
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String[] PERM_CAMERA =
            {Manifest.permission.CAMERA};

    //    String url = "http://devsrv.ir";
//    String url = "https://hypersamen.com/";
    public static String url = "https://rahnamaeman.com/";
//    private WebChromeClient mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.enableDefaults();

        MainActivity.context = getApplicationContext();


        setContentView(R.layout.activity_main);
//
//        Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
//        startActivity(intent);
//        finish();



        askNotificationPermission();
//        if (Build.VERSION.SDK_INT >= 33) {
//            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
//            }
//            else {
////                createChannel();
//            }
//        }

        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(false);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true);
        }
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setDefaultTextEncodingName("utf-8");

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
//        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;

                try {
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        url = extras.getString("link");
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);

                    return true;
                } catch (Exception e) {
                    Log.i(TAG, "shouldOverrideUrlLoading Exception:" + e);
                    return true;
                }


//                if (URLUtil.isNetworkUrl(url)) {
//                    return false;
//                }
//                Uri uri =  Uri.parse(url);
//                if (uri.getScheme().equals("spsapp") && uri.getHost().equals("setwifi")) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(MainActivity.this, "ERROR APP WITH URL NOT FOUND", Toast.LENGTH_SHORT).show();
//                }
//                return true;

            }

//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
////                Log.i(TAG, "Start: " + url);
//            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                if (Objects.equals(url, "https://rahnamaeman.com/Profile.Html")) {


                    Log.d(TAG, "GET TOKEN FROM DEVICE STORAGE");
                    Log.d(TAG, deviceFirebaseToken);

                    if (deviceFirebaseToken.equals("NOTHING")){
                        deviceFirebaseToken = MyFirebaseMessagingService.getToken(context);
                        Log.d(TAG, "GET TOKEN FROM DEVICE STORAGE");
                        Log.d(TAG, deviceFirebaseToken);
                    }

                    String script = "(function() { var form = new FormData();\n" +
                            "form.append(\"push_id\", \"" + deviceFirebaseToken + "\");\n" +
                            "var settings = {\n" +
                            "  \"url\": \"https://rahnamaeman.com/ajax/savePushId\",\n" +
                            "  \"method\": \"POST\",\n" +
                            "  \"timeout\": 0,\n" +
                            "  \"processData\": false,\n" +
                            "  \"mimeType\": \"multipart/form-data\",\n" +
                            "  \"contentType\": false,\n" +
                            "  \"data\": form,\n" +
                            "  xhrFields: {\n" +
                            "      withCredentials: true\n" +
                            "  }\n" +
                            "};\n" +
                            "\n" +
                            "$.ajax(settings).done(function (response) {\n" +
                            "  return response;\n" +
                            "});" +
                            " })();";

                    view.evaluateJavascript(script, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.d(TAG, value);

                        }
                    });
                }
            }

//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                Log.i(TAG, "Finish: " + url );
//            }


        });


//        mWebView.setWebChromeClient(new WebChromeClient() {
//            // Grant permissions for cam
//            @Override
//            public void onPermissionRequest(final PermissionRequest request) {
//                Log.d(TAG, "onPermissionRequest");
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @TargetApi(Build.VERSION_CODES.M)
//                    @Override
//                    public void run() {
//                        Log.d(TAG, request.getOrigin().toString());
//                        if(request.getOrigin().toString().equals("file:///")) {
//                            Log.d(TAG, "GRANTED");
//                            request.grant(request.getResources());
//                        } else {
//                            Log.d(TAG, "DENIED");
//                            request.deny();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                android.util.Log.d(TAG, consoleMessage.message());
//                return true;
//            }
//
//
//        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            // Grant permissions for cam

            private View customView;
            private android.webkit.WebChromeClient.CustomViewCallback customViewCallback;
            private int originalOrientation;
            private int originalSystemVisibility;

            @Nullable
            @Override
            public Bitmap getDefaultVideoPoster() {

                // on below line returning our resource from bitmap factory.
                if (customView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            @Override
            public void onHideCustomView() {

                // on below line removing our custom view and setting it to null.
                ((FrameLayout) getWindow().getDecorView()).removeView(customView);
                this.customView = null;

                // on below line setting system ui visibility to original one and setting orientation for it.
                getWindow().getDecorView().setSystemUiVisibility(this.originalSystemVisibility);
                setRequestedOrientation(this.originalOrientation);

                // on below line setting custom view call back to null.
                this.customViewCallback.onCustomViewHidden();
                this.customViewCallback = null;
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (this.customView != null) {
                    onHideCustomView();
                    return;
                }
                // on below line initializing all variables.
                this.customView = view;
                this.originalSystemVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.originalOrientation = getRequestedOrientation();
                this.customViewCallback = callback;
                ((FrameLayout) getWindow().getDecorView()).addView(this.customView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                Log.i(TAG, "onPermissionRequest");

                mPermissionRequest = request;
                final String[] requestedResources = request.getResources();
                for (String r : requestedResources) {
                    if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                        // In this sample, we only accept video capture request.
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Allow Permission to camera")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mPermissionRequest.grant(new String[]{PermissionRequest.RESOURCE_VIDEO_CAPTURE});
                                        Log.d(TAG, "Granted");
                                    }
                                })
                                .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        mPermissionRequest.deny();
                                        Log.d(TAG, "Denied");
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        break;
                    }
                }
            }


            @Override
            public void onPermissionRequestCanceled(PermissionRequest request) {
                super.onPermissionRequestCanceled(request);
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, consoleMessage.message());
                return true;
            }
        });
        mWebView.loadUrl(url);

        if (hasCameraPermission()) {
            mWebView.loadUrl(url);

//            mWebView.loadUrl("Your URL");
//            setContentView(mWebView);
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs access to your camera so you can take pictures.",
                    REQUEST_CAMERA_PERMISSION,
                    PERM_CAMERA);
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(this, "CATCH", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(MainActivity.this, PERM_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        mWebView.loadUrl(url);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    FirebaseMessaging.getInstance().setAutoInitEnabled(true);

                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });


    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
                FirebaseMessaging.getInstance().setAutoInitEnabled(true);

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

//    FirebaseMessaging.getInstance().getToken()
//    .addOnCompleteListener(new OnCompleteListener<String>() {
//        @Override
//        public void onComplete(@NonNull Task<String> task) {
//            if (!task.isSuccessful()) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
//                return;
//            }
//
//            // Get new FCM registration token
//            String token = task.getResult();
//
//            // Log and toast
//            String msg = getString(R.string.msg_token_fmt, token);
//            Log.d(TAG, msg);
//            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//        }
//    });

    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
//    @Override
//    public void onNewToken(@NonNull String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token);
//    }
//
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج از برنامه اطمینان دارید؟", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}


