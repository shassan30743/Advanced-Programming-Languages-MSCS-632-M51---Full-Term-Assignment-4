import java.util.*;

public class SchedulerApp {

    private static final int MAX_SHIFT_CAPACITY = 2;   // At most 2 employees per day-shift
    private static final int MAX_DAYS_PER_EMPLOYEE = 5; // Each employee can work up to 5 days
    private static final int MIN_EMPLOYEES_REQUIRED = 12; // For guaranteed coverage

    enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    enum Shift {
        MORNING, AFTERNOON, EVENING
    }

    static class Employee {
        private String name;
        private int daysAssigned; // how many days they've been scheduled so far

        public Employee(String name) {
            this.name = name;
            this.daysAssigned = 0;
        }

        public String getName() {
            return name;
        }

        public int getDaysAssigned() {
            return daysAssigned;
        }

        public void incrementDaysAssigned() {
            daysAssigned++;
        }
    }

    /**
     * WeeklySchedule structure:
     *   Map<Day, Map<Shift, List<Employee>>>
     * For each Day, for each Shift, we store a list of employees assigned (up to 2).
     */
    static class WeeklySchedule {
        private Map<Day, Map<Shift, List<Employee>>> schedule;

        public WeeklySchedule() {
            schedule = new HashMap<>();
            for (Day d : Day.values()) {
                Map<Shift, List<Employee>> shiftMap = new HashMap<>();
                for (Shift s : Shift.values()) {
                    shiftMap.put(s, new ArrayList<>());
                }
                schedule.put(d, shiftMap);
            }
        }

        /**
         * Checks how many employees are assigned on a given (day, shift).
         */
        public int countEmployees(Day day, Shift shift) {
            return schedule.get(day).get(shift).size();
        }

        /**
         * Assigns an employee to a day-shift. (Doesn't check capacity or conflicts, so be sure
         * to check before calling this.)
         */
        public void assign(Day day, Shift shift, Employee emp) {
            schedule.get(day).get(shift).add(emp);
        }

        /**
         * Prints out the final schedule in a neat format.
         */
        public void printSchedule() {
            System.out.println("\n===== FINAL WEEKLY SCHEDULE =====");
            for (Day d : Day.values()) {
                System.out.println("\n--- " + d + " ---");
                for (Shift s : Shift.values()) {
                    List<Employee> emps = schedule.get(d).get(s);
                    System.out.print("  " + s + ": ");
                    if (emps.isEmpty()) {
                        System.out.print("No one assigned");
                    } else {
                        for (Employee e : emps) {
                            System.out.print(e.getName() + " ");
                        }
                    }
                    System.out.println();
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1) Ask how many employees
        System.out.print("How many employees do you want to schedule? ");
        int numEmployees = Integer.parseInt(scanner.nextLine().trim());

        // 1a) Check if we have enough employees to guarantee coverage
        if (numEmployees < MIN_EMPLOYEES_REQUIRED) {
            System.out.println("\nWARNING: For full 7-day coverage with 2 employees per shift, "
                    + "you need at least " + MIN_EMPLOYEES_REQUIRED + " employees each working 5 days.\n"
                    + "You only have " + numEmployees + " employees, so the schedule might be incomplete.\n");
        } else {
            System.out.println("\nGreat! You have " + numEmployees + " employees, which should be enough "
                    + "to cover 7 days (2 employees per shift).");
        }

        // 2) Gather employees
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < numEmployees; i++) {
            System.out.print("Enter name for Employee #" + (i + 1) + ": ");
            String name = scanner.nextLine().trim();
            Employee emp = new Employee(name);
            employees.add(emp);
        }

        WeeklySchedule schedule = new WeeklySchedule();
        Random rand = new Random();

        // 3) For each employee, go day by day, ask user for shift.
        for (Employee emp : employees) {
            System.out.println("\n=== Assigning shifts for " + emp.getName() + " ===");

            // They can only work up to MAX_DAYS_PER_EMPLOYEE
            for (Day day : Day.values()) {
                if (emp.getDaysAssigned() >= MAX_DAYS_PER_EMPLOYEE) {
                    System.out.println(emp.getName() + " has already worked " + MAX_DAYS_PER_EMPLOYEE 
                            + " days. Skipping remaining days.");
                    break; // proceed to next employee
                }

                // Attempt to get a valid shift choice from the user for this day
                System.out.println("\n" + emp.getName() + ", pick a shift for " + day
                        + " (MORNING/AFTERNOON/EVENING) or leave blank if no preference.\n"
                        + "If all shifts are full or you type 'skip', you won't work this day.");

                while (true) {
                    System.out.print("Your choice (or blank for no preference, 'skip' to skip day): ");
                    String input = scanner.nextLine().trim().toUpperCase();

                    // If user types "skip", we skip this day for that employee
                    if ("SKIP".equals(input)) {
                        System.out.println("Skipping " + day + " for " + emp.getName());
                        break; // move on to next day
                    }

                    // If blank => no preference => pick a random shift that isn't full
                    if (input.isEmpty()) {
                        List<Shift> availableShifts = new ArrayList<>();
                        for (Shift s : Shift.values()) {
                            if (schedule.countEmployees(day, s) < MAX_SHIFT_CAPACITY) {
                                availableShifts.add(s);
                            }
                        }
                        if (availableShifts.isEmpty()) {
                            System.out.println("All shifts are full on " + day + ". No assignment possible.");
                        } else {
                            // randomly pick one
                            Shift chosen = availableShifts.get(rand.nextInt(availableShifts.size()));
                            schedule.assign(day, chosen, emp);
                            emp.incrementDaysAssigned();
                            System.out.println(emp.getName() + " assigned to " + chosen + " on " + day);
                        }
                        break; // done picking for this day
                    }

                    // Otherwise, user typed something (MORNING/AFTERNOON/EVENING?), or invalid
                    Shift shiftChoice;
                    try {
                        shiftChoice = Shift.valueOf(input);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid shift. Type MORNING, AFTERNOON, EVENING, blank, or 'skip'.");
                        continue; // re-prompt
                    }

                    // Check if that shift is full
                    if (schedule.countEmployees(day, shiftChoice) >= MAX_SHIFT_CAPACITY) {
                        System.out.println("That shift is already full. Pick another or skip.");
                    } else {
                        // Assign and move on
                        schedule.assign(day, shiftChoice, emp);
                        emp.incrementDaysAssigned();
                        System.out.println(emp.getName() + " assigned to " + shiftChoice + " on " + day);
                        break; // done for this day
                    }
                } // end while
            } // end for each day
        } // end for each employee

        // 4) Print final schedule
        schedule.printSchedule();
        scanner.close();
    }
}
