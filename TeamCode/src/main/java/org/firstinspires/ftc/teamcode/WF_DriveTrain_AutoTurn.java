/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) y:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) x:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Basic: Omni TeleOp", group="Linear Opmode")
//@Disabled
public class WF_DriveTrain_AutoTurn extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private IMU imu = null;

    private void Initialize()
    {
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "frontleft");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "backleft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontright");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backright");

        imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void runOpMode() {
        Initialize();

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad previousGamepad1 = new Gamepad();

        // Wait for the game to start (driver presses PLAY)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Speed factor to slow down the robot, goes from 0.1 to 1.0
        double speedFactor = 0.5;
        double kp = -0.033;
        boolean isAutoTurning = false;
        double autoTurningStart = 0.0;
        double autoTurningTarget = 0.0;
        imu.resetYaw();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            previousGamepad1.copy(currentGamepad1);
            currentGamepad1.copy(gamepad1);

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double y = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
            double yaw = gamepad1.right_stick_x;

            if (gamepad1.back)
                imu.resetYaw();

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double rotatingSpeed = imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;

            // Field centric driving is activated when any of the hat buttons are pressed
            if (gamepad1.dpad_down || gamepad1.dpad_up || gamepad1.dpad_left || gamepad1.dpad_right) {
                x = gamepad1.dpad_left ? -1 : (gamepad1.dpad_right ? 1 : 0);
                y = gamepad1.dpad_up ? 1 : (gamepad1.dpad_down ? -1 : 0);
                double botHeadingRadians = Math.toRadians(botHeading);
                // Rotate the movement direction counter to the bot's rotation
                double newX = x * Math.cos(-botHeadingRadians) - y * Math.sin(-botHeadingRadians);
                double newY = x * Math.sin(-botHeadingRadians) + y * Math.cos(-botHeadingRadians);
                x = newX;
                y = newY;
            }

            if (gamepad1.left_bumper)
                speedFactor = 0.5;
            if (gamepad1.right_bumper)
                speedFactor = 1.0;

            if (previousGamepad1.left_trigger != 0 && currentGamepad1.left_trigger == 0 && speedFactor > 0.1)
                speedFactor -= 0.1;
            if (previousGamepad1.right_trigger != 0 && currentGamepad1.right_trigger == 0 && speedFactor < 1.0)
                speedFactor += 0.1;

            if (!isAutoTurning &&
                    ((!previousGamepad1.y && currentGamepad1.y) || (!previousGamepad1.x && currentGamepad1.x) ||
                            (!previousGamepad1.b && currentGamepad1.b) || (!previousGamepad1.a && currentGamepad1.a))) {
                isAutoTurning = true;
                autoTurningTarget = currentGamepad1.y ? 0.0 : currentGamepad1.x ? 90.0 : currentGamepad1.b ? -90.0 : 179.9;
                autoTurningStart = runtime.milliseconds();
            }

            if (isAutoTurning)
            {
                double currentTime = runtime.milliseconds();
                double deltaDegrees = (autoTurningTarget - botHeading + 540) % 360 - 180;
                if ((Math.abs(deltaDegrees) < 1.0 && Math.abs(rotatingSpeed) < 2.0) || (currentTime - autoTurningStart > 1300.0)) {
                    isAutoTurning = false;
                }
                else {
                    yaw = kp * deltaDegrees / speedFactor;
                }
            }

            SendPowerToWheels(x, y, yaw, speedFactor);

            telemetry.addData("Status - 9/10", "Run Time: " + runtime.toString());
            telemetry.addData("Speed Factor", "%1.1f", speedFactor);
            telemetry.addData("Bot Heading", "%4.2f", botHeading);
            telemetry.addData("X/Y/Yaw", "%4.2f, %4.2f, %4.2f", x, y, yaw);
            telemetry.update();
        }
    }

    private void SendPowerToWheels(double x, double y, double yaw, double speedFactor)
    {
        double max;
        double leftFrontPower  = y + x + yaw;
        double rightFrontPower = y - x - yaw;
        double leftBackPower   = y - x + yaw;
        double rightBackPower  = y + x - yaw;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send calculated power to wheels
        leftFrontDrive.setPower(leftFrontPower * speedFactor);
        rightFrontDrive.setPower(rightFrontPower * speedFactor);
        leftBackDrive.setPower(leftBackPower * speedFactor);
        rightBackDrive.setPower(rightBackPower * speedFactor);
    }
}

