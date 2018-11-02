package org.firstinspires.ftc.teamcode._Libs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.followers.TankPIDVAFollower;
import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.drive.SampleTankDriveOptimized;
import org.firstinspires.ftc.teamcode.util.DashboardUtil;

public class RoadRunnerImplementer {
    static public class Follow2dTrajectory extends AutoLib.Step {
        TankPIDVAFollower mFollower;
        Trajectory mTrajectory;
        SampleTankDriveOptimized mDrive;
        FtcDashboard mDashboard;

        public Follow2dTrajectory(SampleTankDriveOptimized drive, FtcDashboard dashboard, Trajectory trajectory) {
            mDashboard = dashboard;
            mDrive = drive;
            mTrajectory = trajectory;
        }

        public boolean loop()
        {
            super.loop();

            if(firstLoopCall()) {
                mDrive.followTrajectory(mTrajectory);
            }

            Pose2d currentPose = mDrive.getPoseEstimate();

            TelemetryPacket packet = new TelemetryPacket();
            Canvas fieldOverlay = packet.fieldOverlay();

            packet.put("x", currentPose.getX());
            packet.put("y", currentPose.getY());
            packet.put("heading", currentPose.getHeading());

            fieldOverlay.setStroke("green");
            DashboardUtil.drawSampledTrajectory(fieldOverlay, mTrajectory);

            fieldOverlay.setFill("blue");
            fieldOverlay.fillCircle(currentPose.getX(), currentPose.getY(), 3);

            mDashboard.sendTelemetryPacket(packet);

            mDrive.update();

            return !mDrive.isFollowingTrajectory();
        }
    }
}