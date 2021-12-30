package com.mes.mesBackend.entity;

import com.mes.mesBackend.entity.enumeration.EnrollmentType;
import com.mes.mesBackend.entity.enumeration.QualityLevel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.mes.mesBackend.entity.enumeration.EnrollmentType.PURCHASE_INPUT;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PUBLIC;

/*
 * 7-1. Lot 마스터 조회
 * 검색: 공장,품목그룹,품목,LOT번호,창고,등록유형,재고유무,LOT유형,검사증여부,유효여부
 * 공장            -> Factory
 * 품번            -> Item
 * 품명            -> Item
 * 창고            -> WareHouse
 * LOT 번호
 * 시리얼번호
 * LOT 유형         -> LotType
 * 등록유형          -> EnrollmentType
 * 재고수량
 * 생성수량
 * 불량수량
 * 투입수량
 * 전환수량
 * 이동수량
 * 실사수량
 * 출하수량
 * 반품수량
 * 검사요청수량
 * 검사수량
 * 품질등급             -> QualityLevel
 * 생성일시             -> BaseTimeEntity
 * */
@AllArgsConstructor
@NoArgsConstructor(access = PUBLIC)
@Entity(name = "LOT_MASTERS")
@Data
public class LotMaster extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ID", columnDefinition = "bigint COMMENT 'LOT마스터 고유아이디'")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ITEM",columnDefinition = "bigint COMMENT '품목'", nullable = false)
    private Item item;      // 품목

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "WARE_HOUSE", columnDefinition = "bigint COMMENT '창고'", nullable = false)
    private WareHouse wareHouse;        // 창고

    @Column(name = "LOT_NO", columnDefinition = "varchar(255) COMMENT 'LOT 번호'", nullable = false)
    private String lotNo;       // LOT 번호

    @Column(name = "SERIAL_NO", columnDefinition = "varchar(255) COMMENT '시리얼 번호'")
    private String serialNo;        // 시리얼번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "LOT_TYPE", columnDefinition = "bigint COMMENT 'LOT 유형'")
    private LotType lotType;        // LOT 유형

    @Enumerated(STRING)
    @Column(name = "ENROLLMENT_TYPE", columnDefinition = "varchar(255) COMMENT '등록유형'", nullable = false)
    private EnrollmentType enrollmentType;      // 등록유형

    @Column(name = "PROCESS_YN", columnDefinition = "bit(1) COMMENT '공정용'", nullable = false)
    private boolean processYn;      // 공정용

    @Column(name = "STOCK_AMOUNT", columnDefinition = "int COMMENT '재고수량'")
    private int stockAmount;        // 재고수량

    @Column(name = "CREATED_AMOUNT", columnDefinition = "int COMMENT '생성수량'")
    private int createdAmount;       // 생성수량

    @Column(name = "BAD_ITEM_AMOUNT", columnDefinition = "int COMMENT '불량수량'")
    private int badItemAmount;      // 불량수량

    @Column(name = "INPUT_AMOUNT", columnDefinition = "int COMMENT '투입수량'")
    private int inputAmount;        // 투입수량

    @Column(name = "CHANGED_AMOUNT", columnDefinition = "int COMMENT '전환수량'")
    private int changeAmount;       // 전환수량

    @Column(name = "TRANSFER_AMOUNT", columnDefinition = "int COMMENT '수량'")
    private int transferAmount;     // 이동수량

    @Column(name = "INSPECT_AMOUNT", columnDefinition = "int COMMENT '실사수량'")
    private int inspectAmount;      // 실사수량

    @Column(name = "SHIPMENT_AMOUNT", columnDefinition = "int COMMENT '출하수량'")
    private int shipmentAmount;     // 출하수량

    @Column(name = "RETURN_AMOUNT", columnDefinition = "int COMMENT '반품수량'")
    private int returnAmount;       // 반품수량

    @Column(name = "CHECK_REQUEST_AMOUNT", columnDefinition = "int COMMENT '검사요청수량'")
    private int checkRequestAmount;     // 검사요청수량

    @Column(name = "CHECK_AMOUNT", columnDefinition = "int COMMENT '검사수량'")
    private int checkAmount;            // 검사수량

    @Enumerated(STRING)
    @Column(name = "QUALITY_LEVEL", columnDefinition = "varchar(255) COMMENT '품질등급'")
    private QualityLevel qualityLevel;      // 품질등급

    // 구매입고
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "PURCHASE_INPUT", columnDefinition = "bigint COMMENT '구매입고'")
    private PurchaseInput purchaseInput;

    @Column(name = "DELETE_YN", columnDefinition = "bit(1) COMMENT '삭제여부'", nullable = false)
    private boolean deleteYn = false;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "BEFORE_LOT_NO", columnDefinition = "bigint(1) COMMENT '직전 로트번호'")
    private LotMaster beforeLotNo;     // 직전 로트번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "NEXT_LOT_NO", columnDefinition = "bigint(1) COMMENT '다음 로트번호'")
    private LotMaster nextLotNo;     // 다음 로트번호

//    @Column(name = "END_YN", columnDefinition = "bit(1) COMMENT '로트 엔드'")
//    private boolean endYn;      // 로트엔드

//    @ManyToMany(mappedBy = "lotMasters")
//    @JoinColumn(name = "CONTRACTS")
//    private List<Contract> contracts = new ArrayList<>();

    public void putPurchaseInput(
            LotType lotType,
            PurchaseInput purchaseInput,
            String lotNo
    ) {
        setLotType(lotType);
        setPurchaseInput(purchaseInput);
        setEnrollmentType(PURCHASE_INPUT);
        setLotNo(lotNo);
    }

    public void updatePurchaseInput(int inputAmount) {
        setStockAmount(inputAmount);
        setCreatedAmount(inputAmount);
    }

    public void delete() {
        setDeleteYn(true);
    }
}
