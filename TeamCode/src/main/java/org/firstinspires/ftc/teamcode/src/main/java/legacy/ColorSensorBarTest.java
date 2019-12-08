package org.firstinspires.ftc.teamcode.src.main.java.legacy;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name="ColorSensorTest", group="ColorSensor")
//@Disabled
@Disabled

public class ColorSensorBarTest extends OpMode {

    ColorSensorBarTestHardware robot = new ColorSensorBarTestHardware();

    private float drive = .8f;
    ElapsedTime time = new ElapsedTime();
    int[] colorChange = new int[4];
    int prevRed = 0;
    int prevBlue = 0;
    int prevGreen = 0;
    int prevAlpha = 0;
    int colorChanges = 0;

    @Override
    public void init()
    {
        //Initialize the hardware variables.
        //The init() method of the hardware class does all the work here
        robot.init(hardwareMap);
        colorChange = calibrateSensor();

    }

    @Override
    public void loop()
    {
        if(robot.color1.red() - prevRed > colorChange[0] && robot.color1.green() - prevGreen > colorChange[1] && robot.color1.blue() - prevBlue > colorChange[2] && robot.color1.alpha() - prevAlpha > colorChange[3])
        {
            colorChanges++;
        }

        telemetry.addData("color changes", colorChanges);
        telemetry.update();
        //

        prevRed = robot.color1.red();
        prevGreen = robot.color1.green();
        prevBlue = robot.color1.blue();
        prevAlpha = robot.color1.alpha();
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
        //telemetry.update();
        //not funny didnt laugh
    }
    public int[] calibrateSensor()
    {
        int[] change = new int[4];
            time.reset();
            int maxRed = 0;
            int minRed = 1000;
            int maxGreen = 0;
            int minGreen = 1000;
            int maxBlue = 0;
            int minBlue = 1000;
            int maxAlpha = 0;
            int minAlpha = 1000;
            while(time.seconds() <= 3)
            {
                if(robot.color1.red() < minRed)
                {
                    minRed = robot.color1.red();
                }
                if(robot.color1.green() < minGreen)
                {
                    minGreen = robot.color1.green();
                }
                if(robot.color1.blue() < minBlue)
                {
                    minBlue = robot.color1.blue();
                }
                if(robot.color1.alpha() < minAlpha)
                {
                    minAlpha = robot.color1.alpha();
                }

                if(robot.color1.red() > maxRed)
                {
                    maxRed = robot.color1.red();
                }
                if(robot.color1.green() > maxGreen)
                {
                    maxGreen = robot.color1.green();
                }
                if(robot.color1.blue() > maxBlue)
                {
                    maxBlue = robot.color1.blue();
                }
                if(robot.color1.alpha() > maxAlpha)
                {
                    maxAlpha = robot.color1.alpha();
                }
            }
            change[0] = maxRed - minRed;
            change[1] = maxGreen - minGreen;
            change[2] = maxBlue - minBlue;
            change[3] = maxAlpha - minAlpha;
            return change;
    }

}