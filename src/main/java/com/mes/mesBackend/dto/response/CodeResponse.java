package com.mes.mesBackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeResponse {
    Long id;
    String code;
    String codeName;
    String description;
    boolean useYn;
}
