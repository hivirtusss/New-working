#include "config/Config.h"
#include "hooks/RenderHook.h"

#include <android/log.h>
#include <cstring>
#include <zygisk.hpp>

#define LOG_TAG "HivirtusMenu"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static const char *TARGET_PACKAGES[] = {
    "com.pubg.imobile",
    "com.tencent.ig",
    nullptr
};

static bool isTargetPackage(const char *package) {
    if (!package) return false;
    for (int i = 0; TARGET_PACKAGES[i] != nullptr; i++) {
        if (strcmp(package, TARGET_PACKAGES[i]) == 0) return true;
    }
    return false;
}

class FloatingMenuModule : public zygisk::ModuleBase {
public:
    void onLoad(zygisk::Api *api, JNIEnv *env) override {
        api_ = api;
        env_ = env;
    }

    void preAppSpecialize(zygisk::AppSpecializeArgs *args) override {
        const char *process = env_->GetStringUTFChars(args->nice_name, nullptr);
        enabled_ = isTargetPackage(process);
        env_->ReleaseStringUTFChars(args->nice_name, process);

        if (!enabled_) {
            api_->setOption(zygisk::DLCLOSE_MODULE_LIBRARY);
        }
    }

    void postAppSpecialize(const zygisk::AppSpecializeArgs * /*args*/) override {
        if (!enabled_) return;

        cfg_ = Config::load();
        RenderHook::setConfig(&cfg_);
        RenderHook::install(api_);
        LOGI("Floating menu active");
    }

    void preServerSpecialize(zygisk::ServerSpecializeArgs * /*args*/) override {
        api_->setOption(zygisk::DLCLOSE_MODULE_LIBRARY);
    }

private:
    zygisk::Api *api_ = nullptr;
    JNIEnv *env_ = nullptr;
    bool enabled_ = false;
    MenuConfig cfg_{};
};

REGISTER_ZYGISK_MODULE(FloatingMenuModule)
