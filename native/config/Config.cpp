#include "Config.h"

#include <cstdio>
#include <cstring>

static void trim(char *s) {
    if (!s) return;
    size_t len = strlen(s);
    while (len > 0 && (s[len - 1] == '\n' || s[len - 1] == '\r' || s[len - 1] == ' ')) {
        s[--len] = '\0';
    }
}

MenuConfig Config::load() {
    MenuConfig cfg{};
    FILE *f = fopen(PATH, "r");
    if (!f) return cfg;

    char line[256];
    while (fgets(line, sizeof(line), f)) {
        if (line[0] == '#' || line[0] == '\n') continue;
        char *eq = strchr(line, '=');
        if (!eq) continue;

        *eq = '\0';
        char *key = line;
        char *val = eq + 1;
        trim(key);
        trim(val);

        if (strcmp(key, "menu_x") == 0) cfg.menu_x = strtof(val, nullptr);
        else if (strcmp(key, "menu_y") == 0) cfg.menu_y = strtof(val, nullptr);
        else if (strcmp(key, "menu_open") == 0) cfg.menu_open = atoi(val) != 0;
        else if (strcmp(key, "toggle_esp") == 0) cfg.toggle_esp = atoi(val) != 0;
        else if (strcmp(key, "toggle_aim") == 0) cfg.toggle_aim = atoi(val) != 0;
    }
    fclose(f);
    return cfg;
}

void Config::save(const MenuConfig &cfg) {
    FILE *f = fopen(PATH, "w");
    if (!f) return;

    fprintf(f, "# Hivirtus Floating Menu — local config\n");
    fprintf(f, "menu_x=%.0f\n", cfg.menu_x);
    fprintf(f, "menu_y=%.0f\n", cfg.menu_y);
    fprintf(f, "menu_open=%d\n", cfg.menu_open ? 1 : 0);
    fprintf(f, "toggle_esp=%d\n", cfg.toggle_esp ? 1 : 0);
    fprintf(f, "toggle_aim=%d\n", cfg.toggle_aim ? 1 : 0);
    fclose(f);
}
