# Preemptive Multiple-Level Queues CPU Scheduler (Java)

This program simulates a **preemptive variant of the Multiple-Level Queues CPU scheduling algorithm**. It schedules processes using multiple priority queues and reports the completion time and deadline status for each process.

---

# What the Program Does

- Simulates **100 processes** using the workload described in the assignment.
- Uses **Preemptive Multiple-Level Queues scheduling** with three queues.

Processes are placed into queues based on their burst time:

- **Queue 0** – Round Robin scheduling with time quantum **5**
- **Queue 1** – Round Robin scheduling with time quantum **10**
- **Queue 2** – **First-Come, First-Served (FCFS)**

The scheduler always runs the **highest-priority non-empty queue**.  
If a higher-priority process arrives while a lower-priority process is running, the running process is **preempted**.

The program prints scheduling results including:

- arrival time
- burst time
- deadlines
- queue assignment
- completion time
- deadline status

---

# Main Logic

The scheduler works as follows:

1. Only processes that have **arrived and are not finished** are considered.
2. Each process is **assigned to a queue based on its burst time**.
3. The scheduler selects a process from the **highest-priority non-empty queue**.
4. **Round Robin scheduling** is used for Queue 0 and Queue 1.
5. **FCFS scheduling** is used for Queue 2.
6. If a higher-priority process arrives while a lower-priority process is running, the scheduler **preempts the running process** and switches queues.

---

# Customizing the Input (For Peer Testing)

To test a different arrival scheme, edit the process generation method inside:

## Compile and Run

Compile the program:

- javac mlq_scheduler_simulator.java

The program will simulate the scheduling of processes and print the completion results for each process.
