package core;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Camera {
    public static Vector3D position;
    public static Vector3D viewDirection;
    public static boolean MOVE_FORWARD, MOVE_BACKWARD, SLIDE_LEFT, SLIDE_RIGHT, LOOK_UP, LOOK_DOWN, LOOK_RIGHT, LOOK_LEFT;
    public static int Y_angle;
    public static int X_angle;
    public static int turnRate=2; // 2 degree per frame
    public static float moveSpeed=0.03f;

    public static void init(float x, float y, float z) {
        position = new Vector3D(x, y, z);
        viewDirection = new Vector3D(0, 0, 1);
    }

    public static void update() {
        if (LOOK_UP) {
            X_angle += turnRate;
            if (X_angle > 89 && X_angle < 180)
                X_angle = 89;
        }
        if (LOOK_DOWN) {
            X_angle -= turnRate;
            if (X_angle >= 180 && X_angle <= 270)
                X_angle = -89;
        }
        if (LOOK_RIGHT) {
            Y_angle += turnRate;
        }
        if (LOOK_LEFT) {
            Y_angle -= turnRate;
        }

        X_angle = (X_angle + 360) % 360;
        Y_angle = (Y_angle + 360) % 360;

        // update viewDirection
        viewDirection.set(0,0,1);
        viewDirection.rotate_X(X_angle);
        viewDirection.rotate_Y(Y_angle);
        viewDirection.unit();

        if (MOVE_FORWARD) {
            position.add(viewDirection, moveSpeed);
        }
        if (MOVE_BACKWARD) {
            position.subtract(viewDirection, moveSpeed);
        }
        if (SLIDE_LEFT) {
            Vector3D left = viewDirection.cross(new Vector3D(0, -1, 0));
            left.unit();
            position.subtract(left, moveSpeed);
        }
        if (SLIDE_RIGHT) {
            Vector3D right = viewDirection.cross(new Vector3D(0, 1, 0));
            right.unit();
            position.subtract(right, moveSpeed);
        }
    }
}
