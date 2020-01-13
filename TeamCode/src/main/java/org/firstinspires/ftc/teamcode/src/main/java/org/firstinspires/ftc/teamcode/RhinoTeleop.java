package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


@TeleOp(name="RhinoTeleop", group="Rhino")
//@Disabled
//@Disabled

public class RhinoTeleop extends OpMode {

    RhinoHardware robot = new RhinoHardware();

    private float driveVal = .8f;
    private float drive = driveVal;
    private float driveSlow = .2f;
    //private boolean intakeToggle = true;
    //private float BRDrive = 1f;

    @Override
    public void init()
    {
        //Initialize the hardware variables.
        //The init() method of the hardware class does all the work here
        robot.init(hardwareMap);
        //robot.intakeL.setPower(1);
        //robot.intakeR.setPower(1);
    }

    @Override
    public void loop()

    {
        mecanumMove();

        //compression intake
        if(gamepad1.b)
        {
            robot.intakeL.setPower(0);
            robot.intakeR.setPower(0);
        }
        else if (gamepad1.a)
        {
            robot.intakeR.setPower(1);
            robot.intakeL.setPower(1);
        }
        if(gamepad1.left_bumper)
        {
            drive = driveSlow;
        }
        else
        {
            drive = driveVal;
        }


        if(gamepad1.dpad_up || gamepad2.dpad_up)
        {
            robot.liftL.setPower(.3);
            robot.liftR.setPower(.3);
        }
        else if(gamepad1.dpad_down || gamepad2.dpad_down)
        {
            robot.liftL.setPower(-.3);
            robot.liftR.setPower(-.3);
        }

        if(gamepad1.left_trigger >= .1)
        {
            robot.schlide.setPower(-gamepad1.left_trigger * drive);
        }
        else if(gamepad2.left_trigger >= .1)
        {
            robot.schlide.setPower(-gamepad2.left_trigger * drive);
        }
        else if(gamepad1.right_trigger >= .1)
        {
            robot.schlide.setPower(gamepad1.right_trigger * drive);
        }
        else if(gamepad2.right_trigger >= .1)
        {
            robot.schlide.setPower(gamepad2.right_trigger * drive);
        }

        if(gamepad1.x ||gamepad2.x)
        {
            robot.claw.setPosition(1);
        }
        else if(gamepad1.y || gamepad2.y)
        {
            robot.claw.setPosition(0);
        }
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