package core;

public class Vector3D {
    public float x, y, z;

    public Vector3D() {}

    public Vector3D(float x, float y, float z) {
        this.set(x, y, z);
    }

    public Vector3D(Vector3D v) {
        this.set(v);
    }

    public void set(Vector3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3D v) {
        this.x += v.x;
        this.y += v.y;
        this.z += v.z;
    }

    public void add(Vector3D v, float times) {
        this.x += v.x*times;
        this.y += v.y*times;
        this.z += v.z*times;
    }

    public void subtract(Vector3D v) {
        this.x -= v.x;
        this.y -= v.y;
        this.z -= v.z;
    }

    public void subtract(Vector3D v, float times) {
        this.x -= v.x*times;
        this.y -= v.y*times;
        this.z -= v.z*times;
    }

    public static float dot(Vector3D v1, Vector3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public float dot(Vector3D v) {
        return Vector3D.dot(this, v);
    }

    public void cross(Vector3D v1, Vector3D v2) {
        this.x = v1.y*v2.z - v1.z*v2.y;
        this.y = v1.z*v2.x - v1.x*v2.z;
        this.z = v1.x*v2.y - v1.y*v2.x;
    }

    public Vector3D cross(Vector3D v) {
        Vector3D res = new Vector3D();
        res.cross(this, v);
        return res;
    }

    public float getLength() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public void unit() {
        float length = getLength();
        x /= length;
        y /= length;
        z /= length;
    }

    public void scale(float scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
    }

    public void rotate_Y(int angle) {
        float sin = LookupTables.sin[angle];
        float cos = LookupTables.cos[angle];
        float old_X = x;
        float old_Z = z;
        x = cos*old_X - sin*old_Z;
        z = sin*old_X + cos*old_Z;
    }

    public void rotate_X(int angle) {
        float sin = LookupTables.sin[angle];
        float cos = LookupTables.cos[angle];
        float old_Y = y;
        float old_Z = z;
        y = cos*old_Y - sin*old_Z;
        z = sin*old_Y + cos*old_Z;
    }

    public void rotate_Z(int angle) {
        float sin = LookupTables.sin[angle];
        float cos = LookupTables.cos[angle];
        float old_X = x;
        float old_Y = y;
        x = cos*old_X - sin*old_Y;
        y = sin*old_X + cos*old_Y;
    }
}
