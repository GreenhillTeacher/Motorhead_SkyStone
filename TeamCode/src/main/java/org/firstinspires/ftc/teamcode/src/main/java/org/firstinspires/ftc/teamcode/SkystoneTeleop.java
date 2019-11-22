package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="SkystoneTeleOp", group="Skystone")
//@Disabled

public class SkystoneTeleop extends OpMode {

    SkyStoneHardware robot = new SkyStoneHardware();

    static final double     COUNTS_PER_MOTOR_REV = 1120 ;    // Currently: Andymark Neverest 40
    static final double     COUNTS_PER_REV_ARM = 1440;
    static final double     COUNTS_PER_INCH_ARM = COUNTS_PER_REV_ARM/4;
    static final double     DRIVE_GEAR_REDUCTION = 1;     // This is < 1.0 if geared UP //On OUR CENTER MOTOR THE GEAR REDUCTION IS .5
    static final double     WHEEL_DIAMETER_INCHES = 4;     // For figuring circumference
    static final double     COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    //double susanPower = .45;
    double liftPower = 1;

    @Override
    public void init() {
        //Initialize the hardware variables.
        //The init() method of the hardware class does all the work here
        robot.init(hardwareMap);
    }

    @Override
    public void loop()
    {
        mecanumMove();

        //arm lift
        if(gamepad1.dpad_up)
        {
            robot.armLift.setPower(-liftPower);
        }
        else if(gamepad1.dpad_down)
        {
            robot.armLift.setPower(liftPower);
        }
        else
        {
            robot.armLift.setPower(0);
        }

        //arm extension
        if(gamepad2.left_trigger >= .5)
        {
            robot.armExt.setPower(-.5);
        }
        else if(gamepad2.right_trigger >= .5)
        {
            robot.armExt.setPower(.5);
        }
        else
        {
            robot.armExt.setPower(0);
        }

        //claw grab
        if(gamepad2.a)
        {
            //open
            robot.claw.setPosition(1);
        }
        else if(gamepad2.b)
        {
            //close
            robot.claw.setPosition(0);
        }

        //wrist movement
        if(gamepad2.x)
        {
            robot.wrist.setPosition(1);
        }
        else if(gamepad2.y)
        {
            robot.wrist.setPosition(.5);
        }
    }

    public void mecanumMove() {
        //variables
        double drive = .8f;
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