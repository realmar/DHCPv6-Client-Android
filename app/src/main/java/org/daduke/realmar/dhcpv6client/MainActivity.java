package org.daduke.realmar.dhcpv6client;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.daduke.realmar.dhcpv6client.util.IabHelper;
import org.daduke.realmar.dhcpv6client.util.IabResult;
import org.daduke.realmar.dhcpv6client.util.Inventory;
import org.daduke.realmar.dhcpv6client.util.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anastassios Martakos on 8/12/15.
 */

public class MainActivity extends AppCompatActivity {
    public static Menu option_menu_main;

    public static Context main_context;

    public static IabHelper mHelper;
    public static boolean billig_initialized = false;
    //  IInAppBillingService mService;

    public static String donation_001_price;
    public static String donation_005_price;
    public static String donation_010_price;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private DrawerLayout dlDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private int request_code;
    private String developer_payload;

    private MenuItem mPreviousMenuItem;

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if(result.getResponse() != -1005) {
                if (!result.isSuccess()) {
                    MsgBoxes msgbox = new MsgBoxes();
                    AlertDialog failed = (AlertDialog) msgbox.one_button(MainActivity.this, "Purchase Failed", getString(R.string.purchase_error) + result, false);
                    failed.show();
                    return;
                } else {
                    MainActivity.mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {
                    if(result.isSuccess()) {
                        MsgBoxes msg_box = new MsgBoxes();
                        AlertDialog success = (AlertDialog) msg_box.one_button(MainActivity.this, "Thank You!", getString(R.string.thank_you), false);
                        success.show();
                    }else{
                        MsgBoxes msg_box = new MsgBoxes();
                        AlertDialog success = (AlertDialog) msg_box.one_button(MainActivity.this, "Purchase Failed", getString(R.string.purchase_error) + result, false);
                        success.show();
                    }
                }
            };


    public IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            if (result.isFailure()) {
                donation_001_price = "ERROR";
                donation_005_price = "ERROR";
                donation_010_price = "ERROR";
            }
            else {
                donation_001_price = inventory.getSkuDetails(Constants.DONATION_001).getPrice();
                donation_005_price = inventory.getSkuDetails(Constants.DONATION_005).getPrice();
                donation_010_price = inventory.getSkuDetails(Constants.DONATION_010).getPrice();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("EXCEPTION", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d("EXCEPTION", "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            }catch(Exception e) {
            }
        }
        mHelper = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppIntegrity.check_shared_preferences(MainActivity.this);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, Misc.getStatusBarHeight(MainActivity.this), 0, 0);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintColor(Color.parseColor("#20000000"));

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.mipmap.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Tie DrawerLayout events to the ActionBarToggle
        dlDrawer.setDrawerListener(drawerToggle);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_fragment_current("action_preference_advanced")) {
                    replace_fragment(SettingsFragment.class, "action_settings");
                    getSupportActionBar().setTitle("Settings");
                    toggle_drawer_icon(1, 0, 500);
                } else if (is_fragment_current("action_preference_advanced_add")) {
                    replace_fragment(SettingsAdvancedFragment.class, "action_preference_advanced");
                } else {
                    if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                        mDrawer.closeDrawer(GravityCompat.START);
                    } else {
                        mDrawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });

        insert_main_fragment();

        option_menu_main = nvDrawer.getMenu();

        new DoStartup(MainActivity.this, this).execute();

        main_context = MainActivity.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.client_main, menu);

        // option_menu_main = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_about) {
        //    return true;
        //}

        /*switch(item.getItemId()) {
            case android.R.id.home:
                if(is_fragment_current("action_preference_advanced")) {
                    replace_fragment(SettingsFragment.class, "action_settings");
                    getSupportActionBar().setTitle("Settings");
                    toggle_drawer_icon(1, 0);
                }else if(is_fragment_current("action_preference_advanced_add")) {
                    replace_fragment(SettingsAdvancedFragment.class, "action_preference_advanced");
                }else{
                    mDrawer.openDrawer(GravityCompat.START);
                }
                return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onKeyDown(int key_code, KeyEvent event) {
        if (key_code == KeyEvent.KEYCODE_BACK) {
            if(mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawers();
                return true;
            }else {
                if(is_fragment_current("action_preference_advanced")) {
                    replace_fragment(SettingsFragment.class, "action_settings");
                    getSupportActionBar().setTitle("Settings");
                    toggle_drawer_icon(1, 0, 500);
                    return true;
                }else if(is_fragment_current("action_preference_advanced_add")) {
                    replace_fragment(SettingsAdvancedFragment.class, "action_preference_advanced");
                    return true;
                }else if(!is_fragment_current("action_main")) {
                    insert_main_fragment();
                    return true;
                }
            }
        }
        return super.onKeyDown(key_code, event);
    }

    private boolean is_fragment_current(String tag) {
        Fragment fragment = (Fragment)getFragmentManager().findFragmentByTag(tag);
        if(fragment != null && fragment.isVisible()) {
            return true;
        }else{
            return false;
        }
    }

    private void insert_main_fragment() {
        replace_fragment(MainFragment.class, "action_main");

        Menu mi = nvDrawer.getMenu();
        getSupportActionBar().setTitle(mi.findItem(R.id.action_main).getTitle());

        mi.findItem(R.id.action_main).setCheckable(true);
        mi.findItem(R.id.action_main).setChecked(true);
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }

        mPreviousMenuItem = mi.findItem(R.id.action_main);

        mDrawer.closeDrawers();
    }

    public void purchase_item(final String item) {
        request_code = Misc.get_random_positive_int();
        developer_payload = item;

        if(!MainActivity.billig_initialized) {
            MsgBoxes msgbox = new MsgBoxes();
            AlertDialog failed = (AlertDialog) msgbox.one_button(MainActivity.this, "Purchase Failed", getString(R.string.google_play_connection_issue), false);
            failed.show();
        }else{
            try {
                MainActivity.mHelper.launchPurchaseFlow(MainActivity.this, item, request_code, mPurchaseFinishedListener, developer_payload);
            }
            catch(Exception e) {
                Toast.makeText(MainActivity.this, getString(R.string.donation_retry), Toast.LENGTH_SHORT).show();
                MainActivity.mHelper.flagEndAsync();
            }
        }

    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                check_drawer();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                check_drawer();
            }

            private void check_drawer() {
                if(is_fragment_current("action_preference_advanced_add") || is_fragment_current("action_preference_advanced")) {
                    toggle_drawer_icon(0, 1,0);
                }
            }
        };
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setCheckable(true);
                        menuItem.setChecked(true);
                        if (mPreviousMenuItem != null) {
                            mPreviousMenuItem.setChecked(false);
                        }
                        mPreviousMenuItem = menuItem;

                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;
        String fragment_tag = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.action_main:
                fragmentClass = MainFragment.class;
                fragment_tag = "action_main";
                break;
            case R.id.action_invoke:
                fragmentClass = CustomDHCPv6Fragment.class;
                fragment_tag = "action_invoke";
                break;
            case R.id.action_unorinstall:
                fragmentClass = MainFragment.class;
                fragment_tag = "action_main";
                menuItem = option_menu_main.findItem(R.id.action_main);
                UIMenu.unorinstall(MainActivity.this);
                break;
            case R.id.action_settings:
                fragmentClass = SettingsFragment.class;
                fragment_tag = "action_settings";
                break;
            case R.id.action_donate:
                fragmentClass = DonationFragment.class;
                fragment_tag = "action_donate";
                break;
            case R.id.action_support:
                fragmentClass = SupportFragment.class;
                fragment_tag = "action_support";
                break;
            case R.id.action_about:
                fragmentClass = AboutFragment.class;
                fragment_tag = "action_about";
                break;
            default:
                fragmentClass = MainFragment.class;
                fragment_tag = "main_fragment";
        }

        replace_fragment(fragmentClass, fragment_tag);

        // Highlight the selected item, update the title, and close the drawer
        uncheck_all_drawer();
        menuItem.setChecked(true);
        getSupportActionBar().setTitle(menuItem.getTitle());

        mPreviousMenuItem = menuItem;

        mDrawer.closeDrawers();
    }

    public void go_settings_advanced() {
        replace_fragment(SettingsAdvancedFragment.class, "action_preference_advanced");

        toggle_drawer_icon(0, 1, 500);

        Menu mi = nvDrawer.getMenu();
        getSupportActionBar().setTitle("Settings Advanced");
    }

    public void go_settings_advanced_add() {
        replace_fragment(SettingsAdvancedAddInterfacesFragment.class, "action_preference_advanced_add");

        Menu mi = nvDrawer.getMenu();
        getSupportActionBar().setTitle("Settings Advanced");
    }

    private void toggle_drawer_icon(int start, int end, int duration) {
        ValueAnimator anim = ValueAnimator.ofFloat(start, end);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                drawerToggle.onDrawerSlide(dlDrawer, slideOffset);
            }
        });
        anim.setInterpolator(new DecelerateInterpolator());
// You can change this duration to more closely match that of the default animation.
        anim.setDuration(duration);
        anim.start();
    }

    private void replace_fragment(Class fragmentClass, String tag){
        Fragment fragment = null;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.flContent, fragment, tag)
                .commit();
    }

    private void uncheck_all_drawer() {
        for(int i = 0; i < option_menu_main.size(); i++) {
            option_menu_main.getItem(i).setChecked(false);
        }
    }

    public void post_startup() {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = shared_preferences.edit();

        if (!DHCPv6Integrity.CheckBase().equals("ok")) {
            editor.putBoolean("is_installed", false);
            editor.commit();

            option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_install);
            option_menu_main.findItem(R.id.action_invoke).setEnabled(false);
            option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_install);

            Misc.question_installation(MainActivity.this);
        }else if(!DHCPv6Integrity.CheckUpdate().equals("ok")) {
            editor.putBoolean("is_installed", true);
            editor.putBoolean("is_installed_update", false);
            editor.commit();

            option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_install_update);
            option_menu_main.findItem(R.id.action_invoke).setEnabled(false);
            option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_install);

            Misc.question_installation_update(MainActivity.this);
        }else{
            editor.putBoolean("is_installed", true);
            editor.putBoolean("is_installed_update", true);
            option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_uninstall);
            option_menu_main.findItem(R.id.action_invoke).setEnabled(true);
            option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_uninstall);

            if(!DHCPv6Integrity.check_config_files(MainActivity.this)) { new GenerateClientConfig(MainActivity.this).execute(); Log.v("EXCEPTION", "REFRESHING CONFIG FILE"); }

            editor.commit();
        }

        String base64EncodedPublicKey = BillingPublicKey.BILLING_PUBLIC_KEY;
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        try {
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Log.d("EXCEPTION", "Problem setting up In-app Billing: " + result);
                        billig_initialized = false;
                    } else {
                        List<String> additionalSkuList = new ArrayList<String>();
                        additionalSkuList.add(Constants.DONATION_001);
                        additionalSkuList.add(Constants.DONATION_005);
                        additionalSkuList.add(Constants.DONATION_010);

                        mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
                        billig_initialized = true;
                    }
                }
            });
        }catch (Exception e) {
        }

        if(shared_preferences.getBoolean(Constants.SHOW_ARCH_WARNING, false)) {
            String current_arch = SystemIntegrity.check_arch();
            if(!current_arch.equals("ok")) {
                if(current_arch.equals(Constants.ARCH_X86)) {
                    Misc.unsupported_arch(MainActivity.this, getString(R.string.unsupported_arch_x86));
                }
            }
        }
    }
}