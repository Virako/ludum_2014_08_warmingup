package com.mygdx.ludum;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

public class ConfigControllers {
	// Botones del mando / Gamepad Buttons
	public boolean leftPressed = false;
	public boolean rightPressed = false;
	public boolean jumpPressed = false;

    public ConfigControllers() {
    }

    public void init() {
        // CODIGO DE PRUEBAS PARA LOS MANDOS / GAMEPAD TESTING CODE

        // print the currently connected controllers to the console
        System.out.println("Controllers: " + Controllers.getControllers().size);
        int i = 0;
        for (Controller controller : Controllers.getControllers()) {
            System.out.println("#" + i++ + ": " + controller.getName());
        }
        if (Controllers.getControllers().size == 0)
            System.out.println("No controllers attached");

        // setup the listener that prints events to the console
        Controllers.addListener(new ControllerListener() {
            //public int indexOf(Controller controller) {
            //    return Controllers.getControllers().indexOf(controller, true);
            //}

            @Override
            public void connected (Controller controller) {
                System.out.println("connected " + controller.getName());
                int i = 0;
                for (Controller c : Controllers.getControllers()) {
                    System.out.println("#" + i++ + ": " + c.getName());
                }
            }

            @Override
            public void disconnected (Controller controller) {
                System.out.println("disconnected " + controller.getName());
                int i = 0;
                for (Controller c : Controllers.getControllers()) {
                    System.out.println("#" + i++ + ": " + c.getName());
                }
                if (Controllers.getControllers().size == 0) System.out.println("No controllers attached");
            }

            @Override
            public boolean buttonDown (Controller controller, int buttonIndex) {
                // System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " down");
                if (buttonIndex == 0){
                    ConfigControllers.this.jumpPressed = true;
                }
                return false;
            }

            @Override
            public boolean buttonUp (Controller controller, int buttonIndex) {
                // System.out.println("#" + indexOf(controller) + ", button " + buttonIndex + " up");
                if (buttonIndex == 0){
                    ConfigControllers.this.jumpPressed = false;
                }
                return false;
            }

            @Override
            public boolean axisMoved (Controller controller, int axisIndex, float value) {
                // System.out.println("#" + indexOf(controller) + ", axis " + axisIndex + ": " + value);
                return false;
            }

            @Override
            public boolean xSliderMoved (Controller controller, int sliderIndex, boolean value) {
                // System.out.println("#" + indexOf(controller) + ", x slider " + sliderIndex + ": " + value);
                return false;
            }

            @Override
            public boolean ySliderMoved (Controller controller, int sliderIndex, boolean value) {
                // System.out.println("#" + indexOf(controller) + ", y slider " + sliderIndex + ": " + value);
                return false;
            }

            @Override
            public boolean accelerometerMoved (Controller controller, int accelerometerIndex, Vector3 value) {
                // not printing this as we get to many values
                return false;
            }

            @Override
            public boolean povMoved(Controller controller, int povCode, PovDirection value) {
                // TODO Auto-generated method stub
                return false;
            }
        });

    }
}