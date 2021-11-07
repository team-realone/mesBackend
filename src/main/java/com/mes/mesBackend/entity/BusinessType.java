package com.mes.mesBackend.entity;

import lombok.*;

import javax.persistence.*;

// 거래처 - 업태
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Entity(name = "BUSINESS_TYPES")
@Data
public class BusinessType extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", columnDefinition = "bigint COMMENT '업태 고유아이디'")
    private Long id;

    @Column(name = "NAME", nullable = false, columnDefinition = "varchar(255) COMMENT '업태'")
    private String name;

    @Column(name = "USE_YN", nullable = false, columnDefinition = "bit(1) COMMENT '사용여부'")
    private Boolean useYn = true;      //  사용여부

    @Column(name = "DELETE_YN", columnDefinition = "bit(1) COMMENT '삭제여부'")
    private boolean deleteYn = false;  // 삭제여부

    // 수정 매핑
    public void put(BusinessType newBusinessType) {
        setName(newBusinessType.name);
        setUseYn(newBusinessType.useYn);
        setDeleteYn(newBusinessType.deleteYn);
    }

    public void delete() {
        setDeleteYn(true);
    }
}
