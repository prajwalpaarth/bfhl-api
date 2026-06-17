package com.bajaj.bfhl.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BfhlRequest {

    @NotNull(message = "data field must not be null")
    private List<Object> data;

    public List<Object> getData() { return data; }
    public void setData(List<Object> data) { this.data = data; }
}
