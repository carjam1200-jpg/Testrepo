package com.customandroid.server.pm;

import com.customandroid.content.PackageInfo;
import java.util.HashMap;
import java.util.Map;

public class PackageManagerService {
    private final Map<String, PackageInfo> packages = new HashMap<>();

    public void installPackage(String name, String version) {
        packages.put(name, new PackageInfo(name, version));
    }

    public PackageInfo getPackageInfo(String name) {
        return packages.get(name);
    }

    public boolean isInstalled(String name) {
        return packages.containsKey(name);
    }
}
