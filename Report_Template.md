# Report Template — Asia Pacific Airport Simulation

## 1. Introduction
This report describes the design and implementation of a multithreaded simulation of “Asia Pacific Airport.” The project demonstrates low-level Java concurrency using `Thread`, `synchronized`, and `wait()/notifyAll()` without higher-level frameworks. The simulation models six planes interacting with a shared runway, three gates, and a shared refuel truck. It also includes an emergency landing scenario and produces detailed logs and statistics.

## 2. Assumptions
- A maximum of three planes can be on airport grounds (runway + gates) at any time.
- Planes that cannot land must wait in the air.
- Only one plane can use the runway at a time for landing or takeoff.
- Only one plane can refuel at a time (shared refuel truck).
- Gate services (passengers, cleaning, refueling) execute concurrently while docked.
- Plane-6 is the emergency plane and receives priority for landing.

## 3. Basic Requirements Met
- Six planes are simulated, each running as a separate thread.
- A single runway is shared for landing and takeoff.
- Three gates are assigned to planes on a first-available basis.
- Maximum of three planes on the ground is enforced.
- Planes wait in air when the airport is full.
- Each plane logs lifecycle events.
- Plane waits for gate services to complete before takeoff.
- Thread.sleep is used to simulate realistic timing.

## 4. Additional Requirements Met
- Emergency landing priority is implemented using a separate queue for emergency planes.
- Final statistics include minimum, maximum, and average waiting times.
- Total planes served and total passengers boarded are reported.
- Sanity check confirms all gates are empty at the end.

## 5. Concurrency Concepts Used
### Threads
Each plane and service task runs in its own thread. Example:
```java
PassengerService ps = new PassengerService(getName(), passengerCount);
ps.start();
```

### Shared Resources
The runway, gates, and refuel truck are shared. Access is controlled through synchronized methods in the `Airport` and `RefuelTruck` classes.

### Mutual Exclusion
The runway is protected with a synchronized block so only one plane can land or take off at a time.

### Atomicity
State updates such as `planesOnGround++` and `planesOnGround--` are performed inside synchronized sections to ensure atomicity.

### Synchronization
The `Airport` class uses synchronized methods to coordinate access:
```java
public synchronized void requestLanding(Plane plane) { ... }
```

### wait()/notifyAll()
Planes waiting to land or take off block using `wait()` and are awakened using `notifyAll()` when the runway or gates change.

## 6. Safety Aspects of Multithreaded Systems
- **Race conditions** are prevented by synchronizing access to shared state (runway, gates, queues).
- **Deadlocks** are avoided by keeping synchronized sections small and avoiding nested locks.
- **Starvation** is prevented by giving emergency planes priority while still allowing normal planes to land in order.

## 7. Justification of Coding Techniques
Low-level synchronization primitives were chosen to satisfy the assignment restrictions. The `Airport` class centralizes access to shared resources, reducing the risk of inconsistent state. Service tasks were separated into individual classes to keep the design modular and easy to understand.

## 8. Requirements Not Met
All requirements were fulfilled.

## 9. Conclusion
The Asia Pacific Airport simulation demonstrates effective use of Java concurrency primitives in a realistic multithreaded environment. The implementation satisfies all assignment constraints, handles emergency priority correctly, avoids concurrency hazards, and produces clear logging and statistics.

