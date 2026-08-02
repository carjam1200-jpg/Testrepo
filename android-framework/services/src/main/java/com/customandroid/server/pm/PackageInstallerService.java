package com.customandroid.server.pm;

public class PackageInstallerService {
    private final PackageManagerService packageManager;

    public PackageInstallerService(PackageManagerService packageManager) {
        this.packageManager = packageManager;
    }

    public void install(String packageName, String version) {
        packageManager.installPackage(packageName, version);
    }
}
