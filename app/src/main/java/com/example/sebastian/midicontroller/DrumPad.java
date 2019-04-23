package com.example.sebastian.midicontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class DrumPad extends AppCompatActivity {
Button[] drumButtons = new Button[12];
int noteValue = 0;
int drumLayer = 0;
MidiControllerManager midiControllerManager;
    MenuItem transposePlusButton,transposeMinusButton;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drum_pad);
        midiControllerManager = new MidiControllerManager(this);

        drumButtons[0] = findViewById(R.id.drumButton1);
        drumButtons[1] = findViewById(R.id.drumButton2);
        drumButtons[2] = findViewById(R.id.drumButton3);
        drumButtons[3] = findViewById(R.id.drumButton4);
        drumButtons[4] = findViewById(R.id.drumButton5);
        drumButtons[5] = findViewById(R.id.drumButton6);
        drumButtons[6] = findViewById(R.id.drumButton7);
        drumButtons[7] = findViewById(R.id.drumButton8);
        drumButtons[8] = findViewById(R.id.drumButton9);
        drumButtons[9] = findViewById(R.id.drumButton10);
        drumButtons[10] = findViewById(R.id.drumButton11);
        drumButtons[11] = findViewById(R.id.drumButton12);

        for(int i = 0;i <= drumButtons.length-1;i++) {
            final int ii = i;
            drumButtons[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    noteValue =  ii + drumLayer * 12;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //some code....
                            try {
                                midiControllerManager.sendNoteOn(9, noteValue, 127);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            try {
                                midiControllerManager.sendNoteOff(9, noteValue, 127);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
        setPadText();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.piano_menu_file, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.transpose_plus:
                transposePlusLayer();
                return true;
            case R.id.transpose_minus:
                transposeMinusLayer();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(DrumPad.this, MainActivity.class);
        //myIntent.putExtra("key", ); //Optional parameters
        //myIntent.putExtra("MainActivity Midi Object: ", midiControllerManager);
        midiControllerManager.closeMidiInputPort();
        DrumPad.this.startActivity(myIntent);
        return;
    }
    public void transposePlusLayer(){
        if(drumLayer < 10){
            drumLayer+=1;
            setPadText();
        }

    }
    public void transposeMinusLayer(){

        if(drumLayer > 0){
            drumLayer-=1;
            setPadText();
        }

    }
    public void setPadText(){
        for(int i = 0; i <= drumButtons.length-1;i++){
            int drumLayerText = i + 12 * drumLayer;
            drumButtons[i].setText(getString(R.string.pad_notes,drumLayerText));
        }
    }

}
