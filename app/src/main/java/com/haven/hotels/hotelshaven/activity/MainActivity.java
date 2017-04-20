package com.haven.hotels.hotelshaven.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.haven.hotels.hotelshaven.R;
import com.haven.hotels.hotelshaven.fragments.AboutUs;
import com.haven.hotels.hotelshaven.fragments.Home;
import com.haven.hotels.hotelshaven.fragments.OffersFragment;
import com.haven.hotels.hotelshaven.other.DataValidation;
import com.haven.hotels.hotelshaven.other.HhData;
import com.haven.hotels.hotelshaven.other.ProvidersUrl;
import com.haven.hotels.hotelshaven.other.StoredValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    final Context context = this;
    private EditText destination, checkinDate, checkoutDate;
    private DatePickerDialog checkinDatePickerDialog, checkoutDatePickerDialog;
    private Spinner adultSpinner, childrenSpinner, roomSpinner;
    private String destinationText, checkinText, checkoutText, adultText,
            childrenText, roomText, url, nights, urlCheckin, cinText, coutText, providerChosen;
    private Date userCheckinDate, userCheckOutDate;


    private SimpleDateFormat dateFormatter, dateFormatterUrl, dateFormat;
    private Calendar newDate, checkin, checkout, cin;

    DataValidation dataValidation = new DataValidation();
    StoredValues storedValues = new StoredValues();
    HhData hhData = new HhData();
    ProvidersUrl providersUrl = new ProvidersUrl();
    TimeZone timeZone = TimeZone.getTimeZone("GMT");


    private Button findHotels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        dateFormatter = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
        dateFormatterUrl = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        findViewsById();

        setDateTimeField();

        findHotels.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                setDataFromApp();
                getDataFromApp();


                userCheckinDate = dataValidation.convertStringToDate(checkinText);
                userCheckOutDate = dataValidation.convertStringToDate(checkoutText);
                nights = dataValidation.numberOfNights(userCheckinDate, userCheckOutDate);
                urlCheckin = dateFormatterUrl.format(userCheckinDate);
                cinText = dateFormat.format(userCheckinDate);
                coutText = dateFormat.format(userCheckOutDate);

                if(dataValidation.isEmpty(destinationText))
                {
                    showPositiveAlert("Destination", "        Oops! You forgot your destination");
                    return;
                }
                else if(!dataValidation.isTodayOrAfter(userCheckinDate))
                {
                    showPositiveAlert("Checkin Date", "       Checkin date cannot be in the past");
                    return;
                }
                else if(!dataValidation.checkOutDateGreaterThanCheckInDate(userCheckinDate, userCheckOutDate))
                {
                    showPositiveAlert("Checkout Date", "  Checkout date must be after checkin date");
                    return;
                }

                else if(dataValidation.isUkPostcode(destinationText) || dataValidation.isUsPostcode(destinationText))
                {
                    //use laterooms
                    url = providersUrl.getLaterooms(adultText, childrenText, nights, urlCheckin, destinationText);
                    providerChosen = "Laterooms.com";
                    storedValues.store("url", url);
                    storedValues.store("provider", providerChosen);
                    Intent intent = new Intent(context, WebViewActivity.class);
                    startActivity(intent);

                }
                else
                {
                    //use hotelscombined
                    url = providersUrl.getHotelsCombined(destinationText, cinText, coutText, adultText, roomText);
                    providerChosen = "HotelsCombined.com";
                    storedValues.store("url", url);
                    storedValues.store("provider", providerChosen);
                    Intent intent = new Intent(context, WebViewActivity.class);
                    startActivity(intent);
                }



            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id)
    {
        Fragment fragment = null;

        switch (id)
        {
            case R.id.aboutus:
                fragment = new AboutUs();
                break;
            case R.id.hh_relative_layout:
                fragment = new Home();
                break;
        }

        if(fragment != null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.context_main, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.home) {
//            Toast.makeText(this, "This is home", Toast.LENGTH_SHORT).show();
//        } else if (id == R.id.offers) {
//
//            OffersFragment offerfragment = new OffersFragment();
//            FragmentManager manager = getSupportFragmentManager();
//           manager.beginTransaction().replace(R.id.hh_relative_layout,
//                            offerfragment, offerfragment.getTag()).commit();
//
//        } else if (id == R.id.cookie) {
//
//        } else if (id == R.id.aboutus) {
//
//            Intent intent = new Intent(context, WebViewActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.privacy) {
//
//        } else if (id == R.id.blog) {
////            BlogFragment blogfragment = new BlogFragment();
////            FragmentManager manager = getSupportFragmentManager();
////            manager.beginTransaction().replace(R.id.hh_relative_layout,
////                            blogfragment, blogfragment.getTag()).commit();
//
//        } else if (id == R.id.term) {
//
//        }

        displaySelectedScreen(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void findViewsById()
    {
        destination = (EditText) findViewById(R.id.destinationedittext);


        checkinDate = (EditText) findViewById(R.id.checkinDate);
        checkinDate.setInputType(InputType.TYPE_NULL);
        cin = Calendar.getInstance(timeZone);
        //cin.add(Calendar.DATE, 1);
        checkinDate.setText(dateFormatter.format(cin.getTime()));

        checkoutDate = (EditText) findViewById(R.id.checkoutDate);
        checkoutDate.setInputType(InputType.TYPE_NULL);
        newDate = Calendar.getInstance(timeZone);
        newDate.add(Calendar.DATE, 1);
        checkoutDate.setText(dateFormatter.format(newDate.getTime()));

        adultSpinner = (Spinner) findViewById(R.id.adultSpinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.adultGuestArray, R.layout.spinnertext);
        adultSpinner.setAdapter(adapter);

        childrenSpinner = (Spinner) findViewById(R.id.childrenSpinner);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.childrenGuestArray, R.layout.spinnertext);
        childrenSpinner.setAdapter(adapter1);

        roomSpinner = (Spinner) findViewById(R.id.roomSpinner);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.roomArray, R.layout.spinnertext);
        roomSpinner.setAdapter(adapter2);

        findHotels = (Button) findViewById(R.id.findhotels);
    }

    private void setDateTimeField() {
        checkinDate.setOnClickListener(this);
        checkoutDate.setOnClickListener(this);


        Calendar newCalendar = Calendar.getInstance(timeZone);
        checkinDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                checkin = Calendar.getInstance(timeZone);
                checkin.set(year, monthOfYear, dayOfMonth);
                checkinDate.setText(dateFormatter.format(checkin.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        checkoutDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                checkout = Calendar.getInstance(timeZone);
                checkout.set(year, monthOfYear, dayOfMonth);
                checkoutDate.setText(dateFormatter.format(checkout.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == checkinDate) {
            checkinDatePickerDialog.show();
        } else if(view == checkoutDate) {
            checkoutDatePickerDialog.show();
        }
    }


    private void setDataFromApp()
    {
        hhData.setDestinationText(destination.getText().toString());
        hhData.setCheckinText(checkinDate.getText().toString());
        hhData.setCheckoutText(checkoutDate.getText().toString());
        hhData.setAdultText(adultSpinner.getSelectedItem().toString());
        hhData.setChildrenText(childrenSpinner.getSelectedItem().toString());
        hhData.setRoomText(roomSpinner.getSelectedItem().toString());
    }

    private void getDataFromApp()
    {
        destinationText =  hhData.getDestinationText().trim();
        checkinText = hhData.getCheckinText().trim();
        checkoutText = hhData.getCheckoutText().trim();
        adultText = hhData.getAdultText().trim();
        childrenText = hhData.getChildrenText().trim();
        roomText = hhData.getRoomText().trim();
    }

    private void showPositiveAlert(String title, String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_black_18dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();

                    }
                }) ;

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    private void showAlert(String title, String message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_black_18dp)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        //MainActivity.this.finish();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
