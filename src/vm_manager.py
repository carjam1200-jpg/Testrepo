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
    disk = disk_var.get().strip()
    if not name:
        messagebox.showerror('vmtest', 'Enter a VM name.')
        return
    vms = load_vms()
    if any(v['name'] == name for v in vms):
        messagebox.showerror('vmtest', 'That VM already exists.')
        return
    vms.append({'name': name, 'iso': iso, 'disk': disk, 'ram': ram_var.get(), 'cpus': cpu_var.get()})
    save_vms(vms)
    refresh()

def select_iso():
    path = filedialog.askopenfilename(title='Enter/select ISO', filetypes=[('ISO images', '*.iso'), ('All files', '*.*')])
    if path:
        iso_var.set(path)

def select_disk():
    path = filedialog.askopenfilename(title='Select virtual disk', filetypes=[('QCOW2 disks', '*.qcow2'), ('Raw disks', '*.img'), ('All files', '*.*')])
    if path:
        disk_var.set(path)

def iso_to_disk():
    """Convert an ISO file into a QCOW2 image using qemu-img.

    This creates a virtual disk image; it never writes to a physical disk.
    """
    iso = iso_var.get().strip()
    if not iso:
        messagebox.showerror('vmtest', 'Enter/select an ISO first.')
        return
    if not os.path.isfile(iso):
        messagebox.showerror('vmtest', 'The selected ISO does not exist.')
        return
    qemu_img = shutil.which('qemu-img')
    if not qemu_img:
        messagebox.showerror('vmtest', 'qemu-img was not found in PATH.')
        return
    output = filedialog.asksaveasfilename(
        title='Create virtual disk from ISO',
        defaultextension='.qcow2',
        filetypes=[('QCOW2 virtual disk', '*.qcow2')],
    )
    if not output:
        return
    try:
        subprocess.run([qemu_img, 'convert', '-f', 'raw', '-O', 'qcow2', iso, output], check=True)
        disk_var.set(output)
        messagebox.showinfo('vmtest', f'Created virtual disk:\n{output}')
    except subprocess.CalledProcessError as exc:
        messagebox.showerror('vmtest', f'qemu-img conversion failed (exit {exc.returncode}).')

def refresh():
    for item in tree.get_children():
        tree.delete(item)
    for vm in load_vms():
        tree.insert('', 'end', values=(vm['name'], vm.get('ram', '2048'), vm.get('cpus', '2'), vm.get('iso') or '(none)', vm.get('disk') or '(none)')))

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
    cmd = [qemu, '-name', vm['name'], '-m', str(vm.get('ram', '2048')), '-smp', str(vm.get('cpus', '2'))]
    if vm.get('disk'):
        cmd += ['-drive', f"file={vm['disk']},format=qcow2"]
    if vm.get('iso'):
        cmd += ['-cdrom', vm['iso'], '-boot', 'd']
    subprocess.Popen(cmd)

root = tk.Tk()
root.title('vmtest - Mini VM Manager')
root.geometry('1050x600')

name_var = tk.StringVar(value='TestVM')
iso_var = tk.StringVar()
disk_var = tk.StringVar()
ram_var = tk.StringVar(value='2048')
cpu_var = tk.StringVar(value='2')

controls = ttk.Frame(root, padding=12)
controls.pack(fill='x')

ttk.Label(controls, text='VM name').grid(row=0, column=0, sticky='w')
ttk.Entry(controls, textvariable=name_var, width=22).grid(row=0, column=1, padx=6)
ttk.Label(controls, text='RAM (MB)').grid(row=0, column=2, sticky='w')
ttk.Entry(controls, textvariable=ram_var, width=10).grid(row=0, column=3, padx=6)
ttk.Label(controls, text='CPUs').grid(row=0, column=4, sticky='w')
ttk.Entry(controls, textvariable=cpu_var, width=6).grid(row=0, column=5, padx=6)

ttk.Label(controls, text='ISO').grid(row=1, column=0, sticky='w')
ttk.Entry(controls, textvariable=iso_var, width=65).grid(row=1, column=1, columnspan=5, padx=6, sticky='ew')
ttk.Button(controls, text='Enter / Select ISO', command=select_iso).grid(row=1, column=6, padx=4)
ttk.Button(controls, text='ISO → Disk', command=iso_to_disk).grid(row=1, column=7, padx=4)

ttk.Label(controls, text='Virtual disk').grid(row=2, column=0, sticky='w')
ttk.Entry(controls, textvariable=disk_var, width=65).grid(row=2, column=1, columnspan=5, padx=6, sticky='ew')
ttk.Button(controls, text='Select Disk', command=select_disk).grid(row=2, column=6, padx=4)

ttk.Button(controls, text='Create VM', command=create_vm).grid(row=3, column=0, pady=8)
ttk.Button(controls, text='Start VM', command=start_vm).grid(row=3, column=1, sticky='w')

tree = ttk.Treeview(root, columns=('name', 'ram', 'cpu', 'iso', 'disk'), show='headings')
for col, title in [('name','Name'), ('ram','RAM'), ('cpu','CPUs'), ('iso','ISO'), ('disk','Virtual Disk')]:
    tree.heading(col, text=title)
tree.pack(fill='both', expand=True, padx=12, pady=12)

refresh()
root.mainloop()
