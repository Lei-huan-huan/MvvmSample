package com.lhh.mvvmsample.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class ServerConfigStore @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    fun getServerIp(): String {
        return sharedPreferences.getString(KEY_SERVER_IP, DEFAULT_SERVER_IP).orEmpty()
    }

    fun getServerPort(): Int {
        return sharedPreferences.getInt(KEY_SERVER_PORT, DEFAULT_SERVER_PORT)
    }

    fun saveServerConfig(ip: String, port: Int) {
        sharedPreferences.edit {
            putString(KEY_SERVER_IP, ip)
                .putInt(KEY_SERVER_PORT, port)
        }
    }

    companion object {
        private const val KEY_SERVER_IP = "key_server_ip"
        private const val KEY_SERVER_PORT = "key_server_port"
        private const val DEFAULT_SERVER_IP = "192.168.2.18"
        private const val DEFAULT_SERVER_PORT = 8081
    }
}
