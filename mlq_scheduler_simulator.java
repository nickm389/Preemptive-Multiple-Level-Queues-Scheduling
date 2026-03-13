import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class mlq_scheduler_simulator {

    static class process {
        int process_id;
        int arrival_time;
        int burst_time;
        int remaining_time;
        int relative_deadline;
        int absolute_deadline;
        int completion_time = -1;
        int queue_level;

        process(int process_id, int arrival_time, int burst_time, int relative_deadline) {
            this.process_id = process_id;
            this.arrival_time = arrival_time;
            this.burst_time = burst_time;
            this.remaining_time = burst_time;
            this.relative_deadline = relative_deadline;
            this.absolute_deadline = arrival_time + relative_deadline;
            this.queue_level = assign_queue_level(burst_time);
        }

        static int assign_queue_level(int burst_time) {
            if (burst_time <= 15) {
                return 0;
            } else if (burst_time <= 30) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    static class simulation_result {
        List<process> completed_processes;
        int total_time;

        simulation_result(List<process> completed_processes, int total_time) {
            this.completed_processes = completed_processes;
            this.total_time = total_time;
        }
    }

    public static void main(String[] args) {
        List<process> processes = generate_default_processes(100);
        simulation_result result = run_mlq_simulation(processes);

        System.out.println("Preemptive Multiple-Level Queues Scheduling");
        System.out.println("Queue 0 = Round Robin, quantum 5");
        System.out.println("Queue 1 = Round Robin, quantum 10");
        System.out.println("Queue 2 = FCFS");
        System.out.println();

        System.out.printf("%-8s %-8s %-8s %-10s %-8s %-10s %-8s%n",
                "process", "arrival", "runtime", "deadline", "queue", "completed", "met_dl");

        for (process p : result.completed_processes) {
            String met_deadline = p.completion_time <= p.absolute_deadline ? "yes" : "no";
            System.out.printf("%-8d %-8d %-8d %-10d %-8d %-10d %-8s%n",
                    p.process_id,
                    p.arrival_time,
                    p.burst_time,
                    p.absolute_deadline,
                    p.queue_level,
                    p.completion_time,
                    met_deadline);
        }

        System.out.println();
        System.out.println("Total finishing time: " + result.total_time);
    }

    static List<process> generate_default_processes(int number_of_processes) {
        List<process> processes = new ArrayList<>();

        for (int i = 0; i < number_of_processes; i++) {
            int arrival_time;
            if (i < 10) {
                arrival_time = 0;
            } else {
                arrival_time = (i - 9) * 5;
            }

            int burst_time = 5 * ((i % 10) + 1);
            int relative_deadline = 10 * ((i % 10) + 1);

            processes.add(new process(i, arrival_time, burst_time, relative_deadline));
        }

        return processes;
    }

    static simulation_result run_mlq_simulation(List<process> processes) {
        Deque<process> queue_0 = new ArrayDeque<>();
        Deque<process> queue_1 = new ArrayDeque<>();
        Deque<process> queue_2 = new ArrayDeque<>();

        List<process> completed_processes = new ArrayList<>();

        int current_time = 0;
        int next_arrival_index = 0;
        int completed_count = 0;
        int total_processes = processes.size();

        process current_process = null;
        int quantum_used = 0;

        while (completed_count < total_processes) {

            while (next_arrival_index < total_processes &&
                    processes.get(next_arrival_index).arrival_time <= current_time) {
                process p = processes.get(next_arrival_index);

                if (p.queue_level == 0) {
                    queue_0.addLast(p);
                } else if (p.queue_level == 1) {
                    queue_1.addLast(p);
                } else {
                    queue_2.addLast(p);
                }

                next_arrival_index++;
            }

            if (current_process != null) {
                if (current_process.queue_level > 0 && !queue_0.isEmpty()) {
                    requeue_process(current_process, queue_0, queue_1, queue_2);
                    current_process = null;
                    quantum_used = 0;
                } else if (current_process.queue_level > 1 && !queue_1.isEmpty()) {
                    requeue_process(current_process, queue_0, queue_1, queue_2);
                    current_process = null;
                    quantum_used = 0;
                }
            }

            if (current_process == null) {
                if (!queue_0.isEmpty()) {
                    current_process = queue_0.pollFirst();
                } else if (!queue_1.isEmpty()) {
                    current_process = queue_1.pollFirst();
                } else if (!queue_2.isEmpty()) {
                    current_process = queue_2.pollFirst();
                }
                quantum_used = 0;
            }

            if (current_process == null) {
                current_time++;
                continue;
            }

            current_process.remaining_time--;
            quantum_used++;
            current_time++;

            if (current_process.remaining_time == 0) {
                current_process.completion_time = current_time;
                completed_processes.add(current_process);
                completed_count++;
                current_process = null;
                quantum_used = 0;
                continue;
            }

            if (current_process.queue_level == 0 && quantum_used == 5) {
                queue_0.addLast(current_process);
                current_process = null;
                quantum_used = 0;
            } else if (current_process.queue_level == 1 && quantum_used == 10) {
                queue_1.addLast(current_process);
                current_process = null;
                quantum_used = 0;
            }
        }

        completed_processes.sort((a, b) -> Integer.compare(a.process_id, b.process_id));
        return new simulation_result(completed_processes, current_time);
    }

    static void requeue_process(process p, Deque<process> queue_0, Deque<process> queue_1, Deque<process> queue_2) {
        if (p.queue_level == 0) {
            queue_0.addLast(p);
        } else if (p.queue_level == 1) {
            queue_1.addLast(p);
        } else {
            queue_2.addLast(p);
        }
    }
}