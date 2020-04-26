package org.springcat.dragonli.core.rpc.exception;

public enum RpcExceptionCodes{

    SUCCESS("0"),
    ERR_REQUEST_NULL("1"),
    ERR_VALIDATE_CODE("2"),
    ERR_SERVICE_NOT_FIND("3"),
    ERR_LOAD_BALANCE("4"),
    ERR_REQUEST_SERIALIZE("5"),
    ERR_TRANSFORM_INVOKE("6"),
    ERR_RESPONSE_DESERIALIZE("7"),
    ERR_CURRENT_LIMITING("8"),
    ERR_FUSING("9"),
    ERR_RPC_INIT("10");

    private String code;

    RpcExceptionCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
