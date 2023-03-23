package com.ielson.djiBote;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.FlightMode;
import dji.common.flightcontroller.simulator.SimulatorState;
import dji.common.util.CommonCallbacks;
import dji.midware.data.model.P3.DataFlycGetPushFlightRecord;
import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;
import dji.sdk.flightcontroller.FlightController;
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.flightcontroller.FlightOrientationMode;




public class droneController {
    private static BaseProduct product;
    public static FlightController mFlightController;

    public static double positionX;
    public static double positionY;
    public static double positionZ;
    public static double roll;
    public static double pitch;
    public static double yaw;
    public static int headDirection;
    public static int flightTime;
    public static int goHomeHeight;
    public static double xVelocity;
    public static double yVelocity;
    public static double zVelocity;




        /*if (MainActivity.state == MainActivity.State.TAKEOFFCOMPLETE) {
            if (!flightControllerState.isFlying()) {
                Log.d("controller", "drone not flying");
            } else {
                if (flightControllerState.getFlightMode() == FlightMode.AUTO_TAKEOFF) {
                    Log.d("controller", "still taking off");
                } else {
                    Log.d("controller", "took off");
                    MainActivity.state = MainActivity.State.VIRTUALSTICKSTART;
                }
            }
        }*/

    /*MainActivity.mFlightController.getSimulator().setStateCallback(new SimulatorState.Callback() {
        @Override
        public void onUpdate(@NonNull SimulatorState simulatorState) {
            Log.d("controller simulator", "received simulator state callback");
            Log.d("controller simulator", "some values: altitude: " + simulatorState.getPositionZ());

        }
    });*/


    // gets all new values at the same time and send them to the screen
    public static void updateSensorValues(FlightControllerState flightControllerState) {

        Log.d("FLOW controller", "onUpdateSensorValues");
        yaw = flightControllerState.getAttitude().yaw;
        pitch = flightControllerState.getAttitude().pitch;
        roll = flightControllerState.getAttitude().roll;
        positionX = flightControllerState.getAircraftLocation().getLatitude();
        positionY = flightControllerState.getAircraftLocation().getLongitude();
        positionZ = flightControllerState.getAircraftLocation().getAltitude();


        headDirection = flightControllerState.getAircraftHeadDirection();
        flightTime = flightControllerState.getFlightTimeInSeconds();
        goHomeHeight = flightControllerState.getGoHomeHeight();
        xVelocity = flightControllerState.getVelocityX();
        yVelocity = flightControllerState.getVelocityY();
        zVelocity = flightControllerState.getVelocityZ();

        Log.d("Controller:", "yaw: " + yaw);
        Log.d("Controller:", "pitch: " + pitch);
        Log.d("Controller:", "roll: " + roll);
        Log.d("Controller:", "posX: " + positionX);
        Log.d("Controller:", "posy: " + positionY);
        Log.d("Controller:", "posZ: " + positionZ);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                MainActivity.mTextView.setText("xvel : " + String.format("%.2f", xVelocity) + ", yvel : " + String.format("%.2f", yVelocity) + ", zvel : " + String.format("%.2f", zVelocity) + "\n" +
                        ", PosX : " + String.format("%.2f", positionX) + ", PosY : " + String.format("%.2f", positionY) + ", PosZ : " + String.format("%.2f", positionZ));
            }
        });
    }

    public static void initFlightController() {
        product = ConnectionActivity.mProduct;
        Log.d("FLOW controller", "init Flight Controller");
        //talker = new Talker("position", MainActivity.this);
        //Log.d("Flow main", "Talker created");
        Log.d("FLOW debug", "product: " +product + " connected? " +product.isConnected());
        if (product != null && product.isConnected()) {
            if (product instanceof Aircraft) {
                mFlightController = ((Aircraft) product).getFlightController();
                Log.d("FLOW main", "got product flight controller");
            }
            // colocar mensagem de erro se nao for, dizer qual o erro e tal
        }
        if (mFlightController != null) {
            Log.d("FLOW main", "setting flight controller modes");
            mFlightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY);
            mFlightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY);
            mFlightController.setVerticalControlMode(VerticalControlMode.VELOCITY);
            mFlightController.setRollPitchCoordinateSystem(FlightCoordinateSystem.BODY);
            mFlightController.setFlightOrientationMode(FlightOrientationMode.AIRCRAFT_HEADING, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        Log.d("FLOW main", "Flight orientation set to aircraft heading");
                    }
                    else {
                        Log.e("STICK orientation", "error : " + djiError.getDescription());
                    }
                }
            });
//       rosDjiCameraPreviewView = new RosDjiCameraPreviewView(this.getApplicationContext());
            /*if (useSimulator) {
                Log.d("Flow main", "using simulator");
                mFlightController.getSimulator().start(InitializationData.createInstance(new LocationCoordinate2D(-12.97, -38.51), 10, 10), new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        if (djiError != null) {
                            Toast.makeText(MainActivity.this, djiError.getDescription(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error starting simulator: " + djiError.getDescription());
                        } else {
                            Log.d("FLOW main", "Simulator created");
                            Toast.makeText(MainActivity.this, "Started Simulator", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }*/
            // cria um timer para mandar comandos para o virtualStickData(botoes de controle na tela) de tempos em tempos
            /*if (null == mSendVirtualStickDataTimer) {

                mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                mSendVirtualStickDataTimer = new Timer();

                // tirando os envios do VirtualStick por enquanto
                //mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 100, 200);
                Log.d("FLOW main", "Created mSendVirtualDataTimer");
            }*/
        }
    }
}
