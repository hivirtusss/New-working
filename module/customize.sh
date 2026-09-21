#!/system/bin/sh
# Magisk module install script

SKIPUNZIP=1

ui_print "*******************************"
ui_print "  Hivirtus Floating Menu v1.0  "
ui_print "*******************************"
ui_print ""
ui_print "- Sirf ZIP flash (koi APK nahi)"
ui_print "- Floating menu game ke upar"
ui_print "- Config: /data/local/tmp/hivirtus_menu.conf"
ui_print ""

# Create default config if missing
CONFIG="/data/local/tmp/hivirtus_menu.conf"
if [ ! -f "$CONFIG" ]; then
  cat > "$CONFIG" << 'EOF'
# Hivirtus Floating Menu — local config
menu_x=100
menu_y=200
menu_open=0
toggle_esp=0
toggle_aim=0
EOF
  chmod 644 "$CONFIG"
  ui_print "Default config created at $CONFIG"
fi

ui_print ""
ui_print "Install complete. Reboot recommended."
