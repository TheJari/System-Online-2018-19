package org.firstinspires.ftc.teamcode._Auto;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode._Auto.Steps.ChoosePathStep;
import org.firstinspires.ftc.teamcode._Auto.Steps.DoStepsStep;
import org.firstinspires.ftc.teamcode._Auto.Steps.SampleStep;
import org.firstinspires.ftc.teamcode._Auto.Steps.UnlatchStep;
import org.firstinspires.ftc.teamcode._Libs.AutoLib;
import org.firstinspires.ftc.teamcode._Libs.AutoOpMode;
import org.firstinspires.ftc.teamcode._Libs.HeadingSensor;
import org.firstinspires.ftc.teamcode._Libs.RoadRunnerImplementer;
import org.firstinspires.ftc.teamcode._Libs.SensorLib;
import org.firstinspires.ftc.teamcode.drive.SampleTankDriveREV;
@Config
@Autonomous(name="FULL Depot Auto", group="FullAuto")
public class DepotAuto extends AutoOpMode {

    private SampleTankDriveREV drive;
    private DcMotor hangMotors[];
    private DcMotorEx motors[];
    private Servo claimServo;
    private AutoLib.Data data;
    private BNO055IMU gyro;
    private AutoLib.Step samplePath1[], samplePath2[], samplePath3[];
    private DoStepsStep doStepStep;
    private SensorLib.PID pid;

    // parameters of the PID controller for this sequence
    public static double Kp = 0.025;       // motor power proportional term correction per degree of deviation
    public static double Ki = 0.005;         // ... integrator term
    public static double Kd = 0.0034;             // ... derivative term
    public static double KiCutoff = 20.0;    // maximum angle error for which we update integrator

    public static double Distance1 = 5;
    public static double Distance2 = 25;
    public static double Angle1 = 45;
    public static double Distance3 = 20;
    public static double Angle2 = -45;
    public static double Distance4 = 65;
    public static double MaxPower = 0.5;
    public static double AngleTolerance = 2.0;

    public DepotAuto() {
        msStuckDetectInit = 10000;
        msStuckDetectInitLoop = 10000;
        msStuckDetectStop = 10000;
    }

    @Override
    public void setup() {
        drive = new SampleTankDriveREV(hardwareMap);

        hangMotors = new DcMotor[2];
        hangMotors[0] = mHardwareFactory.getDcMotor("l1");
        hangMotors[1] = mHardwareFactory.getDcMotor("l2");

        gyro = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        gyro.initialize(parameters);

        claimServo = mHardwareFactory.getServo("marker");
        data = new AutoLib.Data();
        data.Float = 2;

        hangMotors[0].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hangMotors[0].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        hangMotors[1].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        hangMotors[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hangMotors[1].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        hangMotors[1].setDirection(DcMotor.Direction.REVERSE);

        motors = new DcMotorEx[4];

        motors[0] = mHardwareFactory.getDcMotorEx("fr");
        motors[1] = mHardwareFactory.getDcMotorEx("br");
        motors[2] = mHardwareFactory.getDcMotorEx("fl");
        motors[3] = mHardwareFactory.getDcMotorEx("bl");

        motors[0].setDirection(DcMotorEx.Direction.REVERSE);
        motors[1].setDirection(DcMotorEx.Direction.REVERSE);

        for(DcMotorEx em : motors) {
            em.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            em.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        pid = new SensorLib.PID((float) Kp, (float) Ki, (float) Kd, (float) KiCutoff);

        samplePath1 = new AutoLib.Step[6];
        samplePath1[0] = new AutoLib.GyroRotateStep(this, motors, (float) MaxPower, gyro, pid, (float) Angle1, (float) AngleTolerance);
        samplePath1[1] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward(Distance2).build());
        samplePath1[2] = new AutoLib.GyroRotateStep(this, motors, (float) MaxPower, gyro, pid, (float) Angle2, (float) AngleTolerance);
        samplePath1[3] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward(Distance3).build());
        samplePath1[4] = new AutoLib.ServoStep(claimServo, 1.0);
        samplePath1[5] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().back(Distance4).build());

        samplePath2 = new AutoLib.Step[2];
        samplePath2[0] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward(15).build());
        samplePath2[1] = new AutoLib.ServoStep(claimServo, 1.0);

        samplePath3 = new AutoLib.Step[6];
        samplePath3[0] = new AutoLib.GyroRotateStep(this, motors, (float) MaxPower, gyro, pid, (float) -Angle1, (float) AngleTolerance);
        samplePath3[1] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward(Distance2).build());
        samplePath3[2] = new AutoLib.GyroRotateStep(this, motors, (float) MaxPower, gyro, pid, (float) -Angle2, (float) AngleTolerance);
        samplePath3[3] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward(Distance3).build());
        samplePath3[4] = new AutoLib.ServoStep(claimServo, 1.0);
        samplePath3[5] = new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().back(Distance4).build());

        doStepStep = new DoStepsStep(samplePath2);

        mSequence.add(new UnlatchStep(this, hangMotors[1], hangMotors[0], 1.0, 1.8f));
        mSequence.add(new SampleStep(mVlib, this, data));
        mSequence.add(new RoadRunnerImplementer.Follow2dTrajectory(this, drive, drive.trajectoryBuilder().forward((float) Distance1).build()));
        mSequence.add(new AutoLib.MoveByTimeStep(hangMotors, -1, 1.4, true)); // lower the lift
        mSequence.add(new ChoosePathStep(doStepStep, samplePath1, samplePath2, samplePath3, data));
        mSequence.add(doStepStep);
        mSequence.add(new AutoLib.ServoStep(claimServo, 0.4));
    }
}