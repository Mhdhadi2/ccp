/**
 * Gate represents a docking gate.
 */
public class Gate {
    private final String name;
    private boolean occupied = false;
    private String currentPlane = null;

    public Gate(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void occupy(String planeName) {
        this.occupied = true;
        this.currentPlane = planeName;
    }

    public void release() {
        this.occupied = false;
        this.currentPlane = null;
    }
}
