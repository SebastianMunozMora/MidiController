package com.example.sebastian.midicontroller;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Sebastian on 3/8/2018.
 */

public class MidiControllerManager implements Parcelable{
    private static final String TAG = "MidiControllerManager";
    private MidiInputPort midiInputPort;
    private MidiDeviceInfo[] midiDeviceInfo;
    private Context context;
    Notification noti;
    MidiManager midiManager;
    public MidiControllerManager(Context context) {
        this.context = context;
        /*this.midiInputPort =  midiInputPort;
        this.midiDeviceInfo = midiDeviceInfo;*/
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            // do MIDI stuff
            //main midi class
            midiManager = (MidiManager) context.getSystemService(Context.MIDI_SERVICE);
            //Get Midi devices
            midiDeviceInfo = midiManager.getDevices();
            //Notification
            noti = new Notification.Builder(context)
                    .setContentTitle("Midi In")
                    .setContentText("New Device")
                    .build();
            midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
                public void onDeviceAdded(MidiDeviceInfo info) {
                    noti.notify();
                }

                public void onDeviceRemoved(MidiDeviceInfo info) {

                }
            }, new Handler(Looper.getMainLooper()));
            if (midiDeviceInfo.length > 0) {

                //Open Device
                midiManager.openDevice(midiDeviceInfo[0], new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice midiDevice) {
                        if (midiDevice == null) {

                        } else {
                            midiInputPort = midiDevice.openInputPort(0);
                        }
                    }
                }, new Handler(Looper.getMainLooper()));
            }
        }
    }

    protected MidiControllerManager(Parcel in) {
        midiDeviceInfo = in.createTypedArray(MidiDeviceInfo.CREATOR);
        noti = in.readParcelable(Notification.class.getClassLoader());
    }

    public static final Creator<MidiControllerManager> CREATOR = new Creator<MidiControllerManager>() {
        @Override
        public MidiControllerManager createFromParcel(Parcel in) {
            return new MidiControllerManager(in);
        }

        @Override
        public MidiControllerManager[] newArray(int size) {
            return new MidiControllerManager[size];
        }
    };

    public void sendNoteOn (int channel, int note, int velocity) throws IOException {
        byte[] buffer = new byte[32];
        int numBytes = 0;
        buffer[numBytes++] = (byte)(0x90 + (channel));
        buffer[numBytes++] = (byte)note;
        buffer[numBytes++] = (byte)velocity;
        int offset = 0;
        if(midiDeviceInfo.length > 0) {
            // post is non-blocking
            midiInputPort.send(buffer, offset, numBytes);
        }
    }
    public void sendNoteOff (int channel, int note, int velocity) throws IOException {
        byte[] buffer = new byte[32];
        int numBytes = 0;
        buffer[numBytes++] = (byte)(0x80 + (channel));
        buffer[numBytes++] = (byte)note;
        buffer[numBytes++] = (byte)velocity;
        int offset = 0;
        if(midiDeviceInfo.length > 0) {
            // post is non-blocking
            midiInputPort.send(buffer, offset, numBytes);
        }
    }
    public void sendControlChange (int channel,int controllerNumber, int controllerValue) throws IOException {
        byte[] buffer = new byte[32];
        int numBytes = 0;
        buffer[numBytes++] = (byte) (0xB0 + (channel));
        buffer[numBytes++] = (byte) controllerNumber;
        buffer[numBytes++] = (byte) controllerValue;
        int offset = 0;
        if(midiDeviceInfo.length > 0) {
            // post is non-blocking
            midiInputPort.send(buffer, offset, numBytes);
        }
    }
    public void sendSysEx(int message) throws IOException{
        byte[] buffer = new byte[64];
        int numBytes = 0;
        buffer[numBytes++] = (byte)(0xF0);
        buffer[numBytes++] = (byte)(0x7F);
        buffer[numBytes++] = (byte)(127);
        buffer[numBytes++] = (byte)(6);
        buffer[numBytes++] = (byte)(message);
        buffer[numBytes++] = (byte)(0);
        buffer[numBytes++] = (byte)(0xF7);
        int offset = 0;
        // post is non-blocking
        if(midiDeviceInfo.length > 0) {
            midiInputPort.send(buffer, offset, numBytes);
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
        if(midiDeviceInfo.length > 0) {
            midiInputPort.send(buffer, offset, numBytes);
        }
    }
    public void closeMidiInputPort(){
        if(midiDeviceInfo.length>0) {
            try {
                midiInputPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedArray(midiDeviceInfo, i);
        parcel.writeParcelable(noti, i);
    }
}
