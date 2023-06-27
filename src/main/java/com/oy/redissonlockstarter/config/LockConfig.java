package com.oy.redissonlockstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * redisson相关配置
 *
 * @author ouyang
 * @createDate 2023/6/26 12:25
 */
@ConfigurationProperties(prefix = LockConfig.PREFIX)
public class LockConfig {

    public static final String PREFIX = "oylock";

    //redisson
    private String address;
    private String password;
    private int database=15;
    private long waitTime = 60;
    private long leaseTime = 60;
    private ClusterServer clusterServer;

    public static class ClusterServer{
        private String[] nodeAddresses;

        public String[] getNodeAddresses() {
            return nodeAddresses;
        }

        public void setNodeAddresses(String[] nodeAddresses) {
            this.nodeAddresses = nodeAddresses;
        }
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getLeaseTime() {
        return leaseTime;
    }

    public void setLeaseTime(long leaseTime) {
        this.leaseTime = leaseTime;
    }

    public ClusterServer getClusterServer() {
        return clusterServer;
    }

    public void setClusterServer(ClusterServer clusterServer) {
        this.clusterServer = clusterServer;
    }
}
