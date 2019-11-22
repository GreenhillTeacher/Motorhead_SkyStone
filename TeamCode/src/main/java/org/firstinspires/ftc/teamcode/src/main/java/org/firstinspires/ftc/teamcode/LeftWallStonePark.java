package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="FullAutonRed", group="Skystone")
public class LeftWallStonePark extends AutonDriving {

    @Override
    public void runOpMode()
    {
        encoderDrive(9,"f", 5, 1);
    }
}