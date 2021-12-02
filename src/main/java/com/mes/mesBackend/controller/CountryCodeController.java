package com.mes.mesBackend.controller;

import com.mes.mesBackend.dto.request.CountryCodeRequest;
import com.mes.mesBackend.dto.response.CountryCodeResponse;
import com.mes.mesBackend.exception.NotFoundException;
import com.mes.mesBackend.service.CountryCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "country-code", description = "국가코드 API")
@RequestMapping(value = "/country-codes")
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class CountryCodeController {

    @Autowired
    CountryCodeService countryCodeService;

    // 국가코드 생성
    @PostMapping
    @ResponseBody()
    @Operation(summary = "국가코드 생성")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "not found resource"),
                    @ApiResponse(responseCode = "400", description = "bad request")
            }
    )
    public ResponseEntity<CountryCodeResponse> createCountryCode(
            @RequestBody @Valid CountryCodeRequest countryCodeRequest
    ) {
        return new ResponseEntity<>(countryCodeService.createCountryCode(countryCodeRequest), HttpStatus.OK);
    }

    // 국가코드 단일 조회
    @GetMapping("/{id}")
    @ResponseBody()
    @Operation(summary = "국가코드 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "not found resource"),
            }
    )
    public ResponseEntity<CountryCodeResponse> getCountryCode(
            @PathVariable Long id
    ) throws NotFoundException {
        return new ResponseEntity<>(countryCodeService.getCountryCode(id), HttpStatus.OK);
    }

//    국가코드 전체 조회
    @GetMapping
    @ResponseBody
    @Operation(summary = "국가코드 전체 조회")
    public ResponseEntity<List<CountryCodeResponse>> getCountryCodes() {
        return new ResponseEntity<>(countryCodeService.getCountryCodes(), HttpStatus.OK);
    }

    // 국가코드 수정
    @PatchMapping("/{id}")
    @ResponseBody()
    @Operation(summary = "국가코드 수정")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "success"),
                    @ApiResponse(responseCode = "404", description = "not found resource"),
                    @ApiResponse(responseCode = "400", description = "bad request")
            }
    )
    public ResponseEntity<CountryCodeResponse> updateCountryCode(
            @PathVariable Long id,
            @RequestBody @Valid CountryCodeRequest countryCodeRequest
    ) throws NotFoundException {
        return new ResponseEntity<>(countryCodeService.updateCountryCode(id, countryCodeRequest), HttpStatus.OK);
    }

    // 국가코드 삭제
    @DeleteMapping("/{id}")
    @ResponseBody()
    @Operation(summary = "국가코드 삭제")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "no content"),
                    @ApiResponse(responseCode = "404", description = "not found resource")
            }
    )
    public ResponseEntity<Void> deleteCountryCode(@PathVariable Long id) throws NotFoundException {
        countryCodeService.deleteCountryCode(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

//    @GetMapping
//    @ResponseBody()
//    @Operation(summary = "국가코드 페이징 조회")
//    @Parameters(
//            value = {
//                    @Parameter(
//                            name = "page", description = "0 부터 시작되는 페이지 (0..N)",
//                            in = ParameterIn.QUERY,
//                            schema = @Schema(type = "integer", defaultValue = "0")
//                    ),
//                    @Parameter(
//                            name = "size", description = "페이지의 사이즈",
//                            in = ParameterIn.QUERY,
//                            schema = @Schema(type = "integer", defaultValue = "20")
//                    ),
//                    @Parameter(
//                            name = "sort", in = ParameterIn.QUERY,
//                            description = "정렬할 대상과 정렬 방식, 데이터 형식: property(,asc|desc). + 디폴트 정렬순서는 오름차순, 다중정렬 가능",
//                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,desc"))
//                    )
//            }
//    )
//    public ResponseEntity<Page<CountryCodeResponse>> getCountryCodes(
//            @PageableDefault @Parameter(hidden = true) Pageable pageable
//    ) {
//        return new ResponseEntity<>(countryCodeService.getCountryCodes(pageable), HttpStatus.OK);
//    }
}
