#include "RenderHook.h"

#include "config/Config.h"
#include "menu/Menu.h"

#include <EGL/egl.h>
#include <android/log.h>
#include <imgui.h>
#include <imgui_impl_opengl3.h>

#include <zygisk.hpp>

#define LOG_TAG "HivirtusMenu"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static zygisk::Api *g_api = nullptr;
static MenuConfig *g_cfg = nullptr;
static bool g_imgui_inited = false;
static int g_width = 0;
static int g_height = 0;
static int g_frame = 0;

using eglSwapBuffers_t = EGLBoolean (*)(EGLDisplay, EGLSurface);
static eglSwapBuffers_t orig_eglSwapBuffers = nullptr;

static void initImGui(EGLDisplay display, EGLSurface surface) {
    if (g_imgui_inited) return;

    eglQuerySurface(display, surface, EGL_WIDTH, &g_width);
    eglQuerySurface(display, surface, EGL_HEIGHT, &g_height);

    Menu::init();

    ImGuiIO &io = ImGui::GetIO();
    io.DisplaySize = ImVec2((float) g_width, (float) g_height);

    ImGui_ImplOpenGL3_Init("#version 300 es");
    g_imgui_inited = true;
    LOGI("ImGui initialized (%dx%d)", g_width, g_height);
}

static EGLBoolean hook_eglSwapBuffers(EGLDisplay display, EGLSurface surface) {
    if (g_cfg) {
        if (!g_imgui_inited) {
            initImGui(display, surface);
        }

        if (g_imgui_inited) {
            int w = 0, h = 0;
            eglQuerySurface(display, surface, EGL_WIDTH, &w);
            eglQuerySurface(display, surface, EGL_HEIGHT, &h);
            if (w > 0 && h > 0 && (w != g_width || h != g_height)) {
                g_width = w;
                g_height = h;
                ImGui::GetIO().DisplaySize = ImVec2((float) w, (float) h);
            }

            ImGui_ImplOpenGL3_NewFrame();
            ImGui::NewFrame();

            Menu::renderFrame(*g_cfg);

            if (++g_frame % 120 == 0) {
                Config::save(*g_cfg);
            }

            ImGui::Render();
            ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
        }
    }

    return orig_eglSwapBuffers(display, surface);
}

void RenderHook::install(void *api_ptr) {
    g_api = static_cast<zygisk::Api *>(api_ptr);
    g_api->pltHookRegister("libEGL.so", "eglSwapBuffers",
                           (void *) hook_eglSwapBuffers,
                           (void **) &orig_eglSwapBuffers);
    g_api->pltHookCommit();
    LOGI("eglSwapBuffers hook installed");
}

void RenderHook::setConfig(MenuConfig *cfg) {
    g_cfg = cfg;
}

void RenderHook::shutdown() {
    if (g_imgui_inited) {
        ImGui_ImplOpenGL3_Shutdown();
        Menu::shutdown();
        g_imgui_inited = false;
    }
}
