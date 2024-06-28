package com.machpay.affiliate.fee;

import com.machpay.affiliate.common.ListResponse;
import com.machpay.affiliate.entity.FeeRange;
import com.machpay.affiliate.fee.dto.FeeRequest;
import com.machpay.affiliate.fee.dto.FeeSetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/fees")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/source/{sourceCountry}/destination/{destinationCountry}")
    public ResponseEntity<ListResponse> getFeeSets(@PathVariable String sourceCountry,
                                                   @PathVariable String destinationCountry) {
        List<FeeSetResponse> feeSets = feeService.getFeeSetsBySourceDestinationCountry(sourceCountry, destinationCountry);

        return ResponseEntity.ok(new ListResponse(feeSets));
    }

    @GetMapping("/parameters")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse getAllFeeParameter() {
        return new ListResponse(feeService.getAllFeeParameter());
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<FeeRange> createFees(@Valid @RequestBody FeeRequest feeRequest) {
        return feeService.createFees(feeRequest);
    }

    @GetMapping("")
    public ListResponse getAllFees() {
        return new ListResponse(feeService.getAllFeeSet());
    }
}
