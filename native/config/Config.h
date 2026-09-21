#pragma once

#include <string>

struct MenuConfig {
    float menu_x = 100.f;
    float menu_y = 200.f;
    bool menu_open = false;
    bool toggle_esp = false;
    bool toggle_aim = false;
};

namespace Config {
    constexpr const char *PATH = "/data/local/tmp/hivirtus_menu.conf";

    MenuConfig load();
    void save(const MenuConfig &cfg);
}
