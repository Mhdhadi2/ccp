# Asia Pacific Airport - Concurrent Programming Simulation

## Purpose
This project simulates a concurrent airport environment using only **low-level Java concurrency** mechanisms:
`Thread`, `Runnable`, `synchronized`, `wait()`, and `notify()`, `notifyAll()`.

The simulation demonstrates:
- A single shared runway
- Three gates
- Maximum of three planes on the ground
- Emergency landing priority
- Concurrent gate services

## Requirements Implemented
- 6 planes total (Plane-6 is emergency)
- Random arrival delays (0–2 seconds)
- Random passenger count (10–50)
- Three concurrent services at gate
- Shared refuel truck (synchronized)
- Waiting statistics (min, max, avg)

## Compile & Run (IntelliJ IDEA or VS Code)
1. Open the folder in IntelliJ IDEA or VS Code.
2. Ensure all `.java` files are in the same source directory.
3. Compile and run `Main.java`.

### Using Command Line
```bash
javac *.java
java Main
```

## Notes
- Output includes detailed logs from ATC, planes, and service threads.
- Entire simulation finishes in under 60 seconds.

