/*
 * dLeyna
 *
 * Copyright (C) 2013-2017 Intel Corporation. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU Lesser General Public License,
 * version 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St - Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Tom Keel <thomas.keel@intel.com>
 */

package com.intel.dleyna.testnativelibs;

import java.util.LinkedList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This is the main Activity of an application to test native libraries.
 * @author Tom Keel
 */
public class MainActivity extends Activity {

    private static final String TAG = App.TAG;

    private Prefs prefs = Prefs.getInstance();

    // Widgets.
    private TextView ttyTextView;
    private Button allButton;
    private Button noneButton;
    private Button selectButton;
    private Button goButton;
    private Button clearButton;
    private Button skipButton;
    private Button killButton;

    /** The list of tests to be executed. */
    private LinkedList<Tests.Enum> testList = new LinkedList<Tests.Enum>();

    /** A test has been removed from the list and is currently running. */
    private boolean testRunning;

    /** The handle for running a test in the remote tester process. */
    private TestRunnerInterface testRunner;

    /** A test has been run in the remote tester process. */
    private boolean testRunnerTainted;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (App.LOG) Log.i(TAG, "MainActivity: onCreate");
        setContentView(R.layout.main);
        ttyTextView = (TextView) findViewById(R.id.tty_text_view);
        ttyTextView.setMovementMethod(new ScrollingMovementMethod()); // makes it scrollable...
        allButton = (Button) findViewById(R.id.all_button);
        allButton.setOnClickListener(allButtonListener);
        noneButton = (Button) findViewById(R.id.none_button);
        noneButton.setOnClickListener(noneButtonListener);
        selectButton = (Button) findViewById(R.id.select_button);
        selectButton.setOnClickListener(selectButtonListener);
        goButton = (Button) findViewById(R.id.go_button);
        goButton.setOnClickListener(goButtonListener);
        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(clearButtonListener);
        skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setOnClickListener(skipButtonListener);
        killButton = (Button) findViewById(R.id.kill_button);
        killButton.setOnClickListener(killButtonListener);
        showHelp();
    }

    protected void onStart() {
        super.onStart();
        if (App.LOG) Log.i(TAG, "MainActivity: onStart");
    }

    protected void onResume() {
        super.onResume();
        if (App.LOG) Log.i(TAG, "MainActivity: onResume");
        setEnabledStateOfWidgets(!testRunning && testList.isEmpty());
    }

    protected void onPause() {
        super.onPause();
        if (App.LOG) Log.i(TAG, "MainActivity: onPause");
    }

    protected void onStop() {
        super.onStop();
        if (App.LOG) Log.i(TAG, "MainActivity: onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        if (App.LOG) Log.i(TAG, "MainActivity: onDestroy");
        stopTests();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        menu.findItem(R.id.mi_select_all).setIcon(android.R.drawable.ic_menu_preferences);
        menu.findItem(R.id.mi_select_none).setIcon(android.R.drawable.ic_menu_preferences);
        menu.findItem(R.id.mi_select_some).setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return goButton.isEnabled();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.mi_select_all:
            selectAllOrNone(true);
            startActivity(new Intent(this, PrefsActivity.class));
            return true;
        case R.id.mi_select_none:
            selectAllOrNone(false);
            startActivity(new Intent(this, PrefsActivity.class));
            return true;
        case R.id.mi_select_some:
            startActivity(new Intent(this, PrefsActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void selectAllOrNone(boolean value) {
        Prefs.getInstance().setAllTestEnabledValues(value);
    }

    private final OnClickListener allButtonListener = new OnClickListener() {
        public void onClick(View v) {
            selectAllOrNone(true);
        }
    };

    private final OnClickListener noneButtonListener = new OnClickListener() {
        public void onClick(View v) {
            selectAllOrNone(false);
        }
    };

    private final OnClickListener selectButtonListener = new OnClickListener() {
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, PrefsActivity.class));
        }
    };

    private final OnClickListener clearButtonListener = new OnClickListener() {
        public void onClick(View v) {
            ttyTextView.setText(null);
        }
    };

    private final OnClickListener goButtonListener = new OnClickListener() {
        public void onClick(View v) {
            startTests();
        }
    };

    private final OnClickListener skipButtonListener = new OnClickListener() {
        public void onClick(View v) {
            skipTest();
        }
    };

    private final OnClickListener killButtonListener = new OnClickListener() {
        public void onClick(View v) {
            stopTests();
        }
    };

    private void startTests() {
        if (!testRunning && testList.isEmpty()) {
            for (Tests.Enum test : Tests.Enum.values()) {
                if (prefs.getTestEnabledValue(test)) {
                    testList.add(test);
                }
            }
            if (!testList.isEmpty()) {
                setEnabledStateOfWidgets(false);
                doNextTest();
            } else {
                writeTty("No tests selected!\n");
            }
        }
    }

    private void doNextTest() {
        if (!testRunning) {
            if (testList.isEmpty()) {
                setEnabledStateOfWidgets(true);
            } else if (testRunner == null) {
                bindService(new Intent(MainActivity.this, TestRunnerService.class),
                        testRunnerConnection, Context.BIND_AUTO_CREATE);
            } else if (testRunnerTainted) {
                try {
                    testRunner.die();
                } catch (RemoteException e) {
                    if (App.LOG) Log.w(TAG, "MainActivity: doNextTest: " + e);
                    testRunner = null;
                    testRunnerTainted = false;
                    bindService(new Intent(MainActivity.this, TestRunnerService.class),
                            testRunnerConnection, Context.BIND_AUTO_CREATE);
                }
            } else {
                testRunning = true;
                testRunnerTainted = true;
                new TestTask().execute();
            }
        }
    }

    private void skipTest() {
        if (testRunning && testRunner != null) {
            try {
                testRunner.die();
            } catch (RemoteException e) {
                if (App.LOG) Log.w(TAG, "MainActivity: stopTests: " + e);
                testRunning = false;
                doNextTest();
            }
        }
    }

    private void stopTests() {
        testList.clear();
        if (testRunning && testRunner != null) {
            try {
                testRunner.die();
            } catch (RemoteException e) {
                if (App.LOG) Log.w(TAG, "MainActivity: stopTests: " + e);
                testRunning = false;
                setEnabledStateOfWidgets(true);
            }
        } else {
            testRunning = false;
            setEnabledStateOfWidgets(true);
        }
    }

    private final ServiceConnection testRunnerConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder b) {
            if (App.LOG) Log.i(TAG, "MainActivity: onServiceConnected");
            testRunner = TestRunnerInterface.Stub.asInterface(b);
            testRunnerTainted = false;
            doNextTest();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            if (App.LOG) Log.i(TAG, "MainActivity: onServiceDisconnected");
            testRunning = false;
            testRunner = null;
            doNextTest();
        }
    };

    /**
     * This subclass of AsyncTask runs one test from a background thread
     * and then publishes the results on the UI thread.
     */
    private class TestTask extends AsyncTask<Void, String, Boolean> {

        public Boolean doInBackground(Void... params) {
            Tests.Enum thisTest = testList.removeFirst();
            publishProgress("Running " + thisTest.nameInLowerCase() + "...\n");
            try {
                testRunner.exec(thisTest.name());
            } catch (RemoteException e) {
                if (App.LOG) Log.w(TAG, "MainActivity: TestTask: doInBackground: " + e);
                return false;
            }
            return true;
        }

        protected void onProgressUpdate (String... values) {
            for (String s : values) {
                writeTty(s);
            }
        }

        protected void onCancelled() {
            finish("Canceled!");
        }

        protected void onPostExecute(Boolean result) {
            finish(result ? "Done." : "CRASH!!!");
        }

        private void finish(String message) {
            writeTty(message + '\n');
            testRunning = false;
            doNextTest();
        }
    }

    private void setEnabledStateOfWidgets(boolean enabled) {
        allButton.setEnabled(enabled);
        noneButton.setEnabled(enabled);
        selectButton.setEnabled(enabled);
        goButton.setEnabled(enabled);
        clearButton.setEnabled(enabled);
        skipButton.setEnabled(!enabled);
        killButton.setEnabled(!enabled);
    }

    private void writeTty(CharSequence cs) {
        ttyTextView.append(cs);
        ttyTextView.bringPointIntoView(ttyTextView.length() - 1);
    }

    private static final String help =
        "This app tests various native libraries.\n" +
        "Click the menu button to select the tests to run.\n" +
        "Click 'Go' to start running the tests.\n" +
        "Click 'Skip' to skip the current test.\n" +
        "Click 'Kill' to abort all tests.\n" +
        "\n";

    private void showHelp() {
        ttyTextView.append(help);
    }
}
