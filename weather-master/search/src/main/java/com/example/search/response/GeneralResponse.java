package com.example.search.response;


import java.time.Instant;


public class GeneralResponse<T> {
    private int code;
    private Instant timestamp;
    private T data;

    public GeneralResponse(int code, Instant timestamp, T data) {
        this.code = code;
        this.timestamp = timestamp;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GeneralResponse{" +
                "code=" + code +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}

