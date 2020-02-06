package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
//import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton.AutonDrivingDustBowlRefugee;
import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.AutonDrivingDustBowlRefugee;

@Disabled
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

        waitForStart();
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
        startAngle = readAngle("z");

        //gyroDrive(29, 0);
        //gyroDriveStrafe(10, 0);
        //gyroDrive(47.5, 0);

        //line up with center foundation
        //gyroDrive(-12.5, gyroDriveThreshold);

        gyroDrive(20, readAngle("z"), true, gyroDriveSpeedSlow, true);

        turnToPosition(-90, "z", 1, 7);

        gyroDrive(20,readAngle("z"), true, gyroDriveSpeedSlow, true);
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