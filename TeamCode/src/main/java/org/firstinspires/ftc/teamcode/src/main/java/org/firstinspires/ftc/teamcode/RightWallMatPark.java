package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="FullAutonRed", group="Skystone")
public class RightWallMatPark extends AutonDriving {

    @Override
    public void runOpMode()
    {
        encoderDrive(9,"b", 5, 1);
    }
}