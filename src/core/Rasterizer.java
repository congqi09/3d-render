package core;

import static java.lang.Math.max;
import static java.lang.Math.min;

// only render triangles
public class Rasterizer {
    public static int screen_w = MainThread.screen_w;
    public static int screen_h = MainThread.screen_h;
    public static int half_screen_w = MainThread.half_screen_w;
    public static int half_screen_h = MainThread.half_screen_h;

    public static int[] screen = MainThread.screen;
    public static int screenDistance = screen_w/2;

    public static Vector3D[] triangleVertices;
    public static Vector3D[] updatedVertices;

    public static int verticesCount = 3;
    public static float[][] vertices2D = new float[4][2];

    public static int[] xLeft = new int[screen_h], xRight = new int[screen_h];
    public static int scanUpperPosition, scanLowerPosition;

    public static int triangleColor;
    public static int renderType;

    public static void init() {
        updatedVertices = new Vector3D[] {
            new Vector3D(0,0, 0),
            new Vector3D(0,0, 0),
            new Vector3D(0,0, 0),
            new Vector3D(0,0, 0),
        };
    }

    public static void rasterize() {
        transformVertices();
        if (testHidden()) return;
        scanTriangle();
        renderTriangle();
    }

    public static void transformVertices() {
        float x = 0, y = 0, z = 0;
        float sinX, sinY, cosX, cosY;
        sinY = LookupTables.sin[Camera.Y_angle];
        cosY = LookupTables.cos[Camera.Y_angle];
        sinX = LookupTables.sin[Camera.X_angle];
        cosX = LookupTables.cos[Camera.X_angle];
        for (int i = 0; i < verticesCount; i++) {
            x = triangleVertices[i].x - Camera.position.x;
            y = triangleVertices[i].y - Camera.position.y;
            z = triangleVertices[i].z - Camera.position.z;

            updatedVertices[i].x = cosY*x - sinY*z;
            updatedVertices[i].z = sinY*x + cosY*z;

            z = updatedVertices[i].z;

            updatedVertices[i].y = cosX*y - sinX*z;
            updatedVertices[i].z = sinX*y + cosX*z;
        }
    }

    public static boolean testHidden() {
        boolean allBehindClippingPlane = true;
        for (int i = 0; i < verticesCount; i++) {
            if (updatedVertices[i].z >= 0.01f) {
                allBehindClippingPlane = false;
                break;
            }
        }
        if (allBehindClippingPlane)
            return true;

        Vector3D edge1 = new Vector3D(updatedVertices[1]);
        edge1.subtract(updatedVertices[0]);
        Vector3D edge2 = new Vector3D(updatedVertices[2]);
        edge2.subtract(updatedVertices[0]);
        Vector3D surfaceNormal = edge1.cross(edge2);
        float dotProduct = surfaceNormal.dot(updatedVertices[0]);
        return dotProduct >= 0;
    }

    public static void scanTriangle() {
        for (int i = 0; i < verticesCount; i++) {
            vertices2D[i][0] = half_screen_w + updatedVertices[i].x * screenDistance / updatedVertices[i].z;
            vertices2D[i][1] = half_screen_h - updatedVertices[i].y * screenDistance / updatedVertices[i].z;
        }

        scanUpperPosition = screen_h;
        scanLowerPosition = -1;

        int temp_x;

        for (int i = 0; i < verticesCount; i++) {
            float[] vertex1 = vertices2D[i];
            float[] vertex2 = vertices2D[i+1 < verticesCount ? i+1 : 0];

            boolean downwards = true; // default direction

            if (vertex1[1] > vertex2[1]) {
                downwards = false;
                float[] tmp = vertex1;
                vertex1 = vertex2;
                vertex2 = tmp;
            }

            float dy = vertex2[1] - vertex1[1];
            if (dy == 0) continue;  // ignore the horizontal edge

            int startY = max((int)(vertex1[1])+1, 0);
            int endY = min((int)(vertex2[1]), screen_h-1);

            scanUpperPosition = min(scanUpperPosition, startY);
            scanLowerPosition = max(scanLowerPosition, endY);

            float gradient = (vertex2[0] - vertex1[0]) * 2048 / dy;
            int g = (int)(gradient);

            int startX = (int)((vertex1[0] * 2048) + (startY - vertex1[1]) * gradient);
            for (int y = startY; y <= endY; y++) {
                temp_x = startX >> 11;
                if (downwards) {
                    xRight[y] = min(temp_x, screen_w);
                } else {
                    xLeft[y] = max(temp_x, 0);
                }
                startX += g;
            }
        }
    }

    public static void renderTriangle() {
        if (renderType == 0) {
            renderSolidTriangle();
        }
    }

    public static void renderSolidTriangle() {  // single color
        for (int i = scanUpperPosition; i < scanLowerPosition; i++) {
            int x_left = xLeft[i]+i*screen_w;
            int x_right = xRight[i]+i*screen_w;
            for (int j = x_left; j <= x_right; j++) {
                screen[j] = triangleColor;
            }
        }
    }
}
