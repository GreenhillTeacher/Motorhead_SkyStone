package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.AutonDrivingDustBowlRefugee;

//import org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode.Auton.AutonDrivingDustBowlRefugee;

//@Disabled
@Autonomous(name="TestDriving", group="AutonTest")
public class TestDriving extends AutonDrivingDustBowlRefugee {
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

        gyroDrive(25, readAngle("z"), true, gyroDriveSpeedSlow, moderate);

        turnDegrees(90,"z", turnSpeed,5);

        sleep(100);

        gyroDrive(20, readAngle("z"), true, gyroDriveSpeedSlow, moderate);


        pathComplete(500);
    }
}