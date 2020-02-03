package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

//@Autonomous(name="AutonDrivingDriveOnly", group="AutonTesting")
public class AutonDrivingDustBowlRefugee extends LinearOpMode {

    /* Declare OpMode members. */
    DustBowlRefugeeHardware robot = new DustBowlRefugeeHardware();
    private ElapsedTime runtime = new ElapsedTime();
    String xyz = "z";
    //CONTAINS ALL METHODS AND VARIABlES TO BE EXTENDED BY OTHER AUTON CLASSES
    static final double     COUNTS_PER_MOTOR_REV = 537.6;    // Currently: Andymark Neverest 20
    //static final double     COUNTS_PER_REV_ARM = 1495; //torquenado
    //static final double     PULLEY_DIAMETER = 1.3;
   // static final double     COUNTS_PER_INCH_ARM = COUNTS_PER_REV_ARM/(PULLEY_DIAMETER * Math.PI);
    static final double     DRIVE_GEAR_REDUCTION = .410;     // This is < 1.0 if geared UP //On OUR CENTER MOTOR THE GEAR REDUCTION IS .5
    static final double     WHEEL_DIAMETER_INCHES = 2.95276;     // For figuring circumference
    static final double     COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);

    static final double     HEADING_THRESHOLD       = 1 ;      // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.1;     // Larger is more responsive, but also less stable
    static final double     P_DRIVE_COEFF           = 0.15;     // Larger is more responsive, but also less stable

    BNO055IMU imu;

    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    public static final boolean PHONE_IS_PORTRAIT = false  ;

    public static final String VUFORIA_KEY =
            "AYy6NYn/////AAABmTW3q+TyLUMbg/IXWlIG3BkMMq0okH0hLmwj3CxhPhvUlEZHaOAmESqfePJ57KC2g6UdWLN7OYvc8ihGAZSUJ2JPWAsHQGv6GUAj4BlrMCjHvqhY0w3tV/Azw2wlPmls4FcUCRTzidzVEDy+dtxqQ7U5ZtiQhjBZetAcnLsCYb58dgwZEjTx2+36jiqcFYvS+FlNJBpbwmnPUyEEb32YBBZj4ra5jB0v4IW4wYYRKTNijAQKxco33VYSCbH0at99SqhXECURA55dtmmJxYpFlT/sMmj0iblOqoG/auapQmmyEEXt/T8hv9StyirabxhbVVSe7fPsAueiXOWVm0kCPO+KN/TyWYB9Hg/mSfnNu9i9";

    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
    public static final float mmPerInch        = 25.4f;
    // the height of the center of the target image above the floor

    // Constant for Stone Target
    public static final float stoneZ = 2.00f * mmPerInch;

    // Constants for the center support targets

    // Class Members
    public OpenGLMatrix lastLocation = null;
    public VuforiaLocalizer vuforia = null;

    WebcamName webcamName = null;

    public boolean targetVisible = false;
    public float phoneXRotate    = 0;
    public float phoneYRotate    = 0;
    public float phoneZRotate    = 0;
    public List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
    public VuforiaTrackables targetsSkyStone;

    //public String skystonePosition = "center";
    public double driveSpeedRight = .3;
    public double driveSpeedLeft = 1;

    //TODO: CHECK
    public double turnSpeed = .7;
    public double axisTurnSpeed = 1;


    Orientation angles;
    Acceleration gravity;
    public double startAngle = 0;
    //TODO: CHECK
    public double gyroDriveThreshold = 1;
    private double gyroDriveSpeed = .275;

    private double gyroTurnThreshold = .7;
    private double degreeError = 7;
    public double negative90timeout = 2.5;

    @Override
    public void runOpMode() {
    }

    public void latch(boolean enabled)
    {
        if(enabled)
        {
            robot.latch1.setPosition(.95);
            robot.latch2.setPosition(.95);
        }
        else
        {
            robot.latch1.setPosition(0);
            robot.latch2.setPosition(0);
        }
    }

    public String vuforia(List<VuforiaTrackable> allTrackables, VuforiaTrackables targetsSkyStone)
    {
        runtime.reset();
        String skystonePosition = "null";
        targetsSkyStone.activate();
        while (runtime.seconds() <= 3) {

            // check all the trackable targets to see which one (if any) is visible.
            targetVisible = false;
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                    telemetry.addData("Visible Target", trackable.getName());
                    targetVisible = true;

                    // getUpdatedRobotLocation() will return null if no new information is available since
                    // the last time that call was made, or if the trackable is not currently visible.
                    OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform;
                    }
                    break;
                }
            }

            // Provide feedback as to where the robot is located (if we know).
            if (targetVisible) {
                // express position (translation) of robot in inches.
                VectorF translation = lastLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

                // express the rotation of the robot in degrees.
                Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
                double pos = translation.get(1);
                //TODO: FIND POS
                if(pos > 0)
                {
                    skystonePosition = "right";
                }
                else if(-pos > 9)
                {
                    skystonePosition = "center";
                }
                else
                {
                    skystonePosition = "left";
                }
            }
            else {
                telemetry.addData("Visible Target", "none");
                skystonePosition = "left";
            }
            if(!skystonePosition.equals("null") && !skystonePosition.equals("left") && runtime.seconds() >= 2)
            {
                return skystonePosition;
            }
            telemetry.addData("Skystone Position", skystonePosition);
            telemetry.update();
        }
        telemetry.addData("stopped", "stopped");
        telemetry.update();
        //sleep(2500);

        // Disable Tracking when we are done;
        targetsSkyStone.deactivate();
        return skystonePosition;
    }


    public static double counts(double inches)
    {
        double newInches = (inches - 3.7959) / 1.1239;
        return newInches;
    }


    public void updateAngles()
    {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    }


    public void turnDegreesLegacy(double target, double power, double timeoutS)
    {
        //Write code to correct to a target position (NOT FINISHED)
        runtime.reset();
        updateAngles(); //variable for gyro correction around z axis
        target *= -1;//switches clockwise and counterclockwise directions

        if(target < 0) {//this fixes a problem where the turn undershoots by 6ish degrees for some reason
            //target += 2;
        }
        else if(target > 0)
        {
            target -= 2;
        }
        //target += 6;
        double error = angles.firstAngle - target;
        double errorAbs;
        //wrapping error to have it remain in the field
        if (error > 180)  error -= 360;
        if (error <= -180) error += 360;

        double powerScaled = power;
        do
        {
            updateAngles();
            error = angles.firstAngle - target;
            errorAbs = Math.abs(error);

            if (errorAbs <= 10)
            {
                powerScaled /= 2;
            }
            telemetry.addData("error", error);
            //telemetry.addData("NORTH", NORTH);
            telemetry.addData("angle", angles.firstAngle);
            telemetry.update();
            if(error > 0)
            {
                robot.fRMotor.setPower(-powerScaled);
                robot.bRMotor.setPower(-powerScaled);
                robot.fLMotor.setPower(powerScaled);
                robot.bLMotor.setPower(powerScaled);
            }
            else if(error < 0)
            {
                robot.fRMotor.setPower(powerScaled);
                robot.bRMotor.setPower(powerScaled);
                robot.fLMotor.setPower(-powerScaled);
                robot.bLMotor.setPower(-powerScaled);
            }
        }
        while ((Math.abs(error) > 1) && (runtime.seconds() < timeoutS) && opModeIsActive());

        robot.fRMotor.setPower(0);
        robot.bRMotor.setPower(0);
        robot.fLMotor.setPower(0);
        robot.bLMotor.setPower(0);
    }


    public void normalDrive(double lpower, double rpower) {

        if (opModeIsActive()) {
            robot.fLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.fLMotor.setPower(lpower);
            robot.fRMotor.setPower(rpower);
            robot.bLMotor.setPower(lpower);
            robot.bRMotor.setPower(rpower);
        }
    }


    public void turnToPosition (double target, String xyz, double topPower, double timeoutS) {
        //stopAndReset();
        //if target
        target*= -1;
        double originalAngle = readAngle(xyz);

        //undershoots in 1 direction and overshoots in other
        //if(target != 0)target += 4;

        runtime.reset();

        double angle = readAngle(xyz); //variable for gyro correction around z axis
        double error = angle - target;
        double powerScaled = topPower;
        do {
            //telemetry.addData("hello", "2");
            //sleep(1000);
            //telemetry.update();
            angle = readAngle(xyz);
            error = angle - target;

            //power gets too low towards end of turn
            if(Math.abs(error) > 6)
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error);
            }
            else if(error > 0)
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error) + .1;
            }
            //weirdly slow on negative turns
            else
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error) + .4;
            }

            //this works for some reason??
            powerScaled *= -1;

            //double powerScaled = power*pidMultiplier(error);
            telemetry.addData("original angle", originalAngle);
            telemetry.addData("current angle", readAngle(xyz));
            telemetry.addData("error", error);
            telemetry.update();
            if (error > 0)
            {
                normalDrive(-powerScaled, powerScaled);
            }
            else if (error < 0)
            {
                normalDrive(powerScaled, -powerScaled);
            }
        } while (opModeIsActive() && (Math.abs(error) > gyroTurnThreshold) && (runtime.seconds() < timeoutS));
        normalDrive(0, 0);
        //stopAndReset();

    }

    public void turnDegrees (double degrees, String xyz, double topPower, double timeoutS) {
        //stopAndReset();
        //if target
        double target = readAngle(xyz) + degrees;
        target*= -1;
        double originalAngle = readAngle(xyz);

        //undershoots in 1 direction and overshoots in other
        if(target != 0)target += 4;

        runtime.reset();

        double angle = readAngle(xyz); //variable for gyro correction around z axis
        double error = angle - target;
        double powerScaled = topPower;
        do {
            //telemetry.addData("hello", "2");
            //sleep(1000);
            //telemetry.update();
            angle = readAngle(xyz);
            error = angle - target;

            //power gets too low towards end of turn
            if(Math.abs(error) > 6)
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error);
            }
            else if(error > 0)
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error) + .05;
            }
            //weirdly slow on negative turns
            else
            {
                powerScaled = topPower * (error / 180) * pidMultiplierTurning(error) + .2;
            }

            //this works for some reason??
            if(target > 0) powerScaled *= -1;

            //double powerScaled = power*pidMultiplier(error);
            telemetry.addData("original angle", originalAngle);
            telemetry.addData("current angle", readAngle(xyz));
            telemetry.addData("error", error);
            telemetry.update();
            if (error > 0)
            {
                normalDrive(-powerScaled, powerScaled);
            }
            else if (error < 0)
            {
                normalDrive(powerScaled, -powerScaled);
            }
        } while (opModeIsActive() && (Math.abs(error) > gyroTurnThreshold) && (runtime.seconds() < timeoutS));
        normalDrive(0, 0);
        //stopAndReset();

    }

    public void axisTurn (double target, String xyz, double topPower, double timeoutS)
    {
        //turning w/ axis on side of robot
        //stopAndReset();
        target*= -1;
        double originalAngle = readAngle(xyz);

        //wild
        if (target > 0)
        {
            target -= degreeError;
        }
        else
        {
            target += degreeError;
        }
        //telemetry.addData("hello", "1");
        //telemetry.update();

        runtime.reset();

        double angle = readAngle(xyz); //variable for gyro correction around z axis
        double error = angle - target;
        double powerScaled = topPower;
        do {
            //telemetry.addData("hello", "2");
            //sleep(1000);
            //telemetry.update();
            angle = readAngle(xyz);
            error = angle - target;
            powerScaled = topPower * (error / 180) * pidMultiplierTurning(error);
            if (error < 15)
            {
                powerScaled = .3;
            }
            //powerScaled *= -1;

            //double powerScaled = power*pidMultiplier(error);
            telemetry.addData("original angle", originalAngle);
            telemetry.addData("current angle", readAngle(xyz));
            telemetry.addData("error", error);
            telemetry.update();
            if (error > 0)
            {
                normalDrive(0, powerScaled);
            }
            else if (error < 0)
            {
                normalDrive(powerScaled, 0);
            }
        } while (opModeIsActive() && (Math.abs(error) > gyroTurnThreshold) && (runtime.seconds() < timeoutS));
        normalDrive(0, 0);
        //stopAndReset();

    }

    public void stopAndReset()
    {
        robot.bRMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.bLMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.fRMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.fLMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.armExt.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.armLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //robot.susan.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        //
        robot.bRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.bLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.fRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.fLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.armExt.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.armLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //robot.susan.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public double pidMultiplierDriving(double error) {
        //equation for power multiplier is x/sqrt(x^2 + C)
        int C = 500;
        return Math.abs(error / Math.sqrt((error * error) + C));
    }
    public double pidMultiplierTurning(double error) {
        //equation for power multiplier is x/sqrt(x^2 + C)
        double C = .001;
        return Math.abs(error / Math.sqrt((error * error) + C));
    }

    public double readAngle(String xyz) {
        Orientation angles;
        Acceleration gravity;
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        if (xyz.equals("x")) {
            return angles.thirdAngle;
        } else if (xyz.equals("y")) {
            return angles.secondAngle;
        } else if (xyz.equals("z")) {
            return angles.firstAngle;
        } else {
            return 0;
        }
    }

    public void encoderDrive(double inches, String direction, double timeoutS, double topPower)
    {
        stopAndReset();
        int TargetFL = 0;
        int TargetFR = 0;
        int TargetBL = 0;
        int TargetBR = 0;
        double errorFL = 0;
        double errorFR = 0;
        double errorBL = 0;
        double errorBR = 0;
        double powerFL = 0;
        double powerFR = 0;
        double powerBL = 0;
        double powerBR = 0;


        String heading = direction;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {
            if(heading == "f")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetBR = robot.bRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);

            }

            else if(heading == "b")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBR = robot.bRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);


            }

            else if(heading == "r")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBR = robot.bRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH); //weird should be +


            }

            else if(heading == "l")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH); // weird should be +
                TargetBR = robot.bRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);

            }

            else
            {
                telemetry.addData("not a valid direction", heading );
            }



            // Determine new target position, and pass to motor controller

            robot.fLMotor.setTargetPosition(TargetFL);
            robot.fRMotor.setTargetPosition(TargetFR);
            robot.bRMotor.setTargetPosition(TargetBR);
            robot.bLMotor.setTargetPosition(TargetBL);


            // Turn On RUN_TO_POSITION
            robot.fLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // reset the timeout time and start motion.
            runtime.reset();
            /*robot.fLMotor.setPower(Speed);
            robot.fRMotor.setPower(Speed);
            robot.bRMotor.setPower(Speed);
            robot.bLMotor.setPower(Speed);*/


            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) && ((robot.fLMotor.isBusy() && robot.fRMotor.isBusy()) && robot.bLMotor.isBusy() && robot.bRMotor.isBusy()))
            {
                errorFL = TargetFL - robot.fLMotor.getCurrentPosition();
                errorFR = TargetFR - robot.fRMotor.getCurrentPosition();
                errorBL = TargetBL - robot.bLMotor.getCurrentPosition();
                errorBR = TargetBR - robot.bRMotor.getCurrentPosition();

                powerFL = topPower * pidMultiplierDriving(errorFL);
                powerFR = topPower * pidMultiplierDriving(errorFR);
                powerBL = topPower * pidMultiplierDriving(errorBL);
                powerBR = topPower* pidMultiplierDriving(errorBR);

                robot.fLMotor.setPower(Math.abs(powerFL));
                robot.fRMotor.setPower(Math.abs(powerFR));
                robot.bRMotor.setPower(Math.abs(powerBL));
                robot.bLMotor.setPower(Math.abs(powerBR));
                telemetry.addData("Path1",  "Running to %7d :%7d :%7d :%7d", TargetFL,  TargetFR, TargetBL, TargetBR);

                telemetry.addData("Path2",  "Running at %7d :%7d :%7d :%7d", robot.fLMotor.getCurrentPosition(), robot.fRMotor.getCurrentPosition(), robot.bLMotor.getCurrentPosition(), robot.bRMotor.getCurrentPosition());
                //telemetry.addData("speeds",  "Running to %7f :%7f :%7f :%7f", speedfL,  speedfR, speedfL, speedbR);
                telemetry.update();
            }

            // Stop all motion;
            robot.fLMotor.setPower(0);
            robot.bLMotor.setPower(0);
            robot.fRMotor.setPower(0);
            robot.bRMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.fLMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //  sleep(250);   // optional pause after each move
        }
        stopAndReset();
    }
    /*public void armExtend(double inches, double topPower, double timeoutS)
    {
        stopAndReset();
        int target = robot.armExt.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH_ARM);

        robot.armExt.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.armExt.setTargetPosition(target);

        runtime.reset();
        while(opModeIsActive() && runtime.seconds() < timeoutS && robot.armExt.isBusy())
        {
            double error = target - robot.armExt.getCurrentPosition();
            double power = topPower * pidMultiplierDriving(error);
            robot.armExt.setPower(power);
            telemetry.addData("Path1",  "Running to %7d", target);
            telemetry.addData("Path2",  "Running at %7d", robot.armExt.getCurrentPosition());
            telemetry.update();
        }
        robot.armExt.setPower(0);
        stopAndReset();
    }

    public void armLift(double inches, double topPower, double timeoutS)
    {
        stopAndReset();
        int target = robot.armLift.getCurrentPosition() + (int)(inches * COUNTS_PER_INCH_ARM);

        robot.armLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.armLift.setTargetPosition(target);

        runtime.reset();
        while(opModeIsActive() && runtime.seconds() < timeoutS && robot.armLift.isBusy())
        {
            double error = target - robot.armLift.getCurrentPosition();
            double power = topPower * pidMultiplierDriving(error);
            robot.armLift.setPower(power);
            telemetry.addData("Path1",  "Running to %7d", target);
            telemetry.addData("Path2",  "Running at %7d", robot.armLift.getCurrentPosition());
            telemetry.update();
        }
        robot.armLift.setPower(0);
        stopAndReset();
    }
*/
    public void gyroDrive (double distance, double threshold)
    {
        //updateAngles();
        double angle = readAngle("z");
        stopAndReset();

        int fLTarget;
        int fRTarget;
        int bLTarget;
        int bRTarget;
        //int     newRightTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            fLTarget = robot.fLMotor.getCurrentPosition() + moveCounts;
            fRTarget = robot.fRMotor.getCurrentPosition() + moveCounts;
            bLTarget = robot.bLMotor.getCurrentPosition() + moveCounts;
            bRTarget = robot.bLMotor.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            robot.fLMotor.setTargetPosition(fLTarget);
            robot.fRMotor.setTargetPosition(fRTarget);
            robot.bLMotor.setTargetPosition(bLTarget);
            robot.bRMotor.setTargetPosition(bRTarget);


            robot.fLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            //gyroDriveSpeed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.fLMotor.setPower(gyroDriveSpeed);
            robot.fRMotor.setPower(gyroDriveSpeed);
            robot.bLMotor.setPower(gyroDriveSpeed);
            robot.bRMotor.setPower(gyroDriveSpeed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.fLMotor.isBusy() && robot.fRMotor.isBusy() && robot.bLMotor.isBusy() && robot.bRMotor.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);

                if(Math.abs(error) < threshold)
                {
                    error = 0;
                }
                steer = -getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = gyroDriveSpeed - steer;
                rightSpeed = gyroDriveSpeed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                leftSpeed *= gyroDriveSpeed;
                rightSpeed *= gyroDriveSpeed;

                //increase starting speed of drive
                if(robot.fLMotor.getCurrentPosition()< 50)
                {
                    leftSpeed += .1;
                }
                if(robot.fRMotor.getCurrentPosition() < 50)
                {
                    rightSpeed += .1;
                }

                robot.fLMotor.setPower(leftSpeed);
                robot.fRMotor.setPower(rightSpeed);
                robot.bLMotor.setPower(leftSpeed);
                robot.bRMotor.setPower(rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d:%7d:%7d",      fLTarget,  fRTarget, bLTarget, bRTarget);
                telemetry.addData("Actual",  "%7d:%7d:%7d:%7d",      robot.fLMotor.getCurrentPosition(),
                        robot.fRMotor.getCurrentPosition(), robot.bLMotor.getCurrentPosition(), robot.bRMotor.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            normalDrive(0, 0);

            // Turn off RUN_TO_POSITION
            stopAndReset();
        }
    }

    public void gyroDriveStrafe (double distance, double angle)
    {
        stopAndReset();

        int fLTarget;
        int fRTarget;
        int bLTarget;
        int bRTarget;
        //int     newRightTarget;
        int     moveCounts;
        double  max;
        double  error;
        double  steer;
        double  leftSpeed;
        double  rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            moveCounts = (int)(distance * COUNTS_PER_INCH);
            fLTarget = robot.fLMotor.getCurrentPosition() + moveCounts;
            fRTarget = robot.fRMotor.getCurrentPosition() - moveCounts;
            bLTarget = robot.bLMotor.getCurrentPosition() - moveCounts;
            bRTarget = robot.bLMotor.getCurrentPosition() + moveCounts;

            // Set Target and Turn On RUN_TO_POSITION
            robot.fLMotor.setTargetPosition(fLTarget);
            robot.fRMotor.setTargetPosition(fRTarget);
            robot.bLMotor.setTargetPosition(bLTarget);
            robot.bRMotor.setTargetPosition(bRTarget);


            robot.fLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // start motion.
            //gyroDriveSpeed = Range.clip(Math.abs(speed), 0.0, 1.0);
            robot.fLMotor.setPower(gyroDriveSpeed);
            robot.fRMotor.setPower(gyroDriveSpeed);
            robot.bLMotor.setPower(gyroDriveSpeed);
            robot.bRMotor.setPower(gyroDriveSpeed);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.fLMotor.isBusy() && robot.fRMotor.isBusy() && robot.bLMotor.isBusy() && robot.bRMotor.isBusy())) {

                // adjust relative speed based on heading error.
                error = getError(angle);

                if(Math.abs(error) < gyroDriveThreshold)
                {
                    error = 0;
                }
                steer = -getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    steer *= -1.0;

                leftSpeed = gyroDriveSpeed - steer;
                rightSpeed = gyroDriveSpeed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
                if (max > 1.0)
                {
                    leftSpeed /= max;
                    rightSpeed /= max;
                }

                //leftSpeed *= gyroDriveSpeed;
                //rightSpeed *= gyroDriveSpeed;

                robot.fLMotor.setPower(leftSpeed);
                robot.fRMotor.setPower(rightSpeed);
                robot.bLMotor.setPower(leftSpeed);
                robot.bRMotor.setPower(rightSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
                telemetry.addData("Target",  "%7d:%7d:%7d:%7d",      fLTarget,  fRTarget, bLTarget, bRTarget);
                telemetry.addData("Actual",  "%7d:%7d:%7d:%7d",      robot.fLMotor.getCurrentPosition(),
                        robot.fRMotor.getCurrentPosition(), robot.bLMotor.getCurrentPosition(), robot.bRMotor.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
                telemetry.update();
            }

            // Stop all motion;
            normalDrive(0, 0);

            // Turn off RUN_TO_POSITION
            stopAndReset();
        }
    }
    /*
    public void gyroDriveWithC (double inches, double angle, String heading, double timeoutS)
    {
        //THIS IS BROKEN AS FUCK
        //WHAT A RIP

        stopAndReset();
        runtime.reset();

        //inches *= .5;
        int TargetFL = 0;
        int TargetFR = 0;
        int TargetBL = 0;
        int TargetBR = 0;
        double errorFL = 0;
        double errorFR = 0;
        double errorBL = 0;
        double errorBR = 0;
        double powerFL = 0;
        double powerFR = 0;
        double powerBL = 0;
        double powerBR = 0;
        //int newLeftTarget;
        //int newRightTarget;
        //int counts;
        double  max;
        double  angleError;
        double  steer;
        //double  leftSpeed;
        //double  rightSpeed;

        // Ensure that the opmode is still active
        if (opModeIsActive())
        {
            if(heading == "f")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);
                TargetBR = robot.bRMotor.getCurrentPosition() + (int)( inches* COUNTS_PER_INCH);

            }

            else if(heading == "b")
            {
                TargetFL = robot.fLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetFR = robot.fRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBL = robot.bLMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);
                TargetBR = robot.bRMotor.getCurrentPosition() - (int)( inches* COUNTS_PER_INCH);


            }

            else
            {
                telemetry.addData("not a valid direction", heading );
            }

            // Set Target and Turn On RUN_TO_POSITION
            robot.fLMotor.setTargetPosition(TargetFL);
            robot.fRMotor.setTargetPosition(TargetFR);
            robot.bLMotor.setTargetPosition(TargetBL);
            robot.bRMotor.setTargetPosition(TargetBR);

            robot.fLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.fRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bLMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.bRMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (robot.fLMotor.isBusy() && robot.fRMotor.isBusy() && robot.bLMotor.isBusy() && robot.bRMotor.isBusy()) && runtime.seconds() <= timeoutS) {

                // adjust relative speed based on heading error.

                //prevent over-correcting by having a threshold
                angleError = getError(angle);
                if(Math.abs(angleError) > 7)
                {
                    steer = getSteer(angleError, P_DRIVE_COEFF);
                }
                else
                {
                    steer = 0;
                }
                //steer = getSteer(error, P_DRIVE_COEFF);

                // if driving in reverse, the motor correction also needs to be reversed
                if (heading.equals("b"))
                {
                    steer *= -1.0;
                }
                else if(!heading.equals("f"))
                {
                    steer = 0;
                }

               // powerFL = (topPower - steer) * speed;
               // rightSpeed = (speed + steer) * speed;
                errorFL = TargetFL - robot.fLMotor.getCurrentPosition();
                errorFR = TargetFR - robot.fRMotor.getCurrentPosition();
                errorBL = TargetBL - robot.bLMotor.getCurrentPosition();
                errorBR = TargetBR - robot.bRMotor.getCurrentPosition();

                //steer *= 1.2;
                
                powerFL = gyroDriveSpeed - steer;
                powerFR = gyroDriveSpeed + steer;
                powerBL = gyroDriveSpeed - steer;
                powerBR = gyroDriveSpeed + steer;

                // Normalize speeds if either one exceeds +/- 1.0;
                max = Math.max(Math.max(Math.abs(powerFL), Math.abs(powerFR)), Math.max(powerBL, powerBR));
                if (max > 1)
                {
                    powerFL /= max;
                    powerFR /= max;
                    powerBL /= max;
                    powerBR /= max;
                }

                powerFL *= pidMultiplierDriving(errorFL);
                powerFR *= pidMultiplierDriving(errorFR);
                powerBL *= pidMultiplierDriving(errorBL);
                powerBR *= pidMultiplierDriving(errorBR);

                robot.fLMotor.setPower(powerFL * gyroDriveSpeed);
                robot.bLMotor.setPower(powerFR * gyroDriveSpeed);
                robot.fRMotor.setPower(powerBL * gyroDriveSpeed);
                robot.bRMotor.setPower(powerBR * gyroDriveSpeed);

                // Display drive status for the driver.
                telemetry.addData("Err/St",  "%5.1f/%5.1f",  angleError, steer);
                telemetry.addData("Target",  "%7d:%7d:%7d:%7d", TargetFL,  TargetFR, TargetBL, TargetBR);
                telemetry.addData("Current Pos",  "%7d:%7d:%7d:%7d", robot.fLMotor.getCurrentPosition(),  robot.fLMotor.getCurrentPosition(), robot.fLMotor.getCurrentPosition(), robot.fLMotor.getCurrentPosition());
                telemetry.addData("Speed",   "%5.2f:%5.2f:%5.2f:%5.2f",  powerFL, powerFR, powerBL, powerBR);
                telemetry.update();
            }

            // Stop all motion;
            normalDrive(0 ,0);

            // Turn off RUN_TO_POSITION
            stopAndReset();
        }
        stopAndReset();
    }*/

    public double getError(double targetAngle)
    {

        double robotError;

        // calculate error in -179 to +180 range  (
        robotError = targetAngle - readAngle("z");
        while (robotError > 180)  robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     * @param error   Error angle in robot relative degrees
     * @param PCoeff  Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }
}