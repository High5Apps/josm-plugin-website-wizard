package org.openstreetmap.josm.plugins.websitehelper;

import org.openstreetmap.josm.gui.dialogs.ToggleDialog;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.data.osm.OsmPrimitive;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.openstreetmap.josm.command.ChangePropertyCommand;
import org.openstreetmap.josm.command.Command;
import org.openstreetmap.josm.command.SequenceCommand;
import org.openstreetmap.josm.data.UndoRedoHandler;
import org.openstreetmap.josm.gui.util.GuiHelper;
import org.openstreetmap.josm.gui.widgets.DisableShortcutsOnFocusGainedTextField;

public class WebsiteHelperDialog extends ToggleDialog {
    
    private final DisableShortcutsOnFocusGainedTextField searchPrefixField;
    private final DisableShortcutsOnFocusGainedTextField websiteUrlField;
    private final JButton searchButton;
    private final JButton setTagButton;
    private final JLabel statusLabel;
    
    public WebsiteHelperDialog() {
        super("Website Helper", "websitehelper", "Tool to add website tags to OSM features", null, 150);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Search Prefix Label & Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Search Prefix:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        searchPrefixField = new DisableShortcutsOnFocusGainedTextField(15);
        searchPrefixField.setToolTipText("e.g., 'site:' or custom text to prepend");
        mainPanel.add(searchPrefixField, gbc);
        
        // Search Button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        searchButton = new JButton("Search DuckDuckGo");
        searchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDuckDuckGo();
            }
        });
        mainPanel.add(searchButton, gbc);
        
        // Website URL Label & Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Website URL:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        websiteUrlField = new DisableShortcutsOnFocusGainedTextField(15);
        websiteUrlField.setToolTipText("Paste or enter the website URL");
        mainPanel.add(websiteUrlField, gbc);
        
        // Set Tag Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        setTagButton = new JButton("Set website= Tag");
        setTagButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setWebsiteTag();
            }
        });
        mainPanel.add(setTagButton, gbc);
        
        // Status Label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(0, 100, 0));
        mainPanel.add(statusLabel, gbc);
        
        // Add padding at bottom
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 1;
        mainPanel.add(Box.createVerticalGlue(), gbc);
        
        createLayout(mainPanel, false, null);
    }
    
    private void searchDuckDuckGo() {
        String prefix = searchPrefixField.getText().trim();
        OsmPrimitive selected = getSelectedOsmObject();
        
        if (selected == null) {
            statusLabel.setText("Error: No OSM object selected");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        String name = selected.get("name");
        if (name == null || name.isEmpty()) {
            statusLabel.setText("Error: Selected object has no 'name' tag");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        try {
            String searchQuery = prefix + " " + name;
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String duckDuckGoUrl = "https://duckduckgo.com/?q=" + encodedQuery;
            
            Desktop.getDesktop().browse(new URI(duckDuckGoUrl));
            statusLabel.setText("Opened DuckDuckGo search for: " + searchQuery);
            statusLabel.setForeground(new Color(0, 100, 0));
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void setWebsiteTag() {
        final String url = websiteUrlField.getText().trim();
        OsmPrimitive selected = getSelectedOsmObject();
        
        if (selected == null) {
            statusLabel.setText("Error: No OSM object selected");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (url.isEmpty()) {
            statusLabel.setText("Error: No URL entered");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // Execute tag injection reliably inside the Event Dispatch Thread (EDT)
        GuiHelper.runInEDT(() -> {
            try {
                ChangePropertyCommand command = new ChangePropertyCommand(selected, "website", url);
                UndoRedoHandler.getInstance().add(command);

                statusLabel.setText("Website tag set: " + url);
                statusLabel.setForeground(new Color(0, 100, 0));
                websiteUrlField.setText("");
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
            }
        });
    }
    
    private OsmPrimitive getSelectedOsmObject() {
        var selection = MainApplication.getLayerManager().getEditLayer();
        if (selection == null) return null;
        
        var selected = selection.data.getSelected();
        if (selected.isEmpty()) return null;
        
        return selected.iterator().next();
    }
}
