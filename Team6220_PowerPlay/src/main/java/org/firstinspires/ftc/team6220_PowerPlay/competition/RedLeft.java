package org.firstinspires.ftc.team6220_PowerPlay.competition;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.team6220_PowerPlay.AutoFramework;

@Autonomous(name = "RedLeft")
public class RedLeft extends AutoFramework {

    @Override
    public void runOpMode() throws InterruptedException {
        runAuto(AutoState.LeftAutos);
    }
    // If you want to add anything to this autonomous method, make the changes in the AutoFramework method please!
}