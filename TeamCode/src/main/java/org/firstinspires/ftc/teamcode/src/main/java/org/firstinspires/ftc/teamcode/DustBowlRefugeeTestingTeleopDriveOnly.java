package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp(name="DustBowlRefugeeTestingTeleopDriveOnly", group="Test")
//@Disabled
//@Disabled

public class DustBowlRefugeeTestingTeleopDriveOnly extends OpMode {

    DustBowlRefugeeHardwareDriveOnly robot = new DustBowlRefugeeHardwareDriveOnly();

    private float driveVal = .666f;
    private float drive = driveVal;
    private float driveSlow = .3f;
    //private double up = 1;
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
        robot.fLMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.fRMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.bLMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.bRMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public void loop()

    {
        mecanumMove();
        //gamepad1.

//        if(gamepad1.left_stick_y > .2 || gamepad1.left_stick_y < -.2)
//        {
//            robot.fLMotor.setPower(gamepad1.left_stick_y);
//            robot.fRMotor.setPower(gamepad1.left_stick_y);
//            robot.bLMotor.setPower(gamepad1.left_stick_y);
//            robot.bRMotor.setPower(gamepad1.left_stick_y);
//        }
//        else if(gamepad1.left_stick_x > .2 || gamepad1.left_stick_x < -.2)
//        {
//            robot.fLMotor.setPower(gamepad1.left_stick_x);
//            robot.fRMotor.setPower(-gamepad1.left_stick_x);
//            robot.bLMotor.setPower(-gamepad1.left_stick_x);
//            robot.bRMotor.setPower(gamepad1.left_stick_x);
//        }
//        else
//        {
//            robot.fLMotor.setPower(0);
//            robot.fRMotor.setPower(0);
//            robot.bLMotor.setPower(0);
//            robot.bRMotor.setPower(0);
//        }
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

        //cr claw
        if(gamepad1.a)
        {
            robot.claw.setPower(1);
        }
        else if(gamepad1.b)
        {
            robot.claw.setPower(-1);
        }
        else
        {
            robot.claw.setPower(0);
        }
        //latch
//        if(gamepad1.y)
//        {
//            robot.latch1.setPosition(1);
//            robot.latch2.setPosition(1);
//            //latch = false;
//        }
//        else if(gamepad1.x)
//        {
//            robot.latch1.setPosition(0);
//            robot.latch2.setPosition(0);
//            //latch = true;
//        }
//
//        //compression intake
//        if(gamepad1.a)
//        {
//            robot.intakeL.setPower(0);
//            robot.intakeR.setPower(0);
//        }
//        else if (gamepad1.b)
//        {
//            robot.intakeR.setPower(1);
//            robot.intakeL.setPower(1);
//        }

        //slow drive
        if(gamepad1.x || gamepad1.dpad_right)
        {
            drive = driveSlow;
        }
        else
        {
            drive = driveVal;
        }

        //lift controls
//        if(gamepad1.dpad_up)
//        {
//            robot.liftL.setPower(.3);
//            robot.liftR.setPower(.3);
//        }
//        else if(gamepad1.dpad_down)
//        {
//            robot.liftL.setPower(-.3);
//            robot.liftR.setPower(-.3);
//        }
//        else
//        {
//            robot.liftL.setPower(0);
//            robot.liftR.setPower(0);
//        }
//
//        //slide controls
//        if(gamepad1.left_trigger >= .1)
//        {
//            robot.schlide.setPower(-gamepad1.left_trigger * drive);
//        }
//        else if(gamepad1.right_trigger >= .1)
//        {
//            robot.schlide.setPower(gamepad1.right_trigger * drive);
//        }
//        else
//        {
//            robot.schlide.setPower(0);
//        }
//
//        //claw controls
//        if(gamepad1.x ||gamepad2.x)
//        {
//            robot.claw.setPosition(1);
//        }
//        else if(gamepad1.y || gamepad2.y)
//        {
//            robot.claw.setPosition(0);
//        }


    }

    public void mecanumMove()
    {
        //variables
        double r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
        double robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;
        double rightX = -gamepad1.right_stick_x;
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