package com.mes.mesBackend.repository.impl;

import com.mes.mesBackend.dto.response.ClientResponse;
import com.mes.mesBackend.dto.response.SalesRelatedStatusResponse;
import com.mes.mesBackend.entity.Contract;
import com.mes.mesBackend.entity.QContract;
import com.mes.mesBackend.entity.QContractItem;
import com.mes.mesBackend.entity.QItem;
import com.mes.mesBackend.repository.custom.ContractRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepositoryCustom {
    // 수주 리스트 조회 검색조건 : 거래처, 수주기간, 화폐, 담당자

    private final JPAQueryFactory jpaQueryFactory;

    final QContract contract = QContract.contract;
    final QContractItem contractItem = QContractItem.contractItem;
    final QItem item = QItem.item;

    @Override
    @Transactional(readOnly = true)
    public List<Contract> findAllByCondition(
            String clientName,
            String userName,
            LocalDate fromDate,
            LocalDate toDate,
            Long currencyId
    ) {
        return jpaQueryFactory
                .selectFrom(contract)
                .where(
                        isClientNameContaining(clientName),
                        isUserNameContaining(userName),
                        isCurrencyEq(currencyId),
                        isContractDateBetween(fromDate, toDate),
                        isDeleteYnFalse()
                )
                .orderBy(contract.createdDate.desc())
                .fetch();
    }

    // 수주 등록된 제조사 list 조회 api
    @Override
    public List<ClientResponse.CodeAndName> findContractClientResponse() {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                ClientResponse.CodeAndName.class,
                                contract.client.id.as("id"),
                                contract.client.clientCode.as("clientCode"),
                                contract.client.clientName.as("clientName")
                        )
                )
                .from(contract)
                .where(
                        contract.deleteYn.isFalse()
                )
                .distinct()
                .fetch();
    }

    // 납기일자 남은거(오늘 이후)
    @Override
    public Optional<Long> findContractPeriodDateByTodayAmountSum() {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(contract.id.count())
                        .from(contract)
                        .where(
                                contract.deleteYn.isFalse(),
                                contract.periodDate.after(LocalDate.now().minusDays(1))
                        )
                        .fetchOne()
        );
    }

    // 매출관련현황 - 수주
    @Override
    public List<SalesRelatedStatusResponse> findSalesRelatedStatusResponseByContractItems(LocalDate fromDate, LocalDate toDate) {
        return jpaQueryFactory
                .select(
                        Projections.fields(
                                SalesRelatedStatusResponse.class,
                                item.id.as("itemId"),
                                item.itemNo.as("itemNo"),
                                item.itemName.as("itemName")
                        )
                )
                .from(contractItem)
                .leftJoin(contract).on(contract.id.eq(contractItem.contract.id))
                .leftJoin(item).on(item.id.eq(contractItem.item.id))
                .where(
                        contractItem.deleteYn.isFalse(),
                        contract.deleteYn.isFalse(),
                        contract.contractDate.between(fromDate, toDate)
                )
                .groupBy(item.id)
                .orderBy(contractItem.amount.sum().desc())
                .limit(5)
                .fetch();
    }

    // 주에 해당하는 품목 별 수주 갯수
    @Override
    public Optional<Integer> findWeekAmountByWeekDate(LocalDate fromDate, LocalDate toDate, Long itemId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .select(contractItem.amount.sum())
                        .from(contractItem)
                        .leftJoin(contract).on(contract.id.eq(contractItem.contract.id))
                        .leftJoin(item).on(item.id.eq(contractItem.item.id))
                        .where(
                                item.id.eq(itemId),
                                contract.contractDate.between(fromDate, toDate),
                                contract.deleteYn.isFalse(),
                                contractItem.deleteYn.isFalse()
                        )
                        .groupBy(item.id)
                        .fetchOne()
        );
    }

    // 거래처명 조회
    private BooleanExpression isClientNameContaining(String clientName) {
        return clientName != null ? contract.client.clientName.contains(clientName) : null;
    }

    // 담당자 조회
    private BooleanExpression isUserNameContaining(String userName) {
        return userName != null ? contract.user.korName.contains(userName) : null;
    }

    // 수주기간 조회
    private BooleanExpression isContractDateBetween(LocalDate fromDate, LocalDate toDate) {
        return fromDate != null ? contract.contractDate.between(fromDate, toDate) : null;
    }

    // 화폐 조회
    private BooleanExpression isCurrencyEq(Long currencyId) {
        return currencyId != null ? contract.currency.id.eq(currencyId) : null;
    }


    private BooleanExpression isDeleteYnFalse() {
        return contract.deleteYn.isFalse();
    }
}
