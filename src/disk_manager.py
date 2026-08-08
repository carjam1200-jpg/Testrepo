"""Virtual disk helpers for vmtest."""
import shutil
import subprocess


def qemu_img_path():
    return shutil.which('qemu-img')


def create_disk(path, size='20G'):
    tool = qemu_img_path()
    if not tool:
        raise RuntimeError('qemu-img was not found in PATH')
    return subprocess.run([tool, 'create', '-f', 'qcow2', path, size], check=True)


def disk_info(path):
    tool = qemu_img_path()
    if not tool:
        raise RuntimeError('qemu-img was not found in PATH')
    return subprocess.run([tool, 'info', path], check=True, capture_output=True, text=True).stdout
