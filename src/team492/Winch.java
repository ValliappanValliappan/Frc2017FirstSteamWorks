/*
 * Copyright (c) 2015 Titan Robotics Club (http://www.titanrobotics.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package team492;

import frclib.FrcCANTalon;

public class Winch
{
	private static final String module = "Winch";
	
    private FrcCANTalon motor1;
    private FrcCANTalon motor2;
    private boolean manualOverride = false;
    private boolean offGround = false;
    private Robot robot;

    public Winch(Robot robot)
    {
    	this.robot = robot;
        motor1 = new FrcCANTalon("WinchMotor1", RobotInfo.CANID_WINCH1);
        motor2 = new FrcCANTalon("WinchMotor2", RobotInfo.CANID_WINCH2);
        motor1.setPositionSensorInverted(false);
    }

    public void setManualOverride(boolean override)
    {
        this.manualOverride = override;
    }

    public boolean isUpperLimitSwitchActive()
    {
        return !motor1.isUpperLimitSwitchActive();
    }

    public boolean isLowerLimitSwitchActive()
    {
        return !motor1.isLowerLimitSwitchActive();
    }

    public double getPosition()
    {
        return motor1.getPosition()*RobotInfo.WINCH_POSITION_SCALE; 
    }

    public void setPower(double power)
    {
    	if(!offGround && getCurrent() >= RobotInfo.WINCH_MOTOR_CURRENT_THRESHOLD){
    		offGround = true;
    		motor1.resetPosition();
    	}
    	
    	if(manualOverride){
    		motor1.setPower(power);
    		motor2.setPower(power);
    	}
    	else if(touchingPlate()){
    		motor1.setPower(0.0);
    		motor2.setPower(0.0);
    	}
    	else if(offGround && getPosition() >= RobotInfo.WINCH_HEIGHT_THRESHOLD){
    		motor1.setPower(power * RobotInfo.WINCH_MOTOR_POWER_SCALE);
    		motor2.setPower(power * RobotInfo.WINCH_MOTOR_POWER_SCALE);
    	}
    	else{
    		motor1.setPower(power);
    		motor2.setPower(power);
    	}
    }
    
    private boolean touchingPlate(){
    	return isUpperLimitSwitchActive() || isLowerLimitSwitchActive();
    }
    
    private double getCurrent(){
    	double current1 = motor1.motor.getOutputCurrent();
    	double current2 = motor2.motor.getOutputCurrent();
    	robot.tracer.traceInfo(module, "motor1Current=%.1f, motor2Current=%.1f", current1, current2);
    	return Math.abs(current1) + Math.abs(current2);
    }
  
}   //class Winch
