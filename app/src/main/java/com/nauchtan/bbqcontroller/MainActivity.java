package com.nauchtan.bbqcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final String TAG = "NAUCHTAN";

    // COMMANDS
    private static final byte USBAccessoryWhat = 0;
    public static final byte MOTOR1_TARGET_SIDE1    = 13;
    public static final byte MOTOR1_TARGET_SIDE2    = 14;
    public static final byte MOTOR2_TARGET_SIDE1    = 15;
    public static final byte MOTOR2_TARGET_SIDE2    = 16;
    public static final byte MOTOR3_TARGET_SIDE1    = 17;
    public static final byte MOTOR3_TARGET_SIDE2    = 18;
    public static final byte GOTO_PRESET_SIDE1      = 19;
    public static final byte GOTO_PRESET_SIDE2      = 20;

    public static final byte CONFIG2A_PRESET_MOTOR1 = 29;
    public static final byte CONFIG2_PRESET_MOTOR1  = 30; //
    public static final byte CONFIG1_PRESET_MOTOR1  = 31;
    public static final byte CONFIG0_PRESET_MOTOR1  = 32;
    public static final byte CONFIG1A_PRESET_MOTOR1 = 33;
    public static final byte CONFIG3_PRESET_MOTOR1  = 34;

    public static final byte CONFIG2A_PRESET_MOTOR2 = 39;
    public static final byte CONFIG2_PRESET_MOTOR2  = 40; // CONFIG2_PRESET_MOTOR2
    public static final byte CONFIG1_PRESET_MOTOR2  = 41;
    public static final byte CONFIG0_PRESET_MOTOR2  = 42;
    public static final byte CONFIG1A_PRESET_MOTOR2 = 43;
    public static final byte CONFIG3_PRESET_MOTOR2  = 44;

    public static final byte CONFIG2A_PRESET_MOTOR3 = 49;
    public static final byte CONFIG2_PRESET_MOTOR3  = 50;
    public static final byte CONFIG1_PRESET_MOTOR3  = 51;
    public static final byte CONFIG0_PRESET_MOTOR3  = 52;
    public static final byte CONFIG1A_PRESET_MOTOR3 = 53;
    public static final byte CONFIG3_PRESET_MOTOR3  = 54;
    public static final byte ZERO_AND_SPAN_ALL      = 97;
    public static final byte REZERO_ALL_LIFTERS     = 98;
    public static final byte NOMINATE_SIDE          = 99;

    // Commands that usually come from the Host Device
    public static final byte ACTUAL_POSITION_MOTOR1_SIDE1   = 110;
    //public static final byte ACTUAL_POSITION_MOTOR1_SIDE2 = 111;
    public static final byte ACTUAL_POSITION_MOTOR2_SIDE1   = 114;
    public static final byte ACTUAL_POSITION_MOTOR3_SIDE1   = 119;
    public static final byte APP_CONNECT                    = 126;
    public static final byte APP_DISCONNECT                 = 127;


    // GOTO PRESET SELECTION CODES
    public static final byte CONFIG_2               = 1;
    public static final byte CONFIG_1               = 2;
    public static final byte CONFIG_0               = 3;
    public static final byte CONFIG_3               = 4;
    public static final byte CONFIG_1A              = 5;
    public static final byte CONFIG_2A              = 6;

    public static final byte MOTOR_CONTROL_INTERFACE = 1;


    // VARIABLES
    public byte ZONE1_TARGET_SIDE1                  = 50;
    public byte ZONE1_ACTUAL_SIDE1                  = 50;
    public byte ZONE1_SAVED_TARGET_SIDE1            = 50;
    public byte ZONE1_PRESET_CONFIG2_SIDE1          = 31;
    public byte ZONE1_PRESET_CONFIG2A_SIDE1         = 27;
    public byte ZONE1_PRESET_CONFIG1_SIDE1          = 5;
    public byte ZONE1_PRESET_CONFIG1A_SIDE1         = 10;
    public byte ZONE1_PRESET_CONFIG0_SIDE1          = 1;
    public byte ZONE1_PRESET_CONFIG3_SIDE1          = 15;

    public byte ZONE2_TARGET_SIDE1                  = 40;
    public byte ZONE2_ACTUAL_SIDE1                  = 40;
    public byte ZONE2_SAVED_TARGET_SIDE1            = 40;
    public byte ZONE2_PRESET_CONFIG2_SIDE1          = 48;
    public byte ZONE2_PRESET_CONFIG2A_SIDE1         = 55;
    public byte ZONE2_PRESET_CONFIG1_SIDE1          = 33;
    public byte ZONE2_PRESET_CONFIG1A_SIDE1         = 40;
    public byte ZONE2_PRESET_CONFIG0_SIDE1          = 50;
    public byte ZONE2_PRESET_CONFIG3_SIDE1          = 80;

    public byte ZONE3_TARGET_SIDE1                  = 50;
    public byte ZONE3_ACTUAL_SIDE1                  = 50;
    public byte ZONE3_SAVED_TARGET_SIDE1            = 50;
    public byte ZONE3_PRESET_CONFIG2_SIDE1          = 10;
    public byte ZONE3_PRESET_CONFIG2A_SIDE1         = 0;
    public byte ZONE3_PRESET_CONFIG1_SIDE1          = 90;
    public byte ZONE3_PRESET_CONFIG1A_SIDE1         = 5;
    public byte ZONE3_PRESET_CONFIG0_SIDE1          = 0;
    public byte ZONE3_PRESET_CONFIG3_SIDE1          = 50;

    public byte         Side                        = 0;
    public static byte  autoManual1                 = CONFIG_2;
    public static byte  userInterfaceNumber         = MOTOR_CONTROL_INTERFACE;


    // Accessory Filter Variables
    private boolean             deviceAttached              = false;          // was "private"
    private int                 firmwareProtocol            = 0;             // was private
    private String              firmwareDescription         = "Description has not yet been read";
    private int                 numSides                    = 1;
    private int                 numZones                    = 2;
    private byte                zonesByte                   = 0x03;          // 0b11 = 2-axis (i.e. motor1 and motor 2) // 0x07 or 0b111 is for 3-axis
    private byte                currentModuleConfiguration  = CONFIG_2;


    // zonesByte data codes / masks
    public static final byte    MOTOR1_MASK = 0x01;
    public static final byte    MOTOR2_MASK = 0x02;
    public static final byte    MOTOR3_MASK = 0x04;


    // UI VARIABLES - MOTOR CONFIG Side1
    private static EditText     targetZone1Side1;
    private static EditText     targetZone2Side1;
    private static EditText     targetZone3Side1;
    private static EditText     actualZone1Side1;
    private static EditText     actualZone2Side1;
    private static EditText     actualZone3Side1;
    private static ImageView    imageViewCurrentManualMode;
    private static Button       buttonSaveConfigToCurrentMode;
    private static TextView     connectionStatus;

    private USBAccessoryManager accessoryManager;
    Message                     generalMessage;
    private static byte         incrementCounter = 1, incrementDirection = 1;

    Handler timerHandler1 = new Handler();
    Runnable timerRunnable1 = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "entered timerRunnable1 ");
            Message message;
            timerHandler1.postDelayed(this, 1000);  // Re increments the motor every 1 second

            ZONE1_TARGET_SIDE1 = (byte)(ZONE1_PRESET_CONFIG1_SIDE1 + (int)((float)(incrementCounter*(ZONE1_PRESET_CONFIG1A_SIDE1-ZONE1_PRESET_CONFIG1_SIDE1))/100 + 0.45));
            targetZone1Side1.setText("" + ZONE1_TARGET_SIDE1);

            ZONE2_TARGET_SIDE1 = (byte)(ZONE2_PRESET_CONFIG1_SIDE1 + (int)((float)(incrementCounter*(ZONE2_PRESET_CONFIG1A_SIDE1-ZONE2_PRESET_CONFIG1_SIDE1))/100 + 0.45));
            targetZone2Side1.setText("" + ZONE2_TARGET_SIDE1);

            message = Message.obtain(handler, MOTOR1_TARGET_SIDE1);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
            if (handler != null) {
                handler.sendMessage(message);
                // Toast.makeText(this, "MOTOR1_TARGET_SIDE1 sent to Handler", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "timerRunnable1 : MOTOR1_TARGET_SIDE1 sent to Handler");
            }
            // Repeat for second motor port so that the user has the flexibility to plug into either
            message = Message.obtain(handler, MOTOR2_TARGET_SIDE1);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
            if (handler != null) {
                handler.sendMessage(message);
                // Toast.makeText(this, "MOTOR1_TARGET_SIDE1 sent to Handler", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "timerRunnable1 : MOTOR2_TARGET_SIDE1 sent to Handler");
            }

            incrementCounter += incrementDirection;
            if ( ( incrementCounter > 100 ) || ( incrementCounter < 0 ) )
                incrementDirection *= -1;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userInterfaceNumber = MOTOR_CONTROL_INTERFACE;

        connectionStatus = (TextView) findViewById(R.id.connection_status);

        accessoryManager = new USBAccessoryManager(handler, USBAccessoryWhat);
        accessoryManager.enable(this, getIntent());

        Log.d(TAG, "numsides == 1 therefore Inflate GUI for controlling one side only.");

        // FIRSTLY, GREY OUT ANY ZONES WHICH ARE NOT APPLICABLE

        if (  !( ( zonesByte & MOTOR1_MASK) == MOTOR1_MASK) ) { // UH ZONE
            //Inflate the Hidden Layout Information View
            Log.d(TAG, "zonesByte == 0x01, hiding US(zone3) and UP(zone2) side 1 only");

            findViewById(R.id.text_zone1).setAlpha(0.3f);
            findViewById(R.id.button_zone1_up).setAlpha(0.3f);
            findViewById(R.id.button_zone1_up).setClickable(false);
            findViewById(R.id.edit_target_zone1).setAlpha(0.3f);
            findViewById(R.id.edit_actual_zone1).setAlpha(0.3f);
            findViewById(R.id.button_zone1_down).setAlpha(0.3f);
            findViewById(R.id.button_zone1_down).setClickable(false);
        }

        if (  !( ( zonesByte & MOTOR2_MASK) == MOTOR2_MASK) ) { // firmwareDescription == "Neck and Shoulders System"){
            //Inflate the Hidden Layout Information View
            Log.d(TAG, "zonesByte -> ZONE2_MASK missing (UP) side 1 only");

            findViewById(R.id.text_zone2).setAlpha(0.3f);
            findViewById(R.id.button_zone2_up).setAlpha(0.3f);
            findViewById(R.id.button_zone2_up).setClickable(false);
            findViewById(R.id.edit_target_zone2).setAlpha(0.3f);
            findViewById(R.id.edit_actual_zone2).setAlpha(0.3f);
            findViewById(R.id.button_zone2_down).setAlpha(0.3f);
            findViewById(R.id.button_zone2_down).setClickable(false);
        }

        if (  !( ( zonesByte & MOTOR3_MASK) == MOTOR3_MASK) ) { // firmwareDescription == "Neck, Shoulders and Pelvis System"){
            //Inflate the Hidden Layout Information View
            Log.d(TAG, "zonesByte == 0x03, showing UH (zone1) and UP (zone2) whilst hiding US (zone3) - one side only");

            findViewById(R.id.text_zone3).setAlpha(0.3f);
            findViewById(R.id.button_zone3_up).setAlpha(.3f);
            findViewById(R.id.button_zone3_up).setClickable(false);
            findViewById(R.id.edit_target_zone3).setAlpha(.3f);
            findViewById(R.id.edit_actual_zone3).setAlpha(.3f);
            findViewById(R.id.button_zone3_down).setAlpha(.3f);
            findViewById(R.id.button_zone3_down).setClickable(false);
        }

        // NEXT, SET VALUES FOR THE EDIT TEXT BOXES

        if ( ( zonesByte & MOTOR1_MASK) == MOTOR1_MASK) {
            targetZone1Side1 = (EditText) findViewById(R.id.edit_target_zone1);
            targetZone1Side1.setText("" + ZONE1_TARGET_SIDE1);
            actualZone1Side1 = (EditText) findViewById(R.id.edit_actual_zone1);
            actualZone1Side1.setText("" + ZONE1_ACTUAL_SIDE1);
        }

        if ( ( zonesByte & MOTOR2_MASK) == MOTOR2_MASK){
            targetZone2Side1 = (EditText) findViewById(R.id.edit_target_zone2);
            targetZone2Side1.setText("" + ZONE2_TARGET_SIDE1);
            actualZone2Side1 = (EditText) findViewById(R.id.edit_actual_zone2);
            actualZone2Side1.setText("" + ZONE2_ACTUAL_SIDE1);

        }
        if ( ( zonesByte & MOTOR3_MASK) == MOTOR3_MASK )  {
            targetZone3Side1 = (EditText) findViewById(R.id.edit_target_zone3);
            targetZone3Side1.setText("" + ZONE3_TARGET_SIDE1);
            actualZone3Side1 = (EditText) findViewById(R.id.edit_actual_zone3);
            actualZone3Side1.setText("" + ZONE3_ACTUAL_SIDE1);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        finish();
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause()");
        try {
            Thread.sleep(10);
            // End any actions and call backs here...

            Log.d(TAG, "onPause() post mpAudio.pause()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    @Override
    public void onStop() {
        timerHandler1.removeCallbacks(timerRunnable1);
 // Release any audio, e.g.
        /*
        if (mpAudio != null) {
            //  Toast.makeText(this, "onStop() is releasing audio", Toast.LENGTH_SHORT).show();

            mpAudio.release();
            Log.d(TAG, "onStop() post mpAudio.pause()");
        }*/
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putByte("ZONE1_PRESET_CONFIG2_SIDE1", ZONE1_PRESET_CONFIG2_SIDE1);
        savedInstanceState.putByte("ZONE1_PRESET_CONFIG1_SIDE1", ZONE1_PRESET_CONFIG1_SIDE1);
        savedInstanceState.putByte("ZONE1_PRESET_CONFIG0_SIDE1", ZONE1_PRESET_CONFIG0_SIDE1);
        savedInstanceState.putByte("ZONE1_PRESET_CONFIG1A_SIDE1", ZONE1_PRESET_CONFIG1A_SIDE1);
        savedInstanceState.putByte("ZONE1_PRESET_CONFIG2A_SIDE1", ZONE1_PRESET_CONFIG2A_SIDE1);

        super.onSaveInstanceState(savedInstanceState); // Do this after "putting" the data above

    }

    public void disconnectAccessory() {
        if (deviceAttached == false) {
            return;
        }
        Log.d(TAG, "disconnectAccessory()");
        this.setTitle("Nauchtan: Now Disconnected.");
    }

    @Override
    public void onDestroy() {
        //accessoryManager = null;
        //onSaveInstanceState(null);
        byte[] commandPacket = new byte[2];
        commandPacket[0] = (byte) APP_DISCONNECT;
        commandPacket[1] = 0;
        accessoryManager.write(commandPacket);
        Log.d(TAG, "onDestroy() post accessoryManager.write()");

        accessoryManager.disable(this);
     //todo:   timerHandlerIsConnectedRecursive.removeCallbacks(timerRunnableIsConnectedRecursive);

        //Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] commandPacket = new byte[2];

            switch(msg.what)
            {

                case MOTOR1_TARGET_SIDE1:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"MOTOR1_TARGET_SIDE1 not sent to accessory - not connected");
                        return;
                    }
                    // Populate commandPacket and Write ZONE1_TARGET_SIDE1 to USB
                    commandPacket[0] = MOTOR1_TARGET_SIDE1;
                    commandPacket[1] = ZONE1_TARGET_SIDE1;
                    accessoryManager.write(commandPacket);
                    Log.d(TAG,"MOTOR1_TARGET_SIDE1 Message from Handler to Accessory");
                    break;

                case MOTOR1_TARGET_SIDE2:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"MOTOR1_TARGET_SIDE2 not sent to accessory - not connected");
                        return;
                    }
                    Log.d(TAG,"MOTOR1_TARGET_SIDE2 Message from Handler to Accessory");
                    // Populate commandPacket and Write ZONE1_TARGET_SIDE1 to USB
                    commandPacket[0] = MOTOR1_TARGET_SIDE2;
                    accessoryManager.write(commandPacket);
                    break;

                case MOTOR2_TARGET_SIDE1:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"MOTOR2_TARGET_SIDE1 not sent to accessory - not connected");
                        return;
                    }
                    Log.d(TAG,"MOTOR2_TARGET_SIDE1 Message from Handler to Accessory = "+ZONE2_TARGET_SIDE1);
                    // Populate commandPacket and Write TARGET_HEIGHT_SIDE1 to USB
                    commandPacket[0] = MOTOR2_TARGET_SIDE1;
                    commandPacket[1] = ZONE2_TARGET_SIDE1;
                    accessoryManager.write(commandPacket);
                    break;


                case MOTOR3_TARGET_SIDE1:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"MOTOR3_TARGET_SIDE1 not sent to accessory - not connected");
                        return;
                    }
                    Log.d(TAG,"MOTOR3_TARGET_SIDE1 Message from Handler to Accessory");
                    // Populate commandPacket and Write TARGET_HEIGHT_SIDE1 to USB
                    commandPacket[0] = MOTOR3_TARGET_SIDE1;
                    commandPacket[1] = ZONE3_TARGET_SIDE1;
                    accessoryManager.write(commandPacket);
                    break;


                case GOTO_PRESET_SIDE1:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"GOTO_PRESET_SIDE1 not sent to accessory - not connected, data = " + autoManual1);
                        return;
                    }
                    // Populate commandPacket and Write TARGET_HEIGHT_SIDE1 to USB
                    commandPacket[0] = GOTO_PRESET_SIDE1;
                    commandPacket[1] = autoManual1;
                    accessoryManager.write(commandPacket);
                    Log.d(TAG,"GOTO_PRESET_SIDE1 Message from Handler to Accessory, data = " + autoManual1);
                    break;

                case ZERO_AND_SPAN_ALL:
                    if(accessoryManager.isConnected() == false) {
                        Log.d(TAG,"ZERO_AND_SPAN_ALL Message NOT sent from Handler to Accessory - NOT CONNECTED");
                        return;
                    }
                    commandPacket[0] = ZERO_AND_SPAN_ALL;
                    commandPacket[1] = 0;
                    accessoryManager.write(commandPacket);
                    Log.d(TAG,"ZERO_AND_SPAN_ALL Message sent from Handler to Accessory");
                    break;

                case REZERO_ALL_LIFTERS:
                    if(accessoryManager.isConnected() == false) {
                        return;
                    }
                    commandPacket[0] = REZERO_ALL_LIFTERS;
                    commandPacket[1] = 0;
                    accessoryManager.write(commandPacket);
                    Log.d(TAG,"REZERO_ALL_LIFTERS Message sent from Handler to Accessory");
                    break;


                case USBAccessoryWhat:
                    switch(((USBAccessoryManagerMessage)msg.obj).type) {
                        case READ:
                            if(accessoryManager.isConnected() == false) {
                                return;
                            }

                            while(true) {  // not an infinite loop because of the "break" statement
                                if(accessoryManager.available() < 2) {
                                    //All of our commands in this example are 2 bytes.  If there are less
                                    //  than 2 bytes left, it is a partial command
                                    break;
                                }

                                accessoryManager.read(commandPacket);

                                switch(commandPacket[0]) {


                                    case ACTUAL_POSITION_MOTOR1_SIDE1:
                                        ZONE1_ACTUAL_SIDE1 = commandPacket[1];
                                        Log.d(TAG,"ACTUAL_POSITION_MOTOR1_SIDE1 command received from Accessory");
                                        if( ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE ) ) // && ( actualZone1Side1.isCursorVisible() )
                                        {
                                            actualZone1Side1.setText("" + ZONE1_ACTUAL_SIDE1);
                                        }
                                        break;

                                    case ACTUAL_POSITION_MOTOR2_SIDE1:
                                        ZONE2_ACTUAL_SIDE1 = commandPacket[1];
                                        Log.d(TAG,"ACTUAL_POSITION_MOTOR2_SIDE1 command received from Accessory");
                                        if ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE )
                                            actualZone2Side1.setText(""+ ZONE2_ACTUAL_SIDE1);
                                        break;


                                    case MOTOR1_TARGET_SIDE1:
                                        ZONE1_TARGET_SIDE1 = commandPacket[1];
                                        Log.d(TAG,"MOTOR1_TARGET_SIDE1 command received from Accessory with value: "+commandPacket[1]);
                                        if ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE ) {
                                            targetZone1Side1.setText("" + ZONE1_TARGET_SIDE1);
                                        }
                                        break;

                                    case MOTOR2_TARGET_SIDE1:
                                        ZONE2_TARGET_SIDE1 = commandPacket[1];
                                        Log.d(TAG,"MOTOR2_TARGET_SIDE1 command received from Accessory with value: "+commandPacket[1]);
                                        if ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE )
                                            targetZone2Side1.setText(""+ ZONE2_TARGET_SIDE1);
                                        break;



                                } // end switch commandPacket[0] (within USBAccessoryWhat/Read)

                            } // end while (within USBAccessoryWhat/Read)
                            break;

                        case CONNECTED:
                            Log.d(TAG, "USBAccesoryWhat: case CONNECTED");
                            break;

                        case READY:
                            Log.d(TAG, "USBAccesoryWhat= READY. Now check version before sending APP_CONNECT msg.");

                            String version = ((USBAccessoryManagerMessage)msg.obj).accessory.getVersion();
                            firmwareDescription = ((USBAccessoryManagerMessage)msg.obj).accessory.getDescription();
                            firmwareProtocol = getFirmwareProtocol(version);
                            numSides = getNumSides(firmwareDescription);

                            Log.d( TAG,"numSides = "+ numSides );

                            zonesByte = (byte) getZonesByte(firmwareDescription);
                            Log.d(TAG,"zonesByte = " + zonesByte);

                            numZones = (int)( zonesByte & MOTOR1_MASK) + (int)( ( zonesByte & MOTOR2_MASK) != 0x00 ? (zonesByte & MOTOR2_MASK) >> 1 :0) + (int)( (zonesByte & MOTOR3_MASK) != 0 ? (zonesByte & MOTOR3_MASK)>> 2 : 0);
                            Log.d(TAG,"numZones = "+numZones);

                            deviceAttached = true;
                            commandPacket[0] = (byte) APP_CONNECT;
                            commandPacket[1] = 0;
                            //Log.d(TAG,"sending connect message.");
                            accessoryManager.write(commandPacket);
                            Log.d(TAG,"APP_CONNECT message sent to accessory.");

                            //deflateAnyUserInterface();
                            //inflateControlUI();

                            if ( accessoryManager.isConnected() == true )
                                connectionStatus.setText("CONNECTED"); // This is also done in inflateControlUI() BUT WAS ERRONEOUSLY SAYING "NOT CONNECTED"
                            else
                                connectionStatus.setText("NOT CONNECTED");

                         //todo:   timerHandlerInitialiseOrientation.postDelayed(timerRunnableInitialiseOrientation, 2000);  // This one relates to the inital setting of the target on the Control UI to match the actual.

                            break;

                        case DISCONNECTED:
                            Log.d(TAG, "USBAccessoryWhat: case DISCONNECTED");
                            disconnectAccessory();
                            break;
                    }		// end switch within USBAccessoryWhat

                    break;
                default:
                    break;
            }	// end switch msg.what
        } //handleMessage
    }; //handler




    private int getFirmwareProtocol(String version) {

        String major = "0";

        int positionOfDot;

        positionOfDot = version.indexOf('.');
        if(positionOfDot != -1) {
            major = version.substring(0, positionOfDot);
        }

        return new Integer(major).intValue();
    }


    private int getNumSides(String version) {

        String major = "0";

        int positionOf_x;

        positionOf_x = version.indexOf('x');
        if(positionOf_x != -1) {
            major = version.substring(version.length()-1);
        }
        return new Integer(major).intValue();  // TODO: Validate this
    }


    private int getZonesByte(String firmwareDescript) {

        String zone_byte = "0";

        int positionOf_x;

        positionOf_x = firmwareDescript.indexOf('x');
        if(positionOf_x != -1) {
            zone_byte = firmwareDescript.substring(positionOf_x-1,positionOf_x);
        }

        return new Integer(zone_byte).intValue();  // TODO: Validate this
    }


    public void onSaveConfig1(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to change CONFIG 1?")

                .setTitle("Save Confirmation")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                if  ( ( zonesByte & MOTOR1_MASK) == MOTOR1_MASK)
                                    ZONE1_PRESET_CONFIG1_SIDE1 = ZONE1_TARGET_SIDE1;

                                if  ( ( zonesByte & MOTOR2_MASK) == MOTOR2_MASK)
                                    ZONE2_PRESET_CONFIG1_SIDE1 = ZONE2_TARGET_SIDE1;

                                if  ( ( zonesByte & MOTOR3_MASK) == MOTOR3_MASK)
                                    ZONE3_PRESET_CONFIG1_SIDE1 = ZONE3_TARGET_SIDE1;

                        /* This stuff is unnecessary for the sausage roller demo - but can be used to store preset configurations to the control unit
                                generalMessage = Message.obtain(handler, CONFIG1_PRESET_MOTOR1);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
                                if(handler != null)
                                {
                                    handler.sendMessage(generalMessage);
                                    //Toast.makeText(this, "CONFIG2_PRESET_MOTOR1 sent to Handler", Toast.LENGTH_SHORT).show();
                                }

                                generalMessage = Message.obtain(handler, CONFIG1_PRESET_MOTOR2);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
                                if(handler != null)
                                {
                                    handler.sendMessage(generalMessage);
                                    //Toast.makeText(this, "CONFIG2_PRESET_MOTOR2 sent to Handler", Toast.LENGTH_SHORT).show();
                                }*/
                                ZONE1_SAVED_TARGET_SIDE1 = ZONE1_TARGET_SIDE1;
                                ZONE2_SAVED_TARGET_SIDE1 = ZONE2_TARGET_SIDE1;

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
                .setCancelable(false);
        builder.create().setCanceledOnTouchOutside(false);
        builder.create().show();
    }

    public void onSaveConfig1A(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Are you sure you want to change CONFIG 1A?")
                .setTitle("Save Confirmation")
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                if  ( ( zonesByte & MOTOR1_MASK) == MOTOR1_MASK)
                                    ZONE1_PRESET_CONFIG1A_SIDE1 = ZONE1_TARGET_SIDE1;

                                if  ( ( zonesByte & MOTOR2_MASK) == MOTOR2_MASK)
                                    ZONE2_PRESET_CONFIG1A_SIDE1 = ZONE2_TARGET_SIDE1; // ZONE2_PRESET_CONFIG1_SIDE1;

                                if  ( ( zonesByte & MOTOR3_MASK) == MOTOR3_MASK)
                                    ZONE3_PRESET_CONFIG1A_SIDE1 = ZONE3_TARGET_SIDE1;

                            /* This stuff below is unnecessary for the sausage roller demo - but can be used to save motor configuration in the memory of the control box
                                generalMessage = Message.obtain(handler, CONFIG1A_PRESET_MOTOR1);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
                                if(handler != null)
                                {
                                    handler.sendMessage(generalMessage);
                                    //Toast.makeText(this, "CONFIG1A_PRESET_MOTOR1 sent to Handler", Toast.LENGTH_SHORT).show();
                                }

                                generalMessage = Message.obtain(handler, CONFIG1A_PRESET_MOTOR2);  // Use handler code to write the current position number to sample. The Accessory can record this in the Training Dataset
                                if(handler != null)
                                {
                                    handler.sendMessage(generalMessage);
                                    //Toast.makeText(this, "CONFIG1A_PRESET_MOTOR2 sent to Handler", Toast.LENGTH_SHORT).show();
                                }*/

                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                })
                .setCancelable(false);
        builder.create().setCanceledOnTouchOutside(false);
        builder.create().show();
    }

    public void onStartRollingSausages(View view){

        ZONE1_TARGET_SIDE1 = ZONE1_SAVED_TARGET_SIDE1;
        updateZone1Target();

        ZONE2_TARGET_SIDE1 = ZONE2_SAVED_TARGET_SIDE1;
        updateZone2Target();

        timerHandler1.postDelayed(timerRunnable1, 1000);
    }

    public void onZone1Up(View view) {
        if(ZONE1_TARGET_SIDE1 <100)
            ZONE1_TARGET_SIDE1 +=1;
        updateZone1Target();
    }

    public void onZone2Up(View view) {
        if( ZONE2_TARGET_SIDE1 < 100 )
            ZONE2_TARGET_SIDE1 +=1;
        updateZone2Target();
    }


    public void onZone3Up(View view) {
        if(ZONE3_TARGET_SIDE1 <100)
            ZONE3_TARGET_SIDE1 +=1;
        updateZone3Target();
    }


    public void onZone1Down(View view) {
        if(ZONE1_TARGET_SIDE1 >0)
            ZONE1_TARGET_SIDE1 -=1;
        updateZone1Target();
    }

    public void onZone2Down(View view) {
        if(ZONE2_TARGET_SIDE1 >0)
            ZONE2_TARGET_SIDE1 -=1;
        updateZone2Target();
    }


    public void onZone3Down(View view) {

        if(ZONE3_TARGET_SIDE1 >0)
            ZONE3_TARGET_SIDE1 -=1;
        updateZone3Target();
    }

    public void updateZone1Target() {
        Log.d(TAG,"arrived at updateZone1Target()");
        // Update the adjustable manual target value on the GUI
        if ( ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE )  )
                targetZone1Side1.setText("" + ZONE1_TARGET_SIDE1);
        // Send message via handler method
        Message message = Message.obtain(handler, MOTOR1_TARGET_SIDE1);
        if(handler != null) {
            handler.sendMessage(message);
        }
     }


    public void updateZone2Target() {
        Log.d(TAG,"arrived at updateZone2Target() 2");
        // Update the adjustable manual target value on the GUI
        if ( ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE ) ) {
            targetZone2Side1.setText("" + ZONE2_TARGET_SIDE1); // ZONE2_TARGET_SIDE1
            Log.d(TAG,"arrived at updateZone2Target(), ZONE2_TARGET_SIDE1 = " + ZONE2_TARGET_SIDE1);
        }
        // Send message via handler method
        Message updateTarget = Message.obtain(handler, MOTOR2_TARGET_SIDE1);
        if(handler != null) {
            handler.sendMessage(updateTarget);
        }
    }


    public void updateZone3Target() {
        Log.d(TAG,"arrived at updateZone3Target()");
        // Update the adjustable manual target value on the GUI
        if ( ( userInterfaceNumber == MOTOR_CONTROL_INTERFACE ) )
            targetZone3Side1.setText("" + ZONE3_TARGET_SIDE1);
        // Send message via handler method
        Message updateTarget = Message.obtain(handler, MOTOR3_TARGET_SIDE1);
        if(handler != null) {
            handler.sendMessage(updateTarget);
        }
    }



}
