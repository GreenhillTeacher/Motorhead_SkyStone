package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="FullAutonRed", group="Skystone")
public class FullAutonRed extends AutonDriving {

    @Override
    public void runOpMode()
    {
        if(skystonePosition.equals("right"))
        {
            encoderDrive(8, "f", 5, 1);
            forwardInches -= 8;
        }
        else if(skystonePosition.equals("left"))
        {
            encoderDrive(8, "b", 5, 1);
            forwardInches += 8;
        }

        armExtend(32, .75, 10);
        encoderDrive(forwardInches, "f", 10, 1);


    }
}