# Asia Pacific Airport - Concurrent Programming Simulation

A Java concurrency simulation of an airport with multiple planes competing for limited shared resources.

## Overview
This project simulates an airport environment where planes arrive, land, park at gates, and use shared services.

The goal was to practice low-level Java concurrency (threads and synchronization) by modeling:
- limited runway and gate capacity
- service tasks that run at the same time
- shared resources that must be protected with synchronization

## Features
- Simulates planes arriving and requesting landing
- Enforces limited resources:
  - a single shared runway
  - three gates
  - a maximum number of planes allowed on the ground
- Emergency landing priority (Plane-6)
- Gate services running concurrently (passenger + cleaning + refuel)
- Shared refuel truck protected by synchronization
- Prints waiting time statistics (min, max, average)

## Technologies Used
- Java
- Core concurrency tools: `Thread`, `Runnable`, `synchronized`, `wait()`, `notify()`, `notifyAll()`

## Project Structure
- `Main.java` — starts the simulation and creates plane threads
- `Airport.java` — shared airport state (runway/gates) and synchronization logic
- `Plane.java` — plane thread behavior (arrive, land, service, depart)
- `Gate.java` — gate state
- `PassengerService.java`, `CleaningService.java`, `RefuelService.java` — service threads/tasks
- `RefuelTruck.java` — shared resource for refueling
- `Statistics.java` — tracks and prints timing statistics
- `Logger.java` — simple logging helper
- `Utils.java` — helper functions (random values)

## How to Run
1. Clone the repository and enter the folder:
   ```bash
   git clone https://github.com/Mhdhadi2/ccp.git
   cd ccp
   ```

2. Compile:
   ```bash
   javac *.java
   ```

3. Run:
   ```bash
   java Main
   ```

## What I Learned
- How to coordinate multiple threads using `wait()` / `notifyAll()`
- How to protect shared resources using `synchronized`
- How race conditions can happen when several threads compete for the same state
- How to design a simulation with limited capacity (runway, gates, ground limit)
- How to keep logs readable when many threads print at the same time
