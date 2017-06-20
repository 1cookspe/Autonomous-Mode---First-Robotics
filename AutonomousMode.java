package org.usfirst.frc.team5409.robot;


import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.DoubleSolenoid;


import javax.swing.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
        Joystick driver;
        Joystick liftMechanism;
        RobotDrive motors;
        Jaguar lift;
        String direction = "None";
        DoubleSolenoid mySolenoid= new DoubleSolenoid(0,1);
        boolean isOpen=false;
        Timer timer;
        int actionsExecuted = 0;
        DigitalInput proxSensor;
        DigitalInput proxSensor1;
        DigitalInput proxSensor2;
        DigitalInput proxSensor3;
        double currentLevel;
        double joystickDirection;
        
        final float[] GEAR  = {0.5f, 0.66f, 1};
    public void robotInit() {
            motors = new RobotDrive(0, 1, 2, 3);
            lift = new Jaguar(4);
            driver = new Joystick(0);
            liftMechanism = new Joystick(1);
            /*bottom */ proxSensor = new DigitalInput(0);
            proxSensor1 = new DigitalInput(1);
            proxSensor2 = new DigitalInput(2);
            /* top */ proxSensor3= new DigitalInput(3);
    }


    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
            executeNextCommand();
    }
    
    public void driveRobot(double leftSpeed, double rightSpeed, double seconds, boolean running) {


            double currentTime = System.currentTimeMillis() / 1000.0;
            double futureTime = (double) (currentTime + seconds);
            
            if (!running) {
            do {
                    currentTime = System.currentTimeMillis() / 1000.0;
                    motors.tankDrive(leftSpeed, rightSpeed);
            } while (currentTime < futureTime);
            } else {
                    motors.tankDrive(0, 0);
            }
            System.out.println(currentTime);
            System.out.println(futureTime);
            actionsExecuted++;
            executeNextCommand();
            


    }
    
    
    public void executeNextCommand() {
            switch (actionsExecuted) { 
            case 0:
                    //70 degree turn into box
                    driveRobot(-0.7, 0.5, 2, false);
                    break;
            case 1:
                    //go forward into box
                    driveRobot(-0.33, -0.3, 0.7, false);
                    break;
            case 2:
                //backwards
                    driveRobot(0.8, 0.65, 2, false);
                    break;
            case 3:
                    //Into box
                    driveRobot(-0.6, -0.7, 1.1, false);
                    break;
//            case 2:
//                    driveRobot(-0.3, -0.6, 1.1);
//                    break;
//            case 3:
//                    driveRobot(-0.33, -0.3, 1.3);
//                    break;
//            case 4:
//                    driveRobot(-0.2, -0.2, 1);
//                    break;
            case 4:
                    //lift box
                    liftBox();
                    break;
            case 5:
                    //move back and turn up field
                    driveRobot(0.4, 0.3, 0.32, false);
                    break;
            case 6:
                    //move forward
                    driveRobot(0.6, 0.3, 4, false);
                    break;
            case 7:
                    //drop box
                    dropBox();
                    break;
            case 8:
                    //move back
                    driveRobot(-0.6, -0.64, 2, false);        
                    break;
                    /*In case we want to go for second */
            case 9        :
                    //move to side to get can
                    driveRobot(0.5, 0.3, 1.2, false);
                    break;
            case 10:
                    liftBox();
                    break;
            case 11:
                    //turn to left to pick up robot
                    driveRobot(0.5, 0.3, 1.2, false);
                    break;
            case 12:
                    //drive toward auto zone
                    driveRobot(0.64, 0.6, 2, false);
                    break;
            case 13:
                    dropBox();
                    break; 
            }
    }
    
    public void liftBox() {
            driveRobot(0, 0, 4, true);
            
            // Open claw
            mySolenoid.set(DoubleSolenoid.Value.kReverse);
                isOpen=true;
                
                //Close claw
                                mySolenoid.set(DoubleSolenoid.Value.kForward);
                                isOpen=false;
                
                //Move claw up
                while (proxSensor3.get()) {
                        currentLevel = currentLevel + 1;
                lift.set(-1.0);


    }
        
                
            actionsExecuted++;        
            executeNextCommand();
    }
    
    public void dropBox() {
            driveRobot(0,0,4, true);  
            
            //Move claw down
                 while (proxSensor1.get()) {
                                currentLevel = currentLevel - 0.5;
                                lift.set(1.0);
                }
        
            //Open claw
            mySolenoid.set(DoubleSolenoid.Value.kReverse);
                isOpen=true;
                
            actionsExecuted++;
            executeNextCommand();
    }
    
  
    /**
     * This function is called periodically during operator control
     */
    
    public void teleopPeriodic() {
            // Left-Right Input
            float leftRight = 0;
            leftRight = (float)driver.getRawAxis(0);
            float rightTrigger = (float)driver.getRawAxis(3) * -1;
            float leftTrigger = (float)driver.getRawAxis(2);
            boolean rightCloseButton = driver.getRawButton(8);
            boolean leftOpenButton = driver.getRawButton(10);
            
            boolean close = liftMechanism.getRawButton(3);
            boolean open = liftMechanism.getRawButton(4);
            float joystickDirection = (float) liftMechanism.getRawAxis(1);
            
            //up and down motor
            float rightStick = (float)driver.getRawAxis(5);
            
            
            // Trigger Input
            float currentSpeed =  (rightTrigger + leftTrigger) * GEAR[0];
            
            float finalRightSpeed = currentSpeed;
            float finalLeftSpeed = currentSpeed;
            
            // Final Speed Outputs
            
            
            if(leftRight < 0){ // Turning Right
                    direction = "Right";
            }else if(leftRight > 0){ // Turning Left
                    direction = "Left";
            }
            
            // Value to negate from final speed
            float turnSpeed = Math.abs(leftRight) * currentSpeed;
            
            // Negate speed
            switch(direction){
            case "Right":
                    finalRightSpeed -= turnSpeed*3;
                    break;
            case "Left":
                    finalLeftSpeed -= turnSpeed*3;
                    break;
            case "None":
                    break;
            }
            
            if(rightTrigger!=0){
                finalLeftSpeed = finalLeftSpeed *((float)0.947);
               }
               else if(leftTrigger!=0){
                        finalRightSpeed = finalRightSpeed*((float)0.947);
               }
            
            if(rightStick!=0){
                    
            }
            if(currentSpeed != 0){
                    motors.tankDrive(finalLeftSpeed, finalRightSpeed);
                    System.out.println("Final Left Speed:" + finalLeftSpeed);
            }else{ // Stationary turn
                    motors.tankDrive(leftRight * GEAR[0], -1 * leftRight * GEAR[0]);
            }
            
            if (close&&!isOpen) {
                    mySolenoid.set(DoubleSolenoid.Value.kForward);
                    isOpen=true;
                    System.out.println("working");
            }
            else if (open&&isOpen) {
                    mySolenoid.set(DoubleSolenoid.Value.kReverse);
                    isOpen=false;
                    System.out.println("working");
            }
            else{
                    mySolenoid.set(DoubleSolenoid.Value.kOff);
            }
            
            if (joystickDirection > 0) {
                    
            } else if (joystickDirection < 0) {
                    
            } else {
                    mySolenoid.set(DoubleSolenoid.Value.kOff);
            }
            
    }
    
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
            if(driver.getRawButton(1)){
                    System.out.println("Test Print");
            }
    }
    
}