import tkinter as tk
from tkinter import ttk, messagebox
import random

# Employee data storage
employees = {}
work_count = {}
shifts = {day: {"Morning": [], "Afternoon": [], "Evening": []} for day in 
          ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]}

def add_employee():
    """Adds an employee and their shift preferences."""
    name = name_entry.get().strip()
    prefs = [shift1.get(), shift2.get(), shift3.get()]

    if not name or len(set(prefs)) < 3:
        messagebox.showerror("Error", "Enter valid name and unique shift preferences!")
        return

    employees[name] = prefs
    work_count[name] = 0
    update_employee_list()
    name_entry.delete(0, tk.END)

def update_employee_list():
    """Refreshes the employee list display."""
    employee_listbox.delete(0, tk.END)
    for emp, prefs in employees.items():
        employee_listbox.insert(tk.END, f"{emp}: {prefs}")

def assign_shifts():
    """Assigns shifts based on preferences while following constraints."""
    global shifts
    shifts = {day: {"Morning": [], "Afternoon": [], "Evening": []} for day in 
              ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]}
    
    # Reset work count
    for emp in employees.keys():
        work_count[emp] = 0

    # Assign shifts
    for day in shifts.keys():
        for emp, prefs in employees.items():
            if work_count[emp] < 5:
                for shift in prefs:
                    if len(shifts[day][shift]) < 2 and emp not in sum(shifts[day].values(), []):
                        shifts[day][shift].append(emp)
                        work_count[emp] += 1
                        break

    display_schedule()

def display_schedule():
    """Displays the final shift schedule in the GUI."""
    for widget in output_frame.winfo_children():
        widget.destroy()

    row, col = 1, 0
    for day, schedule in shifts.items():
        ttk.Label(output_frame, text=day, font=("Arial", 12, "bold")).grid(row=row, column=col, padx=10, pady=5)
        col += 1
        for shift, workers in schedule.items():
            shift_text = f"{shift}: {', '.join(workers) if workers else 'None'}"
            ttk.Label(output_frame, text=shift_text, font=("Arial", 10)).grid(row=row, column=col, padx=10, pady=5)
            col += 1
        row += 1
        col = 0

# GUI Setup
root = tk.Tk()
root.title("Employee Shift Scheduler")
root.geometry("600x500")

# Input Section
input_frame = ttk.LabelFrame(root, text="Add Employee")
input_frame.pack(pady=10, padx=10, fill="x")

ttk.Label(input_frame, text="Employee Name:").grid(row=0, column=0, padx=5, pady=5)
name_entry = ttk.Entry(input_frame)
name_entry.grid(row=0, column=1, padx=5, pady=5)

ttk.Label(input_frame, text="1st Preference:").grid(row=1, column=0)
shift1 = ttk.Combobox(input_frame, values=["Morning", "Afternoon", "Evening"])
shift1.grid(row=1, column=1)

ttk.Label(input_frame, text="2nd Preference:").grid(row=2, column=0)
shift2 = ttk.Combobox(input_frame, values=["Morning", "Afternoon", "Evening"])
shift2.grid(row=2, column=1)

ttk.Label(input_frame, text="3rd Preference:").grid(row=3, column=0)
shift3 = ttk.Combobox(input_frame, values=["Morning", "Afternoon", "Evening"])
shift3.grid(row=3, column=1)

ttk.Button(input_frame, text="Add Employee", command=add_employee).grid(row=4, column=0, columnspan=2, pady=10)

# Employee List Section
employee_list_frame = ttk.LabelFrame(root, text="Employees & Preferences")
employee_list_frame.pack(pady=10, padx=10, fill="x")

employee_listbox = tk.Listbox(employee_list_frame, height=5)
employee_listbox.pack(fill="x", padx=5, pady=5)

# Assign Shifts Button
ttk.Button(root, text="Generate Schedule", command=assign_shifts).pack(pady=10)

# Output Section
output_frame = ttk.LabelFrame(root, text="Shift Schedule")
output_frame.pack(pady=10, padx=10, fill="both", expand=True)

root.mainloop()
