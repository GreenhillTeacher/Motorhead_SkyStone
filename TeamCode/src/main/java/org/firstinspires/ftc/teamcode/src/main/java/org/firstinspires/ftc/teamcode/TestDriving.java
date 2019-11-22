package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="TestDriving", group="Skystone")
public class TestDriving extends AutonDriving {

    @Override
    public void runOpMode() {
        armLift(3, .5, 5);
        armExtend(32, .75, 15);
    }
}