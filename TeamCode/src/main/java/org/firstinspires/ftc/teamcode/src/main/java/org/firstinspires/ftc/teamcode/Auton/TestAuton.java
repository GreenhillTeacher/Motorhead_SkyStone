package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton;

import android.database.sqlite.SQLiteException;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
//import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton.AutonDrivingDustBowlRefugee;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.AutonDrivingDustBowlRefugee;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

//@Disabled
@Autonomous(name="TestAuton", group="AutonTest")
public class TestAuton extends AutonDrivingDustBowlRefugee {
//    AutonDrivingDriveOnly auton = new AutonDrivingDriveOnly();

    //SkyStoneHardwareDrivingOnly robot = new SkyStoneHardwareDrivingOnly();
    @Override
    public void runOpMode() {

        //init
        robot.init(hardwareMap);
        BNO055IMU.Parameters p = new BNO055IMU.Parameters();
        p.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        p.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        p.calibrationDataFile = "BNO055IMUCalibration.json";
        p.loggingEnabled = true;
        p.loggingTag = "IMU";
        p.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(p);

        //side motors
        robot.fLMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.fRMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.fLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.fRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //lateral motors
        robot.bLMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.bRMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.bLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.bRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         * We can pass Vuforia the handle to a camera preview resource (on the RC phone);
         * If no camera monitor is desired, use the parameter-less constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Load the data sets for the trackable objects. These particular data
        // sets are stored in the 'assets' part of our application.
        VuforiaTrackables targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");

        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");
        /*VuforiaTrackable blueRearBridge = targetsSkyStone.get(1);
        blueRearBridge.setName("Blue Rear Bridge");
        VuforiaTrackable redRearBridge = targetsSkyStone.get(2);
        redRearBridge.setName("Red Rear Bridge");
        VuforiaTrackable redFrontBridge = targetsSkyStone.get(3);
        redFrontBridge.setName("Red Front Bridge");
        VuforiaTrackable blueFrontBridge = targetsSkyStone.get(4);
        blueFrontBridge.setName("Blue Front Bridge");
        VuforiaTrackable red1 = targetsSkyStone.get(5);
        red1.setName("Red Perimeter 1");
        VuforiaTrackable red2 = targetsSkyStone.get(6);
        red2.setName("Red Perimeter 2");
        VuforiaTrackable front1 = targetsSkyStone.get(7);
        front1.setName("Front Perimeter 1");
        VuforiaTrackable front2 = targetsSkyStone.get(8);
        front2.setName("Front Perimeter 2");
        VuforiaTrackable blue1 = targetsSkyStone.get(9);
        blue1.setName("Blue Perimeter 1");
        VuforiaTrackable blue2 = targetsSkyStone.get(10);
        blue2.setName("Blue Perimeter 2");
        VuforiaTrackable rear1 = targetsSkyStone.get(11);
        rear1.setName("Rear Perimeter 1");
        VuforiaTrackable rear2 = targetsSkyStone.get(12);
        rear2.setName("Rear Perimeter 2");*/

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        robot.claw.setPosition(clawPos);
        waitForStart();






        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
        startAngle = readAngle("z");
        setDir();


        gyroDrive(14, NORTH, true, gyroDriveSpeed - .02, moderate, 10);//13 is the only distance that works pls no change

        turnToPosition(NORTH, "z", turnSpeed + .05, 5, false);


        String skystone = vuforia(allTrackables, targetsSkyStone);

        //sleep(1000);

        telemetry.addData("Position", skystone);
        telemetry.update();

        sleep(100);


        //turnDegrees(90, "z", turnSpeed, 5);


        if(skystone.equals("center")) {
            strafe(4, .6, left, leftBal + .01, 362.5, .04);


            sleep(100);
            turnToPosition(NORTH + 31, "z", turnSpeed, 10, true);


            robot.intakeL.setPower(1);
            robot.intakeR.setPower(1);
            gyroDrive(30, NORTH + 31, true, gyroDriveSpeed, moderate + .03, 5);


        }

        //TODO: WORK ON ALT SKYSTONE POSITIONS
        /*else if(skystone.equals("left"))
        {
            strafe(4, .6, left, leftBal + .01, 362.5, .04);


            sleep(100);
            turnToPosition(NORTH + 15, "z", turnSpeed, 10, false);


            robot.intakeL.setPower(1);
            robot.intakeR.setPower(1);
            gyroDrive(30, NORTH + 15, true, gyroDriveSpeed, moderate + .03);
        }
        else
        {
            strafe(4, .6, left, leftBal + .01, 362.5, .04);


            sleep(100);
            turnToPosition(NORTH + 40, "z", turnSpeed, 10, false);


            robot.intakeL.setPower(1);
            robot.intakeR.setPower(1);
            gyroDrive(30, NORTH + 40, true, gyroDriveSpeed, moderate + .03);
        }*/
        //sleep(100);
        //turnToPosition(EAST, "z", 1, 5, true);


        sleep(100);


        //gyroDrive(-10, );

        strafe(1, .9, right, .05, 2600, .03);
        robot.intakeL.setPower(0);
        robot.intakeR.setPower(0);

        turnToPosition(178, "z", turnSpeed, 5, false);

        sleep(100);
        gyroDrive(-25, SOUTH, true, gyroDriveSpeed, moderate,10);

        robot.latch.setPosition(0);

        sleep(1000);

        //TODO: WTF IS UP W/ THIS
        turnToPosition(EAST, "z", .5, 5, false, true);

        //turnDegrees(90, "z", turnSpeed, 5);
        //sleep(100);
        //turnDegrees(25, "z", turnSpeed, 10);
        //gyroDrive(-5, EAST, true, gyroDriveSpeedSlow, slow);

        //sleep(1000);
        //gyroDrive(29, 0);
        //gyroDriveStrafe(10, 0);
        //gyroDrive(47.5, 0);

        //line up with center foundation
        //gyroDrive(-12.5, gyroDriveThreshold);

        //gyroDrive(20, readAngle("z"), true, gyroDriveSpeedSlow, moderate);

        //turnToPosition(-90, "z", 1, 7);

        //gyroDrive(20,readAngle("z"), true, gyroDriveSpeedSlow, moderate);
//        sleep(100);
//
//        //strafe away from wall
//        robot.fLMotor.setPower(.6);
//        robot.bLMotor.setPower(-.6);
//        robot.fRMotor.setPower(-.6);
//        robot.bRMotor.setPower(.6);
//
//        sleep(250);
//
//        normalDrive(0,0);
//
//        sleep(100);
//
//        //turn towards foundation
//        turnToPosition(-90, "z", turnSpeed, negative90timeout);
//
//        //drive to foundation and latch
//        gyroDrive(-25.5, .5);
//
//        sleep(100);
//        //fix tilting with gyrodrive
//        turnToPosition(-90, "z", turnSpeed, negative90timeout);
//
//        //latch
//        latch(true);
//
//        sleep(600);
//
//        //gyroDrive(15, .5);
//        //drive back and turn
//        //gyroDrive(15, gyroDriveThreshold);
//        //drive back
//        robot.fLMotor.setPower(.3);
//        robot.fRMotor.setPower(.3);
//        robot.bLMotor.setPower(.3);
//        robot.bRMotor.setPower(.3);
//        sleep(1300);
//        normalDrive(0, 0);
//
//        turnToPosition(-90, "z", turnSpeed, negative90timeout);
//        //turnDegrees(90, "z", turnSpeed, 10);

        pathComplete(500);
    }
}