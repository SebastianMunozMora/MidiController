package com.example.sebastian.midicontroller;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class PianoActivity extends AppCompatActivity {
Button[] pianoButtons = new Button[12];
int [] pianoNotes = new int[12];
int pianoOctave = 0;
String[] notesString = new String[12];
MidiControllerManager midiControllerManager;

int noteValue = 0;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano);

        notesString[0] = "C";
        notesString[1] = "C#";
        notesString[2] = "D";
        notesString[3] = "D#";
        notesString[4] = "E";
        notesString[5] = "F";
        notesString[6] = "F#";
        notesString[7] = "G";
        notesString[8] = "G#";
        notesString[9] = "A";
        notesString[10] = "A#";
        notesString[11] = "B";

        pianoButtons[0] = findViewById(R.id.pButton1);
        pianoButtons[1] = findViewById(R.id.pButton2);
        pianoButtons[2] = findViewById(R.id.pButton3);
        pianoButtons[3] = findViewById(R.id.pButton4);
        pianoButtons[4] = findViewById(R.id.pButton5);
        pianoButtons[5] = findViewById(R.id.pButton6);
        pianoButtons[6] = findViewById(R.id.pButton7);
        pianoButtons[7] = findViewById(R.id.pButton8);
        pianoButtons[8] = findViewById(R.id.pButton9);
        pianoButtons[9] = findViewById(R.id.pButton10);
        pianoButtons[10] = findViewById(R.id.pButton11);
        pianoButtons[11] = findViewById(R.id.pButton12);


        //gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        //midiControllerManager = getIntent().getParcelableExtra("MainActivity Midi Object: ");
        midiControllerManager = new MidiControllerManager(this);
        setPianoText();
        for(int i = 0; i <= pianoButtons.length-1;i++) {
            final int ii = i;
            /*pianoButtons[i].setOnClickListener(new View.OnClickListener() {
                //@Override
              *//*  public void onClick(View view) {
                    noteValue = 36 + ii + pianoOctave * 12;
                    try {
                        midiControllerManager.sendNoteOn(0, noteValue, 127);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    *//**//*try {
                        sendNoteOn(0,noteValue,64);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*//**//*
                }*//*
            });*/
            pianoButtons[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    noteValue = 36 + ii + pianoOctave * 12;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //some code....
                            try {
                                midiControllerManager.sendNoteOn(0, noteValue, 127);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            try {
                                midiControllerManager.sendNoteOff(0,noteValue,127);
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
                transposePlusOctave();
                return true;
            case R.id.transpose_minus:
                transposeMinusOctave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void transposePlusOctave(){
        if(pianoOctave < 6){
            pianoOctave+=1;
        }
        setPianoText();
    }
    public void transposeMinusOctave(){

        if(pianoOctave > 0){
            pianoOctave-=1;
        }
        setPianoText();
    }

    public void setPianoText (){
        for(int i = 0; i <= pianoButtons.length-1; i++){
            int pianoOctaveText = pianoOctave+1;
            pianoButtons[i].setText(getString(R.string.piano_notes,notesString[i],pianoOctaveText));
            //pianoButtons[i].setTextColor(getColor(R.color.muteOffButtonColor));
        }
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(PianoActivity.this, MainActivity.class);
        //myIntent.putExtra("key", ); //Optional parameters
        //myIntent.putExtra("MainActivity Midi Object: ", midiControllerManager);
        midiControllerManager.closeMidiInputPort();
        PianoActivity.this.startActivity(myIntent);
        return;
    }


}
