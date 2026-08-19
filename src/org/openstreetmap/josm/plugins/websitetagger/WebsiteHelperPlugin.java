package org.openstreetmap.josm.plugins.websitehelper;

import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.MapFrameListener;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * Main entry point for the JOSM Website Helper Plugin.
 * 
 * To see changes in JOSM:
 * 1. Save everything and run `ant dist` (`ant clean` probably only helpful before a release. Causes much longer build.)
 * 2. Copy/Paste from ~/git/josm/josm/dist/websitehelper.jar to ~/Library/JOSM/plugins
 * 3. Restart JOSM
 * 
 * Docs: https://josm.openstreetmap.de/doc/index.html
 */
public class WebsiteHelperPlugin extends Plugin implements MapFrameListener {

    public WebsiteHelperPlugin(PluginInformation info) {
        super(info);
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        if (newFrame != null) {
            newFrame.addToggleDialog(new WebsiteHelperDialog());
        }
    }
}
