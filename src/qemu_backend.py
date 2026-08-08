"""QEMU backend used by vmtest."""
import shutil
import subprocess

class QEMUBackend:
    def __init__(self, executable=None):
        self.executable = executable or shutil.which('qemu-system-x86_64')

    @property
    def available(self):
        return self.executable is not None

    def build_command(self, vm):
        if not self.available:
            raise RuntimeError('qemu-system-x86_64 was not found in PATH')
        command = [self.executable, '-name', vm.name, '-m', str(vm.ram_mb), '-smp', str(vm.cpus)]
        if vm.iso:
            command += ['-cdrom', vm.iso, '-boot', 'd']
        if vm.disk:
            command += ['-drive', f'file={vm.disk},format=qcow2']
        return command

    def start(self, vm):
        return subprocess.Popen(self.build_command(vm))
