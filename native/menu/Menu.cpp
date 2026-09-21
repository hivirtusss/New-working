#include "Menu.h"

#include <imgui.h>

void Menu::init() {
    ImGui::CreateContext();
    ImGuiIO &io = ImGui::GetIO();
    io.IniFilename = nullptr;
    io.LogFilename = nullptr;
    ImGui::StyleColorsDark();

    ImGuiStyle &style = ImGui::GetStyle();
    style.WindowRounding = 12.f;
    style.FrameRounding = 8.f;
    style.GrabRounding = 8.f;
}

void Menu::shutdown() {
    ImGui::DestroyContext();
}

void Menu::renderFrame(MenuConfig &cfg) {
    const float btn_size = 56.f;

    ImGui::SetNextWindowPos(ImVec2(cfg.menu_x, cfg.menu_y), ImGuiCond_Always);
    ImGui::SetNextWindowBgAlpha(cfg.menu_open ? 0.92f : 0.75f);

    ImGuiWindowFlags flags = ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoResize |
                             ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoSavedSettings;

    if (!cfg.menu_open) {
        flags |= ImGuiWindowFlags_NoScrollbar;
    }

    if (ImGui::Begin("##FloatingMenu", nullptr, flags)) {
        if (ImGui::Button(cfg.menu_open ? "X" : "HM", ImVec2(btn_size, btn_size))) {
            cfg.menu_open = !cfg.menu_open;
        }

        if (ImGui::IsItemActive() && ImGui::IsMouseDragging(ImGuiMouseButton_Left)) {
            ImVec2 delta = ImGui::GetIO().MouseDelta;
            cfg.menu_x += delta.x;
            cfg.menu_y += delta.y;
        }

        if (cfg.menu_open) {
            ImGui::Separator();
            ImGui::Text("Hivirtus Menu");
            ImGui::Spacing();

            if (ImGui::Checkbox("ESP", &cfg.toggle_esp)) {
                // saved by main loop periodically
            }
            ImGui::Checkbox("Aim Assist", &cfg.toggle_aim);

            ImGui::Spacing();
            ImGui::TextDisabled("HM button drag = move menu");
            ImGui::TextDisabled("Config: /data/local/tmp/");
        }
    }
    ImGui::End();
}
