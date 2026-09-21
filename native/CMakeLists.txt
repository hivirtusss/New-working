cmake_minimum_required(VERSION 3.18.1)
project(hivirtus_floating_menu)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(DEPS_DIR "${CMAKE_SOURCE_DIR}/../deps")
set(IMGUI_DIR "${DEPS_DIR}/imgui")
set(ZYGISK_INCLUDE "${DEPS_DIR}/include")

set(IMGUI_SOURCES
    ${IMGUI_DIR}/imgui.cpp
    ${IMGUI_DIR}/imgui_draw.cpp
    ${IMGUI_DIR}/imgui_tables.cpp
    ${IMGUI_DIR}/imgui_widgets.cpp
    ${IMGUI_DIR}/backends/imgui_impl_opengl3.cpp
)

add_library(${CMAKE_PROJECT_NAME} SHARED
    main.cpp
    config/Config.cpp
    menu/Menu.cpp
    hooks/RenderHook.cpp
    ${IMGUI_SOURCES}
)

target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
    ${CMAKE_SOURCE_DIR}
    ${ZYGISK_INCLUDE}
    ${IMGUI_DIR}
    ${IMGUI_DIR}/backends
)

target_compile_options(${CMAKE_PROJECT_NAME} PRIVATE
    -Wall -Wextra -O2 -fvisibility=hidden
)

target_link_libraries(${CMAKE_PROJECT_NAME}
    log
    EGL
    GLESv3
    android
)
