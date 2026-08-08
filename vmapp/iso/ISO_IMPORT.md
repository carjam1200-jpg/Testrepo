# ISO Import + Virtual Disk Flow

The future VM app should handle an imported ISO like this:

1. User selects an `.iso` file.
2. The app validates that the file exists and is readable.
3. A VM is created or selected.
4. The app creates a virtual hard disk (VHD/VHDX or another supported backend format) for that VM.
5. The ISO is attached as virtual optical media for installation/boot.
6. The VM firmware boots from the ISO first when an OS has not yet been installed.
7. After installation, the virtual hard disk becomes the normal boot device, similar to the workflow used by desktop VM software.

Important: importing an ISO does **not** mean copying the ISO directly into the VHD. The ISO acts as boot/install media; the guest OS normally installs itself onto the virtual disk.

Planned implementation pieces:
- ISO picker/import service
- ISO metadata and validation
- VHD/VHDX creation service
- VM boot-order manager
- Virtual optical-drive attachment
- Installer-state detection
- VM runtime/backend adapter
