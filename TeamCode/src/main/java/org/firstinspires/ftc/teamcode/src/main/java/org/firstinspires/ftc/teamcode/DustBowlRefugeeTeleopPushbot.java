package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="DustBowlRefugeeTeleopPushbot", group="DustBowlRefugee")
@Disabled

public class DustBowlRefugeeTeleopPushbot extends OpMode {

    DustBowlRefugeeHardware robot = new DustBowlRefugeeHardware();

    private float driveVal = .6f;
    private float drive = driveVal;
    private float driveSlow = .2f;
    private float driveLeft = .8f;
    private float driveSlowLeft = .4f;
    private float driveLeftVal = .8f;
    //private boolean latch = true;
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
        //mecanum drive
        mecanumMove();

        //switch forward driving direction
        if(gamepad1.start)
        {
            robot.fLMotor.setDirection(DcMotor.Direction.REVERSE);
            robot.fRMotor.setDirection(DcMotor.Direction.FORWARD);
            robot.bLMotor.setDirection(DcMotor.Direction.REVERSE);
            robot.bRMotor.setDirection(DcMotor.Direction.FORWARD);
        }
        else if (gamepad1.back)
        {
            robot.fLMotor.setDirection(DcMotor.Direction.FORWARD);
            robot.fRMotor.setDirection(DcMotor.Direction.REVERSE);
            robot.bLMotor.setDirection(DcMotor.Direction.FORWARD);
            robot.bRMotor.setDirection(DcMotor.Direction.REVERSE);
        }

        //latch
        if(gamepad1.right_trigger > .1)
        {
            robot.latch1.setPosition(1);
            robot.latch2.setPosition(1);
            //latch = false;
        }
        else if(gamepad1.left_trigger > .1)
        {
            robot.latch1.setPosition(0);
            robot.latch2.setPosition(0);
            //latch = true;
        }

        //compression intake
        if(gamepad1.b)
        {
            robot.intakeL.setPower(1);
            robot.intakeR.setPower(1);
        }
        else if (gamepad1.y)
        {
            robot.intakeR.setPower(-1);
            robot.intakeL.setPower(-1);
        }
        else
        {
            robot.intakeR.setPower(0);
            robot.intakeL.setPower(0);
        }

        //slow drive
        if(gamepad1.left_bumper)
        {
            drive = driveSlow;
            driveLeft = driveSlowLeft;
        }
        else
        {
            drive = driveVal;
            driveLeft = driveLeftVal;
        }

        //lift controls
        if(gamepad2.dpad_up)
        {
            robot.liftL.setPower(-.3);
            robot.liftR.setPower(-.3);
        }
        else if(gamepad2.dpad_down)
        {
            robot.liftL.setPower(.3);
            robot.liftR.setPower(.3);
        }
        else
        {
            robot.liftL.setPower(0);
            robot.liftR.setPower(0);
        }

        //slide controls
        if(gamepad2.left_trigger >= .1)
        {
            robot.schlide.setPower(gamepad2.left_trigger * drive);
        }
        else if(gamepad2.right_trigger >= .1)
        {
            robot.schlide.setPower(-gamepad2.right_trigger * drive);
        }
        else
        {
            robot.schlide.setPower(0);
        }

        //claw controls
        if(gamepad2.x)
        {
            robot.claw.setPower(1);
        }
        else if(gamepad2.y)
        {
            robot.claw.setPower(0);
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

        robot.fLMotor.setPower(-driveLeft * v1);
        robot.fRMotor.setPower(-drive * v2);
        robot.bLMotor.setPower(-driveLeft * v3);
        robot.bRMotor.setPower(-drive * v4);
    }
}