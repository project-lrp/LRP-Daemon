#include <jni.h>
#include <string>
#include <chrono>
#include <unistd.h>

extern "C" {
void send_lrp(int next_packet_interval, int drx, int sr);
int get_config_drx();
int get_config_sch();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_LrpHandler_getNativeTime(JNIEnv *env, jclass clazz) {
    auto now = std::chrono::system_clock::now();
    auto now_us = std::chrono::time_point_cast<std::chrono::microseconds>(now);
    return now_us.time_since_epoch().count();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_LrpHandler_doNativeWork(JNIEnv *env, jclass clazz) {
    auto t1 = std::chrono::system_clock::now();
    usleep(5000);
    auto t2 = std::chrono::system_clock::now();

    auto time_us = std::chrono::duration_cast<std::chrono::microseconds>(t2 - t1);
    return time_us.count();
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_LrpHandler_sendPacket(JNIEnv *env, jclass clazz, jint ms, jint drx, jint sr) {
    send_lrp(ms, drx, sr);
    return 1;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_LrpHandler_getConfigDrx(JNIEnv *env, jclass clazz) {
    return get_config_drx();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_lrptest_daemon_LrpHandler_getConfigSch(JNIEnv *env, jclass clazz) {
    return get_config_sch();
}
