package xyz.geosure.roadgis.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;

public class JCheckBoxGroup extends JPanel {

    private JCheckBox all;
    private List<JCheckBox> checkBoxes;

    public JCheckBoxGroup(String... options) {
        checkBoxes = new ArrayList<>(25);
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
        all = new JCheckBox("Select All...");
        all.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JCheckBox cb : checkBoxes) {
                    cb.setSelected(all.isSelected());
                }
            }
        });
        header.add(all);
        add(header, BorderLayout.NORTH);

        JPanel content = new ScrollablePane(new GridBagLayout());
        content.setBackground(UIManager.getColor("List.background"));
        if (options.length > 0) {

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weightx = 1;
            for (int index = 0; index < options.length - 1; index++) {
                JCheckBox cb = new JCheckBox(options[index]);
                cb.setOpaque(false);
                checkBoxes.add(cb);
                content.add(cb, gbc);
            }

            JCheckBox cb = new JCheckBox(options[options.length - 1]);
            cb.setOpaque(false);
            checkBoxes.add(cb);
            gbc.weighty = 1;
            content.add(cb, gbc);

        }

        add(new JScrollPane(content));
    }

    public class ScrollablePane extends JPanel implements Scrollable {

        public ScrollablePane(LayoutManager layout) {
            super(layout);
        }

        public ScrollablePane() {
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(100, 100);
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            boolean track = false;
            Container parent = getParent();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                track = vp.getWidth() > getPreferredSize().width;
            }
            return track;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            boolean track = false;
            Container parent = getParent();
            if (parent instanceof JViewport) {
                JViewport vp = (JViewport) parent;
                track = vp.getHeight() > getPreferredSize().height;
            }
            return track;
        }

    }

}