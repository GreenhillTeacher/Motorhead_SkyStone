package org.firstinspires.ftc.teamcode.src.main.java.legacy;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
//import org.firstinspires.ftc.teamcode.SkyStoneHardware;


@TeleOp(name="GyroTest", group="ColorSensor")
//@Disabled

public class GyroTest extends OpMode {

    MecanumHardware3 robot = new MecanumHardware3();

    private float drive = .8f;
    BNO055IMU imu;
    Orientation angles;
    Acceleration gravity;

    @Override
    public void init()
    {
        //Initialize the hardware variables.
        //The init() method of the hardware class does all the work here
        robot.init(hardwareMap);
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

    }

    @Override
    public void loop()
    {
        //waitForStart();
        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);
        //
        telemetry.addData("x", readAngle("x"));
        telemetry.addData("y", readAngle("y"));
        telemetry.addData("z", readAngle("z"));
        telemetry.update();
//        telemetry.addData("sen1b", robot.sen1.blue());
//        telemetry.addData("sen2", robot.sen2.alpha());
//        telemetry.addData("sen3", robot.sen3.alpha());
//        telemetry.addData("dist", robot.dist.getDistance(DistanceUnit.INCH));//if the distance is < .5in then the stone is too close despite being skystone
        //and if a nonskystone is more than 5.5in away it needs to be brought closer
        // if stone is really close then skystone reading are lower across the board than regular stone
//        double average = robot.sen1.alpha()+ robot.sen3.alpha() + robot.sen2.alpha();
//        boolean isSkystone = false;
//        average/=3;
//        telemetry.addData("average", (robot.sen1.alpha() + robot.sen2.alpha() + robot.sen3.alpha())/3);
//        if(average < 150)
//        {
//            isSkystone = true;
//        }
//        telemetry.addData("Is A Skystone", isSkystone);
       // telemetry.update();
        //not funny didnt laugh
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

}