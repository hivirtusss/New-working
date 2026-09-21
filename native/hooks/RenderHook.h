#pragma once

#include "config/Config.h"

namespace RenderHook {
    void install(void *api);
    void setConfig(MenuConfig *cfg);
    void shutdown();
}
