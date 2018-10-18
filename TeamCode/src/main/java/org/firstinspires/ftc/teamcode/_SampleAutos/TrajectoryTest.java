package org.firstinspires.ftc.teamcode._SampleAutos;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.RobotDrive;
import org.firstinspires.ftc.teamcode._Libs.AutoLib;
import org.firstinspires.ftc.teamcode._Libs.CustomOpMode;

@Autonomous(name="Example", group="Autonomous")
public class TrajectoryTest extends CustomOpMode {

    @Override
    public void init() {
        mHardwareFactory = new AutoLib.RealHardwareFactory(this);

        // create the root Sequence for this autonomous OpMode
        mSequence = new AutoLib.LinearSequence();

        // start out not-done
        bDone = false;
    }
}