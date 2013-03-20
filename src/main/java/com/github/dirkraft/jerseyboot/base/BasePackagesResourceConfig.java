package com.github.dirkraft.jerseyboot.base;

import com.github.dirkraft.jerseyboot.RunServer;
import com.github.dirkraft.jerseyboot.app.StartupListener;
import com.github.dirkraft.jerseyboot.app.scan.NoopScannerHelper;
import com.github.dirkraft.jerseyboot.app.scan.ScannerHelper;
import com.sun.jersey.api.core.PackagesResourceConfig;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;

/**
 * @author jason
 */
public class BasePackagesResourceConfig extends PackagesResourceConfig {

    public BasePackagesResourceConfig(String... basePkgs) {
        this(new NoopScannerHelper(), basePkgs);
    }

    public BasePackagesResourceConfig(final ScannerHelper scannerHelper, String... basePkgs) {
        super(combine(new String[]{
                "org.codehaus.jackson.jaxrs", // json serialization
                JJConst.BASE_PKG // base components
        }, basePkgs));
        // Support for initialization after context building but before serving requests.
        RunServer.SERVER.getHandler().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarted(LifeCycle event) {
                for (StartupListener startupListener : scannerHelper.findImplementing(StartupListener.class)) {
                    startupListener.onStartup();
                }
            }
        });
        init();
    }

    /**
     * Last thing called by any constructor in this class.
     */
    protected void init() {
        ToStringBuilder.setDefaultStyle(JJConst.DEFAULT_TO_STRING_STYLE);
    }

    protected static String[] combine(String pkg, String... moar) {
        return combine(new String[]{pkg}, moar);
    }

    protected static String[] combine(String[] pkgs, String... moar) {
        if (pkgs == null || moar == null) {
            return pkgs != null ? pkgs : (moar != null ? moar : null);
        }
        String[] combined = new String[pkgs.length + moar.length];
        System.arraycopy(pkgs, 0, combined, 0, pkgs.length);
        System.arraycopy(moar, 0, combined, pkgs.length, moar.length);
        return combined;
    }
}
