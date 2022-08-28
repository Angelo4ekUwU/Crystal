/*
 * Copyright (c) 2022 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package io.sapphiremc.crystal.compat;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;

public enum ServerVersion {
    UNKNOWN,

    /**
     * MC Versions: 1.16.4, 1.16.5
     */
    v1_16_R3,

    /**
     * MC Versions: 1.17, 1.17.1
     */
    v1_17_R1,

    /**
     * MC Versions: 1.18, 1.18.1
     */
    v1_18_R1,

    /**
     * MC Versions: 1.18.2
     */
    v1_18_R2,

    /**
     * MC Versions: 1.19
     */
    v1_19_R1;

    private final static String serverPackagePath;
    private final static String serverPackageVersion;
    private final static String serverReleaseVersion;
    private final static ServerVersion serverVersion;

    static {
        serverPackagePath = Bukkit.getServer().getClass().getPackage().getName();
        System.out.println(serverPackagePath);
        serverPackageVersion = serverPackagePath.substring(serverPackagePath.lastIndexOf('.') + 1);
        System.out.println(serverPackageVersion);
        serverReleaseVersion = serverPackageVersion.indexOf('R') != -1 ? serverPackageVersion.substring(serverPackageVersion.indexOf('R') + 1) : "";
        System.out.println(serverReleaseVersion);

        serverVersion = getVersion();
    }

    private static ServerVersion getVersion() {
        final String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return ServerVersion.valueOf(ver);
        } catch (IllegalArgumentException ex) {
            return UNKNOWN;
        }
    }

    public boolean isLessThan(ServerVersion other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAtOrBelow(ServerVersion other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isGreaterThan(ServerVersion other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isAtLeast(ServerVersion other) {
        return this.ordinal() >= other.ordinal();
    }

    public static String getServerVersionString() {
        return serverPackageVersion;
    }

    public static String getVersionReleaseNumber() {
        return serverReleaseVersion;
    }

    public static ServerVersion getServerVersion() {
        return serverVersion;
    }

    public static boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }

    public static boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public static boolean isServerVersionAbove(ServerVersion version) {
        return serverVersion.ordinal() > version.ordinal();
    }

    public static boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public static boolean isServerVersionAtOrBelow(ServerVersion version) {
        return serverVersion.ordinal() <= version.ordinal();
    }

    public static boolean isServerVersionBelow(ServerVersion version) {
        return serverVersion.ordinal() < version.ordinal();
    }
}
