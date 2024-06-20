package pansong291.xposed.quickenergy.rpc;

import pansong291.xposed.quickenergy.entity.RpcEntity;

public interface RpcBridge {

    void load() throws Exception;

    void unload();

    String requestString(RpcEntity rpcEntity, int tryCount, int retryInterval);

    default String requestString(RpcEntity rpcEntity) {
        return requestString(rpcEntity, 3, -1);
    }

    default String requestString(String method, String data) {
        return requestString(method, data, 3, -1);
    }

    default String requestString(String method, String data, int tryCount, int retryInterval) {
        return requestString(new RpcEntity(method, data), tryCount, retryInterval);
    }

    RpcEntity requestObject(RpcEntity rpcEntity, int tryCount, int retryInterval);

    default RpcEntity requestObject(RpcEntity rpcEntity) {
        return requestObject(rpcEntity, 3, -1);
    }

    default RpcEntity requestObject(String method, String data) {
        return requestObject(method, data, 3, -1);
    }

    default RpcEntity requestObject(String method, String data, int tryCount, int retryInterval) {
        return requestObject(new RpcEntity(method, data), tryCount, retryInterval);
    }

}
