package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

//@Disabled
@Autonomous(name="ServoTest", group="Test")
public class ServoTest extends LinearOpMode {
//    AutonDrivingDriveOnly auton = new AutonDrivingDriveOnly();

    ServoHardware robot = new ServoHardware();
    public void runOpMode() {

        //init
        robot.init(hardwareMap);
        waitForStart();
        robot.servo1.setPosition(180);
        sleep(1000);
        robot.servo1.setPosition(0);
    }
}