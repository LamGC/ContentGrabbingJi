/*
 * Copyright (C) 2021  LamGC
 *
 * ContentGrabbingJi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * ContentGrabbingJi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.lamgc.cgj.bot.cache.redis;

import com.google.common.base.Strings;
import redis.clients.jedis.Protocol;

/**
 * Redis 连接配置对象.
 * @author LamGC
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class RedisConnectionProperties {

    private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
    private int socketTimeout = Protocol.DEFAULT_TIMEOUT;
    private String host = Protocol.DEFAULT_HOST;
    private int port = Protocol.DEFAULT_PORT;
    private boolean ssl = false;
    private String userName = null;
    private String password = null;
    private int databaseId = Protocol.DEFAULT_DATABASE;
    private String clientName = null;

    @Override
    public String toString() {
        int showPasswordLength = password.length() / 4;
        return "RedisConnectionProperties{" +
                "connectionTimeout=" + connectionTimeout +
                ", socketTimeout=" + socketTimeout +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", ssl=" + ssl +
                ", userName='" + userName + '\'' +
                ", password='" + password.substring(0, showPasswordLength) +
                Strings.repeat("*", password.length() - showPasswordLength) + '\'' +
                ", databaseId=" + databaseId +
                ", clientName='" + clientName + '\'' +
                '}';
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean enableSsl() {
        return ssl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setEnableSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
