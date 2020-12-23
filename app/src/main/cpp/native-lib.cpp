#include <jni.h>
#include <string>
#include <chrono>
#include <unistd.h>

extern "C" {
void send_lrp(int next_packet_interval);
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
Java_com_lrptest_daemon_LrpHandler_sendPacket(JNIEnv *env, jclass clazz, jint ms) {
    send_lrp(ms);
    return 1;
}
