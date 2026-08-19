package org.openstreetmap.josm.plugins.websitewizard;

import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapFrameListener;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Main entry point for the JOSM Website Wizard Plugin.
 * 
 * To see changes in JOSM:
 * 1. Save everything and run `ant dist` (`ant clean` probably only helpful before a release. Causes much longer build.)
 * 2. Copy/Paste from ~/git/josm/josm/dist/websitewizard.jar to ~/Library/JOSM/plugins
 * 3. Restart JOSM
 * 
 * Docs: https://josm.openstreetmap.de/doc/index.html
 */
public class WebsiteWizardPlugin extends Plugin implements MapFrameListener {

    public WebsiteWizardPlugin(PluginInformation info) {
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            newFrame.addToggleDialog(new WebsiteWizardDialog());
        }
    }
}
