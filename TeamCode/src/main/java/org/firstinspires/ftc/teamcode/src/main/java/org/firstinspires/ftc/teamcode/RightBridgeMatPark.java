package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="RightBridgeMatPark", group="Skystone")
public class RightBridgeMatPark extends AutonDriving {

    @Override
    public void runOpMode()
    {
        encoderDrive(24.5,"f", 5, driveSpeed);
        turnToPosition(-90,"z",turnSpeed,5,false);
        encoderDrive(9,"f",5,driveSpeed);
        turnToPosition(90,"z",turnSpeed,5,false);
        encoderDrive(23.5, "f",5, driveSpeed);
    }
}