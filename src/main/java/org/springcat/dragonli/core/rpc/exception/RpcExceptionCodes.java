package org.springcat.dragonli.core.rpc.exception;

public enum RpcExceptionCodes{

    REQUEST_NONNULL("400");

    private String code;

    RpcExceptionCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
