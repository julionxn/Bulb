package net.pulga22.minigamecore.core.worlds;

/**
 *This record contains the data loaded from the configuration from a point of interest of a WorldOption.
 */
public record PointOfInterest(double x, double y, double z, float pitch, float yaw) {

    @Override
    public String toString() {
        return "PointOfInterest{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointOfInterest that = (PointOfInterest) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0 && Float.compare(that.pitch, pitch) == 0 && Float.compare(that.yaw, yaw) == 0;
    }

}
