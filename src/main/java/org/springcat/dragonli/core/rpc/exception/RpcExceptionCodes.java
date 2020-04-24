package org.springcat.dragonli.core.rpc.exception;

public enum RpcExceptionCodes{

    SUCCESS("0"),
    REQUEST_NONNULL("1"),
    ERR_SERVICE_NOT_FIND("2"),
    ERR_LOAD_BALANCE("3"),
    ERR_REQUEST_SERIALIZE("4"),
    ERR_TRANSFORM_INVOKE("5"),
    ERR_RESPONSE_DESERIALIZE("6"),
    ERR_CURRENT_LIMITING("7"),
    ERR_FUSING("8"),
    ERR_RPC_INIT("9");

    private String code;

    RpcExceptionCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
