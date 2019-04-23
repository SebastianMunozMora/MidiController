package com.example.sebastian.midicontroller;

import android.Manifest;
import android.app.ActionBar;
import android.app.Notification;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;

import android.os.Looper;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    MidiControllerManager midiControllerManager;
    boolean resetOneState = false;
    private InterstitialAd mInterstitialAd;
    int countAd = 0;
    MidiDeviceInfo[] info;
    Notification noti;
    MidiInputPort midiInputPort;
    MidiManager m;
    ArrayAdapter arrayAdapter;
    BluetoothLeScanner bluetoothLeScanner ;
    ScanFilter.Builder scanFilter;
    ParcelUuid parcelUuid;
    BluetoothAdapter bluetoothAdapter = null;
    int channelLayer = 0;
    MenuItem menuItem;
    SeekBar[] faderViews = new SeekBar[8], pannerViews = new SeekBar[8];
    Button[] muteViews = new Button[8], soloViews= new Button[8], recordViews = new Button[8];
    boolean layerState = false;
    int[] faderState = new int[17], pannerState = new int [17];
    boolean[] muteButtonState = new boolean[17], recordButtonState = new boolean[17], soloButtonState = new boolean[17];
    View[] channelViews = new View[8];
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;

        MobileAds.initialize(this, "ca-app-pub-6662696407105574~6994577286");
  //      ActionBar actionBar = getActionBar();
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6662696407105574/3694460674");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
        channelViews[0] = findViewById(R.id.channelView1);
        channelViews[1] = findViewById(R.id.channelView2);
        channelViews[2] = findViewById(R.id.channelView3);
        channelViews[3] = findViewById(R.id.channelView4);
        channelViews[4] = findViewById(R.id.channelView5);
        channelViews[5] = findViewById(R.id.channelView6);
        channelViews[6] = findViewById(R.id.channelView7);
        channelViews[7] = findViewById(R.id.channelView8);

        faderViews[0] = findViewById(R.id.faderBar1);
        faderViews[1] = findViewById(R.id.faderBar2);
        faderViews[2] = findViewById(R.id.faderBar3);
        faderViews[3] = findViewById(R.id.faderBar4);
        faderViews[4] = findViewById(R.id.faderBar5);
        faderViews[5] = findViewById(R.id.faderBar6);
        faderViews[6] = findViewById(R.id.faderBar7);
        faderViews[7] = findViewById(R.id.faderBar8);

        pannerViews[0] = findViewById(R.id.panBar1);
        pannerViews[1] = findViewById(R.id.panBar2);
        pannerViews[2] = findViewById(R.id.panBar3);
        pannerViews[3] = findViewById(R.id.panBar4);
        pannerViews[4] = findViewById(R.id.panBar5);
        pannerViews[5] = findViewById(R.id.panBar6);
        pannerViews[6] = findViewById(R.id.panBar7);
        pannerViews[7] = findViewById(R.id.panBar8);

        recordViews[0] = findViewById(R.id.recordEnable1);
        recordViews[1] = findViewById(R.id.recordEnable2);
        recordViews[2] = findViewById(R.id.recordEnable3);
        recordViews[3] = findViewById(R.id.recordEnable4);
        recordViews[4] = findViewById(R.id.recordEnable5);
        recordViews[5] = findViewById(R.id.recordEnable6);
        recordViews[6] = findViewById(R.id.recordEnable7);
        recordViews[7] = findViewById(R.id.recordEnable8);

        muteViews[0] = findViewById(R.id.muteButton1);
        muteViews[1] = findViewById(R.id.muteButton2);
        muteViews[2] = findViewById(R.id.muteButton3);
        muteViews[3] = findViewById(R.id.muteButton4);
        muteViews[4] = findViewById(R.id.muteButton5);
        muteViews[5] = findViewById(R.id.muteButton6);
        muteViews[6] = findViewById(R.id.muteButton7);
        muteViews[7] = findViewById(R.id.muteButton8);

        soloViews[0] = findViewById(R.id.soloButton1);
        soloViews[1] = findViewById(R.id.soloButton2);
        soloViews[2] = findViewById(R.id.soloButton3);
        soloViews[3] = findViewById(R.id.soloButton4);
        soloViews[4] = findViewById(R.id.soloButton5);
        soloViews[5] = findViewById(R.id.soloButton6);
        soloViews[6] = findViewById(R.id.soloButton7);
        soloViews[7] = findViewById(R.id.soloButton8);

        menuItem = findViewById(R.id.reset_one);

            for(int i = 0; i <= faderViews.length-1; i++) {
                final int ii = i;
                //faderViews[i].getThumb().setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.LIGHTEN);
                //faderViews[i].getProgressDrawable().setColorFilter(getColor(R.color.colorAccent),PorterDuff.Mode.SRC_ATOP);
                faderViews[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int channel = ii + channelLayer;
                        if(!resetOneState) {
                            faderState[channel] = seekBar.getProgress();
                        }else{
                            faderState[channel] = 100;
                            seekBar.setProgress(faderState[channel]);
                        }
                        sendFaderVolume(channel, faderState[channel]);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                pannerViews[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        int channel = ii+channelLayer;
                        if(!resetOneState) {
                            pannerState[channel] = seekBar.getProgress();
                        }else{
                            pannerState[channel] = 64;
                            seekBar.setProgress(pannerState[channel]);
                        }
                        sendPanControl(channel, pannerState[channel]);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                muteViews[i].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        int muteValue;
                        int channel = ii+channelLayer;
                        if (!muteButtonState[channel]) {
                            muteViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.muteOnButtonColor));
                            muteValue = 127;
                            muteButtonState[channel] = true;
                        }else{
                            muteViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.muteOffButtonColor));
                            muteValue = 0;
                            muteButtonState[channel] = false;
                        }
                        sendMuteControl(channel,muteValue);
                    }
                });
                soloViews[i].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        int soloValue;
                        int channel = ii+channelLayer;
                        if (!soloButtonState[channel]) {
                            soloViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.soloOnButtonColor));
                            soloValue = 127;
                            soloButtonState[channel] = true;
                            /*if(!soloButtonState[channel]){
                                muteButtonState[channel] = true;
                                setMuteButtonState();
                            }*/
                        }else{
                            soloViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.soloOffButtonColor));
                            soloValue = 0;
                            soloButtonState[channel] = false;
                        }
                        sendSoloMessage(channel,soloValue);
                    }
                });
                recordViews[i].setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        int recValue;
                        int channel = ii+channelLayer;
                        if (!recordButtonState[channel]) {
                            recordViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.muteOnButtonColor));
                            recValue = 127;
                            recordButtonState[channel] = true;
                        }else{
                            recordViews[ii].setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.muteOffButtonColor));
                            recValue = 0;
                            recordButtonState[channel] = false;
                        }
                        setViewState();
                        sendRecordEnable(channel,recValue);

                    }
                });
            }
            for(int i = 0;i <= 15;i++) {
                muteButtonState[i] = false;
                faderState[i] = 100;
                recordButtonState[i] = false;
                pannerState[i] = 64;
                soloButtonState[i] = false;
            }
            midiControllerManager = new MidiControllerManager(this);
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file, menu);
        return true;
    }
    public void resetOneMenu(MenuItem menuItem){
        if(!resetOneState){
            menuItem.setIcon(R.drawable.ic_action_reset_off);
            resetOneState = true;
        }else {
            menuItem.setIcon(R.drawable.ic_action_reset_on);
            resetOneState = false;
        }
    }
    public void resetAllMenu(MenuItem menuItem){
        resetAll();
    }
    public void resetAll (){
        for(int i = 0;i <= 15;i++) {
            muteButtonState[i] = false;
            faderState[i] = 100;
            recordButtonState[i] = false;
            pannerState[i] = 64;
            soloButtonState[i] = false;
        }
        setMuteButtonState();
        setSoloButtonState();
        setRecordButtonState();
        setFaderState();
        setPannerState();
        setViewState();
    }
    public void playButton(View view){
        countAd += 1;
        if (mInterstitialAd.isLoaded() && countAd > 10) {
            countAd = 0;
            mInterstitialAd.show();

        } else {
            //Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
        try {
            midiControllerManager.sendSysEx(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopButton(View view){
        try {
            midiControllerManager.sendSysEx(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void recordButton(View view){
        try {
            midiControllerManager.sendSysEx(6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void forwardButton(View view){
        try {
            midiControllerManager.sendSysEx(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rewindButton(View view){
        try {
            midiControllerManager.sendSysEx(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enableCh(View view){
        try {
            sendSysExRec(8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFaderVolume(int faderChannel, int faderValue){
        try {
            midiControllerManager.sendControlChange(faderChannel,7,faderValue);
            //sendControlChange(faderChannel, 7, muteValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMuteControl(int muteChannel, int muteValue){
        try {
            midiControllerManager.sendControlChange(muteChannel,64,muteValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendPanControl(int panChannel, int panValue){
        try {
            midiControllerManager.sendControlChange(panChannel,0xA,panValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendRecordEnable(int recChannel, int recValue){
        try {
            midiControllerManager.sendControlChange(recChannel,9,recValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendSoloMessage(int channel, int soloValue) {
        try {
            midiControllerManager.sendControlChange(channel,20,soloValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void layerChange (MenuItem menuItem){
        if(!layerState) {
            menuItem.setTitle(R.string.layer_menu2);
            channelLayer = 8;
            recordViews[0].setText(R.string.Channel_9);
            recordViews[1].setText(R.string.Channel_10);
            recordViews[2].setText(R.string.Channel_11);
            recordViews[3].setText(R.string.Channel_12);
            recordViews[4].setText(R.string.Channel_13);
            recordViews[5].setText(R.string.Channel_14);
            recordViews[6].setText(R.string.Channel_15);
            recordViews[7].setText(R.string.Channel_16);
            layerState = true;
        }else{
            menuItem.setTitle(R.string.layer_menu1);
            channelLayer = 0;
            recordViews[0].setText(R.string.Channel_1);
            recordViews[1].setText(R.string.Channel_2);
            recordViews[2].setText(R.string.Channel_3);
            recordViews[3].setText(R.string.Channel_4);
            recordViews[4].setText(R.string.Channel_5);
            recordViews[5].setText(R.string.Channel_6);
            recordViews[6].setText(R.string.Channel_7);
            recordViews[7].setText(R.string.Channel_8);
            layerState = false;
        }
        setMuteButtonState();
        setSoloButtonState();
        setRecordButtonState();
        setFaderState();
        setPannerState();
        setViewState();
    }
    public void setFaderState (){
        for(int i = 0; i <= faderViews.length - 1; i++){
            faderViews[i].setProgress(faderState[i+channelLayer]);
        }
    }
    public void setPannerState (){
        for(int i = 0; i <= faderViews.length - 1; i++){
            pannerViews[i].setProgress(pannerState[i+channelLayer]);
        }
    }
    public void setMuteButtonState(){
        for(int i = 0; i <= faderViews.length - 1; i++){
            if (muteButtonState[i+channelLayer]) {
                muteViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOnButtonColor));
            }else{
                muteViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOffButtonColor));
            }
        }
    }
    public void setSoloButtonState(){
        for(int i = 0; i <= faderViews.length - 1; i++){
            if (soloButtonState[i+channelLayer]) {
                soloViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.soloOnButtonColor));
            }else{
                soloViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.soloOffButtonColor));
            }
        }
    }
    public void setRecordButtonState(){
        for(int i = 0; i <= faderViews.length - 1; i++){
            if (recordButtonState[i+channelLayer]) {
                recordViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOnButtonColor));
            }else{
                recordViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOffButtonColor));
            }
        }
    }
    public void setViewState(){
        for(int i = 0; i <= faderViews.length - 1; i++){
            if (recordButtonState[i+channelLayer]) {
                channelViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOnButtonColor));
            }else{
                channelViews[i].setBackgroundColor(ContextCompat.getColor(this, R.color.muteOffButtonColor));
            }
        }
    }
    public void sendSysExRec(int message) throws IOException{
        byte[] buffer = new byte[88];
        int numBytes = 0;
        buffer[numBytes++] = (byte)(0xF0);
        buffer[numBytes++] = (byte)(0x7F);
        buffer[numBytes++] = (byte)(127);
        buffer[numBytes++] = (byte)(6);
        buffer[numBytes++] = (byte)(65);
        buffer[numBytes++] = (byte)(4);
        buffer[numBytes++] = (byte)(79);
        buffer[numBytes++] = (byte)(0);
        buffer[numBytes++] = (byte)(32);
        buffer[numBytes++] = (byte)(0);
        buffer[numBytes++] = (byte)(0xF7);
        int offset = 0;
        // post is non-blocking
        if(info.length > 0) {
            midiInputPort.send(buffer, offset, numBytes);
        }
    }

    public void pianoLaunch(MenuItem item) {

        Intent myIntent = new Intent(MainActivity.this, PianoActivity.class);
        //myIntent.putExtra("key", ); //Optional parameters
        //myIntent.putExtra("MainActivity Midi Object: ", midiControllerManager);
        midiControllerManager.closeMidiInputPort();
        MainActivity.this.startActivity(myIntent);
    }
    public void padLaunch(MenuItem item) {
        Intent myIntent = new Intent(MainActivity.this, DrumPad.class);
        //myIntent.putExtra("key", ); //Optional parameters
        //myIntent.putExtra("MainActivity Midi Object: ", midiControllerManager);
        midiControllerManager.closeMidiInputPort();
        MainActivity.this.startActivity(myIntent);
    }
}
