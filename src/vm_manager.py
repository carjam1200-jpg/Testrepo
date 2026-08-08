#!/usr/bin/env python3
"""vmtest - small QEMU VM manager prototype."""
import json
import os
import shutil
import subprocess
import tkinter as tk
from tkinter import filedialog, messagebox, ttk

CONFIG = os.path.join(os.path.expanduser('~'), '.vmtest_vms.json')

def load_vms():
    try:
        with open(CONFIG, 'r', encoding='utf-8') as f:
            return json.load(f)
    except (OSError, json.JSONDecodeError):
        return []

def save_vms(vms):
    with open(CONFIG, 'w', encoding='utf-8') as f:
        json.dump(vms, f, indent=2)

def create_vm():
    name = name_var.get().strip()
    iso = iso_var.get().strip()
    if not name:
        messagebox.showerror('vmtest', 'Enter a VM name.')
        return
    vms = load_vms()
    if any(v['name'] == name for v in vms):
        messagebox.showerror('vmtest', 'That VM already exists.')
        return
    vms.append({'name': name, 'iso': iso, 'ram': ram_var.get(), 'cpus': cpu_var.get()})
    save_vms(vms)
    refresh()

def select_iso():
    path = filedialog.askopenfilename(title='Select ISO', filetypes=[('ISO images', '*.iso'), ('All files', '*.*')])
    if path:
        iso_var.set(path)

def refresh():
    for item in tree.get_children():
        tree.delete(item)
    for vm in load_vms():
        tree.insert('', 'end', values=(vm['name'], vm['ram'], vm['cpus'], vm['iso'] or '(none)'))

def start_vm():
    item = tree.selection()
    if not item:
        messagebox.showinfo('vmtest', 'Select a VM first.')
        return
    values = tree.item(item[0], 'values')
    vm = next(v for v in load_vms() if v['name'] == values[0])
    qemu = shutil.which('qemu-system-x86_64')
    if not qemu:
        messagebox.showerror('vmtest', 'QEMU was not found in PATH. Install QEMU first.')
        return
    cmd = [qemu, '-m', str(vm['ram']), '-smp', str(vm['cpus'])]
    if vm['iso']:
        cmd += ['-cdrom', vm['iso'], '-boot', 'd']
    subprocess.Popen(cmd)

root = tk.Tk()
root.title('vmtest - Mini VM Manager')
root.geometry('850x500')

name_var = tk.StringVar(value='TestVM')
iso_var = tk.StringVar()
ram_var = tk.StringVar(value='2048')
cpu_var = tk.StringVar(value='2')

controls = ttk.Frame(root, padding=12)
controls.pack(fill='x')

ttk.Label(controls, text='VM name').grid(row=0, column=0, sticky='w')
ttk.Entry(controls, textvariable=name_var, width=24).grid(row=0, column=1, padx=6)
ttk.Label(controls, text='RAM (MB)').grid(row=0, column=2, sticky='w')
ttk.Entry(controls, textvariable=ram_var, width=10).grid(row=0, column=3, padx=6)
ttk.Label(controls, text='CPUs').grid(row=0, column=4, sticky='w')
ttk.Entry(controls, textvariable=cpu_var, width=6).grid(row=0, column=5, padx=6)
ttk.Button(controls, text='Select ISO', command=select_iso).grid(row=1, column=0, pady=8)
ttk.Entry(controls, textvariable=iso_var, width=70).grid(row=1, column=1, columnspan=5, padx=6, sticky='ew')
ttk.Button(controls, text='Create VM', command=create_vm).grid(row=2, column=0, pady=6)
ttk.Button(controls, text='Start VM', command=start_vm).grid(row=2, column=1, sticky='w')

tree = ttk.Treeview(root, columns=('name', 'ram', 'cpu', 'iso'), show='headings')
for col, title in [('name','Name'), ('ram','RAM'), ('cpu','CPUs'), ('iso','ISO')]:
    tree.heading(col, text=title)
tree.pack(fill='both', expand=True, padx=12, pady=12)

refresh()
root.mainloop()
