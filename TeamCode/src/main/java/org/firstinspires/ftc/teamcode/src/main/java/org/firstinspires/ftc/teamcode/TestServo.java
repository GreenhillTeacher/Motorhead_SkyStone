package org.firstinspires.ftc.teamcode.src.main.java.org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name="TestServo", group="Skystone")
public class TestServo extends AutonDriving {
    ServoHardware robot = new ServoHardware();
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        robot.servo1.setPower(1);
    }
}