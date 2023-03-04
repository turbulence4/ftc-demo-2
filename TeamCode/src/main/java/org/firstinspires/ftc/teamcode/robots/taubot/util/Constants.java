package org.firstinspires.ftc.teamcode.robots.taubot.util;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.robots.taubot.Field;

@Config(value = "PPConstants")
public class Constants {


    //Subsystems
    //----------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    // Physical Constants
    //----------------------------------------------------------------------------------------------

    // driveTrain

    public static double MIN_CHASSIS_LENGTH = 8.5;
    public static double MIN_SAFE_CHASSIS_LENGTH = 10;
    public static double MAX_CHASSIS_LENGTH = 30.5;

    public static final double DISTANCE_SENSOR_TO_FRONT_AXLE = 2.755906;
    public static final double Distance_HUB_TO_UNDERARM_MIN = 5.7;

    public static final double TRACK_WIDTH = 13.5;
    public static final double DISTANCE_BETWEEN_WHEELS = TRACK_WIDTH;


    public static double MAX_ANG_VEL = Math.toRadians(180);
    public static double MAX_ANG_ACCEL = Math.toRadians(60);
    public static final double MAX_RPM = 150;

    public static boolean USE_CUSTOM_VELOCITY_PID = true;
    public static PIDFCoefficients DIFF_MOTOR_VELOCITY_PID = new PIDFCoefficients(10, 3, 0,
            getMotorVelocityF(MAX_RPM / 60 * 1120));
    public static PIDFCoefficients SWERVE_VELOCITY_PID = new PIDFCoefficients(10, 3, 0,
            getMotorVelocityF(MAX_RPM / 60 * 1120));

    public static final double MAX_VELOCITY = 30.0;
    public static final double MAX_ACCELERATION = 10.0;

    public static double DIFF_TICKS_PER_INCH = 34; //rough measure

    // ratios

    public static double DIFF_TICKS_PER_REV = 768; //dummy - update to estimate and then validated

// UnderArm IK
    public static final double SHOULDER_TO_ELBOW = 14.031496;
    public static final double ELBOW_TO_WRIST = 11.0236;
    public static final double SHOULDER_AXLE_TO_GROUND_HEIGHT = 13.75; //todo measure - this is from reach
    public static final double HIGH_TIER_SHIPPING_HUB_HEIGHT = 20.25;

    //----------------------------------------------------------------------------------------------
    // Control Constants
    //----------------------------------------------------------------------------------------------

    public static double EPSILON = 1e-6; // small value used for the approximately equal calculation in MathUtils
    public static double TRIGGER_DEADZONE = 0.1; // gamepad trigger values below this threshold will be ignored
    public static double JOYSTICK_DEADZONE = 0.05;
    public static double LOW_BATTERY_VOLTAGE = 12.0;

    //----------------------------------------------------------------------------------------------
    // Simulation
    //----------------------------------------------------------------------------------------------
    public static boolean USE_MOTOR_SMOOTHING = false;
    public static double INCHES_PER_METER = 39.3701;

    //----------------------------------------------------------------------------------------------
    // Enums
    //----------------------------------------------------------------------------------------------
    public enum Alliance {
        RED(true), BLUE(false);

        private boolean mod;

        Alliance(boolean mod) {
            this.mod = mod;
        }
        public boolean getMod() {
            return mod;
        }
        public void Toggle(){this.mod = !this.mod;}
    }

    public enum Position {
        ORIGIN(new Pose2d(0, 9, Math.toRadians(0))), //not a legal starting position - should never be actually used
        START_LEFT(new Pose2d(9, 1.5 * Field.INCHES_PER_GRID, Math.toRadians(0))),
        START_RIGHT(new Pose2d(9, -1.5 * Field.INCHES_PER_GRID, Math.toRadians(0))),

        RED_SHIPPING_HUB(new Pose2d(-12, -24)),
        BLUE_SHIPPING_HUB(new Pose2d(-12, 24));

        private final Pose2d pose;

        Position(Pose2d pose) {
            this.pose = pose;
        }

        public Pose2d getPose() {
            return pose;
        }
    }

    public static double diffEncoderTicksToInches(double ticks) {
        return ticks / DIFF_TICKS_PER_INCH;
    }


    public static double diffInchesToEncoderTicks(double inches) {
        return inches * DIFF_TICKS_PER_INCH;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        return 32767 / ticksPerSecond;
    }
}