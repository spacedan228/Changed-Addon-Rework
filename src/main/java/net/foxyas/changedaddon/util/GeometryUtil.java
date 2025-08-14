package net.foxyas.changedaddon.util;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for simple geometric checks such as circular or spherical area containment.
 */
public class GeometryUtil {

    /**
     * Checks if a point is inside an ellipsoid (or a sphere if all radii are equal).
     *
     * @param dx      Offset along the X axis from the center
     * @param dy      Offset along the Y axis from the center
     * @param dz      Offset along the Z axis from the center
     * @param radiusX Radius along the X axis
     * @param radiusY Radius along the Y axis
     * @param radiusZ Radius along the Z axis
     * @return true if the point is inside or on the surface of the ellipsoid
     */
    public static boolean isInsideEllipsoid(double dx, double dy, double dz,
                                            double radiusX, double radiusY, double radiusZ) {
        double nx = (dx * dx) / (radiusX * radiusX);
        double ny = (dy * dy) / (radiusY * radiusY);
        double nz = (dz * dz) / (radiusZ * radiusZ);
        return (nx + ny + nz) <= 1.0;
    }

    /**
     * @param dx      Offset along the X axis from the center
     * @param dy      Offset along the Y axis from the center
     * @param dz      Offset along the Z axis from the center
     * @param radiusX Radius along the X axis
     * @param radiusY Radius along the Y axis
     * @param radiusZ Radius along the Z axis
     * @return value of the Ellipsoid
     */
    public static double getEllipsoidValue(double dx, double dy, double dz,
                                           double radiusX, double radiusY, double radiusZ) {
        double nx = (dx * dx) / (radiusX * radiusX);
        double ny = (dy * dy) / (radiusY * radiusY);
        double nz = (dz * dz) / (radiusZ * radiusZ);
        return (nx + ny + nz);
    }

    /**
     * Checks if a point is inside a sphere.
     *
     * @param dx     Offset along the X axis from the center
     * @param dy     Offset along the Y axis from the center
     * @param dz     Offset along the Z axis from the center
     * @param radius Radius of the sphere
     * @return true if the point is inside or on the surface of the sphere
     */
    public static boolean isInsideSphere(double dx, double dy, double dz, double radius) {
        return isInsideEllipsoid(dx, dy, dz, radius, radius, radius);
    }

    /**
     * Checks whether a 2D point is inside a circle.
     *
     * @param point  The 2D point to check (X, Y)
     * @param center The center of the circle
     * @param radius The radius of the circle
     * @return true if the point is inside or on the circle
     */
    public static boolean isInsideCircle(Vec2 point, Vec2 center, float radius) {
        float dx = point.x - center.x;
        float dy = point.y - center.y;
        return (dx * dx + dy * dy) <= (radius * radius);
    }

    /**
     * Checks whether a 3D point is inside a circular area on the XZ plane (ignores Y).
     *
     * @param point  The 3D point to check
     * @param center The center of the circle
     * @param radius The radius of the circle
     * @return true if the point is inside or on the circle
     */
    public static boolean isInsideCircleXZ(Vec3 point, Vec3 center, float radius) {
        double dx = point.x - center.x;
        double dz = point.z - center.z;
        return (dx * dx + dz * dz) <= (radius * radius);
    }

    /**
     * Checks whether a 3D point is inside a full sphere in 3D space.
     *
     * @param point  The point to check
     * @param center The center of the sphere
     * @param radius The radius of the sphere
     * @return true if the point is inside or on the sphere
     */
    public static boolean isInsideSphere(Vec3 point, Vec3 center, float radius) {
        double dx = point.x - center.x;
        double dy = point.y - center.y;
        double dz = point.z - center.z;
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }

    /**
     * Calculates the squared distance between two 3D points.
     * Useful when comparing distances without needing the square root.
     */
    public static double distanceSquared(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dy = a.y - b.y;
        double dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Calculates the squared 2D distance on the XZ plane between two 3D points.
     */
    public static double distanceSquaredXZ(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return dx * dx + dz * dz;
    }

    /**
     * Checks whether a 2D point is inside a rectangle.
     * The rectangle is defined by two corners: min (bottom-left) and max (top-right).
     */
    public static boolean isInsideRectangle(Vec2 point, Vec2 min, Vec2 max) {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y;
    }

    /**
     * Checks whether a 3D point is inside a cuboid (box).
     * The cube is defined by two opposite corners: min and max.
     */
    public static boolean isInsideCube(Vec3 point, Vec3 min, Vec3 max) {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z;
    }

}
