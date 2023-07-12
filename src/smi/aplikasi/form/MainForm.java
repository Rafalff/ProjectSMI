package smi.aplikasi.form;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import smi.aplikasi.Application;
import smi.aplikasi.form.oth.FormDashboard;
import smi.aplikasi.form.oth.FormAlat;
import smi.aplikasi.form.oth.FormTransferAlat;
import smi.menu.Menu;
import smi.menu.MenuAction;

/**
 *
 * @author Raven
 */
public class MainForm extends JLayeredPane {
    private FormTransferAlat formTransferAlat;
    String level;
    String nama;
    String idCabang;
    String kodePegawai;
    public MainForm(String nama, String level, String idCabang, String kodePegawai) {
        init();  
        setValues(nama,level,idCabang, kodePegawai);
    }
    
    public void setValues(String nama, String level, String idCabang, String kodePegawai) {
        this.nama = nama;
        this.idCabang = idCabang;
        this.level = level;     
        this.kodePegawai = kodePegawai;
        System.out.println("Received values in MainForm: " + nama + ", " + level + ", " + idCabang + "kode pegawai :" + kodePegawai);
    }


    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        initMenuEvent();
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("raven/icon/svg/" + icon, 0.8f));
    }

    public void initMenuEvent() {
    
    menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
        System.out.println("Selected menu: index=" + index + ", subIndex=" + subIndex);
        
         if (index == 0) {
                Application.showForm(new FormDashboard());
            } else if (index == 1) {
                if (subIndex == 1) {
                    Application.showForm(new FormAlat());                    
                } else if (subIndex == 2) {
                    Application.showForm(new FormTransferAlat(nama, level, idCabang, kodePegawai));
                } else {
                    action.cancel();
                }
            } else if (index == 2) {
                Application.showForm(new FormTransferAlat(nama, level, idCabang, kodePegawai));
            } else if (index == 7) {
                Application.logout();
            } else {
                action.cancel();
            }
        });
    }


    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("raven/icon/svg/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        System.out.println("Showing form: " + component.getClass().getSimpleName());
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }


    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
