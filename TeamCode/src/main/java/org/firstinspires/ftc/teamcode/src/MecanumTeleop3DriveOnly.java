package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import legacy.MecanumHardware3;


@TeleOp(name="MecanumTeleop3DriveOnly", group="MecanumBot3")
//@Disabled
@Disabled

public class MecanumTeleop3DriveOnly extends OpMode {

    MecanumHardware3DriveOnly robot = new MecanumHardware3DriveOnly();

    private float drive = .8f;

    @Override
    public void init()
    {
        //Initialize the hardware variables.
        //The init() method of the hardware class does all the work here
        robot.init(hardwareMap);
    }

    @Override
    public void loop()

    {

        mecanumMove();
        //telemetry.addData("Distance Sensor", robot.distSen.getDistance(DistanceUnit.INCH));
        /*if(gamepad1.a)
        {
            robot.servo1.setPosition(0);
            robot.servo2.setPosition(1);
        }
        if(gamepad1.b)
        {
            robot.servo1.setPosition(1);
            robot.servo2.setPosition(0);
        }*/

        telemetry.update();
    }

    public void mecanumMove()
    {
        //variables
        double r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
        double robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = gamepad1.right_stick_x;
        final double v1 = r * Math.cos(robotAngle) + rightX;
        final double v2 = r * Math.sin(robotAngle) - rightX;
        final double v3 = r * Math.sin(robotAngle) + rightX;
        final double v4 = r * Math.cos(robotAngle) - rightX;

        robot.fLMotor.setPower(-drive * v1);
        robot.fRMotor.setPower(-drive * v2);
        robot.bLMotor.setPower(-drive * v3);
        robot.bRMotor.setPower(-drive * v4);
    }
}